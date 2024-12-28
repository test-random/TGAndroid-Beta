package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.recorder.PreviewView;
import org.telegram.ui.Stories.recorder.StoryEntry;

public abstract class MessageEntityView extends EntityView {
    private final BlurringShader.BlurManager blurManager;
    private boolean clipVideoMessageForBitmap;
    public final FrameLayout container;
    private final SparseIntArray currentColors;
    public boolean firstMeasure;
    private MessageObject.GroupedMessages groupedMessages;
    private boolean isDark;
    public final RecyclerListView listView;
    public final ArrayList messageObjects;
    private Theme.MessageDrawable msgInDrawable;
    private Theme.MessageDrawable msgInDrawableSelected;
    private Theme.MessageDrawable msgMediaInDrawable;
    private Theme.MessageDrawable msgMediaInDrawableSelected;
    private Theme.MessageDrawable msgMediaOutDrawable;
    private Theme.MessageDrawable msgMediaOutDrawableSelected;
    private Theme.MessageDrawable msgOutDrawable;
    private Theme.MessageDrawable msgOutDrawableSelected;
    public final Theme.ResourcesProvider resourcesProvider;
    private TextureView textureView;
    private boolean textureViewActive;
    private boolean usesBackgroundPaint;
    private int videoHeight;
    private int videoWidth;

    public class MessageEntityViewSelectionView extends EntityView.SelectionView {
        private final Paint clearPaint;
        private Path path;

        public MessageEntityViewSelectionView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.clearPaint = paint;
            this.path = new Path();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int saveCount = canvas.getSaveCount();
            float showAlpha = getShowAlpha();
            if (showAlpha <= 0.0f) {
                return;
            }
            if (showAlpha < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (showAlpha * 255.0f), 31);
            }
            float dp = AndroidUtilities.dp(2.0f);
            float dpf2 = AndroidUtilities.dpf2(5.66f);
            float dp2 = dp + dpf2 + AndroidUtilities.dp(15.0f);
            float f = dp2 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f;
            float measuredHeight = getMeasuredHeight() - f;
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = dp2 + measuredWidth;
            float f3 = dp2 + measuredHeight;
            rectF.set(dp2, dp2, f2, f3);
            float dp3 = AndroidUtilities.dp(12.0f);
            float min = Math.min(dp3, measuredWidth / 2.0f);
            float f4 = measuredHeight / 2.0f;
            float min2 = Math.min(dp3, f4);
            this.path.rewind();
            float f5 = min * 2.0f;
            float f6 = dp2 + f5;
            float f7 = 2.0f * min2;
            float f8 = dp2 + f7;
            rectF.set(dp2, dp2, f6, f8);
            this.path.arcTo(rectF, 180.0f, 90.0f);
            float f9 = f2 - f5;
            rectF.set(f9, dp2, f2, f8);
            this.path.arcTo(rectF, 270.0f, 90.0f);
            canvas.drawPath(this.path, this.paint);
            this.path.rewind();
            float f10 = f3 - f7;
            rectF.set(dp2, f10, f6, f3);
            this.path.arcTo(rectF, 180.0f, -90.0f);
            rectF.set(f9, f10, f2, f3);
            this.path.arcTo(rectF, 90.0f, -90.0f);
            canvas.drawPath(this.path, this.paint);
            float f11 = dp2 + f4;
            canvas.drawCircle(dp2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(dp2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.drawCircle(f2, f11, dpf2, this.dotStrokePaint);
            canvas.drawCircle(f2, f11, (dpf2 - AndroidUtilities.dp(1.0f)) + 1.0f, this.dotPaint);
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
            float f12 = dp2 + min2;
            float f13 = f3 - min2;
            canvas.drawLine(dp2, f12, dp2, f13, this.paint);
            canvas.drawLine(f2, f12, f2, f13, this.paint);
            canvas.drawCircle(f2, f11, (AndroidUtilities.dp(1.0f) + dpf2) - 1.0f, this.clearPaint);
            canvas.drawCircle(dp2, f11, (dpf2 + AndroidUtilities.dp(1.0f)) - 1.0f, this.clearPaint);
            canvas.restoreToCount(saveCount);
        }

        @Override
        protected int pointInsideHandle(float f, float f2) {
            float dp = AndroidUtilities.dp(1.0f);
            float dp2 = AndroidUtilities.dp(19.5f);
            float f3 = dp + dp2;
            float f4 = f3 * 2.0f;
            float measuredWidth = getMeasuredWidth() - f4;
            float measuredHeight = getMeasuredHeight() - f4;
            float f5 = (measuredHeight / 2.0f) + f3;
            if (f > f3 - dp2 && f2 > f5 - dp2 && f < f3 + dp2 && f2 < f5 + dp2) {
                return 1;
            }
            float f6 = f3 + measuredWidth;
            if (f <= f6 - dp2 || f2 <= f5 - dp2 || f >= f6 + dp2 || f2 >= f5 + dp2) {
                return (f <= f3 || f >= measuredWidth || f2 <= f3 || f2 >= measuredHeight) ? 0 : 3;
            }
            return 2;
        }
    }

