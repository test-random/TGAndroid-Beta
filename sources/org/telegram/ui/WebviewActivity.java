package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.WebviewActivity;

public class WebviewActivity extends BaseFragment {
    private String currentBot;
    private long currentDialogId;
    private String currentGame;
    private MessageObject currentMessageObject;
    private String currentUrl;
    private String linkToCopy;
    private boolean loadStats;
    private ActionBarMenuItem progressItem;
    private ContextProgressView progressView;
    private String short_param;
    private int type;
    public Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {
            if (WebviewActivity.this.currentMessageObject == null || WebviewActivity.this.getParentActivity() == null) {
                return;
            }
            WebviewActivity webviewActivity = WebviewActivity.this;
            if (webviewActivity.typingRunnable == null) {
                return;
            }
            MessagesController.getInstance(((BaseFragment) webviewActivity).currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 0L, 6, 0);
            AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000L);
        }
    };
    private WebView webView;

    public class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        public void lambda$postEvent$0(String str) {
            TLRPC.Message message;
            boolean z;
            if (WebviewActivity.this.getParentActivity() == null) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d(str);
            }
            str.hashCode();
            if (!str.equals("share_game")) {
                if (str.equals("share_score")) {
                    message = WebviewActivity.this.currentMessageObject.messageOwner;
                    z = true;
                }
                WebviewActivity webviewActivity = WebviewActivity.this;
                webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
            }
            message = WebviewActivity.this.currentMessageObject.messageOwner;
            z = false;
            message.with_my_score = z;
            WebviewActivity webviewActivity2 = WebviewActivity.this;
            webviewActivity2.showDialog(ShareAlert.createShareAlert(webviewActivity2.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
        }

        @JavascriptInterface
        public void postEvent(final String str, String str2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    WebviewActivity.TelegramWebviewProxy.this.lambda$postEvent$0(str);
                }
            });
        }
    }

    public WebviewActivity(String str, String str2, String str3, String str4, MessageObject messageObject) {
        String str5;
        this.currentUrl = str;
        this.currentBot = str2;
        this.currentGame = str3;
        this.currentMessageObject = messageObject;
        this.short_param = str4;
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        sb.append("/");
        sb.append(this.currentBot);
        if (TextUtils.isEmpty(str4)) {
            str5 = "";
        } else {
            str5 = "?game=" + str4;
        }
        sb.append(str5);
        this.linkToCopy = sb.toString();
        this.type = 0;
    }

    public void lambda$reloadStats$0(TLObject tLObject) {
        this.loadStats = false;
        if (tLObject != null) {
            WebView webView = this.webView;
            String str = ((TLRPC.TL_statsURL) tLObject).url;
            this.currentUrl = str;
            webView.loadUrl(str);
        }
    }

    public void lambda$reloadStats$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WebviewActivity.this.lambda$reloadStats$0(tLObject);
            }
        });
    }

    public static void openGameInBrowser(String str, MessageObject messageObject, Activity activity, String str2, String str3) {
        StringBuilder sb;
        String str4 = "";
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String string = sharedPreferences.getString("" + messageObject.getId(), null);
            StringBuilder sb2 = new StringBuilder(string != null ? string : "");
            StringBuilder sb3 = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
            if (string == null) {
                char[] charArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    sb2.append(charArray[Utilities.random.nextInt(charArray.length)]);
                }
            }
            sb3.append((CharSequence) sb2);
            int indexOf = str.indexOf(35);
            if (indexOf < 0) {
                sb = new StringBuilder();
                sb.append(str);
                sb.append("#");
            } else {
                String substring = str.substring(indexOf + 1);
                if (substring.indexOf(61) < 0 && substring.indexOf(63) < 0) {
                    if (substring.length() > 0) {
                        sb = new StringBuilder();
                        sb.append(str);
                        sb.append("?");
                    } else {
                        sb = new StringBuilder();
                        sb.append(str);
                    }
                }
                sb = new StringBuilder();
                sb.append(str);
                sb.append("&");
            }
            sb.append((Object) sb3);
            String sb4 = sb.toString();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt(((Object) sb2) + "_date", (int) (System.currentTimeMillis() / 1000));
            SerializedData serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            edit.putString(((Object) sb2) + "_m", Utilities.bytesToHex(serializedData.toByteArray()));
            String str5 = ((Object) sb2) + "_link";
            StringBuilder sb5 = new StringBuilder();
            sb5.append("https://");
            sb5.append(MessagesController.getInstance(messageObject.currentAccount).linkPrefix);
            sb5.append("/");
            sb5.append(str3);
            if (!TextUtils.isEmpty(str2)) {
                str4 = "?game=" + str2;
            }
            sb5.append(str4);
            edit.putString(str5, sb5.toString());
            edit.commit();
            Browser.openUrl((Context) activity, sb4, false);
            serializedData.cleanup();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void reloadStats(String str) {
        if (this.loadStats) {
            return;
        }
        this.loadStats = true;
        TLRPC.TL_messages_getStatsURL tL_messages_getStatsURL = new TLRPC.TL_messages_getStatsURL();
        tL_messages_getStatsURL.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.currentDialogId);
        if (str == null) {
            str = "";
        }
        tL_messages_getStatsURL.params = str;
        tL_messages_getStatsURL.dark = Theme.getCurrentTheme().isDark();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStatsURL, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WebviewActivity.this.lambda$reloadStats$1(tLObject, tL_error);
            }
        });
    }

    public static boolean supportWebview() {
        return ("samsung".equals(Build.MANUFACTURER) && "GT-I9500".equals(Build.MODEL)) ? false : true;
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    WebviewActivity.this.lambda$onBackPressed$323();
                    return;
                }
                if (i != 1) {
                    if (i == 2) {
                        WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
                    }
                } else if (WebviewActivity.this.currentMessageObject != null) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                    WebviewActivity webviewActivity = WebviewActivity.this;
                    webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.progressItem = createMenu.addItemWithWidth(1, R.drawable.share, AndroidUtilities.dp(54.0f));
        int i = this.type;
        if (i == 0) {
            createMenu.addItem(0, R.drawable.ic_ab_other).addSubItem(2, R.drawable.msg_openin, LocaleController.getString(R.string.OpenInExternalApp));
            this.actionBar.setTitle(this.currentGame);
            this.actionBar.setSubtitle("@" + this.currentBot);
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            this.progressItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
        } else if (i == 1) {
            this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            ActionBar actionBar = this.actionBar;
            int i2 = Theme.key_player_actionBarItems;
            actionBar.setItemsColor(Theme.getColor(i2), false);
            this.actionBar.setItemsColor(Theme.getColor(i2), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_player_actionBarSelector), false);
            this.actionBar.setTitleColor(Theme.getColor(Theme.key_player_actionBarTitle));
            this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
            this.actionBar.setTitle(LocaleController.getString(R.string.Statistics));
            ContextProgressView contextProgressView2 = new ContextProgressView(context, 3);
            this.progressView = contextProgressView2;
            this.progressItem.addView(contextProgressView2, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getContentView().setVisibility(8);
            this.progressItem.setEnabled(false);
        }
        AndroidUtilities.checkAndroidTheme(context, true);
        WebView webView = new WebView(context);
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        int i3 = Build.VERSION.SDK_INT;
        this.webView.setLayerType(2, null);
        this.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (i3 >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            if (this.type == 0) {
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
            }
        }
        this.webView.setWebViewClient(new WebViewClient() {
            private boolean isInternalUrl(String str) {
                if (TextUtils.isEmpty(str)) {
                    return false;
                }
                Uri parse = Uri.parse(str);
                if (!"tg".equals(parse.getScheme())) {
                    return false;
                }
                if (WebviewActivity.this.type == 1) {
                    try {
                        WebviewActivity.this.reloadStats(Uri.parse(str.replace("tg:statsrefresh", "tg://telegram.org")).getQueryParameter("params"));
                    } catch (Throwable th) {
                        e = th;
                        FileLog.e(e);
                        return true;
                    }
                } else {
                    WebviewActivity.this.finishFragment(false);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", parse);
                        intent.setComponent(new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName()));
                        intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                        ApplicationLoader.applicationContext.startActivity(intent);
                    } catch (Exception e) {
                        e = e;
                        FileLog.e(e);
                        return true;
                    }
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView webView2, String str) {
                if (isInternalUrl(str)) {
                    return;
                }
                super.onLoadResource(webView2, str);
            }

            @Override
            public void onPageFinished(WebView webView2, String str) {
                super.onPageFinished(webView2, str);
                if (WebviewActivity.this.progressView == null || WebviewActivity.this.progressView.getVisibility() != 0) {
                    return;
                }
                AnimatorSet animatorSet = new AnimatorSet();
                if (WebviewActivity.this.type == 0) {
                    WebviewActivity.this.progressItem.getContentView().setVisibility(0);
                    WebviewActivity.this.progressItem.setEnabled(true);
                    animatorSet.playTogether(ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleX", 0.0f, 1.0f), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleY", 0.0f, 1.0f), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "alpha", 0.0f, 1.0f));
                } else {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", 1.0f, 0.1f), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", 1.0f, 0.0f));
                }
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (WebviewActivity.this.type == 1) {
                            WebviewActivity.this.progressItem.setVisibility(8);
                        } else {
                            WebviewActivity.this.progressView.setVisibility(4);
                        }
                    }
                });
                animatorSet.setDuration(150L);
                animatorSet.start();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView2, String str) {
                return isInternalUrl(str) || super.shouldOverrideUrlLoading(webView2, str);
            }
        });
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ThemeDescription themeDescription;
        ArrayList arrayList = new ArrayList();
        if (this.type == 0) {
            arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
            arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2));
            themeDescription = new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2);
        } else {
            arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_player_actionBarItems));
            ActionBar actionBar = this.actionBar;
            int i = ThemeDescription.FLAG_AB_TITLECOLOR;
            int i2 = Theme.key_player_actionBarTitle;
            arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, i2));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_player_actionBarSelector));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
            arrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner4));
            themeDescription = new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter4);
        }
        arrayList.add(themeDescription);
        return arrayList;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.checkAndroidTheme(getContext(), false);
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.webView.setLayerType(0, null);
        this.typingRunnable = null;
        try {
            ViewParent parent = this.webView.getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(this.webView);
            }
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
            this.webView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable.run();
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        WebView webView;
        if (!z || z2 || (webView = this.webView) == null) {
            return;
        }
        webView.loadUrl(this.currentUrl);
    }
}
