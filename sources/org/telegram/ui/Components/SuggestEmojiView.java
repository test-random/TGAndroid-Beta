package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SuggestEmojiView;
import org.telegram.ui.ContentPreviewViewer;

public class SuggestEmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private Adapter adapter;
    private Integer arrowToEnd;
    private Emoji.EmojiSpan arrowToSpan;
    private Integer arrowToStart;
    private float arrowX;
    private AnimatedFloat arrowXAnimated;
    private Paint backgroundPaint;
    private Path circlePath;
    private boolean clear;
    private FrameLayout containerView;
    private final int currentAccount;
    private int direction;
    private AnchorViewDelegate enterView;
    private boolean forceClose;
    private int horizontalPadding;
    private boolean isCopyForbidden;
    private boolean isSetAsStatusForbidden;
    private ArrayList keywordResults;
    private String[] lastLang;
    private long lastLangChangedTime;
    private String lastQuery;
    private int lastQueryId;
    private int lastQueryType;
    private float lastSpanY;
    private AnimatedFloat leftGradientAlpha;
    private RecyclerListView listView;
    private AnimatedFloat listViewCenterAnimated;
    private AnimatedFloat listViewWidthAnimated;
    private MediaDataController.SearchStickersKey loadingKey;
    private OvershootInterpolator overshootInterpolator;
    private Path path;
    private ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatedFloat rightGradientAlpha;
    private Runnable searchRunnable;
    private boolean show;
    private AnimatedFloat showFloat1;
    private AnimatedFloat showFloat2;
    private Runnable updateRunnable;

    public class AnonymousClass1 implements ContentPreviewViewer.ContentPreviewViewerDelegate {
        AnonymousClass1() {
        }

        public void lambda$setAsEmojiStatus$0(TLRPC.EmojiStatus emojiStatus) {
            MessagesController.getInstance(SuggestEmojiView.this.currentAccount).updateEmojiStatus(emojiStatus);
        }

        @Override
        public void addToFavoriteSelected(String str) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$addToFavoriteSelected(this, str);
        }

        @Override
        public boolean can() {
            return true;
        }

        @Override
        public boolean canDeleteSticker(TLRPC.Document document) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$canDeleteSticker(this, document);
        }

        @Override
        public boolean canEditSticker() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$canEditSticker(this);
        }

        @Override
        public boolean canSchedule() {
            return false;
        }

        @Override
        public Boolean canSetAsStatus(TLRPC.Document document) {
            TLRPC.User currentUser;
            if (SuggestEmojiView.this.isSetAsStatusForbidden || !UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || (currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser()) == null) {
                return null;
            }
            Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(currentUser);
            return Boolean.valueOf(document != null && (emojiStatusDocumentId == null || emojiStatusDocumentId.longValue() != document.id));
        }

        @Override
        public void copyEmoji(TLRPC.Document document) {
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(MessageObject.findAnimatedEmojiEmoticon(document));
            valueOf.setSpan(new AnimatedEmojiSpan(document, (Paint.FontMetricsInt) null), 0, valueOf.length(), 33);
            if (!AndroidUtilities.addToClipboard(valueOf) || SuggestEmojiView.this.enterView == null) {
                return;
            }
            BulletinFactory.of(SuggestEmojiView.this.enterView.getParentFragment()).createCopyBulletin(LocaleController.getString(R.string.EmojiCopied)).show();
        }

        @Override
        public void deleteSticker(TLRPC.Document document) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$deleteSticker(this, document);
        }

        @Override
        public void editSticker(TLRPC.Document document) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$editSticker(this, document);
        }

        @Override
        public long getDialogId() {
            return 0L;
        }

        @Override
        public String getQuery(boolean z) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
        }

        @Override
        public void gifAddedOrDeleted() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
        }

        @Override
        public boolean isInScheduleMode() {
            if (SuggestEmojiView.this.enterView == null) {
                return false;
            }
            BaseFragment parentFragment = SuggestEmojiView.this.enterView.getParentFragment();
            if (parentFragment instanceof ChatActivity) {
                return ((ChatActivity) parentFragment).isInScheduleMode();
            }
            return false;
        }

        @Override
        public boolean isPhotoEditor() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isPhotoEditor(this);
        }

        @Override
        public boolean isReplacedSticker() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isReplacedSticker(this);
        }

        @Override
        public boolean isSettingIntroSticker() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isSettingIntroSticker(this);
        }

        @Override
        public boolean isStickerEditor() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isStickerEditor(this);
        }

        @Override
        public boolean needCopy(TLRPC.Document document) {
            if (SuggestEmojiView.this.isCopyForbidden) {
                return false;
            }
            return UserConfig.getInstance(UserConfig.selectedAccount).isPremium();
        }

        @Override
        public boolean needMenu() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
        }

        @Override
        public boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        @Override
        public boolean needRemove() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
        }

        @Override
        public boolean needRemoveFromRecent(TLRPC.Document document) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemoveFromRecent(this, document);
        }

        @Override
        public boolean needSend(int i) {
            if (SuggestEmojiView.this.enterView == null) {
                return false;
            }
            BaseFragment parentFragment = SuggestEmojiView.this.enterView.getParentFragment();
            if (!(parentFragment instanceof ChatActivity)) {
                return false;
            }
            ChatActivity chatActivity = (ChatActivity) parentFragment;
            if (chatActivity.canSendMessage()) {
                return UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || (chatActivity.getCurrentUser() != null && UserObject.isUserSelf(chatActivity.getCurrentUser()));
            }
            return false;
        }

        @Override
        public void newStickerPackSelected(CharSequence charSequence, String str, Utilities.Callback callback) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$newStickerPackSelected(this, charSequence, str, callback);
        }

        @Override
        public void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z) {
        }

        @Override
        public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        @Override
        public void removeFromRecent(TLRPC.Document document) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$removeFromRecent(this, document);
        }

        @Override
        public void resetTouch() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$resetTouch(this);
        }

        @Override
        public void sendEmoji(TLRPC.Document document) {
            if (SuggestEmojiView.this.enterView == null) {
                return;
            }
            BaseFragment parentFragment = SuggestEmojiView.this.enterView.getParentFragment();
            if (parentFragment instanceof ChatActivity) {
                ((ChatActivity) parentFragment).sendAnimatedEmoji(document, true, 0);
                SuggestEmojiView.this.enterView.setFieldText("");
            }
        }

        @Override
        public void sendGif(Object obj, Object obj2, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
        }

        @Override
        public void sendSticker() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendSticker(this);
        }

        @Override
        public void sendSticker(TLRPC.Document document, String str, Object obj, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendSticker(this, document, str, obj, z, i);
        }

        @Override
        public void setAsEmojiStatus(TLRPC.Document document, Integer num) {
            TLRPC.EmojiStatus emojiStatus;
            Bulletin createEmojiBulletin;
            if (document == null) {
                emojiStatus = new TLRPC.TL_emojiStatusEmpty();
            } else {
                TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                tL_emojiStatus.document_id = document.id;
                if (num != null) {
                    tL_emojiStatus.flags |= 1;
                    tL_emojiStatus.until = num.intValue();
                }
                emojiStatus = tL_emojiStatus;
            }
            TLRPC.User currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            final TLRPC.EmojiStatus tL_emojiStatusEmpty = currentUser == null ? new TLRPC.TL_emojiStatusEmpty() : currentUser.emoji_status;
            MessagesController.getInstance(SuggestEmojiView.this.currentAccount).updateEmojiStatus(emojiStatus);
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    SuggestEmojiView.AnonymousClass1.this.lambda$setAsEmojiStatus$0(tL_emojiStatusEmpty);
                }
            };
            BaseFragment parentFragment = SuggestEmojiView.this.enterView == null ? null : SuggestEmojiView.this.enterView.getParentFragment();
            if (parentFragment != null) {
                if (document == null) {
                    Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(SuggestEmojiView.this.getContext(), SuggestEmojiView.this.resourcesProvider);
                    simpleLayout.textView.setText(LocaleController.getString(R.string.RemoveStatusInfo));
                    simpleLayout.imageView.setImageResource(R.drawable.msg_settings_premium);
                    Bulletin.UndoButton undoButton = new Bulletin.UndoButton(SuggestEmojiView.this.getContext(), true, SuggestEmojiView.this.resourcesProvider);
                    undoButton.setUndoAction(runnable);
                    simpleLayout.setButton(undoButton);
                    createEmojiBulletin = Bulletin.make(parentFragment, simpleLayout, 1500);
                } else {
                    createEmojiBulletin = BulletinFactory.of(parentFragment).createEmojiBulletin(document, LocaleController.getString(R.string.SetAsEmojiStatusInfo), LocaleController.getString(R.string.Undo), runnable);
                }
                createEmojiBulletin.show();
            }
        }

        @Override
        public void setIntroSticker(String str) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$setIntroSticker(this, str);
        }

        @Override
        public void stickerSetSelected(TLRPC.StickerSet stickerSet, String str) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$stickerSetSelected(this, stickerSet, str);
        }
    }

    public class Adapter extends RecyclerListView.SelectionAdapter {
        SuggestEmojiView suggestEmojiView;

        public Adapter(SuggestEmojiView suggestEmojiView) {
            this.suggestEmojiView = suggestEmojiView;
        }

        @Override
        public int getItemCount() {
            if (this.suggestEmojiView.keywordResults == null) {
                return 0;
            }
            return this.suggestEmojiView.keywordResults.size();
        }

        @Override
        public long getItemId(int i) {
            if (this.suggestEmojiView.keywordResults == null) {
                return 0L;
            }
            return ((MediaDataController.KeywordResult) this.suggestEmojiView.keywordResults.get(i)).emoji.hashCode();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((EmojiImageView) viewHolder.itemView).setEmoji(this.suggestEmojiView.keywordResults == null ? null : ((MediaDataController.KeywordResult) this.suggestEmojiView.keywordResults.get(i)).emoji, this.suggestEmojiView.getDirection());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new EmojiImageView(this.suggestEmojiView.getContext()));
        }
    }

    public interface AnchorViewDelegate {
        void addTextChangedListener(TextWatcher textWatcher);

        EditTextBoldCursor getEditField();

        Editable getEditText();

        CharSequence getFieldText();

        BaseFragment getParentFragment();

        int getVisibility();

        void setFieldText(CharSequence charSequence);
    }

    public class EmojiImageView extends View {
        private boolean attached;
        private int direction;
        public Drawable drawable;
        private String emoji;
        private final int paddingDp;
        private AnimatedFloat pressed;

        public EmojiImageView(Context context) {
            super(context);
            this.direction = 0;
            this.pressed = new AnimatedFloat(this, 350L, new OvershootInterpolator(5.0f));
            this.paddingDp = 3;
        }

        public void setEmoji(java.lang.String r5, int r6) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SuggestEmojiView.EmojiImageView.setEmoji(java.lang.String, int):void");
        }

        public void attach() {
            Drawable drawable = this.drawable;
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).addView(this);
            }
            this.attached = true;
        }

        public void detach() {
            Drawable drawable = this.drawable;
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).removeView(this);
            }
            this.attached = false;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float f = ((1.0f - this.pressed.set(isPressed() ? 1.0f : 0.0f)) * 0.2f) + 0.8f;
            if (this.drawable != null) {
                int width = getWidth() / 2;
                int height = ((getHeight() - getPaddingBottom()) + getPaddingTop()) / 2;
                this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                canvas.scale(f, f, width, height);
                Drawable drawable = this.drawable;
                if (drawable instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable).setTime(System.currentTimeMillis());
                }
                this.drawable.draw(canvas);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            attach();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            detach();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp((this.direction == 0 ? 0.0f : 6.66f) + 3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp((this.direction != 0 ? 0.0f : 6.66f) + 3.0f));
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), 1073741824));
        }

        public void setDirection(int i) {
            this.direction = i;
            invalidate();
        }

        public void setImageDrawable(Drawable drawable) {
            Drawable drawable2 = this.drawable;
            if (drawable2 instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable2).removeView(this);
            }
            this.drawable = drawable;
            if ((drawable instanceof AnimatedEmojiDrawable) && this.attached) {
                ((AnimatedEmojiDrawable) drawable).addView(this);
            }
        }

        @Override
        public void setPressed(boolean z) {
            super.setPressed(z);
            invalidate();
        }
    }

    public SuggestEmojiView(Context context, final int i, AnchorViewDelegate anchorViewDelegate, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.direction = 0;
        this.horizontalPadding = AndroidUtilities.dp(10.0f);
        this.lastLangChangedTime = 0L;
        this.currentAccount = i;
        this.enterView = anchorViewDelegate;
        this.resourcesProvider = resourcesProvider;
        postDelayed(new Runnable() {
            @Override
            public final void run() {
                SuggestEmojiView.lambda$new$0(i);
            }
        }, 260L);
    }

    private void createListView() {
        if (this.listView != null) {
            return;
        }
        this.path = new Path();
        this.circlePath = new Path();
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                SuggestEmojiView.this.drawContainerBegin(canvas);
                super.dispatchDraw(canvas);
                SuggestEmojiView.this.drawContainerEnd(canvas);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                setPadding(SuggestEmojiView.this.horizontalPadding, SuggestEmojiView.this.direction == 0 ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(6.66f), SuggestEmojiView.this.horizontalPadding, SuggestEmojiView.this.direction == 0 ? AndroidUtilities.dp(6.66f) : AndroidUtilities.dp(8.0f));
                super.onMeasure(i, i2);
            }

            @Override
            public void setVisibility(int i) {
                boolean z = getVisibility() == i;
                super.setVisibility(i);
                if (z) {
                    return;
                }
                boolean z2 = i == 0;
                if (SuggestEmojiView.this.listView != null) {
                    for (int i2 = 0; i2 < SuggestEmojiView.this.listView.getChildCount(); i2++) {
                        EmojiImageView emojiImageView = (EmojiImageView) SuggestEmojiView.this.listView.getChildAt(i2);
                        if (z2) {
                            emojiImageView.attach();
                        } else {
                            emojiImageView.detach();
                        }
                    }
                }
            }
        };
        this.containerView = frameLayout;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.showFloat1 = new AnimatedFloat(frameLayout, 120L, 350L, cubicBezierInterpolator);
        this.showFloat2 = new AnimatedFloat(this.containerView, 150L, 600L, cubicBezierInterpolator);
        this.overshootInterpolator = new OvershootInterpolator(0.4f);
        this.leftGradientAlpha = new AnimatedFloat(this.containerView, 300L, cubicBezierInterpolator);
        this.rightGradientAlpha = new AnimatedFloat(this.containerView, 300L, cubicBezierInterpolator);
        this.arrowXAnimated = new AnimatedFloat(this.containerView, 200L, cubicBezierInterpolator);
        this.listViewCenterAnimated = new AnimatedFloat(this.containerView, 350L, cubicBezierInterpolator);
        this.listViewWidthAnimated = new AnimatedFloat(this.containerView, 350L, cubicBezierInterpolator);
        RecyclerListView recyclerListView = new RecyclerListView(getContext()) {
            private boolean left;
            private boolean right;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, SuggestEmojiView.this.listView, 0, SuggestEmojiView.this.getPreviewDelegate(), this.resourcesProvider);
            }

            @Override
            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
                boolean canScrollHorizontally = canScrollHorizontally(-1);
                boolean canScrollHorizontally2 = canScrollHorizontally(1);
                if (this.left == canScrollHorizontally && this.right == canScrollHorizontally2) {
                    return;
                }
                if (SuggestEmojiView.this.containerView != null) {
                    SuggestEmojiView.this.containerView.invalidate();
                }
                this.left = canScrollHorizontally;
                this.right = canScrollHorizontally2;
            }
        };
        this.listView = recyclerListView;
        Adapter adapter = new Adapter(this);
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(0);
        this.listView.setLayoutManager(linearLayoutManager);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(45L);
        defaultItemAnimator.setTranslationInterpolator(cubicBezierInterpolator);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setSelectorDrawableColor(Theme.getColor(Theme.key_listSelector, this.resourcesProvider));
        RecyclerListView recyclerListView2 = this.listView;
        final RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                SuggestEmojiView.this.lambda$createListView$1(view, i);
            }
        };
        recyclerListView2.setOnItemClickListener(onItemClickListener);
        this.listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createListView$2;
                lambda$createListView$2 = SuggestEmojiView.this.lambda$createListView$2(onItemClickListener, view, motionEvent);
                return lambda$createListView$2;
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, 52.0f));
        addView(this.containerView, LayoutHelper.createFrame(-1.0f, 66.66f, 80));
        AnchorViewDelegate anchorViewDelegate = this.enterView;
        if (anchorViewDelegate != null) {
            anchorViewDelegate.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    if (SuggestEmojiView.this.enterView == null || SuggestEmojiView.this.enterView.getVisibility() != 0) {
                        return;
                    }
                    SuggestEmojiView.this.fireUpdate();
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
            });
        }
    }

    private String[] detectKeyboardLangThrottleFirstWithDelay() {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastLang == null || Math.abs(currentTimeMillis - this.lastLangChangedTime) > 360) {
            this.lastLangChangedTime = currentTimeMillis;
            return AndroidUtilities.getCurrentKeyboardLanguage();
        }
        this.lastLangChangedTime = currentTimeMillis;
        return this.lastLang;
    }

    public void drawContainerBegin(Canvas canvas) {
        FrameLayout frameLayout;
        float scrollY;
        int height;
        ArrayList arrayList;
        Canvas canvas2 = canvas;
        AnchorViewDelegate anchorViewDelegate = this.enterView;
        if (anchorViewDelegate != null && anchorViewDelegate.getEditField() != null) {
            Emoji.EmojiSpan emojiSpan = this.arrowToSpan;
            if (emojiSpan != null && emojiSpan.drawn) {
                float x = this.enterView.getEditField().getX() + this.enterView.getEditField().getPaddingLeft();
                Emoji.EmojiSpan emojiSpan2 = this.arrowToSpan;
                this.arrowX = x + emojiSpan2.lastDrawX;
                this.lastSpanY = emojiSpan2.lastDrawY;
            } else if (this.arrowToStart != null && this.arrowToEnd != null) {
                this.arrowX = this.enterView.getEditField().getX() + this.enterView.getEditField().getPaddingLeft() + AndroidUtilities.dp(12.0f);
            }
        }
        boolean z = (!this.show || this.forceClose || (arrayList = this.keywordResults) == null || arrayList.isEmpty() || this.clear) ? false : true;
        float f = this.showFloat1.set(z ? 1.0f : 0.0f);
        float f2 = this.showFloat2.set(z ? 1.0f : 0.0f);
        float f3 = this.arrowXAnimated.set(this.arrowX);
        if (f <= 0.0f && f2 <= 0.0f && !z) {
            this.containerView.setVisibility(8);
        }
        this.path.rewind();
        float left = this.listView.getLeft();
        int left2 = this.listView.getLeft();
        ArrayList arrayList2 = this.keywordResults;
        float size = left2 + ((arrayList2 == null ? 0 : arrayList2.size()) * AndroidUtilities.dp(44.0f));
        boolean z2 = this.listViewWidthAnimated.get() <= 0.0f;
        float f4 = size - left;
        float f5 = f4 <= 0.0f ? this.listViewWidthAnimated.get() : this.listViewWidthAnimated.set(f4, z2);
        float f6 = this.listViewCenterAnimated.set((left + size) / 2.0f, z2);
        AnchorViewDelegate anchorViewDelegate2 = this.enterView;
        if (anchorViewDelegate2 != null && anchorViewDelegate2.getEditField() != null) {
            int i = this.direction;
            if (i == 0) {
                frameLayout = this.containerView;
                scrollY = ((-this.enterView.getEditField().getHeight()) - this.enterView.getEditField().getScrollY()) + this.lastSpanY;
                height = AndroidUtilities.dp(5.0f);
            } else if (i == 1) {
                frameLayout = this.containerView;
                scrollY = ((-getMeasuredHeight()) - this.enterView.getEditField().getScrollY()) + this.lastSpanY + AndroidUtilities.dp(20.0f);
                height = this.containerView.getHeight();
            }
            frameLayout.setTranslationY(scrollY + height);
        }
        float f7 = f5 / 4.0f;
        float f8 = f5 / 2.0f;
        int max = (int) Math.max((this.arrowX - Math.max(f7, Math.min(f8, AndroidUtilities.dp(66.0f)))) - this.listView.getLeft(), 0.0f);
        if (this.listView.getPaddingLeft() != max) {
            int paddingLeft = this.listView.getPaddingLeft() - max;
            this.listView.setPadding(max, 0, 0, 0);
            this.listView.scrollBy(paddingLeft, 0);
        }
        this.listView.setTranslationX(((int) Math.max((f3 - Math.max(f7, Math.min(f8, AndroidUtilities.dp(66.0f)))) - this.listView.getLeft(), 0.0f)) - max);
        float paddingLeft2 = (f6 - f8) + this.listView.getPaddingLeft() + this.listView.getTranslationX();
        float top = this.listView.getTop() + this.listView.getTranslationY() + this.listView.getPaddingTop() + (this.direction == 0 ? 0 : AndroidUtilities.dp(6.66f));
        float min = Math.min(f6 + f8 + this.listView.getPaddingLeft() + this.listView.getTranslationX(), getWidth() - this.containerView.getPaddingRight());
        float bottom = (this.listView.getBottom() + this.listView.getTranslationY()) - (this.direction == 0 ? AndroidUtilities.dp(6.66f) : 0);
        float min2 = Math.min(AndroidUtilities.dp(9.0f), f8) * 2.0f;
        int i2 = this.direction;
        if (i2 == 0) {
            RectF rectF = AndroidUtilities.rectTmp;
            float f9 = bottom - min2;
            float f10 = paddingLeft2 + min2;
            rectF.set(paddingLeft2, f9, f10, bottom);
            this.path.arcTo(rectF, 90.0f, 90.0f);
            float f11 = top + min2;
            rectF.set(paddingLeft2, top, f10, f11);
            this.path.arcTo(rectF, -180.0f, 90.0f);
            float f12 = min - min2;
            rectF.set(f12, top, min, f11);
            this.path.arcTo(rectF, -90.0f, 90.0f);
            rectF.set(f12, f9, min, bottom);
            this.path.arcTo(rectF, 0.0f, 90.0f);
            this.path.lineTo(AndroidUtilities.dp(8.66f) + f3, bottom);
            this.path.lineTo(f3, AndroidUtilities.dp(6.66f) + bottom);
            this.path.lineTo(f3 - AndroidUtilities.dp(8.66f), bottom);
        } else if (i2 == 1) {
            RectF rectF2 = AndroidUtilities.rectTmp;
            float f13 = min - min2;
            float f14 = top + min2;
            rectF2.set(f13, top, min, f14);
            this.path.arcTo(rectF2, -90.0f, 90.0f);
            float f15 = bottom - min2;
            rectF2.set(f13, f15, min, bottom);
            this.path.arcTo(rectF2, 0.0f, 90.0f);
            float f16 = min2 + paddingLeft2;
            rectF2.set(paddingLeft2, f15, f16, bottom);
            this.path.arcTo(rectF2, 90.0f, 90.0f);
            rectF2.set(paddingLeft2, top, f16, f14);
            this.path.arcTo(rectF2, -180.0f, 90.0f);
            this.path.lineTo(f3 - AndroidUtilities.dp(8.66f), top);
            this.path.lineTo(f3, top - AndroidUtilities.dp(6.66f));
            this.path.lineTo(AndroidUtilities.dp(8.66f) + f3, top);
        }
        this.path.close();
        if (this.backgroundPaint == null) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(2.0f)));
            this.backgroundPaint.setShadowLayer(AndroidUtilities.dp(4.33f), 0.0f, AndroidUtilities.dp(0.33333334f), 855638016);
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_stickersHintPanel, this.resourcesProvider));
        }
        if (f < 1.0f) {
            this.circlePath.rewind();
            float dp = this.direction == 0 ? AndroidUtilities.dp(6.66f) + bottom : top - AndroidUtilities.dp(6.66f);
            double d = f3 - paddingLeft2;
            double d2 = dp - top;
            double d3 = f3 - min;
            double d4 = dp - bottom;
            this.circlePath.addCircle(f3, dp, ((float) Math.sqrt(Math.max(Math.max(Math.pow(d, 2.0d) + Math.pow(d2, 2.0d), Math.pow(d3, 2.0d) + Math.pow(d2, 2.0d)), Math.max(Math.pow(d, 2.0d) + Math.pow(d4, 2.0d), Math.pow(d3, 2.0d) + Math.pow(d4, 2.0d))))) * f, Path.Direction.CW);
            canvas.save();
            canvas2 = canvas;
            canvas2.clipPath(this.circlePath);
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (255.0f * f), 31);
        }
        canvas2.drawPath(this.path, this.backgroundPaint);
        canvas.save();
        canvas2.clipPath(this.path);
    }

    public ContentPreviewViewer.ContentPreviewViewerDelegate getPreviewDelegate() {
        if (this.previewDelegate == null) {
            this.previewDelegate = new AnonymousClass1();
        }
        return this.previewDelegate;
    }

    public void lambda$createListView$1(View view, int i) {
        onClick(((EmojiImageView) view).emoji);
    }

    public boolean lambda$createListView$2(RecyclerListView.OnItemClickListener onItemClickListener, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.listView, 0, onItemClickListener, getPreviewDelegate(), this.resourcesProvider);
    }

    public static void lambda$new$0(int i) {
        MediaDataController.getInstance(i).checkStickers(5);
    }

    public void lambda$searchAnimated$5(int i, String str, ArrayList arrayList) {
        if (i == this.lastQueryId) {
            this.lastQuery = str;
            this.lastQueryType = 2;
            arrayList.remove(arrayList.size() - 1);
            if (arrayList.isEmpty()) {
                this.clear = true;
                forceClose();
                return;
            }
            this.clear = false;
            this.forceClose = false;
            createListView();
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                this.containerView.invalidate();
            }
            this.keywordResults = arrayList;
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void lambda$searchAnimated$6(final String str, final int i) {
        final ArrayList<MediaDataController.KeywordResult> arrayList = new ArrayList<>(1);
        arrayList.add(new MediaDataController.KeywordResult(str, null));
        MediaDataController.getInstance(this.currentAccount).fillWithAnimatedEmoji(arrayList, 15, false, false, false, new Runnable() {
            @Override
            public final void run() {
                SuggestEmojiView.this.lambda$searchAnimated$5(i, str, arrayList);
            }
        });
    }

    public void lambda$searchKeywords$3(int i, String str, HashSet hashSet, ArrayList arrayList, ArrayList arrayList2, String str2) {
        if (i != this.lastQueryId) {
            return;
        }
        this.lastQueryType = 1;
        this.lastQuery = str;
        if (arrayList2 != null) {
            Iterator it = arrayList2.iterator();
            while (it.hasNext()) {
                MediaDataController.KeywordResult keywordResult = (MediaDataController.KeywordResult) it.next();
                if (!hashSet.contains(keywordResult.emoji)) {
                    hashSet.add(keywordResult.emoji);
                    arrayList.add(keywordResult);
                }
            }
        }
        if (arrayList.isEmpty()) {
            this.keywordResults = null;
            this.clear = true;
            forceClose();
            return;
        }
        this.clear = false;
        this.forceClose = false;
        createListView();
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null) {
            frameLayout.setVisibility(0);
        }
        this.lastSpanY = AndroidUtilities.dp(10.0f);
        this.keywordResults = arrayList2;
        this.arrowToStart = 0;
        this.arrowToEnd = Integer.valueOf(str.length());
        FrameLayout frameLayout2 = this.containerView;
        if (frameLayout2 != null) {
            frameLayout2.invalidate();
        }
        Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void lambda$searchKeywords$4(String[] strArr, final String str, final int i) {
        final HashSet hashSet = new HashSet();
        final ArrayList arrayList = new ArrayList();
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(strArr, str, true, new MediaDataController.KeywordResultCallback() {
            @Override
            public final void run(ArrayList arrayList2, String str2) {
                SuggestEmojiView.this.lambda$searchKeywords$3(i, str, hashSet, arrayList, arrayList2, str2);
            }
        }, SharedConfig.suggestAnimatedEmoji && UserConfig.getInstance(this.currentAccount).isPremium());
    }

    private CharSequence makeEmoji(String str) {
        Paint.FontMetricsInt fontMetricsInt = this.enterView.getEditField() != null ? this.enterView.getEditField().getPaint().getFontMetricsInt() : null;
        if (fontMetricsInt == null) {
            Paint paint = new Paint();
            paint.setTextSize(AndroidUtilities.dp(18.0f));
            fontMetricsInt = paint.getFontMetricsInt();
        }
        if (str == null || !str.startsWith("animated_")) {
            return Emoji.replaceEmoji(str, fontMetricsInt, true);
        }
        try {
            long parseLong = Long.parseLong(str.substring(9));
            TLRPC.Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, parseLong);
            SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(findDocument));
            spannableString.setSpan(findDocument == null ? new AnimatedEmojiSpan(parseLong, fontMetricsInt) : new AnimatedEmojiSpan(findDocument, fontMetricsInt), 0, spannableString.length(), 33);
            return spannableString;
        } catch (Exception unused) {
            return null;
        }
    }

    private void onClick(String str) {
        AnchorViewDelegate anchorViewDelegate;
        int intValue;
        int intValue2;
        CharSequence makeEmoji;
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        if (this.show && (anchorViewDelegate = this.enterView) != null && (anchorViewDelegate.getFieldText() instanceof Spanned)) {
            if (this.arrowToSpan != null) {
                intValue = ((Spanned) this.enterView.getFieldText()).getSpanStart(this.arrowToSpan);
                intValue2 = ((Spanned) this.enterView.getFieldText()).getSpanEnd(this.arrowToSpan);
            } else {
                Integer num = this.arrowToStart;
                if (num == null || this.arrowToEnd == null) {
                    return;
                }
                intValue = num.intValue();
                intValue2 = this.arrowToEnd.intValue();
                this.arrowToEnd = null;
                this.arrowToStart = null;
            }
            Editable editText = this.enterView.getEditText();
            if (editText == null || intValue < 0 || intValue2 < 0 || intValue > editText.length() || intValue2 > editText.length()) {
                return;
            }
            if (this.arrowToSpan != null) {
                if (this.enterView.getFieldText() instanceof Spannable) {
                    ((Spannable) this.enterView.getFieldText()).removeSpan(this.arrowToSpan);
                }
                this.arrowToSpan = null;
            }
            String obj = editText.toString();
            String substring = obj.substring(intValue, intValue2);
            int length = substring.length();
            while (true) {
                intValue2 -= length;
                if (intValue2 < 0) {
                    break;
                }
                int i = intValue2 + length;
                if (!obj.substring(intValue2, i).equals(substring) || (makeEmoji = makeEmoji(str)) == null || ((animatedEmojiSpanArr = (AnimatedEmojiSpan[]) editText.getSpans(intValue2, i, AnimatedEmojiSpan.class)) != null && animatedEmojiSpanArr.length > 0)) {
                    break;
                }
                Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) editText.getSpans(intValue2, i, Emoji.EmojiSpan.class);
                if (emojiSpanArr != null) {
                    for (Emoji.EmojiSpan emojiSpan : emojiSpanArr) {
                        editText.removeSpan(emojiSpan);
                    }
                }
                editText.replace(intValue2, i, "");
                editText.insert(intValue2, makeEmoji);
            }
            try {
                performHapticFeedback(3, 1);
            } catch (Exception unused) {
            }
            Emoji.addRecentEmoji(str);
            this.show = false;
            this.forceClose = true;
            this.lastQueryType = 0;
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
        }
    }

    private void searchAnimated(final String str) {
        ArrayList arrayList;
        if (str == null) {
            return;
        }
        String str2 = this.lastQuery;
        if (str2 != null && this.lastQueryType == 2 && str2.equals(str) && !this.clear && (arrayList = this.keywordResults) != null && !arrayList.isEmpty()) {
            this.forceClose = false;
            createListView();
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                this.containerView.invalidate();
                return;
            }
            return;
        }
        final int i = this.lastQueryId + 1;
        this.lastQueryId = i;
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.searchRunnable = new Runnable() {
            @Override
            public final void run() {
                SuggestEmojiView.this.lambda$searchAnimated$6(str, i);
            }
        };
        ArrayList arrayList2 = this.keywordResults;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 600L);
        } else {
            this.searchRunnable.run();
        }
    }

    private void searchKeywords(final String str) {
        ArrayList arrayList;
        if (str == null) {
            return;
        }
        String str2 = this.lastQuery;
        if (str2 != null && this.lastQueryType == 1 && str2.equals(str) && !this.clear && (arrayList = this.keywordResults) != null && !arrayList.isEmpty()) {
            this.forceClose = false;
            createListView();
            this.containerView.setVisibility(0);
            this.lastSpanY = AndroidUtilities.dp(10.0f);
            this.containerView.invalidate();
            return;
        }
        final int i = this.lastQueryId + 1;
        this.lastQueryId = i;
        if (this.loadingKey != null) {
            MediaDataController.getInstance(this.currentAccount).cancelSearchStickers(this.loadingKey);
            this.loadingKey = null;
        }
        final String[] detectKeyboardLangThrottleFirstWithDelay = detectKeyboardLangThrottleFirstWithDelay();
        String[] strArr = this.lastLang;
        if (strArr == null || !Arrays.equals(detectKeyboardLangThrottleFirstWithDelay, strArr)) {
            MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(detectKeyboardLangThrottleFirstWithDelay);
        }
        this.lastLang = detectKeyboardLangThrottleFirstWithDelay;
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
        this.searchRunnable = new Runnable() {
            @Override
            public final void run() {
                SuggestEmojiView.this.lambda$searchKeywords$4(detectKeyboardLangThrottleFirstWithDelay, str, i);
            }
        };
        ArrayList arrayList2 = this.keywordResults;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 600L);
        } else {
            this.searchRunnable.run();
        }
    }

    public void update() {
        this.updateRunnable = null;
        AnchorViewDelegate anchorViewDelegate = this.enterView;
        if (anchorViewDelegate == null || anchorViewDelegate.getEditField() == null || this.enterView.getFieldText() == null) {
            this.show = false;
            this.forceClose = true;
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.invalidate();
                return;
            }
            return;
        }
        int selectionStart = this.enterView.getEditField().getSelectionStart();
        int selectionEnd = this.enterView.getEditField().getSelectionEnd();
        if (selectionStart != selectionEnd) {
            this.show = false;
            FrameLayout frameLayout2 = this.containerView;
            if (frameLayout2 != null) {
                frameLayout2.invalidate();
                return;
            }
            return;
        }
        CharSequence fieldText = this.enterView.getFieldText();
        boolean z = fieldText instanceof Spanned;
        Emoji.EmojiSpan[] emojiSpanArr = z ? (Emoji.EmojiSpan[]) ((Spanned) fieldText).getSpans(Math.max(0, selectionEnd - 24), selectionEnd, Emoji.EmojiSpan.class) : null;
        if (emojiSpanArr == null || emojiSpanArr.length <= 0 || !SharedConfig.suggestAnimatedEmoji || !UserConfig.getInstance(this.currentAccount).isPremium()) {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = z ? (AnimatedEmojiSpan[]) ((Spanned) fieldText).getSpans(Math.max(0, selectionEnd), selectionEnd, AnimatedEmojiSpan.class) : null;
            if ((animatedEmojiSpanArr == null || animatedEmojiSpanArr.length == 0) && selectionEnd < 52) {
                this.show = true;
                createListView();
                this.arrowToSpan = null;
                searchKeywords(fieldText.toString().substring(0, selectionEnd));
                FrameLayout frameLayout3 = this.containerView;
                if (frameLayout3 != null) {
                    frameLayout3.invalidate();
                    return;
                }
                return;
            }
        } else {
            Emoji.EmojiSpan emojiSpan = emojiSpanArr[emojiSpanArr.length - 1];
            if (emojiSpan != null) {
                Spanned spanned = (Spanned) fieldText;
                int spanStart = spanned.getSpanStart(emojiSpan);
                int spanEnd = spanned.getSpanEnd(emojiSpan);
                if (selectionStart == spanEnd) {
                    String substring = fieldText.toString().substring(spanStart, spanEnd);
                    this.show = true;
                    createListView();
                    this.arrowToSpan = emojiSpan;
                    this.arrowToEnd = null;
                    this.arrowToStart = null;
                    searchAnimated(substring);
                    FrameLayout frameLayout4 = this.containerView;
                    if (frameLayout4 != null) {
                        frameLayout4.invalidate();
                        return;
                    }
                    return;
                }
            }
        }
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
        this.show = false;
        FrameLayout frameLayout5 = this.containerView;
        if (frameLayout5 != null) {
            frameLayout5.invalidate();
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            ArrayList arrayList = this.keywordResults;
            if (arrayList == null || arrayList.isEmpty()) {
                return;
            }
            fireUpdate();
            return;
        }
        if (i != NotificationCenter.emojiLoaded || this.listView == null) {
            return;
        }
        for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
            this.listView.getChildAt(i3).invalidate();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.listView == null) {
            return super.dispatchTouchEvent(motionEvent);
        }
        float f = this.listViewWidthAnimated.get();
        float f2 = this.listViewCenterAnimated.get();
        RectF rectF = AndroidUtilities.rectTmp;
        float f3 = f / 2.0f;
        rectF.set((f2 - f3) + this.listView.getPaddingLeft() + this.listView.getTranslationX(), this.listView.getTop() + this.listView.getPaddingTop(), Math.min(f2 + f3 + this.listView.getPaddingLeft() + this.listView.getTranslationX(), getWidth() - this.containerView.getPaddingRight()), this.listView.getBottom());
        rectF.offset(this.containerView.getX(), this.containerView.getY());
        if (this.show && rectF.contains(motionEvent.getX(), motionEvent.getY())) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            motionEvent.setAction(3);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void drawContainerEnd(Canvas canvas) {
        float f = this.listViewWidthAnimated.get();
        float f2 = this.listViewCenterAnimated.get();
        float f3 = f / 2.0f;
        float paddingLeft = (f2 - f3) + this.listView.getPaddingLeft() + this.listView.getTranslationX();
        float top = this.listView.getTop() + this.listView.getPaddingTop();
        float min = Math.min(f2 + f3 + this.listView.getPaddingLeft() + this.listView.getTranslationX(), getWidth() - this.containerView.getPaddingRight());
        float bottom = this.listView.getBottom();
        float f4 = this.leftGradientAlpha.set(this.listView.canScrollHorizontally(-1) ? 1.0f : 0.0f);
        if (f4 > 0.0f) {
            int i = (int) paddingLeft;
            Theme.chat_gradientRightDrawable.setBounds(i, (int) top, AndroidUtilities.dp(32.0f) + i, (int) bottom);
            Theme.chat_gradientRightDrawable.setAlpha((int) (f4 * 255.0f));
            Theme.chat_gradientRightDrawable.draw(canvas);
        }
        float f5 = this.rightGradientAlpha.set(this.listView.canScrollHorizontally(1) ? 1.0f : 0.0f);
        if (f5 > 0.0f) {
            int i2 = (int) min;
            Theme.chat_gradientLeftDrawable.setBounds(i2 - AndroidUtilities.dp(32.0f), (int) top, i2, (int) bottom);
            Theme.chat_gradientLeftDrawable.setAlpha((int) (f5 * 255.0f));
            Theme.chat_gradientLeftDrawable.draw(canvas);
        }
        canvas.restore();
        if (this.showFloat1.get() < 1.0f) {
            canvas.restore();
            canvas.restore();
        }
    }

    protected int emojiCacheType() {
        return 2;
    }

    public void fireUpdate() {
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                SuggestEmojiView.this.update();
            }
        };
        this.updateRunnable = runnable2;
        AndroidUtilities.runOnUIThread(runnable2, 16L);
    }

    public void forbidCopy() {
        this.isCopyForbidden = true;
    }

    public void forbidSetAsStatus() {
        this.isSetAsStatusForbidden = true;
    }

    public void forceClose() {
        Runnable runnable = this.updateRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRunnable = null;
        }
        this.show = false;
        this.forceClose = true;
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public AnchorViewDelegate getDelegate() {
        return this.enterView;
    }

    public int getDirection() {
        return this.direction;
    }

    @Override
    public boolean isShown() {
        return this.show;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    public void onTextSelectionChanged(int i, int i2) {
        fireUpdate();
    }

    public void setDelegate(AnchorViewDelegate anchorViewDelegate) {
        this.enterView = anchorViewDelegate;
    }

    public void setDirection(int i) {
        if (this.direction != i) {
            this.direction = i;
            requestLayout();
        }
    }

    public void setHorizontalPadding(int i) {
        this.horizontalPadding = i;
    }

    public void updateColors() {
        Paint paint = this.backgroundPaint;
        if (paint != null) {
            paint.setColor(Theme.getColor(Theme.key_chat_stickersHintPanel, this.resourcesProvider));
        }
        Drawable drawable = Theme.chat_gradientLeftDrawable;
        int i = Theme.key_chat_stickersHintPanel;
        int color = Theme.getColor(i, this.resourcesProvider);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        drawable.setColorFilter(new PorterDuffColorFilter(color, mode));
        Theme.chat_gradientRightDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i, this.resourcesProvider), mode));
    }
}
