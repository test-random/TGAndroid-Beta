package org.telegram.ui.Cells;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.spoilers.SpoilerEffect2;

public class GroupMedia {
    private final AnimatedFloat animatedHidden;
    public boolean attached;
    private Bitmap blurBitmap;
    private int blurBitmapHeight;
    private int blurBitmapMessageId;
    private Paint blurBitmapPaint;
    private int blurBitmapState;
    private int blurBitmapWidth;
    private final ButtonBounce bounce;
    private Text buttonText;
    private long buttonTextPrice;
    public final ChatMessageCell cell;
    public int height;
    public boolean hidden;
    private GroupedMessages layout;
    private LoadingDrawable loadingDrawable;
    public int maxWidth;
    private int overrideWidth;
    private boolean pressButton;
    private MediaHolder pressHolder;
    private Text priceText;
    private long priceTextPrice;
    SpoilerEffect2 spoilerEffect;
    public int width;
    public int x;
    public int y;
    public final ArrayList holders = new ArrayList();
    private Path clipPath = new Path();
    private Path clipPath2 = new Path();
    private RectF clipRect = new RectF();

    public static class GroupedMessages {
        public boolean hasSibling;
        float height;
        int maxX;
        int maxY;
        int width;
        public ArrayList medias = new ArrayList();
        public ArrayList posArray = new ArrayList();
        public HashMap positions = new HashMap();
        public int maxSizeWidth = 800;
        public float maxSizeHeight = 814.0f;
        public final TransitionParams transitionParams = new TransitionParams();

