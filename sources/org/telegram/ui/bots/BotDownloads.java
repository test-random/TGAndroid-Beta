package org.telegram.ui.bots;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.bots.BotDownloads;

public class BotDownloads {
    public final long botId;
    public final Context context;
    public final int currentAccount;
    private FileDownload currentFile;
    public final DownloadManager downloadManager;
    private final ArrayList files = new ArrayList();
    private static final HashMap instances = new HashMap();
    private static HashMap cachedMimeAndSizes = new HashMap();

    public static class DownloadBulletin extends Bulletin.ButtonLayout {
        public final BackgroundDrawable background;
        private int currentButtonType;
        private FileDownload file;
        private final ImageView imageView;
        private final Theme.ResourcesProvider resourcesProvider;
        public final StatusDrawable status;
        private final TextView subtitleView;
        private final LinearLayout textLayout;
        private final TextView titleView;

        public static class BackgroundDrawable extends Drawable {
            private boolean arrow;
            private int arrowMargin;
            private final AnimatedFloat arrowProgress;
            private final AnimatedFloat arrowX;
            private final Path path;
            private final int r;
            private final Paint paint = new Paint(1);
            private final RectF rect = new RectF();

            public BackgroundDrawable(int i) {
                Path path = new Path();
                this.path = path;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        BotDownloads.DownloadBulletin.BackgroundDrawable.this.invalidateSelf();
                    }
                };
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                this.arrowProgress = new AnimatedFloat(runnable, 0L, 320L, cubicBezierInterpolator);
                this.arrowX = new AnimatedFloat(new Runnable() {
                    @Override
                    public final void run() {
                        BotDownloads.DownloadBulletin.BackgroundDrawable.this.invalidateSelf();
                    }
                }, 0L, 320L, cubicBezierInterpolator);
                this.r = i;
                path.moveTo(-AndroidUtilities.dp(6.5f), 0.0f);
                path.lineTo(AndroidUtilities.dp(6.5f), 0.0f);
                path.lineTo(0.0f, -AndroidUtilities.dp(6.16f));
                path.close();
            }

