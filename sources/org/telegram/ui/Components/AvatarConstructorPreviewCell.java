package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarConstructorFragment;

public abstract class AvatarConstructorPreviewCell extends FrameLayout {
    private AnimatedEmojiDrawable animatedEmojiDrawable;
    int backgroundIndex;
    private final int currentAccount;
    GradientTools currentBackgroundDrawable;
    BackupImageView currentImage;
    int emojiIndex;
    TLRPC.TL_emojiList emojiList;
    public final boolean forUser;
    private boolean isAllEmojiDrawablesLoaded;
    private AnimatedEmojiDrawable nextAnimatedEmojiDrawable;
    GradientTools nextBackgroundDrawable;
    BackupImageView nextImage;
    float progressToNext;
    Runnable scheduleSwitchToNextRunnable;
    TextView textView;

    public AvatarConstructorPreviewCell(Context context, boolean z) {
        super(context);
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.backgroundIndex = 0;
        this.emojiIndex = 0;
        this.progressToNext = 1.0f;
        this.scheduleSwitchToNextRunnable = new Runnable() {
            @Override
            public void run() {
                AndroidUtilities.runOnUIThread(AvatarConstructorPreviewCell.this.scheduleSwitchToNextRunnable, 1000L);
                TLRPC.TL_emojiList tL_emojiList = AvatarConstructorPreviewCell.this.emojiList;
                if (tL_emojiList == null || tL_emojiList.document_id.isEmpty()) {
                    return;
                }
                AvatarConstructorPreviewCell avatarConstructorPreviewCell = AvatarConstructorPreviewCell.this;
                if (avatarConstructorPreviewCell.progressToNext != 1.0f) {
                    return;
                }
                if (avatarConstructorPreviewCell.isAllEmojiDrawablesLoaded || (AvatarConstructorPreviewCell.this.nextAnimatedEmojiDrawable.getImageReceiver() != null && AvatarConstructorPreviewCell.this.nextAnimatedEmojiDrawable.getImageReceiver().hasImageLoaded())) {
                    AvatarConstructorPreviewCell avatarConstructorPreviewCell2 = AvatarConstructorPreviewCell.this;
                    int i2 = avatarConstructorPreviewCell2.emojiIndex + 1;
                    avatarConstructorPreviewCell2.emojiIndex = i2;
                    avatarConstructorPreviewCell2.backgroundIndex++;
                    if (i2 > avatarConstructorPreviewCell2.emojiList.document_id.size() - 1) {
                        AvatarConstructorPreviewCell.this.emojiIndex = 0;
                    }
                    AvatarConstructorPreviewCell avatarConstructorPreviewCell3 = AvatarConstructorPreviewCell.this;
                    int i3 = avatarConstructorPreviewCell3.backgroundIndex;
                    int[][] iArr = AvatarConstructorFragment.defaultColors;
                    if (i3 > iArr.length - 1) {
                        avatarConstructorPreviewCell3.backgroundIndex = 0;
                    }
                    int i4 = AvatarConstructorPreviewCell.this.currentAccount;
                    AvatarConstructorPreviewCell avatarConstructorPreviewCell4 = AvatarConstructorPreviewCell.this;
                    avatarConstructorPreviewCell3.animatedEmojiDrawable = new AnimatedEmojiDrawable(4, i4, avatarConstructorPreviewCell4.emojiList.document_id.get(avatarConstructorPreviewCell4.emojiIndex).longValue());
                    AvatarConstructorPreviewCell avatarConstructorPreviewCell5 = AvatarConstructorPreviewCell.this;
                    avatarConstructorPreviewCell5.nextImage.setAnimatedEmojiDrawable(avatarConstructorPreviewCell5.animatedEmojiDrawable);
                    AvatarConstructorPreviewCell avatarConstructorPreviewCell6 = AvatarConstructorPreviewCell.this;
                    int[] iArr2 = iArr[avatarConstructorPreviewCell6.backgroundIndex];
                    int i5 = iArr2[0];
                    int i6 = iArr2[1];
                    int i7 = iArr2[2];
                    int i8 = iArr2[3];
                    avatarConstructorPreviewCell6.nextBackgroundDrawable = new GradientTools();
                    AvatarConstructorPreviewCell.this.nextBackgroundDrawable.setColors(i5, i6, i7, i8);
                    AvatarConstructorPreviewCell avatarConstructorPreviewCell7 = AvatarConstructorPreviewCell.this;
                    avatarConstructorPreviewCell7.progressToNext = 0.0f;
                    avatarConstructorPreviewCell7.preloadNextEmojiDrawable();
                    AvatarConstructorPreviewCell.this.invalidate();
                }
            }
        };
        this.forUser = z;
        this.emojiList = z ? MediaDataController.getInstance(i).profileAvatarConstructorDefault : MediaDataController.getInstance(i).groupAvatarConstructorDefault;
        TLRPC.TL_emojiList tL_emojiList = this.emojiList;
        if (tL_emojiList == null || tL_emojiList.document_id.isEmpty()) {
            ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(i).getStickerSets(5);
            this.emojiList = new TLRPC.TL_emojiList();
            if (stickerSets.isEmpty()) {
                ArrayList<TLRPC.StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(i).getFeaturedEmojiSets();
                for (int i2 = 0; i2 < featuredEmojiSets.size(); i2++) {
                    TLRPC.StickerSetCovered stickerSetCovered = featuredEmojiSets.get(i2);
                    TLRPC.Document document = stickerSetCovered.cover;
                    if (document != null) {
                        this.emojiList.document_id.add(Long.valueOf(document.id));
                    } else if (stickerSetCovered instanceof TLRPC.TL_stickerSetFullCovered) {
                        TLRPC.TL_stickerSetFullCovered tL_stickerSetFullCovered = (TLRPC.TL_stickerSetFullCovered) stickerSetCovered;
                        if (!tL_stickerSetFullCovered.documents.isEmpty()) {
                            this.emojiList.document_id.add(Long.valueOf(tL_stickerSetFullCovered.documents.get(0).id));
                        }
                    }
                }
            } else {
                for (int i3 = 0; i3 < stickerSets.size(); i3++) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets.get(i3);
                    if (!tL_messages_stickerSet.documents.isEmpty()) {
                        this.emojiList.document_id.add(Long.valueOf(tL_messages_stickerSet.documents.get(Math.abs(Utilities.fastRandom.nextInt() % tL_messages_stickerSet.documents.size())).id));
                    }
                }
            }
        }
        this.currentImage = new BackupImageView(context);
        this.nextImage = new BackupImageView(context);
        addView(this.currentImage, LayoutHelper.createFrame(50, 50, 1));
        addView(this.nextImage, LayoutHelper.createFrame(50, 50, 1));
        TLRPC.TL_emojiList tL_emojiList2 = this.emojiList;
        if (tL_emojiList2 != null && !tL_emojiList2.document_id.isEmpty()) {
            AnimatedEmojiDrawable animatedEmojiDrawable = new AnimatedEmojiDrawable(4, this.currentAccount, this.emojiList.document_id.get(0).longValue());
            this.animatedEmojiDrawable = animatedEmojiDrawable;
            this.currentImage.setAnimatedEmojiDrawable(animatedEmojiDrawable);
            preloadNextEmojiDrawable();
        }
        int[] iArr = AvatarConstructorFragment.defaultColors[this.backgroundIndex];
        int i4 = iArr[0];
        int i5 = iArr[1];
        int i6 = iArr[2];
        int i7 = iArr[3];
        GradientTools gradientTools = new GradientTools();
        this.currentBackgroundDrawable = gradientTools;
        gradientTools.setColors(i4, i5, i6, i7);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 12.0f);
        this.textView.setTextColor(Theme.getColor(Theme.key_avatar_text));
        this.textView.setTypeface(AndroidUtilities.bold());
        this.textView.setGravity(17);
        this.textView.setText(LocaleController.getString(R.string.UseEmoji));
        addView(this.textView, LayoutHelper.createFrame(-1, 28.0f, 80, 10.0f, 10.0f, 10.0f, 10.0f));
    }

    public void preloadNextEmojiDrawable() {
        if (this.isAllEmojiDrawablesLoaded) {
            return;
        }
        int i = this.emojiIndex + 1;
        if (i > this.emojiList.document_id.size() - 1) {
            this.isAllEmojiDrawablesLoaded = true;
            return;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = new AnimatedEmojiDrawable(4, this.currentAccount, this.emojiList.document_id.get(i).longValue());
        this.nextAnimatedEmojiDrawable = animatedEmojiDrawable;
        animatedEmojiDrawable.preload();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        GradientTools gradientTools = this.currentBackgroundDrawable;
        if (gradientTools != null) {
            gradientTools.setBounds(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        }
        GradientTools gradientTools2 = this.nextBackgroundDrawable;
        if (gradientTools2 != null) {
            gradientTools2.setBounds(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
        }
        float f = this.progressToNext;
        if (f == 1.0f) {
            this.currentBackgroundDrawable.paint.setAlpha(255);
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.currentBackgroundDrawable.paint);
            this.currentImage.setAlpha(1.0f);
            this.currentImage.setScaleX(1.0f);
            this.currentImage.setScaleY(1.0f);
            this.nextImage.setAlpha(0.0f);
        } else {
            float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(f);
            this.currentBackgroundDrawable.paint.setAlpha(255);
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.currentBackgroundDrawable.paint);
            this.nextBackgroundDrawable.paint.setAlpha((int) (255.0f * interpolation));
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.nextBackgroundDrawable.paint);
            this.progressToNext += 0.064f;
            float f2 = 1.0f - interpolation;
            this.currentImage.setAlpha(f2);
            this.currentImage.setScaleX(f2);
            this.currentImage.setScaleY(f2);
            this.currentImage.setPivotY(0.0f);
            this.nextImage.setAlpha(interpolation);
            this.nextImage.setScaleX(interpolation);
            this.nextImage.setScaleY(interpolation);
            this.nextImage.setPivotY(r0.getMeasuredHeight());
            if (this.progressToNext > 1.0f) {
                this.progressToNext = 1.0f;
                this.currentBackgroundDrawable = this.nextBackgroundDrawable;
                BackupImageView backupImageView = this.currentImage;
                this.currentImage = this.nextImage;
                this.nextImage = backupImageView;
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    public AnimatedEmojiDrawable getAnimatedEmoji() {
        return this.animatedEmojiDrawable;
    }

    public AvatarConstructorFragment.BackgroundGradient getBackgroundGradient() {
        AvatarConstructorFragment.BackgroundGradient backgroundGradient = new AvatarConstructorFragment.BackgroundGradient();
        int[] iArr = AvatarConstructorFragment.defaultColors[this.backgroundIndex];
        backgroundGradient.color1 = iArr[0];
        backgroundGradient.color2 = iArr[1];
        backgroundGradient.color3 = iArr[2];
        backgroundGradient.color4 = iArr[3];
        return backgroundGradient;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.scheduleSwitchToNextRunnable, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.scheduleSwitchToNextRunnable);
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int top = (int) (this.textView.getTop() * 0.7f);
        int i3 = (int) ((r3 - top) * 0.7f);
        ViewGroup.LayoutParams layoutParams = this.currentImage.getLayoutParams();
        this.currentImage.getLayoutParams().height = top;
        layoutParams.width = top;
        ViewGroup.LayoutParams layoutParams2 = this.nextImage.getLayoutParams();
        this.nextImage.getLayoutParams().height = top;
        layoutParams2.width = top;
        ((FrameLayout.LayoutParams) this.currentImage.getLayoutParams()).topMargin = i3;
        ((FrameLayout.LayoutParams) this.nextImage.getLayoutParams()).topMargin = i3;
    }
}
