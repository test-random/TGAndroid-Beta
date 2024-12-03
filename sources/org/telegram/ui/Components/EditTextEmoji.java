package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class EditTextEmoji extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate {
    AdjustPanLayoutHelper adjustPanLayoutHelper;
    private boolean allowAnimatedEmoji;
    private boolean allowEmojisForNonPremium;
    private int currentStyle;
    private boolean destroyed;
    private EditTextCaption editText;
    private ImageView emojiButton;
    public boolean emojiExpanded;
    private ReplaceableIconDrawable emojiIconDrawable;
    private int emojiPadding;
    private EmojiView emojiView;
    private boolean emojiViewVisible;
    private ItemOptions formatOptions;
    public boolean includeNavigationBar;
    private int innerTextChange;
    private boolean isAnimatePopupClosing;
    private boolean isPaused;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private boolean lastEmojiExpanded;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private Runnable openKeyboardRunnable;
    private BaseFragment parentFragment;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean showKeyboardOnResume;
    private boolean shownFormatButton;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private boolean waitingForKeyboardOpen;

    public class AnonymousClass7 implements EmojiView.EmojiViewDelegate {
        AnonymousClass7() {
        }

        public void lambda$onClearEmojiRecent$0(DialogInterface dialogInterface, int i) {
            EditTextEmoji.this.emojiView.clearRecentEmoji();
        }

        @Override
        public boolean canSchedule() {
            return EmojiView.EmojiViewDelegate.CC.$default$canSchedule(this);
        }

        @Override
        public long getDialogId() {
            return EmojiView.EmojiViewDelegate.CC.$default$getDialogId(this);
        }

        @Override
        public float getProgressToSearchOpened() {
            return EmojiView.EmojiViewDelegate.CC.$default$getProgressToSearchOpened(this);
        }

        @Override
        public int getThreadId() {
            return EmojiView.EmojiViewDelegate.CC.$default$getThreadId(this);
        }

        @Override
        public void invalidateEnterView() {
            EmojiView.EmojiViewDelegate.CC.$default$invalidateEnterView(this);
        }

        @Override
        public boolean isExpanded() {
            return EmojiView.EmojiViewDelegate.CC.$default$isExpanded(this);
        }

        @Override
        public boolean isInScheduleMode() {
            return EmojiView.EmojiViewDelegate.CC.$default$isInScheduleMode(this);
        }

        @Override
        public boolean isSearchOpened() {
            return EditTextEmoji.this.emojiExpanded;
        }

        @Override
        public boolean isUserSelf() {
            return EmojiView.EmojiViewDelegate.CC.$default$isUserSelf(this);
        }

        @Override
        public void onAnimatedEmojiUnlockClick() {
            BaseFragment baseFragment = EditTextEmoji.this.parentFragment;
            if (baseFragment == null) {
                new PremiumFeatureBottomSheet(new BaseFragment() {
                    @Override
                    public Context getContext() {
                        return EditTextEmoji.this.getContext();
                    }

                    @Override
                    public int getCurrentAccount() {
                        return this.currentAccount;
                    }

                    @Override
                    public Activity getParentActivity() {
                        for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                            if (context instanceof Activity) {
                                return (Activity) context;
                            }
                        }
                        return null;
                    }

                    @Override
                    public Dialog getVisibleDialog() {
                        return new Dialog(EditTextEmoji.this.getContext()) {
                            @Override
                            public void dismiss() {
                                EditTextEmoji.this.hidePopup(false);
                                EditTextEmoji.this.closeParent();
                            }
                        };
                    }
                }, 11, false).show();
            } else {
                baseFragment.showDialog(new PremiumFeatureBottomSheet(baseFragment, 11, false));
            }
        }

        @Override
        public boolean onBackspace() {
            if (EditTextEmoji.this.editText.length() == 0) {
                return false;
            }
            EditTextEmoji.this.editText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        @Override
        public void onClearEmojiRecent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditTextEmoji.this.getContext(), EditTextEmoji.this.resourcesProvider);
            builder.setTitle(LocaleController.getString(R.string.ClearRecentEmojiTitle));
            builder.setMessage(LocaleController.getString(R.string.ClearRecentEmojiText));
            builder.setPositiveButton(LocaleController.getString(R.string.ClearButton), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    EditTextEmoji.AnonymousClass7.this.lambda$onClearEmojiRecent$0(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            if (EditTextEmoji.this.parentFragment != null) {
                EditTextEmoji.this.parentFragment.showDialog(builder.create());
            } else {
                builder.show();
            }
        }

        @Override
        public void onCustomEmojiSelected(long j, TLRPC.Document document, String str, boolean z) {
            int selectionEnd = EditTextEmoji.this.editText.getSelectionEnd();
            if (selectionEnd < 0) {
                selectionEnd = 0;
            }
            try {
                try {
                    EditTextEmoji.this.innerTextChange = 2;
                    SpannableString spannableString = new SpannableString(str);
                    AnimatedEmojiSpan animatedEmojiSpan = document != null ? new AnimatedEmojiSpan(document, EditTextEmoji.this.editText.getPaint().getFontMetricsInt()) : new AnimatedEmojiSpan(j, EditTextEmoji.this.editText.getPaint().getFontMetricsInt());
                    animatedEmojiSpan.cacheType = EditTextEmoji.this.emojiView.emojiCacheType;
                    spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                    EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(selectionEnd, spannableString));
                    int length = selectionEnd + spannableString.length();
                    EditTextEmoji.this.editText.setSelection(length, length);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                EditTextEmoji.this.innerTextChange = 0;
            } catch (Throwable th) {
                EditTextEmoji.this.innerTextChange = 0;
                throw th;
            }
        }

        @Override
        public void onEmojiSelected(String str) {
            int selectionEnd = EditTextEmoji.this.editText.getSelectionEnd();
            if (selectionEnd < 0) {
                selectionEnd = 0;
            }
            try {
                try {
                    EditTextEmoji.this.innerTextChange = 2;
                    CharSequence replaceEmoji = Emoji.replaceEmoji((CharSequence) str, EditTextEmoji.this.editText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    EditTextEmoji.this.editText.setText(EditTextEmoji.this.editText.getText().insert(selectionEnd, replaceEmoji));
                    int length = selectionEnd + replaceEmoji.length();
                    EditTextEmoji.this.editText.setSelection(length, length);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } finally {
                EditTextEmoji.this.innerTextChange = 0;
            }
        }

        @Override
        public void onEmojiSettingsClick(ArrayList arrayList) {
            EmojiView.EmojiViewDelegate.CC.$default$onEmojiSettingsClick(this, arrayList);
        }

        @Override
        public void lambda$onGifSelected$1(View view, Object obj, String str, Object obj2, boolean z, int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onGifSelected(this, view, obj, str, obj2, z, i);
        }

        @Override
        public void onSearchOpenClose(int i) {
            if (EditTextEmoji.this.allowSearch()) {
                EditTextEmoji editTextEmoji = EditTextEmoji.this;
                editTextEmoji.emojiExpanded = i != 0;
                editTextEmoji.updatedEmojiExpanded();
                if (EditTextEmoji.this.sizeNotifierLayout != null) {
                    EditTextEmoji.this.sizeNotifierLayout.notifyHeightChanged();
                }
            }
        }

        @Override
        public void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet, boolean z) {
            EmojiView.EmojiViewDelegate.CC.$default$onShowStickerSet(this, stickerSet, inputStickerSet, z);
        }

        @Override
        public void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickerSelected(this, view, document, str, obj, sendAnimationData, z, i);
        }

        @Override
        public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickerSetAdd(this, stickerSetCovered);
        }

        @Override
        public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickerSetRemove(this, stickerSetCovered);
        }

        @Override
        public void onStickersGroupClick(long j) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickersGroupClick(this, j);
        }

        @Override
        public void onStickersSettingsClick() {
            EmojiView.EmojiViewDelegate.CC.$default$onStickersSettingsClick(this);
        }

        @Override
        public void onTabOpened(int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onTabOpened(this, i);
        }

        @Override
        public void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
            EmojiView.EmojiViewDelegate.CC.$default$showTrendingStickersAlert(this, trendingStickersLayout);
        }
    }

    public interface EditTextEmojiDelegate {
    }

    public EditTextEmoji(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, int i, boolean z) {
        this(context, sizeNotifierFrameLayout, baseFragment, i, z, null);
    }

    public EditTextEmoji(Context context, final SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, final int i, boolean z, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        EditTextCaption editTextCaption;
        float f;
        float f2;
        int i2;
        float f3;
        int i3;
        float f4;
        float f5;
        ImageView imageView;
        float f6;
        float f7;
        int i4;
        float f8;
        int i5;
        FrameLayout.LayoutParams createFrame;
        this.isPaused = true;
        this.openKeyboardRunnable = new Runnable() {
            @Override
            public void run() {
                if (EditTextEmoji.this.destroyed || EditTextEmoji.this.editText == null || !EditTextEmoji.this.waitingForKeyboardOpen || EditTextEmoji.this.keyboardVisible || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet()) {
                    return;
                }
                EditTextEmoji.this.editText.requestFocus();
                AndroidUtilities.showKeyboard(EditTextEmoji.this.editText);
                AndroidUtilities.cancelRunOnUIThread(EditTextEmoji.this.openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(EditTextEmoji.this.openKeyboardRunnable, 100L);
            }
        };
        this.allowAnimatedEmoji = z;
        this.resourcesProvider = resourcesProvider;
        this.currentStyle = i;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.parentFragment = baseFragment;
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.addDelegate(this);
        EditTextCaption editTextCaption2 = new EditTextCaption(context, resourcesProvider) {
            private Drawable lastIcon = null;

            @Override
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                if (EditTextEmoji.this.allowEntities()) {
                    ChatActivity.fillActionModeMenu(menu, null, EditTextEmoji.this.currentStyle == 3);
                } else {
                    EditTextEmoji.this.extendActionMode(actionMode, menu);
                }
                super.extendActionMode(actionMode, menu);
            }

            @Override
            public int getActionModeStyle() {
                int i6 = i;
                if (i6 == 2 || i6 == 3) {
                    return 2;
                }
                return super.getActionModeStyle();
            }

            @Override
            protected void onLineCountChanged(int i6, int i7) {
                EditTextEmoji.this.onLineCountChanged(i6, i7);
            }

            @Override
            public void onSelectionChanged(int i6, int i7) {
                super.onSelectionChanged(i6, i7);
                if (EditTextEmoji.this.emojiIconDrawable != null) {
                    boolean z2 = false;
                    boolean z3 = i7 != i6;
                    if (EditTextEmoji.this.allowEntities() && z3) {
                        XiaomiUtilities.isMIUI();
                        z2 = true;
                    }
                    if (EditTextEmoji.this.shownFormatButton != z2) {
                        EditTextEmoji.this.shownFormatButton = z2;
                        ReplaceableIconDrawable replaceableIconDrawable = EditTextEmoji.this.emojiIconDrawable;
                        if (z2) {
                            this.lastIcon = replaceableIconDrawable.getIcon();
                            EditTextEmoji.this.emojiIconDrawable.setIcon(R.drawable.msg_edit, true);
                        } else {
                            replaceableIconDrawable.setIcon(this.lastIcon, true);
                            this.lastIcon = null;
                        }
                    }
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (EditTextEmoji.this.isPopupShowing() && motionEvent.getAction() == 0) {
                    EditTextEmoji.this.onWaitingForKeyboard();
                    EditTextEmoji editTextEmoji = EditTextEmoji.this;
                    if (!editTextEmoji.emojiExpanded || editTextEmoji.emojiView == null) {
                        EditTextEmoji.this.showPopup(AndroidUtilities.usingHardwareInput ? 0 : 2);
                    } else {
                        EditTextEmoji.this.emojiView.closeSearch(false);
                        EditTextEmoji editTextEmoji2 = EditTextEmoji.this;
                        editTextEmoji2.emojiExpanded = false;
                        editTextEmoji2.hidePopup(true);
                        AndroidUtilities.showKeyboard(this);
                    }
                    EditTextEmoji.this.openKeyboardInternal();
                }
                if (motionEvent.getAction() == 0) {
                    requestFocus();
                    if (!AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                }
                try {
                    return super.onTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e(e);
                    return false;
                }
            }

            @Override
            public void scrollTo(int i6, int i7) {
                if (EditTextEmoji.this.onScrollYChange(i7)) {
                    super.scrollTo(i6, i7);
                }
            }
        };
        this.editText = editTextCaption2;
        editTextCaption2.setImeOptions(268435456);
        EditTextCaption editTextCaption3 = this.editText;
        editTextCaption3.setInputType(editTextCaption3.getInputType() | 16384);
        EditTextCaption editTextCaption4 = this.editText;
        editTextCaption4.setFocusable(editTextCaption4.isEnabled());
        this.editText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.editText.setCursorWidth(1.5f);
        EditTextCaption editTextCaption5 = this.editText;
        int i6 = Theme.key_windowBackgroundWhiteBlackText;
        editTextCaption5.setCursorColor(getThemedColor(i6));
        if (i == 0) {
            this.editText.setTextSize(1, 18.0f);
            this.editText.setMaxLines(4);
            this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.editText.setBackground(null);
            this.editText.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_text_RedRegular));
            this.editText.setHintTextColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
            this.editText.setTextColor(getThemedColor(i6));
            this.editText.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), AndroidUtilities.dp(11.0f));
            editTextCaption = this.editText;
            boolean z2 = LocaleController.isRTL;
            f4 = z2 ? 11.0f : 0.0f;
            f = z2 ? 0.0f : 11.0f;
            f2 = 0.0f;
            i2 = -1;
            f3 = -2.0f;
            i3 = 19;
            f5 = 1.0f;
        } else {
            if (i == 2 || i == 3) {
                this.editText.setTextSize(1, 16.0f);
                this.editText.setMaxLines(8);
                this.editText.setGravity(19);
                this.editText.setAllowTextEntitiesIntersection(true);
                this.editText.setHintTextColor(-1929379841);
                this.editText.setTextColor(-1);
                this.editText.setCursorColor(-1);
                this.editText.setBackground(null);
                this.editText.setClipToPadding(false);
                this.editText.setPadding(0, AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f));
                this.editText.setHandlesColor(-1);
                this.editText.setHighlightColor(822083583);
                this.editText.setLinkTextColor(-12147733);
                EditTextCaption editTextCaption6 = this.editText;
                editTextCaption6.quoteColor = -1;
                editTextCaption6.setTextIsSelectable(true);
                setClipChildren(false);
                setClipToPadding(false);
                editTextCaption = this.editText;
                f = 24.0f;
                f2 = 0.0f;
                i2 = -1;
                f3 = -1.0f;
                i3 = 19;
                f4 = 40.0f;
            } else {
                this.editText.setTextSize(1, 18.0f);
                this.editText.setMaxLines(4);
                this.editText.setGravity(19);
                this.editText.setHintTextColor(getThemedColor(Theme.key_dialogTextHint));
                this.editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
                this.editText.setBackground(null);
                this.editText.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(12.0f));
                editTextCaption = this.editText;
                if (i == 4) {
                    f = 48.0f;
                    f2 = 0.0f;
                    i2 = -1;
                    f3 = -1.0f;
                    i3 = 19;
                    f4 = 14.0f;
                } else {
                    f = 0.0f;
                    f2 = 0.0f;
                    i2 = -1;
                    f3 = -1.0f;
                    i3 = 19;
                    f4 = 48.0f;
                }
            }
            f5 = 0.0f;
        }
        addView(editTextCaption, LayoutHelper.createFrame(i2, f3, i3, f4, f5, f, f2));
        ImageView imageView2 = new ImageView(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                EditTextEmoji editTextEmoji = EditTextEmoji.this;
                if (editTextEmoji.customEmojiButtonDraw(canvas, editTextEmoji.emojiButton, EditTextEmoji.this.emojiIconDrawable)) {
                    return;
                }
                super.dispatchDraw(canvas);
            }
        };
        this.emojiButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageView imageView3 = this.emojiButton;
        ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(context);
        this.emojiIconDrawable = replaceableIconDrawable;
        imageView3.setImageDrawable(replaceableIconDrawable);
        if (i == 0) {
            this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
            this.emojiIconDrawable.setIcon(R.drawable.smiles_tab_smiles, false);
            imageView = this.emojiButton;
            createFrame = LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 0.0f, 0.0f, 0.0f, 5.0f);
        } else {
            if (i == 2 || i == 3) {
                this.emojiIconDrawable.setColorFilter(new PorterDuffColorFilter(-1929379841, PorterDuff.Mode.MULTIPLY));
                this.emojiIconDrawable.setIcon(R.drawable.input_smile, false);
                imageView = this.emojiButton;
                f6 = 0.0f;
                f7 = 0.0f;
                i4 = 40;
                f8 = 40.0f;
            } else {
                ReplaceableIconDrawable replaceableIconDrawable2 = this.emojiIconDrawable;
                if (i == 4) {
                    replaceableIconDrawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
                    this.emojiIconDrawable.setIcon(R.drawable.input_smile, false);
                    imageView = this.emojiButton;
                    f6 = 0.0f;
                    f7 = 0.0f;
                    i4 = 48;
                    f8 = 48.0f;
                    i5 = 53;
                    createFrame = LayoutHelper.createFrame(i4, f8, i5, 0.0f, 0.0f, f6, f7);
                } else {
                    replaceableIconDrawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelIcons), PorterDuff.Mode.MULTIPLY));
                    this.emojiIconDrawable.setIcon(R.drawable.input_smile, false);
                    imageView = this.emojiButton;
                    f6 = 0.0f;
                    f7 = 0.0f;
                    i4 = 48;
                    f8 = 48.0f;
                }
            }
            i5 = 83;
            createFrame = LayoutHelper.createFrame(i4, f8, i5, 0.0f, 0.0f, f6, f7);
        }
        addView(imageView, createFrame);
        if (Build.VERSION.SDK_INT >= 21) {
            this.emojiButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        }
        this.emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                EditTextEmoji.this.lambda$new$0(sizeNotifierFrameLayout, resourcesProvider, view);
            }
        });
        this.emojiButton.setContentDescription(LocaleController.getString(R.string.Emoji));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$hidePopup$1(int i, ValueAnimator valueAnimator) {
        int i2;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.emojiView.setTranslationY(floatValue);
        if (i > 0 && ((i2 = this.currentStyle) == 2 || i2 == 3)) {
            this.emojiView.setAlpha(1.0f - (floatValue / i));
        }
        bottomPanelTranslationY(floatValue - i);
    }

    public void lambda$new$0(SizeNotifierFrameLayout sizeNotifierFrameLayout, Theme.ResourcesProvider resourcesProvider, View view) {
        if (!this.emojiButton.isEnabled() || this.emojiButton.getAlpha() < 0.5f) {
            return;
        }
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if (adjustPanLayoutHelper == null || !adjustPanLayoutHelper.animationInProgress()) {
            if (!this.shownFormatButton) {
                if (!isPopupShowing()) {
                    showPopup(1);
                    this.emojiView.onOpen(this.editText.length() > 0, false);
                    this.editText.requestFocus();
                    return;
                } else {
                    if (this.emojiExpanded) {
                        hidePopup(true);
                        this.emojiExpanded = false;
                        onEmojiKeyboardUpdate();
                    }
                    openKeyboardInternal();
                    return;
                }
            }
            ItemOptions itemOptions = this.formatOptions;
            if (itemOptions != null) {
                itemOptions.dismiss();
                this.formatOptions = null;
                return;
            }
            this.editText.hideActionMode();
            ItemOptions makeOptions = ItemOptions.makeOptions(sizeNotifierFrameLayout, resourcesProvider, this.emojiButton);
            makeOptions.setMaxHeight(AndroidUtilities.dp(280.0f));
            final EditTextCaption editTextCaption = this.editText;
            Objects.requireNonNull(editTextCaption);
            editTextCaption.extendActionMode(null, new MenuToItemOptions(makeOptions, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    EditTextCaption.this.performMenuAction(((Integer) obj).intValue());
                }
            }, this.editText.getOnPremiumMenuLockClickListener()));
            makeOptions.forceTop(true);
            makeOptions.show();
        }
    }

    public void lambda$showPopup$2(ValueAnimator valueAnimator) {
        int i;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.emojiView.setTranslationY(floatValue);
        int i2 = this.emojiPadding;
        if (i2 > 0 && ((i = this.currentStyle) == 2 || i == 3)) {
            this.emojiView.setAlpha(1.0f - (floatValue / i2));
        }
        bottomPanelTranslationY(floatValue);
    }

    private void onWindowSizeChanged() {
        this.sizeNotifierLayout.getHeight();
    }

    public void allowEmojisForNonPremium(boolean z) {
        this.allowEmojisForNonPremium = z;
    }

    protected boolean allowEntities() {
        int i = this.currentStyle;
        return i == 2 || i == 3;
    }

    protected boolean allowSearch() {
        return false;
    }

    protected boolean allowSearchAnimation() {
        return false;
    }

    protected void bottomPanelTranslationY(float f) {
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.editText);
    }

    protected void closeParent() {
    }

    public void collapseEmojiView() {
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.hideSearchKeyboard();
            this.emojiView.closeSearch(false);
        }
    }

    public void createEmojiView() {
        EmojiView emojiView = this.emojiView;
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            this.sizeNotifierLayout.removeView(emojiView);
            this.emojiView = null;
        }
        if (this.emojiView != null) {
            return;
        }
        BaseFragment baseFragment = this.parentFragment;
        boolean z = this.allowAnimatedEmoji;
        Context context = getContext();
        boolean allowSearch = allowSearch();
        int i = this.currentStyle;
        EmojiView emojiView2 = new EmojiView(baseFragment, z, false, false, context, allowSearch, null, null, (i == 2 || i == 3) ? false : true, this.resourcesProvider, false) {
            private boolean changedExpanded;
            private boolean lastExpanded;
            private int lastHeight;

            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (EditTextEmoji.this.currentStyle == 2 || EditTextEmoji.this.currentStyle == 3) {
                    EditTextEmoji.this.drawEmojiBackground(canvas, this);
                }
                super.dispatchDraw(canvas);
            }

            @Override
            protected void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
                int i6;
                super.onLayout(z2, i2, i3, i4, i5);
                if (EditTextEmoji.this.allowSearch()) {
                    int i7 = i5 - i3;
                    if (!this.lastExpanded && EditTextEmoji.this.emojiExpanded) {
                        this.changedExpanded = true;
                    }
                    if (this.changedExpanded && (i6 = this.lastHeight) > 0 && i7 > 0 && i7 != i6) {
                        setTranslationY(i7 - i6);
                        animate().translationY(0.0f).setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator).setDuration(250L).start();
                        this.changedExpanded = false;
                    }
                    this.lastExpanded = EditTextEmoji.this.emojiExpanded;
                    this.lastHeight = i7;
                }
            }
        };
        this.emojiView = emojiView2;
        emojiView2.allowEmojisForNonPremium(this.allowEmojisForNonPremium);
        this.emojiView.setVisibility(8);
        if (AndroidUtilities.isTablet()) {
            this.emojiView.setForseMultiwindowLayout(true);
        }
        this.emojiView.setDelegate(new AnonymousClass7());
        this.sizeNotifierLayout.addView(this.emojiView);
    }

    protected boolean customEmojiButtonDraw(Canvas canvas, View view, Drawable drawable) {
        return false;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
            EditTextCaption editTextCaption = this.editText;
            if (editTextCaption != null) {
                int currentTextColor = editTextCaption.getCurrentTextColor();
                this.editText.setTextColor(-1);
                this.editText.setTextColor(currentTextColor);
            }
        }
    }

    protected void drawEmojiBackground(Canvas canvas, View view) {
    }

    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    public EditTextCaption getEditText() {
        return this.editText;
    }

    public View getEmojiButton() {
        return this.emojiButton;
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public EmojiView getEmojiView() {
        return this.emojiView;
    }

    public int getKeyboardHeight() {
        android.graphics.Point point = AndroidUtilities.displaySize;
        int i = (point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight) + (this.includeNavigationBar ? AndroidUtilities.navigationBarHeight : 0);
        return this.emojiExpanded ? Math.min(i + AndroidUtilities.dp(200.0f), AndroidUtilities.displaySize.y) : i;
    }

    public Editable getText() {
        return this.editText.getText();
    }

    public void hideEmojiView() {
        EmojiView emojiView;
        if (!this.emojiViewVisible && (emojiView = this.emojiView) != null && emojiView.getVisibility() != 8) {
            this.emojiView.setVisibility(8);
        }
        this.emojiPadding = 0;
        boolean z = this.emojiExpanded;
        this.emojiExpanded = false;
        if (z) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.closeSearch(false);
            }
            updatedEmojiExpanded();
        }
    }

    public void hidePopup(boolean z) {
        if (isPopupShowing()) {
            showPopup(0);
        }
        if (z) {
            EmojiView emojiView = this.emojiView;
            if (emojiView == null || emojiView.getVisibility() != 0 || this.waitingForKeyboardOpen) {
                hideEmojiView();
            } else {
                final int measuredHeight = this.emojiView.getMeasuredHeight();
                if (this.emojiView.getParent() instanceof ViewGroup) {
                    measuredHeight += ((ViewGroup) this.emojiView.getParent()).getHeight() - this.emojiView.getBottom();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, measuredHeight);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EditTextEmoji.this.lambda$hidePopup$1(measuredHeight, valueAnimator);
                    }
                });
                this.isAnimatePopupClosing = true;
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        EditTextEmoji.this.isAnimatePopupClosing = false;
                        EditTextEmoji.this.emojiView.setTranslationY(0.0f);
                        EditTextEmoji.this.emojiView.setAlpha(0.0f);
                        EditTextEmoji.this.bottomPanelTranslationY(0.0f);
                        EditTextEmoji.this.hideEmojiView();
                    }
                });
                ofFloat.setDuration(250L);
                ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                ofFloat.start();
            }
        }
        boolean z2 = this.emojiExpanded;
        this.emojiExpanded = false;
        if (z2) {
            EmojiView emojiView2 = this.emojiView;
            if (emojiView2 != null) {
                emojiView2.closeSearch(false);
            }
            updatedEmojiExpanded();
        }
    }

    public boolean isAnimatePopupClosing() {
        return this.isAnimatePopupClosing;
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public boolean isPopupShowing() {
        return this.emojiViewVisible;
    }

    public boolean isPopupView(View view) {
        return view == this.emojiView;
    }

    public boolean isPopupVisible() {
        EmojiView emojiView = this.emojiView;
        return emojiView != null && emojiView.getVisibility() == 0;
    }

    public boolean isWaitingForKeyboardOpen() {
        return this.waitingForKeyboardOpen;
    }

    public int length() {
        return this.editText.length();
    }

    public void onDestroy() {
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.removeDelegate(this);
        }
    }

    protected void onEmojiKeyboardUpdate() {
    }

    public void onLineCountChanged(int i, int i2) {
    }

    public void onPause() {
        this.isPaused = true;
        closeKeyboard();
    }

    public void onResume() {
        this.isPaused = false;
        if (this.showKeyboardOnResume) {
            this.showKeyboardOnResume = false;
            this.editText.requestFocus();
            AndroidUtilities.showKeyboard(this.editText);
            if (AndroidUtilities.usingHardwareInput || this.keyboardVisible || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                return;
            }
            this.waitingForKeyboardOpen = true;
            onWaitingForKeyboard();
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
        }
    }

    protected boolean onScrollYChange(int i) {
        return true;
    }

    @Override
    public void onSizeChanged(int i, boolean z) {
        boolean z2;
        SharedPreferences.Editor edit;
        int i2;
        String str;
        int i3;
        if (i > AndroidUtilities.dp(50.0f) && ((this.keyboardVisible || (i3 = this.currentStyle) == 2 || i3 == 3) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet())) {
            if (z) {
                this.keyboardHeightLand = i;
                edit = MessagesController.getGlobalEmojiSettings().edit();
                i2 = this.keyboardHeightLand;
                str = "kbd_height_land3";
            } else {
                this.keyboardHeight = i;
                edit = MessagesController.getGlobalEmojiSettings().edit();
                i2 = this.keyboardHeight;
                str = "kbd_height";
            }
            edit.putInt(str, i2).commit();
        }
        boolean z3 = false;
        if (isPopupShowing()) {
            int i4 = (z ? this.keyboardHeightLand : this.keyboardHeight) + (this.includeNavigationBar ? AndroidUtilities.navigationBarHeight : 0);
            if (this.emojiExpanded) {
                i4 = Math.min(i4 + AndroidUtilities.dp(200.0f), AndroidUtilities.displaySize.y);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            int i5 = layoutParams.width;
            int i6 = AndroidUtilities.displaySize.x;
            if (i5 != i6 || layoutParams.height != i4) {
                layoutParams.width = i6;
                layoutParams.height = i4;
                this.emojiView.setLayoutParams(layoutParams);
                SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                if (sizeNotifierFrameLayout != null) {
                    this.emojiPadding = layoutParams.height;
                    sizeNotifierFrameLayout.requestLayout();
                    onWindowSizeChanged();
                    if (this.lastEmojiExpanded != this.emojiExpanded) {
                        onEmojiKeyboardUpdate();
                    }
                }
            }
        }
        this.lastEmojiExpanded = this.emojiExpanded;
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            if (allowSearch()) {
                if (this.editText.isFocused() && i > 0) {
                    z3 = true;
                }
                this.keyboardVisible = z3;
            }
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z4 = this.keyboardVisible;
        boolean z5 = this.editText.isFocused() && i > 0;
        this.keyboardVisible = z5;
        if (z5 && isPopupShowing()) {
            showPopup(0);
        }
        if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z4 && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    protected void onWaitingForKeyboard() {
    }

    public void openKeyboard() {
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    protected void openKeyboardInternal() {
        onWaitingForKeyboard();
        showPopup((AndroidUtilities.usingHardwareInput || this.isPaused) ? 0 : 2);
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
            return;
        }
        if (AndroidUtilities.usingHardwareInput || this.keyboardVisible || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
            return;
        }
        this.waitingForKeyboardOpen = true;
        AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
    }

    public void setAdjustPanLayoutHelper(AdjustPanLayoutHelper adjustPanLayoutHelper) {
        this.adjustPanLayoutHelper = adjustPanLayoutHelper;
    }

    public void setDelegate(EditTextEmojiDelegate editTextEmojiDelegate) {
    }

    @Override
    public void setEnabled(boolean z) {
        this.editText.setEnabled(z);
        this.emojiButton.setVisibility(z ? 0 : 8);
        int dp = AndroidUtilities.dp(this.currentStyle == 0 ? 11.0f : 8.0f);
        if (z) {
            this.editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), dp);
        } else {
            this.editText.setPadding(0, 0, 0, dp);
        }
    }

    public void setFilters(InputFilter[] inputFilterArr) {
        this.editText.setFilters(inputFilterArr);
    }

    @Override
    public void setFocusable(boolean z) {
        this.editText.setFocusable(z);
    }

    public void setHint(CharSequence charSequence) {
        this.editText.setHint(charSequence);
    }

    public void setMaxLines(int i) {
        this.editText.setMaxLines(i);
    }

    public void setSelection(int i) {
        this.editText.setSelection(i);
    }

    public void setSelection(int i, int i2) {
        this.editText.setSelection(i, i2);
    }

    public void setSizeNotifierLayout(SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout2 != null) {
            sizeNotifierFrameLayout2.removeDelegate(this);
        }
        this.sizeNotifierLayout = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.addDelegate(this);
    }

    public void setSuggestionsEnabled(boolean z) {
        int inputType = this.editText.getInputType();
        int i = !z ? 524288 | inputType : (-524289) & inputType;
        if (this.editText.getInputType() != i) {
            this.editText.setInputType(i);
        }
    }

    public void setText(CharSequence charSequence) {
        this.editText.setText(charSequence);
    }

    public void showPopup(int i) {
        ReplaceableIconDrawable replaceableIconDrawable;
        int i2;
        if (i != 1) {
            if (this.emojiButton != null) {
                if (this.currentStyle == 0) {
                    replaceableIconDrawable = this.emojiIconDrawable;
                    i2 = R.drawable.smiles_tab_smiles;
                } else {
                    replaceableIconDrawable = this.emojiIconDrawable;
                    i2 = R.drawable.input_smile;
                }
                replaceableIconDrawable.setIcon(i2, true);
            }
            if (this.emojiView != null) {
                this.emojiViewVisible = false;
                onEmojiKeyboardUpdate();
                if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    this.emojiView.setVisibility(8);
                }
            }
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
            if (sizeNotifierFrameLayout != null) {
                if (i == 0) {
                    this.emojiPadding = 0;
                }
                sizeNotifierFrameLayout.requestLayout();
                onWindowSizeChanged();
                return;
            }
            return;
        }
        EmojiView emojiView = this.emojiView;
        boolean z = emojiView != null && emojiView.getVisibility() == 0;
        createEmojiView();
        this.emojiView.setVisibility(0);
        this.emojiViewVisible = true;
        EmojiView emojiView2 = this.emojiView;
        if (this.keyboardHeight <= 0) {
            this.keyboardHeight = AndroidUtilities.isTablet() ? AndroidUtilities.dp(150.0f) : MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
        }
        if (this.keyboardHeightLand <= 0) {
            this.keyboardHeightLand = AndroidUtilities.isTablet() ? AndroidUtilities.dp(150.0f) : MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        }
        android.graphics.Point point = AndroidUtilities.displaySize;
        int i3 = (point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight) + (this.includeNavigationBar ? AndroidUtilities.navigationBarHeight : 0);
        if (this.emojiExpanded) {
            i3 = Math.min(i3 + AndroidUtilities.dp(200.0f), AndroidUtilities.displaySize.y);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView2.getLayoutParams();
        layoutParams.height = i3;
        emojiView2.setLayoutParams(layoutParams);
        if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            AndroidUtilities.hideKeyboard(this.editText);
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout2 != null) {
            this.emojiPadding = i3;
            sizeNotifierFrameLayout2.requestLayout();
            this.emojiIconDrawable.setIcon(R.drawable.input_keyboard, true);
            onWindowSizeChanged();
        }
        onEmojiKeyboardUpdate();
        if (this.keyboardVisible || z || !allowSearchAnimation()) {
            this.emojiView.setAlpha(1.0f);
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.emojiPadding, 0.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                EditTextEmoji.this.lambda$showPopup$2(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                EditTextEmoji.this.emojiView.setTranslationY(0.0f);
                EditTextEmoji.this.emojiView.setAlpha(1.0f);
                EditTextEmoji.this.bottomPanelTranslationY(0.0f);
            }
        });
        ofFloat.setDuration(250L);
        ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
        ofFloat.start();
    }

    public void updateColors() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EditTextEmoji.updateColors():void");
    }

    protected void updatedEmojiExpanded() {
    }
}
