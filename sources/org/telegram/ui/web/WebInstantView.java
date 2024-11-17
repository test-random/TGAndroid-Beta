package org.telegram.ui.web;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Timer;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.web.BotWebViewContainer;
import org.telegram.ui.web.MHTML;
import org.telegram.ui.web.WebInstantView;

public class WebInstantView {
    public static final HashMap instants = new HashMap();
    private static HashMap loadingPhotos;
    public final HashMap loadedPhotos = new HashMap();
    public MHTML mhtml;
    public String url;
    public TLRPC.WebPage webpage;

    public class AnonymousClass4 {
        final boolean[] val$done;
        final WebView val$webView;
        final FrameLayout val$webViewContainer;
        final Utilities.Callback val$whenDone;

        AnonymousClass4(boolean[] zArr, WebView webView, FrameLayout frameLayout, Utilities.Callback callback) {
            this.val$done = zArr;
            this.val$webView = webView;
            this.val$webViewContainer = frameLayout;
            this.val$whenDone = callback;
        }

        public static void lambda$done$0(boolean[] zArr, WebView webView, FrameLayout frameLayout, String str, Utilities.Callback callback) {
            JSONObject jSONObject;
            if (zArr[0]) {
                return;
            }
            zArr[0] = true;
            if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                webView.onPause();
                webView.destroy();
                AndroidUtilities.removeFromParent(webView);
                AndroidUtilities.removeFromParent(frameLayout);
            }
            try {
                jSONObject = new JSONObject(str);
            } catch (Exception e) {
                FileLog.e(e);
                jSONObject = null;
            }
            callback.run(jSONObject);
        }

