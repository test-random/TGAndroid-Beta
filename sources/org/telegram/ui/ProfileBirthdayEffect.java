package org.telegram.ui;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;
import j$.time.LocalDate;
import j$.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ProfileBirthdayEffect;

public class ProfileBirthdayEffect extends View {
    public static String[] interactions = {"🎉", "🎆", "🎈"};
    public static String interactionsPack = "EmojiAnimations";
    public static String numbersEmojipack = "FestiveFontEmoji";
    private boolean attached;
    private boolean autoplayed;
    private final int currentAccount;
    private final long dialogId;
    private BirthdayEffectFetcher fetcher;
    private BirthdayEffectFetcher fetcherToSet;
    private boolean isPlaying;
    private long lastTime;
    private final ProfileActivity profileActivity;
    public PointF sourcePoint;
    private float t;

    public static class BirthdayEffectFetcher {
        public final int age;
        public final int currentAccount;
        private boolean detachLater;
        public ImageReceiverAsset interactionAsset;
        private boolean loaded;
        private final boolean[] setsLoaded;
        public ArrayList digitAssets = new ArrayList();
        public ArrayList allAssets = new ArrayList();
        public ArrayList loadedAssets = new ArrayList();
        private ArrayList callbacks = new ArrayList();
        public ArrayList views = new ArrayList();

