package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Pair;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;

public class SeekBar {
    private static Paint paint;
    private static int thumbWidth;
    private static Path tmpPath;
    private static float[] tmpRadii;
    private int backgroundColor;
    private int backgroundSelectedColor;
    private float bufferedProgress;
    private int cacheColor;
    private int circleColor;
    private float currentRadius;
    private SeekBarDelegate delegate;
    private int height;
    private CharSequence lastCaption;
    private long lastTimestampUpdate;
    private long lastTimestampsAppearingUpdate;
    private long lastUpdateTime;
    private long lastVideoDuration;
    private View parentView;
    private int progressColor;
    private boolean selected;
    private float thumbProgress;
    private StaticLayout[] timestampLabel;
    private TextPaint timestampLabelPaint;
    private ArrayList timestamps;
    private int width;
    private int thumbX = 0;
    private int draggingThumbX = 0;
    private int thumbDX = 0;
    private boolean pressed = false;
    private RectF rect = new RectF();
    private int lineHeight = AndroidUtilities.dp(2.0f);
    private float alpha = 1.0f;
    private float timestampsAppearing = 0.0f;
    private final float TIMESTAMP_GAP = 1.0f;
    private int currentTimestamp = -1;
    private int lastTimestamp = -1;
    private float timestampChangeT = 1.0f;
    private float lastWidth = -1.0f;

    public interface SeekBarDelegate {

        public abstract class CC {
            public static boolean $default$isSeekBarDragAllowed(SeekBarDelegate seekBarDelegate) {
                return true;
            }

            public static void $default$onSeekBarContinuousDrag(SeekBarDelegate seekBarDelegate, float f) {
            }

            public static void $default$onSeekBarPressed(SeekBarDelegate seekBarDelegate) {
            }

            public static void $default$onSeekBarReleased(SeekBarDelegate seekBarDelegate) {
            }

            public static boolean $default$reverseWaveform(SeekBarDelegate seekBarDelegate) {
                return false;
            }
        }

        boolean isSeekBarDragAllowed();

        void onSeekBarContinuousDrag(float f);

        void onSeekBarDrag(float f);

        void onSeekBarPressed();

        void onSeekBarReleased();

        boolean reverseWaveform();
    }

    public SeekBar(View view) {
        if (paint == null) {
            paint = new Paint(1);
        }
        this.parentView = view;
        thumbWidth = AndroidUtilities.dp(24.0f);
        this.currentRadius = AndroidUtilities.dp(6.0f);
    }

