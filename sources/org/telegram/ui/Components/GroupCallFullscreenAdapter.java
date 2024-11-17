package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;

public class GroupCallFullscreenAdapter extends RecyclerListView.SelectionAdapter {
    private final GroupCallActivity activity;
    private ArrayList attachedRenderers;
    private final int currentAccount;
    private ChatObject.Call groupCall;
    private GroupCallRenderersContainer renderersContainer;
    private final ArrayList videoParticipants = new ArrayList();
    private final ArrayList participants = new ArrayList();
    private boolean visible = false;

    public class GroupCallUserCell extends FrameLayout implements GroupCallStatusIcon.Callback {
        boolean attached;
        AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        GroupCallUserCell.AvatarWavesDrawable avatarWavesDrawable;
        Paint backgroundPaint;
        ValueAnimator colorAnimator;
        private TLRPC.Chat currentChat;
        private TLRPC.User currentUser;
        String drawingName;
        boolean hasAvatar;
        int lastColor;
        int lastWavesColor;
        RLottieImageView muteButton;
        String name;
        int nameWidth;
        TLRPC.TL_groupCallParticipant participant;
        long peerId;
        float progress;
        GroupCallMiniTextureView renderer;
        boolean selected;
        Paint selectionPaint;
        float selectionProgress;
        boolean skipInvalidate;
        GroupCallStatusIcon statusIcon;
        TextPaint textPaint;
        ChatObject.VideoParticipant videoParticipant;