        private BirthdayEffectFetcher(int i, int i2) {
            boolean[] zArr = new boolean[2];
            this.setsLoaded = zArr;
            this.currentAccount = i;
            this.age = i2;
            if (i2 <= 0) {
                zArr[0] = true;
            } else {
                final ArrayList arrayList = new ArrayList();
                final HashSet hashSet = new HashSet();
                String str = "" + i2;
                for (int i3 = 0; i3 < str.length(); i3++) {
                    int charAt = str.charAt(i3) - '0';
                    if (charAt >= 0 && charAt <= 9) {
                        arrayList.add(Integer.valueOf(charAt));
                        hashSet.add(Integer.valueOf(charAt));
                    }
                }
                TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
                tL_inputStickerSetShortName.short_name = ProfileBirthdayEffect.numbersEmojipack;
                MediaDataController.getInstance(i).getStickerSet(tL_inputStickerSetShortName, 0, false, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ProfileBirthdayEffect.BirthdayEffectFetcher.this.lambda$new$1(hashSet, arrayList, (TLRPC.TL_messages_stickerSet) obj);
                    }
                });
            }
            final String str2 = ProfileBirthdayEffect.interactions[Utilities.random.nextInt(ProfileBirthdayEffect.interactions.length)];
            TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName2 = new TLRPC.TL_inputStickerSetShortName();
            tL_inputStickerSetShortName2.short_name = ProfileBirthdayEffect.interactionsPack;
            MediaDataController.getInstance(i).getStickerSet(tL_inputStickerSetShortName2, 0, false, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    ProfileBirthdayEffect.BirthdayEffectFetcher.this.lambda$new$3(str2, (TLRPC.TL_messages_stickerSet) obj);
                }
            });
        }

        public void lambda$new$0(ImageReceiverAsset imageReceiverAsset) {
            this.loadedAssets.add(imageReceiverAsset);
            checkWhenLoaded();
        }

        public void lambda$new$1(HashSet hashSet, ArrayList arrayList, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            HashMap hashMap = new HashMap();
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                Integer num = (Integer) it.next();
                TLRPC.Document findSticker = SelectAnimatedEmojiDialog.findSticker(tL_messages_stickerSet, num + "️⃣");
                if (findSticker == null) {
                    findSticker = SelectAnimatedEmojiDialog.findSticker(tL_messages_stickerSet, num + "⃣");
                }
                if (findSticker == null) {
                    FileLog.e("couldn't find " + num + "️⃣ emoji in " + ProfileBirthdayEffect.numbersEmojipack);
                    return;
                }
                hashMap.put(num, findSticker);
            }
            HashMap hashMap2 = new HashMap();
            for (Map.Entry entry : hashMap.entrySet()) {
                Integer num2 = (Integer) entry.getKey();
                num2.intValue();
                final ImageReceiverAsset imageReceiverAsset = new ImageReceiverAsset();
                this.allAssets.add(imageReceiverAsset);
                imageReceiverAsset.setEmoji((TLRPC.Document) entry.getValue(), "80_80", tL_messages_stickerSet, new Runnable() {
                    @Override
                    public final void run() {
                        ProfileBirthdayEffect.BirthdayEffectFetcher.this.lambda$new$0(imageReceiverAsset);
                    }
                });
                imageReceiverAsset.onAttachedToWindow();
                hashMap2.put(num2, imageReceiverAsset);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                Integer num3 = (Integer) arrayList.get(i);
                num3.intValue();
                this.digitAssets.add((ImageReceiverAsset) hashMap2.get(num3));
            }
            this.setsLoaded[0] = true;
            checkWhenLoaded();
        }

        public void lambda$new$2() {
            this.loadedAssets.add(this.interactionAsset);
            checkWhenLoaded();
        }

        public void lambda$new$3(String str, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            TLRPC.Document findSticker = SelectAnimatedEmojiDialog.findSticker(tL_messages_stickerSet, str);
            if (findSticker == null) {
                FileLog.e("couldn't find " + str + " sticker in " + ProfileBirthdayEffect.interactionsPack);
                return;
            }
            ImageReceiverAsset imageReceiverAsset = new ImageReceiverAsset();
            this.interactionAsset = imageReceiverAsset;
            this.allAssets.add(imageReceiverAsset);
            int filterWidth = EmojiAnimationsOverlay.getFilterWidth();
            this.interactionAsset.setAutoRepeat(0);
            this.interactionAsset.setEmoji(findSticker, filterWidth + "_" + filterWidth + "_precache", tL_messages_stickerSet, new Runnable() {
                @Override
                public final void run() {
                    ProfileBirthdayEffect.BirthdayEffectFetcher.this.lambda$new$2();
                }
            });
            this.interactionAsset.onAttachedToWindow();
            this.setsLoaded[1] = true;
            checkWhenLoaded();
        }

        public static BirthdayEffectFetcher of(int i, TLRPC.UserFull userFull, BirthdayEffectFetcher birthdayEffectFetcher) {
            TLRPC.TL_birthday tL_birthday;
            if (!LiteMode.isEnabled(2) || !BirthdayController.isToday(userFull)) {
                if (birthdayEffectFetcher == null) {
                    return null;
                }
                birthdayEffectFetcher.detach(false);
                return null;
            }
            int years = (userFull == null || (tL_birthday = userFull.birthday) == null || (tL_birthday.flags & 1) == 0) ? 0 : Period.between(LocalDate.of(tL_birthday.year, tL_birthday.month, tL_birthday.day), LocalDate.now()).getYears();
            if (birthdayEffectFetcher != null) {
                if (birthdayEffectFetcher.age == years) {
                    return birthdayEffectFetcher;
                }
                birthdayEffectFetcher.detach(false);
            }
            return new BirthdayEffectFetcher(i, years);
        }

        public void addView(ProfileBirthdayEffect profileBirthdayEffect) {
            this.views.add(profileBirthdayEffect);
        }

        public void checkWhenLoaded() {
            if (this.loaded || this.loadedAssets.size() < this.allAssets.size()) {
                return;
            }
            boolean[] zArr = this.setsLoaded;
            if (zArr[0] && zArr[1]) {
                this.loaded = true;
                Iterator it = this.callbacks.iterator();
                while (it.hasNext()) {
                    ((Runnable) it.next()).run();
                }
                this.callbacks.clear();
            }
        }

        public void detach(boolean z) {
            if (!z && !this.views.isEmpty()) {
                this.detachLater = true;
                return;
            }
            this.callbacks.clear();
            for (int i = 0; i < this.allAssets.size(); i++) {
                ((ImageReceiverAsset) this.allAssets.get(i)).onDetachedFromWindow();
            }
            this.allAssets.clear();
        }

        public void removeView(ProfileBirthdayEffect profileBirthdayEffect) {
            this.views.remove(profileBirthdayEffect);
            if (this.views.isEmpty() && this.detachLater) {
                detach(true);
                this.detachLater = false;
            }
        }

        public void subscribe(Runnable runnable) {
            if (this.loaded) {
                runnable.run();
            } else {
                this.callbacks.add(runnable);
            }
        }
    }

    public static class ImageReceiverAsset extends ImageReceiver {

        public class AnonymousClass1 implements ImageReceiver.ImageReceiverDelegate {
            final Runnable[] val$callback;

            AnonymousClass1(Runnable[] runnableArr) {
                this.val$callback = runnableArr;
            }

            public static void lambda$didSetImage$0(Runnable[] runnableArr) {
                runnableArr[0].run();
                runnableArr[0] = null;
            }

            @Override
            public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                if (!imageReceiver.hasBitmapImage() || this.val$callback[0] == null) {
                    return;
                }
                RLottieDrawable lottieAnimation = imageReceiver.getLottieAnimation();
                if (lottieAnimation == null) {
                    this.val$callback[0].run();
                    this.val$callback[0] = null;
                } else if (lottieAnimation.isGeneratingCache()) {
                    final Runnable[] runnableArr = this.val$callback;
                    lottieAnimation.whenCacheDone = new Runnable() {
                        @Override
                        public final void run() {
                            ProfileBirthdayEffect.ImageReceiverAsset.AnonymousClass1.lambda$didSetImage$0(runnableArr);
                        }
                    };
                } else {
                    this.val$callback[0].run();
                    this.val$callback[0] = null;
                }
            }

            @Override
            public void didSetImageBitmap(int i, String str, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i, str, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        }

        private ImageReceiverAsset() {
        }

        public void setEmoji(TLRPC.Document document, String str, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, Runnable runnable) {
            setDelegate(new AnonymousClass1(new Runnable[]{runnable}));
            setImage(ImageLocation.getForDocument(document), str, null, null, tL_messages_stickerSet, 0);
        }
    }

    public ProfileBirthdayEffect(ProfileActivity profileActivity, BirthdayEffectFetcher birthdayEffectFetcher) {
        super(profileActivity.getContext());
        this.sourcePoint = new PointF();
        this.t = 1.0f;
        this.isPlaying = false;
        this.currentAccount = profileActivity.getCurrentAccount();
        this.dialogId = profileActivity.getDialogId();
        this.profileActivity = profileActivity;
        this.fetcher = birthdayEffectFetcher;
    }

    private void updateSourcePoint() {
        RecyclerListView listView = this.profileActivity.getListView();
        int i = this.profileActivity.birthdayRow;
        if (i < 0) {
            return;
        }
        for (int i2 = 0; i2 < listView.getChildCount(); i2++) {
            View childAt = listView.getChildAt(i2);
            if (i == listView.getChildAdapterPosition(childAt) && (childAt instanceof TextDetailCell)) {
                LinkSpanDrawable.LinksTextView linksTextView = ((TextDetailCell) childAt).textView;
                this.sourcePoint.set(listView.getX() + childAt.getX() + linksTextView.getX() + AndroidUtilities.dp(12.0f), listView.getY() + childAt.getY() + linksTextView.getY() + (linksTextView.getMeasuredHeight() / 2.0f));
                return;
            }
        }
    }

    public void hide() {
        animate().alpha(0.0f).setDuration(200L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.fetcher.addView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.attached) {
            for (int i = 0; i < this.fetcher.allAssets.size(); i++) {
                ((ImageReceiverAsset) this.fetcher.allAssets.get(i)).setParentView(null);
            }
            this.attached = false;
        }
        this.fetcher.removeView(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.fetcher.loaded) {
            int i = 1;
            if (!this.attached) {
                for (int i2 = 0; i2 < this.fetcher.allAssets.size(); i2++) {
                    ((ImageReceiverAsset) this.fetcher.allAssets.get(i2)).setParentView(this);
                }
                this.attached = true;
                if (!this.autoplayed) {
                    this.autoplayed = true;
                    post(new Runnable() {
                        @Override
                        public final void run() {
                            ProfileBirthdayEffect.this.lambda$onDraw$0();
                        }
                    });
                }
            }
            if (this.isPlaying) {
                long currentTimeMillis = System.currentTimeMillis();
                this.t = Utilities.clamp(this.t + (((float) Utilities.clamp(currentTimeMillis - this.lastTime, 20L, 0L)) / 4200.0f), 1.0f, 0.0f);
                this.lastTime = currentTimeMillis;
                updateSourcePoint();
                float filterWidth = EmojiAnimationsOverlay.getFilterWidth();
                this.fetcher.interactionAsset.setImageCoords((getWidth() - AndroidUtilities.dp(filterWidth)) / 2.0f, Math.max(0.0f, this.sourcePoint.y - (AndroidUtilities.dp(filterWidth) * 0.5f)), AndroidUtilities.dp(filterWidth), AndroidUtilities.dp(filterWidth));
                canvas.save();
                canvas.scale(-1.0f, 1.0f, getWidth() / 2.0f, 0.0f);
                this.fetcher.interactionAsset.draw(canvas);
                this.fetcher.interactionAsset.setAlpha(1.0f - ((this.t - 0.9f) / 0.1f));
                canvas.restore();
                int dp = AndroidUtilities.dp(110.0f);
                int size = this.fetcher.digitAssets.size() - 1;
                while (size >= 0) {
                    ImageReceiverAsset imageReceiverAsset = (ImageReceiverAsset) this.fetcher.digitAssets.get(size);
                    float f = size;
                    float cascade = AndroidUtilities.cascade(this.t, f, this.fetcher.digitAssets.size(), 1.8f);
                    float f2 = dp;
                    float f3 = 0.88f * f2;
                    float width = (getWidth() - ((this.fetcher.digitAssets.size() - i) * f3)) / 2.0f;
                    PointF pointF = this.sourcePoint;
                    float f4 = pointF.x;
                    float f5 = pointF.y;
                    float f6 = f4 + (f3 * f) + ((width - f4) * cascade);
                    float pow = f5 - ((f5 + f2) * ((float) Math.pow(this.t, 2.0d)));
                    float interpolation = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(Utilities.clamp(cascade / 0.4f, 1.0f, 0.0f));
                    float f7 = (f2 / 2.0f) * interpolation;
                    float f8 = f2 * interpolation;
                    imageReceiverAsset.setImageCoords(f6 - f7, pow - f7, f8, f8);
                    imageReceiverAsset.draw(canvas);
                    size--;
                    i = 1;
                }
                if (this.t < 1.0f) {
                    invalidate();
                    return;
                }
                this.isPlaying = false;
                updateFetcher(this.fetcherToSet);
                this.fetcherToSet = null;
            }
        }
    }

    public boolean lambda$onDraw$0() {
        if (!this.fetcher.loaded || this.t < 1.0f) {
            return false;
        }
        if (this.fetcher.interactionAsset.getLottieAnimation() != null) {
            this.fetcher.interactionAsset.getLottieAnimation().setCurrentFrame(0, false);
            this.fetcher.interactionAsset.getLottieAnimation().restart(true);
        }
        this.isPlaying = true;
        this.t = 0.0f;
        invalidate();
        return true;
    }

    public void updateFetcher(BirthdayEffectFetcher birthdayEffectFetcher) {
        if (this.fetcher == birthdayEffectFetcher || birthdayEffectFetcher == null) {
            return;
        }
        if (this.isPlaying) {
            this.fetcherToSet = birthdayEffectFetcher;
            return;
        }
        if (this.attached) {
            for (int i = 0; i < this.fetcher.allAssets.size(); i++) {
                ((ImageReceiverAsset) this.fetcher.allAssets.get(i)).setParentView(null);
            }
            this.attached = false;
        }
        this.fetcher.removeView(this);
        this.fetcher = birthdayEffectFetcher;
        if (this.attached) {
            return;
        }
        for (int i2 = 0; i2 < birthdayEffectFetcher.allAssets.size(); i2++) {
            ((ImageReceiverAsset) birthdayEffectFetcher.allAssets.get(i2)).setParentView(this);
        }
        this.attached = true;
    }
}
