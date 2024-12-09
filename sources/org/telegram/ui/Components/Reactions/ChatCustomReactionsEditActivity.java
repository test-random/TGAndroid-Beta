package org.telegram.ui.Components.Reactions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Reactions.ChatCustomReactionsEditActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class ChatCustomReactionsEditActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private UpdateReactionsButton actionButton;
    private FrameLayout actionButtonContainer;
    private ImageView actionButtonContainerGradient;
    private BackSpaceButtonView backSpaceButtonView;
    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private FrameLayout bottomDialogLayout;
    private final long chatId;
    private LinearLayout contentLayout;
    private TLRPC.Chat currentChat;
    private int currentReactionsCount;
    private CustomReactionEditText editText;
    private TextCheckCell enableReactionsCell;
    private final TLRPC.ChatFull info;
    private boolean initialPaid;
    private boolean isPaused;
    private boolean paid;
    private TextCheckCell paidCheckCell;
    private int reactionsCount;
    private ScrollView scrollView;
    private SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    private int selectedCustomReactions;
    private SlideIntChooseView slideView;
    private LinearLayout switchLayout;
    private final HashMap selectedEmojisMap = new LinkedHashMap();
    private final List selectedEmojisIds = new ArrayList();
    private final HashMap initialSelectedEmojis = new LinkedHashMap();
    private final List allAvailableReactions = new ArrayList();
    private final int maxReactionsCount = getMessagesController().boostsChannelLevelMax;
    private boolean emojiKeyboardVisible = false;
    private int selectedType = -1;
    private final Runnable checkAfterFastDeleteRunnable = new Runnable() {
        @Override
        public final void run() {
            ChatCustomReactionsEditActivity.this.lambda$new$0();
        }
    };

    public class AnonymousClass5 extends SelectAnimatedEmojiDialog {
        private boolean firstLayout;

        AnonymousClass5(BaseFragment baseFragment, Context context, boolean z, Integer num, int i, boolean z2, Theme.ResourcesProvider resourcesProvider, int i2, int i3) {
            super(baseFragment, context, z, num, i, z2, resourcesProvider, i2, i3);
            this.firstLayout = true;
            setDrawBackground(false);
        }

        public void lambda$onEmojiSelected$0(AnimatedEmojiSpan animatedEmojiSpan) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(ChatCustomReactionsEditActivity.this.editText.getText());
            for (AnimatedEmojiSpan animatedEmojiSpan2 : (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class)) {
                if (animatedEmojiSpan2 == animatedEmojiSpan) {
                    int editTextSelectionEnd = ChatCustomReactionsEditActivity.this.editText.getEditTextSelectionEnd();
                    int spanEnd = spannableStringBuilder.getSpanEnd(animatedEmojiSpan2);
                    int spanStart = spannableStringBuilder.getSpanStart(animatedEmojiSpan2);
                    ChatCustomReactionsEditActivity.this.editText.getText().delete(spanStart, spanEnd);
                    int i = spanEnd - spanStart;
                    CustomReactionEditText customReactionEditText = ChatCustomReactionsEditActivity.this.editText;
                    if (spanEnd <= editTextSelectionEnd) {
                        editTextSelectionEnd -= i;
                    }
                    customReactionEditText.setSelection(editTextSelectionEnd);
                    return;
                }
            }
        }

        @Override
        protected void onEmojiSelected(View view, Long l, TLRPC.Document document, Integer num) {
            if (ChatCustomReactionsEditActivity.this.selectedEmojisMap.containsKey(l)) {
                ChatCustomReactionsEditActivity.this.selectedEmojisIds.remove(l);
                final AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) ChatCustomReactionsEditActivity.this.selectedEmojisMap.remove(l);
                animatedEmojiSpan.setRemoved(new Runnable() {
                    @Override
                    public final void run() {
                        ChatCustomReactionsEditActivity.AnonymousClass5.this.lambda$onEmojiSelected$0(animatedEmojiSpan);
                    }
                });
                ChatCustomReactionsEditActivity.this.animateChangesInNextRows(animatedEmojiSpan);
                ChatCustomReactionsEditActivity.this.selectAnimatedEmojiDialog.setMultiSelected(l, true);
                ChatCustomReactionsEditActivity.this.checkMaxCustomReactions(false);
                return;
            }
            if (ChatCustomReactionsEditActivity.this.selectedEmojisMap.size() - (ChatCustomReactionsEditActivity.this.selectedEmojisMap.containsKey(-1L) ? 1 : 0) >= ChatCustomReactionsEditActivity.this.maxReactionsCount) {
                BulletinFactory.of(ChatCustomReactionsEditActivity.this).createErrorBulletin(LocaleController.formatPluralString("ReactionMaxCountError", ChatCustomReactionsEditActivity.this.maxReactionsCount, new Object[0])).show();
                return;
            }
            try {
                int editTextSelectionEnd = ChatCustomReactionsEditActivity.this.editText.getEditTextSelectionEnd();
                SpannableString spannableString = new SpannableString("b");
                AnimatedEmojiSpan createAnimatedEmojiSpan = ReactionsUtils.createAnimatedEmojiSpan(document, l, ChatCustomReactionsEditActivity.this.editText.getFontMetricsInt());
                createAnimatedEmojiSpan.cacheType = AnimatedEmojiDrawable.getCacheTypeForEnterView();
                createAnimatedEmojiSpan.setAdded();
                ChatCustomReactionsEditActivity.this.selectedEmojisIds.add(editTextSelectionEnd, l);
                ChatCustomReactionsEditActivity.this.selectedEmojisMap.put(l, createAnimatedEmojiSpan);
                spannableString.setSpan(createAnimatedEmojiSpan, 0, spannableString.length(), 33);
                ChatCustomReactionsEditActivity.this.editText.getText().insert(editTextSelectionEnd, spannableString);
                ChatCustomReactionsEditActivity.this.editText.setSelection(editTextSelectionEnd + spannableString.length());
                ChatCustomReactionsEditActivity.this.selectAnimatedEmojiDialog.setMultiSelected(l, true);
                ChatCustomReactionsEditActivity.this.checkMaxCustomReactions(true);
                ChatCustomReactionsEditActivity.this.animateChangesInNextRows(createAnimatedEmojiSpan);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (this.firstLayout) {
                this.firstLayout = false;
                ChatCustomReactionsEditActivity.this.selectAnimatedEmojiDialog.onShow(null);
            }
        }
    }

    public ChatCustomReactionsEditActivity(long j, TLRPC.ChatFull chatFull) {
        this.chatId = j;
        this.info = chatFull;
    }

    public void animateChangesInNextRows(AnimatedEmojiSpan animatedEmojiSpan) {
        Editable text = this.editText.getText();
        Layout layout = this.editText.getLayout();
        int lineForOffset = layout.getLineForOffset(text.getSpanStart(animatedEmojiSpan)) + 1;
        if (lineForOffset < layout.getLineCount()) {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) text.getSpans(layout.getLineStart(lineForOffset), text.length(), AnimatedEmojiSpan.class);
            for (AnimatedEmojiSpan animatedEmojiSpan2 : animatedEmojiSpanArr) {
                animatedEmojiSpan2.setAnimateChanges();
            }
        }
    }

    public boolean checkChangesBeforeExit() {
        boolean z = !this.selectedEmojisMap.keySet().equals(this.initialSelectedEmojis.keySet());
        TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus = this.boostsStatus;
        if (tL_premium_boostsStatus != null && tL_premium_boostsStatus.level < this.selectedCustomReactions) {
            z = false;
        }
        boolean z2 = this.initialPaid == this.paid ? z : true;
        if (z2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), getResourceProvider());
            builder.setTitle(LocaleController.getString("UnsavedChanges", R.string.UnsavedChanges));
            builder.setMessage(LocaleController.getString("ReactionApplyChangesDialog", R.string.ReactionApplyChangesDialog));
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatCustomReactionsEditActivity.this.lambda$checkChangesBeforeExit$14(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Discard), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatCustomReactionsEditActivity.this.lambda$checkChangesBeforeExit$15(dialogInterface, i);
                }
            });
            builder.show();
        }
        return z2;
    }

    public void checkMaxCustomReactions(boolean z) {
        if (this.boostsStatus == null) {
            return;
        }
        if (this.selectedType == 0) {
            this.selectedType = 1;
        }
        int size = grabReactions(true).size();
        this.selectedCustomReactions = size;
        if (this.boostsStatus.level >= size) {
            this.actionButton.removeLvlRequiredState();
            return;
        }
        if (z) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, AndroidUtilities.replaceTags(LocaleController.formatPluralString("ReactionReachLvlForReactionShort", size, Integer.valueOf(size)))).show();
        }
        this.actionButton.setLvlRequiredState(this.selectedCustomReactions);
    }

    private boolean closeKeyboard() {
        if (!this.emojiKeyboardVisible) {
            return false;
        }
        this.emojiKeyboardVisible = false;
        if (isClearFocusNotWorking()) {
            this.switchLayout.setFocusableInTouchMode(true);
            this.switchLayout.requestFocus();
        } else {
            this.editText.clearFocus();
        }
        updateScrollViewMarginBottom(0);
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.bottomDialogLayout.animate().setListener(null).cancel();
        this.bottomDialogLayout.animate().translationY(this.bottomDialogLayout.getMeasuredHeight()).setDuration(350L).withLayer().setInterpolator(CubicBezierInterpolator.DEFAULT).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatCustomReactionsEditActivity.this.lambda$closeKeyboard$17(valueAnimator);
            }
        }).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
                ChatCustomReactionsEditActivity.this.bottomDialogLayout.setVisibility(4);
                if (ChatCustomReactionsEditActivity.this.isClearFocusNotWorking()) {
                    ChatCustomReactionsEditActivity.this.switchLayout.setFocusableInTouchMode(false);
                }
            }
        }).start();
        return true;
    }

    public boolean deleteSelectedEmojis() {
        int editTextSelectionEnd = this.editText.getEditTextSelectionEnd();
        int editTextSelectionStart = this.editText.getEditTextSelectionStart();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.editText.getText());
        if (!this.editText.hasSelection()) {
            return false;
        }
        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(editTextSelectionStart, editTextSelectionEnd, AnimatedEmojiSpan.class);
        for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
            this.selectedEmojisMap.remove(Long.valueOf(animatedEmojiSpan.documentId));
            this.selectedEmojisIds.remove(Long.valueOf(animatedEmojiSpan.documentId));
            this.selectAnimatedEmojiDialog.unselect(Long.valueOf(animatedEmojiSpan.documentId));
        }
        this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
        checkMaxCustomReactions(false);
        return true;
    }

    private List grabReactions(boolean z) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Long l : this.selectedEmojisIds) {
            if (l.longValue() != -1) {
                Iterator it = this.allAvailableReactions.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        TLRPC.TL_reactionCustomEmoji tL_reactionCustomEmoji = new TLRPC.TL_reactionCustomEmoji();
                        tL_reactionCustomEmoji.document_id = l.longValue();
                        arrayList.add(tL_reactionCustomEmoji);
                        arrayList2.add(tL_reactionCustomEmoji);
                        break;
                    }
                    TLRPC.TL_availableReaction tL_availableReaction = (TLRPC.TL_availableReaction) it.next();
                    if (l.longValue() == tL_availableReaction.activate_animation.id) {
                        TLRPC.TL_reactionEmoji tL_reactionEmoji = new TLRPC.TL_reactionEmoji();
                        tL_reactionEmoji.emoticon = tL_availableReaction.reaction;
                        arrayList.add(tL_reactionEmoji);
                        break;
                    }
                }
            }
        }
        return z ? arrayList2 : arrayList;
    }

    private void initSelectAnimatedEmojiDialog() {
        if (this.selectAnimatedEmojiDialog != null) {
            return;
        }
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(this, getContext(), false, null, 6, false, getResourceProvider(), 16, Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, getResourceProvider()));
        this.selectAnimatedEmojiDialog = anonymousClass5;
        anonymousClass5.setAnimationsEnabled(false);
        this.selectAnimatedEmojiDialog.setClipChildren(false);
        this.selectAnimatedEmojiDialog.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.bottomDialogLayout.addView(this.selectAnimatedEmojiDialog, LayoutHelper.createFrame(-1, -2, 80));
        BackSpaceButtonView backSpaceButtonView = new BackSpaceButtonView(getContext(), getResourceProvider());
        this.backSpaceButtonView = backSpaceButtonView;
        backSpaceButtonView.setOnBackspace(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChatCustomReactionsEditActivity.this.lambda$initSelectAnimatedEmojiDialog$11((Boolean) obj);
            }
        });
        this.bottomDialogLayout.addView(this.backSpaceButtonView, LayoutHelper.createFrame(-1, -2.0f, 85, 0.0f, 0.0f, 8.0f, 8.0f));
        Iterator it = this.selectedEmojisIds.iterator();
        while (it.hasNext()) {
            this.selectAnimatedEmojiDialog.setMultiSelected((Long) it.next(), false);
        }
    }

    public boolean isClearFocusNotWorking() {
        return Build.MODEL.toLowerCase().startsWith("zte") && Build.VERSION.SDK_INT <= 28;
    }

    public void lambda$checkChangesBeforeExit$14(DialogInterface dialogInterface, int i) {
        this.actionButton.performClick();
    }

    public void lambda$checkChangesBeforeExit$15(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    public void lambda$closeKeyboard$17(ValueAnimator valueAnimator) {
        this.actionButtonContainer.setTranslationY((-(1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue())) * this.bottomDialogLayout.getMeasuredHeight());
    }

    public void lambda$createView$2(View view) {
        TextCheckCell textCheckCell;
        if (this.enableReactionsCell.isChecked() && (textCheckCell = this.paidCheckCell) != null && textCheckCell.isChecked()) {
            toggleStarsEnabled();
        }
        setCheckedEnableReactionCell(this.enableReactionsCell.isChecked() ? 2 : 1, this.enableReactionsCell.isChecked() ? false : this.paid, true);
    }

    public void lambda$createView$3() {
        Browser.openUrl(getContext(), "https://t.me/stickers");
    }

    public void lambda$createView$4(Integer num) {
        this.reactionsCount = num.intValue();
    }

    public void lambda$createView$5(View view) {
        toggleStarsEnabled();
    }

    public void lambda$createView$6() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.ChannelEnablePaidReactionsInfoLink));
    }

    public void lambda$createView$7(TLRPC.TL_error tL_error) {
        if (this.boostsStatus != null && tL_error.text.equals("BOOSTS_REQUIRED")) {
            ReactionsUtils.showLimitReachedDialogForReactions(-this.chatId, this.selectedCustomReactions, this.boostsStatus);
            return;
        }
        String str = tL_error.text;
        if (str.equals("REACTIONS_TOO_MANY")) {
            str = LocaleController.formatPluralString("ReactionMaxCountError", this.maxReactionsCount, new Object[0]);
        }
        BulletinFactory.of(this).createErrorBulletin(str).show();
    }

    public void lambda$createView$8(final TLRPC.TL_error tL_error) {
        if (isFinishing()) {
            return;
        }
        this.actionButton.setLoading(false);
        if (tL_error.text.equals("CHAT_NOT_MODIFIED")) {
            lambda$onBackPressed$321();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatCustomReactionsEditActivity.this.lambda$createView$7(tL_error);
                }
            }, this.boostsStatus == null ? 200L : 0L);
        }
    }

    public void lambda$createView$9(View view) {
        if (this.actionButton.isLoading()) {
            return;
        }
        TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus = this.boostsStatus;
        if (tL_premium_boostsStatus != null) {
            int i = tL_premium_boostsStatus.level;
            int i2 = this.selectedCustomReactions;
            if (i < i2) {
                ReactionsUtils.showLimitReachedDialogForReactions(-this.chatId, i2, tL_premium_boostsStatus);
                return;
            }
        }
        TextCheckCell textCheckCell = this.paidCheckCell;
        Boolean valueOf = (textCheckCell == null || !this.info.paid_media_allowed) ? null : Boolean.valueOf(textCheckCell.isChecked());
        this.actionButton.setLoading(true);
        MessagesController messagesController = getMessagesController();
        long j = this.chatId;
        int i3 = this.selectedType;
        List<TLRPC.Reaction> grabReactions = grabReactions(false);
        int i4 = this.reactionsCount;
        this.currentReactionsCount = i4;
        messagesController.setCustomChatReactions(j, i3, grabReactions, i4, valueOf, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChatCustomReactionsEditActivity.this.lambda$createView$8((TLRPC.TL_error) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                ChatCustomReactionsEditActivity.this.lambda$onBackPressed$321();
            }
        });
    }

    public void lambda$initSelectAnimatedEmojiDialog$10(AnimatedEmojiSpan animatedEmojiSpan, int i) {
        Editable text = this.editText.getText();
        int spanStart = text.getSpanStart(animatedEmojiSpan);
        int spanEnd = text.getSpanEnd(animatedEmojiSpan);
        int i2 = spanEnd - spanStart;
        if (spanStart == -1 || spanEnd == -1) {
            return;
        }
        this.editText.getText().delete(spanStart, spanEnd);
        CustomReactionEditText customReactionEditText = this.editText;
        customReactionEditText.setSelection(Math.min(i - i2, customReactionEditText.getText().length()));
    }

    public void lambda$initSelectAnimatedEmojiDialog$11(Boolean bool) {
        TextCheckCell textCheckCell;
        if (deleteSelectedEmojis()) {
            return;
        }
        final int editTextSelectionEnd = this.editText.getEditTextSelectionEnd();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.editText.getText());
        for (final AnimatedEmojiSpan animatedEmojiSpan : (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class)) {
            if (spannableStringBuilder.getSpanEnd(animatedEmojiSpan) == editTextSelectionEnd) {
                this.selectedEmojisMap.remove(Long.valueOf(animatedEmojiSpan.documentId));
                this.selectedEmojisIds.remove(Long.valueOf(animatedEmojiSpan.documentId));
                this.selectAnimatedEmojiDialog.unselect(Long.valueOf(animatedEmojiSpan.documentId));
                if (animatedEmojiSpan.documentId == -1 && (textCheckCell = this.paidCheckCell) != null) {
                    textCheckCell.setChecked(false);
                    this.editText.setMaxLength(this.maxReactionsCount);
                }
                if (bool.booleanValue()) {
                    this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
                    AndroidUtilities.cancelRunOnUIThread(this.checkAfterFastDeleteRunnable);
                    AndroidUtilities.runOnUIThread(this.checkAfterFastDeleteRunnable, 350L);
                    return;
                } else {
                    animatedEmojiSpan.setRemoved(new Runnable() {
                        @Override
                        public final void run() {
                            ChatCustomReactionsEditActivity.this.lambda$initSelectAnimatedEmojiDialog$10(animatedEmojiSpan, editTextSelectionEnd);
                        }
                    });
                    animateChangesInNextRows(animatedEmojiSpan);
                    checkMaxCustomReactions(false);
                    return;
                }
            }
        }
    }

    public void lambda$new$0() {
        checkMaxCustomReactions(false);
    }

    public void lambda$onFragmentCreate$1(TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        this.boostsStatus = tL_premium_boostsStatus;
        if (!this.selectedEmojisMap.keySet().equals(this.initialSelectedEmojis.keySet())) {
            checkMaxCustomReactions(false);
        }
    }

    public void lambda$onResume$13() {
        this.editText.requestFocus();
    }

    public static void lambda$onTransitionAnimationEnd$12() {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
    }

    public void lambda$showKeyboard$16(ValueAnimator valueAnimator) {
        this.actionButtonContainer.setTranslationY((-((Float) valueAnimator.getAnimatedValue()).floatValue()) * this.bottomDialogLayout.getMeasuredHeight());
    }

    public void lambda$toggleStarsEnabled$18(AnimatedEmojiSpan animatedEmojiSpan) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.editText.getText());
        for (AnimatedEmojiSpan animatedEmojiSpan2 : (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class)) {
            if (animatedEmojiSpan2 == animatedEmojiSpan) {
                int editTextSelectionEnd = this.editText.getEditTextSelectionEnd();
                int spanEnd = spannableStringBuilder.getSpanEnd(animatedEmojiSpan2);
                int spanStart = spannableStringBuilder.getSpanStart(animatedEmojiSpan2);
                this.editText.getText().delete(spanStart, spanEnd);
                int i = spanEnd - spanStart;
                CustomReactionEditText customReactionEditText = this.editText;
                if (spanEnd <= editTextSelectionEnd) {
                    editTextSelectionEnd -= i;
                }
                customReactionEditText.setSelection(editTextSelectionEnd);
                return;
            }
        }
    }

    private void setCheckedEnableReactionCell(int i, boolean z, boolean z2) {
        if (this.selectedType == i && this.paid == z) {
            return;
        }
        this.paid = z;
        boolean z3 = i == 1 || i == 0 || z;
        this.enableReactionsCell.setChecked(z3);
        int color = Theme.getColor(z3 ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked);
        if (!z2) {
            this.enableReactionsCell.setBackgroundColor(color);
        } else if (z3) {
            this.enableReactionsCell.setBackgroundColorAnimated(true, color);
        } else {
            this.enableReactionsCell.setBackgroundColorAnimatedReverse(color);
        }
        this.selectedType = i;
        if (i != 1 && i != 0 && !z) {
            if (!z2) {
                this.switchLayout.setVisibility(4);
                this.actionButtonContainer.setVisibility(4);
                return;
            }
            closeKeyboard();
            this.actionButtonContainer.animate().setListener(null).cancel();
            this.switchLayout.animate().setListener(null).cancel();
            ViewPropertyAnimator duration = this.actionButtonContainer.animate().alpha(0.0f).setDuration(350L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ChatCustomReactionsEditActivity.this.actionButtonContainer.setVisibility(4);
                }
            }).start();
            this.switchLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ChatCustomReactionsEditActivity.this.editText.setFocusableInTouchMode(false);
                    ChatCustomReactionsEditActivity.this.switchLayout.setVisibility(4);
                }
            }).start();
            return;
        }
        this.switchLayout.setVisibility(0);
        this.actionButtonContainer.setVisibility(0);
        if (z2) {
            this.actionButtonContainer.animate().setListener(null).cancel();
            this.switchLayout.animate().setListener(null).cancel();
            ViewPropertyAnimator duration2 = this.switchLayout.animate().alpha(1.0f).setDuration(350L);
            CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.DEFAULT;
            duration2.setInterpolator(cubicBezierInterpolator2).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ChatCustomReactionsEditActivity.this.editText.setFocusableInTouchMode(true);
                }
            }).start();
            this.actionButtonContainer.animate().alpha(1.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator2).start();
            if (this.selectedEmojisMap.isEmpty()) {
                this.selectAnimatedEmojiDialog.clearSelectedDocuments();
                this.editText.setText("");
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                Iterator it = this.allAvailableReactions.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    ReactionsUtils.addReactionToEditText((TLRPC.TL_availableReaction) it.next(), this.selectedEmojisMap, this.selectedEmojisIds, spannableStringBuilder, this.selectAnimatedEmojiDialog, this.editText.getFontMetricsInt());
                    i2++;
                    if (i2 >= this.maxReactionsCount) {
                        break;
                    }
                }
                this.editText.append(spannableStringBuilder);
                this.editText.addReactionsSpan();
                this.selectAnimatedEmojiDialog.notifyDataSetChanged();
                checkMaxCustomReactions(false);
            }
        }
    }

    public void showKeyboard() {
        if (this.emojiKeyboardVisible) {
            return;
        }
        this.emojiKeyboardVisible = true;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        updateScrollViewMarginBottom(this.bottomDialogLayout.getMeasuredHeight());
        this.bottomDialogLayout.setVisibility(0);
        this.bottomDialogLayout.setTranslationY(r0.getMeasuredHeight());
        this.bottomDialogLayout.animate().setListener(null).cancel();
        this.bottomDialogLayout.animate().translationY(0.0f).withLayer().setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatCustomReactionsEditActivity.this.lambda$showKeyboard$16(valueAnimator);
            }
        }).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
            }
        }).start();
    }

    public void updateScrollViewMarginBottom(int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.scrollView.getLayoutParams();
        marginLayoutParams.bottomMargin = i;
        this.scrollView.setLayoutParams(marginLayoutParams);
    }

    @Override
    public boolean canBeginSlide() {
        if (checkChangesBeforeExit()) {
            return false;
        }
        return super.canBeginSlide();
    }

    @Override
    public android.view.View createView(android.content.Context r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.ChatCustomReactionsEditActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogDeleted && ((Long) objArr[0]).longValue() == (-this.chatId)) {
            INavigationLayout iNavigationLayout = this.parentLayout;
            if (iNavigationLayout == null || iNavigationLayout.getLastFragment() != this) {
                removeSelfFromStack();
            } else {
                lambda$onBackPressed$321();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (closeKeyboard() || checkChangesBeforeExit()) {
            return false;
        }
        return super.onBackPressed();
    }

    @Override
    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat == null) {
            TLRPC.Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
        }
        if (this.info == null) {
            return false;
        }
        getMessagesController().getBoostsController().getBoostsStats(-this.chatId, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChatCustomReactionsEditActivity.this.lambda$onFragmentCreate$1((TL_stories.TL_premium_boostsStatus) obj);
            }
        });
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        this.allAvailableReactions.addAll(getMediaDataController().getEnabledReactionsList());
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogDeleted);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.checkAfterFastDeleteRunnable);
        if (this.selectedType == 2 && this.reactionsCount != this.currentReactionsCount) {
            getMessagesController().setCustomChatReactions(this.chatId, this.selectedType, grabReactions(false), this.reactionsCount, null, null, null);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogDeleted);
    }

    @Override
    public void onPause() {
        this.isPaused = true;
        this.editText.setFocusable(false);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isPaused) {
            this.isPaused = false;
            this.editText.setFocusable(true);
            this.editText.setFocusableInTouchMode(true);
            if (this.emojiKeyboardVisible) {
                this.editText.removeReactionsSpan(false);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChatCustomReactionsEditActivity.this.lambda$onResume$13();
                    }
                }, 250L);
            }
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (z && this.selectedType != 2) {
            this.editText.setFocusableInTouchMode(true);
        }
        if (!z || z2) {
            return;
        }
        initSelectAnimatedEmojiDialog();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatCustomReactionsEditActivity.lambda$onTransitionAnimationEnd$12();
            }
        }, 200L);
    }

    public void toggleStarsEnabled() {
        long j = -1;
        if (this.paidCheckCell.isChecked()) {
            this.paidCheckCell.setChecked(false);
            this.selectedEmojisIds.remove((Object) (-1L));
            final AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) this.selectedEmojisMap.remove(-1L);
            if (animatedEmojiSpan != null) {
                animatedEmojiSpan.setRemoved(new Runnable() {
                    @Override
                    public final void run() {
                        ChatCustomReactionsEditActivity.this.lambda$toggleStarsEnabled$18(animatedEmojiSpan);
                    }
                });
            }
            animateChangesInNextRows(animatedEmojiSpan);
            this.selectAnimatedEmojiDialog.setMultiSelected(-1L, true);
            checkMaxCustomReactions(false);
            this.editText.setMaxLength(this.maxReactionsCount);
            setCheckedEnableReactionCell(this.selectedType, this.paid, true);
        } else {
            this.paidCheckCell.setChecked(true);
            try {
                this.editText.setMaxLength(this.maxReactionsCount + 1);
                SpannableString spannableString = new SpannableString("b");
                AnimatedEmojiSpan animatedEmojiSpan2 = new AnimatedEmojiSpan(j, null) {
                    private final Bitmap bitmap;

                    {
                        Bitmap createBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), Bitmap.Config.ARGB_8888);
                        this.bitmap = createBitmap;
                        Drawable mutate = ChatCustomReactionsEditActivity.this.getContext().getResources().getDrawable(R.drawable.star_small_inner).mutate();
                        mutate.setBounds(0, 0, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                        mutate.draw(new Canvas(createBitmap));
                    }

                    @Override
                    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                        super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
                        canvas.save();
                        canvas.translate(f, ((i3 + i5) / 2.0f) - AndroidUtilities.dp(12.0f));
                        float f2 = this.extraScale;
                        canvas.scale(f2, f2, f + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
                        canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, (Paint) null);
                        canvas.restore();
                    }

                    @Override
                    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                        return super.getSize(paint, charSequence, i, i2, fontMetricsInt) + AndroidUtilities.dp(5.0f);
                    }
                };
                animatedEmojiSpan2.cacheType = AnimatedEmojiDrawable.getCacheTypeForEnterView();
                animatedEmojiSpan2.setAdded();
                this.selectedEmojisIds.add(0, -1L);
                this.selectedEmojisMap.put(-1L, animatedEmojiSpan2);
                spannableString.setSpan(animatedEmojiSpan2, 0, spannableString.length(), 33);
                this.editText.getText().insert(0, spannableString);
                this.selectAnimatedEmojiDialog.setMultiSelected(-1L, true);
                checkMaxCustomReactions(true);
                animateChangesInNextRows(animatedEmojiSpan2);
            } catch (Exception e) {
                FileLog.e(e);
            }
            setCheckedEnableReactionCell(this.selectedType, true, true);
        }
        this.editText.updateAnimatedEmoji(true);
    }
}