        public GroupCallUserCell(Context context) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            this.backgroundPaint = new Paint(1);
            this.selectionPaint = new Paint(1);
            this.progress = 1.0f;
            this.textPaint = new TextPaint(1);
            this.avatarWavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
            this.avatarDrawable.setTextSize((int) (AndroidUtilities.dp(18.0f) / 1.15f));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, 1, 0.0f, 9.0f, 0.0f, 9.0f));
            setWillNotDraw(false);
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_voipgroup_listViewBackground));
            this.selectionPaint.setColor(Theme.getColor(Theme.key_voipgroup_speakingText));
            this.selectionPaint.setStyle(Paint.Style.STROKE);
            this.selectionPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.textPaint.setColor(-1);
            RLottieImageView rLottieImageView = new RLottieImageView(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    GroupCallUserCell.this.invalidate();
                }
            };
            this.muteButton = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.muteButton, LayoutHelper.createFrame(24, 24.0f));
        }

        private void drawSelection(android.graphics.Canvas r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.drawSelection(android.graphics.Canvas):void");
        }

        public void lambda$updateState$0(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
            this.lastColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.lastWavesColor = ColorUtils.blendARGB(i3, i4, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.muteButton.setColorFilter(new PorterDuffColorFilter(this.lastColor, PorterDuff.Mode.MULTIPLY));
            this.textPaint.setColor(this.lastColor);
            this.selectionPaint.setColor(this.lastWavesColor);
            this.avatarWavesDrawable.setColor(ColorUtils.setAlphaComponent(this.lastWavesColor, 38));
            invalidate();
        }

        private void setSelectedProgress(float f) {
            if (this.selectionProgress != f) {
                this.selectionProgress = f;
                this.selectionPaint.setAlpha((int) (f * 255.0f));
            }
        }

        public void attachRenderer(boolean z) {
            if (GroupCallFullscreenAdapter.this.activity.isDismissed()) {
                return;
            }
            if (z && this.renderer == null) {
                this.renderer = GroupCallMiniTextureView.getOrCreate(GroupCallFullscreenAdapter.this.attachedRenderers, GroupCallFullscreenAdapter.this.renderersContainer, null, this, null, this.videoParticipant, GroupCallFullscreenAdapter.this.groupCall, GroupCallFullscreenAdapter.this.activity);
            } else {
                if (z) {
                    return;
                }
                GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.setSecondaryView(null);
                }
                this.renderer = null;
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            if (groupCallMiniTextureView != null && groupCallMiniTextureView.isFullyVisible() && !GroupCallFullscreenAdapter.this.activity.drawingForBlur) {
                drawSelection(canvas);
                return;
            }
            if (this.progress > 0.0f) {
                float measuredWidth = (getMeasuredWidth() / 2.0f) * (1.0f - this.progress);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(measuredWidth, measuredWidth, getMeasuredWidth() - measuredWidth, getMeasuredHeight() - measuredWidth);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), this.backgroundPaint);
                drawSelection(canvas);
            }
            float x = this.avatarImageView.getX() + (this.avatarImageView.getMeasuredWidth() / 2);
            float y = this.avatarImageView.getY() + (this.avatarImageView.getMeasuredHeight() / 2);
            this.avatarWavesDrawable.update();
            this.avatarWavesDrawable.draw(canvas, x, y, this);
            float f = this.progress;
            float dp = ((AndroidUtilities.dp(46.0f) / AndroidUtilities.dp(40.0f)) * (1.0f - f)) + (f * 1.0f);
            this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale() * dp);
            this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale() * dp);
            super.dispatchDraw(canvas);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view == this.muteButton) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        public void drawOverlays(Canvas canvas) {
            if (this.drawingName != null) {
                canvas.save();
                int measuredWidth = ((getMeasuredWidth() - this.nameWidth) - AndroidUtilities.dp(24.0f)) / 2;
                this.textPaint.setAlpha((int) (this.progress * 255.0f * getAlpha()));
                canvas.drawText(this.drawingName, AndroidUtilities.dp(22.0f) + measuredWidth, AndroidUtilities.dp(69.0f), this.textPaint);
                canvas.restore();
                canvas.save();
                canvas.translate(measuredWidth, AndroidUtilities.dp(53.0f));
                if (this.muteButton.getDrawable() != null) {
                    this.muteButton.getDrawable().setAlpha((int) (this.progress * 255.0f * getAlpha()));
                    this.muteButton.draw(canvas);
                    this.muteButton.getDrawable().setAlpha(255);
                }
                canvas.restore();
            }
        }

        public BackupImageView getAvatarImageView() {
            return this.avatarImageView;
        }

        public TLRPC.TL_groupCallParticipant getParticipant() {
            return this.participant;
        }

        public long getPeerId() {
            return this.peerId;
        }

        public float getProgressToFullscreen() {
            return this.progress;
        }

        public GroupCallMiniTextureView getRenderer() {
            return this.renderer;
        }

        public ChatObject.VideoParticipant getVideoParticipant() {
            return this.videoParticipant;
        }

        @Override
        public void invalidate() {
            if (this.skipInvalidate) {
                return;
            }
            this.skipInvalidate = true;
            super.invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.invalidate();
            } else {
                GroupCallFullscreenAdapter.this.renderersContainer.invalidate();
            }
            this.skipInvalidate = false;
        }

        public boolean isRemoving(RecyclerListView recyclerListView) {
            return recyclerListView.getChildAdapterPosition(this) == -1;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (GroupCallFullscreenAdapter.this.visible && this.videoParticipant != null) {
                attachRenderer(true);
            }
            this.attached = true;
            this.statusIcon = GroupCallFullscreenAdapter.this.activity.statusIconPool.size() > 0 ? (GroupCallStatusIcon) GroupCallFullscreenAdapter.this.activity.statusIconPool.remove(GroupCallFullscreenAdapter.this.activity.statusIconPool.size() - 1) : new GroupCallStatusIcon();
            this.statusIcon.setCallback(this);
            this.statusIcon.setImageView(this.muteButton);
            this.statusIcon.setParticipant(this.participant, false);
            updateState(false);
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            if (this.statusIcon.isSpeaking()) {
                return;
            }
            this.avatarWavesDrawable.setAmplitude(0.0d);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            attachRenderer(false);
            this.attached = false;
            if (this.statusIcon != null) {
                GroupCallFullscreenAdapter.this.activity.statusIconPool.add(this.statusIcon);
                this.statusIcon.setImageView(null);
                this.statusIcon.setCallback(null);
            }
            this.statusIcon = null;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            if (this.name != null) {
                int min = (int) Math.min(AndroidUtilities.dp(46.0f), this.textPaint.measureText(this.name));
                this.nameWidth = min;
                this.drawingName = TextUtils.ellipsize(this.name, this.textPaint, min, TextUtils.TruncateAt.END).toString();
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
        }

        @Override
        public void onStatusChanged() {
            this.avatarWavesDrawable.setShowWaves(this.statusIcon.isSpeaking(), this);
            updateState(true);
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
        }

        public void setAmplitude(double d) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                groupCallStatusIcon.setAmplitude(d);
            }
            this.avatarWavesDrawable.setAmplitude(d);
        }

        public void setParticipant(org.telegram.messenger.ChatObject.VideoParticipant r12, org.telegram.tgnet.TLRPC.TL_groupCallParticipant r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.setParticipant(org.telegram.messenger.ChatObject$VideoParticipant, org.telegram.tgnet.TLRPC$TL_groupCallParticipant):void");
        }

        public void setProgressToFullscreen(float f) {
            if (this.progress == f) {
                return;
            }
            this.progress = f;
            if (f == 1.0f) {
                this.avatarImageView.setTranslationY(0.0f);
                this.avatarImageView.setScaleX(1.0f);
                this.avatarImageView.setScaleY(1.0f);
                this.backgroundPaint.setAlpha(255);
                invalidate();
                GroupCallMiniTextureView groupCallMiniTextureView = this.renderer;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.invalidate();
                    return;
                }
                return;
            }
            float f2 = 1.0f - f;
            float dp = ((AndroidUtilities.dp(46.0f) / AndroidUtilities.dp(40.0f)) * f2) + (1.0f * f);
            this.avatarImageView.setTranslationY((-((this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2.0f)) - (getMeasuredHeight() / 2.0f))) * f2);
            this.avatarImageView.setScaleX(dp);
            this.avatarImageView.setScaleY(dp);
            this.backgroundPaint.setAlpha((int) (f * 255.0f));
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView2 = this.renderer;
            if (groupCallMiniTextureView2 != null) {
                groupCallMiniTextureView2.invalidate();
            }
        }

        public void setRenderer(GroupCallMiniTextureView groupCallMiniTextureView) {
            this.renderer = groupCallMiniTextureView;
        }

        public void updateState(boolean r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell.updateState(boolean):void");
        }
    }

    public GroupCallFullscreenAdapter(ChatObject.Call call, int i, GroupCallActivity groupCallActivity) {
        this.groupCall = call;
        this.currentAccount = i;
        this.activity = groupCallActivity;
    }

    @Override
    public int getItemCount() {
        return this.videoParticipants.size() + this.participants.size();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        ChatObject.VideoParticipant videoParticipant;
        GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
        ChatObject.VideoParticipant videoParticipant2 = groupCallUserCell.videoParticipant;
        if (i < this.videoParticipants.size()) {
            videoParticipant = (ChatObject.VideoParticipant) this.videoParticipants.get(i);
            tL_groupCallParticipant = ((ChatObject.VideoParticipant) this.videoParticipants.get(i)).participant;
        } else {
            if (i - this.videoParticipants.size() >= this.participants.size()) {
                return;
            }
            tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) this.participants.get(i - this.videoParticipants.size());
            videoParticipant = null;
        }
        groupCallUserCell.setParticipant(videoParticipant, tL_groupCallParticipant);
        boolean z = false;
        if (videoParticipant2 != null && !videoParticipant2.equals(videoParticipant) && groupCallUserCell.attached && groupCallUserCell.getRenderer() != null) {
            groupCallUserCell.attachRenderer(false);
            if (videoParticipant == null) {
                return;
            }
        } else {
            if (!groupCallUserCell.attached) {
                return;
            }
            if (groupCallUserCell.getRenderer() != null || videoParticipant == null || !this.visible) {
                if (groupCallUserCell.getRenderer() == null || videoParticipant != null) {
                    return;
                }
                groupCallUserCell.attachRenderer(z);
            }
        }
        z = true;
        groupCallUserCell.attachRenderer(z);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecyclerListView.Holder(new GroupCallUserCell(viewGroup.getContext()));
    }

    public void scrollTo(ChatObject.VideoParticipant videoParticipant, RecyclerListView recyclerListView) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerListView.getLayoutManager();
        if (linearLayoutManager == null) {
            return;
        }
        for (int i = 0; i < this.videoParticipants.size(); i++) {
            if (((ChatObject.VideoParticipant) this.videoParticipants.get(i)).equals(videoParticipant)) {
                linearLayoutManager.scrollToPositionWithOffset(i, AndroidUtilities.dp(13.0f));
                return;
            }
        }
    }

    public void setGroupCall(ChatObject.Call call) {
        this.groupCall = call;
    }

    public void setRenderersPool(ArrayList arrayList, GroupCallRenderersContainer groupCallRenderersContainer) {
        this.attachedRenderers = arrayList;
        this.renderersContainer = groupCallRenderersContainer;
    }

    public void setVisibility(RecyclerListView recyclerListView, boolean z) {
        this.visible = z;
        for (int i = 0; i < recyclerListView.getChildCount(); i++) {
            View childAt = recyclerListView.getChildAt(i);
            if (childAt instanceof GroupCallUserCell) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt;
                if (groupCallUserCell.getVideoParticipant() != null) {
                    groupCallUserCell.attachRenderer(z);
                }
            }
        }
    }

    public void update(boolean z, RecyclerListView recyclerListView) {
        if (this.groupCall == null) {
            return;
        }
        if (!z) {
            this.participants.clear();
            ChatObject.Call call = this.groupCall;
            if (!call.call.rtmp_stream) {
                this.participants.addAll(call.visibleParticipants);
            }
            this.videoParticipants.clear();
            ChatObject.Call call2 = this.groupCall;
            if (!call2.call.rtmp_stream) {
                this.videoParticipants.addAll(call2.visibleVideoParticipants);
            }
            notifyDataSetChanged();
            return;
        }
        final ArrayList arrayList = new ArrayList(this.participants);
        final ArrayList arrayList2 = new ArrayList(this.videoParticipants);
        this.participants.clear();
        ChatObject.Call call3 = this.groupCall;
        if (!call3.call.rtmp_stream) {
            this.participants.addAll(call3.visibleParticipants);
        }
        this.videoParticipants.clear();
        ChatObject.Call call4 = this.groupCall;
        if (!call4.call.rtmp_stream) {
            this.videoParticipants.addAll(call4.visibleVideoParticipants);
        }
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public boolean areContentsTheSame(int i, int i2) {
                return true;
            }

            @Override
            public boolean areItemsTheSame(int i, int i2) {
                if (i < arrayList2.size() && i2 < GroupCallFullscreenAdapter.this.videoParticipants.size()) {
                    return ((ChatObject.VideoParticipant) arrayList2.get(i)).equals(GroupCallFullscreenAdapter.this.videoParticipants.get(i2));
                }
                int size = i - arrayList2.size();
                int size2 = i2 - GroupCallFullscreenAdapter.this.videoParticipants.size();
                if (size2 < 0 || size2 >= GroupCallFullscreenAdapter.this.participants.size() || size < 0 || size >= arrayList.size()) {
                    return MessageObject.getPeerId((i < arrayList2.size() ? ((ChatObject.VideoParticipant) arrayList2.get(i)).participant : (TLRPC.TL_groupCallParticipant) arrayList.get(size)).peer) == MessageObject.getPeerId((i2 < GroupCallFullscreenAdapter.this.videoParticipants.size() ? ((ChatObject.VideoParticipant) GroupCallFullscreenAdapter.this.videoParticipants.get(i2)).participant : (TLRPC.TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(size2)).peer);
                }
                return MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) arrayList.get(size)).peer) == MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) GroupCallFullscreenAdapter.this.participants.get(size2)).peer);
            }

            @Override
            public int getNewListSize() {
                return GroupCallFullscreenAdapter.this.videoParticipants.size() + GroupCallFullscreenAdapter.this.participants.size();
            }

            @Override
            public int getOldListSize() {
                return arrayList2.size() + arrayList.size();
            }
        }).dispatchUpdatesTo(this);
        AndroidUtilities.updateVisibleRows(recyclerListView);
    }
}
