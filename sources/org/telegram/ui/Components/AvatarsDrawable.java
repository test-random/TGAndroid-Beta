package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;
import java.util.Random;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Stories.StoriesGradientTools;

public class AvatarsDrawable {
    private boolean attached;
    boolean centered;
    public int count;
    int currentStyle;
    public boolean drawStoriesCircle;
    public int height;
    private boolean isInCall;
    private int overrideSize;
    View parent;
    private boolean showSavedMessages;
    StoriesGradientTools storiesTools;
    private boolean transitionInProgress;
    ValueAnimator transitionProgressAnimator;
    boolean updateAfterTransition;
    Runnable updateDelegate;
    boolean wasDraw;
    public int width;
    DrawingState[] currentStates = new DrawingState[3];
    DrawingState[] animatingStates = new DrawingState[3];
    float transitionProgress = 1.0f;
    private Paint paint = new Paint(1);
    private Paint xRefP = new Paint(1);
    public int strokeWidth = AndroidUtilities.dp(1.67f);
    private float overrideSizeStepFactor = 0.8f;
    private float overrideAlpha = 1.0f;
    public long transitionDuration = 220;
    public Interpolator transitionInterpolator = CubicBezierInterpolator.DEFAULT;
    Random random = new Random();

    public static class DrawingState {
        private int animationType;
        private AvatarDrawable avatarDrawable;
        private long id;
        private ImageReceiver imageReceiver;
        private long lastSpeakTime;
        private long lastUpdateTime;
        private int moveFromIndex;
        private TLObject object;
        TLRPC.TL_groupCallParticipant participant;
        private GroupCallUserCell.AvatarWavesDrawable wavesDrawable;

        private DrawingState() {
        }
    }

    public AvatarsDrawable(View view, boolean z) {
        this.parent = view;
        for (int i = 0; i < 3; i++) {
            this.currentStates[i] = new DrawingState();
            this.currentStates[i].imageReceiver = new ImageReceiver(view);
            this.currentStates[i].imageReceiver.setInvalidateAll(true);
            this.currentStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            this.currentStates[i].avatarDrawable = new AvatarDrawable();
            this.currentStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            this.animatingStates[i] = new DrawingState();
            this.animatingStates[i].imageReceiver = new ImageReceiver(view);
            this.animatingStates[i].imageReceiver.setInvalidateAll(true);
            this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
            this.animatingStates[i].avatarDrawable = new AvatarDrawable();
            this.animatingStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        }
        this.isInCall = z;
        this.xRefP.setColor(0);
        this.xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void invalidate() {
        View view = this.parent;
        if (view != null) {
            view.invalidate();
        }
    }

    public void lambda$commitTransition$0(ValueAnimator valueAnimator) {
        this.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void swapStates() {
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr = this.currentStates;
            DrawingState drawingState = drawingStateArr[i];
            DrawingState[] drawingStateArr2 = this.animatingStates;
            drawingStateArr[i] = drawingStateArr2[i];
            drawingStateArr2[i] = drawingState;
        }
    }

    public void animateFromState(AvatarsDrawable avatarsDrawable, int i, boolean z) {
        if (avatarsDrawable == null) {
            return;
        }
        ValueAnimator valueAnimator = avatarsDrawable.transitionProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            if (this.transitionInProgress) {
                this.transitionInProgress = false;
                swapStates();
            }
        }
        TLObject[] tLObjectArr = new TLObject[3];
        for (int i2 = 0; i2 < 3; i2++) {
            tLObjectArr[i2] = this.currentStates[i2].object;
            setObject(i2, i, avatarsDrawable.currentStates[i2].object);
        }
        commitTransition(false);
        for (int i3 = 0; i3 < 3; i3++) {
            setObject(i3, i, tLObjectArr[i3]);
        }
        this.wasDraw = true;
        commitTransition(true, z);
    }

    public void commitTransition(boolean z) {
        commitTransition(z, true);
    }