    private void drawProgressBar(Canvas canvas, RectF rectF, Paint paint2) {
        int i;
        SeekBar seekBar = this;
        float f = thumbWidth / 2.0f;
        ArrayList arrayList = seekBar.timestamps;
        if (arrayList == null || arrayList.isEmpty()) {
            canvas.drawRoundRect(rectF, f, f, paint2);
            return;
        }
        float f2 = rectF.bottom;
        float f3 = thumbWidth / 2.0f;
        float f4 = seekBar.width - f3;
        AndroidUtilities.rectTmp.set(rectF);
        float dp = AndroidUtilities.dp(seekBar.timestampsAppearing * 1.0f) / 2.0f;
        if (tmpPath == null) {
            tmpPath = new Path();
        }
        tmpPath.reset();
        float dp2 = AndroidUtilities.dp(4.0f) / (f4 - f3);
        int i2 = 0;
        while (true) {
            i = -1;
            if (i2 >= seekBar.timestamps.size()) {
                i2 = -1;
                break;
            } else if (((Float) ((Pair) seekBar.timestamps.get(i2)).first).floatValue() >= dp2) {
                break;
            } else {
                i2++;
            }
        }
        if (i2 < 0) {
            i2 = 0;
        }
        int i3 = 1;
        int size = seekBar.timestamps.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }
            if (1.0f - ((Float) ((Pair) seekBar.timestamps.get(size)).first).floatValue() >= dp2) {
                i = size + 1;
                break;
            }
            size--;
        }
        if (i < 0) {
            i = seekBar.timestamps.size();
        }
        int i4 = i2;
        while (i4 <= i) {
            float floatValue = i4 == i2 ? 0.0f : ((Float) ((Pair) seekBar.timestamps.get(i4 - 1)).first).floatValue();
            float floatValue2 = i4 == i ? 1.0f : ((Float) ((Pair) seekBar.timestamps.get(i4)).first).floatValue();
            while (i4 != i && i4 != 0 && i4 < seekBar.timestamps.size() - i3 && ((Float) ((Pair) seekBar.timestamps.get(i4)).first).floatValue() - floatValue <= dp2) {
                i4++;
                floatValue2 = ((Float) ((Pair) seekBar.timestamps.get(i4)).first).floatValue();
            }
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.left = AndroidUtilities.lerp(f3, f4, floatValue) + (i4 > 0 ? dp : 0.0f);
            float lerp = AndroidUtilities.lerp(f3, f4, floatValue2) - (i4 < i ? dp : 0.0f);
            rectF2.right = lerp;
            float f5 = rectF.right;
            boolean z = lerp > f5;
            if (z) {
                rectF2.right = f5;
            }
            float f6 = rectF2.right;
            float f7 = rectF.left;
            if (f6 >= f7) {
                if (rectF2.left < f7) {
                    rectF2.left = f7;
                }
                if (tmpRadii == null) {
                    tmpRadii = new float[8];
                }
                if (i4 == i2 || (z && rectF2.left >= rectF.left)) {
                    float[] fArr = tmpRadii;
                    fArr[7] = f;
                    fArr[6] = f;
                    fArr[1] = f;
                    fArr[0] = f;
                    float f8 = 0.7f * f * seekBar.timestampsAppearing;
                    fArr[5] = f8;
                    fArr[4] = f8;
                    fArr[3] = f8;
                    fArr[2] = f8;
                } else if (i4 >= i) {
                    float[] fArr2 = tmpRadii;
                    float f9 = 0.7f * f * seekBar.timestampsAppearing;
                    fArr2[7] = f9;
                    fArr2[6] = f9;
                    fArr2[1] = f9;
                    fArr2[0] = f9;
                    fArr2[5] = f;
                    fArr2[4] = f;
                    fArr2[3] = f;
                    fArr2[2] = f;
                } else {
                    float[] fArr3 = tmpRadii;
                    float f10 = 0.7f * f * seekBar.timestampsAppearing;
                    fArr3[5] = f10;
                    fArr3[4] = f10;
                    fArr3[3] = f10;
                    fArr3[2] = f10;
                    fArr3[7] = f10;
                    fArr3[6] = f10;
                    fArr3[1] = f10;
                    fArr3[0] = f10;
                }
                tmpPath.addRoundRect(rectF2, tmpRadii, Path.Direction.CW);
                if (z) {
                    break;
                }
            }
            i4++;
            i3 = 1;
            seekBar = this;
        }
        canvas.drawPath(tmpPath, paint2);
    }

    public static int lambda$updateTimestamps$0(Pair pair, Pair pair2) {
        if (((Float) pair.first).floatValue() > ((Float) pair2.first).floatValue()) {
            return 1;
        }
        return ((Float) pair2.first).floatValue() > ((Float) pair.first).floatValue() ? -1 : 0;
    }

    private void updateTimestampAnimation() {
        ArrayList arrayList = this.timestamps;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        float f = (this.pressed ? this.draggingThumbX : this.thumbX) / (this.width - thumbWidth);
        int size = this.timestamps.size() - 1;
        while (true) {
            if (size < 0) {
                size = -1;
                break;
            } else if (((Float) ((Pair) this.timestamps.get(size)).first).floatValue() - 0.001f <= f) {
                break;
            } else {
                size--;
            }
        }
        if (this.timestampLabel == null) {
            this.timestampLabel = new StaticLayout[2];
        }
        float f2 = thumbWidth / 2.0f;
        this.lastWidth = Math.abs(f2 - (this.width - f2)) - AndroidUtilities.dp(66.0f);
        if (size != this.currentTimestamp) {
            if (this.pressed) {
                AndroidUtilities.vibrateCursor(this.parentView);
            }
            this.currentTimestamp = size;
            if (size >= 0 && size < this.timestamps.size()) {
                onTimestampUpdate((URLSpanNoUnderline) ((Pair) this.timestamps.get(this.currentTimestamp)).second);
            }
        }
        if (this.timestampChangeT < 1.0f) {
            this.timestampChangeT = Math.min(this.timestampChangeT + (((float) Math.min(17L, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate))) / (this.timestamps.size() > 8 ? 160.0f : 220.0f)), 1.0f);
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
            this.lastTimestampUpdate = SystemClock.elapsedRealtime();
        }
        if (this.timestampsAppearing < 1.0f) {
            this.timestampsAppearing = Math.min(this.timestampsAppearing + (((float) Math.min(17L, Math.abs(SystemClock.elapsedRealtime() - this.lastTimestampUpdate))) / 200.0f), 1.0f);
            View view2 = this.parentView;
            if (view2 != null) {
                view2.invalidate();
            }
            this.lastTimestampsAppearingUpdate = SystemClock.elapsedRealtime();
        }
    }

    public void clearTimestamps() {
        this.timestamps = null;
        this.currentTimestamp = -1;
        this.timestampsAppearing = 0.0f;
        StaticLayout[] staticLayoutArr = this.timestampLabel;
        if (staticLayoutArr != null) {
            staticLayoutArr[1] = null;
            staticLayoutArr[0] = null;
        }
        this.lastCaption = null;
        this.lastVideoDuration = -1L;
    }

    public void draw(android.graphics.Canvas r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBar.draw(android.graphics.Canvas):void");
    }

    public float getProgress() {
        return this.thumbX / (this.width - thumbWidth);
    }

    public int getWidth() {
        return this.width - thumbWidth;
    }

    public boolean isDragging() {
        return this.pressed;
    }

    protected void onTimestampUpdate(URLSpanNoUnderline uRLSpanNoUnderline) {
    }

    public boolean onTouch(int i, float f, float f2) {
        SeekBarDelegate seekBarDelegate;
        if (i == 0) {
            int i2 = this.height;
            int i3 = thumbWidth;
            int i4 = (i2 - i3) / 2;
            if (f >= (-i4)) {
                int i5 = this.width;
                if (f <= i5 + i4 && f2 >= 0.0f && f2 <= i2) {
                    int i6 = this.thumbX;
                    if (i6 - i4 > f || f > i6 + i3 + i4) {
                        int i7 = ((int) f) - (i3 / 2);
                        this.thumbX = i7;
                        if (i7 < 0) {
                            this.thumbX = 0;
                        } else {
                            int i8 = i5 - i3;
                            if (i7 > i8) {
                                this.thumbX = i8;
                            }
                        }
                    }
                    this.pressed = true;
                    int i9 = this.thumbX;
                    this.draggingThumbX = i9;
                    this.thumbDX = (int) (f - i9);
                    return true;
                }
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                int i10 = this.draggingThumbX;
                this.thumbX = i10;
                if (i == 1 && (seekBarDelegate = this.delegate) != null) {
                    seekBarDelegate.onSeekBarDrag(i10 / (this.width - thumbWidth));
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            int i11 = (int) (f - this.thumbDX);
            this.draggingThumbX = i11;
            if (i11 < 0) {
                this.draggingThumbX = 0;
            } else {
                int i12 = this.width - thumbWidth;
                if (i11 > i12) {
                    this.draggingThumbX = i12;
                }
            }
            SeekBarDelegate seekBarDelegate2 = this.delegate;
            if (seekBarDelegate2 != null) {
                seekBarDelegate2.onSeekBarContinuousDrag(this.draggingThumbX / (this.width - thumbWidth));
            }
            return true;
        }
        return false;
    }

    public void setAlpha(float f) {
        this.alpha = f;
    }

    public void setBufferedProgress(float f) {
        this.bufferedProgress = f;
    }

    public void setColors(int i, int i2, int i3, int i4, int i5) {
        this.backgroundColor = i;
        this.cacheColor = i2;
        this.circleColor = i4;
        this.progressColor = i3;
        this.backgroundSelectedColor = i5;
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setProgress(float f) {
        this.thumbProgress = f;
        int ceil = (int) Math.ceil((this.width - thumbWidth) * f);
        this.thumbX = ceil;
        if (ceil < 0) {
            this.thumbX = 0;
            return;
        }
        int i = this.width - thumbWidth;
        if (ceil > i) {
            this.thumbX = i;
        }
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setSize(int i, int i2) {
        if (this.width == i && this.height == i2) {
            return;
        }
        this.width = i;
        this.height = i2;
        setProgress(this.thumbProgress);
    }

    public void updateTimestamps(MessageObject messageObject, Long l) {
        Integer parseInt;
        String str;
        if (messageObject == null) {
            clearTimestamps();
            return;
        }
        if (l == null) {
            l = Long.valueOf(((long) messageObject.getDuration()) * 1000);
        }
        if (l.longValue() < 0) {
            clearTimestamps();
            return;
        }
        CharSequence charSequence = messageObject.caption;
        if (messageObject.isYouTubeVideo()) {
            if (messageObject.youtubeDescription == null && (str = messageObject.messageOwner.media.webpage.description) != null) {
                messageObject.youtubeDescription = SpannableString.valueOf(str);
                MessageObject.addUrlsByPattern(messageObject.isOut(), messageObject.youtubeDescription, false, 3, (int) l.longValue(), false);
            }
            charSequence = messageObject.youtubeDescription;
        }
        if (charSequence == this.lastCaption && this.lastVideoDuration == l.longValue()) {
            return;
        }
        this.lastCaption = charSequence;
        this.lastVideoDuration = l.longValue();
        if (!(charSequence instanceof Spanned)) {
            this.timestamps = null;
            this.currentTimestamp = -1;
            this.timestampsAppearing = 0.0f;
            StaticLayout[] staticLayoutArr = this.timestampLabel;
            if (staticLayoutArr != null) {
                staticLayoutArr[1] = null;
                staticLayoutArr[0] = null;
                return;
            }
            return;
        }
        Spanned spanned = (Spanned) charSequence;
        try {
            URLSpanNoUnderline[] uRLSpanNoUnderlineArr = (URLSpanNoUnderline[]) spanned.getSpans(0, spanned.length(), URLSpanNoUnderline.class);
            this.timestamps = new ArrayList();
            this.timestampsAppearing = 0.0f;
            if (this.timestampLabelPaint == null) {
                TextPaint textPaint = new TextPaint(1);
                this.timestampLabelPaint = textPaint;
                textPaint.setTextSize(AndroidUtilities.dp(12.0f));
                this.timestampLabelPaint.setColor(-1);
            }
            for (URLSpanNoUnderline uRLSpanNoUnderline : uRLSpanNoUnderlineArr) {
                try {
                    if (uRLSpanNoUnderline != null && uRLSpanNoUnderline.getURL() != null && uRLSpanNoUnderline.label != null && uRLSpanNoUnderline.getURL().startsWith("audio?") && (parseInt = Utilities.parseInt((CharSequence) uRLSpanNoUnderline.getURL().substring(6))) != null && parseInt.intValue() >= 0) {
                        float intValue = ((float) (parseInt.intValue() * 1000)) / ((float) l.longValue());
                        Emoji.replaceEmoji((CharSequence) new SpannableStringBuilder(uRLSpanNoUnderline.label), this.timestampLabelPaint.getFontMetricsInt(), AndroidUtilities.dp(14.0f), false);
                        this.timestamps.add(new Pair(Float.valueOf(intValue), uRLSpanNoUnderline));
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            Collections.sort(this.timestamps, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$updateTimestamps$0;
                    lambda$updateTimestamps$0 = SeekBar.lambda$updateTimestamps$0((Pair) obj, (Pair) obj2);
                    return lambda$updateTimestamps$0;
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
            this.timestamps = null;
            this.currentTimestamp = -1;
            this.timestampsAppearing = 0.0f;
            StaticLayout[] staticLayoutArr2 = this.timestampLabel;
            if (staticLayoutArr2 != null) {
                staticLayoutArr2[1] = null;
                staticLayoutArr2[0] = null;
            }
        }
    }
}
