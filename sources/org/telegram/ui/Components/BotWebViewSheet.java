package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotApp;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_appWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_dataJSON;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputBotAppID;
import org.telegram.tgnet.TLRPC$TL_messages_prolongWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestAppWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_messages_requestWebView;
import org.telegram.tgnet.TLRPC$TL_messages_sendWebViewData;
import org.telegram.tgnet.TLRPC$TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC$TL_simpleWebViewResultUrl;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$TL_webViewResultUrl;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BotWebViewContainer;
import org.telegram.ui.Components.BotWebViewSheet;
import org.telegram.ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.TopicsFragment;
public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    private static final SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat("actionBarTransitionProgress", BotWebViewSheet$$ExternalSyntheticLambda21.INSTANCE, BotWebViewSheet$$ExternalSyntheticLambda22.INSTANCE).setMultiplier(100.0f);
    private ActionBar actionBar;
    private int actionBarColor;
    private Paint actionBarPaint;
    private Drawable actionBarShadow;
    private float actionBarTransitionProgress;
    private Paint backgroundPaint;
    private long botId;
    private String buttonText;
    private int currentAccount;
    private Paint dimPaint;
    private boolean dismissed;
    private SizeNotifierFrameLayout frameLayout;
    private boolean ignoreLayout;
    private Paint linePaint;
    private TextView mainButton;
    private VerticalPositionAutoAnimator mainButtonAutoAnimator;
    private boolean mainButtonProgressWasVisible;
    private boolean mainButtonWasVisible;
    private boolean needCloseConfirmation;
    private boolean overrideBackgroundColor;
    private Activity parentActivity;
    private PasscodeView passcodeView;
    private long peerId;
    private Runnable pollRunnable;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private long queryId;
    private VerticalPositionAutoAnimator radialProgressAutoAnimator;
    private RadialProgressView radialProgressView;
    private int replyToMsgId;
    private Theme.ResourcesProvider resourcesProvider;
    private ActionBarMenuSubItem settingsItem;
    private boolean silent;
    private SpringAnimation springAnimation;
    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private Boolean wasLightStatusBar;
    private BotWebViewContainer webViewContainer;

    public static void lambda$static$1(BotWebViewSheet botWebViewSheet, float f) {
        botWebViewSheet.actionBarTransitionProgress = f;
        botWebViewSheet.frameLayout.invalidate();
        botWebViewSheet.actionBar.setAlpha(f);
        botWebViewSheet.updateLightStatusBar();
    }

    public void lambda$new$4() {
        if (this.dismissed) {
            return;
        }
        TLRPC$TL_messages_prolongWebView tLRPC$TL_messages_prolongWebView = new TLRPC$TL_messages_prolongWebView();
        tLRPC$TL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        tLRPC$TL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
        tLRPC$TL_messages_prolongWebView.query_id = this.queryId;
        tLRPC$TL_messages_prolongWebView.silent = this.silent;
        int i = this.replyToMsgId;
        if (i != 0) {
            tLRPC$TL_messages_prolongWebView.reply_to_msg_id = i;
            tLRPC$TL_messages_prolongWebView.flags |= 1;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_prolongWebView, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                BotWebViewSheet.this.lambda$new$3(tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$new$3(TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$2(tLRPC$TL_error);
            }
        });
    }

    public void lambda$new$2(TLRPC$TL_error tLRPC$TL_error) {
        if (this.dismissed) {
            return;
        }
        if (tLRPC$TL_error != null) {
            dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public BotWebViewSheet(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.actionBarTransitionProgress = 0.0f;
        this.linePaint = new Paint(1);
        this.dimPaint = new Paint();
        this.backgroundPaint = new Paint(1);
        this.actionBarPaint = new Paint(1);
        this.pollRunnable = new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$4();
            }
        };
        this.resourcesProvider = resourcesProvider;
        this.swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
            @Override
            protected void onMeasure(int r5, int r6) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotWebViewSheet.AnonymousClass1.onMeasure(int, int):void");
            }

            @Override
            public void requestLayout() {
                if (BotWebViewSheet.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        BotWebViewContainer botWebViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor("windowBackgroundWhite"));
        this.webViewContainer = botWebViewContainer;
        botWebViewContainer.setDelegate(new AnonymousClass2(context, resourcesProvider));
        this.linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.dimPaint.setColor(1073741824);
        this.actionBarColor = getColor("windowBackgroundWhite");
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (BotWebViewSheet.this.passcodeView.getVisibility() != 0) {
                    if (!BotWebViewSheet.this.overrideBackgroundColor) {
                        BotWebViewSheet.this.backgroundPaint.setColor(BotWebViewSheet.this.getColor("windowBackgroundWhite"));
                    }
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                    canvas.drawRect(rectF, BotWebViewSheet.this.dimPaint);
                    BotWebViewSheet.this.actionBarPaint.setColor(ColorUtils.blendARGB(BotWebViewSheet.this.actionBarColor, BotWebViewSheet.this.getColor("windowBackgroundWhite"), BotWebViewSheet.this.actionBarTransitionProgress));
                    float dp = AndroidUtilities.dp(16.0f) * (AndroidUtilities.isTablet() ? 1.0f : 1.0f - BotWebViewSheet.this.actionBarTransitionProgress);
                    rectF.set(BotWebViewSheet.this.swipeContainer.getLeft(), AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress), BotWebViewSheet.this.swipeContainer.getRight(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f) + dp);
                    canvas.drawRoundRect(rectF, dp, dp, BotWebViewSheet.this.actionBarPaint);
                    rectF.set(BotWebViewSheet.this.swipeContainer.getLeft(), BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), BotWebViewSheet.this.swipeContainer.getRight(), getHeight());
                    canvas.drawRect(rectF, BotWebViewSheet.this.backgroundPaint);
                }
            }

            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);
                float f = AndroidUtilities.isTablet() ? 0.0f : BotWebViewSheet.this.actionBarTransitionProgress;
                BotWebViewSheet.this.linePaint.setColor(Theme.getColor("key_sheet_scrollUp"));
                BotWebViewSheet.this.linePaint.setAlpha((int) (BotWebViewSheet.this.linePaint.getAlpha() * (1.0f - (Math.min(0.5f, f) / 0.5f))));
                canvas.save();
                float f2 = 1.0f - f;
                float lerp = AndroidUtilities.isTablet() ? AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(12.0f), AndroidUtilities.statusBarHeight / 2.0f, BotWebViewSheet.this.actionBarTransitionProgress) : AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f), f) + AndroidUtilities.dp(12.0f);
                canvas.scale(f2, f2, getWidth() / 2.0f, lerp);
                canvas.drawLine((getWidth() / 2.0f) - AndroidUtilities.dp(16.0f), lerp, (getWidth() / 2.0f) + AndroidUtilities.dp(16.0f), lerp, BotWebViewSheet.this.linePaint);
                canvas.restore();
                BotWebViewSheet.this.actionBarShadow.setAlpha((int) (BotWebViewSheet.this.actionBar.getAlpha() * 255.0f));
                float y = BotWebViewSheet.this.actionBar.getY() + BotWebViewSheet.this.actionBar.getTranslationY() + BotWebViewSheet.this.actionBar.getHeight();
                BotWebViewSheet.this.actionBarShadow.setBounds(0, (int) y, getWidth(), (int) (y + BotWebViewSheet.this.actionBarShadow.getIntrinsicHeight()));
                BotWebViewSheet.this.actionBarShadow.draw(canvas);
            }

            @Override
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && (motionEvent.getY() <= AndroidUtilities.lerp(BotWebViewSheet.this.swipeContainer.getTranslationY() + AndroidUtilities.dp(24.0f), 0.0f, BotWebViewSheet.this.actionBarTransitionProgress) || motionEvent.getX() > BotWebViewSheet.this.swipeContainer.getRight() || motionEvent.getX() < BotWebViewSheet.this.swipeContainer.getLeft())) {
                    BotWebViewSheet.this.onCheckDismissByUser();
                    return true;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.frameLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
            @Override
            public final void onSizeChanged(int i, boolean z) {
                BotWebViewSheet.this.lambda$new$5(i, z);
            }
        });
        this.frameLayout.addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f, 49, 0.0f, 24.0f, 0.0f, 0.0f));
        TextView textView = new TextView(this, context) {
            @Override
            protected void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i, i2);
            }
        };
        this.mainButton = textView;
        textView.setVisibility(8);
        this.mainButton.setAlpha(0.0f);
        this.mainButton.setSingleLine();
        this.mainButton.setGravity(17);
        this.mainButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int dp = AndroidUtilities.dp(16.0f);
        this.mainButton.setPadding(dp, 0, dp, 0);
        this.mainButton.setTextSize(1, 14.0f);
        this.mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BotWebViewSheet.this.lambda$new$6(view);
            }
        });
        this.frameLayout.addView(this.mainButton, LayoutHelper.createFrame(-1, 48, 81));
        this.mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(this.mainButton);
        RadialProgressView radialProgressView = new RadialProgressView(this, context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    marginLayoutParams.rightMargin = (int) (AndroidUtilities.dp(10.0f) + (Math.min(point.x, point.y) * 0.1f));
                    return;
                }
                marginLayoutParams.rightMargin = AndroidUtilities.dp(10.0f);
            }
        };
        this.radialProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(8);
        this.frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        this.radialProgressAutoAnimator = VerticalPositionAutoAnimator.attach(this.radialProgressView);
        this.actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();
        ActionBar actionBar = new ActionBar(this, context, resourcesProvider) {
            @Override
            public void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i, i2);
            }
        };
        this.actionBar = actionBar;
        actionBar.setBackgroundColor(0);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        updateActionBarColors();
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    BotWebViewSheet.this.onCheckDismissByUser();
                }
            }
        });
        this.actionBar.setAlpha(0.0f);
        this.frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2, 49));
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.frameLayout;
        ChatAttachAlertBotWebViewLayout.WebProgressView webProgressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(this, context, resourcesProvider) {
            @Override
            protected void onMeasure(int i, int i2) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    android.graphics.Point point = AndroidUtilities.displaySize;
                    i = View.MeasureSpec.makeMeasureSpec((int) (Math.min(point.x, point.y) * 0.8f), 1073741824);
                }
                super.onMeasure(i, i2);
            }
        };
        this.progressView = webProgressView;
        sizeNotifierFrameLayout2.addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 81, 0.0f, 0.0f, 0.0f, 0.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() {
            @Override
            public final void accept(Object obj) {
                BotWebViewSheet.this.lambda$new$8((Float) obj);
            }
        });
        this.swipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$9();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$new$10();
            }
        });
        this.swipeContainer.setDelegate(new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.Delegate() {
            @Override
            public final void onDismiss() {
                BotWebViewSheet.this.lambda$new$11();
            }
        });
        this.swipeContainer.setTopActionBarOffsetY((ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(24.0f));
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Boolean lambda$new$12;
                lambda$new$12 = BotWebViewSheet.this.lambda$new$12((Void) obj);
                return lambda$new$12;
            }
        });
        PasscodeView passcodeView = new PasscodeView(context);
        this.passcodeView = passcodeView;
        this.frameLayout.addView(passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        setContentView(this.frameLayout, new ViewGroup.LayoutParams(-1, -1));
    }

    public class AnonymousClass2 implements BotWebViewContainer.Delegate {
        private boolean sentWebViewData;
        final Context val$context;
        final Theme.ResourcesProvider val$resourcesProvider;

        @Override
        public boolean isClipboardAvailable() {
            return BotWebViewContainer.Delegate.CC.$default$isClipboardAvailable(this);
        }

        @Override
        public void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        AnonymousClass2(Context context, Theme.ResourcesProvider resourcesProvider) {
            this.val$context = context;
            this.val$resourcesProvider = resourcesProvider;
        }

        @Override
        public void onCloseRequested(Runnable runnable) {
            BotWebViewSheet.this.dismiss(runnable);
        }

        @Override
        public void onWebAppSetupClosingBehavior(boolean z) {
            BotWebViewSheet.this.needCloseConfirmation = z;
        }

        @Override
        public void onSendWebViewData(String str) {
            if (BotWebViewSheet.this.queryId != 0 || this.sentWebViewData) {
                return;
            }
            this.sentWebViewData = true;
            TLRPC$TL_messages_sendWebViewData tLRPC$TL_messages_sendWebViewData = new TLRPC$TL_messages_sendWebViewData();
            tLRPC$TL_messages_sendWebViewData.bot = MessagesController.getInstance(BotWebViewSheet.this.currentAccount).getInputUser(BotWebViewSheet.this.botId);
            tLRPC$TL_messages_sendWebViewData.random_id = Utilities.random.nextLong();
            tLRPC$TL_messages_sendWebViewData.button_text = BotWebViewSheet.this.buttonText;
            tLRPC$TL_messages_sendWebViewData.data = str;
            ConnectionsManager.getInstance(BotWebViewSheet.this.currentAccount).sendRequest(tLRPC$TL_messages_sendWebViewData, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.AnonymousClass2.this.lambda$onSendWebViewData$0(tLObject, tLRPC$TL_error);
                }
            });
        }

        public void lambda$onSendWebViewData$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$TL_updates) {
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
            }
            final BotWebViewSheet botWebViewSheet = BotWebViewSheet.this;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    BotWebViewSheet.this.dismiss();
                }
            });
        }

        @Override
        public void onWebAppSetActionBarColor(String str) {
            final int i = BotWebViewSheet.this.actionBarColor;
            final int color = BotWebViewSheet.this.getColor(str);
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppSetActionBarColor$1(i, color, valueAnimator);
                }
            });
            duration.start();
        }

        public void lambda$onWebAppSetActionBarColor$1(int i, int i2, ValueAnimator valueAnimator) {
            BotWebViewSheet.this.actionBarColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            BotWebViewSheet.this.frameLayout.invalidate();
        }

        @Override
        public void onWebAppSetBackgroundColor(final int i) {
            BotWebViewSheet.this.overrideBackgroundColor = true;
            final int color = BotWebViewSheet.this.backgroundPaint.getColor();
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppSetBackgroundColor$2(color, i, valueAnimator);
                }
            });
            duration.start();
        }

        public void lambda$onWebAppSetBackgroundColor$2(int i, int i2, ValueAnimator valueAnimator) {
            BotWebViewSheet.this.backgroundPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
            BotWebViewSheet.this.frameLayout.invalidate();
        }

        @Override
        public void onSetBackButtonVisible(boolean z) {
            AndroidUtilities.updateImageViewImageAnimated(BotWebViewSheet.this.actionBar.getBackButton(), z ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
        }

        @Override
        public void onWebAppOpenInvoice(final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
            if (tLObject instanceof TLRPC$TL_payments_paymentForm) {
                TLRPC$TL_payments_paymentForm tLRPC$TL_payments_paymentForm = (TLRPC$TL_payments_paymentForm) tLObject;
                MessagesController.getInstance(BotWebViewSheet.this.currentAccount).putUsers(tLRPC$TL_payments_paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(tLRPC$TL_payments_paymentForm, str, lastFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC$TL_payments_paymentReceipt ? new PaymentFormActivity((TLRPC$TL_payments_paymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
                AndroidUtilities.hideKeyboard(BotWebViewSheet.this.frameLayout);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(this.val$context, this.val$resourcesProvider);
                overlayActionBarLayoutDialog.show();
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                    @Override
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppOpenInvoice$3(overlayActionBarLayoutDialog, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(this.val$resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        public void lambda$onWebAppOpenInvoice$3(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            BotWebViewSheet.this.webViewContainer.onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public void onWebAppExpand() {
            if (BotWebViewSheet.this.swipeContainer.isSwipeInProgress()) {
                return;
            }
            BotWebViewSheet.this.swipeContainer.stickTo((-BotWebViewSheet.this.swipeContainer.getOffsetY()) + BotWebViewSheet.this.swipeContainer.getTopActionBarOffsetY());
        }

        @Override
        public void onWebAppSwitchInlineQuery(final TLRPC$User tLRPC$User, final String str, List<String> list) {
            if (list.isEmpty()) {
                if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                    BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                    if (lastFragment instanceof ChatActivity) {
                        ChatActivityEnterView chatActivityEnterView = ((ChatActivity) lastFragment).getChatActivityEnterView();
                        chatActivityEnterView.setFieldText("@" + UserObject.getPublicUsername(tLRPC$User) + " " + str);
                        BotWebViewSheet.this.dismiss();
                        return;
                    }
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 14);
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("allowGroups", list.contains("groups"));
            bundle.putBoolean("allowUsers", list.contains("users"));
            bundle.putBoolean("allowChannels", list.contains("channels"));
            bundle.putBoolean("allowBots", list.contains("bots"));
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            AndroidUtilities.hideKeyboard(BotWebViewSheet.this.frameLayout);
            final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(this.val$context, this.val$resourcesProvider);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
                    boolean lambda$onWebAppSwitchInlineQuery$4;
                    lambda$onWebAppSwitchInlineQuery$4 = BotWebViewSheet.AnonymousClass2.this.lambda$onWebAppSwitchInlineQuery$4(tLRPC$User, str, overlayActionBarLayoutDialog, dialogsActivity2, arrayList, charSequence, z, topicsFragment);
                    return lambda$onWebAppSwitchInlineQuery$4;
                }
            });
            overlayActionBarLayoutDialog.show();
            overlayActionBarLayoutDialog.addFragment(dialogsActivity);
        }

        public boolean lambda$onWebAppSwitchInlineQuery$4(TLRPC$User tLRPC$User, String str, OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
            long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
            } else if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            bundle.putString("inline_query_input", "@" + UserObject.getPublicUsername(tLRPC$User) + " " + str);
            if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                BaseFragment lastFragment = ((LaunchActivity) BotWebViewSheet.this.parentActivity).getActionBarLayout().getLastFragment();
                if (MessagesController.getInstance(BotWebViewSheet.this.currentAccount).checkCanOpenChat(bundle, lastFragment)) {
                    overlayActionBarLayoutDialog.dismiss();
                    BotWebViewSheet.this.dismissed = true;
                    AndroidUtilities.cancelRunOnUIThread(BotWebViewSheet.this.pollRunnable);
                    BotWebViewSheet.this.webViewContainer.destroyWebView();
                    NotificationCenter.getInstance(BotWebViewSheet.this.currentAccount).removeObserver(BotWebViewSheet.this, NotificationCenter.webViewResultSent);
                    NotificationCenter.getGlobalInstance().removeObserver(BotWebViewSheet.this, NotificationCenter.didSetNewTheme);
                    BotWebViewSheet.super.dismiss();
                    lastFragment.presentFragment(new INavigationLayout.NavigationParams(new ChatActivity(bundle)).setRemoveLast(true));
                }
            }
            return true;
        }

        @Override
        public void onSetupMainButton(final boolean z, boolean z2, String str, int i, int i2, final boolean z3) {
            BotWebViewSheet.this.mainButton.setClickable(z2);
            BotWebViewSheet.this.mainButton.setText(str);
            BotWebViewSheet.this.mainButton.setTextColor(i2);
            BotWebViewSheet.this.mainButton.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
            if (z != BotWebViewSheet.this.mainButtonWasVisible) {
                BotWebViewSheet.this.mainButtonWasVisible = z;
                BotWebViewSheet.this.mainButton.animate().cancel();
                if (z) {
                    BotWebViewSheet.this.mainButton.setAlpha(0.0f);
                    BotWebViewSheet.this.mainButton.setVisibility(0);
                }
                BotWebViewSheet.this.mainButton.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            BotWebViewSheet.this.mainButton.setVisibility(8);
                        }
                        BotWebViewSheet.this.swipeContainer.requestLayout();
                    }
                }).start();
            }
            BotWebViewSheet.this.radialProgressView.setProgressColor(i2);
            if (z3 != BotWebViewSheet.this.mainButtonProgressWasVisible) {
                BotWebViewSheet.this.mainButtonProgressWasVisible = z3;
                BotWebViewSheet.this.radialProgressView.animate().cancel();
                if (z3) {
                    BotWebViewSheet.this.radialProgressView.setAlpha(0.0f);
                    BotWebViewSheet.this.radialProgressView.setVisibility(0);
                }
                BotWebViewSheet.this.radialProgressView.animate().alpha(z3 ? 1.0f : 0.0f).scaleX(z3 ? 1.0f : 0.1f).scaleY(z3 ? 1.0f : 0.1f).setDuration(250L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (z3) {
                            return;
                        }
                        BotWebViewSheet.this.radialProgressView.setVisibility(8);
                    }
                }).start();
            }
        }
    }

    public void lambda$new$5(int i, boolean z) {
        if (i > AndroidUtilities.dp(20.0f)) {
            ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
            webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
        }
    }

    public void lambda$new$6(View view) {
        this.webViewContainer.onMainButtonPressed();
    }

    public void lambda$new$8(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BotWebViewSheet.this.lambda$new$7(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    BotWebViewSheet.this.progressView.setVisibility(8);
                }
            });
            duration.start();
        }
    }

    public void lambda$new$7(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$new$9() {
        if (this.swipeContainer.getSwipeOffsetY() > 0.0f) {
            this.dimPaint.setAlpha((int) ((1.0f - MathUtils.clamp(this.swipeContainer.getSwipeOffsetY() / this.swipeContainer.getHeight(), 0.0f, 1.0f)) * 64.0f));
        } else {
            this.dimPaint.setAlpha(64);
        }
        this.frameLayout.invalidate();
        this.webViewContainer.invalidateViewPortHeight();
        if (this.springAnimation != null) {
            float f = (1.0f - (Math.min(this.swipeContainer.getTopActionBarOffsetY(), this.swipeContainer.getTranslationY() - this.swipeContainer.getTopActionBarOffsetY()) / this.swipeContainer.getTopActionBarOffsetY()) > 0.5f ? 1 : 0) * 100.0f;
            if (this.springAnimation.getSpring().getFinalPosition() != f) {
                this.springAnimation.getSpring().setFinalPosition(f);
                this.springAnimation.start();
            }
        }
        float max = Math.max(0.0f, this.swipeContainer.getSwipeOffsetY());
        this.mainButtonAutoAnimator.setOffsetY(max);
        this.radialProgressAutoAnimator.setOffsetY(max);
        System.currentTimeMillis();
    }

    public void lambda$new$10() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    public void lambda$new$11() {
        if (onCheckDismissByUser()) {
            return;
        }
        this.swipeContainer.stickTo(0.0f);
    }

    public Boolean lambda$new$12(Void r2) {
        return Boolean.valueOf(this.frameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Context context = getContext();
        if ((context instanceof ContextWrapper) && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).addOverlayPasscodeView(this.passcodeView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Context context = getContext();
        if ((context instanceof ContextWrapper) && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).removeOverlayPasscodeView(this.passcodeView);
        }
    }

    public void setParentActivity(Activity activity) {
        this.parentActivity = activity;
    }

    private void updateActionBarColors() {
        this.actionBar.setTitleColor(getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(getColor("actionBarWhiteSelector"), false);
        this.actionBar.setPopupBackgroundColor(getColor("actionBarDefaultSubmenuBackground"), false);
        this.actionBar.setPopupItemsColor(getColor("actionBarDefaultSubmenuItem"), false, false);
        this.actionBar.setPopupItemsColor(getColor("actionBarDefaultSubmenuItemIcon"), true, false);
        this.actionBar.setPopupItemsSelectorColor(getColor("dialogButtonSelector"), false);
    }

    private void updateLightStatusBar() {
        boolean z = true;
        z = (AndroidUtilities.isTablet() || ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", null, true)) < 0.9d || this.actionBarTransitionProgress < 0.85f) ? false : false;
        Boolean bool = this.wasLightStatusBar;
        if (bool == null || bool.booleanValue() != z) {
            this.wasLightStatusBar = Boolean.valueOf(z);
            if (Build.VERSION.SDK_INT >= 23) {
                int systemUiVisibility = this.frameLayout.getSystemUiVisibility();
                this.frameLayout.setSystemUiVisibility(z ? systemUiVisibility | LiteMode.FLAG_ANIMATED_EMOJI_REACTIONS_NOT_PREMIUM : systemUiVisibility & (-8193));
            }
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        int i = Build.VERSION.SDK_INT;
        if (i >= 30) {
            window.addFlags(-2147483392);
        } else if (i >= 21) {
            window.addFlags(-2147417856);
        }
        window.setWindowAnimations(R.style.DialogNoAnimation);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        attributes.flags &= -3;
        attributes.softInputMode = 16;
        attributes.height = -1;
        if (i >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
        if (i >= 23) {
            window.setStatusBarColor(0);
        }
        this.frameLayout.setSystemUiVisibility(1280);
        if (i >= 21) {
            this.frameLayout.setOnApplyWindowInsetsListener(BotWebViewSheet$$ExternalSyntheticLambda2.INSTANCE);
        }
        if (i >= 26) {
            AndroidUtilities.setLightNavigationBar(window, ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", null, true)) >= 0.9d);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    public static WindowInsets lambda$onCreate$13(View view, WindowInsets windowInsets) {
        view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
        return windowInsets;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.springAnimation == null) {
            this.springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE).setSpring(new SpringForce().setStiffness(1200.0f).setDampingRatio(1.0f));
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SpringAnimation springAnimation = this.springAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.springAnimation = null;
        }
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z, int i4) {
        requestWebView(i, j, j2, str, str2, i2, i3, z, null, null, false, null, null, i4);
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z) {
        requestWebView(i, j, j2, str, str2, i2, i3, z, null, null, false, null, null, 0);
    }

    public void requestWebView(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z, BaseFragment baseFragment, TLRPC$BotApp tLRPC$BotApp, boolean z2, String str3, TLRPC$User tLRPC$User) {
        requestWebView(i, j, j2, str, str2, i2, i3, z, baseFragment, tLRPC$BotApp, z2, str3, tLRPC$User, 0);
    }

    public void requestWebView(final int i, long j, final long j2, String str, String str2, int i2, int i3, boolean z, BaseFragment baseFragment, TLRPC$BotApp tLRPC$BotApp, boolean z2, String str3, TLRPC$User tLRPC$User, int i4) {
        String str4;
        boolean z3;
        TLRPC$InputPeer inputPeer;
        this.currentAccount = i;
        this.peerId = j;
        this.botId = j2;
        this.replyToMsgId = i3;
        this.silent = z;
        this.buttonText = str;
        this.actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(i).getUser(Long.valueOf(j2))));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.removeAllViews();
        ActionBarMenuItem addItem = createMenu.addItem(0, R.drawable.ic_ab_other);
        addItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString(R.string.BotWebViewOpenBot));
        addItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString(R.string.BotWebViewReloadPage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i5) {
                if (i5 == -1) {
                    if (BotWebViewSheet.this.webViewContainer.onBackPressed()) {
                        return;
                    }
                    BotWebViewSheet.this.onCheckDismissByUser();
                } else if (i5 == R.id.menu_open_bot) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j2);
                    if (BotWebViewSheet.this.parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) BotWebViewSheet.this.parentActivity).lambda$runLinkRequest$79(new ChatActivity(bundle));
                    }
                    BotWebViewSheet.this.dismiss();
                } else if (i5 == R.id.menu_reload_page) {
                    if (BotWebViewSheet.this.webViewContainer.getWebView() != null) {
                        BotWebViewSheet.this.webViewContainer.getWebView().animate().cancel();
                        BotWebViewSheet.this.webViewContainer.getWebView().animate().alpha(0.0f).start();
                    }
                    BotWebViewSheet.this.progressView.setLoadProgress(0.0f);
                    BotWebViewSheet.this.progressView.setAlpha(1.0f);
                    BotWebViewSheet.this.progressView.setVisibility(0);
                    BotWebViewSheet.this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
                    BotWebViewSheet.this.webViewContainer.loadFlickerAndSettingsItem(i, j2, BotWebViewSheet.this.settingsItem);
                    BotWebViewSheet.this.webViewContainer.reload();
                } else if (i5 == R.id.menu_settings) {
                    BotWebViewSheet.this.webViewContainer.onSettingsButtonPressed();
                }
            }
        });
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("bg_color", getColor("windowBackgroundWhite"));
            jSONObject.put("secondary_bg_color", getColor("windowBackgroundGray"));
            jSONObject.put("text_color", getColor("windowBackgroundWhiteBlackText"));
            jSONObject.put("hint_color", getColor("windowBackgroundWhiteHintText"));
            jSONObject.put("link_color", getColor("windowBackgroundWhiteLinkText"));
            jSONObject.put("button_color", getColor("featuredStickers_addButton"));
            jSONObject.put("button_text_color", getColor("featuredStickers_buttonText"));
            str4 = jSONObject.toString();
            z3 = true;
        } catch (Exception e) {
            FileLog.e(e);
            str4 = null;
            z3 = false;
        }
        this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
        this.webViewContainer.loadFlickerAndSettingsItem(i, j2, this.settingsItem);
        if (i2 == 0) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
            tLRPC$TL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestWebView.platform = "android";
            if (str2 != null) {
                tLRPC$TL_messages_requestWebView.url = str2;
                tLRPC$TL_messages_requestWebView.flags |= 2;
            }
            if (i3 != 0) {
                tLRPC$TL_messages_requestWebView.reply_to_msg_id = i3;
                tLRPC$TL_messages_requestWebView.flags |= 1;
            }
            if (z3) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView.theme_params = tLRPC$TL_dataJSON;
                tLRPC$TL_dataJSON.data = str4;
                tLRPC$TL_messages_requestWebView.flags |= 4;
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$19(i, tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
        } else if (i2 == 1) {
            TLRPC$TL_messages_requestSimpleWebView tLRPC$TL_messages_requestSimpleWebView = new TLRPC$TL_messages_requestSimpleWebView();
            tLRPC$TL_messages_requestSimpleWebView.from_switch_webview = (i4 & 1) != 0;
            tLRPC$TL_messages_requestSimpleWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestSimpleWebView.platform = "android";
            if (z3) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON2 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestSimpleWebView.theme_params = tLRPC$TL_dataJSON2;
                tLRPC$TL_dataJSON2.data = str4;
                tLRPC$TL_messages_requestSimpleWebView.flags |= 1;
            }
            tLRPC$TL_messages_requestSimpleWebView.url = str2;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestSimpleWebView, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$17(i, tLObject, tLRPC$TL_error);
                }
            });
        } else if (i2 == 2) {
            TLRPC$TL_messages_requestWebView tLRPC$TL_messages_requestWebView2 = new TLRPC$TL_messages_requestWebView();
            tLRPC$TL_messages_requestWebView2.bot = MessagesController.getInstance(i).getInputUser(j2);
            tLRPC$TL_messages_requestWebView2.peer = MessagesController.getInstance(i).getInputPeer(j2);
            tLRPC$TL_messages_requestWebView2.platform = "android";
            tLRPC$TL_messages_requestWebView2.url = str2;
            tLRPC$TL_messages_requestWebView2.flags |= 2;
            if (z3) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON3 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestWebView2.theme_params = tLRPC$TL_dataJSON3;
                tLRPC$TL_dataJSON3.data = str4;
                tLRPC$TL_messages_requestWebView2.flags |= 4;
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestWebView2, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$15(i, tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
        } else if (i2 != 3) {
        } else {
            TLRPC$TL_messages_requestAppWebView tLRPC$TL_messages_requestAppWebView = new TLRPC$TL_messages_requestAppWebView();
            TLRPC$TL_inputBotAppID tLRPC$TL_inputBotAppID = new TLRPC$TL_inputBotAppID();
            tLRPC$TL_inputBotAppID.id = tLRPC$BotApp.id;
            tLRPC$TL_inputBotAppID.access_hash = tLRPC$BotApp.access_hash;
            tLRPC$TL_messages_requestAppWebView.app = tLRPC$TL_inputBotAppID;
            tLRPC$TL_messages_requestAppWebView.write_allowed = z2;
            tLRPC$TL_messages_requestAppWebView.platform = "android";
            if (baseFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                inputPeer = chatActivity.getCurrentUser() != null ? MessagesController.getInputPeer(chatActivity.getCurrentUser()) : MessagesController.getInputPeer(chatActivity.getCurrentChat());
            } else {
                inputPeer = MessagesController.getInputPeer(tLRPC$User);
            }
            tLRPC$TL_messages_requestAppWebView.peer = inputPeer;
            if (!TextUtils.isEmpty(str3)) {
                tLRPC$TL_messages_requestAppWebView.start_param = str3;
                tLRPC$TL_messages_requestAppWebView.flags |= 2;
            }
            if (z3) {
                TLRPC$TL_dataJSON tLRPC$TL_dataJSON4 = new TLRPC$TL_dataJSON();
                tLRPC$TL_messages_requestAppWebView.theme_params = tLRPC$TL_dataJSON4;
                tLRPC$TL_dataJSON4.data = str4;
                tLRPC$TL_messages_requestAppWebView.flags |= 4;
            }
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_requestAppWebView, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    BotWebViewSheet.this.lambda$requestWebView$21(i, tLObject, tLRPC$TL_error);
                }
            }, 66);
        }
    }

    public void lambda$requestWebView$15(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$14(tLObject, i);
            }
        });
    }

    public void lambda$requestWebView$14(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public void lambda$requestWebView$17(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$16(tLObject, i);
            }
        });
    }

    public void lambda$requestWebView$16(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_simpleWebViewResultUrl) {
            this.queryId = 0L;
            this.webViewContainer.loadUrl(i, ((TLRPC$TL_simpleWebViewResultUrl) tLObject).url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
        }
    }

    public void lambda$requestWebView$19(final int i, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$18(tLObject, i);
            }
        });
    }

    public void lambda$requestWebView$18(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC$TL_webViewResultUrl) {
            TLRPC$TL_webViewResultUrl tLRPC$TL_webViewResultUrl = (TLRPC$TL_webViewResultUrl) tLObject;
            this.queryId = tLRPC$TL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tLRPC$TL_webViewResultUrl.url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public void lambda$requestWebView$21(final int i, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$requestWebView$20(tLRPC$TL_error, tLObject, i);
            }
        });
    }

    public void lambda$requestWebView$20(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        if (tLRPC$TL_error == null) {
            this.queryId = 0L;
            this.webViewContainer.loadUrl(i, ((TLRPC$TL_appWebViewResultUrl) tLObject).url);
            this.swipeContainer.setWebView(this.webViewContainer.getWebView());
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public int getColor(String str) {
        Integer valueOf;
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            valueOf = resourcesProvider.getColor(str);
        } else {
            valueOf = Integer.valueOf(Theme.getColor(str));
        }
        return valueOf != null ? valueOf.intValue() : Theme.getColor(str);
    }

    @Override
    public void show() {
        this.frameLayout.setAlpha(0.0f);
        this.frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                view.removeOnLayoutChangeListener(this);
                BotWebViewSheet.this.swipeContainer.setSwipeOffsetY(BotWebViewSheet.this.swipeContainer.getHeight());
                BotWebViewSheet.this.frameLayout.setAlpha(1.0f);
                new SpringAnimation(BotWebViewSheet.this.swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0.0f).setSpring(new SpringForce(0.0f).setDampingRatio(0.75f).setStiffness(500.0f)).start();
            }
        });
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            if (getOwnerActivity() != null) {
                getOwnerActivity().finish();
            }
        } else if (this.webViewContainer.onBackPressed()) {
        } else {
            onCheckDismissByUser();
        }
    }

    @Override
    public void dismiss() {
        dismiss(null);
    }

    public boolean onCheckDismissByUser() {
        if (this.needCloseConfirmation) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
            AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved)).setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    BotWebViewSheet.this.lambda$onCheckDismissByUser$22(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
            create.show();
            ((TextView) create.getButton(-1)).setTextColor(getColor("text_RedBold"));
            return false;
        }
        dismiss();
        return true;
    }

    public void lambda$onCheckDismissByUser$22(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    public void dismiss(final Runnable runnable) {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
        this.webViewContainer.destroyWebView();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo(webViewSwipeContainer.getHeight() + this.frameLayout.measureKeyboardHeight(), new Runnable() {
            @Override
            public final void run() {
                BotWebViewSheet.this.lambda$dismiss$23(runnable);
            }
        });
    }

    public void lambda$dismiss$23(Runnable runnable) {
        super.dismiss();
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.webViewResultSent) {
            if (this.queryId == ((Long) objArr[0]).longValue()) {
                dismiss();
            }
        } else if (i == NotificationCenter.didSetNewTheme) {
            this.frameLayout.invalidate();
            this.webViewContainer.updateFlickerBackgroundColor(getColor("windowBackgroundWhite"));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }
}