    public void commitTransition(boolean z, boolean z2) {
        if (!this.wasDraw || !z) {
            this.transitionProgress = 1.0f;
            swapStates();
            return;
        }
        DrawingState[] drawingStateArr = new DrawingState[3];
        boolean z3 = false;
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr2 = this.currentStates;
            drawingStateArr[i] = drawingStateArr2[i];
            if (drawingStateArr2[i].id != this.animatingStates[i].id) {
                z3 = true;
            } else {
                this.currentStates[i].lastSpeakTime = this.animatingStates[i].lastSpeakTime;
            }
        }
        if (!z3) {
            this.transitionProgress = 1.0f;
            return;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= 3) {
                    this.animatingStates[i2].animationType = 0;
                    break;
                }
                if (this.currentStates[i3].id == this.animatingStates[i2].id) {
                    drawingStateArr[i3] = null;
                    if (i2 == i3) {
                        this.animatingStates[i2].animationType = -1;
                        GroupCallUserCell.AvatarWavesDrawable avatarWavesDrawable = this.animatingStates[i2].wavesDrawable;
                        this.animatingStates[i2].wavesDrawable = this.currentStates[i2].wavesDrawable;
                        this.currentStates[i2].wavesDrawable = avatarWavesDrawable;
                    } else {
                        this.animatingStates[i2].animationType = 2;
                        this.animatingStates[i2].moveFromIndex = i3;
                    }
                } else {
                    i3++;
                }
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            DrawingState drawingState = drawingStateArr[i4];
            if (drawingState != null) {
                drawingState.animationType = 1;
            }
        }
        ValueAnimator valueAnimator = this.transitionProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.transitionProgressAnimator.cancel();
            if (this.transitionInProgress) {
                swapStates();
                this.transitionInProgress = false;
            }
        }
        this.transitionProgress = 0.0f;
        if (z2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.transitionProgressAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    AvatarsDrawable.this.lambda$commitTransition$0(valueAnimator2);
                }
            });
            this.transitionProgressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    AvatarsDrawable avatarsDrawable = AvatarsDrawable.this;
                    if (avatarsDrawable.transitionProgressAnimator != null) {
                        avatarsDrawable.transitionProgress = 1.0f;
                        avatarsDrawable.swapStates();
                        AvatarsDrawable avatarsDrawable2 = AvatarsDrawable.this;
                        if (avatarsDrawable2.updateAfterTransition) {
                            avatarsDrawable2.updateAfterTransition = false;
                            Runnable runnable = avatarsDrawable2.updateDelegate;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                        AvatarsDrawable.this.invalidate();
                    }
                    AvatarsDrawable.this.transitionProgressAnimator = null;
                }
            });
            this.transitionProgressAnimator.setDuration(this.transitionDuration);
            this.transitionProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.transitionProgressAnimator.start();
        } else {
            this.transitionInProgress = true;
        }
        invalidate();
    }

    public int getSize() {
        int i = this.overrideSize;
        if (i != 0) {
            return i;
        }
        int i2 = this.currentStyle;
        return AndroidUtilities.dp((i2 == 4 || i2 == 10) ? 32.0f : 24.0f);
    }

    public void onAttachedToWindow() {
        if (this.attached) {
            return;
        }
        this.attached = true;
        for (int i = 0; i < 3; i++) {
            this.currentStates[i].imageReceiver.onAttachedToWindow();
            this.animatingStates[i].imageReceiver.onAttachedToWindow();
        }
    }

    public void onDetachedFromWindow() {
        if (this.attached) {
            this.attached = false;
            this.wasDraw = false;
            for (int i = 0; i < 3; i++) {
                this.currentStates[i].imageReceiver.onDetachedFromWindow();
                this.animatingStates[i].imageReceiver.onDetachedFromWindow();
            }
            if (this.currentStyle == 3) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
            }
        }
    }

    public void onDraw(android.graphics.Canvas r37) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarsDrawable.onDraw(android.graphics.Canvas):void");
    }

    public void reset() {
        for (int i = 0; i < this.animatingStates.length; i++) {
            setObject(0, 0, null);
        }
    }

    public void setAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void setAvatarsTextSize(int i) {
        for (int i2 = 0; i2 < 3; i2++) {
            DrawingState drawingState = this.currentStates[i2];
            if (drawingState != null && drawingState.avatarDrawable != null) {
                this.currentStates[i2].avatarDrawable.setTextSize(i);
            }
            DrawingState drawingState2 = this.animatingStates[i2];
            if (drawingState2 != null && drawingState2.avatarDrawable != null) {
                this.animatingStates[i2].avatarDrawable.setTextSize(i);
            }
        }
    }

    public void setCentered(boolean z) {
        this.centered = z;
    }

    public void setCount(int i) {
        this.count = i;
        View view = this.parent;
        if (view != null) {
            view.requestLayout();
        }
    }

    public void setDelegate(Runnable runnable) {
        this.updateDelegate = runnable;
    }

    public void setObject(int i, int i2, TLObject tLObject) {
        TLRPC.User user;
        TLRPC.Chat chat;
        ImageReceiver imageReceiver;
        ImageLocation forPhoto;
        String str;
        ImageLocation forPhoto2;
        StringBuilder sb;
        DrawingState drawingState;
        long j;
        this.animatingStates[i].id = 0L;
        DrawingState drawingState2 = this.animatingStates[i];
        drawingState2.participant = null;
        if (tLObject == null) {
            drawingState2.imageReceiver.setImageBitmap((Drawable) null);
        } else {
            drawingState2.lastSpeakTime = -1L;
            this.animatingStates[i].object = tLObject;
            if (tLObject instanceof TLRPC.TL_groupCallParticipant) {
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) tLObject;
                this.animatingStates[i].participant = tL_groupCallParticipant;
                long peerId = MessageObject.getPeerId(tL_groupCallParticipant.peer);
                if (DialogObject.isUserDialog(peerId)) {
                    user = MessagesController.getInstance(i2).getUser(Long.valueOf(peerId));
                    this.animatingStates[i].avatarDrawable.setInfo(i2, user);
                    chat = null;
                } else {
                    TLRPC.Chat chat2 = MessagesController.getInstance(i2).getChat(Long.valueOf(-peerId));
                    this.animatingStates[i].avatarDrawable.setInfo(i2, chat2);
                    chat = chat2;
                    user = null;
                }
                if (this.currentStyle != 4) {
                    drawingState = this.animatingStates[i];
                } else if (peerId == AccountInstance.getInstance(i2).getUserConfig().getClientUserId()) {
                    this.animatingStates[i].lastSpeakTime = 0L;
                    this.animatingStates[i].id = peerId;
                } else if (this.isInCall) {
                    drawingState = this.animatingStates[i];
                    j = tL_groupCallParticipant.lastActiveDate;
                    drawingState.lastSpeakTime = j;
                    this.animatingStates[i].id = peerId;
                } else {
                    drawingState = this.animatingStates[i];
                }
                j = tL_groupCallParticipant.active_date;
                drawingState.lastSpeakTime = j;
                this.animatingStates[i].id = peerId;
            } else if (tLObject instanceof TLRPC.User) {
                user = (TLRPC.User) tLObject;
                if (user.self && this.showSavedMessages) {
                    this.animatingStates[i].avatarDrawable.setAvatarType(1);
                    this.animatingStates[i].avatarDrawable.setScaleSize(0.6f);
                } else {
                    this.animatingStates[i].avatarDrawable.setAvatarType(0);
                    this.animatingStates[i].avatarDrawable.setScaleSize(1.0f);
                    this.animatingStates[i].avatarDrawable.setInfo(i2, user);
                }
                this.animatingStates[i].id = user.id;
                chat = null;
            } else if (tLObject instanceof TLRPC.Chat) {
                chat = (TLRPC.Chat) tLObject;
                this.animatingStates[i].avatarDrawable.setAvatarType(0);
                this.animatingStates[i].avatarDrawable.setScaleSize(1.0f);
                this.animatingStates[i].avatarDrawable.setInfo(i2, chat);
                this.animatingStates[i].id = -chat.id;
                user = null;
            } else {
                user = null;
                chat = null;
            }
            int size = getSize();
            if (tLObject instanceof TL_stories.StoryItem) {
                TL_stories.StoryItem storyItem = (TL_stories.StoryItem) tLObject;
                this.animatingStates[i].id = storyItem.id;
                TLRPC.MessageMedia messageMedia = storyItem.media;
                TLRPC.Document document = messageMedia.document;
                if (document != null) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50, true, null, false);
                    TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(storyItem.media.document.thumbs, 50, true, closestPhotoSizeWithSize, true);
                    imageReceiver = this.animatingStates[i].imageReceiver;
                    forPhoto = ImageLocation.getForDocument(closestPhotoSizeWithSize2, storyItem.media.document);
                    str = size + "_" + size;
                    forPhoto2 = ImageLocation.getForDocument(closestPhotoSizeWithSize, storyItem.media.document);
                    sb = new StringBuilder();
                } else {
                    TLRPC.Photo photo = messageMedia.photo;
                    if (photo != null) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 50, true, null, false);
                        TLRPC.PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(storyItem.media.photo.sizes, 50, true, closestPhotoSizeWithSize3, true);
                        imageReceiver = this.animatingStates[i].imageReceiver;
                        forPhoto = ImageLocation.getForPhoto(closestPhotoSizeWithSize4, storyItem.media.photo);
                        str = size + "_" + size;
                        forPhoto2 = ImageLocation.getForPhoto(closestPhotoSizeWithSize3, storyItem.media.photo);
                        sb = new StringBuilder();
                    }
                }
                sb.append(size);
                sb.append("_");
                sb.append(size);
                imageReceiver.setImage(forPhoto, str, forPhoto2, sb.toString(), 0L, null, storyItem, 0);
            } else if (user == null) {
                this.animatingStates[i].imageReceiver.setForUserOrChat(chat, this.animatingStates[i].avatarDrawable);
            } else if (user.self && this.showSavedMessages) {
                this.animatingStates[i].imageReceiver.setImageBitmap(this.animatingStates[i].avatarDrawable);
            } else {
                this.animatingStates[i].imageReceiver.setForUserOrChat(user, this.animatingStates[i].avatarDrawable);
            }
            this.animatingStates[i].imageReceiver.setRoundRadius(size / 2);
            float f = size;
            this.animatingStates[i].imageReceiver.setImageCoords(0.0f, 0.0f, f, f);
        }
        invalidate();
    }

    public void setShowSavedMessages(boolean z) {
        this.showSavedMessages = z;
    }

    public void setSize(int i) {
        this.overrideSize = i;
    }

    public void setStepFactor(float f) {
        this.overrideSizeStepFactor = f;
    }

    public void setStyle(int i) {
        this.currentStyle = i;
        invalidate();
    }

    public void setTransitionProgress(float f) {
        if (!this.transitionInProgress || this.transitionProgress == f) {
            return;
        }
        this.transitionProgress = f;
        if (f == 1.0f) {
            swapStates();
            this.transitionInProgress = false;
        }
    }

    public void updateAfterTransitionEnd() {
        this.updateAfterTransition = true;
    }
}