            @Override
            public void draw(Canvas canvas) {
                this.rect.set(getBounds());
                this.rect.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                RectF rectF = this.rect;
                float f = this.r;
                canvas.drawRoundRect(rectF, f, f, this.paint);
                float f2 = this.arrowProgress.set(this.arrow);
                float dp = (this.rect.right + AndroidUtilities.dp(8.0f)) - this.arrowX.set(this.arrowMargin);
                if (f2 > 0.0f) {
                    canvas.save();
                    canvas.translate(dp, AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(6.16f) * (1.0f - f2)));
                    canvas.drawPath(this.path, this.paint);
                    canvas.restore();
                }
            }

            @Override
            public int getOpacity() {
                return -2;
            }

            @Override
            public void setAlpha(int i) {
            }

            public void setArrow(int i) {
                boolean z = i >= 0;
                this.arrow = z;
                if (z) {
                    this.arrowMargin = i;
                }
                invalidateSelf();
            }

            public BackgroundDrawable setColor(int i) {
                this.paint.setColor(i);
                return this;
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }
        }

        public static class StatusDrawable extends Drawable {
            private AnimatedFloat animatedDone;
            private AnimatedFloat animatedHasPercent;
            private AnimatedFloat animatedProgress;
            private boolean cancelled;
            private final Drawable doc;
            private boolean done;
            private RLottieDrawable doneDrawable;
            private boolean hasPercent;
            private float progress;
            private final RectF rect;
            private final long start;
            private final Paint strokePaint;
            private final View view;

            public StatusDrawable(Context context, View view) {
                Paint paint = new Paint(1);
                this.strokePaint = paint;
                this.rect = new RectF();
                this.done = false;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        BotDownloads.DownloadBulletin.StatusDrawable.this.invalidateSelf();
                    }
                };
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                this.animatedHasPercent = new AnimatedFloat(runnable, 0L, 320L, cubicBezierInterpolator);
                this.animatedProgress = new AnimatedFloat(new Runnable() {
                    @Override
                    public final void run() {
                        BotDownloads.DownloadBulletin.StatusDrawable.this.invalidateSelf();
                    }
                }, 0L, 320L, cubicBezierInterpolator);
                this.animatedDone = new AnimatedFloat(new Runnable() {
                    @Override
                    public final void run() {
                        BotDownloads.DownloadBulletin.StatusDrawable.this.invalidateSelf();
                    }
                }, 0L, 320L, cubicBezierInterpolator);
                this.view = view;
                this.start = System.currentTimeMillis();
                this.doc = context.getResources().getDrawable(R.drawable.search_files_filled).mutate();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
            }

            @Override
            public void draw(Canvas canvas) {
                Rect bounds = getBounds();
                int centerX = bounds.centerX();
                int centerY = bounds.centerY();
                float f = this.animatedDone.set(this.done);
                if (f < 1.0f) {
                    float f2 = 1.0f - f;
                    float f3 = (f2 * 0.4f) + 0.6f;
                    canvas.save();
                    float f4 = centerX;
                    float f5 = centerY;
                    canvas.scale(f3, f3, f4, f5);
                    Drawable drawable = this.doc;
                    drawable.setBounds(centerX - (drawable.getIntrinsicWidth() / 2), centerY - (this.doc.getIntrinsicHeight() / 2), (this.doc.getIntrinsicWidth() / 2) + centerX, (this.doc.getIntrinsicHeight() / 2) + centerY);
                    this.doc.setAlpha((int) (f2 * 255.0f));
                    this.doc.draw(canvas);
                    float dp = AndroidUtilities.dp(14.0f);
                    this.strokePaint.setColor(Theme.multAlpha(-1, 0.2f * f2));
                    canvas.drawCircle(f4, f5, dp, this.strokePaint);
                    float f6 = f2 * 1.0f;
                    this.strokePaint.setColor(Theme.multAlpha(-1, f6));
                    this.rect.set(f4 - dp, f5 - dp, f4 + dp, f5 + dp);
                    float f7 = this.animatedHasPercent.set(this.hasPercent);
                    this.strokePaint.setColor(Theme.multAlpha(-1, f2 * 0.15f * (1.0f - f7)));
                    canvas.drawArc(this.rect, (-(((((float) ((System.currentTimeMillis() - this.start) % 600)) / 600.0f) - 1.0f) * 360.0f)) - 90.0f, -90.0f, false, this.strokePaint);
                    float currentTimeMillis = (((float) (System.currentTimeMillis() - this.start)) * 0.45f) % 5400.0f;
                    float max = Math.max(0.0f, ((1520.0f * currentTimeMillis) / 5400.0f) - 20.0f);
                    for (int i = 0; i < 4; i++) {
                        FastOutSlowInInterpolator fastOutSlowInInterpolator = CircularProgressDrawable.interpolator;
                        fastOutSlowInInterpolator.getInterpolation((currentTimeMillis - (i * 1350)) / 667.0f);
                        max += fastOutSlowInInterpolator.getInterpolation((currentTimeMillis - (r5 + 667)) / 667.0f) * 250.0f;
                    }
                    this.strokePaint.setColor(Theme.multAlpha(-1, f6));
                    canvas.drawArc(this.rect, (-90.0f) - max, Math.max(0.02f, this.animatedProgress.set(this.progress)) * (-360.0f) * f7, false, this.strokePaint);
                    invalidateSelf();
                    canvas.restore();
                }
                if (f > 0.0f) {
                    float f8 = (f * 0.4f) + 0.6f;
                    if (this.cancelled) {
                        canvas.save();
                        canvas.scale(f8, f8, centerX, centerY);
                    }
                    RLottieDrawable rLottieDrawable = this.doneDrawable;
                    if (rLottieDrawable != null) {
                        rLottieDrawable.setBounds(centerX - (rLottieDrawable.getIntrinsicWidth() / 2), centerY - (this.doneDrawable.getIntrinsicHeight() / 2), centerX + (this.doneDrawable.getIntrinsicWidth() / 2), centerY + (this.doneDrawable.getIntrinsicHeight() / 2));
                        this.doneDrawable.setAlpha((int) (f * 255.0f));
                        this.doneDrawable.draw(canvas);
                    }
                    if (this.cancelled) {
                        canvas.restore();
                    }
                }
            }

            @Override
            public int getIntrinsicHeight() {
                return AndroidUtilities.dp(40.0f);
            }

            @Override
            public int getIntrinsicWidth() {
                return AndroidUtilities.dp(40.0f);
            }

            @Override
            public int getOpacity() {
                return -2;
            }

            public void reset() {
                AnimatedFloat animatedFloat = this.animatedDone;
                this.done = false;
                animatedFloat.set(false, true);
                this.cancelled = false;
                RLottieDrawable rLottieDrawable = this.doneDrawable;
                if (rLottieDrawable != null) {
                    rLottieDrawable.recycle(true);
                    this.doneDrawable = null;
                }
                AnimatedFloat animatedFloat2 = this.animatedHasPercent;
                this.hasPercent = false;
                animatedFloat2.set(false, true);
            }

            @Override
            public void setAlpha(int i) {
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }

            public void setDone(boolean z) {
                if (this.done) {
                    return;
                }
                this.done = true;
                this.cancelled = z;
                RLottieDrawable rLottieDrawable = new RLottieDrawable(z ? R.raw.error : R.raw.contact_check, z ? "error" : "contact_check", AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                this.doneDrawable = rLottieDrawable;
                rLottieDrawable.setMasterParent(this.view);
                this.doneDrawable.setAllowDecodeSingleFrame(true);
                this.doneDrawable.start();
                if (z) {
                    return;
                }
                this.progress = 1.0f;
            }

            public void setProgress(Pair pair) {
                boolean z = pair != null && ((Long) pair.second).longValue() > 0;
                this.hasPercent = z;
                if (z) {
                    this.progress = Utilities.clamp(((float) ((Long) pair.first).longValue()) / ((float) ((Long) pair.second).longValue()), 1.0f, 0.0f);
                }
                invalidateSelf();
            }
        }

        public DownloadBulletin(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.currentButtonType = 0;
            this.resourcesProvider = resourcesProvider;
            BackgroundDrawable color = new BackgroundDrawable(AndroidUtilities.dp(10.0f)).setColor(Theme.getColor(Theme.key_undo_background, resourcesProvider));
            this.background = color;
            setBackground(color);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            StatusDrawable statusDrawable = new StatusDrawable(context, imageView);
            this.status = statusDrawable;
            imageView.setImageDrawable(statusDrawable);
            addView(imageView, LayoutHelper.createFrame(40, 40.0f, 23, 7.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.textLayout = linearLayout;
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 23, 54.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            int i = Theme.key_undo_infoColor;
            textView.setTextColor(Theme.getColor(i, resourcesProvider));
            textView.setTypeface(AndroidUtilities.bold());
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 55, 0, 0, 0, 2));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 13.0f);
            textView2.setTextColor(Theme.getColor(i, resourcesProvider));
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 55, 0, 0, 0, 0));
        }

        public void lambda$setButton$0() {
            Bulletin bulletin = getBulletin();
            if (bulletin != null) {
                bulletin.setDuration(2750);
                bulletin.setCanHide(true);
            }
            FileDownload fileDownload = this.file;
            if (fileDownload != null) {
                fileDownload.cancel();
            }
        }

        public void lambda$setButton$1() {
            Bulletin bulletin = getBulletin();
            if (bulletin != null) {
                bulletin.hide();
            }
            FileDownload fileDownload = this.file;
            if (fileDownload != null) {
                fileDownload.open();
            }
        }

        private void setButton(int r4) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotDownloads.DownloadBulletin.setButton(int):void");
        }

        @Override
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), 1073741824));
        }

        public boolean set(FileDownload fileDownload) {
            TextView textView;
            String str;
            if (this.file != fileDownload) {
                this.status.reset();
            }
            this.file = fileDownload;
            this.titleView.setText(fileDownload.file_name);
            if (fileDownload.isDownloading()) {
                Pair progress = fileDownload.getProgress();
                this.status.setProgress(progress);
                if (((Long) progress.first).longValue() <= 0) {
                    this.subtitleView.setText(LocaleController.getString(R.string.BotFileDownloading));
                } else {
                    if (((Long) progress.second).longValue() <= 0) {
                        textView = this.subtitleView;
                        str = AndroidUtilities.formatFileSize(((Long) progress.first).longValue());
                    } else {
                        textView = this.subtitleView;
                        str = AndroidUtilities.formatFileSize(((Long) progress.first).longValue()) + " / " + AndroidUtilities.formatFileSize(((Long) progress.second).longValue());
                    }
                    textView.setText(str);
                }
                setButton(1);
            } else {
                if (fileDownload.cancelled) {
                    Bulletin bulletin = getBulletin();
                    if (bulletin != null) {
                        bulletin.hide();
                    }
                    return true;
                }
                if (fileDownload.done) {
                    this.subtitleView.setText(LocaleController.getString(R.string.BotFileDownloaded));
                    setButton(2);
                    this.status.setDone(false);
                    Bulletin bulletin2 = getBulletin();
                    if (bulletin2 != null) {
                        bulletin2.setCanHide(false);
                        bulletin2.setDuration(5000);
                        bulletin2.setCanHide(true);
                    }
                }
            }
            return false;
        }

        public void setArrow(int i) {
            this.background.setArrow(i);
        }
    }

    public class FileDownload {
        public boolean cancelled;
        public boolean done;
        public File file;
        public String file_name;
        public Long id;
        public long last_progress_time;
        public long loaded_size;
        public String mime;
        public boolean resaved;
        public boolean shown;
        public long size;
        private final Runnable updateProgressRunnable = new Runnable() {
            @Override
            public final void run() {
                BotDownloads.FileDownload.this.updateProgress();
            }
        };
        public String url;

        public FileDownload(String str, String str2) {
            String str3;
            this.url = str;
            this.file_name = str2;
            TLRPC.User user = MessagesController.getInstance(BotDownloads.this.currentAccount).getUser(Long.valueOf(BotDownloads.this.botId));
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
            request.setTitle(UserObject.getUserName(user));
            if (TextUtils.isEmpty(str2)) {
                str3 = "Downloading file...";
            } else {
                str3 = "Downloading " + str2 + "...";
            }
            request.setDescription(str3);
            request.setNotificationVisibility(0);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, str2);
            this.id = Long.valueOf(BotDownloads.this.downloadManager.enqueue(request));
        }

        public FileDownload(JSONObject jSONObject) {
            this.url = jSONObject.optString("url");
            this.file_name = jSONObject.optString("file_name");
            this.size = jSONObject.optLong("size");
            this.done = jSONObject.optBoolean("done");
            this.mime = jSONObject.optString("mime");
            String optString = jSONObject.optString("path");
            if (TextUtils.isEmpty(optString)) {
                return;
            }
            this.file = new File(optString);
        }

        public void updateProgress() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.BotDownloads.FileDownload.updateProgress():void");
        }

        public void cancel() {
            BotDownloads.this.cancel(this);
        }

        public Pair getProgress() {
            if (this.done) {
                return new Pair(Long.valueOf(this.size), Long.valueOf(this.size));
            }
            if (this.id == null || this.cancelled) {
                return new Pair(Long.valueOf(this.loaded_size), Long.valueOf(this.size));
            }
            if (System.currentTimeMillis() - this.last_progress_time < 150) {
                return new Pair(Long.valueOf(this.loaded_size), Long.valueOf(this.size));
            }
            updateProgress();
            return new Pair(Long.valueOf(this.loaded_size), Long.valueOf(this.size));
        }

        public boolean isDownloading() {
            return (this.done || this.id == null) ? false : true;
        }

        public void open() {
            File file = this.file;
            if (file == null || !file.exists()) {
                return;
            }
            File file2 = this.file;
            AndroidUtilities.openForView(file2, file2.getName(), null, LaunchActivity.instance, null, true);
        }

        public JSONObject toJSON() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("url", this.url);
                jSONObject.put("file_name", this.file_name);
                jSONObject.put("size", this.size);
                File file = this.file;
                jSONObject.put("path", file == null ? null : file.getAbsolutePath());
                jSONObject.put("done", this.done);
                jSONObject.put("mime", this.mime);
            } catch (Exception e) {
                FileLog.e(e);
            }
            return jSONObject;
        }
    }

    private BotDownloads(Context context, int i, long j) {
        this.context = context;
        this.currentAccount = i;
        this.botId = j;
        this.downloadManager = (DownloadManager) context.getSystemService("download");
        Set<String> stringSet = context.getSharedPreferences("botdownloads_" + i, 0).getStringSet("" + j, null);
        if (stringSet != null) {
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                try {
                    FileDownload fileDownload = new FileDownload(new JSONObject(it.next()));
                    File file = fileDownload.file;
                    if (file != null && file.exists()) {
                        this.files.add(fileDownload);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    public static void clear() {
        Context context = ApplicationLoader.applicationContext;
        if (context == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            context.getSharedPreferences("botdownloads_" + i, 0).edit().clear().apply();
        }
        instances.clear();
    }

    public static BotDownloads get(Context context, int i, long j) {
        Pair pair = new Pair(Integer.valueOf(i), Long.valueOf(j));
        HashMap hashMap = instances;
        BotDownloads botDownloads = (BotDownloads) hashMap.get(pair);
        if (botDownloads != null) {
            return botDownloads;
        }
        BotDownloads botDownloads2 = new BotDownloads(context, i, j);
        hashMap.put(pair, botDownloads2);
        return botDownloads2;
    }

    public static String getExt(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        char c = 65535;
        switch (str.hashCode()) {
            case -2008589971:
                if (str.equals("application/epub+zip")) {
                    c = 0;
                    break;
                }
                break;
            case -1719571662:
                if (str.equals("application/vnd.oasis.opendocument.text")) {
                    c = 1;
                    break;
                }
                break;
            case -1664118616:
                if (str.equals("video/3gpp")) {
                    c = 2;
                    break;
                }
                break;
            case -1578389996:
                if (str.equals("application/vnd.ms-fontobject")) {
                    c = 3;
                    break;
                }
                break;
            case -1348237359:
                if (str.equals("application/x-cdf")) {
                    c = 4;
                    break;
                }
                break;
            case -1348236892:
                if (str.equals("application/x-csh")) {
                    c = 5;
                    break;
                }
                break;
            case -1079884372:
                if (str.equals("video/x-msvideo")) {
                    c = 6;
                    break;
                }
                break;
            case -1073633483:
                if (str.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                    c = 7;
                    break;
                }
                break;
            case -1071817359:
                if (str.equals("application/vnd.ms-powerpoint")) {
                    c = '\b';
                    break;
                }
                break;
            case -1050893613:
                if (str.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    c = '\t';
                    break;
                }
                break;
            case -1007601745:
                if (str.equals("audio/x-midi")) {
                    c = '\n';
                    break;
                }
                break;
            case -958424608:
                if (str.equals("text/calendar")) {
                    c = 11;
                    break;
                }
                break;
            case -816908365:
                if (str.equals("application/x-httpd-php")) {
                    c = '\f';
                    break;
                }
                break;
            case -648684635:
                if (str.equals("audio/3gpp2")) {
                    c = '\r';
                    break;
                }
                break;
            case -433129473:
                if (str.equals("application/vnd.apple.installer+xml")) {
                    c = 14;
                    break;
                }
                break;
            case -366307023:
                if (str.equals("application/vnd.ms-excel")) {
                    c = 15;
                    break;
                }
                break;
            case -48069494:
                if (str.equals("video/3gpp2")) {
                    c = 16;
                    break;
                }
                break;
            case -43923783:
                if (str.equals("application/gzip")) {
                    c = 17;
                    break;
                }
                break;
            case -43491031:
                if (str.equals("application/x-sh")) {
                    c = 18;
                    break;
                }
                break;
            case 187091926:
                if (str.equals("audio/ogg")) {
                    c = 19;
                    break;
                }
                break;
            case 817335912:
                if (str.equals("text/plain")) {
                    c = 20;
                    break;
                }
                break;
            case 859118878:
                if (str.equals("application/x-abiword")) {
                    c = 21;
                    break;
                }
                break;
            case 886992732:
                if (str.equals("application/ld+json")) {
                    c = 22;
                    break;
                }
                break;
            case 904647503:
                if (str.equals("application/msword")) {
                    c = 23;
                    break;
                }
                break;
            case 1154306387:
                if (str.equals("application/x-bzip")) {
                    c = 24;
                    break;
                }
                break;
            case 1154455342:
                if (str.equals("application/x-gzip")) {
                    c = 25;
                    break;
                }
                break;
            case 1178484637:
                if (str.equals("application/octet-stream")) {
                    c = 26;
                    break;
                }
                break;
            case 1423759679:
                if (str.equals("application/x-bzip2")) {
                    c = 27;
                    break;
                }
                break;
            case 1436962847:
                if (str.equals("application/vnd.oasis.opendocument.presentation")) {
                    c = 28;
                    break;
                }
                break;
            case 1454024983:
                if (str.equals("application/x-7z-compressed")) {
                    c = 29;
                    break;
                }
                break;
            case 1455492626:
                if (str.equals("application/x-freearc")) {
                    c = 30;
                    break;
                }
                break;
            case 1503095341:
                if (str.equals("audio/3gpp")) {
                    c = 31;
                    break;
                }
                break;
            case 1504831518:
                if (str.equals("audio/mpeg")) {
                    c = ' ';
                    break;
                }
                break;
            case 1509238306:
                if (str.equals("application/vnd.rar")) {
                    c = '!';
                    break;
                }
                break;
            case 1578362927:
                if (str.equals("image/vnd.microsoft.icon")) {
                    c = '\"';
                    break;
                }
                break;
            case 1643664935:
                if (str.equals("application/vnd.oasis.opendocument.spreadsheet")) {
                    c = '#';
                    break;
                }
                break;
            case 1672200517:
                if (str.equals("application/vnd.amazon.ebook")) {
                    c = '$';
                    break;
                }
                break;
            case 1993842850:
                if (str.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                    c = '%';
                    break;
                }
                break;
            case 2049276534:
                if (str.equals("application/java-archive")) {
                    c = '&';
                    break;
                }
                break;
            case 2132236175:
                if (str.equals("text/javascript")) {
                    c = '\'';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "epub";
            case 1:
                return "odt";
            case 2:
            case 31:
                return "3gp";
            case 3:
                return "eot";
            case 4:
                return "cda";
            case 5:
                return "csh";
            case 6:
                return "avi";
            case 7:
                return "pptx";
            case '\b':
                return "ppt";
            case '\t':
                return "docx";
            case '\n':
                return "midi";
            case 11:
                return "ics";
            case '\f':
                return "php";
            case '\r':
            case 16:
                return "3g2";
            case 14:
                return "mpkg";
            case 15:
                return "xls";
            case 17:
            case 25:
                return "gz";
            case 18:
                return "sh";
            case 19:
                return "opus";
            case 20:
                return "txt";
            case 21:
                return "abw";
            case 22:
                return "jsonld";
            case 23:
                return "doc";
            case 24:
                return "bz";
            case 26:
                return "bin";
            case 27:
                return "bz2";
            case 28:
                return "odp";
            case 29:
                return "7z";
            case 30:
                return "arc";
            case ' ':
                return "mp3";
            case '!':
                return "rar";
            case '\"':
                return "ico";
            case '#':
                return "ods";
            case '$':
                return "azw";
            case '%':
                return "xlsx";
            case '&':
                return "jar";
            case '\'':
                return "js";
            default:
                if (str.contains("/")) {
                    str = str.substring(str.indexOf("/") + 1);
                }
                if (str.contains("-")) {
                    str = str.substring(str.indexOf("-") + 1);
                }
                if (str.contains("+")) {
                    str = str.substring(0, str.indexOf("+"));
                }
                return str.toLowerCase();
        }
    }

    public static void getMimeAndSize(final String str, final Utilities.Callback2 callback2) {
        if (!cachedMimeAndSizes.containsKey(str)) {
            new AsyncTask() {
                String mime;
                long size;

                @Override
                public String doInBackground(String... strArr) {
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                        httpURLConnection.setConnectTimeout(1000);
                        httpURLConnection.setReadTimeout(1000);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setDefaultUseCaches(false);
                        httpURLConnection.setDoOutput(false);
                        httpURLConnection.setDoInput(false);
                        httpURLConnection.getResponseCode();
                        this.size = Build.VERSION.SDK_INT >= 24 ? httpURLConnection.getContentLengthLong() : httpURLConnection.getContentLength();
                        String contentType = httpURLConnection.getContentType();
                        this.mime = contentType;
                        if (contentType.contains("; ")) {
                            String str2 = this.mime;
                            this.mime = str2.substring(0, str2.indexOf("; "));
                        }
                        httpURLConnection.getInputStream().close();
                        return null;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return null;
                    }
                }

                @Override
                public void onPostExecute(String str2) {
                    BotDownloads.cachedMimeAndSizes.put(str, new Pair(this.mime, Long.valueOf(this.size)));
                    Utilities.Callback2 callback22 = callback2;
                    if (callback22 != null) {
                        callback22.run(this.mime, Long.valueOf(this.size));
                    }
                }
            }.execute(str);
        } else {
            Pair pair = (Pair) cachedMimeAndSizes.get(str);
            callback2.run((String) pair.first, (Long) pair.second);
        }
    }

    public static void lambda$showAlert$0(AnimatedTextView animatedTextView, String str, Long l) {
        StringBuilder sb = new StringBuilder();
        if (l.longValue() > 0) {
            sb.append("~");
            sb.append(AndroidUtilities.formatFileSize(l.longValue()));
        }
        String upperCase = str == null ? null : getExt(str).toUpperCase();
        if (!TextUtils.isEmpty(upperCase)) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(upperCase.toUpperCase());
        }
        if (sb.length() <= 0) {
            sb.append(LocaleController.getString(R.string.AttachDocument));
        }
        animatedTextView.setText(sb);
    }

    public static void lambda$showAlert$1(boolean[] zArr, Utilities.Callback callback, DialogInterface dialogInterface, int i) {
        if (zArr[0]) {
            return;
        }
        callback.run(Boolean.FALSE);
        zArr[0] = true;
    }

    public static void lambda$showAlert$2(boolean[] zArr, Utilities.Callback callback, DialogInterface dialogInterface, int i) {
        if (zArr[0]) {
            return;
        }
        callback.run(Boolean.TRUE);
        zArr[0] = true;
    }

    public static void lambda$showAlert$3(boolean[] zArr, Utilities.Callback callback, DialogInterface dialogInterface) {
        if (zArr[0]) {
            return;
        }
        callback.run(Boolean.FALSE);
        zArr[0] = true;
    }

    public void postNotify() {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.botDownloadsUpdate, new Object[0]);
    }

    public static AlertDialog showAlert(Context context, String str, String str2, String str3, final Utilities.Callback callback, long j, String str4) {
        if (callback == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.BotDownloadFileTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotDownloadFileText, str3)));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        linearLayout.setOrientation(0);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(44.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
        imageView.setImageResource(R.drawable.msg_round_file_s);
        linearLayout.addView(imageView, LayoutHelper.createLinear(44, 44, 19, 0, 0, 10, 0));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(str2);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        linearLayout2.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 3.0f));
        final AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        animatedTextView.setTextSize(AndroidUtilities.dp(12.0f));
        SpannableString spannableString = new SpannableString("l");
        LoadingSpan loadingSpan = new LoadingSpan(animatedTextView, AndroidUtilities.dp(55.0f));
        int i = Theme.key_chat_inFileInfoText;
        loadingSpan.setColors(Theme.multAlpha(Theme.getColor(i), 0.35f), Theme.multAlpha(Theme.getColor(i), 0.075f));
        spannableString.setSpan(loadingSpan, 0, 1, 33);
        animatedTextView.setText(spannableString);
        getMimeAndSize(str, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                BotDownloads.lambda$showAlert$0(AnimatedTextView.this, (String) obj, (Long) obj2);
            }
        });
        animatedTextView.setTextColor(Theme.getColor(i));
        linearLayout2.addView(animatedTextView, LayoutHelper.createLinear(-1, 15));
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 23, 0, 0, 0, 2));
        builder.setView(linearLayout);
        final boolean[] zArr = new boolean[1];
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                BotDownloads.lambda$showAlert$1(zArr, callback, dialogInterface, i2);
            }
        });
        builder.setPositiveButton(LocaleController.getString(R.string.BotDownloadFileDownload), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                BotDownloads.lambda$showAlert$2(zArr, callback, dialogInterface, i2);
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                BotDownloads.lambda$showAlert$3(zArr, callback, dialogInterface);
            }
        });
        create.show();
        return create;
    }

    public static void showAlert(Context context, String str, String str2, String str3, Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        showAlert(context, str, str2, str3, callback, 0L, "");
    }

    public void cancel(FileDownload fileDownload) {
        if (fileDownload == null) {
            return;
        }
        fileDownload.cancelled = true;
        Long l = fileDownload.id;
        if (l != null) {
            this.downloadManager.remove(l.longValue());
            fileDownload.id = null;
        }
        this.files.remove(fileDownload);
        postNotify();
    }

    public FileDownload download(String str, String str2) {
        FileDownload cached = getCached(str);
        if (cached != null) {
            this.currentFile = cached;
            cached.resaved = true;
        } else {
            cached = new FileDownload(str, str2);
            this.currentFile = cached;
            cached.shown = false;
            this.files.add(cached);
            save();
        }
        postNotify();
        return cached;
    }

    public FileDownload getCached(String str) {
        Iterator it = this.files.iterator();
        while (it.hasNext()) {
            FileDownload fileDownload = (FileDownload) it.next();
            if (TextUtils.equals(fileDownload.url, str) && fileDownload.done) {
                return fileDownload;
            }
        }
        return null;
    }

    public FileDownload getCurrent() {
        return this.currentFile;
    }

    public ArrayList getFiles() {
        return this.files;
    }

    public boolean hasFiles() {
        return !this.files.isEmpty();
    }

    public boolean isDownloading() {
        Iterator it = this.files.iterator();
        while (it.hasNext()) {
            if (((FileDownload) it.next()).isDownloading()) {
                return true;
            }
        }
        return false;
    }

    public void save() {
        SharedPreferences.Editor edit = this.context.getSharedPreferences("botdownloads_" + this.currentAccount, 0).edit();
        edit.clear();
        HashSet hashSet = new HashSet();
        Iterator it = this.files.iterator();
        while (it.hasNext()) {
            hashSet.add(((FileDownload) it.next()).toJSON().toString());
        }
        edit.putStringSet("" + this.botId, hashSet);
        edit.apply();
    }
}