        @JavascriptInterface
        public void done(final String str) {
            final boolean[] zArr = this.val$done;
            final WebView webView = this.val$webView;
            final FrameLayout frameLayout = this.val$webViewContainer;
            final Utilities.Callback callback = this.val$whenDone;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    WebInstantView.AnonymousClass4.lambda$done$0(zArr, webView, frameLayout, str, callback);
                }
            });
        }
    }

    public static class Loader {
        private Runnable cancelLocal;
        private boolean cancelled;
        private final int currentAccount;
        public boolean currentIsLoaded;
        public float currentProgress;
        public String currentUrl;
        private boolean gotLocal;
        private boolean gotRemote;
        private final ArrayList listeners = new ArrayList();
        private TLRPC.WebPage localPage;
        private TLRPC.WebPage remotePage;
        private int reqId;
        private boolean started;

        public Loader(int i) {
            this.currentAccount = i;
        }

        public void lambda$listen$4(Runnable runnable) {
            this.listeners.remove(runnable);
        }

        public void lambda$retryLocal$0(WebInstantView webInstantView) {
            this.cancelLocal = null;
            this.gotLocal = true;
            TLRPC.WebPage webPage = this.localPage;
            if (webPage != null) {
                WebInstantView.recycle(webPage);
            }
            this.localPage = webInstantView.webpage;
            notifyUpdate();
        }

        public void lambda$start$1(WebInstantView webInstantView) {
            this.cancelLocal = null;
            this.gotLocal = true;
            TLRPC.WebPage webPage = this.localPage;
            if (webPage != null) {
                WebInstantView.recycle(webPage);
            }
            this.localPage = webInstantView.webpage;
            notifyUpdate();
        }

        public void lambda$start$2(org.telegram.tgnet.TLObject r5) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.web.WebInstantView.Loader.lambda$start$2(org.telegram.tgnet.TLObject):void");
        }

        public void lambda$start$3(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    WebInstantView.Loader.this.lambda$start$2(tLObject);
                }
            });
        }

        private void notifyUpdate() {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
        }

        public void cancel() {
            Runnable runnable;
            if (this.cancelled) {
                return;
            }
            this.cancelled = true;
            if (!this.gotRemote) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            }
            if (this.gotLocal || (runnable = this.cancelLocal) == null) {
                return;
            }
            runnable.run();
        }

        public TLRPC.WebPage getWebPage() {
            TLRPC.WebPage webPage;
            if (!SharedConfig.onlyLocalInstantView && (webPage = this.remotePage) != null) {
                return webPage;
            }
            TLRPC.WebPage webPage2 = this.localPage;
            if (webPage2 != null) {
                return webPage2;
            }
            return null;
        }

        public boolean isDone() {
            return (this.gotRemote && this.gotLocal) || this.remotePage != null || this.localPage != null || this.cancelled;
        }

        public Runnable listen(final Runnable runnable) {
            this.listeners.add(runnable);
            return new Runnable() {
                @Override
                public final void run() {
                    WebInstantView.Loader.this.lambda$listen$4(runnable);
                }
            };
        }

        public void recycle() {
            TLRPC.WebPage webPage = this.localPage;
            if (webPage != null) {
                WebInstantView.recycle(webPage);
                this.localPage = null;
            }
        }

        public void retryLocal(BotWebViewContainer.MyWebView myWebView) {
            if (this.cancelled) {
                return;
            }
            TLRPC.WebPage webPage = this.localPage;
            if (webPage != null) {
                WebInstantView.recycle(webPage);
                this.localPage = null;
            }
            this.gotLocal = false;
            this.currentUrl = myWebView.getUrl();
            this.currentProgress = myWebView.getProgress();
            this.currentIsLoaded = myWebView.isPageLoaded();
            Runnable runnable = this.cancelLocal;
            if (runnable != null) {
                runnable.run();
            }
            this.cancelLocal = WebInstantView.generate(myWebView, false, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    WebInstantView.Loader.this.lambda$retryLocal$0((WebInstantView) obj);
                }
            });
        }

        public void start(BotWebViewContainer.MyWebView myWebView) {
            if (this.started) {
                return;
            }
            this.started = true;
            this.currentUrl = myWebView.getUrl();
            this.currentProgress = myWebView.getProgress();
            this.currentIsLoaded = myWebView.isPageLoaded();
            this.cancelLocal = WebInstantView.generate(myWebView, false, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    WebInstantView.Loader.this.lambda$start$1((WebInstantView) obj);
                }
            });
            TLRPC.TL_messages_getWebPage tL_messages_getWebPage = new TLRPC.TL_messages_getWebPage();
            tL_messages_getWebPage.url = this.currentUrl;
            tL_messages_getWebPage.hash = 0;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getWebPage, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WebInstantView.Loader.this.lambda$start$3(tLObject, tL_error);
                }
            });
        }
    }

    public class WebPhoto extends TLRPC.Photo {
        public int h;
        public TLRPC.TL_textImage inlineImage;
        public WebInstantView instantView;
        public String url;
        public HashSet urls = new HashSet();
        public int w;

        public WebPhoto() {
        }
    }

    public static TLRPC.RichText addLastSpace(TLRPC.RichText richText) {
        TLRPC.TL_textPlain tL_textPlain;
        String str;
        if (richText == null) {
            return richText;
        }
        TLRPC.RichText richText2 = richText.text;
        if (richText2 == null) {
            if (richText.texts.isEmpty()) {
                if ((richText instanceof TLRPC.TL_textPlain) && (str = (tL_textPlain = (TLRPC.TL_textPlain) richText).text) != null && !str.endsWith(" ")) {
                    tL_textPlain.text += ' ';
                }
                return richText;
            }
            richText2 = richText.texts.get(r0.size() - 1);
        }
        addLastSpace(richText2);
        return richText;
    }

    public static TLRPC.RichText addNewLine(TLRPC.RichText richText) {
        if (richText == null) {
            return richText;
        }
        TLRPC.RichText richText2 = richText.text;
        if (richText2 == null) {
            if (richText.texts.isEmpty()) {
                if (richText instanceof TLRPC.TL_textPlain) {
                    StringBuilder sb = new StringBuilder();
                    TLRPC.TL_textPlain tL_textPlain = (TLRPC.TL_textPlain) richText;
                    sb.append(tL_textPlain.text);
                    sb.append('\n');
                    tL_textPlain.text = sb.toString();
                }
                return richText;
            }
            richText2 = richText.texts.get(r0.size() - 1);
        }
        addNewLine(richText2);
        return richText;
    }

    public static TLRPC.RichText applyAnchor(TLRPC.RichText richText, JSONObject jSONObject) {
        if (jSONObject == null) {
            return richText;
        }
        String optString = jSONObject.optString("id");
        if (TextUtils.isEmpty(optString)) {
            return richText;
        }
        TLRPC.TL_textAnchor tL_textAnchor = new TLRPC.TL_textAnchor();
        tL_textAnchor.text = richText;
        tL_textAnchor.name = optString;
        return tL_textAnchor;
    }

    public static void cancelLoadPhoto(ImageReceiver imageReceiver) {
        HashMap hashMap = loadingPhotos;
        if (hashMap == null) {
            return;
        }
        for (Map.Entry entry : hashMap.entrySet()) {
            String str = (String) entry.getKey();
            ArrayList arrayList = (ArrayList) entry.getValue();
            int i = 0;
            while (true) {
                if (i >= arrayList.size()) {
                    break;
                }
                if (((Pair) arrayList.get(i)).first == imageReceiver) {
                    arrayList.remove(i);
                    break;
                }
                i++;
            }
            if (arrayList.isEmpty()) {
                loadingPhotos.remove(str);
                return;
            }
        }
    }

    public static TLRPC.RichText filterRecursiveAnchorLinks(TLRPC.RichText richText, String str, String str2) {
        TLRPC.TL_textUrl tL_textUrl;
        String str3;
        if (richText == null) {
            return richText;
        }
        if (richText instanceof TLRPC.TL_textConcat) {
            TLRPC.TL_textConcat tL_textConcat = (TLRPC.TL_textConcat) richText;
            TLRPC.TL_textConcat tL_textConcat2 = new TLRPC.TL_textConcat();
            for (int i = 0; i < tL_textConcat.texts.size(); i++) {
                TLRPC.RichText filterRecursiveAnchorLinks = filterRecursiveAnchorLinks(tL_textConcat.texts.get(i), str, str2);
                if (filterRecursiveAnchorLinks != null) {
                    tL_textConcat2.texts.add(filterRecursiveAnchorLinks);
                }
            }
            return tL_textConcat2;
        }
        if (!(richText instanceof TLRPC.TL_textUrl) || (str3 = (tL_textUrl = (TLRPC.TL_textUrl) richText).url) == null) {
            return richText;
        }
        if (!str3.toLowerCase().equals("#" + str2)) {
            if (!TextUtils.equals(tL_textUrl.url.toLowerCase(), str + "#" + str2)) {
                return richText;
            }
        }
        return null;
    }

    public static Runnable generate(WebView webView, boolean z, final Utilities.Callback callback) {
        if (callback == null) {
            return null;
        }
        if (webView == null) {
            callback.run(null);
            return null;
        }
        final boolean[] zArr = {false};
        final WebInstantView webInstantView = new WebInstantView();
        webInstantView.url = webView.getUrl();
        final Timer create = Timer.create("WebInstantView");
        final Timer.Task start = Timer.start(create, "getHTML");
        webInstantView.getHTML(webView, z, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                WebInstantView.lambda$generate$1(Timer.Task.this, zArr, create, webInstantView, callback, (InputStream) obj);
            }
        });
        return new Runnable() {
            @Override
            public final void run() {
                WebInstantView.lambda$generate$2(zArr);
            }
        };
    }

    public static void lambda$generate$0(Timer.Task task, boolean[] zArr, Timer timer, WebInstantView webInstantView, Utilities.Callback callback, JSONObject jSONObject) {
        Timer.done(task);
        if (zArr[0]) {
            return;
        }
        Timer.Task start = Timer.start(timer, "parseJSON");
        try {
            webInstantView.webpage = webInstantView.parseJSON(webInstantView.url, jSONObject);
        } catch (Exception e) {
            Timer.log(timer, "error: " + e);
            FileLog.e(e);
        }
        Timer.done(start);
        callback.run(webInstantView);
        TLRPC.WebPage webPage = webInstantView.webpage;
        if (webPage != null) {
            instants.put(webPage, webInstantView);
        }
        Timer.finish(timer);
    }

    public static void lambda$generate$1(Timer.Task task, final boolean[] zArr, final Timer timer, final WebInstantView webInstantView, final Utilities.Callback callback, InputStream inputStream) {
        Timer.done(task);
        if (zArr[0]) {
            return;
        }
        final Timer.Task start = Timer.start(timer, "readHTML");
        webInstantView.readHTML(webInstantView.url, inputStream, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                WebInstantView.lambda$generate$0(Timer.Task.this, zArr, timer, webInstantView, callback, (JSONObject) obj);
            }
        });
    }

    public static void lambda$generate$2(boolean[] zArr) {
        zArr[0] = true;
    }

    public static void lambda$getHTML$5(Utilities.Callback callback, String str) {
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(str));
            jsonReader.setLenient(true);
            String nextString = jsonReader.nextString();
            jsonReader.close();
            callback.run(new ByteArrayInputStream(nextString.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            FileLog.e(e);
            callback.run(null);
        }
    }

    public static void lambda$getHTML$6(String str) {
    }

    public void lambda$getHTML$7(WebView webView, File file, Utilities.Callback callback, String str) {
        webView.evaluateJavascript(AndroidUtilities.readRes(R.raw.open_collapsed).replace("$OPEN$", "false"), new ValueCallback() {
            @Override
            public final void onReceiveValue(Object obj) {
                WebInstantView.lambda$getHTML$6((String) obj);
            }
        });
        try {
            MHTML mhtml = new MHTML(file);
            this.mhtml = mhtml;
            if (!mhtml.entries.isEmpty()) {
                callback.run(((MHTML.Entry) this.mhtml.entries.get(0)).getInputStream());
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        callback.run(null);
    }

    public void lambda$getHTML$8(final WebView webView, final File file, final Utilities.Callback callback, String str) {
        webView.saveWebArchive(file.getAbsolutePath(), false, new ValueCallback() {
            @Override
            public final void onReceiveValue(Object obj) {
                WebInstantView.this.lambda$getHTML$7(webView, file, callback, (String) obj);
            }
        });
    }

    public void lambda$loadPhotoInternal$3(org.telegram.ui.web.WebInstantView.WebPhoto r4, android.graphics.Bitmap r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.web.WebInstantView.lambda$loadPhotoInternal$3(org.telegram.ui.web.WebInstantView$WebPhoto, android.graphics.Bitmap):void");
    }

    public void lambda$loadPhotoInternal$4(final WebPhoto webPhoto, final Bitmap bitmap) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WebInstantView.this.lambda$loadPhotoInternal$3(webPhoto, bitmap);
            }
        });
    }

    public static void loadPhoto(WebPhoto webPhoto, ImageReceiver imageReceiver, Runnable runnable) {
        WebInstantView webInstantView;
        if (webPhoto == null || (webInstantView = webPhoto.instantView) == null) {
            return;
        }
        webInstantView.loadPhotoInternal(webPhoto, imageReceiver, runnable);
    }

    private void loadPhotoInternal(final org.telegram.ui.web.WebInstantView.WebPhoto r6, org.telegram.messenger.ImageReceiver r7, java.lang.Runnable r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.web.WebInstantView.loadPhotoInternal(org.telegram.ui.web.WebInstantView$WebPhoto, org.telegram.messenger.ImageReceiver, java.lang.Runnable):void");
    }

    public static TLRPC.RichText parseRichText(String str) {
        TLRPC.TL_textPlain tL_textPlain = new TLRPC.TL_textPlain();
        tL_textPlain.text = str;
        return tL_textPlain;
    }

    public static void recycle(TLRPC.WebPage webPage) {
        WebInstantView webInstantView = (WebInstantView) instants.remove(webPage);
        if (webInstantView != null) {
            webInstantView.recycle();
        }
    }

    public static TLRPC.RichText trim(TLRPC.RichText richText) {
        TLRPC.TL_textPlain tL_textPlain;
        String str;
        if (richText == null) {
            return richText;
        }
        TLRPC.RichText richText2 = richText.text;
        if (richText2 == null) {
            if (richText.texts.size() != 1) {
                if (!richText.texts.isEmpty()) {
                    trimStart(richText.texts.get(0));
                    ArrayList<TLRPC.RichText> arrayList = richText.texts;
                    trimEnd(arrayList.get(arrayList.size() - 1));
                } else if ((richText instanceof TLRPC.TL_textPlain) && (str = (tL_textPlain = (TLRPC.TL_textPlain) richText).text) != null) {
                    tL_textPlain.text = str.trim();
                }
                return richText;
            }
            richText2 = richText.texts.get(0);
        }
        trim(richText2);
        return richText;
    }

    public static TLRPC.RichText trimEnd(TLRPC.RichText richText) {
        TLRPC.TL_textPlain tL_textPlain;
        String str;
        if (richText == null) {
            return richText;
        }
        TLRPC.RichText richText2 = richText.text;
        if (richText2 == null) {
            if (richText.texts.isEmpty()) {
                if ((richText instanceof TLRPC.TL_textPlain) && (str = (tL_textPlain = (TLRPC.TL_textPlain) richText).text) != null) {
                    tL_textPlain.text = str.replaceAll("\\s+$", "");
                }
                return richText;
            }
            richText2 = richText.texts.get(r0.size() - 1);
        }
        trimEnd(richText2);
        return richText;
    }

    public static TLRPC.RichText trimStart(TLRPC.RichText richText) {
        TLRPC.TL_textPlain tL_textPlain;
        String str;
        if (richText == null) {
            return richText;
        }
        TLRPC.RichText richText2 = richText.text;
        if (richText2 == null) {
            if (richText.texts.isEmpty()) {
                if ((richText instanceof TLRPC.TL_textPlain) && (str = (tL_textPlain = (TLRPC.TL_textPlain) richText).text) != null) {
                    tL_textPlain.text = str.replaceAll("^\\s+", "");
                }
                return richText;
            }
            richText2 = richText.texts.get(0);
        }
        trimStart(richText2);
        return richText;
    }

    public void getHTML(final WebView webView, boolean z, final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        if (webView == null) {
            callback.run(null);
        } else {
            if (z) {
                webView.evaluateJavascript("document.documentElement.outerHTML", new ValueCallback() {
                    @Override
                    public final void onReceiveValue(Object obj) {
                        WebInstantView.lambda$getHTML$5(Utilities.Callback.this, (String) obj);
                    }
                });
                return;
            }
            System.currentTimeMillis();
            final File file = new File(AndroidUtilities.getCacheDir(), "archive.mht");
            webView.evaluateJavascript(AndroidUtilities.readRes(R.raw.open_collapsed).replace("$OPEN$", "true"), new ValueCallback() {
                @Override
                public final void onReceiveValue(Object obj) {
                    WebInstantView.this.lambda$getHTML$8(webView, file, callback, (String) obj);
                }
            });
        }
    }

    public boolean isInline(JSONArray jSONArray) {
        List asList = Arrays.asList("b", "strong", "span", "img", "i", "s", "a", "code", "mark", "sub", "sup");
        for (int i = 0; i < jSONArray.length(); i++) {
            Object obj = jSONArray.get(i);
            if (!(obj instanceof String)) {
                if (obj instanceof JSONObject) {
                    JSONObject jSONObject = (JSONObject) obj;
                    String optString = jSONObject.optString("tag");
                    if (!asList.contains(optString)) {
                        if ("div".equalsIgnoreCase(optString) || "span".equalsIgnoreCase(optString)) {
                            isInline(jSONObject.optJSONArray("content"));
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    public TLRPC.TL_pageBlockDetails parseDetails(String str, JSONObject jSONObject, TLRPC.TL_page tL_page) {
        TLRPC.TL_pageBlockDetails tL_pageBlockDetails = new TLRPC.TL_pageBlockDetails();
        JSONArray optJSONArray = jSONObject.optJSONArray("content");
        if (optJSONArray == null) {
            return null;
        }
        int i = 0;
        while (true) {
            if (i >= optJSONArray.length()) {
                break;
            }
            Object obj = optJSONArray.get(i);
            if (obj instanceof JSONObject) {
                JSONObject jSONObject2 = (JSONObject) obj;
                if ("summary".equals(jSONObject2.optString("tag"))) {
                    tL_pageBlockDetails.title = trim(parseRichText(jSONObject2, tL_page));
                    optJSONArray.remove(i);
                    break;
                }
            }
            i++;
        }
        tL_pageBlockDetails.blocks.addAll(parsePageBlocks(str, optJSONArray, tL_page));
        tL_pageBlockDetails.open = jSONObject.has("open");
        return tL_pageBlockDetails;
    }

    public TLRPC.TL_pageBlockPhoto parseFigure(JSONObject jSONObject, TLRPC.TL_page tL_page) {
        JSONArray optJSONArray = jSONObject.optJSONArray("content");
        ArrayList arrayList = new ArrayList();
        WebPhoto webPhoto = null;
        int i = 0;
        TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = null;
        TLRPC.RichText richText = null;
        for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
            Object obj = optJSONArray.get(i2);
            if (obj instanceof JSONObject) {
                JSONObject jSONObject2 = (JSONObject) obj;
                String optString = jSONObject2.optString("tag");
                if ("figurecaption".equalsIgnoreCase(optString) || "caption".equalsIgnoreCase(optString)) {
                    richText = trim(parseRichText(jSONObject2, tL_page));
                } else if ("img".equalsIgnoreCase(optString)) {
                    tL_pageBlockPhoto = parseImage(jSONObject2, tL_page);
                } else if ("source".equalsIgnoreCase(optString)) {
                    String optString2 = jSONObject2.optString("src");
                    if (TextUtils.isEmpty(optString2)) {
                        String optString3 = jSONObject2.optString("srcset");
                        if (!TextUtils.isEmpty(optString3)) {
                            for (String str : optString3.split(",")) {
                                arrayList.add(str.trim().split(" ")[0].trim());
                            }
                        }
                    } else {
                        arrayList.add(optString2);
                    }
                }
            }
        }
        if (tL_pageBlockPhoto == null) {
            return null;
        }
        if (richText != null) {
            TLRPC.TL_pageCaption tL_pageCaption = new TLRPC.TL_pageCaption();
            tL_pageBlockPhoto.caption = tL_pageCaption;
            tL_pageCaption.text = richText;
            tL_pageCaption.credit = new TLRPC.TL_textEmpty();
        }
        while (true) {
            if (i >= tL_page.photos.size()) {
                break;
            }
            if ((tL_page.photos.get(i) instanceof WebPhoto) && tL_page.photos.get(i).id == tL_pageBlockPhoto.photo_id) {
                webPhoto = (WebPhoto) tL_page.photos.get(i);
                break;
            }
            i++;
        }
        if (webPhoto != null) {
            webPhoto.urls.addAll(arrayList);
        }
        return tL_pageBlockPhoto;
    }

    public TLRPC.TL_pageBlockPhoto parseImage(JSONObject jSONObject, TLRPC.TL_page tL_page) {
        TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = new TLRPC.TL_pageBlockPhoto();
        tL_pageBlockPhoto.caption = new TLRPC.TL_pageCaption();
        String optString = jSONObject.optString("alt");
        if (optString != null) {
            tL_pageBlockPhoto.caption.text = trim(parseRichText(optString));
            tL_pageBlockPhoto.caption.credit = trim(parseRichText(""));
        }
        String optString2 = jSONObject.optString("src");
        if (optString2 == null) {
            return null;
        }
        WebPhoto webPhoto = new WebPhoto();
        webPhoto.instantView = this;
        webPhoto.id = (-1) - tL_page.photos.size();
        webPhoto.url = optString2;
        webPhoto.urls.add(optString2);
        try {
            webPhoto.w = Integer.parseInt(jSONObject.optString("width"));
        } catch (Exception unused) {
        }
        try {
            webPhoto.h = Integer.parseInt(jSONObject.optString("height"));
        } catch (Exception unused2) {
        }
        if (webPhoto.w == 0) {
            webPhoto.w = webPhoto.h;
        }
        if (webPhoto.h == 0) {
            webPhoto.h = webPhoto.w;
        }
        tL_pageBlockPhoto.photo_id = webPhoto.id;
        tL_pageBlockPhoto.url = optString2;
        tL_page.photos.add(webPhoto);
        return tL_pageBlockPhoto;
    }

    public TLRPC.TL_textImage parseInlineImage(JSONObject jSONObject, TLRPC.TL_page tL_page) {
        TLRPC.TL_textImage tL_textImage = new TLRPC.TL_textImage();
        String optString = jSONObject.optString("src");
        if (optString == null) {
            return null;
        }
        WebPhoto webPhoto = new WebPhoto();
        webPhoto.instantView = this;
        webPhoto.id = (-1) - tL_page.photos.size();
        webPhoto.url = optString;
        webPhoto.urls.add(optString);
        try {
            webPhoto.w = Integer.parseInt(jSONObject.optString("width"));
        } catch (Exception unused) {
        }
        try {
            webPhoto.h = Integer.parseInt(jSONObject.optString("height"));
        } catch (Exception unused2) {
        }
        tL_textImage.url = optString;
        tL_page.photos.add(webPhoto);
        if (webPhoto.w == 0) {
            webPhoto.w = webPhoto.h;
        }
        if (webPhoto.h == 0) {
            webPhoto.h = webPhoto.w;
        }
        try {
            tL_textImage.w = Integer.parseInt(jSONObject.optString("width"));
        } catch (Exception unused3) {
        }
        try {
            tL_textImage.h = Integer.parseInt(jSONObject.optString("height"));
        } catch (Exception unused4) {
        }
        if (tL_textImage.w == 0) {
            tL_textImage.w = tL_textImage.h;
        }
        if (tL_textImage.h == 0) {
            tL_textImage.h = tL_textImage.w;
        }
        tL_textImage.photo_id = webPhoto.id;
        return tL_textImage;
    }

    public TLRPC.TL_webPage parseJSON(String str, JSONObject jSONObject) {
        TLRPC.TL_webPage tL_webPage = new TLRPC.TL_webPage();
        tL_webPage.id = 0L;
        tL_webPage.url = str;
        tL_webPage.display_url = str;
        String string = jSONObject.getString("siteName");
        if (string != null && !"null".equals(string)) {
            tL_webPage.flags |= 2;
            tL_webPage.site_name = string;
        }
        String optString = jSONObject.optString("title");
        if (optString != null && !"null".equals(optString)) {
            tL_webPage.flags |= 4;
            tL_webPage.title = optString;
        }
        String optString2 = jSONObject.optString("byline");
        if (optString2 != null && !"null".equals(optString2) && !"by".equalsIgnoreCase(optString2)) {
            tL_webPage.flags |= 256;
            tL_webPage.author = optString2;
        }
        String optString3 = jSONObject.optString("excerpt");
        if (optString3 != null && !"null".equals(optString3)) {
            tL_webPage.flags |= 8;
            tL_webPage.description = optString3;
        }
        JSONArray optJSONArray = jSONObject.optJSONArray("content");
        if (optJSONArray != null && !"null".equals(optJSONArray)) {
            tL_webPage.flags |= 1024;
            tL_webPage.cached_page = parsePage(str, jSONObject);
        }
        return tL_webPage;
    }

    public TLRPC.PageBlock parseList(String str, JSONObject jSONObject, TLRPC.TL_page tL_page) {
        TLRPC.TL_pageListItemBlocks tL_pageListItemBlocks;
        TLRPC.TL_pageListOrderedItemBlocks tL_pageListOrderedItemBlocks;
        int i = 0;
        if ("ol".equals(jSONObject.optString("tag"))) {
            TLRPC.TL_pageBlockOrderedList tL_pageBlockOrderedList = new TLRPC.TL_pageBlockOrderedList();
            JSONArray jSONArray = jSONObject.getJSONArray("content");
            while (i < jSONArray.length()) {
                Object obj = jSONArray.get(i);
                if (obj instanceof JSONObject) {
                    JSONObject jSONObject2 = (JSONObject) obj;
                    if ("li".equals(jSONObject2.optString("tag"))) {
                        JSONArray optJSONArray = jSONObject2.optJSONArray("content");
                        if (isInline(optJSONArray)) {
                            TLRPC.TL_pageListOrderedItemText tL_pageListOrderedItemText = new TLRPC.TL_pageListOrderedItemText();
                            tL_pageListOrderedItemText.text = parseRichText(optJSONArray, tL_page);
                            tL_pageListOrderedItemBlocks = tL_pageListOrderedItemText;
                        } else {
                            TLRPC.TL_pageListOrderedItemBlocks tL_pageListOrderedItemBlocks2 = new TLRPC.TL_pageListOrderedItemBlocks();
                            tL_pageListOrderedItemBlocks2.blocks.addAll(parsePageBlocks(str, optJSONArray, tL_page));
                            tL_pageListOrderedItemBlocks = tL_pageListOrderedItemBlocks2;
                        }
                        tL_pageBlockOrderedList.items.add(tL_pageListOrderedItemBlocks);
                    }
                }
                i++;
            }
            return tL_pageBlockOrderedList;
        }
        TLRPC.TL_pageBlockList tL_pageBlockList = new TLRPC.TL_pageBlockList();
        JSONArray jSONArray2 = jSONObject.getJSONArray("content");
        while (i < jSONArray2.length()) {
            Object obj2 = jSONArray2.get(i);
            if (obj2 instanceof JSONObject) {
                JSONObject jSONObject3 = (JSONObject) obj2;
                if ("li".equals(jSONObject3.optString("tag"))) {
                    JSONArray optJSONArray2 = jSONObject3.optJSONArray("content");
                    if (isInline(optJSONArray2)) {
                        TLRPC.TL_pageListItemText tL_pageListItemText = new TLRPC.TL_pageListItemText();
                        tL_pageListItemText.text = parseRichText(optJSONArray2, tL_page);
                        tL_pageListItemBlocks = tL_pageListItemText;
                    } else {
                        TLRPC.TL_pageListItemBlocks tL_pageListItemBlocks2 = new TLRPC.TL_pageListItemBlocks();
                        tL_pageListItemBlocks2.blocks.addAll(parsePageBlocks(str, optJSONArray2, tL_page));
                        tL_pageListItemBlocks = tL_pageListItemBlocks2;
                    }
                    tL_pageBlockList.items.add(tL_pageListItemBlocks);
                }
            }
            i++;
        }
        return tL_pageBlockList;
    }

    public TLRPC.TL_page parsePage(String str, JSONObject jSONObject) {
        String optString = jSONObject.optString("title");
        if ("null".equals(optString)) {
            optString = null;
        }
        "null".equals(jSONObject.optString("publishedTime"));
        JSONArray optJSONArray = jSONObject.optJSONArray("content");
        TLRPC.TL_page tL_page = new TLRPC.TL_page();
        tL_page.web = true;
        tL_page.url = str;
        tL_page.blocks.addAll(parsePageBlocks(str, optJSONArray, tL_page));
        if (tL_page.blocks.isEmpty() || !(tL_page.blocks.get(0) instanceof TLRPC.TL_pageBlockHeader)) {
            TLRPC.TL_pageBlockTitle tL_pageBlockTitle = new TLRPC.TL_pageBlockTitle();
            tL_pageBlockTitle.text = trim(parseRichText(optString));
            tL_page.blocks.add(0, tL_pageBlockTitle);
        }
        return tL_page;
    }

    public ArrayList parsePageBlocks(String str, JSONArray jSONArray, TLRPC.TL_page tL_page) {
        TLRPC.RichText trim;
        TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto;
        TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph;
        TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph2;
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            Object obj = jSONArray.get(i);
            if (obj instanceof String) {
                TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph3 = new TLRPC.TL_pageBlockParagraph();
                trim = parseRichText((String) obj);
                tL_pageBlockParagraph2 = tL_pageBlockParagraph3;
            } else {
                if (obj instanceof JSONObject) {
                    JSONObject jSONObject = (JSONObject) obj;
                    String optString = jSONObject.optString("tag");
                    JSONArray optJSONArray = jSONObject.optJSONArray("content");
                    optString.hashCode();
                    char c = 65535;
                    switch (optString.hashCode()) {
                        case -1274639644:
                            if (optString.equals("figure")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -891980137:
                            if (optString.equals("strong")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -577741570:
                            if (optString.equals("picture")) {
                                c = 2;
                                break;
                            }
                            break;
                        case 97:
                            if (optString.equals("a")) {
                                c = 3;
                                break;
                            }
                            break;
                        case 98:
                            if (optString.equals("b")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 105:
                            if (optString.equals("i")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 112:
                            if (optString.equals("p")) {
                                c = 6;
                                break;
                            }
                            break;
                        case 115:
                            if (optString.equals("s")) {
                                c = 7;
                                break;
                            }
                            break;
                        case 3273:
                            if (optString.equals("h1")) {
                                c = '\b';
                                break;
                            }
                            break;
                        case 3274:
                            if (optString.equals("h2")) {
                                c = '\t';
                                break;
                            }
                            break;
                        case 3275:
                            if (optString.equals("h3")) {
                                c = '\n';
                                break;
                            }
                            break;
                        case 3276:
                            if (optString.equals("h4")) {
                                c = 11;
                                break;
                            }
                            break;
                        case 3277:
                            if (optString.equals("h5")) {
                                c = '\f';
                                break;
                            }
                            break;
                        case 3278:
                            if (optString.equals("h6")) {
                                c = '\r';
                                break;
                            }
                            break;
                        case 3338:
                            if (optString.equals("hr")) {
                                c = 14;
                                break;
                            }
                            break;
                        case 3549:
                            if (optString.equals("ol")) {
                                c = 15;
                                break;
                            }
                            break;
                        case 3735:
                            if (optString.equals("ul")) {
                                c = 16;
                                break;
                            }
                            break;
                        case 104387:
                            if (optString.equals("img")) {
                                c = 17;
                                break;
                            }
                            break;
                        case 111267:
                            if (optString.equals("pre")) {
                                c = 18;
                                break;
                            }
                            break;
                        case 114240:
                            if (optString.equals("sub")) {
                                c = 19;
                                break;
                            }
                            break;
                        case 114254:
                            if (optString.equals("sup")) {
                                c = 20;
                                break;
                            }
                            break;
                        case 3059181:
                            if (optString.equals("code")) {
                                c = 21;
                                break;
                            }
                            break;
                        case 3344077:
                            if (optString.equals("mark")) {
                                c = 22;
                                break;
                            }
                            break;
                        case 3536714:
                            if (optString.equals("span")) {
                                c = 23;
                                break;
                            }
                            break;
                        case 110115790:
                            if (optString.equals("table")) {
                                c = 24;
                                break;
                            }
                            break;
                        case 1303202319:
                            if (optString.equals("blockquote")) {
                                c = 25;
                                break;
                            }
                            break;
                        case 1557721666:
                            if (optString.equals("details")) {
                                c = 26;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                        case 2:
                            TLRPC.TL_pageBlockPhoto parseFigure = parseFigure(jSONObject, tL_page);
                            tL_pageBlockPhoto = parseFigure;
                            if (parseFigure == null) {
                                break;
                            }
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        case 1:
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                            JSONArray jSONArray2 = new JSONArray();
                            jSONArray2.put(jSONObject);
                            ?? tL_pageBlockParagraph4 = new TLRPC.TL_pageBlockParagraph();
                            tL_pageBlockParagraph4.text = parseRichText(jSONArray2, tL_page);
                            tL_pageBlockPhoto = tL_pageBlockParagraph4;
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        case 6:
                            TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph5 = new TLRPC.TL_pageBlockParagraph();
                            trim = trim(parseRichText(jSONObject, tL_page));
                            tL_pageBlockParagraph2 = tL_pageBlockParagraph5;
                            break;
                        case '\b':
                        case '\t':
                            ?? tL_pageBlockHeader = new TLRPC.TL_pageBlockHeader();
                            tL_pageBlockHeader.text = trim(parseRichText(jSONObject, tL_page));
                            tL_pageBlockParagraph = tL_pageBlockHeader;
                            arrayList.add(tL_pageBlockParagraph);
                            break;
                        case '\n':
                        case 11:
                        case '\f':
                        case '\r':
                            ?? tL_pageBlockSubheader = new TLRPC.TL_pageBlockSubheader();
                            tL_pageBlockSubheader.text = trim(parseRichText(jSONObject, tL_page));
                            tL_pageBlockParagraph = tL_pageBlockSubheader;
                            arrayList.add(tL_pageBlockParagraph);
                            break;
                        case 14:
                            tL_pageBlockPhoto = new TLRPC.TL_pageBlockDivider();
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        case 15:
                        case 16:
                            tL_pageBlockPhoto = parseList(str, jSONObject, tL_page);
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        case 17:
                            TLRPC.TL_pageBlockPhoto parseImage = parseImage(jSONObject, tL_page);
                            tL_pageBlockPhoto = parseImage;
                            if (parseImage == null) {
                                break;
                            }
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        case 18:
                            ?? tL_pageBlockPreformatted = new TLRPC.TL_pageBlockPreformatted();
                            TLRPC.TL_textFixed tL_textFixed = new TLRPC.TL_textFixed();
                            tL_textFixed.text = trim(parseRichText(jSONObject, tL_page));
                            tL_pageBlockPreformatted.text = tL_textFixed;
                            tL_pageBlockPreformatted.language = "";
                            tL_pageBlockParagraph = tL_pageBlockPreformatted;
                            arrayList.add(tL_pageBlockParagraph);
                            break;
                        case 24:
                            tL_pageBlockPhoto = parseTable(str, jSONObject, tL_page);
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        case 25:
                            ?? tL_pageBlockBlockquote = new TLRPC.TL_pageBlockBlockquote();
                            tL_pageBlockBlockquote.text = trim(parseRichText(jSONObject, tL_page));
                            TLRPC.TL_textItalic tL_textItalic = new TLRPC.TL_textItalic();
                            tL_textItalic.text = tL_pageBlockBlockquote.text;
                            tL_pageBlockBlockquote.text = tL_textItalic;
                            tL_pageBlockParagraph = tL_pageBlockBlockquote;
                            arrayList.add(tL_pageBlockParagraph);
                            break;
                        case 26:
                            TLRPC.TL_pageBlockDetails parseDetails = parseDetails(str, jSONObject, tL_page);
                            tL_pageBlockPhoto = parseDetails;
                            if (parseDetails == null) {
                                break;
                            }
                            arrayList.add(tL_pageBlockPhoto);
                            break;
                        default:
                            if (optJSONArray != null) {
                                arrayList.addAll(parsePageBlocks(str, optJSONArray, tL_page));
                                break;
                            } else {
                                break;
                            }
                    }
                }
            }
            tL_pageBlockParagraph2.text = trim;
            tL_pageBlockParagraph = tL_pageBlockParagraph2;
            arrayList.add(tL_pageBlockParagraph);
        }
        return arrayList;
    }

    public org.telegram.tgnet.TLRPC.RichText parseRichText(org.json.JSONArray r11, org.telegram.tgnet.TLRPC.TL_page r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.web.WebInstantView.parseRichText(org.json.JSONArray, org.telegram.tgnet.TLRPC$TL_page):org.telegram.tgnet.TLRPC$RichText");
    }

    public TLRPC.RichText parseRichText(JSONObject jSONObject, TLRPC.TL_page tL_page) {
        TLRPC.RichText applyAnchor = applyAnchor(parseRichText(jSONObject.getJSONArray("content"), tL_page), jSONObject);
        if (jSONObject.has("bold")) {
            TLRPC.TL_textBold tL_textBold = new TLRPC.TL_textBold();
            tL_textBold.text = applyAnchor;
            applyAnchor = tL_textBold;
        }
        if (!jSONObject.has("italic")) {
            return applyAnchor;
        }
        TLRPC.TL_textItalic tL_textItalic = new TLRPC.TL_textItalic();
        tL_textItalic.text = applyAnchor;
        return tL_textItalic;
    }

    public TLRPC.TL_pageBlockTable parseTable(String str, JSONObject jSONObject, TLRPC.TL_page tL_page) {
        TLRPC.TL_pageBlockTable tL_pageBlockTable = new TLRPC.TL_pageBlockTable();
        tL_pageBlockTable.bordered = true;
        tL_pageBlockTable.striped = true;
        String optString = jSONObject.optString("title");
        if (optString == null) {
            optString = "";
        }
        tL_pageBlockTable.title = trim(applyAnchor(parseRichText(optString), jSONObject));
        tL_pageBlockTable.rows.addAll(parseTableRows(str, jSONObject.getJSONArray("content"), tL_page));
        return tL_pageBlockTable;
    }

    public TLRPC.TL_pageTableRow parseTableRow(String str, JSONObject jSONObject, TLRPC.TL_page tL_page) {
        JSONObject jSONObject2;
        String optString;
        TLRPC.TL_pageTableRow tL_pageTableRow = new TLRPC.TL_pageTableRow();
        JSONArray jSONArray = jSONObject.getJSONArray("content");
        for (int i = 0; i < jSONArray.length(); i++) {
            Object obj = jSONArray.get(i);
            if ((obj instanceof JSONObject) && (optString = (jSONObject2 = (JSONObject) obj).optString("tag")) != null && ("td".equals(optString) || "th".equals(optString))) {
                TLRPC.TL_pageTableCell tL_pageTableCell = new TLRPC.TL_pageTableCell();
                tL_pageTableCell.header = "th".equals(optString);
                try {
                    tL_pageTableCell.colspan = Integer.parseInt(jSONObject2.optString("colspan"));
                    tL_pageTableCell.flags |= 2;
                } catch (Exception unused) {
                }
                try {
                    tL_pageTableCell.rowspan = Integer.parseInt(jSONObject2.optString("rowspan"));
                    tL_pageTableCell.flags |= 4;
                } catch (Exception unused2) {
                }
                tL_pageTableCell.text = trim(parseRichText(jSONObject2.getJSONArray("content"), tL_page));
                if (jSONObject2.has("bold") || tL_pageTableCell.header) {
                    TLRPC.TL_textBold tL_textBold = new TLRPC.TL_textBold();
                    tL_textBold.text = tL_pageTableCell.text;
                    tL_pageTableCell.text = tL_textBold;
                }
                if (jSONObject2.has("italic")) {
                    TLRPC.TL_textItalic tL_textItalic = new TLRPC.TL_textItalic();
                    tL_textItalic.text = tL_pageTableCell.text;
                    tL_pageTableCell.text = tL_textItalic;
                }
                tL_pageTableCell.align_center = jSONObject2.has("xcenter");
                tL_pageTableRow.cells.add(tL_pageTableCell);
            }
        }
        return tL_pageTableRow;
    }

    public ArrayList parseTableRows(String str, JSONArray jSONArray, TLRPC.TL_page tL_page) {
        ArrayList arrayList = new ArrayList();
        new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            Object obj = jSONArray.get(i);
            if (obj instanceof JSONObject) {
                JSONObject jSONObject = (JSONObject) obj;
                if ("tr".equals(jSONObject.optString("tag"))) {
                    arrayList.add(parseTableRow(str, jSONObject, tL_page));
                } else {
                    JSONArray optJSONArray = jSONObject.optJSONArray("content");
                    if (optJSONArray != null) {
                        arrayList.addAll(parseTableRows(str, optJSONArray, tL_page));
                    }
                }
            }
        }
        return arrayList;
    }

    public void readHTML(String str, final InputStream inputStream, Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        if (inputStream == null) {
            callback.run(null);
            return;
        }
        Context context = LaunchActivity.instance;
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity == null) {
            callback.run(null);
            return;
        }
        View rootView = findActivity.findViewById(16908290).getRootView();
        if (!(rootView instanceof ViewGroup)) {
            callback.run(null);
            return;
        }
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                return false;
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                return false;
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(500.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(500.0f), 1073741824));
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }
        };
        ((ViewGroup) rootView).addView(frameLayout);
        WebView webView = new WebView(context);
        WebSettings settings = webView.getSettings();
        settings.setAllowContentAccess(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setGeolocationEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        webView.setWebViewClient(new WebViewClient() {
            private boolean firstLoad = true;
            private boolean streamLoaded;

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView2, String str2) {
                InputStream inputStream2;
                String str3;
                if (this.firstLoad) {
                    this.firstLoad = false;
                    return new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(("<script>\n" + AndroidUtilities.readRes(R.raw.instant).replace("$DEBUG$", "" + BuildVars.DEBUG_VERSION) + "\n</script>").getBytes(StandardCharsets.UTF_8)));
                }
                if (str2 == null || !str2.endsWith("/index.html")) {
                    MHTML mhtml = WebInstantView.this.mhtml;
                    MHTML.Entry entry = mhtml != null ? (MHTML.Entry) mhtml.entriesByLocation.get(str2) : null;
                    if (entry == null) {
                        return Build.VERSION.SDK_INT < 21 ? new WebResourceResponse("text/html", "UTF-8", null) : new WebResourceResponse("text/plain", "utf-8", 404, "Not Found", null, null);
                    }
                    String type = entry.getType();
                    if (!"text/html".equalsIgnoreCase(type) && !"text/css".equalsIgnoreCase(type)) {
                        return Build.VERSION.SDK_INT < 21 ? new WebResourceResponse(type, "UTF-8", null) : new WebResourceResponse("text/plain", "utf-8", 404, "Not Found", null, null);
                    }
                    try {
                        inputStream2 = entry.getInputStream();
                        str3 = type;
                    } catch (IOException e) {
                        FileLog.e(e);
                        return Build.VERSION.SDK_INT < 21 ? new WebResourceResponse(type, "UTF-8", null) : new WebResourceResponse("text/plain", "utf-8", 503, "Server error", null, null);
                    }
                } else {
                    str3 = "application/octet-stream";
                    if (this.streamLoaded) {
                        MHTML mhtml2 = WebInstantView.this.mhtml;
                        MHTML.Entry entry2 = mhtml2 != null ? (MHTML.Entry) mhtml2.entries.get(0) : null;
                        if (entry2 == null) {
                            return Build.VERSION.SDK_INT < 21 ? new WebResourceResponse("application/octet-stream", "UTF-8", null) : new WebResourceResponse("text/plain", "utf-8", 404, "Not Found", null, null);
                        }
                        try {
                            inputStream2 = entry2.getInputStream();
                        } catch (IOException e2) {
                            FileLog.e(e2);
                            return Build.VERSION.SDK_INT < 21 ? new WebResourceResponse("application/octet-stream", "UTF-8", null) : new WebResourceResponse("text/plain", "utf-8", 503, "Server error", null, null);
                        }
                    } else {
                        inputStream2 = inputStream;
                        this.streamLoaded = true;
                    }
                }
                return new WebResourceResponse(str3, null, inputStream2);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
        });
        frameLayout.addView(webView, LayoutHelper.createFrame(-1, -1.0f));
        webView.addJavascriptInterface(new AnonymousClass4(new boolean[]{false}, webView, frameLayout, callback), "Instant");
        webView.loadUrl(str);
    }

    public void recycle() {
        TLRPC.Page page;
        ArrayList<TLRPC.Photo> arrayList;
        instants.remove(this.webpage);
        Iterator it = this.loadedPhotos.entrySet().iterator();
        while (it.hasNext()) {
            AndroidUtilities.recycleBitmap((Bitmap) ((Map.Entry) it.next()).getValue());
        }
        this.loadedPhotos.clear();
        TLRPC.WebPage webPage = this.webpage;
        if (webPage == null || (page = webPage.cached_page) == null || (arrayList = page.photos) == null) {
            return;
        }
        Iterator<TLRPC.Photo> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            TLRPC.Photo next = it2.next();
            if (next instanceof WebPhoto) {
                WebPhoto webPhoto = (WebPhoto) next;
                HashMap hashMap = loadingPhotos;
                if (hashMap != null) {
                    hashMap.remove(webPhoto.url);
                }
            }
        }
    }
}