    public MessageEntityView(final Context context, Point point, float f, float f2, ArrayList arrayList, final BlurringShader.BlurManager blurManager, final boolean z, final PreviewView.TextureViewHolder textureViewHolder) {
        super(context, point);
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Peer peer;
        this.messageObjects = new ArrayList();
        this.videoWidth = 1;
        this.videoHeight = 1;
        this.firstMeasure = true;
        this.isDark = Theme.isCurrentThemeDark();
        this.currentColors = new SparseIntArray();
        this.resourcesProvider = new Theme.ResourcesProvider() {
            public final Paint chat_actionBackgroundGradientDarkenPaint;
            public final Paint chat_actionBackgroundPaint;
            public final Paint chat_actionBackgroundSelectedPaint;
            public final TextPaint chat_actionTextPaint;
            public final TextPaint chat_actionTextPaint2;
            public final TextPaint chat_botButtonPaint;

            {
                TextPaint textPaint = new TextPaint();
                this.chat_actionTextPaint = textPaint;
                TextPaint textPaint2 = new TextPaint();
                this.chat_actionTextPaint2 = textPaint2;
                TextPaint textPaint3 = new TextPaint();
                this.chat_botButtonPaint = textPaint3;
                this.chat_actionBackgroundPaint = new Paint(3);
                this.chat_actionBackgroundSelectedPaint = new Paint(3);
                Paint paint = new Paint(3);
                this.chat_actionBackgroundGradientDarkenPaint = paint;
                textPaint.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
                textPaint2.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
                textPaint3.setTextSize(AndroidUtilities.dp(15.0f));
                textPaint3.setTypeface(AndroidUtilities.bold());
                paint.setColor(352321536);
            }

            @Override
            public void applyServiceShaderMatrix(int i, int i2, float f3, float f4) {
                Theme.applyServiceShaderMatrix(i, i2, f3, f4);
            }

            @Override
            public ColorFilter getAnimatedEmojiColorFilter() {
                ColorFilter colorFilter;
                colorFilter = Theme.chat_animatedEmojiTextColorFilter;
                return colorFilter;
            }

            @Override
            public int getColor(int i) {
                return MessageEntityView.this.currentColors.get(i, Theme.getColor(i));
            }

            @Override
            public int getColorOrDefault(int i) {
                int color;
                color = getColor(i);
                return color;
            }

            @Override
            public int getCurrentColor(int i) {
                int color;
                color = getColor(i);
                return color;
            }

            @Override
            public Drawable getDrawable(String str) {
                if (str.equals("drawableMsgIn")) {
                    if (MessageEntityView.this.msgInDrawable == null) {
                        MessageEntityView.this.msgInDrawable = new Theme.MessageDrawable(0, false, false, MessageEntityView.this.resourcesProvider);
                    }
                    return MessageEntityView.this.msgInDrawable;
                }
                if (str.equals("drawableMsgInSelected")) {
                    if (MessageEntityView.this.msgInDrawableSelected == null) {
                        MessageEntityView.this.msgInDrawableSelected = new Theme.MessageDrawable(0, false, true, MessageEntityView.this.resourcesProvider);
                    }
                    return MessageEntityView.this.msgInDrawableSelected;
                }
                if (str.equals("drawableMsgOut")) {
                    if (MessageEntityView.this.msgOutDrawable == null) {
                        MessageEntityView.this.msgOutDrawable = new Theme.MessageDrawable(0, true, false, MessageEntityView.this.resourcesProvider);
                    }
                    return MessageEntityView.this.msgOutDrawable;
                }
                if (str.equals("drawableMsgOutSelected")) {
                    if (MessageEntityView.this.msgOutDrawableSelected == null) {
                        MessageEntityView.this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true, MessageEntityView.this.resourcesProvider);
                    }
                    return MessageEntityView.this.msgOutDrawableSelected;
                }
                if (str.equals("drawableMsgInMedia")) {
                    if (MessageEntityView.this.msgMediaInDrawable == null) {
                        MessageEntityView.this.msgMediaInDrawable = new Theme.MessageDrawable(1, false, false, MessageEntityView.this.resourcesProvider);
                    }
                    MessageEntityView.this.msgMediaInDrawable.invalidateSelf();
                    return MessageEntityView.this.msgMediaInDrawable;
                }
                if (str.equals("drawableMsgInMediaSelected")) {
                    if (MessageEntityView.this.msgMediaInDrawableSelected == null) {
                        MessageEntityView.this.msgMediaInDrawableSelected = new Theme.MessageDrawable(1, false, true, MessageEntityView.this.resourcesProvider);
                    }
                    return MessageEntityView.this.msgMediaInDrawableSelected;
                }
                if (str.equals("drawableMsgOutMedia")) {
                    if (MessageEntityView.this.msgMediaOutDrawable == null) {
                        MessageEntityView.this.msgMediaOutDrawable = new Theme.MessageDrawable(1, true, false, MessageEntityView.this.resourcesProvider);
                    }
                    return MessageEntityView.this.msgMediaOutDrawable;
                }
                if (!str.equals("drawableMsgOutMediaSelected")) {
                    return Theme.getThemeDrawable(str);
                }
                if (MessageEntityView.this.msgMediaOutDrawableSelected == null) {
                    MessageEntityView.this.msgMediaOutDrawableSelected = new Theme.MessageDrawable(1, true, true, MessageEntityView.this.resourcesProvider);
                }
                return MessageEntityView.this.msgMediaOutDrawableSelected;
            }

            @Override
            public Paint getPaint(String str) {
                Paint themePaint;
                str.hashCode();
                char c = 65535;
                switch (str.hashCode()) {
                    case -1490966183:
                        if (str.equals("paintChatActionText2")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 1712385955:
                        if (str.equals("paintChatBotButton")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1790254137:
                        if (str.equals("paintChatActionBackgroundDarken")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1897339317:
                        if (str.equals("paintChatActionBackgroundSelected")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 2030114297:
                        if (str.equals("paintChatActionText")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        return this.chat_actionTextPaint2;
                    case 1:
                        return this.chat_botButtonPaint;
                    case 2:
                        return this.chat_actionBackgroundGradientDarkenPaint;
                    case 3:
                        return this.chat_actionBackgroundSelectedPaint;
                    case 4:
                        return this.chat_actionTextPaint;
                    default:
                        themePaint = Theme.getThemePaint(str);
                        return themePaint;
                }
            }

            @Override
            public boolean hasGradientService() {
                return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
            }

            @Override
            public boolean isDark() {
                return MessageEntityView.this.isDark;
            }

            @Override
            public void setAnimatedColor(int i, int i2) {
                Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i, i2);
            }
        };
        this.blurManager = blurManager;
        setRotation(f);
        setScale(f2);
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject messageObject = (MessageObject) arrayList.get(i);
            TLRPC.Message message = messageObject.messageOwner;
            int i2 = message.date;
            TLRPC.TL_message copyMessage = copyMessage(message);
            Boolean useForwardForRepost = StoryEntry.useForwardForRepost(messageObject);
            if (useForwardForRepost != null && useForwardForRepost.booleanValue() && (messageFwdHeader = copyMessage.fwd_from) != null && (peer = messageFwdHeader.from_id) != null) {
                copyMessage.from_id = peer;
                copyMessage.peer_id = peer;
                copyMessage.flags &= -5;
                copyMessage.fwd_from = null;
            }
            copyMessage.voiceTranscriptionOpen = false;
            int i3 = messageObject.currentAccount;
            this.messageObjects.add(new MessageObject(i3, copyMessage, messageObject.replyMessageObject, MessagesController.getInstance(i3).getUsers(), MessagesController.getInstance(messageObject.currentAccount).getChats(), null, null, true, true, 0L, true, z, false));
        }
        this.groupedMessages = null;
        if (this.messageObjects.size() > 1) {
            MessageObject.GroupedMessages groupedMessages = new MessageObject.GroupedMessages();
            this.groupedMessages = groupedMessages;
            groupedMessages.messages.addAll(this.messageObjects);
            this.groupedMessages.groupId = ((MessageObject) this.messageObjects.get(0)).getGroupId();
            this.groupedMessages.calculate();
        }
        FrameLayout frameLayout = new FrameLayout(context) {
            private final Matrix videoMatrix = new Matrix();
            private final float[] radii = new float[8];
            private final Path clipPath = new Path();

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                ImageReceiver photoImage;
                if (view != MessageEntityView.this.textureView) {
                    return super.drawChild(canvas, view, j);
                }
                ChatMessageCell cell = MessageEntityView.this.getCell();
                if (cell == null || (photoImage = cell.getPhotoImage()) == null) {
                    return false;
                }
                this.videoMatrix.reset();
                float max = Math.max(photoImage.getImageWidth() / MessageEntityView.this.videoWidth, photoImage.getImageHeight() / MessageEntityView.this.videoHeight);
                this.videoMatrix.postScale((MessageEntityView.this.videoWidth / MessageEntityView.this.textureView.getWidth()) * max, (MessageEntityView.this.videoHeight / MessageEntityView.this.textureView.getHeight()) * max);
                this.videoMatrix.postTranslate(((MessageEntityView.this.listView.getX() + cell.getX()) + photoImage.getCenterX()) - ((MessageEntityView.this.videoWidth * max) / 2.0f), ((MessageEntityView.this.listView.getY() + cell.getY()) + photoImage.getCenterY()) - ((MessageEntityView.this.videoHeight * max) / 2.0f));
                MessageEntityView.this.textureView.setTransform(this.videoMatrix);
                canvas.save();
                this.clipPath.rewind();
                AndroidUtilities.rectTmp.set(MessageEntityView.this.listView.getX() + cell.getX() + photoImage.getImageX(), MessageEntityView.this.listView.getY() + cell.getY() + photoImage.getImageY(), MessageEntityView.this.listView.getX() + cell.getX() + photoImage.getImageX2(), MessageEntityView.this.listView.getY() + cell.getY() + photoImage.getImageY2());
                for (int i4 = 0; i4 < photoImage.getRoundRadius().length; i4++) {
                    int i5 = i4 * 2;
                    this.radii[i5] = photoImage.getRoundRadius()[i4];
                    this.radii[i5 + 1] = photoImage.getRoundRadius()[i4];
                }
                this.clipPath.addRoundRect(AndroidUtilities.rectTmp, this.radii, Path.Direction.CW);
                canvas.clipPath(this.clipPath);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            @Override
            protected void onLayout(boolean z2, int i4, int i5, int i6, int i7) {
                int measuredWidth = MessageEntityView.this.listView.getMeasuredWidth();
                int i8 = 0;
                for (int i9 = 0; i9 < MessageEntityView.this.listView.getChildCount(); i9++) {
                    View childAt = MessageEntityView.this.listView.getChildAt(i9);
                    int left = childAt.getLeft();
                    int right = childAt.getRight();
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        left = childAt.getLeft() + chatMessageCell.getBoundsLeft();
                        right = chatMessageCell.getBoundsRight() + childAt.getLeft();
                    }
                    measuredWidth = Math.min(left, measuredWidth);
                    i8 = Math.max(right, i8);
                }
                RecyclerListView recyclerListView = MessageEntityView.this.listView;
                recyclerListView.layout(-measuredWidth, 0, recyclerListView.getMeasuredWidth() - measuredWidth, MessageEntityView.this.listView.getMeasuredHeight());
                if (MessageEntityView.this.textureView != null) {
                    MessageEntityView.this.textureView.layout(0, 0, getMeasuredWidth(), MessageEntityView.this.listView.getMeasuredHeight());
                }
            }

            @Override
            protected void onMeasure(int i4, int i5) {
                MessageEntityView.this.listView.measure(i4, View.MeasureSpec.makeMeasureSpec(0, 0));
                if (MessageEntityView.this.textureView != null) {
                    MessageEntityView.this.textureView.measure(View.MeasureSpec.makeMeasureSpec(MessageEntityView.this.listView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(MessageEntityView.this.listView.getMeasuredHeight(), 1073741824));
                }
                int measuredWidth = MessageEntityView.this.listView.getMeasuredWidth();
                int i6 = 0;
                for (int i7 = 0; i7 < MessageEntityView.this.listView.getChildCount(); i7++) {
                    View childAt = MessageEntityView.this.listView.getChildAt(i7);
                    int left = childAt.getLeft();
                    int right = childAt.getRight();
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        left = childAt.getLeft() + chatMessageCell.getBoundsLeft();
                        right = chatMessageCell.getBoundsRight() + childAt.getLeft();
                    }
                    measuredWidth = Math.min(left, measuredWidth);
                    i6 = Math.max(right, i6);
                }
                setMeasuredDimension(i6 - measuredWidth, MessageEntityView.this.listView.getMeasuredHeight());
            }
        };
        this.container = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, this.resourcesProvider) {
            private final ArrayList drawTimeAfter = new ArrayList();
            private final ArrayList drawNamesAfter = new ArrayList();
            private final ArrayList drawCaptionAfter = new ArrayList();
            private final ArrayList drawReactionsAfter = new ArrayList();
            private final ArrayList drawingGroups = new ArrayList(10);

            private void drawChatBackgroundElements(Canvas canvas) {
                float f3;
                int i4;
                int i5;
                boolean z2;
                MessageObject.GroupedMessages currentMessagesGroup;
                int i6;
                int childCount = getChildCount();
                int i7 = 0;
                MessageObject.GroupedMessages groupedMessages2 = null;
                while (true) {
                    f3 = 0.0f;
                    i4 = 4;
                    i5 = 2;
                    z2 = true;
                    if (i7 >= childCount) {
                        break;
                    }
                    View childAt = getChildAt(i7);
                    if (childAt.getVisibility() != 4) {
                        if (childAt instanceof ChatMessageCell) {
                            ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                            MessageObject.GroupedMessages currentMessagesGroup2 = chatMessageCell.getCurrentMessagesGroup();
                            if (currentMessagesGroup2 == null || currentMessagesGroup2 != groupedMessages2) {
                                MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                                MessageBackgroundDrawable backgroundDrawable = chatMessageCell.getBackgroundDrawable();
                                if ((backgroundDrawable.isAnimationInProgress() || chatMessageCell.isDrawingSelectionBackground()) && (currentPosition == null || (currentPosition.flags & 2) != 0)) {
                                    int y = (int) chatMessageCell.getY();
                                    canvas.save();
                                    if (currentPosition == null) {
                                        i6 = chatMessageCell.getMeasuredHeight();
                                    } else {
                                        int measuredHeight = chatMessageCell.getMeasuredHeight() + y;
                                        long j = 0;
                                        float f4 = 0.0f;
                                        for (int i8 = 0; i8 < childCount; i8++) {
                                            View childAt2 = getChildAt(i8);
                                            if (childAt2 instanceof ChatMessageCell) {
                                                ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                                                if (chatMessageCell2.getCurrentMessagesGroup() == currentMessagesGroup2) {
                                                    MessageBackgroundDrawable backgroundDrawable2 = chatMessageCell2.getBackgroundDrawable();
                                                    y = Math.min(y, (int) chatMessageCell2.getY());
                                                    measuredHeight = Math.max(measuredHeight, ((int) chatMessageCell2.getY()) + chatMessageCell2.getMeasuredHeight());
                                                    long lastTouchTime = backgroundDrawable2.getLastTouchTime();
                                                    if (lastTouchTime > j) {
                                                        f3 = backgroundDrawable2.getTouchX() + chatMessageCell2.getX();
                                                        f4 = backgroundDrawable2.getTouchY() + chatMessageCell2.getY();
                                                        j = lastTouchTime;
                                                    }
                                                }
                                            }
                                        }
                                        backgroundDrawable.setTouchCoordsOverride(f3, f4 - y);
                                        i6 = measuredHeight - y;
                                    }
                                    int i9 = i6 + y;
                                    canvas.clipRect(0, y, getMeasuredWidth(), i9);
                                    backgroundDrawable.setCustomPaint(null);
                                    backgroundDrawable.setColor(getThemedColor(Theme.key_chat_selectedBackground));
                                    backgroundDrawable.setBounds(0, y, getMeasuredWidth(), i9);
                                    backgroundDrawable.draw(canvas);
                                    canvas.restore();
                                }
                                groupedMessages2 = currentMessagesGroup2;
                            }
                        } else if (childAt instanceof ChatActionCell) {
                            ChatActionCell chatActionCell = (ChatActionCell) childAt;
                            if (chatActionCell.hasGradientService()) {
                                canvas.save();
                                canvas.translate(chatActionCell.getX(), chatActionCell.getY());
                                canvas.scale(chatActionCell.getScaleX(), chatActionCell.getScaleY(), chatActionCell.getMeasuredWidth() / 2.0f, chatActionCell.getMeasuredHeight() / 2.0f);
                                chatActionCell.drawBackground(canvas, true);
                                chatActionCell.drawReactions(canvas, true);
                                canvas.restore();
                            }
                        }
                    }
                    i7++;
                }
                int i10 = 0;
                while (i10 < 3) {
                    this.drawingGroups.clear();
                    if (i10 != i5 || isFastScrollAnimationRunning()) {
                        int i11 = 0;
                        while (i11 < childCount) {
                            View childAt3 = getChildAt(i11);
                            if (childAt3 instanceof ChatMessageCell) {
                                ChatMessageCell chatMessageCell3 = (ChatMessageCell) childAt3;
                                if (childAt3.getY() <= getHeight() && childAt3.getY() + childAt3.getHeight() >= f3 && chatMessageCell3.getVisibility() != i4 && chatMessageCell3.getVisibility() != 8 && (currentMessagesGroup = chatMessageCell3.getCurrentMessagesGroup()) != null && ((i10 != 0 || currentMessagesGroup.messages.size() != z2) && ((i10 != z2 || currentMessagesGroup.transitionParams.drawBackgroundForDeletedItems) && ((i10 != 0 || !chatMessageCell3.getMessageObject().deleted) && ((i10 != z2 || chatMessageCell3.getMessageObject().deleted) && ((i10 != i5 || chatMessageCell3.willRemovedAfterAnimation()) && (i10 == i5 || !chatMessageCell3.willRemovedAfterAnimation()))))))) {
                                    if (!this.drawingGroups.contains(currentMessagesGroup)) {
                                        MessageObject.GroupedMessages.TransitionParams transitionParams = currentMessagesGroup.transitionParams;
                                        transitionParams.left = 0;
                                        transitionParams.top = 0;
                                        transitionParams.right = 0;
                                        transitionParams.bottom = 0;
                                        transitionParams.pinnedBotton = false;
                                        transitionParams.pinnedTop = false;
                                        transitionParams.cell = chatMessageCell3;
                                        this.drawingGroups.add(currentMessagesGroup);
                                    }
                                    currentMessagesGroup.transitionParams.pinnedTop = chatMessageCell3.isPinnedTop();
                                    currentMessagesGroup.transitionParams.pinnedBotton = chatMessageCell3.isPinnedBottom();
                                    int left = chatMessageCell3.getLeft() + chatMessageCell3.getBackgroundDrawableLeft();
                                    int left2 = chatMessageCell3.getLeft() + chatMessageCell3.getBackgroundDrawableRight();
                                    int top = chatMessageCell3.getTop() + chatMessageCell3.getBackgroundDrawableTop();
                                    int top2 = chatMessageCell3.getTop() + chatMessageCell3.getBackgroundDrawableBottom();
                                    if ((chatMessageCell3.getCurrentPosition().flags & i4) == 0) {
                                        top -= AndroidUtilities.dp(10.0f);
                                    }
                                    if ((8 & chatMessageCell3.getCurrentPosition().flags) == 0) {
                                        top2 += AndroidUtilities.dp(10.0f);
                                    }
                                    int i12 = top2;
                                    if (chatMessageCell3.willRemovedAfterAnimation()) {
                                        currentMessagesGroup.transitionParams.cell = chatMessageCell3;
                                    }
                                    MessageObject.GroupedMessages.TransitionParams transitionParams2 = currentMessagesGroup.transitionParams;
                                    int i13 = transitionParams2.top;
                                    if (i13 == 0 || top < i13) {
                                        transitionParams2.top = top;
                                    }
                                    int i14 = transitionParams2.bottom;
                                    if (i14 == 0 || i12 > i14) {
                                        transitionParams2.bottom = i12;
                                    }
                                    int i15 = transitionParams2.left;
                                    if (i15 == 0 || left < i15) {
                                        transitionParams2.left = left;
                                    }
                                    int i16 = transitionParams2.right;
                                    if (i16 == 0 || left2 > i16) {
                                        transitionParams2.right = left2;
                                    }
                                    i11++;
                                    i5 = 2;
                                }
                            }
                            i11++;
                            i5 = 2;
                        }
                        int i17 = 0;
                        while (i17 < this.drawingGroups.size()) {
                            MessageObject.GroupedMessages groupedMessages3 = (MessageObject.GroupedMessages) this.drawingGroups.get(i17);
                            float nonAnimationTranslationX = groupedMessages3.transitionParams.cell.getNonAnimationTranslationX(z2);
                            MessageObject.GroupedMessages.TransitionParams transitionParams3 = groupedMessages3.transitionParams;
                            float f5 = transitionParams3.left + nonAnimationTranslationX + transitionParams3.offsetLeft;
                            float f6 = transitionParams3.top + transitionParams3.offsetTop;
                            float f7 = transitionParams3.right + nonAnimationTranslationX + transitionParams3.offsetRight;
                            float f8 = transitionParams3.bottom + transitionParams3.offsetBottom;
                            if (!transitionParams3.backgroundChangeBounds) {
                                f6 += transitionParams3.cell.getTranslationY();
                                f8 += groupedMessages3.transitionParams.cell.getTranslationY();
                            }
                            float f9 = f8;
                            boolean z3 = (groupedMessages3.transitionParams.cell.getScaleX() == 1.0f && groupedMessages3.transitionParams.cell.getScaleY() == 1.0f) ? false : true;
                            if (z3) {
                                canvas.save();
                                canvas.scale(groupedMessages3.transitionParams.cell.getScaleX(), groupedMessages3.transitionParams.cell.getScaleY(), f5 + ((f7 - f5) / 2.0f), f6 + ((f9 - f6) / 2.0f));
                            }
                            MessageObject.GroupedMessages.TransitionParams transitionParams4 = groupedMessages3.transitionParams;
                            float f10 = f6;
                            int i18 = i17;
                            transitionParams4.cell.drawBackground(canvas, (int) f5, (int) f6, (int) f7, (int) f9, transitionParams4.pinnedTop, transitionParams4.pinnedBotton, false, 0);
                            MessageObject.GroupedMessages.TransitionParams transitionParams5 = groupedMessages3.transitionParams;
                            transitionParams5.cell = null;
                            transitionParams5.drawCaptionLayout = groupedMessages3.hasCaption;
                            if (z3) {
                                canvas.restore();
                                for (int i19 = 0; i19 < childCount; i19++) {
                                    View childAt4 = getChildAt(i19);
                                    if (childAt4 instanceof ChatMessageCell) {
                                        ChatMessageCell chatMessageCell4 = (ChatMessageCell) childAt4;
                                        if (chatMessageCell4.getCurrentMessagesGroup() == groupedMessages3) {
                                            int left3 = chatMessageCell4.getLeft();
                                            int top3 = chatMessageCell4.getTop();
                                            childAt4.setPivotX((f5 - left3) + ((f7 - f5) / 2.0f));
                                            childAt4.setPivotY((f10 - top3) + ((f9 - f10) / 2.0f));
                                        }
                                    }
                                }
                            }
                            i17 = i18 + 1;
                            z2 = true;
                        }
                    }
                    i10++;
                    z2 = true;
                    i5 = 2;
                    i4 = 4;
                    f3 = 0.0f;
                }
            }

            private void drawChatForegroundElements(Canvas canvas) {
                int size = this.drawTimeAfter.size();
                boolean z2 = 1;
                boolean z3 = false;
                if (size > 0) {
                    for (int i4 = 0; i4 < size; i4++) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) this.drawTimeAfter.get(i4);
                        canvas.save();
                        canvas.translate(chatMessageCell.getLeft() + chatMessageCell.getNonAnimationTranslationX(false), chatMessageCell.getY());
                        chatMessageCell.drawTime(canvas, chatMessageCell.shouldDrawAlphaLayer() ? chatMessageCell.getAlpha() : 1.0f, true);
                        canvas.restore();
                    }
                    this.drawTimeAfter.clear();
                }
                int size2 = this.drawNamesAfter.size();
                if (size2 > 0) {
                    for (int i5 = 0; i5 < size2; i5++) {
                        ChatMessageCell chatMessageCell2 = (ChatMessageCell) this.drawNamesAfter.get(i5);
                        float left = chatMessageCell2.getLeft() + chatMessageCell2.getNonAnimationTranslationX(false);
                        float y = chatMessageCell2.getY();
                        float alpha = chatMessageCell2.shouldDrawAlphaLayer() ? chatMessageCell2.getAlpha() : 1.0f;
                        canvas.save();
                        canvas.translate(left, y);
                        chatMessageCell2.setInvalidatesParent(true);
                        chatMessageCell2.drawNamesLayout(canvas, alpha);
                        chatMessageCell2.setInvalidatesParent(false);
                        canvas.restore();
                    }
                    this.drawNamesAfter.clear();
                }
                int size3 = this.drawCaptionAfter.size();
                if (size3 > 0) {
                    int i6 = 0;
                    while (i6 < size3) {
                        ChatMessageCell chatMessageCell3 = (ChatMessageCell) this.drawCaptionAfter.get(i6);
                        boolean z4 = chatMessageCell3.getCurrentPosition() != null && (chatMessageCell3.getCurrentPosition().flags & z2) == 0;
                        float alpha2 = chatMessageCell3.shouldDrawAlphaLayer() ? chatMessageCell3.getAlpha() : 1.0f;
                        float left2 = chatMessageCell3.getLeft() + chatMessageCell3.getNonAnimationTranslationX(z3);
                        float y2 = chatMessageCell3.getY();
                        canvas.save();
                        MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell3.getCurrentMessagesGroup();
                        if (currentMessagesGroup != null && currentMessagesGroup.transitionParams.backgroundChangeBounds) {
                            float nonAnimationTranslationX = chatMessageCell3.getNonAnimationTranslationX(z2);
                            MessageObject.GroupedMessages.TransitionParams transitionParams = currentMessagesGroup.transitionParams;
                            float f3 = transitionParams.left + nonAnimationTranslationX + transitionParams.offsetLeft;
                            float f4 = transitionParams.top + transitionParams.offsetTop;
                            float f5 = transitionParams.right + nonAnimationTranslationX + transitionParams.offsetRight;
                            float f6 = transitionParams.bottom + transitionParams.offsetBottom;
                            if (!transitionParams.backgroundChangeBounds) {
                                f4 += chatMessageCell3.getTranslationY();
                                f6 += chatMessageCell3.getTranslationY();
                            }
                            canvas.clipRect(f3 + AndroidUtilities.dp(8.0f), f4 + AndroidUtilities.dp(8.0f), f5 - AndroidUtilities.dp(8.0f), f6 - AndroidUtilities.dp(8.0f));
                        }
                        if (chatMessageCell3.getTransitionParams().wasDraw) {
                            canvas.translate(left2, y2);
                            chatMessageCell3.setInvalidatesParent(true);
                            chatMessageCell3.drawCaptionLayout(canvas, z4, alpha2);
                            chatMessageCell3.setInvalidatesParent(false);
                            canvas.restore();
                        }
                        i6++;
                        z2 = 1;
                        z3 = false;
                    }
                    this.drawCaptionAfter.clear();
                }
                int size4 = this.drawReactionsAfter.size();
                if (size4 > 0) {
                    for (int i7 = 0; i7 < size4; i7++) {
                        ChatMessageCell chatMessageCell4 = (ChatMessageCell) this.drawReactionsAfter.get(i7);
                        if (chatMessageCell4.getCurrentPosition() == null || (chatMessageCell4.getCurrentPosition().flags & 1) != 0) {
                            float alpha3 = chatMessageCell4.shouldDrawAlphaLayer() ? chatMessageCell4.getAlpha() : 1.0f;
                            float left3 = chatMessageCell4.getLeft() + chatMessageCell4.getNonAnimationTranslationX(false);
                            float y3 = chatMessageCell4.getY();
                            canvas.save();
                            MessageObject.GroupedMessages currentMessagesGroup2 = chatMessageCell4.getCurrentMessagesGroup();
                            if (currentMessagesGroup2 != null && currentMessagesGroup2.transitionParams.backgroundChangeBounds) {
                                float nonAnimationTranslationX2 = chatMessageCell4.getNonAnimationTranslationX(true);
                                MessageObject.GroupedMessages.TransitionParams transitionParams2 = currentMessagesGroup2.transitionParams;
                                float f7 = transitionParams2.left + nonAnimationTranslationX2 + transitionParams2.offsetLeft;
                                float f8 = transitionParams2.top + transitionParams2.offsetTop;
                                float f9 = transitionParams2.right + nonAnimationTranslationX2 + transitionParams2.offsetRight;
                                float f10 = transitionParams2.bottom + transitionParams2.offsetBottom;
                                if (!transitionParams2.backgroundChangeBounds) {
                                    f8 += chatMessageCell4.getTranslationY();
                                    f10 += chatMessageCell4.getTranslationY();
                                }
                                canvas.clipRect(f7 + AndroidUtilities.dp(8.0f), f8 + AndroidUtilities.dp(8.0f), f9 - AndroidUtilities.dp(8.0f), f10 - AndroidUtilities.dp(8.0f));
                            }
                            if (chatMessageCell4.getTransitionParams().wasDraw) {
                                canvas.translate(left3, y3);
                                chatMessageCell4.setInvalidatesParent(true);
                                chatMessageCell4.drawReactionsLayout(canvas, alpha3, null);
                                chatMessageCell4.drawCommentLayout(canvas, alpha3);
                                chatMessageCell4.setInvalidatesParent(false);
                                canvas.restore();
                            }
                        }
                    }
                    this.drawReactionsAfter.clear();
                }
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                canvas.save();
                this.selectorRect.setEmpty();
                drawChatBackgroundElements(canvas);
                super.dispatchDraw(canvas);
                drawChatForegroundElements(canvas);
                canvas.restore();
            }

