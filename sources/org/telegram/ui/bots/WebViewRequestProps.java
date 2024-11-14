package org.telegram.ui.bots;

import android.net.Uri;
import android.text.TextUtils;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class WebViewRequestProps {
    public boolean allowWrite;
    public TLRPC.BotApp app;
    public long botId;
    public TLRPC.User botUser;
    public String buttonText;
    public String buttonUrl;
    public boolean compact;
    public int currentAccount;
    public int flags;
    public boolean fullscreen;
    public long peerId;
    public int replyToMsgId;
    public TLObject response;
    public long responseTime;
    public boolean silent;
    public String startParam;
    public int type;

    public static WebViewRequestProps of(int i, long j, long j2, String str, String str2, int i2, int i3, boolean z, TLRPC.BotApp botApp, boolean z2, String str3, TLRPC.User user, int i4, boolean z3, boolean z4) {
        WebViewRequestProps webViewRequestProps = new WebViewRequestProps();
        webViewRequestProps.currentAccount = i;
        webViewRequestProps.peerId = j;
        webViewRequestProps.botId = j2;
        webViewRequestProps.buttonText = str;
        webViewRequestProps.buttonUrl = str2;
        webViewRequestProps.type = i2;
        webViewRequestProps.replyToMsgId = i3;
        webViewRequestProps.silent = z;
        webViewRequestProps.app = botApp;
        webViewRequestProps.allowWrite = z2;
        webViewRequestProps.startParam = str3;
        webViewRequestProps.botUser = user;
        webViewRequestProps.flags = i4;
        webViewRequestProps.compact = z3;
        webViewRequestProps.fullscreen = z4;
        if (!z3 && !z4 && !TextUtils.isEmpty(str2)) {
            try {
                Uri parse = Uri.parse(str2);
                webViewRequestProps.compact = TextUtils.equals(parse.getQueryParameter("mode"), "compact");
                webViewRequestProps.fullscreen = TextUtils.equals(parse.getQueryParameter("mode"), "fullscreen");
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return webViewRequestProps;
    }

    public void applyResponse(TLObject tLObject) {
        this.response = tLObject;
        this.responseTime = System.currentTimeMillis();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WebViewRequestProps)) {
            return false;
        }
        WebViewRequestProps webViewRequestProps = (WebViewRequestProps) obj;
        if (this.currentAccount != webViewRequestProps.currentAccount || this.peerId != webViewRequestProps.peerId || this.botId != webViewRequestProps.botId || !TextUtils.equals(this.buttonUrl, webViewRequestProps.buttonUrl) || this.type != webViewRequestProps.type || this.replyToMsgId != webViewRequestProps.replyToMsgId || this.silent != webViewRequestProps.silent) {
            return false;
        }
        TLRPC.BotApp botApp = this.app;
        long j = botApp == null ? 0L : botApp.id;
        TLRPC.BotApp botApp2 = webViewRequestProps.app;
        if (j != (botApp2 == null ? 0L : botApp2.id) || this.allowWrite != webViewRequestProps.allowWrite || !TextUtils.equals(this.startParam, webViewRequestProps.startParam)) {
            return false;
        }
        TLRPC.User user = this.botUser;
        long j2 = user == null ? 0L : user.id;
        TLRPC.User user2 = webViewRequestProps.botUser;
        return j2 == (user2 != null ? user2.id : 0L) && this.flags == webViewRequestProps.flags;
    }
}