        public static class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i, int i2, float f, float f2) {
                this.lineCounts = new int[]{i, i2};
                this.heights = new float[]{f, f2};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, float f, float f2, float f3) {
                this.lineCounts = new int[]{i, i2, i3};
                this.heights = new float[]{f, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i, i2, i3, i4};
                this.heights = new float[]{f, f2, f3, f4};
            }
        }

        public static class TransitionParams {
            public float captionEnterProgress = 1.0f;
        }

        private float getLeft(MessageObject.GroupedMessagePosition groupedMessagePosition, int i, int i2, int i3) {
            int i4 = (i2 - i) + 1;
            float[] fArr = new float[i4];
            float f = 0.0f;
            Arrays.fill(fArr, 0.0f);
            int size = this.posArray.size();
            for (int i5 = 0; i5 < size; i5++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = (MessageObject.GroupedMessagePosition) this.posArray.get(i5);
                if (groupedMessagePosition2 != groupedMessagePosition && groupedMessagePosition2.maxX < i3) {
                    int min = Math.min((int) groupedMessagePosition2.maxY, i2) - i;
                    for (int max = Math.max(groupedMessagePosition2.minY - i, 0); max <= min; max++) {
                        fArr[max] = fArr[max] + groupedMessagePosition2.pw;
                    }
                }
            }
            for (int i6 = 0; i6 < i4; i6++) {
                float f2 = fArr[i6];
                if (f < f2) {
                    f = f2;
                }
            }
            return f;
        }

        private float getTop(MessageObject.GroupedMessagePosition groupedMessagePosition, int i) {
            int i2 = this.maxX + 1;
            float[] fArr = new float[i2];
            float f = 0.0f;
            Arrays.fill(fArr, 0.0f);
            int size = this.posArray.size();
            for (int i3 = 0; i3 < size; i3++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = (MessageObject.GroupedMessagePosition) this.posArray.get(i3);
                if (groupedMessagePosition2 != groupedMessagePosition && groupedMessagePosition2.maxY < i) {
                    for (int i4 = groupedMessagePosition2.minX; i4 <= groupedMessagePosition2.maxX; i4++) {
                        fArr[i4] = fArr[i4] + groupedMessagePosition2.ph;
                    }
                }
            }
            for (int i5 = 0; i5 < i2; i5++) {
                float f2 = fArr[i5];
                if (f < f2) {
                    f = f2;
                }
            }
            return f;
        }

        private float multiHeight(float[] fArr, int i, int i2) {
            float f = 0.0f;
            while (i < i2) {
                f += fArr[i];
                i++;
            }
            return this.maxSizeWidth / f;
        }

        public void calculate() {
            int i;
            float f;
            float f2;
            int i2;
            int i3;
            int i4;
            int i5;
            int i6;
            MessageObject.GroupedMessagePosition groupedMessagePosition;
            int i7;
            float f3;
            float f4;
            TLRPC.Document document;
            ArrayList<TLRPC.PhotoSize> arrayList;
            TLRPC.PhotoSize photoSize;
            boolean z = true;
            ?? r3 = 1;
            this.posArray.clear();
            this.positions.clear();
            this.maxX = 0;
            int size = this.medias.size();
            if (size == 0) {
                this.width = 0;
                this.height = 0.0f;
                this.maxY = 0;
                return;
            }
            this.maxSizeWidth = 800;
            StringBuilder sb = new StringBuilder();
            this.hasSibling = false;
            int i8 = 0;
            float f5 = 1.0f;
            boolean z2 = false;
            while (i8 < size) {
                TLRPC.MessageExtendedMedia messageExtendedMedia = (TLRPC.MessageExtendedMedia) this.medias.get(i8);
                MessageObject.GroupedMessagePosition groupedMessagePosition2 = new MessageObject.GroupedMessagePosition();
                groupedMessagePosition2.last = i8 == size + (-1);
                if (messageExtendedMedia instanceof TLRPC.TL_messageExtendedMediaPreview) {
                    TLRPC.TL_messageExtendedMediaPreview tL_messageExtendedMediaPreview = (TLRPC.TL_messageExtendedMediaPreview) messageExtendedMedia;
                    groupedMessagePosition2.photoWidth = tL_messageExtendedMediaPreview.w;
                    groupedMessagePosition2.photoHeight = tL_messageExtendedMediaPreview.h;
                } else {
                    int i9 = 100;
                    if (messageExtendedMedia instanceof TLRPC.TL_messageExtendedMedia) {
                        TLRPC.MessageMedia messageMedia = ((TLRPC.TL_messageExtendedMedia) messageExtendedMedia).media;
                        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                            TLRPC.Photo photo = ((TLRPC.TL_messageMediaPhoto) messageMedia).photo;
                            if (photo != null) {
                                arrayList = photo.sizes;
                                photoSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                            }
                            photoSize = null;
                        } else {
                            if ((messageMedia instanceof TLRPC.TL_messageMediaDocument) && (document = ((TLRPC.TL_messageMediaDocument) messageMedia).document) != null) {
                                arrayList = document.thumbs;
                                photoSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                            }
                            photoSize = null;
                        }
                        groupedMessagePosition2.photoWidth = photoSize == null ? 100 : photoSize.w;
                        if (photoSize != null) {
                            i9 = photoSize.h;
                        }
                    } else {
                        groupedMessagePosition2.photoWidth = 100;
                    }
                    groupedMessagePosition2.photoHeight = i9;
                }
                if (groupedMessagePosition2.photoWidth <= 0 || groupedMessagePosition2.photoHeight <= 0) {
                    groupedMessagePosition2.photoWidth = 50;
                    groupedMessagePosition2.photoHeight = 50;
                }
                float f6 = groupedMessagePosition2.photoWidth / groupedMessagePosition2.photoHeight;
                groupedMessagePosition2.aspectRatio = f6;
                sb.append(f6 > 1.2f ? "w" : f6 < 0.8f ? "n" : "q");
                float f7 = groupedMessagePosition2.aspectRatio;
                f5 += f7;
                if (f7 > 2.0f) {
                    z2 = true;
                }
                this.positions.put(messageExtendedMedia, groupedMessagePosition2);
                this.posArray.add(groupedMessagePosition2);
                i8++;
            }
            int dp = AndroidUtilities.dp(120.0f);
            float dp2 = AndroidUtilities.dp(120.0f);
            Point point = AndroidUtilities.displaySize;
            int min = (int) (dp2 / (Math.min(point.x, point.y) / this.maxSizeWidth));
            float dp3 = AndroidUtilities.dp(40.0f);
            Point point2 = AndroidUtilities.displaySize;
            float min2 = Math.min(point2.x, point2.y);
            float f8 = this.maxSizeWidth;
            int i10 = (int) (dp3 / (min2 / f8));
            float f9 = f8 / this.maxSizeHeight;
            float f10 = f5 / size;
            float dp4 = AndroidUtilities.dp(100.0f) / this.maxSizeHeight;
            if (size == 1) {
                MessageObject.GroupedMessagePosition groupedMessagePosition3 = (MessageObject.GroupedMessagePosition) this.posArray.get(0);
                float f11 = groupedMessagePosition3.aspectRatio;
                if (f11 >= 1.0f) {
                    f3 = this.maxSizeWidth;
                    f4 = ((f3 / f11) / f3) * this.maxSizeHeight;
                } else {
                    float f12 = this.maxSizeHeight;
                    f3 = ((f11 * f12) / f12) * this.maxSizeWidth;
                    f4 = f12;
                }
                groupedMessagePosition3.set(0, 0, 0, 0, (int) f3, f4 / this.maxSizeHeight, 15);
            } else if (z2 || !(size == 2 || size == 3 || size == 4)) {
                int size2 = this.posArray.size();
                float[] fArr = new float[size2];
                for (int i11 = 0; i11 < size; i11++) {
                    if (f10 > 1.1f) {
                        fArr[i11] = Math.max(1.0f, ((MessageObject.GroupedMessagePosition) this.posArray.get(i11)).aspectRatio);
                    } else {
                        fArr[i11] = Math.min(1.0f, ((MessageObject.GroupedMessagePosition) this.posArray.get(i11)).aspectRatio);
                    }
                    fArr[i11] = Math.max(0.66667f, Math.min(1.7f, fArr[i11]));
                }
                ArrayList arrayList2 = new ArrayList();
                for (int i12 = 1; i12 < size2; i12++) {
                    int i13 = size2 - i12;
                    if (i12 <= 3 && i13 <= 3) {
                        arrayList2.add(new MessageGroupedLayoutAttempt(i12, i13, multiHeight(fArr, 0, i12), multiHeight(fArr, i12, size2)));
                    }
                }
                for (int i14 = 1; i14 < size2 - 1; i14++) {
                    int i15 = 1;
                    while (true) {
                        int i16 = size2 - i14;
                        if (i15 < i16) {
                            int i17 = i16 - i15;
                            if (i14 <= 3) {
                                if (i15 <= (f10 < 0.85f ? 4 : 3) && i17 <= 3) {
                                    int i18 = i14 + i15;
                                    arrayList2.add(new MessageGroupedLayoutAttempt(i14, i15, i17, multiHeight(fArr, 0, i14), multiHeight(fArr, i14, i18), multiHeight(fArr, i18, size2)));
                                }
                            }
                            i15++;
                        }
                    }
                }
                for (int i19 = 1; i19 < size2 - 2; i19++) {
                    int i20 = 1;
                    while (true) {
                        int i21 = size2 - i19;
                        if (i20 < i21) {
                            int i22 = 1;
                            while (true) {
                                int i23 = i21 - i20;
                                if (i22 < i23) {
                                    int i24 = i23 - i22;
                                    if (i19 <= 3 && i20 <= 3 && i22 <= 3 && i24 <= 3) {
                                        int i25 = i19 + i20;
                                        int i26 = i25 + i22;
                                        arrayList2.add(new MessageGroupedLayoutAttempt(i19, i20, i22, i24, multiHeight(fArr, 0, i19), multiHeight(fArr, i19, i25), multiHeight(fArr, i25, i26), multiHeight(fArr, i26, size2)));
                                    }
                                    i22++;
                                }
                            }
                            i20++;
                        }
                    }
                }
                float f13 = (this.maxSizeWidth / 3) * 4;
                int i27 = 0;
                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt = null;
                float f14 = 0.0f;
                while (i27 < arrayList2.size()) {
                    MessageGroupedLayoutAttempt messageGroupedLayoutAttempt2 = (MessageGroupedLayoutAttempt) arrayList2.get(i27);
                    int i28 = 0;
                    float f15 = Float.MAX_VALUE;
                    float f16 = 0.0f;
                    while (true) {
                        float[] fArr2 = messageGroupedLayoutAttempt2.heights;
                        if (i28 >= fArr2.length) {
                            break;
                        }
                        float f17 = fArr2[i28];
                        f16 += f17;
                        if (f17 < f15) {
                            f15 = f17;
                        }
                        i28++;
                    }
                    float abs = Math.abs(f16 - f13);
                    int[] iArr = messageGroupedLayoutAttempt2.lineCounts;
                    float f18 = f13;
                    if (iArr.length > 1) {
                        int i29 = iArr[0];
                        int i30 = iArr[1];
                        if (i29 <= i30) {
                            if (iArr.length > 2 && i30 > iArr[2]) {
                                f = 1.2f;
                                abs *= f;
                            } else if (iArr.length <= 3 || iArr[2] <= iArr[3]) {
                            }
                        }
                        f = 1.2f;
                        abs *= f;
                    }
                    if (f15 < min) {
                        abs *= 1.5f;
                    }
                    if (messageGroupedLayoutAttempt == null || abs < f14) {
                        messageGroupedLayoutAttempt = messageGroupedLayoutAttempt2;
                        f14 = abs;
                    }
                    i27++;
                    f13 = f18;
                }
                if (messageGroupedLayoutAttempt == null) {
                    return;
                }
                int i31 = 0;
                int i32 = 0;
                while (true) {
                    int[] iArr2 = messageGroupedLayoutAttempt.lineCounts;
                    if (i31 >= iArr2.length) {
                        break;
                    }
                    int i33 = iArr2[i31];
                    float f19 = messageGroupedLayoutAttempt.heights[i31];
                    int i34 = this.maxSizeWidth;
                    int i35 = i33 - 1;
                    this.maxX = Math.max(this.maxX, i35);
                    int i36 = i34;
                    MessageObject.GroupedMessagePosition groupedMessagePosition4 = null;
                    for (int i37 = 0; i37 < i33; i37++) {
                        int i38 = (int) (fArr[i32] * f19);
                        i36 -= i38;
                        MessageObject.GroupedMessagePosition groupedMessagePosition5 = (MessageObject.GroupedMessagePosition) this.posArray.get(i32);
                        int i39 = i31 == 0 ? 4 : 0;
                        if (i31 == messageGroupedLayoutAttempt.lineCounts.length - 1) {
                            i39 |= 8;
                        }
                        if (i37 == 0) {
                            i39 |= 1;
                        }
                        if (i37 == i35) {
                            i = i39 | 2;
                            groupedMessagePosition4 = groupedMessagePosition5;
                        } else {
                            i = i39;
                        }
                        groupedMessagePosition5.set(i37, i37, i31, i31, i38, Math.max(dp4, f19 / this.maxSizeHeight), i);
                        i32++;
                    }
                    groupedMessagePosition4.pw += i36;
                    groupedMessagePosition4.spanSize += i36;
                    i31++;
                }
            } else {
                if (size == 2) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition6 = (MessageObject.GroupedMessagePosition) this.posArray.get(0);
                    MessageObject.GroupedMessagePosition groupedMessagePosition7 = (MessageObject.GroupedMessagePosition) this.posArray.get(1);
                    String sb2 = sb.toString();
                    if (sb2.equals("ww")) {
                        double d = f10;
                        double d2 = f9;
                        Double.isNaN(d2);
                        if (d > d2 * 1.4d) {
                            float f20 = groupedMessagePosition6.aspectRatio;
                            float f21 = groupedMessagePosition7.aspectRatio;
                            if (f20 - f21 < 0.2d) {
                                float f22 = this.maxSizeWidth;
                                float round = Math.round(Math.min(f22 / f20, Math.min(f22 / f21, this.maxSizeHeight / 2.0f))) / this.maxSizeHeight;
                                groupedMessagePosition6.set(0, 0, 0, 0, this.maxSizeWidth, round, 7);
                                groupedMessagePosition7.set(0, 0, 1, 1, this.maxSizeWidth, round, 11);
                            }
                        }
                    }
                    if (sb2.equals("ww") || sb2.equals("qq")) {
                        int i40 = this.maxSizeWidth / 2;
                        float f23 = i40;
                        i2 = 0;
                        i6 = 0;
                        i7 = i40;
                        f2 = Math.round(Math.min(f23 / groupedMessagePosition6.aspectRatio, Math.min(f23 / groupedMessagePosition7.aspectRatio, this.maxSizeHeight))) / this.maxSizeHeight;
                        groupedMessagePosition6.set(0, 0, 0, 0, i7, f2, 13);
                        i3 = 14;
                        i4 = 1;
                        i5 = 1;
                        groupedMessagePosition = groupedMessagePosition7;
                    } else {
                        float f24 = this.maxSizeWidth;
                        float f25 = groupedMessagePosition6.aspectRatio;
                        int max = (int) Math.max(0.4f * f24, Math.round((f24 / f25) / ((1.0f / f25) + (1.0f / groupedMessagePosition7.aspectRatio))));
                        int i41 = this.maxSizeWidth - max;
                        if (i41 < min) {
                            max -= min - i41;
                        } else {
                            min = i41;
                        }
                        i2 = 0;
                        i6 = 0;
                        f2 = Math.min(this.maxSizeHeight, Math.round(Math.min(min / groupedMessagePosition6.aspectRatio, max / groupedMessagePosition7.aspectRatio))) / this.maxSizeHeight;
                        groupedMessagePosition6.set(0, 0, 0, 0, min, f2, 13);
                        i3 = 14;
                        i4 = 1;
                        i5 = 1;
                        groupedMessagePosition = groupedMessagePosition7;
                        i7 = max;
                    }
                    groupedMessagePosition.set(i4, i5, i6, i2, i7, f2, i3);
                } else {
                    if (size == 3) {
                        MessageObject.GroupedMessagePosition groupedMessagePosition8 = (MessageObject.GroupedMessagePosition) this.posArray.get(0);
                        MessageObject.GroupedMessagePosition groupedMessagePosition9 = (MessageObject.GroupedMessagePosition) this.posArray.get(1);
                        MessageObject.GroupedMessagePosition groupedMessagePosition10 = (MessageObject.GroupedMessagePosition) this.posArray.get(2);
                        if (sb.charAt(0) == 'n') {
                            float f26 = this.maxSizeHeight * 0.5f;
                            float f27 = groupedMessagePosition9.aspectRatio;
                            float min3 = Math.min(f26, Math.round((this.maxSizeWidth * f27) / (groupedMessagePosition10.aspectRatio + f27)));
                            float f28 = this.maxSizeHeight - min3;
                            int max2 = (int) Math.max(min, Math.min(this.maxSizeWidth * 0.5f, Math.round(Math.min(groupedMessagePosition10.aspectRatio * min3, groupedMessagePosition9.aspectRatio * f28))));
                            int round2 = Math.round(Math.min((this.maxSizeHeight * groupedMessagePosition8.aspectRatio) + i10, this.maxSizeWidth - max2));
                            groupedMessagePosition8.set(0, 0, 0, 1, round2, 1.0f, 13);
                            groupedMessagePosition9.set(1, 1, 0, 0, max2, f28 / this.maxSizeHeight, 6);
                            groupedMessagePosition10.set(1, 1, 1, 1, max2, min3 / this.maxSizeHeight, 10);
                            int i42 = this.maxSizeWidth;
                            groupedMessagePosition10.spanSize = i42;
                            float f29 = this.maxSizeHeight;
                            groupedMessagePosition8.siblingHeights = new float[]{min3 / f29, f28 / f29};
                            groupedMessagePosition9.spanSize = i42 - round2;
                            groupedMessagePosition10.leftSpanOffset = round2;
                        } else {
                            float round3 = Math.round(Math.min(this.maxSizeWidth / groupedMessagePosition8.aspectRatio, this.maxSizeHeight * 0.66f)) / this.maxSizeHeight;
                            groupedMessagePosition8.set(0, 1, 0, 0, this.maxSizeWidth, round3, 7);
                            int i43 = this.maxSizeWidth / 2;
                            float f30 = this.maxSizeHeight - round3;
                            float f31 = i43;
                            float min4 = Math.min(f30, Math.round(Math.min(f31 / groupedMessagePosition9.aspectRatio, f31 / groupedMessagePosition10.aspectRatio))) / this.maxSizeHeight;
                            f2 = min4 < dp4 ? dp4 : min4;
                            groupedMessagePosition9.set(0, 0, 1, 1, i43, f2, 9);
                            i2 = 1;
                            i3 = 10;
                            i4 = 1;
                            i5 = 1;
                            i6 = 1;
                            groupedMessagePosition = groupedMessagePosition10;
                            i7 = i43;
                            groupedMessagePosition.set(i4, i5, i6, i2, i7, f2, i3);
                        }
                    } else {
                        MessageObject.GroupedMessagePosition groupedMessagePosition11 = (MessageObject.GroupedMessagePosition) this.posArray.get(0);
                        MessageObject.GroupedMessagePosition groupedMessagePosition12 = (MessageObject.GroupedMessagePosition) this.posArray.get(1);
                        MessageObject.GroupedMessagePosition groupedMessagePosition13 = (MessageObject.GroupedMessagePosition) this.posArray.get(2);
                        MessageObject.GroupedMessagePosition groupedMessagePosition14 = (MessageObject.GroupedMessagePosition) this.posArray.get(3);
                        if (sb.charAt(0) == 'w') {
                            float round4 = Math.round(Math.min(this.maxSizeWidth / groupedMessagePosition11.aspectRatio, this.maxSizeHeight * 0.66f)) / this.maxSizeHeight;
                            groupedMessagePosition11.set(0, 2, 0, 0, this.maxSizeWidth, round4, 7);
                            float round5 = Math.round(this.maxSizeWidth / ((groupedMessagePosition12.aspectRatio + groupedMessagePosition13.aspectRatio) + groupedMessagePosition14.aspectRatio));
                            float f32 = min;
                            int max3 = (int) Math.max(f32, Math.min(this.maxSizeWidth * 0.4f, groupedMessagePosition12.aspectRatio * round5));
                            int max4 = (int) Math.max(Math.max(f32, this.maxSizeWidth * 0.33f), groupedMessagePosition14.aspectRatio * round5);
                            int i44 = (this.maxSizeWidth - max3) - max4;
                            if (i44 < AndroidUtilities.dp(58.0f)) {
                                int dp5 = AndroidUtilities.dp(58.0f) - i44;
                                i44 = AndroidUtilities.dp(58.0f);
                                int i45 = dp5 / 2;
                                max3 -= i45;
                                max4 -= dp5 - i45;
                            }
                            int i46 = max3;
                            float min5 = Math.min(this.maxSizeHeight - round4, round5) / this.maxSizeHeight;
                            if (min5 < dp4) {
                                min5 = dp4;
                            }
                            float f33 = min5;
                            groupedMessagePosition12.set(0, 0, 1, 1, i46, f33, 9);
                            groupedMessagePosition13.set(1, 1, 1, 1, i44, f33, 8);
                            groupedMessagePosition14.set(2, 2, 1, 1, max4, f33, 10);
                            this.maxX = 2;
                        } else {
                            int max5 = Math.max(min, Math.round(this.maxSizeHeight / (((1.0f / groupedMessagePosition12.aspectRatio) + (1.0f / groupedMessagePosition13.aspectRatio)) + (1.0f / groupedMessagePosition14.aspectRatio))));
                            float f34 = dp;
                            float f35 = max5;
                            float min6 = Math.min(0.33f, Math.max(f34, f35 / groupedMessagePosition12.aspectRatio) / this.maxSizeHeight);
                            float min7 = Math.min(0.33f, Math.max(f34, f35 / groupedMessagePosition13.aspectRatio) / this.maxSizeHeight);
                            float f36 = (1.0f - min6) - min7;
                            int round6 = Math.round(Math.min((this.maxSizeHeight * groupedMessagePosition11.aspectRatio) + i10, this.maxSizeWidth - max5));
                            groupedMessagePosition11.set(0, 0, 0, 2, round6, min6 + min7 + f36, 13);
                            groupedMessagePosition12.set(1, 1, 0, 0, max5, min6, 6);
                            groupedMessagePosition13.set(1, 1, 1, 1, max5, min7, 2);
                            groupedMessagePosition13.spanSize = this.maxSizeWidth;
                            groupedMessagePosition14.set(1, 1, 2, 2, max5, f36, 10);
                            int i47 = this.maxSizeWidth;
                            groupedMessagePosition14.spanSize = i47;
                            groupedMessagePosition12.spanSize = i47 - round6;
                            groupedMessagePosition13.leftSpanOffset = round6;
                            groupedMessagePosition14.leftSpanOffset = round6;
                            z = true;
                            groupedMessagePosition11.siblingHeights = new float[]{min6, min7, f36};
                        }
                    }
                    this.hasSibling = z;
                    r3 = z;
                }
                this.maxX = r3;
            }
            for (int i48 = 0; i48 < size; i48++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition15 = (MessageObject.GroupedMessagePosition) this.posArray.get(i48);
                if (groupedMessagePosition15.maxX == this.maxX || (groupedMessagePosition15.flags & 2) != 0) {
                    groupedMessagePosition15.spanSize += 200;
                }
                if ((groupedMessagePosition15.flags & 1) != 0) {
                    groupedMessagePosition15.edge = true;
                }
                if (groupedMessagePosition15.edge) {
                    int i49 = groupedMessagePosition15.spanSize;
                    if (i49 != 1000) {
                        groupedMessagePosition15.spanSize = i49 + 108;
                    }
                    groupedMessagePosition15.pw += 108;
                } else if ((groupedMessagePosition15.flags & 2) != 0) {
                    int i50 = groupedMessagePosition15.spanSize;
                    if (i50 != 1000) {
                        groupedMessagePosition15.spanSize = i50 - 108;
                    } else {
                        int i51 = groupedMessagePosition15.leftSpanOffset;
                        if (i51 != 0) {
                            groupedMessagePosition15.leftSpanOffset = i51 + 108;
                        }
                    }
                }
            }
            for (int i52 = 0; i52 < size; i52++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition16 = (MessageObject.GroupedMessagePosition) this.posArray.get(i52);
                if (groupedMessagePosition16.minX == 0) {
                    groupedMessagePosition16.spanSize += 200;
                }
                if ((groupedMessagePosition16.flags & 2) != 0) {
                    groupedMessagePosition16.edge = true;
                }
                this.maxX = Math.max(this.maxX, (int) groupedMessagePosition16.maxX);
                this.maxY = Math.max(this.maxY, (int) groupedMessagePosition16.maxY);
                groupedMessagePosition16.left = getLeft(groupedMessagePosition16, groupedMessagePosition16.minY, groupedMessagePosition16.maxY, groupedMessagePosition16.minX);
            }
            for (int i53 = 0; i53 < size; i53++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition17 = (MessageObject.GroupedMessagePosition) this.posArray.get(i53);
                groupedMessagePosition17.top = getTop(groupedMessagePosition17, groupedMessagePosition17.minY);
            }
            this.width = getWidth();
            this.height = getHeight();
        }

        public float getHeight() {
            float[] fArr = new float[10];
            Arrays.fill(fArr, 0.0f);
            int size = this.posArray.size();
            for (int i = 0; i < size; i++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = (MessageObject.GroupedMessagePosition) this.posArray.get(i);
                float f = groupedMessagePosition.ph;
                for (int i2 = groupedMessagePosition.minX; i2 <= groupedMessagePosition.maxX; i2++) {
                    fArr[i2] = fArr[i2] + f;
                }
            }
            float f2 = fArr[0];
            for (int i3 = 1; i3 < 10; i3++) {
                float f3 = fArr[i3];
                if (f2 < f3) {
                    f2 = f3;
                }
            }
            return f2;
        }

        public MessageObject.GroupedMessagePosition getPosition(TLRPC.MessageExtendedMedia messageExtendedMedia) {
            if (messageExtendedMedia == null) {
                return null;
            }
            return (MessageObject.GroupedMessagePosition) this.positions.get(messageExtendedMedia);
        }

        public int getWidth() {
            int[] iArr = new int[10];
            Arrays.fill(iArr, 0);
            int size = this.posArray.size();
            for (int i = 0; i < size; i++) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = (MessageObject.GroupedMessagePosition) this.posArray.get(i);
                int i2 = groupedMessagePosition.pw;
                for (int i3 = groupedMessagePosition.minY; i3 <= groupedMessagePosition.maxY; i3++) {
                    iArr[i3] = iArr[i3] + i2;
                }
            }
            int i4 = iArr[0];
            for (int i5 = 1; i5 < 10; i5++) {
                int i6 = iArr[i5];
                if (i4 < i6) {
                    i4 = i6;
                }
            }
            return i4;
        }
    }

    public static class MediaHolder implements DownloadController.FileDownloadProgressListener {
        private final int TAG;
        public boolean album;
        public String attachPath;
        public boolean attached;
        public boolean autoplay;
        public int b;
        public final ChatMessageCell cell;
        private int duration;
        private Text durationText;
        private int durationValue;
        public String filename;
        private final int h;
        public boolean hidden;
        public int icon;
        public final ImageReceiver imageReceiver;
        public int l;
        public TLRPC.MessageExtendedMedia media;
        public int r;
        public final RadialProgress2 radialProgress;
        public int t;
        public boolean video;
        private final int w;
        public final float[] radii = new float[8];
        public final RectF clipRect = new RectF();
        public final Path clipPath = new Path();

        public MediaHolder(org.telegram.ui.Cells.ChatMessageCell r7, org.telegram.messenger.MessageObject r8, org.telegram.tgnet.TLRPC.MessageExtendedMedia r9, boolean r10, int r11, int r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupMedia.MediaHolder.<init>(org.telegram.ui.Cells.ChatMessageCell, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$MessageExtendedMedia, boolean, int, int):void");
        }

        public int getDefaultIcon() {
            return (!this.video || this.autoplay) ? 4 : 0;
        }

        public void attach() {
            if (this.attached) {
                return;
            }
            this.attached = true;
            this.imageReceiver.onAttachedToWindow();
        }

        public void detach() {
            if (this.attached) {
                this.attached = false;
                this.imageReceiver.onDetachedFromWindow();
            }
        }

        @Override
        public int getObserverTag() {
            return this.TAG;
        }

        @Override
        public void onFailedDownload(String str, boolean z) {
        }

        @Override
        public void onProgressDownload(String str, long j, long j2) {
            float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
            RadialProgress2 radialProgress2 = this.radialProgress;
            this.media.downloadProgress = min;
            radialProgress2.setProgress(min, true);
            setIcon(min < 1.0f ? 3 : getDefaultIcon());
            this.cell.invalidate();
        }

        @Override
        public void onProgressUpload(String str, long j, long j2, boolean z) {
            float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
            RadialProgress2 radialProgress2 = this.radialProgress;
            this.media.uploadProgress = min;
            radialProgress2.setProgress(min, true);
            setIcon(min < 1.0f ? 3 : this.album ? 6 : getDefaultIcon());
            this.cell.invalidate();
        }

        @Override
        public void onSuccessDownload(String str) {
        }

        public void setIcon(int i) {
            if (i != this.icon) {
                RadialProgress2 radialProgress2 = this.radialProgress;
                this.icon = i;
                radialProgress2.setIcon(i, true, true);
            }
        }

        public void setTime(int i) {
            int max;
            if (this.video || this.durationValue == (max = Math.max(0, this.duration - i))) {
                return;
            }
            this.durationValue = max;
            this.durationText = new Text(AndroidUtilities.formatLongDuration(max), 12.0f);
        }

        public void updateMedia(TLRPC.MessageExtendedMedia messageExtendedMedia, MessageObject messageObject) {
            ImageLocation forDocument;
            ImageLocation forDocument2;
            TLRPC.Document document;
            if (this.media == messageExtendedMedia) {
                return;
            }
            this.media = messageExtendedMedia;
            this.autoplay = false;
            String str = this.w + "_" + this.h;
            if (messageExtendedMedia instanceof TLRPC.TL_messageExtendedMediaPreview) {
                this.hidden = true;
                this.filename = null;
                this.imageReceiver.setImage(ImageLocation.getForObject(((TLRPC.TL_messageExtendedMediaPreview) messageExtendedMedia).thumb, messageObject.messageOwner), str + "_b2", null, null, messageObject, 0);
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(1.4f);
                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, -0.1f);
                this.imageReceiver.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                return;
            }
            if (messageExtendedMedia instanceof TLRPC.TL_messageExtendedMedia) {
                boolean z = messageObject.isRepostPreview;
                this.hidden = z;
                if (z) {
                    str = str + "_b3";
                }
                String str2 = str;
                this.imageReceiver.setColorFilter(null);
                TLRPC.MessageMedia messageMedia = ((TLRPC.TL_messageExtendedMedia) messageExtendedMedia).media;
                this.filename = MessageObject.getFileName(messageMedia);
                if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                    TLRPC.TL_messageMediaPhoto tL_messageMediaPhoto = (TLRPC.TL_messageMediaPhoto) messageMedia;
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_messageMediaPhoto.photo.sizes, AndroidUtilities.getPhotoSize(), true, null, true);
                    TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tL_messageMediaPhoto.photo.sizes, Math.min(this.w, this.h) / 100, false, closestPhotoSizeWithSize, false);
                    forDocument = ImageLocation.getForPhoto(closestPhotoSizeWithSize, tL_messageMediaPhoto.photo);
                    forDocument2 = ImageLocation.getForPhoto(closestPhotoSizeWithSize2, tL_messageMediaPhoto.photo);
                } else {
                    if (!(messageMedia instanceof TLRPC.TL_messageMediaDocument)) {
                        return;
                    }
                    TLRPC.TL_messageMediaDocument tL_messageMediaDocument = (TLRPC.TL_messageMediaDocument) messageMedia;
                    this.autoplay = !this.hidden && !this.album && this.video && SharedConfig.isAutoplayVideo();
                    if (!this.album && this.video && (document = tL_messageMediaDocument.document) != null) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, AndroidUtilities.getPhotoSize(), true, null, true);
                        TLRPC.PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(tL_messageMediaDocument.document.thumbs, Math.min(this.w, this.h), false, closestPhotoSizeWithSize3, false);
                        ImageLocation forDocument3 = ImageLocation.getForDocument(tL_messageMediaDocument.document);
                        ImageLocation forDocument4 = ImageLocation.getForDocument(closestPhotoSizeWithSize3, tL_messageMediaDocument.document);
                        ImageLocation forDocument5 = ImageLocation.getForDocument(closestPhotoSizeWithSize4, tL_messageMediaDocument.document);
                        ImageReceiver imageReceiver = this.imageReceiver;
                        ImageLocation imageLocation = this.autoplay ? forDocument3 : null;
                        StringBuilder sb = new StringBuilder();
                        sb.append(str2);
                        sb.append(this.autoplay ? "_g" : "");
                        imageReceiver.setImage(imageLocation, sb.toString(), forDocument4, str2, forDocument5, str2, null, 0L, null, messageObject, 0);
                        return;
                    }
                    TLRPC.Document document2 = tL_messageMediaDocument.document;
                    if (document2 == null) {
                        return;
                    }
                    TLRPC.PhotoSize closestPhotoSizeWithSize5 = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, AndroidUtilities.getPhotoSize(), true, null, true);
                    TLRPC.PhotoSize closestPhotoSizeWithSize6 = FileLoader.getClosestPhotoSizeWithSize(tL_messageMediaDocument.document.thumbs, Math.min(this.w, this.h), false, closestPhotoSizeWithSize5, false);
                    forDocument = ImageLocation.getForDocument(closestPhotoSizeWithSize5, tL_messageMediaDocument.document);
                    forDocument2 = ImageLocation.getForDocument(closestPhotoSizeWithSize6, tL_messageMediaDocument.document);
                }
                this.imageReceiver.setImage(forDocument, str2, forDocument2, str2, 0L, null, messageObject, 0);
            }
        }
    }

    public GroupMedia(ChatMessageCell chatMessageCell) {
        this.cell = chatMessageCell;
        this.spoilerEffect = SpoilerEffect2.getInstance(chatMessageCell);
        this.animatedHidden = new AnimatedFloat(chatMessageCell, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.bounce = new ButtonBounce(chatMessageCell);
    }

    public boolean allVisible() {
        Iterator it = this.holders.iterator();
        while (it.hasNext()) {
            if (!((MediaHolder) it.next()).imageReceiver.getVisible()) {
                return false;
            }
        }
        return true;
    }

    public void checkBlurBitmap() {
        int id = this.cell.getMessageObject() != null ? this.cell.getMessageObject().getId() : 0;
        int i = this.width;
        int i2 = this.height;
        int max = (int) Math.max(1.0f, i > i2 ? 100.0f : (i / i2) * 100.0f);
        int i3 = this.height;
        int i4 = this.width;
        int max2 = (int) Math.max(1.0f, i3 <= i4 ? 100.0f * (i3 / i4) : 100.0f);
        int i5 = 0;
        for (int i6 = 0; i6 < this.holders.size(); i6++) {
            MediaHolder mediaHolder = (MediaHolder) this.holders.get(i6);
            if (mediaHolder.imageReceiver.hasImageSet() && mediaHolder.imageReceiver.getBitmap() != null) {
                i5 |= 1 << i6;
            }
        }
        Bitmap bitmap = this.blurBitmap;
        if (bitmap != null && this.blurBitmapMessageId == id && this.blurBitmapState == i5 && this.blurBitmapWidth == max && this.blurBitmapHeight == max2) {
            return;
        }
        this.blurBitmapState = i5;
        this.blurBitmapMessageId = id;
        this.blurBitmapWidth = max;
        this.blurBitmapHeight = max2;
        if (bitmap != null) {
            bitmap.recycle();
        }
        this.blurBitmap = Bitmap.createBitmap(max, max2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.blurBitmap);
        float f = max / this.width;
        canvas.scale(f, f);
        for (int i7 = 0; i7 < this.holders.size(); i7++) {
            MediaHolder mediaHolder2 = (MediaHolder) this.holders.get(i7);
            mediaHolder2.imageReceiver.setImageCoords(mediaHolder2.l, mediaHolder2.t, mediaHolder2.r - r4, mediaHolder2.b - r6);
            mediaHolder2.imageReceiver.draw(canvas);
        }
        Utilities.stackBlurBitmap(this.blurBitmap, 12);
        if (this.blurBitmapPaint == null) {
            this.blurBitmapPaint = new Paint(3);
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1.5f);
            this.blurBitmapPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        }
    }

    public void draw(Canvas canvas) {
        if (this.layout == null) {
            return;
        }
        float f = this.animatedHidden.set(this.hidden);
        drawImages(canvas, true);
        if (this.buttonText != null && f > 0.0f) {
            float scale = this.bounce.getScale(0.05f);
            float dp = AndroidUtilities.dp(28.0f) + this.buttonText.getCurrentWidth();
            float dp2 = AndroidUtilities.dp(32.0f);
            RectF rectF = this.clipRect;
            float f2 = this.x;
            float f3 = this.width;
            float f4 = this.y;
            float f5 = this.height;
            rectF.set(((f3 - dp) / 2.0f) + f2, ((f5 - dp2) / 2.0f) + f4, f2 + ((f3 + dp) / 2.0f), f4 + ((f5 + dp2) / 2.0f));
            this.clipPath.rewind();
            float f6 = dp2 / 2.0f;
            this.clipPath.addRoundRect(this.clipRect, f6, f6, Path.Direction.CW);
            canvas.save();
            canvas.scale(scale, scale, this.x + (this.width / 2.0f), this.y + (this.height / 2.0f));
            canvas.save();
            canvas.clipPath(this.clipPath);
            drawBlurred(canvas, f);
            canvas.drawColor(Theme.multAlpha(1342177280, f));
            this.buttonText.draw(canvas, ((this.x + (this.width / 2.0f)) - (dp / 2.0f)) + AndroidUtilities.dp(14.0f), (this.height / 2.0f) + this.y, -1, f);
            canvas.restore();
            if (isLoading()) {
                LoadingDrawable loadingDrawable = this.loadingDrawable;
                if (loadingDrawable == null) {
                    LoadingDrawable loadingDrawable2 = new LoadingDrawable();
                    this.loadingDrawable = loadingDrawable2;
                    loadingDrawable2.setCallback(this.cell);
                    this.loadingDrawable.setColors(Theme.multAlpha(-1, 0.1f), Theme.multAlpha(-1, 0.3f), Theme.multAlpha(-1, 0.35f), Theme.multAlpha(-1, 0.8f));
                    this.loadingDrawable.setAppearByGradient(true);
                    this.loadingDrawable.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.25f));
                } else if (loadingDrawable.isDisappeared() || this.loadingDrawable.isDisappearing()) {
                    this.loadingDrawable.reset();
                    this.loadingDrawable.resetDisappear();
                }
            } else {
                LoadingDrawable loadingDrawable3 = this.loadingDrawable;
                if (loadingDrawable3 != null && !loadingDrawable3.isDisappearing() && !this.loadingDrawable.isDisappeared()) {
                    this.loadingDrawable.disappear();
                }
            }
            LoadingDrawable loadingDrawable4 = this.loadingDrawable;
            if (loadingDrawable4 != null) {
                loadingDrawable4.setBounds(this.clipRect);
                this.loadingDrawable.setRadiiDp(f6);
                this.loadingDrawable.setAlpha((int) (255.0f * f));
                this.loadingDrawable.draw(canvas);
            }
            canvas.restore();
        }
        if (this.priceText == null || f >= 1.0f || !allVisible()) {
            return;
        }
        float timeAlpha = (1.0f - f) * this.cell.getTimeAlpha();
        float dp3 = AndroidUtilities.dp(11.32f) + this.priceText.getCurrentWidth();
        float dp4 = AndroidUtilities.dp(17.0f);
        float dp5 = AndroidUtilities.dp(5.0f);
        RectF rectF2 = this.clipRect;
        float f7 = this.x + this.width;
        float f8 = this.y + dp5;
        rectF2.set((f7 - dp3) - dp5, f8, f7 - dp5, f8 + dp4);
        this.clipPath.rewind();
        float f9 = dp4 / 2.0f;
        this.clipPath.addRoundRect(this.clipRect, f9, f9, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.clipPath);
        canvas.drawColor(Theme.multAlpha(1073741824, timeAlpha));
        this.priceText.draw(canvas, (((this.x + this.width) - dp3) - dp5) + AndroidUtilities.dp(5.66f), this.y + dp5 + f9, -1, timeAlpha);
        canvas.restore();
    }

    public void drawBlurRect(Canvas canvas, RectF rectF, float f, float f2) {
        canvas.save();
        this.clipPath.rewind();
        this.clipPath.addRoundRect(rectF, f, f, Path.Direction.CW);
        canvas.clipPath(this.clipPath);
        canvas.drawColor(1073741824);
        canvas.restore();
    }

    public void drawBlurred(Canvas canvas, float f) {
        if (this.layout == null) {
            return;
        }
        checkBlurBitmap();
        if (this.blurBitmap != null) {
            canvas.save();
            canvas.translate(this.x, this.y);
            canvas.scale(this.width / this.blurBitmap.getWidth(), this.width / this.blurBitmap.getWidth());
            this.blurBitmapPaint.setAlpha((int) (f * 255.0f));
            canvas.drawBitmap(this.blurBitmap, 0.0f, 0.0f, this.blurBitmapPaint);
            canvas.restore();
        }
    }

    public void drawImages(Canvas canvas, boolean z) {
        int i;
        float f = this.animatedHidden.set(this.hidden);
        MessageObject messageObject = this.cell.getMessageObject();
        this.clipPath2.rewind();
        float f2 = Float.MAX_VALUE;
        float f3 = Float.MAX_VALUE;
        float f4 = Float.MIN_VALUE;
        float f5 = Float.MIN_VALUE;
        int i2 = 0;
        while (i2 < this.holders.size()) {
            MediaHolder mediaHolder = (MediaHolder) this.holders.get(i2);
            ImageReceiver imageReceiver = mediaHolder.imageReceiver;
            int i3 = this.x;
            int i4 = mediaHolder.l;
            int i5 = this.y;
            int i6 = mediaHolder.t;
            imageReceiver.setImageCoords(i3 + i4, i5 + i6, mediaHolder.r - i4, mediaHolder.b - i6);
            mediaHolder.imageReceiver.draw(canvas);
            if (mediaHolder.imageReceiver.getAnimation() != null) {
                mediaHolder.setTime(Math.round(((float) mediaHolder.imageReceiver.getAnimation().currentTime) / 1000.0f));
            }
            if (f > 0.0f) {
                f3 = Math.min(this.x + mediaHolder.l, f3);
                f2 = Math.min(this.y + mediaHolder.t, f2);
                f5 = Math.max(this.x + mediaHolder.r, f5);
                f4 = Math.max(this.y + mediaHolder.b, f4);
                RectF rectF = AndroidUtilities.rectTmp;
                float f6 = mediaHolder.l + this.x;
                int i7 = this.y;
                rectF.set(f6, mediaHolder.t + i7, r11 + mediaHolder.r, i7 + mediaHolder.b);
                this.clipPath2.addRoundRect(rectF, mediaHolder.radii, Path.Direction.CW);
            }
            mediaHolder.radialProgress.setColorKeys(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
            float f7 = f2;
            mediaHolder.radialProgress.setProgressRect(mediaHolder.imageReceiver.getImageX() + ((mediaHolder.imageReceiver.getImageWidth() / 2.0f) - mediaHolder.radialProgress.getRadius()), mediaHolder.imageReceiver.getImageY() + ((mediaHolder.imageReceiver.getImageHeight() / 2.0f) - mediaHolder.radialProgress.getRadius()), mediaHolder.imageReceiver.getImageX() + (mediaHolder.imageReceiver.getImageWidth() / 2.0f) + mediaHolder.radialProgress.getRadius(), mediaHolder.imageReceiver.getImageY() + (mediaHolder.imageReceiver.getImageHeight() / 2.0f) + mediaHolder.radialProgress.getRadius());
            if (messageObject.isSending()) {
                SendMessagesHelper sendMessagesHelper = SendMessagesHelper.getInstance(messageObject.currentAccount);
                long[] fileProgressSizes = ImageLoader.getInstance().getFileProgressSizes(mediaHolder.attachPath);
                boolean isSendingPaidMessage = sendMessagesHelper.isSendingPaidMessage(messageObject.getId(), i2);
                if (fileProgressSizes == null && isSendingPaidMessage) {
                    mediaHolder.radialProgress.setProgress(1.0f, true);
                    if (mediaHolder.album) {
                        i = 6;
                        mediaHolder.setIcon(i);
                    }
                    i = mediaHolder.getDefaultIcon();
                    mediaHolder.setIcon(i);
                }
                canvas.saveLayerAlpha(mediaHolder.radialProgress.getProgressRect(), (int) ((1.0f - f) * 255.0f), 31);
                mediaHolder.radialProgress.draw(canvas);
                canvas.restore();
                i2++;
                f2 = f7;
            } else {
                if (FileLoader.getInstance(messageObject.currentAccount).isLoadingFile(mediaHolder.filename)) {
                    i = 3;
                    mediaHolder.setIcon(i);
                    canvas.saveLayerAlpha(mediaHolder.radialProgress.getProgressRect(), (int) ((1.0f - f) * 255.0f), 31);
                    mediaHolder.radialProgress.draw(canvas);
                    canvas.restore();
                    i2++;
                    f2 = f7;
                }
                i = mediaHolder.getDefaultIcon();
                mediaHolder.setIcon(i);
                canvas.saveLayerAlpha(mediaHolder.radialProgress.getProgressRect(), (int) ((1.0f - f) * 255.0f), 31);
                mediaHolder.radialProgress.draw(canvas);
                canvas.restore();
                i2++;
                f2 = f7;
            }
        }
        if (f > 0.0f && z) {
            canvas.save();
            canvas.clipPath(this.clipPath2);
            canvas.translate(f3, f2);
            int i8 = (int) (f5 - f3);
            int i9 = (int) (f4 - f2);
            canvas.saveLayerAlpha(0.0f, 0.0f, i8, i9, (int) (255.0f * f), 31);
            SpoilerEffect2 spoilerEffect2 = this.spoilerEffect;
            ChatMessageCell chatMessageCell = this.cell;
            spoilerEffect2.draw(canvas, chatMessageCell, i8, i9, 1.0f, chatMessageCell.drawingToBitmap);
            canvas.restore();
            canvas.restore();
            this.cell.invalidate();
        }
        for (int i10 = 0; i10 < this.holders.size(); i10++) {
            MediaHolder mediaHolder2 = (MediaHolder) this.holders.get(i10);
            if (mediaHolder2.durationText != null) {
                float dp = AndroidUtilities.dp(11.4f) + mediaHolder2.durationText.getCurrentWidth();
                float dp2 = AndroidUtilities.dp(17.0f);
                float dp3 = AndroidUtilities.dp(5.0f);
                float f8 = this.x + mediaHolder2.l + dp3;
                float f9 = this.y + mediaHolder2.t + dp3;
                this.clipRect.set(f8, f9, dp + f8, f9 + dp2);
                if (this.priceText == null || this.clipRect.right <= ((this.x + this.width) - (AndroidUtilities.dp(11.32f) + this.priceText.getCurrentWidth())) - dp3 || this.clipRect.top > this.y + dp3) {
                    this.clipPath.rewind();
                    float f10 = dp2 / 2.0f;
                    this.clipPath.addRoundRect(this.clipRect, f10, f10, Path.Direction.CW);
                    canvas.save();
                    canvas.clipPath(this.clipPath);
                    drawBlurred(canvas, f);
                    canvas.drawColor(Theme.multAlpha(1073741824, 1.0f));
                    mediaHolder2.durationText.draw(canvas, this.x + mediaHolder2.l + dp3 + AndroidUtilities.dp(5.66f), this.y + mediaHolder2.t + dp3 + f10, -1, 1.0f);
                    canvas.restore();
                }
            }
        }
    }

    public MediaHolder getHolderAt(float f, float f2) {
        for (int i = 0; i < this.holders.size(); i++) {
            if (((MediaHolder) this.holders.get(i)).imageReceiver.isInsideImage(f, f2)) {
                return (MediaHolder) this.holders.get(i);
            }
        }
        return null;
    }

    public ImageReceiver getPhotoImage(int i) {
        GroupedMessages groupedMessages = this.layout;
        if (groupedMessages != null && i >= 0 && i < groupedMessages.medias.size()) {
            TLRPC.MessageExtendedMedia messageExtendedMedia = (TLRPC.MessageExtendedMedia) this.layout.medias.get(i);
            for (int i2 = 0; i2 < this.holders.size(); i2++) {
                if (((MediaHolder) this.holders.get(i2)).media == messageExtendedMedia) {
                    return ((MediaHolder) this.holders.get(i2)).imageReceiver;
                }
            }
        }
        return null;
    }

    public boolean isLoading() {
        return this.cell.getDelegate() != null && this.cell.getDelegate().isProgressLoading(this.cell, 5);
    }

    public void onAttachedToWindow() {
        if (this.attached) {
            return;
        }
        this.attached = true;
        SpoilerEffect2 spoilerEffect2 = this.spoilerEffect;
        if (spoilerEffect2 != null) {
            spoilerEffect2.detach(this.cell);
        }
        for (int i = 0; i < this.holders.size(); i++) {
            ((MediaHolder) this.holders.get(i)).attach();
        }
    }

    public void onDetachedFromWindow() {
        if (this.attached) {
            this.attached = false;
            SpoilerEffect2 spoilerEffect2 = this.spoilerEffect;
            if (spoilerEffect2 != null) {
                spoilerEffect2.attach(this.cell);
            }
            for (int i = 0; i < this.holders.size(); i++) {
                ((MediaHolder) this.holders.get(i)).detach();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            MediaHolder holderAt = getHolderAt(x, y);
            this.pressHolder = holderAt;
            this.pressButton = (holderAt == null || holderAt.radialProgress.getIcon() == 4 || !this.pressHolder.radialProgress.getProgressRect().contains(x, y)) ? false : true;
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            MediaHolder holderAt2 = getHolderAt(x, y);
            boolean z = (holderAt2 == null || holderAt2.radialProgress.getIcon() == 4 || !holderAt2.radialProgress.getProgressRect().contains(x, y)) ? false : true;
            MediaHolder mediaHolder = this.pressHolder;
            if (mediaHolder != null && mediaHolder == holderAt2 && this.cell.getDelegate() != null && motionEvent.getAction() == 1) {
                MessageObject messageObject = this.cell.getMessageObject();
                if (!this.pressButton || !z || holderAt2.radialProgress.getIcon() != 3 || messageObject == null) {
                    ChatMessageCell.ChatMessageCellDelegate delegate = this.cell.getDelegate();
                    ChatMessageCell chatMessageCell = this.cell;
                    MediaHolder mediaHolder2 = this.pressHolder;
                    delegate.didPressGroupImage(chatMessageCell, mediaHolder2.imageReceiver, mediaHolder2.media, motionEvent.getX(), motionEvent.getY());
                } else if (messageObject.isSending()) {
                    SendMessagesHelper.getInstance(messageObject.currentAccount).cancelSendingMessage(messageObject);
                }
            }
            this.pressButton = false;
            this.pressHolder = null;
        }
        this.bounce.setPressed(this.pressHolder != null);
        return this.pressHolder != null;
    }

    public void setMessageObject(org.telegram.messenger.MessageObject r13, boolean r14, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupMedia.setMessageObject(org.telegram.messenger.MessageObject, boolean, boolean):void");
    }

    public void setOverrideWidth(int i) {
        this.overrideWidth = i;
    }

    public void updateHolders(org.telegram.messenger.MessageObject r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupMedia.updateHolders(org.telegram.messenger.MessageObject):void");
    }
}