            @Override
            public boolean drawChild(android.graphics.Canvas r19, android.view.View r20, long r21) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.MessageEntityView.AnonymousClass2.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                return MessageEntityView.this.messageObjects.size();
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i4) {
                MessageObject.GroupedMessagePosition position;
                MessageObject messageObject2 = (MessageObject) MessageEntityView.this.messageObjects.get((MessageEntityView.this.messageObjects.size() - 1) - i4);
                ((ChatMessageCell) viewHolder.itemView).setMessageObject(messageObject2, MessageEntityView.this.groupedMessages, MessageEntityView.this.groupedMessages != null, (MessageEntityView.this.groupedMessages == null || (position = MessageEntityView.this.groupedMessages.getPosition(messageObject2)) == null || position.minY == 0) ? false : true);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i4) {
                ChatMessageCell chatMessageCell = new ChatMessageCell(context, UserConfig.selectedAccount, false, null, MessageEntityView.this.resourcesProvider) {
                    public BlurringShader.StoryBlurDrawer blurDrawer;
                    private final Paint clearPaint;
                    private final RectF dst;
                    private final Rect src;
                    private final float[] radii = new float[8];
                    private final Path clipPath = new Path();

                    {
                        this.blurDrawer = new BlurringShader.StoryBlurDrawer(blurManager, this, 10);
                        Paint paint = new Paint();
                        this.clearPaint = paint;
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        this.src = new Rect();
                        this.dst = new RectF();
                    }

                    @Override
                    public boolean drawPhotoImage(Canvas canvas) {
                        PreviewView.TextureViewHolder textureViewHolder2;
                        ImageReceiver photoImage = getPhotoImage();
                        AnonymousClass3 anonymousClass3 = AnonymousClass3.this;
                        if (!z || photoImage == null || (((textureViewHolder2 = textureViewHolder) == null || !textureViewHolder2.active || !textureViewHolder2.textureViewActive || !MessageEntityView.this.textureViewActive) && !MessageEntityView.this.clipVideoMessageForBitmap && (MessageEntityView.this.textureView == null || !MessageEntityView.this.drawForBitmap()))) {
                            return super.drawPhotoImage(canvas);
                        }
                        for (int i5 = 0; i5 < photoImage.getRoundRadius().length; i5++) {
                            int i6 = i5 * 2;
                            this.radii[i6] = photoImage.getRoundRadius()[i5];
                            this.radii[i6 + 1] = photoImage.getRoundRadius()[i5];
                        }
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(photoImage.getImageX(), photoImage.getImageY(), photoImage.getImageX2(), photoImage.getImageY2());
                        this.clipPath.rewind();
                        this.clipPath.addRoundRect(rectF, this.radii, Path.Direction.CW);
                        if (MessageEntityView.this.textureView == null || !MessageEntityView.this.drawForBitmap()) {
                            canvas.drawPath(this.clipPath, this.clearPaint);
                        } else {
                            Bitmap bitmap = MessageEntityView.this.textureView.getBitmap();
                            if (bitmap == null) {
                                return super.drawPhotoImage(canvas);
                            }
                            canvas.save();
                            canvas.clipPath(this.clipPath);
                            canvas.translate(-getX(), -getY());
                            float max = Math.max(photoImage.getImageWidth() / MessageEntityView.this.videoWidth, photoImage.getImageHeight() / MessageEntityView.this.videoHeight);
                            canvas.translate(photoImage.getCenterX() - ((MessageEntityView.this.videoWidth * max) / 2.0f), photoImage.getCenterY() - ((MessageEntityView.this.videoHeight * max) / 2.0f));
                            canvas.scale((MessageEntityView.this.videoWidth / MessageEntityView.this.textureView.getWidth()) * max, (MessageEntityView.this.videoHeight / MessageEntityView.this.textureView.getHeight()) * max);
                            this.src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                            this.dst.set(0.0f, 0.0f, MessageEntityView.this.textureView.getWidth(), MessageEntityView.this.textureView.getHeight());
                            canvas.drawBitmap(bitmap, this.src, this.dst, (Paint) null);
                            canvas.restore();
                        }
                        return true;
                    }

                    @Override
                    public Paint getThemedPaint(String str) {
                        if ("paintChatActionBackground".equals(str)) {
                            MessageEntityView.this.usesBackgroundPaint = true;
                            Paint paint = this.blurDrawer.getPaint(1.0f);
                            if (paint != null) {
                                return paint;
                            }
                        }
                        return super.getThemedPaint(str);
                    }

                    @Override
                    public void onDraw(Canvas canvas) {
                        AnonymousClass3 anonymousClass3 = AnonymousClass3.this;
                        PreviewView.TextureViewHolder textureViewHolder2 = textureViewHolder;
                        if ((textureViewHolder2 != null && textureViewHolder2.active && textureViewHolder2.textureViewActive) || MessageEntityView.this.clipVideoMessageForBitmap) {
                            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
                        } else {
                            canvas.save();
                        }
                        super.onDraw(canvas);
                        canvas.restore();
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        return false;
                    }
                };
                chatMessageCell.isChat = true;
                return new RecyclerListView.Holder(chatMessageCell);
            }
        });
        GridLayoutManagerFixed gridLayoutManagerFixed = new GridLayoutManagerFixed(context, 1000, 1, true) {
            @Override
            protected boolean hasSiblingChild(int i4) {
                byte b;
                int size = (MessageEntityView.this.messageObjects.size() - 1) - i4;
                if (MessageEntityView.this.groupedMessages != null && size >= 0 && size < MessageEntityView.this.messageObjects.size()) {
                    MessageObject.GroupedMessagePosition position = MessageEntityView.this.groupedMessages.getPosition((MessageObject) MessageEntityView.this.messageObjects.get(size));
                    if (position != null && position.minX != position.maxX && (b = position.minY) == position.maxY && b != 0) {
                        int size2 = MessageEntityView.this.groupedMessages.posArray.size();
                        for (int i5 = 0; i5 < size2; i5++) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition = MessageEntityView.this.groupedMessages.posArray.get(i5);
                            if (groupedMessagePosition != position) {
                                byte b2 = groupedMessagePosition.minY;
                                byte b3 = position.minY;
                                if (b2 <= b3 && groupedMessagePosition.maxY >= b3) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean shouldLayoutChildFromOpositeSide(View view) {
                if (view instanceof ChatMessageCell) {
                    return !((ChatMessageCell) view).getMessageObject().isOutOwner();
                }
                return false;
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        gridLayoutManagerFixed.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i4) {
                int size = (MessageEntityView.this.messageObjects.size() - 1) - i4;
                if (MessageEntityView.this.groupedMessages == null || size < 0 || size >= MessageEntityView.this.groupedMessages.messages.size()) {
                    return 1000;
                }
                MessageObject.GroupedMessagePosition position = MessageEntityView.this.groupedMessages.getPosition(MessageEntityView.this.groupedMessages.messages.get(size));
                if (position != null) {
                    return position.spanSize;
                }
                return 1000;
            }
        });
        recyclerListView.setLayoutManager(gridLayoutManagerFixed);
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                ChatMessageCell chatMessageCell;
                MessageObject.GroupedMessages currentMessagesGroup;
                MessageObject.GroupedMessagePosition currentPosition;
                int i4 = 0;
                rect.bottom = 0;
                if (!(view instanceof ChatMessageCell) || (currentMessagesGroup = (chatMessageCell = (ChatMessageCell) view).getCurrentMessagesGroup()) == null || (currentPosition = chatMessageCell.getCurrentPosition()) == null || currentPosition.siblingHeights == null) {
                    return;
                }
                android.graphics.Point point2 = AndroidUtilities.displaySize;
                float max = Math.max(point2.x, point2.y) * 0.5f;
                int extraInsetHeight = chatMessageCell.getExtraInsetHeight();
                int i5 = 0;
                while (true) {
                    if (i5 >= currentPosition.siblingHeights.length) {
                        break;
                    }
                    extraInsetHeight += (int) Math.ceil(r3[i5] * max);
                    i5++;
                }
                int round = extraInsetHeight + ((currentPosition.maxY - currentPosition.minY) * Math.round(AndroidUtilities.density * 7.0f));
                int size = currentMessagesGroup.posArray.size();
                while (true) {
                    if (i4 < size) {
                        MessageObject.GroupedMessagePosition groupedMessagePosition = currentMessagesGroup.posArray.get(i4);
                        byte b = groupedMessagePosition.minY;
                        byte b2 = currentPosition.minY;
                        if (b == b2 && ((groupedMessagePosition.minX != currentPosition.minX || groupedMessagePosition.maxX != currentPosition.maxX || b != b2 || groupedMessagePosition.maxY != currentPosition.maxY) && b == b2)) {
                            round -= ((int) Math.ceil(max * groupedMessagePosition.ph)) - AndroidUtilities.dp(4.0f);
                            break;
                        }
                        i4++;
                    } else {
                        break;
                    }
                }
                rect.bottom = -round;
            }
        });
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        if (textureViewHolder != null && textureViewHolder.active) {
            textureViewHolder.takeTextureView(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    MessageEntityView.this.lambda$new$0((TextureView) obj);
                }
            }, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    MessageEntityView.this.lambda$new$2((Integer) obj, (Integer) obj2);
                }
            });
        }
        updatePosition();
    }

    public ChatMessageCell getCell() {
        if (this.listView == null) {
            return null;
        }
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof ChatMessageCell) {
                return (ChatMessageCell) this.listView.getChildAt(i);
            }
        }
        return null;
    }

    public void lambda$new$0(TextureView textureView) {
        this.textureView = textureView;
        if (textureView != null) {
            this.container.addView(textureView, 0);
        }
    }

    public void lambda$new$1() {
        this.textureViewActive = true;
        invalidateAll();
    }

    public void lambda$new$2(Integer num, Integer num2) {
        this.videoWidth = num.intValue();
        this.videoHeight = num2.intValue();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessageEntityView.this.lambda$new$1();
            }
        }, 60L);
    }

    public TLRPC.TL_message copyMessage(TLRPC.Message message) {
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.id = message.id;
        tL_message.from_id = message.from_id;
        tL_message.peer_id = message.peer_id;
        tL_message.date = message.date;
        tL_message.expire_date = message.expire_date;
        tL_message.action = message.action;
        tL_message.message = message.message;
        tL_message.media = message.media;
        tL_message.flags = message.flags;
        tL_message.mentioned = message.mentioned;
        tL_message.media_unread = message.media_unread;
        tL_message.out = message.out;
        tL_message.unread = message.unread;
        tL_message.entities = message.entities;
        tL_message.via_bot_name = message.via_bot_name;
        tL_message.reply_markup = message.reply_markup;
        tL_message.views = message.views;
        tL_message.forwards = message.forwards;
        tL_message.replies = message.replies;
        tL_message.edit_date = message.edit_date;
        tL_message.silent = message.silent;
        tL_message.post = message.post;
        tL_message.from_scheduled = message.from_scheduled;
        tL_message.legacy = message.legacy;
        tL_message.edit_hide = message.edit_hide;
        tL_message.pinned = message.pinned;
        tL_message.fwd_from = message.fwd_from;
        tL_message.via_bot_id = message.via_bot_id;
        tL_message.reply_to = message.reply_to;
        tL_message.post_author = message.post_author;
        tL_message.grouped_id = message.grouped_id;
        tL_message.reactions = message.reactions;
        tL_message.restriction_reason = message.restriction_reason;
        tL_message.ttl_period = message.ttl_period;
        tL_message.noforwards = message.noforwards;
        tL_message.invert_media = message.invert_media;
        tL_message.send_state = message.send_state;
        tL_message.fwd_msg_id = message.fwd_msg_id;
        tL_message.attachPath = message.attachPath;
        tL_message.params = message.params;
        tL_message.random_id = message.random_id;
        tL_message.local_id = message.local_id;
        tL_message.dialog_id = message.dialog_id;
        tL_message.ttl = message.ttl;
        tL_message.destroyTime = message.destroyTime;
        tL_message.destroyTimeMillis = message.destroyTimeMillis;
        tL_message.layer = message.layer;
        tL_message.seq_in = message.seq_in;
        tL_message.seq_out = message.seq_out;
        tL_message.with_my_score = message.with_my_score;
        tL_message.replyMessage = message.replyMessage;
        tL_message.reqId = message.reqId;
        tL_message.realId = message.realId;
        tL_message.stickerVerified = message.stickerVerified;
        tL_message.isThreadMessage = message.isThreadMessage;
        tL_message.voiceTranscription = message.voiceTranscription;
        tL_message.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
        tL_message.voiceTranscriptionRated = message.voiceTranscriptionRated;
        tL_message.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
        tL_message.voiceTranscriptionForce = message.voiceTranscriptionForce;
        tL_message.voiceTranscriptionId = message.voiceTranscriptionId;
        tL_message.premiumEffectWasPlayed = message.premiumEffectWasPlayed;
        tL_message.originalLanguage = message.originalLanguage;
        tL_message.translatedToLanguage = message.translatedToLanguage;
        tL_message.translatedText = message.translatedText;
        tL_message.replyStory = message.replyStory;
        return tL_message;
    }

    @Override
    protected EntityView.SelectionView createSelectionView() {
        return new MessageEntityViewSelectionView(getContext());
    }

    public abstract boolean drawForBitmap();

    @Override
    protected float getBounceScale() {
        return 0.02f;
    }

    public float getBubbleBounds(RectF rectF) {
        float y;
        float y2;
        float f;
        float f2;
        float f3 = 2.1474836E9f;
        float f4 = 2.1474836E9f;
        float f5 = -2.1474836E9f;
        float f6 = -2.1474836E9f;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                if (chatMessageCell.getMessageObject() == null || !chatMessageCell.getMessageObject().isRoundVideo() || chatMessageCell.getPhotoImage() == null) {
                    float x = this.container.getX() + childAt.getX() + chatMessageCell.getBackgroundDrawableLeft() + AndroidUtilities.dp(1.0f);
                    if (this.groupedMessages == null) {
                        x += AndroidUtilities.dp(8.0f);
                    }
                    float x2 = ((this.container.getX() + childAt.getX()) + chatMessageCell.getBackgroundDrawableRight()) - AndroidUtilities.dp(1.66f);
                    y = this.container.getY() + childAt.getY() + chatMessageCell.getBackgroundDrawableTop() + AndroidUtilities.dp(2.0f);
                    y2 = ((this.container.getY() + childAt.getY()) + chatMessageCell.getBackgroundDrawableBottom()) - AndroidUtilities.dp(1.0f);
                    f = x;
                    f2 = x2;
                } else {
                    f = this.container.getX() + chatMessageCell.getX() + chatMessageCell.getPhotoImage().getImageX();
                    f2 = this.container.getX() + chatMessageCell.getX() + chatMessageCell.getPhotoImage().getImageX2();
                    y = this.container.getY() + chatMessageCell.getY() + chatMessageCell.getPhotoImage().getImageY();
                    y2 = this.container.getY() + chatMessageCell.getY() + chatMessageCell.getPhotoImage().getImageY2();
                }
                f3 = Math.min(Math.min(f3, f), f2);
                f5 = Math.max(Math.max(f5, f), f2);
                f4 = Math.min(Math.min(f4, y), y2);
                f6 = Math.max(Math.max(f6, y), y2);
            }
        }
        rectF.set(f3, f4, f5, f6);
        return AndroidUtilities.dp(SharedConfig.bubbleRadius);
    }

    @Override
    public org.telegram.ui.Components.Rect getSelectionBounds() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup == null) {
            return new org.telegram.ui.Components.Rect();
        }
        float scaleX = viewGroup.getScaleX();
        return new org.telegram.ui.Components.Rect(((getPositionX() * scaleX) - (((getMeasuredWidth() * getScale()) / 2.0f) * scaleX)) - AndroidUtilities.dp(35.5f), ((getPositionY() * scaleX) - (((getMeasuredHeight() * getScale()) / 2.0f) * scaleX)) - AndroidUtilities.dp(35.5f), (getMeasuredWidth() * getScale() * scaleX) + AndroidUtilities.dp(71.0f), (getMeasuredHeight() * getScale() * scaleX) + AndroidUtilities.dp(71.0f));
    }

    public void invalidateAll() {
        this.listView.invalidate();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            this.listView.getChildAt(i).invalidate();
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        this.container.measure(i, i2);
        setMeasuredDimension(this.container.getMeasuredWidth(), this.container.getMeasuredHeight());
        updatePosition();
        if (this.firstMeasure) {
            float min = Math.min((View.MeasureSpec.getSize(i) - AndroidUtilities.dp(44.0f)) / getMeasuredWidth(), (View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(192.0f)) / getMeasuredHeight());
            if (min < 1.0f) {
                setScale(min);
            }
            Point position = getPosition();
            position.x -= AndroidUtilities.dp(19.0f) * Math.min(1.0f, min);
            setPosition(position);
            this.firstMeasure = false;
        }
    }

    public void prepareToDraw(boolean z) {
        this.clipVideoMessageForBitmap = z;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                ((ChatMessageCell) childAt).drawingToBitmap = z;
            }
        }
    }

    public void setupTheme(org.telegram.ui.Stories.recorder.StoryEntry r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.MessageEntityView.setupTheme(org.telegram.ui.Stories.recorder.StoryEntry):void");
    }

    @Override
    public void updatePosition() {
        setX(getPositionX() - (getMeasuredWidth() / 2.0f));
        setY(getPositionY() - (getMeasuredHeight() / 2.0f));
        updateSelectionView();
        if (this.usesBackgroundPaint) {
            invalidateAll();
        }
    }
}
