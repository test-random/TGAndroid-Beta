package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Business.ChatAttachAlertQuickRepliesLayout;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.MentionsContainerView;
import org.telegram.ui.Components.MessagePreviewView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MessageSendPreview;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerSearchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.bots.BotWebViewMenuContainer$ActionBarColorsAnimating;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.web.BotWebViewContainer;

public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    public final Property ATTACH_ALERT_LAYOUT_TRANSLATION;
    private final Property ATTACH_ALERT_PROGRESS;
    public ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private boolean allowDrawContent;
    public boolean allowEnterCaption;
    protected boolean allowOrder;
    protected boolean allowPassConfirmationAlert;
    private SpringAnimation appearSpringAnimation;
    private final Paint attachButtonPaint;
    private int attachItemSize;
    private ChatAttachAlertAudioLayout audioLayout;
    protected int avatarPicker;
    protected boolean avatarSearch;
    public final BaseFragment baseFragment;
    private float baseSelectedTextViewTranslationY;
    private LongSparseArray botAttachLayouts;
    private boolean botButtonProgressWasVisible;
    private boolean botButtonWasVisible;
    private float botMainButtonOffsetY;
    private TextView botMainButtonTextView;
    private RadialProgressView botProgressView;
    private float bottomPannelTranslation;
    private boolean buttonPressed;
    private ButtonsAdapter buttonsAdapter;
    private AnimatorSet buttonsAnimation;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    public boolean canOpenPreview;
    public boolean captionAbove;
    private float captionEditTextTopOffset;
    protected boolean captionLimitBulletinShown;
    private final NumberTextView captionLimitView;
    private float chatActivityEnterViewAnimateFromTop;
    private int codepointCount;
    public ChatAttachAlertColorsLayout colorsLayout;
    public EditTextEmoji commentTextView;
    private int[] commentTextViewLocation;
    private AnimatorSet commentsAnimator;
    private boolean confirmationAlertShown;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius;
    public final int currentAccount;
    private AttachAlertLayout currentAttachLayout;
    private int currentLimit;
    float currentPanTranslationY;
    public Utilities.Callback2 customStickerHandler;
    private DecelerateInterpolator decelerateInterpolator;
    protected ChatAttachViewDelegate delegate;
    public boolean destroyed;
    public long dialogId;
    private ChatAttachAlertDocumentLayout documentLayout;
    private ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate documentsDelegate;
    private boolean documentsEnabled;
    protected ActionBarMenuItem doneItem;
    private int editType;
    protected MessageObject editingMessageObject;
    private boolean enterCommentEventSent;
    private ArrayList exclusionRects;
    private android.graphics.Rect exclustionRect;
    public boolean forUser;
    private final boolean forceDarkTheme;
    private FrameLayout frameLayout2;
    private float fromScrollY;
    protected FrameLayout headerView;
    public boolean inBubbleMode;
    public boolean isBizLocationPicker;
    public boolean isPhotoPicker;
    private boolean isSoundPicker;
    public boolean isStickerMode;
    public boolean isStoryAudioPicker;
    public boolean isStoryLocationPicker;
    private AttachAlertLayout[] layouts;
    private ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos;
    protected TextView mediaPreviewTextView;
    protected LinearLayout mediaPreviewView;
    public MentionsContainerView mentionContainer;
    private AnimatorSet menuAnimator;
    private boolean menuShowed;
    private MessageSendPreview messageSendPreview;
    private boolean musicEnabled;
    private AttachAlertLayout nextAttachLayout;
    private boolean openTransitionFinished;
    protected boolean openWithFrontFaceCamera;
    protected ActionBarMenuItem optionsItem;
    private boolean overrideBackgroundColor;
    private Paint paint;
    public ImageUpdater parentImageUpdater;
    public ChatActivity.ThemeDelegate parentThemeDelegate;
    private PasscodeView passcodeView;
    protected boolean paused;
    private ChatAttachAlertPhotoLayout photoLayout;
    private ChatAttachAlertPhotoLayoutPreview photoPreviewLayout;
    private boolean photosEnabled;
    public boolean pinnedToTop;
    private boolean plainTextEnabled;
    private ChatAttachAlertPollLayout pollLayout;
    private boolean pollsEnabled;
    private int previousScrollOffsetY;
    private ChatAttachAlertQuickRepliesLayout quickRepliesLayout;
    private RectF rect;
    private ChatAttachRestrictedLayout restrictedLayout;
    public int[] scrollOffsetY;
    protected ActionBarMenuItem searchItem;
    protected ImageView selectedArrowImageView;
    private View selectedCountView;
    private long selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    protected LinearLayout selectedView;
    boolean sendButtonEnabled;
    private float sendButtonEnabledProgress;
    public boolean sent;
    private ImageUpdater.AvatarFor setAvatarFor;
    private View shadow;
    private final boolean showingFromDialog;
    public SizeNotifierFrameLayout sizeNotifierFrameLayout;
    public boolean storyLocationPickerFileIsVideo;
    public double[] storyLocationPickerLatLong;
    public File storyLocationPickerPhotoFile;
    public boolean storyMediaPicker;
    private TextPaint textPaint;
    private float toScrollY;
    private ValueAnimator topBackgroundAnimator;
    public float translationProgress;
    protected boolean typeButtonsAvailable;
    private boolean videosEnabled;
    private Object viewChangeAnimator;
    private ChatActivityEnterView.SendButton writeButton;
    private FrameLayout writeButtonContainer;

    public class AnonymousClass1 implements BotWebViewContainer.Delegate {
        private ValueAnimator botButtonAnimator;
        final long val$id;
        final String val$startCommand;
        final ChatAttachAlertBotWebViewLayout val$webViewLayout;

        class C00221 extends AnimatorListenerAdapter {
            final boolean val$isVisible;

            C00221(boolean z) {
                r2 = z;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (r2) {
                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(8);
                } else {
                    ChatAttachAlert.this.botMainButtonTextView.setVisibility(8);
                }
                int dp = r2 ? AndroidUtilities.dp(36.0f) : 0;
                for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                    ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                }
                if (AnonymousClass1.this.botButtonAnimator == animator) {
                    AnonymousClass1.this.botButtonAnimator = null;
                }
            }

            @Override
            public void onAnimationStart(Animator animator) {
                if (!r2) {
                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(0.0f);
                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(0);
                    return;
                }
                ChatAttachAlert.this.botMainButtonTextView.setAlpha(0.0f);
                ChatAttachAlert.this.botMainButtonTextView.setVisibility(0);
                int dp = AndroidUtilities.dp(36.0f);
                for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                    ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                }
            }
        }

        class AnonymousClass2 extends AnimatorListenerAdapter {
            final boolean val$isProgressVisible;

            AnonymousClass2(boolean z) {
                r2 = z;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ChatAttachAlert.this.botButtonProgressWasVisible = r2;
                if (r2) {
                    return;
                }
                ChatAttachAlert.this.botProgressView.setVisibility(8);
            }
        }

        AnonymousClass1(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str, long j) {
            this.val$webViewLayout = chatAttachAlertBotWebViewLayout;
            this.val$startCommand = str;
            this.val$id = j;
        }

        public static void lambda$onCloseRequested$0(Runnable runnable) {
            if (runnable != null) {
                runnable.run();
            }
        }

        public void lambda$onSetupMainButton$6(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.buttonsRecyclerView.setAlpha(1.0f - floatValue);
            ChatAttachAlert.this.botMainButtonTextView.setAlpha(floatValue);
            ChatAttachAlert.this.botMainButtonOffsetY = floatValue * AndroidUtilities.dp(36.0f);
            ChatAttachAlert.this.shadow.setTranslationY(ChatAttachAlert.this.botMainButtonOffsetY);
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.buttonsRecyclerView.setTranslationY(chatAttachAlert.botMainButtonOffsetY);
        }

        public static void lambda$onWebAppOpenInvoice$3(ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str, String str2) {
            chatAttachAlertBotWebViewLayout.getWebViewContainer().onInvoiceStatusUpdate(str, str2);
        }

        public static void lambda$onWebAppOpenInvoice$4(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            chatAttachAlertBotWebViewLayout.getWebViewContainer().onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        public void lambda$onWebAppSetActionBarColor$1(int i, int i2, ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, BotWebViewMenuContainer$ActionBarColorsAnimating botWebViewMenuContainer$ActionBarColorsAnimating, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.actionBar.setBackgroundColor(ColorUtils.blendARGB(i, i2, floatValue));
            chatAttachAlertBotWebViewLayout.setCustomActionBarBackground(ColorUtils.blendARGB(i, i2, floatValue));
            ChatAttachAlert.this.currentAttachLayout.invalidate();
            ChatAttachAlert.this.sizeNotifierFrameLayout.invalidate();
            botWebViewMenuContainer$ActionBarColorsAnimating.updateActionBar(ChatAttachAlert.this.actionBar, floatValue);
        }

        public boolean lambda$onWebAppSwitchInlineQuery$5(TLRPC.User user, String str, OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
            String str2;
            long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
            } else {
                if (DialogObject.isUserDialog(j)) {
                    str2 = "user_id";
                } else {
                    j = -j;
                    str2 = "chat_id";
                }
                bundle.putLong(str2, j);
            }
            bundle.putString("start_text", "@" + UserObject.getPublicUsername(user) + " " + str);
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            BaseFragment baseFragment = chatAttachAlert.baseFragment;
            if (MessagesController.getInstance(chatAttachAlert.currentAccount).checkCanOpenChat(bundle, baseFragment)) {
                overlayActionBarLayoutDialog.dismiss();
                ChatAttachAlert.this.dismiss(true);
                baseFragment.presentFragment(new INavigationLayout.NavigationParams(new ChatActivity(bundle)).setRemoveLast(true));
            }
            return true;
        }

        @Override
        public boolean isClipboardAvailable() {
            return MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).botInAttachMenu(this.val$id);
        }

        @Override
        public void onCloseRequested(final Runnable runnable) {
            if (ChatAttachAlert.this.currentAttachLayout != this.val$webViewLayout) {
                return;
            }
            ChatAttachAlert.this.setFocusable(false);
            ChatAttachAlert.this.getWindow().setSoftInputMode(48);
            ChatAttachAlert.this.dismiss();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlert.AnonymousClass1.lambda$onCloseRequested$0(runnable);
                }
            }, 150L);
        }

        @Override
        public void onCloseToTabs() {
            onCloseRequested(null);
        }

        @Override
        public void onInstantClose() {
            onCloseRequested(null);
        }

        @Override
        public void onSendWebViewData(String str) {
            BotWebViewContainer.Delegate.CC.$default$onSendWebViewData(this, str);
        }

        @Override
        public void onSetBackButtonVisible(boolean z) {
            AndroidUtilities.updateImageViewImageAnimated(ChatAttachAlert.this.actionBar.getBackButton(), z ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
        }

        @Override
        public void onSetSettingsButtonVisible(boolean z) {
            ActionBarMenuSubItem actionBarMenuSubItem = this.val$webViewLayout.settingsItem;
            if (actionBarMenuSubItem != null) {
                actionBarMenuSubItem.setVisibility(z ? 0 : 8);
            }
        }

        @Override
        public void onSetupMainButton(boolean z, boolean z2, String str, int i, int i2, boolean z3, boolean z4) {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout == chatAttachAlertBotWebViewLayout) {
                if (chatAttachAlertBotWebViewLayout.isBotButtonAvailable() || this.val$startCommand != null) {
                    ChatAttachAlert.this.botMainButtonTextView.setClickable(z2);
                    ChatAttachAlert.this.botMainButtonTextView.setText(str);
                    ChatAttachAlert.this.botMainButtonTextView.setTextColor(i2);
                    ChatAttachAlert.this.botMainButtonTextView.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(i));
                    if (ChatAttachAlert.this.botButtonWasVisible != z) {
                        ChatAttachAlert.this.botButtonWasVisible = z;
                        ValueAnimator valueAnimator = this.botButtonAnimator;
                        if (valueAnimator != null) {
                            valueAnimator.cancel();
                        }
                        ValueAnimator duration = ValueAnimator.ofFloat(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f).setDuration(250L);
                        this.botButtonAnimator = duration;
                        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                ChatAttachAlert.AnonymousClass1.this.lambda$onSetupMainButton$6(valueAnimator2);
                            }
                        });
                        this.botButtonAnimator.addListener(new AnimatorListenerAdapter() {
                            final boolean val$isVisible;

                            C00221(boolean z5) {
                                r2 = z5;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if (r2) {
                                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(8);
                                } else {
                                    ChatAttachAlert.this.botMainButtonTextView.setVisibility(8);
                                }
                                int dp = r2 ? AndroidUtilities.dp(36.0f) : 0;
                                for (int i3 = 0; i3 < ChatAttachAlert.this.botAttachLayouts.size(); i3++) {
                                    ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i3)).setMeasureOffsetY(dp);
                                }
                                if (AnonymousClass1.this.botButtonAnimator == animator) {
                                    AnonymousClass1.this.botButtonAnimator = null;
                                }
                            }

                            @Override
                            public void onAnimationStart(Animator animator) {
                                if (!r2) {
                                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(0.0f);
                                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(0);
                                    return;
                                }
                                ChatAttachAlert.this.botMainButtonTextView.setAlpha(0.0f);
                                ChatAttachAlert.this.botMainButtonTextView.setVisibility(0);
                                int dp = AndroidUtilities.dp(36.0f);
                                for (int i3 = 0; i3 < ChatAttachAlert.this.botAttachLayouts.size(); i3++) {
                                    ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i3)).setMeasureOffsetY(dp);
                                }
                            }
                        });
                        this.botButtonAnimator.start();
                    }
                    ChatAttachAlert.this.botProgressView.setProgressColor(i2);
                    if (ChatAttachAlert.this.botButtonProgressWasVisible != z3) {
                        ChatAttachAlert.this.botProgressView.animate().cancel();
                        if (z3) {
                            ChatAttachAlert.this.botProgressView.setAlpha(0.0f);
                            ChatAttachAlert.this.botProgressView.setVisibility(0);
                        }
                        ChatAttachAlert.this.botProgressView.animate().alpha(z3 ? 1.0f : 0.0f).scaleX(z3 ? 1.0f : 0.1f).scaleY(z3 ? 1.0f : 0.1f).setDuration(250L).setListener(new AnimatorListenerAdapter() {
                            final boolean val$isProgressVisible;

                            AnonymousClass2(boolean z32) {
                                r2 = z32;
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                ChatAttachAlert.this.botButtonProgressWasVisible = r2;
                                if (r2) {
                                    return;
                                }
                                ChatAttachAlert.this.botProgressView.setVisibility(8);
                            }
                        }).start();
                    }
                }
            }
        }

        @Override
        public void onSetupSecondaryButton(boolean z, boolean z2, String str, int i, int i2, boolean z3, boolean z4, String str2) {
        }

        @Override
        public void onWebAppBackgroundChanged(boolean z, int i) {
            BotWebViewContainer.Delegate.CC.$default$onWebAppBackgroundChanged(this, z, i);
        }

        @Override
        public void onWebAppExpand() {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.canExpandByRequest()) {
                this.val$webViewLayout.scrollToTop();
            }
        }

        @Override
        public void onWebAppOpenInvoice(TLRPC.InputInvoice inputInvoice, final String str, TLObject tLObject) {
            PaymentFormActivity paymentFormActivity;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            BaseFragment baseFragment = chatAttachAlert.baseFragment;
            if (tLObject instanceof TLRPC.TL_payments_paymentFormStars) {
                final AlertDialog alertDialog = new AlertDialog(ChatAttachAlert.this.getContext(), 3);
                alertDialog.showDelayed(150L);
                StarsController starsController = StarsController.getInstance(ChatAttachAlert.this.currentAccount);
                TLRPC.TL_payments_paymentFormStars tL_payments_paymentFormStars = (TLRPC.TL_payments_paymentFormStars) tLObject;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        AlertDialog.this.dismiss();
                    }
                };
                final ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
                starsController.openPaymentForm(null, inputInvoice, tL_payments_paymentFormStars, runnable, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ChatAttachAlert.AnonymousClass1.lambda$onWebAppOpenInvoice$3(ChatAttachAlertBotWebViewLayout.this, str, (String) obj);
                    }
                });
                AndroidUtilities.hideKeyboard(this.val$webViewLayout);
                return;
            }
            if (tLObject instanceof TLRPC.PaymentForm) {
                TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
                MessagesController.getInstance(chatAttachAlert.currentAccount).putUsers(paymentForm.users, false);
                paymentFormActivity = new PaymentFormActivity(paymentForm, str, baseFragment);
            } else {
                paymentFormActivity = tLObject instanceof TLRPC.PaymentReceipt ? new PaymentFormActivity((TLRPC.PaymentReceipt) tLObject) : null;
            }
            if (paymentFormActivity != null) {
                this.val$webViewLayout.scrollToTop();
                AndroidUtilities.hideKeyboard(this.val$webViewLayout);
                final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(baseFragment.getParentActivity(), ((BottomSheet) ChatAttachAlert.this).resourcesProvider);
                overlayActionBarLayoutDialog.show();
                final ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout2 = this.val$webViewLayout;
                paymentFormActivity.setPaymentFormCallback(new PaymentFormActivity.PaymentFormCallback() {
                    @Override
                    public final void onInvoiceStatusChanged(PaymentFormActivity.InvoiceStatus invoiceStatus) {
                        ChatAttachAlert.AnonymousClass1.lambda$onWebAppOpenInvoice$4(OverlayActionBarLayoutDialog.this, chatAttachAlertBotWebViewLayout2, str, invoiceStatus);
                    }
                });
                paymentFormActivity.setResourcesProvider(((BottomSheet) ChatAttachAlert.this).resourcesProvider);
                overlayActionBarLayoutDialog.addFragment(paymentFormActivity);
            }
        }

        @Override
        public void onWebAppReady() {
            BotWebViewContainer.Delegate.CC.$default$onWebAppReady(this);
        }

        @Override
        public void onWebAppSetActionBarColor(int i, final int i2, boolean z) {
            final int color = ((ColorDrawable) ChatAttachAlert.this.actionBar.getBackground()).getColor();
            final BotWebViewMenuContainer$ActionBarColorsAnimating botWebViewMenuContainer$ActionBarColorsAnimating = new BotWebViewMenuContainer$ActionBarColorsAnimating();
            botWebViewMenuContainer$ActionBarColorsAnimating.setFrom(ChatAttachAlert.this.overrideBackgroundColor ? color : 0, ((BottomSheet) ChatAttachAlert.this).resourcesProvider);
            ChatAttachAlert.this.overrideBackgroundColor = z;
            botWebViewMenuContainer$ActionBarColorsAnimating.setTo(ChatAttachAlert.this.overrideBackgroundColor ? i2 : 0, ((BottomSheet) ChatAttachAlert.this).resourcesProvider);
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            final ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlert.AnonymousClass1.this.lambda$onWebAppSetActionBarColor$1(color, i2, chatAttachAlertBotWebViewLayout, botWebViewMenuContainer$ActionBarColorsAnimating, valueAnimator);
                }
            });
            duration.start();
        }

        @Override
        public void onWebAppSetBackgroundColor(int i) {
            this.val$webViewLayout.setCustomBackground(i);
        }

        @Override
        public void onWebAppSetNavigationBarColor(int i) {
            BotWebViewContainer.Delegate.CC.$default$onWebAppSetNavigationBarColor(this, i);
        }

        @Override
        public void onWebAppSetupClosingBehavior(boolean z) {
            this.val$webViewLayout.setNeedCloseConfirmation(z);
        }

        @Override
        public void onWebAppSwipingBehavior(boolean z) {
            this.val$webViewLayout.setAllowSwipes(z);
        }

        @Override
        public void onWebAppSwitchInlineQuery(final TLRPC.User user, final String str, List list) {
            if (list.isEmpty()) {
                BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                if (baseFragment instanceof ChatActivity) {
                    ((ChatActivity) baseFragment).getChatActivityEnterView().setFieldText("@" + UserObject.getPublicUsername(user) + " " + str);
                }
                ChatAttachAlert.this.dismiss(true);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 14);
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("allowGroups", list.contains("groups"));
            bundle.putBoolean("allowLegacyGroups", list.contains("groups"));
            bundle.putBoolean("allowMegagroups", list.contains("groups"));
            bundle.putBoolean("allowUsers", list.contains("users"));
            bundle.putBoolean("allowChannels", list.contains("channels"));
            bundle.putBoolean("allowBots", list.contains("bots"));
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(ChatAttachAlert.this.getContext(), ((BottomSheet) ChatAttachAlert.this).resourcesProvider);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
                    boolean lambda$onWebAppSwitchInlineQuery$5;
                    lambda$onWebAppSwitchInlineQuery$5 = ChatAttachAlert.AnonymousClass1.this.lambda$onWebAppSwitchInlineQuery$5(user, str, overlayActionBarLayoutDialog, dialogsActivity2, arrayList, charSequence, z, z2, i, topicsFragment);
                    return lambda$onWebAppSwitchInlineQuery$5;
                }
            });
            overlayActionBarLayoutDialog.show();
            overlayActionBarLayoutDialog.addFragment(dialogsActivity);
        }
    }

    public class AnonymousClass10 extends RecyclerListView {
        AnonymousClass10(Context context) {
            super(context);
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            ChatAttachAlert.this.currentAttachLayout.onButtonsTranslationYUpdated();
        }
    }

    public class AnonymousClass11 extends FrameLayout {
        private int color;
        private final Paint p;
        final boolean val$forceDarkTheme;

        AnonymousClass11(Context context, boolean z) {
            super(context);
            this.val$forceDarkTheme = z;
            this.p = new Paint();
        }

        public void lambda$onDraw$0(ValueAnimator valueAnimator) {
            ChatAttachAlert.this.captionEditTextTopOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatAttachAlert.this.frameLayout2.invalidate();
            invalidate();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int themedColor;
            if (ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop != 0.0f && ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop != ChatAttachAlert.this.frameLayout2.getTop() + ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop) {
                if (ChatAttachAlert.this.topBackgroundAnimator != null) {
                    ChatAttachAlert.this.topBackgroundAnimator.cancel();
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.captionEditTextTopOffset = chatAttachAlert.chatActivityEnterViewAnimateFromTop - (ChatAttachAlert.this.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset);
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                chatAttachAlert2.topBackgroundAnimator = ValueAnimator.ofFloat(chatAttachAlert2.captionEditTextTopOffset, 0.0f);
                ChatAttachAlert.this.topBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatAttachAlert.AnonymousClass11.this.lambda$onDraw$0(valueAnimator);
                    }
                });
                ChatAttachAlert.this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ChatAttachAlert.this.topBackgroundAnimator.setDuration(200L);
                ChatAttachAlert.this.topBackgroundAnimator.start();
                ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop = 0.0f;
            }
            float measuredHeight = (ChatAttachAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f)) * (1.0f - getAlpha());
            View view = ChatAttachAlert.this.shadow;
            float f = (-(ChatAttachAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f))) + ChatAttachAlert.this.captionEditTextTopOffset;
            ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
            view.setTranslationY(f + chatAttachAlert3.currentPanTranslationY + chatAttachAlert3.bottomPannelTranslation + measuredHeight + ChatAttachAlert.this.botMainButtonOffsetY);
            if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                themedColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
            } else {
                themedColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
            }
            if (this.color != themedColor) {
                this.color = themedColor;
                this.p.setColor(themedColor);
            }
            canvas.drawRect(0.0f, ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight(), this.p);
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
            invalidate();
        }
    }

    public class AnonymousClass12 extends EditTextEmoji {
        private ValueAnimator messageEditTextAnimator;
        private int messageEditTextPredrawHeigth;
        private int messageEditTextPredrawScrollY;
        private boolean shouldAnimateEditTextWithBounds;

        AnonymousClass12(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, int i, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context, sizeNotifierFrameLayout, baseFragment, i, z, resourcesProvider);
        }

        public void lambda$dispatchDraw$0(EditTextCaption editTextCaption, ValueAnimator valueAnimator) {
            editTextCaption.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
            ChatAttachAlert.this.updateCommentTextViewPosition();
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout) {
                ChatAttachAlert.this.photoLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            }
        }

        @Override
        protected void bottomPanelTranslationY(float f) {
            ChatAttachAlert.this.bottomPannelTranslation = f;
            ChatAttachAlert.this.frameLayout2.setTranslationY(f);
            ChatAttachAlert.this.writeButtonContainer.setTranslationY(f);
            ChatAttachAlert.this.frameLayout2.invalidate();
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, true, 0);
        }

        @Override
        protected void closeParent() {
            ChatAttachAlert.super.dismiss();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.shouldAnimateEditTextWithBounds) {
                final EditTextCaption editText = ChatAttachAlert.this.commentTextView.getEditText();
                editText.setOffsetY(editText.getOffsetY() - ((this.messageEditTextPredrawHeigth - editText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - editText.getScrollY())));
                ValueAnimator ofFloat = ValueAnimator.ofFloat(editText.getOffsetY(), 0.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ChatAttachAlert.AnonymousClass12.this.lambda$dispatchDraw$0(editText, valueAnimator);
                    }
                });
                ValueAnimator valueAnimator = this.messageEditTextAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.messageEditTextAnimator = ofFloat;
                ofFloat.setDuration(200L);
                ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ofFloat.start();
                this.shouldAnimateEditTextWithBounds = false;
            }
            super.dispatchDraw(canvas);
        }

        @Override
        public void extendActionMode(ActionMode actionMode, Menu menu) {
            BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                ChatActivity.fillActionModeMenu(menu, ((ChatActivity) baseFragment).getCurrentEncryptedChat(), true);
            }
            super.extendActionMode(actionMode, menu);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            ChatAttachAlert chatAttachAlert;
            EditTextCaption editText;
            boolean z;
            if (!ChatAttachAlert.this.enterCommentEventSent) {
                if (motionEvent.getX() <= ChatAttachAlert.this.commentTextView.getEditText().getLeft() || motionEvent.getX() >= ChatAttachAlert.this.commentTextView.getEditText().getRight() || motionEvent.getY() <= ChatAttachAlert.this.commentTextView.getEditText().getTop() || motionEvent.getY() >= ChatAttachAlert.this.commentTextView.getEditText().getBottom()) {
                    chatAttachAlert = ChatAttachAlert.this;
                    editText = chatAttachAlert.commentTextView.getEditText();
                    z = false;
                } else {
                    chatAttachAlert = ChatAttachAlert.this;
                    editText = chatAttachAlert.commentTextView.getEditText();
                    z = true;
                }
                chatAttachAlert.makeFocusable(editText, z);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatAttachAlert.this.updateCommentTextViewPosition();
        }

        @Override
        protected void onLineCountChanged(int i, int i2) {
            if (TextUtils.isEmpty(getEditText().getText())) {
                getEditText().animate().cancel();
                getEditText().setOffsetY(0.0f);
                this.shouldAnimateEditTextWithBounds = false;
            } else {
                this.shouldAnimateEditTextWithBounds = true;
                this.messageEditTextPredrawHeigth = getEditText().getMeasuredHeight();
                this.messageEditTextPredrawScrollY = getEditText().getScrollY();
                invalidate();
            }
            ChatAttachAlert.this.chatActivityEnterViewAnimateFromTop = r2.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset;
            ChatAttachAlert.this.frameLayout2.invalidate();
            ChatAttachAlert.this.updateCommentTextViewPosition();
        }
    }

    public class AnonymousClass13 implements TextWatcher {
        private boolean processChange;
        private boolean wasEmpty;

        class AnonymousClass1 extends AnimatorListenerAdapter {
            AnonymousClass1() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ChatAttachAlert.this.captionLimitView.setVisibility(8);
            }
        }

        AnonymousClass13() {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int i;
            boolean z = true;
            if (this.wasEmpty != TextUtils.isEmpty(editable)) {
                if (ChatAttachAlert.this.currentAttachLayout != null) {
                    ChatAttachAlert.this.currentAttachLayout.onSelectedItemsCountChanged(ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount());
                }
                this.wasEmpty = !this.wasEmpty;
            }
            if (this.processChange) {
                for (ImageSpan imageSpan : (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class)) {
                    editable.removeSpan(imageSpan);
                }
                Emoji.replaceEmoji((CharSequence) editable, ChatAttachAlert.this.commentTextView.getEditText().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                this.processChange = false;
            }
            ChatAttachAlert.this.codepointCount = Character.codePointCount(editable, 0, editable.length());
            if (ChatAttachAlert.this.currentLimit <= 0 || (i = ChatAttachAlert.this.currentLimit - ChatAttachAlert.this.codepointCount) > 100) {
                ChatAttachAlert.this.captionLimitView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(100L).setListener(new AnimatorListenerAdapter() {
                    AnonymousClass1() {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ChatAttachAlert.this.captionLimitView.setVisibility(8);
                    }
                });
            } else {
                if (i < -9999) {
                    i = -9999;
                }
                ChatAttachAlert.this.captionLimitView.setNumber(i, ChatAttachAlert.this.captionLimitView.getVisibility() == 0);
                if (ChatAttachAlert.this.captionLimitView.getVisibility() != 0) {
                    ChatAttachAlert.this.captionLimitView.setVisibility(0);
                    ChatAttachAlert.this.captionLimitView.setAlpha(0.0f);
                    ChatAttachAlert.this.captionLimitView.setScaleX(0.5f);
                    ChatAttachAlert.this.captionLimitView.setScaleY(0.5f);
                }
                ChatAttachAlert.this.captionLimitView.animate().setListener(null).cancel();
                ChatAttachAlert.this.captionLimitView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).start();
                if (i < 0) {
                    ChatAttachAlert.this.captionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_text_RedRegular));
                    z = false;
                } else {
                    ChatAttachAlert.this.captionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
                }
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (chatAttachAlert.sendButtonEnabled != z) {
                chatAttachAlert.sendButtonEnabled = z;
                chatAttachAlert.writeButton.invalidate();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (i3 - i2 >= 1) {
                this.processChange = true;
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (chatAttachAlert.mentionContainer == null) {
                chatAttachAlert.createMentionsContainer();
            }
            if (ChatAttachAlert.this.mentionContainer.getAdapter() != null) {
                ChatAttachAlert.this.mentionContainer.getAdapter().lambda$searchUsernameOrHashtag$7(charSequence, ChatAttachAlert.this.commentTextView.getEditText().getSelectionStart(), null, false, false);
            }
        }
    }

    public class AnonymousClass14 extends FrameLayout {
        AnonymousClass14(Context context) {
            super(context);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            String formatPluralString;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout) {
                formatPluralString = LocaleController.formatPluralString("AccDescrSendPhotos", ChatAttachAlert.this.photoLayout.getSelectedItemsCount(), new Object[0]);
            } else {
                if (ChatAttachAlert.this.currentAttachLayout != ChatAttachAlert.this.documentLayout) {
                    if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.audioLayout) {
                        formatPluralString = LocaleController.formatPluralString("AccDescrSendAudio", ChatAttachAlert.this.audioLayout.getSelectedItemsCount(), new Object[0]);
                    }
                    accessibilityNodeInfo.setClassName(Button.class.getName());
                    accessibilityNodeInfo.setLongClickable(true);
                    accessibilityNodeInfo.setClickable(true);
                }
                formatPluralString = LocaleController.formatPluralString("AccDescrSendFiles", ChatAttachAlert.this.documentLayout.getSelectedItemsCount(), new Object[0]);
            }
            accessibilityNodeInfo.setText(formatPluralString);
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setLongClickable(true);
            accessibilityNodeInfo.setClickable(true);
        }
    }

    public class AnonymousClass15 extends ChatActivityEnterView.SendButton {
        AnonymousClass15(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context, i, resourcesProvider);
        }

        @Override
        public int getFillColor() {
            return ChatAttachAlert.this.getThemedColor(Theme.key_dialogFloatingButton);
        }

        @Override
        public boolean isInScheduleMode() {
            return super.isInScheduleMode();
        }

        @Override
        public boolean isInactive() {
            return !ChatAttachAlert.this.sendButtonEnabled;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public boolean shouldDrawBackground() {
            return true;
        }
    }

    public class AnonymousClass16 extends View {
        AnonymousClass16(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            String format = String.format("%d", Integer.valueOf(Math.max(1, ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount())));
            int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(ChatAttachAlert.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
            int measuredWidth = getMeasuredWidth() / 2;
            int themedColor = ChatAttachAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck);
            TextPaint textPaint = ChatAttachAlert.this.textPaint;
            double alpha = Color.alpha(themedColor);
            double d = ChatAttachAlert.this.sendButtonEnabledProgress;
            Double.isNaN(d);
            Double.isNaN(alpha);
            textPaint.setColor(ColorUtils.setAlphaComponent(themedColor, (int) (alpha * ((d * 0.42d) + 0.58d))));
            ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogBackground));
            int i = max / 2;
            ChatAttachAlert.this.rect.set(measuredWidth - i, 0.0f, i + measuredWidth, getMeasuredHeight());
            canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ChatAttachAlert.this.paint);
            ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_chat_attachCheckBoxBackground));
            ChatAttachAlert.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
            canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ChatAttachAlert.this.paint);
            canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), ChatAttachAlert.this.textPaint);
        }
    }

    public class AnonymousClass17 extends AnimatorListenerAdapter {
        final Runnable val$onEnd;
        final int val$t;

        AnonymousClass17(int i, Runnable runnable) {
            this.val$t = i;
            this.val$onEnd = runnable;
        }

        public void lambda$onAnimationEnd$0(androidx.dynamicanimation.animation.DynamicAnimation r1, float r2, float r3) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass17.lambda$onAnimationEnd$0(androidx.dynamicanimation.animation.DynamicAnimation, float, float):void");
        }

        public void lambda$onAnimationEnd$1(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            ChatAttachAlert.this.nextAttachLayout.setTranslationY(0.0f);
            ChatAttachAlert.this.nextAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            runnable.run();
            ChatAttachAlert.this.updateSelectedPosition(0);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            ChatAttachAlert.this.currentAttachLayout.setAlpha(0.0f);
            ChatAttachAlert.this.currentAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f) + this.val$t);
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.ATTACH_ALERT_LAYOUT_TRANSLATION.set(chatAttachAlert.currentAttachLayout, Float.valueOf(1.0f));
            ChatAttachAlert.this.actionBar.setAlpha(0.0f);
            SpringAnimation springAnimation = new SpringAnimation(ChatAttachAlert.this.nextAttachLayout, DynamicAnimation.TRANSLATION_Y, 0.0f);
            springAnimation.getSpring().setDampingRatio(0.75f);
            springAnimation.getSpring().setStiffness(500.0f);
            springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    ChatAttachAlert.AnonymousClass17.this.lambda$onAnimationEnd$0(dynamicAnimation, f, f2);
                }
            });
            final Runnable runnable = this.val$onEnd;
            springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    ChatAttachAlert.AnonymousClass17.this.lambda$onAnimationEnd$1(runnable, dynamicAnimation, z, f, f2);
                }
            });
            ChatAttachAlert.this.viewChangeAnimator = springAnimation;
            springAnimation.start();
        }
    }

    public class AnonymousClass18 implements ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate {
        AnonymousClass18() {
        }

        @Override
        public void didSelectContact(TLRPC.User user, boolean z, int i, long j, boolean z2) {
            ((ChatActivity) ChatAttachAlert.this.baseFragment).sendContact(user, z, i, j, z2);
        }

        @Override
        public void didSelectContacts(ArrayList arrayList, String str, boolean z, int i, long j, boolean z2) {
            ((ChatActivity) ChatAttachAlert.this.baseFragment).sendContacts(arrayList, str, z, i, j, z2);
        }
    }

    public class AnonymousClass19 implements ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate {
        AnonymousClass19() {
        }

        @Override
        public void didSelectFiles(ArrayList arrayList, String str, ArrayList arrayList2, boolean z, int i, long j, boolean z2) {
            if (ChatAttachAlert.this.documentsDelegate != null) {
                ChatAttachAlert.this.documentsDelegate.didSelectFiles(arrayList, str, arrayList2, z, i, j, z2);
                return;
            }
            Object obj = ChatAttachAlert.this.baseFragment;
            if (obj instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) obj).didSelectFiles(arrayList, str, arrayList2, z, i, j, z2);
            } else if (obj instanceof PassportActivity) {
                ((PassportActivity) obj).didSelectFiles(arrayList, str, z, i, j, z2);
            }
        }

        @Override
        public void didSelectPhotos(ArrayList arrayList, boolean z, int i) {
            if (ChatAttachAlert.this.documentsDelegate != null) {
                ChatAttachAlert.this.documentsDelegate.didSelectPhotos(arrayList, z, i);
                return;
            }
            BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                ((ChatActivity) baseFragment).didSelectPhotos(arrayList, z, i);
            } else if (baseFragment instanceof PassportActivity) {
                ((PassportActivity) baseFragment).didSelectPhotos(arrayList, z, i);
            }
        }

        @Override
        public void startDocumentSelectActivity() {
            ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate documentSelectActivityDelegate;
            if (ChatAttachAlert.this.documentsDelegate != null) {
                documentSelectActivityDelegate = ChatAttachAlert.this.documentsDelegate;
            } else {
                Object obj = ChatAttachAlert.this.baseFragment;
                if (!(obj instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate)) {
                    if (obj instanceof PassportActivity) {
                        ((PassportActivity) obj).startDocumentSelectActivity();
                        return;
                    }
                    return;
                }
                documentSelectActivityDelegate = (ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) obj;
            }
            documentSelectActivityDelegate.startDocumentSelectActivity();
        }

        @Override
        public void startMusicSelectActivity() {
            ChatAttachAlert.this.openAudioLayout(true);
        }
    }

    public class AnonymousClass2 extends AnimationProperties.FloatProperty {
        AnonymousClass2(String str) {
            super(str);
        }

        @Override
        public Float get(AttachAlertLayout attachAlertLayout) {
            return Float.valueOf(ChatAttachAlert.this.translationProgress);
        }

        @Override
        public void setValue(AttachAlertLayout attachAlertLayout, float f) {
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.translationProgress = f;
            if (chatAttachAlert.nextAttachLayout == null) {
                return;
            }
            if ((ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                int max = Math.max(ChatAttachAlert.this.nextAttachLayout.getWidth(), ChatAttachAlert.this.currentAttachLayout.getWidth());
                if (ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationX((-max) * f);
                    ChatAttachAlert.this.nextAttachLayout.setTranslationX((1.0f - f) * max);
                } else {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationX(max * f);
                    ChatAttachAlert.this.nextAttachLayout.setTranslationX((-max) * (1.0f - f));
                }
            } else {
                ChatAttachAlert.this.nextAttachLayout.setAlpha(f);
                ChatAttachAlert.this.nextAttachLayout.onHideShowProgress(f);
                if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout) {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    chatAttachAlert2.updateSelectedPosition(chatAttachAlert2.nextAttachLayout == ChatAttachAlert.this.pollLayout ? 1 : 0);
                }
                ChatAttachAlert.this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f) * f);
                ChatAttachAlert.this.currentAttachLayout.onHideShowProgress(1.0f - Math.min(1.0f, f / 0.7f));
                ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            }
            if (ChatAttachAlert.this.viewChangeAnimator != null) {
                ChatAttachAlert.this.updateSelectedPosition(1);
            }
            ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
        }
    }

    public class AnonymousClass20 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass20(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (animator.equals(ChatAttachAlert.this.commentsAnimator)) {
                ChatAttachAlert.this.commentsAnimator = null;
            }
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (animator.equals(ChatAttachAlert.this.commentsAnimator)) {
                if (r2) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (chatAttachAlert.typeButtonsAvailable && (chatAttachAlert.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons())) {
                        ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                    }
                } else {
                    if (!ChatAttachAlert.this.isSoundPicker) {
                        ChatAttachAlert.this.frameLayout2.setVisibility(4);
                    }
                    ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    if (!chatAttachAlert2.typeButtonsAvailable && !chatAttachAlert2.isSoundPicker) {
                        ChatAttachAlert.this.shadow.setVisibility(4);
                    }
                }
                ChatAttachAlert.this.commentsAnimator = null;
            }
        }
    }

    public class AnonymousClass21 extends AnimationProperties.FloatProperty {
        private float openProgress;

        AnonymousClass21(String str) {
            super(str);
        }

        @Override
        public Float get(ChatAttachAlert chatAttachAlert) {
            return Float.valueOf(this.openProgress);
        }

        @Override
        public void setValue(ChatAttachAlert chatAttachAlert, float f) {
            float f2;
            View view;
            int childCount = ChatAttachAlert.this.buttonsRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                float f3 = (3 - i) * 32.0f;
                View childAt = ChatAttachAlert.this.buttonsRecyclerView.getChildAt(i);
                if (f > f3) {
                    float f4 = f - f3;
                    if (f4 <= 200.0f) {
                        float f5 = f4 / 200.0f;
                        f2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(f5) * 1.1f;
                        childAt.setAlpha(CubicBezierInterpolator.EASE_BOTH.getInterpolation(f5));
                    } else {
                        childAt.setAlpha(1.0f);
                        float f6 = f4 - 200.0f;
                        f2 = f6 <= 100.0f ? 1.1f - (CubicBezierInterpolator.EASE_IN.getInterpolation(f6 / 100.0f) * 0.1f) : 1.0f;
                    }
                } else {
                    f2 = 0.0f;
                }
                if (childAt instanceof AttachButton) {
                    AttachButton attachButton = (AttachButton) childAt;
                    attachButton.textView.setScaleX(f2);
                    attachButton.textView.setScaleY(f2);
                    attachButton.imageView.setScaleX(f2);
                    view = attachButton.imageView;
                } else if (childAt instanceof AttachBotButton) {
                    AttachBotButton attachBotButton = (AttachBotButton) childAt;
                    attachBotButton.nameTextView.setScaleX(f2);
                    attachBotButton.nameTextView.setScaleY(f2);
                    attachBotButton.imageView.setScaleX(f2);
                    view = attachBotButton.imageView;
                }
                view.setScaleY(f2);
            }
        }
    }

    class AnonymousClass22 extends AnimatorListenerAdapter {
        final Runnable val$onAnimationEnd;

        AnonymousClass22(Runnable runnable) {
            r2 = runnable;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator)) {
                return;
            }
            ((BottomSheet) ChatAttachAlert.this).currentSheetAnimation = null;
            ((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType = 0;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator) || ChatAttachAlert.this.appearSpringAnimation == null || ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                return;
            }
            r2.run();
        }
    }

    public class AnonymousClass23 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass23(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            ChatAttachAlert.this.actionBarAnimation = null;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ChatAttachAlert.this.actionBarAnimation != null) {
                if (r2) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (chatAttachAlert.typeButtonsAvailable) {
                        if (chatAttachAlert.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                            ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                            return;
                        }
                        return;
                    }
                    return;
                }
                ActionBarMenuItem actionBarMenuItem = ChatAttachAlert.this.searchItem;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setVisibility(4);
                }
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                if (chatAttachAlert2.avatarPicker == 0 && chatAttachAlert2.menuShowed) {
                    return;
                }
                ChatAttachAlert.this.selectedMenuItem.setVisibility(4);
            }
        }
    }

    public class AnonymousClass24 extends AnimatorListenerAdapter {
        AnonymousClass24() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            View view;
            ChatAttachAlert.this.menuAnimator = null;
            if (ChatAttachAlert.this.menuShowed) {
                view = ChatAttachAlert.this.searchItem;
                if (view == null) {
                    return;
                }
            } else {
                if (ChatAttachAlert.this.actionBar.getTag() == null) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (chatAttachAlert.avatarPicker == 0 && !chatAttachAlert.storyMediaPicker) {
                        chatAttachAlert.selectedMenuItem.setVisibility(4);
                    }
                }
                view = ChatAttachAlert.this.headerView;
            }
            view.setVisibility(4);
        }
    }

    public class AnonymousClass25 extends MentionsContainerView {
        AnonymousClass25(Context context, long j, long j2, BaseFragment baseFragment, SizeNotifierFrameLayout sizeNotifierFrameLayout, Theme.ResourcesProvider resourcesProvider) {
            super(context, j, j2, baseFragment, sizeNotifierFrameLayout, resourcesProvider);
        }

        @Override
        protected void onAnimationScroll() {
            if (ChatAttachAlert.this.photoLayout != null) {
                ChatAttachAlert.this.photoLayout.checkCameraViewPosition();
            }
        }

        @Override
        protected void onScrolled(boolean z, boolean z2) {
            if (ChatAttachAlert.this.photoLayout != null) {
                ChatAttachAlert.this.photoLayout.checkCameraViewPosition();
            }
        }
    }

    public class AnonymousClass26 implements MentionsContainerView.Delegate {
        AnonymousClass26() {
        }

        @Override
        public void addEmojiToRecent(String str) {
            MentionsContainerView.Delegate.CC.$default$addEmojiToRecent(this, str);
        }

        @Override
        public Paint.FontMetricsInt getFontMetrics() {
            return ChatAttachAlert.this.commentTextView.getEditText().getPaint().getFontMetricsInt();
        }

        @Override
        public void onStickerSelected(TLRPC.TL_document tL_document, String str, Object obj) {
            MentionsContainerView.Delegate.CC.$default$onStickerSelected(this, tL_document, str, obj);
        }

        @Override
        public void replaceText(int i, int i2, CharSequence charSequence, boolean z) {
            ChatAttachAlert.this.replaceWithText(i, i2, charSequence, z);
        }

        @Override
        public void sendBotInlineResult(TLRPC.BotInlineResult botInlineResult, boolean z, int i) {
            MentionsContainerView.Delegate.CC.$default$sendBotInlineResult(this, botInlineResult, z, i);
        }
    }

    public class AnonymousClass3 extends SizeNotifierFrameLayout {
        AdjustPanLayoutHelper adjustPanLayoutHelper;
        private Bulletin.Delegate bulletinDelegate;
        private boolean ignoreLayout;
        private float initialTranslationY;
        private int lastNotifyWidth;
        private RectF rect;
        final boolean val$forceDarkTheme;

        public class AnonymousClass1 implements Bulletin.Delegate {
            AnonymousClass1() {
            }

            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
            }

            @Override
            public boolean clipWithGradient(int i) {
                return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
            }

            @Override
            public int getBottomOffset(int i) {
                return (AnonymousClass3.this.getHeight() - ChatAttachAlert.this.frameLayout2.getTop()) + AndroidUtilities.dp(52.0f);
            }

            @Override
            public int getTopOffset(int i) {
                return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
            }

            @Override
            public void onBottomOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
            }

            @Override
            public void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override
            public void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }
        }

        public class AnonymousClass2 extends AdjustPanLayoutHelper {
            AnonymousClass2(View view) {
                super(view);
            }

            @Override
            protected boolean heightAnimationEnabled() {
                if (ChatAttachAlert.this.isDismissed() || !ChatAttachAlert.this.openTransitionFinished) {
                    return false;
                }
                return !(ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.commentTextView.isPopupVisible()) || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout && !ChatAttachAlert.this.pollLayout.isPopupVisible());
            }

            @Override
            public void onPanTranslationUpdate(float f, float f2, boolean z) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.currentPanTranslationY = f;
                if (chatAttachAlert.fromScrollY > 0.0f) {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    chatAttachAlert2.currentPanTranslationY += (chatAttachAlert2.fromScrollY - ChatAttachAlert.this.toScrollY) * (1.0f - f2);
                }
                ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
                chatAttachAlert3.actionBar.setTranslationY(chatAttachAlert3.currentPanTranslationY);
                ChatAttachAlert chatAttachAlert4 = ChatAttachAlert.this;
                chatAttachAlert4.selectedMenuItem.setTranslationY(chatAttachAlert4.currentPanTranslationY);
                ChatAttachAlert chatAttachAlert5 = ChatAttachAlert.this;
                ActionBarMenuItem actionBarMenuItem = chatAttachAlert5.searchItem;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setTranslationY(chatAttachAlert5.currentPanTranslationY);
                }
                ChatAttachAlert chatAttachAlert6 = ChatAttachAlert.this;
                chatAttachAlert6.doneItem.setTranslationY(chatAttachAlert6.currentPanTranslationY);
                ChatAttachAlert.this.actionBarShadow.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                ChatAttachAlert.this.updateSelectedPosition(0);
                ChatAttachAlert chatAttachAlert7 = ChatAttachAlert.this;
                chatAttachAlert7.setCurrentPanTranslationY(chatAttachAlert7.currentPanTranslationY);
                AnonymousClass3.this.invalidate();
                ChatAttachAlert.this.frameLayout2.invalidate();
                ChatAttachAlert.this.updateCommentTextViewPosition();
                if (ChatAttachAlert.this.currentAttachLayout != null) {
                    ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                }
            }

            @Override
            public void onTransitionEnd() {
                super.onTransitionEnd();
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, false, 0);
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                chatAttachAlert2.previousScrollOffsetY = chatAttachAlert2.scrollOffsetY[0];
                ChatAttachAlert.this.currentAttachLayout.onPanTransitionEnd();
                if (!(ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertBotWebViewLayout) || ChatAttachAlert.this.botButtonWasVisible) {
                    return;
                }
                int dp = ((BottomSheet) ChatAttachAlert.this).keyboardVisible ? AndroidUtilities.dp(84.0f) : 0;
                for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                    ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                }
            }

            @Override
            public void onTransitionStart(boolean r5, int r6) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.AnonymousClass2.onTransitionStart(boolean, int):void");
            }
        }

        AnonymousClass3(Context context, boolean z) {
            super(context);
            this.val$forceDarkTheme = z;
            this.bulletinDelegate = new Bulletin.Delegate() {
                AnonymousClass1() {
                }

                @Override
                public boolean allowLayoutChanges() {
                    return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
                }

                @Override
                public boolean bottomOffsetAnimated() {
                    return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
                }

                @Override
                public boolean clipWithGradient(int i) {
                    return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
                }

                @Override
                public int getBottomOffset(int i) {
                    return (AnonymousClass3.this.getHeight() - ChatAttachAlert.this.frameLayout2.getTop()) + AndroidUtilities.dp(52.0f);
                }

                @Override
                public int getTopOffset(int i) {
                    return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
                }

                @Override
                public void onBottomOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                }

                @Override
                public void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override
                public void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }
            };
            this.rect = new RectF();
            this.adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) {
                AnonymousClass2(View this) {
                    super(this);
                }

                @Override
                protected boolean heightAnimationEnabled() {
                    if (ChatAttachAlert.this.isDismissed() || !ChatAttachAlert.this.openTransitionFinished) {
                        return false;
                    }
                    return !(ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.commentTextView.isPopupVisible()) || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout && !ChatAttachAlert.this.pollLayout.isPopupVisible());
                }

                @Override
                public void onPanTranslationUpdate(float f, float f2, boolean z2) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.currentPanTranslationY = f;
                    if (chatAttachAlert.fromScrollY > 0.0f) {
                        ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                        chatAttachAlert2.currentPanTranslationY += (chatAttachAlert2.fromScrollY - ChatAttachAlert.this.toScrollY) * (1.0f - f2);
                    }
                    ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
                    chatAttachAlert3.actionBar.setTranslationY(chatAttachAlert3.currentPanTranslationY);
                    ChatAttachAlert chatAttachAlert4 = ChatAttachAlert.this;
                    chatAttachAlert4.selectedMenuItem.setTranslationY(chatAttachAlert4.currentPanTranslationY);
                    ChatAttachAlert chatAttachAlert5 = ChatAttachAlert.this;
                    ActionBarMenuItem actionBarMenuItem = chatAttachAlert5.searchItem;
                    if (actionBarMenuItem != null) {
                        actionBarMenuItem.setTranslationY(chatAttachAlert5.currentPanTranslationY);
                    }
                    ChatAttachAlert chatAttachAlert6 = ChatAttachAlert.this;
                    chatAttachAlert6.doneItem.setTranslationY(chatAttachAlert6.currentPanTranslationY);
                    ChatAttachAlert.this.actionBarShadow.setTranslationY(ChatAttachAlert.this.currentPanTranslationY);
                    ChatAttachAlert.this.updateSelectedPosition(0);
                    ChatAttachAlert chatAttachAlert7 = ChatAttachAlert.this;
                    chatAttachAlert7.setCurrentPanTranslationY(chatAttachAlert7.currentPanTranslationY);
                    AnonymousClass3.this.invalidate();
                    ChatAttachAlert.this.frameLayout2.invalidate();
                    ChatAttachAlert.this.updateCommentTextViewPosition();
                    if (ChatAttachAlert.this.currentAttachLayout != null) {
                        ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                    }
                }

                @Override
                public void onTransitionEnd() {
                    super.onTransitionEnd();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, false, 0);
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    chatAttachAlert2.previousScrollOffsetY = chatAttachAlert2.scrollOffsetY[0];
                    ChatAttachAlert.this.currentAttachLayout.onPanTransitionEnd();
                    if (!(ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertBotWebViewLayout) || ChatAttachAlert.this.botButtonWasVisible) {
                        return;
                    }
                    int dp = ((BottomSheet) ChatAttachAlert.this).keyboardVisible ? AndroidUtilities.dp(84.0f) : 0;
                    for (int i = 0; i < ChatAttachAlert.this.botAttachLayouts.size(); i++) {
                        ((ChatAttachAlertBotWebViewLayout) ChatAttachAlert.this.botAttachLayouts.valueAt(i)).setMeasureOffsetY(dp);
                    }
                }

                @Override
                public void onTransitionStart(boolean r5, int r6) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.AnonymousClass2.onTransitionStart(boolean, int):void");
                }
            };
        }

        private void drawChildBackground(android.graphics.Canvas r19, android.view.View r20) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.drawChildBackground(android.graphics.Canvas, android.view.View):void");
        }

        private int getCurrentTop() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            int i = chatAttachAlert.scrollOffsetY[0] - (((BottomSheet) chatAttachAlert).backgroundPaddingTop * 2);
            int dp = AndroidUtilities.dp(13.0f);
            FrameLayout frameLayout = ChatAttachAlert.this.headerView;
            int dp2 = (i - (dp + (frameLayout != null ? AndroidUtilities.dp(frameLayout.getAlpha() * 26.0f) : 0))) + AndroidUtilities.dp(20.0f);
            return (Build.VERSION.SDK_INT < 21 || ChatAttachAlert.this.inBubbleMode) ? dp2 : dp2 + AndroidUtilities.statusBarHeight;
        }

        private float getY(View view) {
            float f;
            float dp;
            if (!(view instanceof AttachAlertLayout)) {
                return 0.0f;
            }
            AttachAlertLayout attachAlertLayout = (AttachAlertLayout) view;
            int needsActionBar = attachAlertLayout.needsActionBar();
            int dp2 = AndroidUtilities.dp(13.0f);
            FrameLayout frameLayout = ChatAttachAlert.this.headerView;
            int alpha = dp2 + ((int) ((frameLayout != null ? frameLayout.getAlpha() : 0.0f) * AndroidUtilities.dp(26.0f)));
            int scrollOffsetY = (ChatAttachAlert.this.getScrollOffsetY(0) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) - alpha;
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType == 1 || ChatAttachAlert.this.viewChangeAnimator != null) {
                scrollOffsetY = (int) (scrollOffsetY + view.getTranslationY());
            }
            int dp3 = AndroidUtilities.dp(20.0f) + scrollOffsetY;
            int currentActionBarHeight = needsActionBar != 0 ? ActionBar.getCurrentActionBarHeight() : ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
            if (needsActionBar != 2 && scrollOffsetY + ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop < currentActionBarHeight) {
                float f2 = alpha;
                if (attachAlertLayout == ChatAttachAlert.this.locationLayout) {
                    f = 11.0f;
                } else if (attachAlertLayout == ChatAttachAlert.this.pollLayout) {
                    dp = f2 - AndroidUtilities.dp(3.0f);
                    dp3 -= (int) (((currentActionBarHeight - dp) + AndroidUtilities.statusBarHeight) * ChatAttachAlert.this.actionBar.getAlpha());
                } else {
                    f = 4.0f;
                }
                dp = f2 + AndroidUtilities.dp(f);
                dp3 -= (int) (((currentActionBarHeight - dp) + AndroidUtilities.statusBarHeight) * ChatAttachAlert.this.actionBar.getAlpha());
            }
            if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                dp3 += AndroidUtilities.statusBarHeight;
            }
            return dp3;
        }

        public void lambda$onMeasure$0() {
            ChatAttachAlert.this.buttonsAdapter.notifyDataSetChanged();
        }

        private void onMeasureInternal(int i, int i2) {
            int makeMeasureSpec;
            int paddingTop;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int i3 = size - (((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft * 2);
            if (!ChatAttachAlert.this.commentTextView.isWaitingForKeyboardOpen() && AndroidUtilities.dp(20.0f) >= 0 && !ChatAttachAlert.this.commentTextView.isPopupShowing() && !ChatAttachAlert.this.commentTextView.isAnimatePopupClosing()) {
                this.ignoreLayout = true;
                ChatAttachAlert.this.commentTextView.hideEmojiView();
                this.ignoreLayout = false;
            }
            if (ChatAttachAlert.this.pollLayout != null && AndroidUtilities.dp(20.0f) >= 0 && !ChatAttachAlert.this.pollLayout.isWaitingForKeyboardOpen() && !ChatAttachAlert.this.pollLayout.isPopupShowing() && !ChatAttachAlert.this.pollLayout.isAnimatePopupClosing() && !ChatAttachAlert.this.pollLayout.isEmojiSearchOpened) {
                this.ignoreLayout = true;
                ChatAttachAlert.this.pollLayout.hideEmojiView();
                this.ignoreLayout = false;
            }
            if (AndroidUtilities.dp(20.0f) >= 0) {
                int dp = ((BottomSheet) ChatAttachAlert.this).keyboardVisible ? (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout && ChatAttachAlert.this.pollLayout.emojiView != null && ChatAttachAlert.this.pollLayout.isEmojiSearchOpened) ? AndroidUtilities.dp(120.0f) : 0 : (ChatAttachAlert.this.currentAttachLayout != ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.pollLayout.emojiView == null) ? ChatAttachAlert.this.commentTextView.getEmojiPadding() : ChatAttachAlert.this.pollLayout.getEmojiPadding();
                if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= dp;
                    i2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
                }
                this.ignoreLayout = true;
                ChatAttachAlert.this.currentAttachLayout.onPreMeasure(i3, size2);
                if (ChatAttachAlert.this.nextAttachLayout != null) {
                    ChatAttachAlert.this.nextAttachLayout.onPreMeasure(i3, size2);
                }
                this.ignoreLayout = false;
            }
            int childCount = getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                View childAt = getChildAt(i4);
                if (childAt != null && childAt.getVisibility() != 8) {
                    EditTextEmoji editTextEmoji = ChatAttachAlert.this.commentTextView;
                    if ((editTextEmoji == null || !editTextEmoji.isPopupView(childAt)) && (ChatAttachAlert.this.pollLayout == null || childAt != ChatAttachAlert.this.pollLayout.emojiView)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else {
                        if (ChatAttachAlert.this.inBubbleMode) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                            paddingTop = getPaddingTop() + size2;
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                            paddingTop = childAt.getLayoutParams().height;
                        } else if (AndroidUtilities.isTablet()) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                            paddingTop = Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop());
                        } else {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                            paddingTop = (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop();
                        }
                        childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
                    }
                }
            }
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout || ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.photoPreviewLayout || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout && ChatAttachAlert.this.nextAttachLayout == null)) {
                drawChildBackground(canvas, ChatAttachAlert.this.currentAttachLayout);
            }
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            int dp;
            float dp2;
            float f;
            int themedColor;
            boolean drawChild;
            int themedColor2;
            float alpha;
            if (!(view instanceof AttachAlertLayout) || view.getAlpha() <= 0.0f) {
                ActionBar actionBar = ChatAttachAlert.this.actionBar;
                if (view != actionBar) {
                    return super.drawChild(canvas, view, j);
                }
                float alpha2 = actionBar.getAlpha();
                if (alpha2 <= 0.0f) {
                    return false;
                }
                if (alpha2 >= 1.0f) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipRect(ChatAttachAlert.this.actionBar.getX(), getY(ChatAttachAlert.this.currentAttachLayout), ChatAttachAlert.this.actionBar.getX() + ChatAttachAlert.this.actionBar.getWidth(), ChatAttachAlert.this.actionBar.getY() + ChatAttachAlert.this.actionBar.getHeight());
                boolean drawChild2 = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild2;
            }
            canvas.save();
            canvas.translate(0.0f, ChatAttachAlert.this.currentPanTranslationY);
            int alpha3 = (int) (view.getAlpha() * 255.0f);
            AttachAlertLayout attachAlertLayout = (AttachAlertLayout) view;
            int needsActionBar = attachAlertLayout.needsActionBar();
            int dp3 = AndroidUtilities.dp(13.0f);
            FrameLayout frameLayout = ChatAttachAlert.this.headerView;
            int dp4 = dp3 + (frameLayout != null ? AndroidUtilities.dp(frameLayout.getAlpha() * 26.0f) : 0);
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            int scrollOffsetY = (chatAttachAlert.getScrollOffsetY(attachAlertLayout == chatAttachAlert.currentAttachLayout ? 0 : 1) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) - dp4;
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType == 1 || ChatAttachAlert.this.viewChangeAnimator != null) {
                scrollOffsetY = (int) (scrollOffsetY + view.getTranslationY());
            }
            int dp5 = AndroidUtilities.dp(20.0f) + scrollOffsetY;
            int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(45.0f) + ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
            int currentActionBarHeight = needsActionBar != 0 ? ActionBar.getCurrentActionBarHeight() : ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop;
            if (needsActionBar == 2) {
                if (scrollOffsetY < currentActionBarHeight) {
                    f = Math.max(0.0f, 1.0f - ((currentActionBarHeight - scrollOffsetY) / ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop));
                }
                f = 1.0f;
            } else {
                if (((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY < currentActionBarHeight) {
                    float f2 = dp4;
                    if (attachAlertLayout == ChatAttachAlert.this.locationLayout) {
                        dp = AndroidUtilities.dp(11.0f);
                    } else if (attachAlertLayout == ChatAttachAlert.this.pollLayout) {
                        dp2 = f2 - AndroidUtilities.dp(3.0f);
                        float min = Math.min(1.0f, ((currentActionBarHeight - scrollOffsetY) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) / dp2);
                        int i = (int) ((currentActionBarHeight - dp2) * min);
                        scrollOffsetY -= i;
                        dp5 -= i;
                        measuredHeight += i;
                        f = 1.0f - min;
                    } else {
                        dp = AndroidUtilities.dp(4.0f);
                    }
                    dp2 = f2 + dp;
                    float min2 = Math.min(1.0f, ((currentActionBarHeight - scrollOffsetY) - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop) / dp2);
                    int i2 = (int) ((currentActionBarHeight - dp2) * min2);
                    scrollOffsetY -= i2;
                    dp5 -= i2;
                    measuredHeight += i2;
                    f = 1.0f - min2;
                }
                f = 1.0f;
            }
            if (Build.VERSION.SDK_INT >= 21 && !ChatAttachAlert.this.inBubbleMode) {
                int i3 = AndroidUtilities.statusBarHeight;
                scrollOffsetY += i3;
                dp5 += i3;
                measuredHeight -= i3;
            }
            if (ChatAttachAlert.this.currentAttachLayout.hasCustomBackground()) {
                themedColor = ChatAttachAlert.this.currentAttachLayout.getCustomBackground();
            } else {
                themedColor = ChatAttachAlert.this.getThemedColor(this.val$forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground);
            }
            boolean z = (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout || ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.photoPreviewLayout || (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout && ChatAttachAlert.this.nextAttachLayout == null)) ? false : true;
            if (z) {
                ((BottomSheet) ChatAttachAlert.this).shadowDrawable.setAlpha(alpha3);
                ((BottomSheet) ChatAttachAlert.this).shadowDrawable.setBounds(0, scrollOffsetY, getMeasuredWidth(), measuredHeight);
                ((BottomSheet) ChatAttachAlert.this).shadowDrawable.draw(canvas);
                if (needsActionBar == 2) {
                    Theme.dialogs_onlineCirclePaint.setColor(themedColor);
                    Theme.dialogs_onlineCirclePaint.setAlpha(alpha3);
                    this.rect.set(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY + AndroidUtilities.dp(24.0f));
                    canvas.save();
                    RectF rectF = this.rect;
                    float f3 = rectF.left;
                    float f4 = rectF.top;
                    canvas.clipRect(f3, f4, rectF.right, (rectF.height() / 2.0f) + f4);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                    canvas.restore();
                }
            }
            if (view == ChatAttachAlert.this.contactsLayout || view == ChatAttachAlert.this.quickRepliesLayout || view == ChatAttachAlert.this.audioLayout) {
                drawChild = super.drawChild(canvas, view, j);
            } else {
                canvas.save();
                canvas.clipRect(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, (ChatAttachAlert.this.actionBar.getY() + ChatAttachAlert.this.actionBar.getMeasuredHeight()) - ChatAttachAlert.this.currentPanTranslationY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, getMeasuredHeight());
                drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
            }
            if (z) {
                if (f != 1.0f && needsActionBar != 2) {
                    Theme.dialogs_onlineCirclePaint.setColor(themedColor);
                    Theme.dialogs_onlineCirclePaint.setAlpha(alpha3);
                    this.rect.set(((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY, getMeasuredWidth() - ((BottomSheet) ChatAttachAlert.this).backgroundPaddingLeft, ((BottomSheet) ChatAttachAlert.this).backgroundPaddingTop + scrollOffsetY + AndroidUtilities.dp(24.0f));
                    canvas.save();
                    RectF rectF2 = this.rect;
                    float f5 = rectF2.left;
                    float f6 = rectF2.top;
                    canvas.clipRect(f5, f6, rectF2.right, (rectF2.height() / 2.0f) + f6);
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                    canvas.restore();
                }
                FrameLayout frameLayout2 = ChatAttachAlert.this.headerView;
                if ((frameLayout2 == null || frameLayout2.getAlpha() != 1.0f) && f != 0.0f) {
                    int dp6 = AndroidUtilities.dp(36.0f);
                    this.rect.set((getMeasuredWidth() - dp6) / 2, dp5, (getMeasuredWidth() + dp6) / 2, dp5 + AndroidUtilities.dp(4.0f));
                    if (needsActionBar == 2) {
                        themedColor2 = 536870912;
                        alpha = f;
                    } else {
                        themedColor2 = ChatAttachAlert.this.getThemedColor(Theme.key_sheet_scrollUp);
                        FrameLayout frameLayout3 = ChatAttachAlert.this.headerView;
                        alpha = frameLayout3 == null ? 1.0f : 1.0f - frameLayout3.getAlpha();
                    }
                    int alpha4 = Color.alpha(themedColor2);
                    Theme.dialogs_onlineCirclePaint.setColor(themedColor2);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha4 * alpha * f * view.getAlpha()));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
            }
            canvas.restore();
            return drawChild;
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.adjustPanLayoutHelper.setResizableView(this);
            this.adjustPanLayoutHelper.onAttach();
            ChatAttachAlert.this.commentTextView.setAdjustPanLayoutHelper(this.adjustPanLayoutHelper);
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.adjustPanLayoutHelper.onDetach();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            boolean z = ChatAttachAlert.this.inBubbleMode;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.currentAttachLayout.onContainerViewTouchEvent(motionEvent)) {
                return true;
            }
            if (motionEvent.getAction() != 0 || ChatAttachAlert.this.scrollOffsetY[0] == 0 || motionEvent.getY() >= getCurrentTop() || ChatAttachAlert.this.actionBar.getAlpha() != 0.0f) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            ChatAttachAlert.this.onDismissWithTouchOutside();
            return true;
        }

        @Override
        public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.onLayout(boolean, int, int, int, int):void");
        }

        @Override
        protected void onMeasure(int r6, int r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.AnonymousClass3.onMeasure(int, int):void");
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.currentAttachLayout.onContainerViewTouchEvent(motionEvent)) {
                return true;
            }
            return !ChatAttachAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        public void setTranslationY(float f) {
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            float f2 = f + chatAttachAlert.currentPanTranslationY;
            if (((BottomSheet) chatAttachAlert).currentSheetAnimationType == 0) {
                this.initialTranslationY = f2;
            }
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType == 1) {
                if (f2 < 0.0f) {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationY(f2);
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    if (chatAttachAlert2.avatarPicker != 0 || chatAttachAlert2.storyMediaPicker) {
                        chatAttachAlert2.headerView.setTranslationY((chatAttachAlert2.baseSelectedTextViewTranslationY + f2) - ChatAttachAlert.this.currentPanTranslationY);
                    }
                    ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(0.0f);
                    f2 = 0.0f;
                } else {
                    ChatAttachAlert.this.currentAttachLayout.setTranslationY(0.0f);
                    ChatAttachAlert.this.buttonsRecyclerView.setTranslationY((-f2) + (r0.getMeasuredHeight() * (f2 / this.initialTranslationY)));
                }
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }
            super.setTranslationY(f2 - ChatAttachAlert.this.currentPanTranslationY);
            if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType != 1) {
                ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            }
        }
    }

    public class AnonymousClass4 implements SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate {
        AnonymousClass4() {
        }

        @Override
        public void onSizeChanged(int i, boolean z) {
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout) {
                ChatAttachAlert.this.currentAttachLayout.invalidate();
            }
        }
    }

    public class AnonymousClass5 extends ActionBar {
        AnonymousClass5(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        public void setAlpha(float f) {
            float alpha = getAlpha();
            super.setAlpha(f);
            if (alpha != f) {
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
                if (ChatAttachAlert.this.frameLayout2 != null) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (chatAttachAlert.buttonsRecyclerView != null) {
                        if (chatAttachAlert.frameLayout2.getTag() != null) {
                            if (ChatAttachAlert.this.currentAttachLayout == null) {
                                float f2 = f != 0.0f ? 0.0f : 1.0f;
                                if (ChatAttachAlert.this.buttonsRecyclerView.getAlpha() != f2) {
                                    ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f2);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        if (ChatAttachAlert.this.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                            float f3 = 1.0f - f;
                            ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f3);
                            ChatAttachAlert.this.shadow.setAlpha(f3);
                            ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(AndroidUtilities.dp(44.0f) * f);
                        }
                        ChatAttachAlert.this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f) * f);
                        ChatAttachAlert.this.shadow.setTranslationY((AndroidUtilities.dp(84.0f) * f) + ChatAttachAlert.this.botMainButtonOffsetY);
                    }
                }
            }
        }
    }

    public class AnonymousClass6 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass6() {
        }

        @Override
        public void onItemClick(int i) {
            if (i != -1) {
                ChatAttachAlert.this.currentAttachLayout.onMenuItemClick(i);
            } else {
                if (ChatAttachAlert.this.currentAttachLayout.onBackPressed()) {
                    return;
                }
                ChatAttachAlert.this.dismiss();
            }
        }
    }

    public class AnonymousClass7 implements PhotoPickerActivity.PhotoPickerActivityDelegate {
        private boolean sendPressed;
        final ArrayList val$order;
        final HashMap val$photos;

        AnonymousClass7(HashMap hashMap, ArrayList arrayList) {
            r2 = hashMap;
            r3 = arrayList;
        }

        @Override
        public void actionButtonPressed(boolean z, boolean z2, int i) {
            if (z || r2.isEmpty() || this.sendPressed) {
                return;
            }
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < r3.size(); i2++) {
                Object obj = r2.get(r3.get(i2));
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                arrayList.add(sendingMediaInfo);
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                String str = searchImage.imagePath;
                if (str != null) {
                    sendingMediaInfo.path = str;
                } else {
                    sendingMediaInfo.searchImage = searchImage;
                }
                sendingMediaInfo.thumbPath = searchImage.thumbPath;
                sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                CharSequence charSequence = searchImage.caption;
                sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                sendingMediaInfo.entities = searchImage.entities;
                sendingMediaInfo.masks = searchImage.stickers;
                sendingMediaInfo.ttl = searchImage.ttl;
                TLRPC.BotInlineResult botInlineResult = searchImage.inlineResult;
                if (botInlineResult != null && searchImage.type == 1) {
                    sendingMediaInfo.inlineResult = botInlineResult;
                    sendingMediaInfo.params = searchImage.params;
                }
                searchImage.date = (int) (System.currentTimeMillis() / 1000);
            }
            ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectSearchPhotos(arrayList, z2, i);
        }

        @Override
        public boolean canFinishFragment() {
            return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
        }

        @Override
        public void onCaptionChanged(CharSequence charSequence) {
        }

        @Override
        public void onOpenInPressed() {
            PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
        }

        @Override
        public void selectedPhotosChanged() {
        }
    }

    public class AnonymousClass8 extends PhotoViewer.EmptyPhotoViewerProvider {
        final MediaController.PhotoEntry val$entry;

        AnonymousClass8(MediaController.PhotoEntry photoEntry) {
            r2 = photoEntry;
        }

        @Override
        public boolean allowCaption() {
            return false;
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.sent = true;
            if (chatAttachAlert.delegate == null) {
                return;
            }
            r2.editedInfo = videoEditedInfo;
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.add(0);
            ChatAttachAlertPhotoLayout.selectedPhotos.put(0, r2);
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
            chatAttachAlert2.delegate.didPressedButton(7, true, z, i2, 0L, chatAttachAlert2.isCaptionAbove(), z2);
        }
    }

    public class AnonymousClass9 extends FrameLayout {
        AnonymousClass9(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                return false;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
            ChatAttachAlert.this.updateSelectedPosition(0);
            ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
        }
    }

    public static class AttachAlertLayout extends FrameLayout {
        protected ChatAttachAlert parentAlert;
        protected final Theme.ResourcesProvider resourcesProvider;

        public AttachAlertLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            this.parentAlert = chatAttachAlert;
        }

        public void applyCaption(CharSequence charSequence) {
        }

        public boolean canDismissWithTouchOutside() {
            return true;
        }

        public boolean canScheduleMessages() {
            return true;
        }

        public void checkColors() {
        }

        public int getButtonsHideOffset() {
            return AndroidUtilities.dp(needsActionBar() != 0 ? 12.0f : 17.0f);
        }

        public int getCurrentItemTop() {
            return 0;
        }

        public int getCustomActionBarBackground() {
            return 0;
        }

        public int getCustomBackground() {
            return 0;
        }

        public int getFirstOffset() {
            return 0;
        }

        public int getListTopPadding() {
            return 0;
        }

        public int getSelectedItemsCount() {
            return 0;
        }

        public ArrayList<ThemeDescription> getThemeDescriptions() {
            return null;
        }

        public int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        public boolean hasCustomActionBarBackground() {
            return false;
        }

        public boolean hasCustomBackground() {
            return false;
        }

        public int needsActionBar() {
            return 0;
        }

        public boolean onBackPressed() {
            return false;
        }

        public void onButtonsTranslationYUpdated() {
        }

        public void onContainerTranslationUpdated(float f) {
        }

        public boolean onContainerViewTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public void onDestroy() {
        }

        public boolean onDismiss() {
            return false;
        }

        public void onDismissWithButtonClick(int i) {
        }

        public boolean onDismissWithTouchOutside() {
            return true;
        }

        public void onHidden() {
        }

        public void onHide() {
        }

        public void onHideShowProgress(float f) {
        }

        public void onMenuItemClick(int i) {
        }

        public void onOpenAnimationEnd() {
        }

        public void onPanTransitionEnd() {
        }

        public void onPanTransitionStart(boolean z, int i) {
        }

        public void onPause() {
        }

        public void onPreMeasure(int i, int i2) {
        }

        public void onResume() {
        }

        public void onSelectedItemsCountChanged(int i) {
        }

        public boolean onSheetKeyDown(int i, KeyEvent keyEvent) {
            return false;
        }

        public void onShow(AttachAlertLayout attachAlertLayout) {
        }

        public void onShown() {
        }

        public void scrollToTop() {
        }

        public void sendSelectedItems(boolean z, int i, long j, boolean z2) {
        }

        public boolean shouldHideBottomButtons() {
            return true;
        }
    }

    public class AttachBotButton extends FrameLayout {
        private TLRPC.TL_attachMenuBot attachMenuBot;
        private AvatarDrawable avatarDrawable;
        private ValueAnimator checkAnimator;
        private Boolean checked;
        private float checkedState;
        private TLRPC.User currentUser;
        private int iconBackgroundColor;
        private BackupImageView imageView;
        private TextView nameTextView;
        private View selector;
        private int textColor;

        public class AnonymousClass1 extends BackupImageView {
            final ChatAttachAlert val$this$0;

            AnonymousClass1(Context context, ChatAttachAlert chatAttachAlert) {
                super(context);
                this.val$this$0 = chatAttachAlert;
                this.imageReceiver.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                    @Override
                    public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                        ChatAttachAlert.AttachBotButton.AnonymousClass1.lambda$new$0(imageReceiver, z, z2, z3);
                    }

                    @Override
                    public void didSetImageBitmap(int i, String str, Drawable drawable) {
                        ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i, str, drawable);
                    }

                    @Override
                    public void onAnimationReady(ImageReceiver imageReceiver) {
                        ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                    }
                });
            }

            public static void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                Drawable drawable = imageReceiver.getDrawable();
                if (drawable instanceof RLottieDrawable) {
                    RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                    rLottieDrawable.setCustomEndFrame(0);
                    rLottieDrawable.stop();
                    rLottieDrawable.setProgress(0.0f, false);
                }
            }

            @Override
            public void setScaleX(float f) {
                super.setScaleX(f);
                AttachBotButton.this.invalidate();
            }
        }

        public AttachBotButton(Context context) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            setWillNotDraw(false);
            setFocusable(true);
            setFocusableInTouchMode(true);
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(context, ChatAttachAlert.this);
            this.imageView = anonymousClass1;
            anonymousClass1.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View view = new View(context);
                this.selector = view;
                view.setBackground(Theme.createSelectorDrawable(ChatAttachAlert.this.getThemedColor(Theme.key_dialogButtonSelector), 1, AndroidUtilities.dp(23.0f)));
                addView(this.selector, LayoutHelper.createFrame(46, 46.0f, 49, 0.0f, 9.0f, 0.0f, 0.0f));
            }
            TextView textView = new TextView(context);
            this.nameTextView = textView;
            textView.setTextSize(1, 12.0f);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 60.0f, 6.0f, 0.0f));
        }

        public void lambda$updateCheckedState$0(ValueAnimator valueAnimator) {
            setCheckedState(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        private void updateMargins() {
            ((ViewGroup.MarginLayoutParams) this.nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 62.0f : 60.0f);
            ((ViewGroup.MarginLayoutParams) this.imageView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.attachMenuBot != null ? 11.0f : 9.0f);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.attachMenuBot != null) {
                float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
                float dp = AndroidUtilities.dp(23.0f) * scaleX;
                float left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f);
                float top = this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2.0f);
                ChatAttachAlert.this.attachButtonPaint.setColor(this.iconBackgroundColor);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
                ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3.0f) * scaleX);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(left, top, dp - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
                ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
                ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(left, top, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.selector == null || !this.checked.booleanValue()) {
                return;
            }
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824));
        }

        public void setAttachBot(TLRPC.User user, TLRPC.TL_attachMenuBot tL_attachMenuBot) {
            boolean z;
            if (user == null || tL_attachMenuBot == null) {
                return;
            }
            this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2));
            this.currentUser = user;
            this.nameTextView.setText(tL_attachMenuBot.short_name);
            this.avatarDrawable.setInfo(ChatAttachAlert.this.currentAccount, user);
            TLRPC.TL_attachMenuBotIcon animatedAttachMenuBotIcon = MediaDataController.getAnimatedAttachMenuBotIcon(tL_attachMenuBot);
            if (animatedAttachMenuBotIcon == null) {
                animatedAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(tL_attachMenuBot);
                z = false;
            } else {
                z = true;
            }
            if (animatedAttachMenuBotIcon != null) {
                this.textColor = ChatAttachAlert.this.getThemedColor(Theme.key_chat_attachContactText);
                this.iconBackgroundColor = ChatAttachAlert.this.getThemedColor(Theme.key_chat_attachContactBackground);
                Iterator<TLRPC.TL_attachMenuBotIconColor> it = animatedAttachMenuBotIcon.colors.iterator();
                while (it.hasNext()) {
                    TLRPC.TL_attachMenuBotIconColor next = it.next();
                    String str = next.name;
                    str.hashCode();
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -1852424286:
                            if (str.equals("dark_icon")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -1852094378:
                            if (str.equals("dark_text")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -208896510:
                            if (str.equals("light_icon")) {
                                c = 2;
                                break;
                            }
                            break;
                        case -208566602:
                            if (str.equals("light_text")) {
                                c = 3;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            if (!Theme.getCurrentTheme().isDark()) {
                                break;
                            } else {
                                this.iconBackgroundColor = next.color;
                                break;
                            }
                        case 1:
                            if (!Theme.getCurrentTheme().isDark()) {
                                break;
                            } else {
                                this.textColor = next.color;
                                break;
                            }
                        case 2:
                            if (!Theme.getCurrentTheme().isDark()) {
                                this.iconBackgroundColor = next.color;
                                break;
                            } else {
                                break;
                            }
                        case 3:
                            if (!Theme.getCurrentTheme().isDark()) {
                                this.textColor = next.color;
                                break;
                            } else {
                                break;
                            }
                    }
                }
                this.textColor = ColorUtils.setAlphaComponent(this.textColor, 255);
                this.iconBackgroundColor = ColorUtils.setAlphaComponent(this.iconBackgroundColor, 255);
                TLRPC.Document document = animatedAttachMenuBotIcon.icon;
                this.imageView.getImageReceiver().setAllowStartLottieAnimation(false);
                this.imageView.setImage(ImageLocation.getForDocument(document), String.valueOf(tL_attachMenuBot.bot_id), z ? "tgs" : "svg", DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 1.0f), tL_attachMenuBot);
            }
            this.imageView.setSize(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f));
            this.imageView.setColorFilter(new PorterDuffColorFilter(ChatAttachAlert.this.getThemedColor(Theme.key_chat_attachIcon), PorterDuff.Mode.SRC_IN));
            this.attachMenuBot = tL_attachMenuBot;
            this.selector.setVisibility(8);
            updateMargins();
            setCheckedState(0.0f);
            invalidate();
        }

        public void setCheckedState(float f) {
            this.checkedState = f;
            float f2 = 1.0f - (f * 0.06f);
            this.imageView.setScaleX(f2);
            this.imageView.setScaleY(f2);
            this.nameTextView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), this.textColor, this.checkedState));
            invalidate();
        }

        public void setUser(TLRPC.User user) {
            if (user == null) {
                return;
            }
            this.nameTextView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2));
            this.currentUser = user;
            this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            this.avatarDrawable.setInfo(ChatAttachAlert.this.currentAccount, user);
            this.imageView.setForUserOrChat(user, this.avatarDrawable);
            this.imageView.setSize(-1, -1);
            this.imageView.setColorFilter(null);
            this.attachMenuBot = null;
            this.selector.setVisibility(0);
            updateMargins();
            setCheckedState(0.0f);
            invalidate();
        }

        void updateCheckedState(boolean z) {
            boolean z2 = this.attachMenuBot != null && (-this.currentUser.id) == ChatAttachAlert.this.selectedId;
            Boolean bool = this.checked;
            if (bool != null && bool.booleanValue() == z2 && z) {
                return;
            }
            this.checked = Boolean.valueOf(z2);
            ValueAnimator valueAnimator = this.checkAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            RLottieDrawable lottieAnimation = this.imageView.getImageReceiver().getLottieAnimation();
            if (!z) {
                if (lottieAnimation != null) {
                    lottieAnimation.stop();
                    lottieAnimation.setProgress(0.0f, false);
                }
                setCheckedState(this.checked.booleanValue() ? 1.0f : 0.0f);
                return;
            }
            if (this.checked.booleanValue() && lottieAnimation != null) {
                lottieAnimation.setAutoRepeat(0);
                lottieAnimation.setCustomEndFrame(-1);
                lottieAnimation.setProgress(0.0f, false);
                lottieAnimation.start();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.checked.booleanValue() ? 0.0f : 1.0f, this.checked.booleanValue() ? 1.0f : 0.0f);
            this.checkAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatAttachAlert.AttachBotButton.this.lambda$updateCheckedState$0(valueAnimator2);
                }
            });
            this.checkAnimator.setDuration(200L);
            this.checkAnimator.start();
        }
    }

    public class AttachButton extends FrameLayout {
        private int backgroundKey;
        private Animator checkAnimator;
        private boolean checked;
        private float checkedState;
        private int currentId;
        private RLottieImageView imageView;
        private int textKey;
        private TextView textView;

        public class AnonymousClass1 extends RLottieImageView {
            final ChatAttachAlert val$this$0;

            AnonymousClass1(Context context, ChatAttachAlert chatAttachAlert) {
                super(context);
                r3 = chatAttachAlert;
            }

            @Override
            public void setScaleX(float f) {
                super.setScaleX(f);
                AttachButton.this.invalidate();
            }
        }

        public AttachButton(Context context) {
            super(context);
            setWillNotDraw(false);
            setFocusable(true);
            AnonymousClass1 anonymousClass1 = new RLottieImageView(context) {
                final ChatAttachAlert val$this$0;

                AnonymousClass1(Context context2, ChatAttachAlert chatAttachAlert) {
                    super(context2);
                    r3 = chatAttachAlert;
                }

                @Override
                public void setScaleX(float f) {
                    super.setScaleX(f);
                    AttachButton.this.invalidate();
                }
            };
            this.imageView = anonymousClass1;
            anonymousClass1.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(32, 32.0f, 49, 0.0f, 18.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context2);
            this.textView = textView;
            textView.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing(-AndroidUtilities.dp(2.0f), 1.0f);
            this.textView.setImportantForAccessibility(2);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float scaleX = this.imageView.getScaleX() + (this.checkedState * 0.06f);
            float dp = AndroidUtilities.dp(23.0f) * scaleX;
            float left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f);
            float top = this.imageView.getTop() + (this.imageView.getMeasuredWidth() / 2.0f);
            ChatAttachAlert.this.attachButtonPaint.setColor(ChatAttachAlert.this.getThemedColor(this.backgroundKey));
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.STROKE);
            ChatAttachAlert.this.attachButtonPaint.setStrokeWidth(AndroidUtilities.dp(3.0f) * scaleX);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(left, top, dp - (ChatAttachAlert.this.attachButtonPaint.getStrokeWidth() * 0.5f), ChatAttachAlert.this.attachButtonPaint);
            ChatAttachAlert.this.attachButtonPaint.setAlpha(255);
            ChatAttachAlert.this.attachButtonPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, top, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), ChatAttachAlert.this.attachButtonPaint);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(this.textView.getText());
            accessibilityNodeInfo.setEnabled(true);
            accessibilityNodeInfo.setSelected(this.checked);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), 1073741824));
        }

        public void setCheckedState(float f) {
            this.checkedState = f;
            float f2 = 1.0f - (f * 0.06f);
            this.imageView.setScaleX(f2);
            this.imageView.setScaleY(f2);
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
            invalidate();
        }

        public void setTextAndIcon(int i, CharSequence charSequence, Drawable drawable, int i2, int i3) {
            this.currentId = i;
            this.textView.setText(charSequence);
            this.imageView.setImageDrawable(drawable);
            this.backgroundKey = i2;
            this.textKey = i3;
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
        }

        public void setTextAndIcon(int i, CharSequence charSequence, RLottieDrawable rLottieDrawable, int i2, int i3) {
            this.currentId = i;
            this.textView.setText(charSequence);
            this.imageView.setAnimation(rLottieDrawable);
            this.backgroundKey = i2;
            this.textKey = i3;
            this.textView.setTextColor(ColorUtils.blendARGB(ChatAttachAlert.this.getThemedColor(Theme.key_dialogTextGray2), ChatAttachAlert.this.getThemedColor(this.textKey), this.checkedState));
        }

        void updateCheckedState(boolean z) {
            if (this.checked == (((long) this.currentId) == ChatAttachAlert.this.selectedId)) {
                return;
            }
            this.checked = ((long) this.currentId) == ChatAttachAlert.this.selectedId;
            Animator animator = this.checkAnimator;
            if (animator != null) {
                animator.cancel();
            }
            if (!z) {
                this.imageView.stopAnimation();
                this.imageView.setProgress(0.0f);
                setCheckedState(this.checked ? 1.0f : 0.0f);
                return;
            }
            if (this.checked) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", this.checked ? 1.0f : 0.0f);
            this.checkAnimator = ofFloat;
            ofFloat.setDuration(200L);
            this.checkAnimator.start();
        }
    }

    public class ButtonsAdapter extends RecyclerListView.SelectionAdapter {
        private int attachBotsEndRow;
        private int attachBotsStartRow;
        private List attachMenuBots = new ArrayList();
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;
        private int quickRepliesButton;

        public ButtonsAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            int i = this.buttonsCount;
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            return (chatAttachAlert.editingMessageObject == null && (chatAttachAlert.baseFragment instanceof ChatActivity)) ? i + MediaDataController.getInstance(chatAttachAlert.currentAccount).inlineBots.size() : i;
        }

        @Override
        public int getItemViewType(int i) {
            if (i < this.buttonsCount) {
                return (i < this.attachBotsStartRow || i >= this.attachBotsEndRow) ? 0 : 1;
            }
            return 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void notifyDataSetChanged() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.ButtonsAdapter.notifyDataSetChanged():void");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            String string;
            RLottieDrawable rLottieDrawable;
            int i3;
            int i4;
            int i5;
            String string2;
            RLottieDrawable rLottieDrawable2;
            int i6;
            int i7;
            int i8;
            Integer valueOf;
            int itemViewType = viewHolder.getItemViewType();
            int i9 = 1;
            if (itemViewType != 0) {
                if (itemViewType != 1) {
                    return;
                }
                AttachBotButton attachBotButton = (AttachBotButton) viewHolder.itemView;
                int i10 = this.attachBotsStartRow;
                if (i < i10 || i >= this.attachBotsEndRow) {
                    int i11 = i - this.buttonsCount;
                    attachBotButton.setTag(Integer.valueOf(i11));
                    attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i11).peer.user_id)));
                    return;
                } else {
                    int i12 = i - i10;
                    attachBotButton.setTag(Integer.valueOf(i12));
                    TLRPC.TL_attachMenuBot tL_attachMenuBot = (TLRPC.TL_attachMenuBot) this.attachMenuBots.get(i12);
                    attachBotButton.setAttachBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Long.valueOf(tL_attachMenuBot.bot_id)), tL_attachMenuBot);
                    return;
                }
            }
            AttachButton attachButton = (AttachButton) viewHolder.itemView;
            if (i != this.galleryButton) {
                int i13 = 4;
                if (i != this.documentButton) {
                    if (i == this.locationButton) {
                        attachButton.setTextAndIcon(6, (CharSequence) LocaleController.getString("ChatLocation", R.string.ChatLocation), Theme.chat_attachButtonDrawables[4], Theme.key_chat_attachLocationBackground, Theme.key_chat_attachLocationText);
                        i2 = 6;
                    } else {
                        i13 = 3;
                        if (i == this.musicButton) {
                            string2 = LocaleController.getString("AttachMusic", R.string.AttachMusic);
                            rLottieDrawable2 = Theme.chat_attachButtonDrawables[1];
                            i6 = Theme.key_chat_attachAudioBackground;
                            i7 = Theme.key_chat_attachAudioText;
                            i8 = 3;
                        } else {
                            i9 = 5;
                            if (i == this.pollButton) {
                                attachButton.setTextAndIcon(9, (CharSequence) LocaleController.getString("Poll", R.string.Poll), Theme.chat_attachButtonDrawables[5], Theme.key_chat_attachPollBackground, Theme.key_chat_attachPollText);
                                i2 = 9;
                            } else if (i == this.contactButton) {
                                string = LocaleController.getString("AttachContact", R.string.AttachContact);
                                rLottieDrawable = Theme.chat_attachButtonDrawables[3];
                                i3 = Theme.key_chat_attachContactBackground;
                                i4 = Theme.key_chat_attachContactText;
                                i5 = 5;
                            } else {
                                if (i != this.quickRepliesButton) {
                                    return;
                                }
                                attachButton.setTextAndIcon(11, LocaleController.getString(R.string.AttachQuickReplies), ChatAttachAlert.this.getContext().getResources().getDrawable(R.drawable.ic_ab_reply).mutate(), Theme.key_chat_attachContactBackground, Theme.key_chat_attachContactText);
                                i2 = 11;
                            }
                        }
                    }
                    valueOf = Integer.valueOf(i2);
                    attachButton.setTag(valueOf);
                }
                string2 = LocaleController.getString("ChatDocument", R.string.ChatDocument);
                rLottieDrawable2 = Theme.chat_attachButtonDrawables[2];
                i6 = Theme.key_chat_attachFileBackground;
                i7 = Theme.key_chat_attachFileText;
                i8 = 4;
                attachButton.setTextAndIcon(i8, (CharSequence) string2, rLottieDrawable2, i6, i7);
                valueOf = Integer.valueOf(i13);
                attachButton.setTag(valueOf);
            }
            string = LocaleController.getString("ChatGallery", R.string.ChatGallery);
            rLottieDrawable = Theme.chat_attachButtonDrawables[0];
            i3 = Theme.key_chat_attachGalleryBackground;
            i4 = Theme.key_chat_attachGalleryText;
            i5 = 1;
            attachButton.setTextAndIcon(i5, (CharSequence) string, rLottieDrawable, i3, i4);
            valueOf = Integer.valueOf(i9);
            attachButton.setTag(valueOf);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View attachBotButton = i != 0 ? new AttachBotButton(this.mContext) : new AttachButton(this.mContext);
            attachBotButton.setImportantForAccessibility(1);
            attachBotButton.setFocusable(true);
            return new RecyclerListView.Holder(attachBotButton);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            ChatAttachAlert.this.applyAttachButtonColors(viewHolder.itemView);
        }
    }

    public interface ChatAttachViewDelegate {

        public abstract class CC {
            public static void $default$didSelectBot(ChatAttachViewDelegate chatAttachViewDelegate, TLRPC.User user) {
            }

            public static void $default$doOnIdle(ChatAttachViewDelegate chatAttachViewDelegate, Runnable runnable) {
                runnable.run();
            }

            public static boolean $default$needEnterComment(ChatAttachViewDelegate chatAttachViewDelegate) {
                return false;
            }

            public static void $default$onCameraOpened(ChatAttachViewDelegate chatAttachViewDelegate) {
            }

            public static void $default$onWallpaperSelected(ChatAttachViewDelegate chatAttachViewDelegate, Object obj) {
            }

            public static void $default$openAvatarsSearch(ChatAttachViewDelegate chatAttachViewDelegate) {
            }

            public static boolean $default$selectItemOnClicking(ChatAttachViewDelegate chatAttachViewDelegate) {
                return false;
            }

            public static void $default$sendAudio(ChatAttachViewDelegate chatAttachViewDelegate, ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2) {
            }
        }

        void didPressedButton(int i, boolean z, boolean z2, int i2, long j, boolean z3, boolean z4);

        void didSelectBot(TLRPC.User user);

        void doOnIdle(Runnable runnable);

        boolean needEnterComment();

        void onCameraOpened();

        void onWallpaperSelected(Object obj);

        void openAvatarsSearch();

        boolean selectItemOnClicking();

        void sendAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2);
    }

    public ChatAttachAlert(Context context, BaseFragment baseFragment, boolean z, boolean z2) {
        this(context, baseFragment, z, z2, true, null);
    }

    public ChatAttachAlert(final Context context, final BaseFragment baseFragment, boolean z, final boolean z2, boolean z3, final Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        int i;
        this.canOpenPreview = false;
        this.isSoundPicker = false;
        this.isStoryLocationPicker = false;
        this.isBizLocationPicker = false;
        this.isStoryAudioPicker = false;
        this.translationProgress = 0.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION = new AnimationProperties.FloatProperty("translation") {
            AnonymousClass2(String str) {
                super(str);
            }

            @Override
            public Float get(AttachAlertLayout attachAlertLayout) {
                return Float.valueOf(ChatAttachAlert.this.translationProgress);
            }

            @Override
            public void setValue(AttachAlertLayout attachAlertLayout, float f) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.translationProgress = f;
                if (chatAttachAlert.nextAttachLayout == null) {
                    return;
                }
                if ((ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                    int max = Math.max(ChatAttachAlert.this.nextAttachLayout.getWidth(), ChatAttachAlert.this.currentAttachLayout.getWidth());
                    if (ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) {
                        ChatAttachAlert.this.currentAttachLayout.setTranslationX((-max) * f);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((1.0f - f) * max);
                    } else {
                        ChatAttachAlert.this.currentAttachLayout.setTranslationX(max * f);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((-max) * (1.0f - f));
                    }
                } else {
                    ChatAttachAlert.this.nextAttachLayout.setAlpha(f);
                    ChatAttachAlert.this.nextAttachLayout.onHideShowProgress(f);
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                        chatAttachAlert2.updateSelectedPosition(chatAttachAlert2.nextAttachLayout == ChatAttachAlert.this.pollLayout ? 1 : 0);
                    }
                    ChatAttachAlert.this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f) * f);
                    ChatAttachAlert.this.currentAttachLayout.onHideShowProgress(1.0f - Math.min(1.0f, f / 0.7f));
                    ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                }
                if (ChatAttachAlert.this.viewChangeAnimator != null) {
                    ChatAttachAlert.this.updateSelectedPosition(1);
                }
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }
        };
        this.layouts = new AttachAlertLayout[8];
        this.botAttachLayouts = new LongSparseArray();
        this.commentTextViewLocation = new int[2];
        this.textPaint = new TextPaint(1);
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.sendButtonEnabled = true;
        this.sendButtonEnabledProgress = 1.0f;
        this.cornerRadius = 1.0f;
        this.botButtonProgressWasVisible = false;
        this.botButtonWasVisible = false;
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        this.documentsEnabled = true;
        this.photosEnabled = true;
        this.videosEnabled = true;
        this.musicEnabled = true;
        this.pollsEnabled = true;
        this.plainTextEnabled = true;
        this.maxSelectedPhotos = -1;
        this.allowOrder = true;
        this.attachItemSize = AndroidUtilities.dp(85.0f);
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.scrollOffsetY = new int[2];
        this.attachButtonPaint = new Paint(1);
        this.captionLimitBulletinShown = false;
        this.exclusionRects = new ArrayList();
        this.exclustionRect = new android.graphics.Rect();
        this.ATTACH_ALERT_PROGRESS = new AnimationProperties.FloatProperty("openProgress") {
            private float openProgress;

            AnonymousClass21(String str) {
                super(str);
            }

            @Override
            public Float get(ChatAttachAlert chatAttachAlert) {
                return Float.valueOf(this.openProgress);
            }

            @Override
            public void setValue(ChatAttachAlert chatAttachAlert, float f) {
                float f2;
                View view;
                int childCount = ChatAttachAlert.this.buttonsRecyclerView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    float f3 = (3 - i3) * 32.0f;
                    View childAt = ChatAttachAlert.this.buttonsRecyclerView.getChildAt(i3);
                    if (f > f3) {
                        float f4 = f - f3;
                        if (f4 <= 200.0f) {
                            float f5 = f4 / 200.0f;
                            f2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(f5) * 1.1f;
                            childAt.setAlpha(CubicBezierInterpolator.EASE_BOTH.getInterpolation(f5));
                        } else {
                            childAt.setAlpha(1.0f);
                            float f6 = f4 - 200.0f;
                            f2 = f6 <= 100.0f ? 1.1f - (CubicBezierInterpolator.EASE_IN.getInterpolation(f6 / 100.0f) * 0.1f) : 1.0f;
                        }
                    } else {
                        f2 = 0.0f;
                    }
                    if (childAt instanceof AttachButton) {
                        AttachButton attachButton = (AttachButton) childAt;
                        attachButton.textView.setScaleX(f2);
                        attachButton.textView.setScaleY(f2);
                        attachButton.imageView.setScaleX(f2);
                        view = attachButton.imageView;
                    } else if (childAt instanceof AttachBotButton) {
                        AttachBotButton attachBotButton = (AttachBotButton) childAt;
                        attachBotButton.nameTextView.setScaleX(f2);
                        attachBotButton.nameTextView.setScaleY(f2);
                        attachBotButton.imageView.setScaleX(f2);
                        view = attachBotButton.imageView;
                    }
                    view.setScaleY(f2);
                }
            }
        };
        this.allowDrawContent = true;
        this.sent = false;
        this.confirmationAlertShown = false;
        this.allowPassConfirmationAlert = false;
        boolean z4 = baseFragment instanceof ChatActivity;
        if (z4) {
            setImageReceiverNumLevel(0, 4);
        }
        this.forceDarkTheme = z;
        this.showingFromDialog = z2;
        this.drawNavigationBar = true;
        this.inBubbleMode = z4 && baseFragment.isInBubbleMode();
        this.openInterpolator = new OvershootInterpolator(0.7f);
        this.baseFragment = baseFragment;
        this.useSmoothKeyboard = true;
        setDelegate(this);
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(i2).addObserver(this, NotificationCenter.quickRepliesUpdated);
        this.exclusionRects.add(this.exclustionRect);
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(context, z);
        this.sizeNotifierFrameLayout = anonymousClass3;
        anonymousClass3.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() {
            AnonymousClass4() {
            }

            @Override
            public void onSizeChanged(int i3, boolean z5) {
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout) {
                    ChatAttachAlert.this.currentAttachLayout.invalidate();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierFrameLayout;
        this.containerView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        ViewGroup viewGroup = this.containerView;
        int i3 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i3, 0, i3, 0);
        AnonymousClass5 anonymousClass5 = new ActionBar(context, resourcesProvider) {
            AnonymousClass5(final Context context2, final Theme.ResourcesProvider resourcesProvider2) {
                super(context2, resourcesProvider2);
            }

            @Override
            public void setAlpha(float f) {
                float alpha = getAlpha();
                super.setAlpha(f);
                if (alpha != f) {
                    ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
                    if (ChatAttachAlert.this.frameLayout2 != null) {
                        ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                        if (chatAttachAlert.buttonsRecyclerView != null) {
                            if (chatAttachAlert.frameLayout2.getTag() != null) {
                                if (ChatAttachAlert.this.currentAttachLayout == null) {
                                    float f2 = f != 0.0f ? 0.0f : 1.0f;
                                    if (ChatAttachAlert.this.buttonsRecyclerView.getAlpha() != f2) {
                                        ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f2);
                                        return;
                                    }
                                    return;
                                }
                                return;
                            }
                            if (ChatAttachAlert.this.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                                float f3 = 1.0f - f;
                                ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f3);
                                ChatAttachAlert.this.shadow.setAlpha(f3);
                                ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(AndroidUtilities.dp(44.0f) * f);
                            }
                            ChatAttachAlert.this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f) * f);
                            ChatAttachAlert.this.shadow.setTranslationY((AndroidUtilities.dp(84.0f) * f) + ChatAttachAlert.this.botMainButtonOffsetY);
                        }
                    }
                }
            }
        };
        this.actionBar = anonymousClass5;
        int i4 = Theme.key_dialogBackground;
        anonymousClass5.setBackgroundColor(getThemedColor(i4));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        ActionBar actionBar = this.actionBar;
        int i5 = Theme.key_dialogTextBlack;
        actionBar.setItemsColor(getThemedColor(i5), false);
        ActionBar actionBar2 = this.actionBar;
        int i6 = Theme.key_dialogButtonSelector;
        actionBar2.setItemsBackgroundColor(getThemedColor(i6), false);
        this.actionBar.setTitleColor(getThemedColor(i5));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            AnonymousClass6() {
            }

            @Override
            public void onItemClick(int i7) {
                if (i7 != -1) {
                    ChatAttachAlert.this.currentAttachLayout.onMenuItemClick(i7);
                } else {
                    if (ChatAttachAlert.this.currentAttachLayout.onBackPressed()) {
                        return;
                    }
                    ChatAttachAlert.this.dismiss();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, null, 0, getThemedColor(i5), false, resourcesProvider2);
        this.selectedMenuItem = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        ActionBarMenuItem actionBarMenuItem2 = this.selectedMenuItem;
        int i7 = R.drawable.ic_ab_other;
        actionBarMenuItem2.setIcon(i7);
        ActionBarMenuItem actionBarMenuItem3 = this.selectedMenuItem;
        int i8 = R.string.AccDescrMoreOptions;
        actionBarMenuItem3.setContentDescription(LocaleController.getString(i8));
        this.selectedMenuItem.setVisibility(4);
        this.selectedMenuItem.setAlpha(0.0f);
        this.selectedMenuItem.setScaleX(0.6f);
        this.selectedMenuItem.setScaleY(0.6f);
        this.selectedMenuItem.setSubMenuOpenSide(2);
        this.selectedMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            @Override
            public final void onItemClick(int i9) {
                ChatAttachAlert.this.lambda$new$0(i9);
            }
        });
        this.selectedMenuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
        this.selectedMenuItem.setTranslationX(AndroidUtilities.dp(6.0f));
        this.selectedMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(i6), 6));
        this.selectedMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$1(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem4 = new ActionBarMenuItem(context2, null, 0, getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader), true, resourcesProvider2);
        this.doneItem = actionBarMenuItem4;
        actionBarMenuItem4.setLongClickEnabled(false);
        this.doneItem.setText(LocaleController.getString(R.string.Create).toUpperCase());
        this.doneItem.setVisibility(4);
        this.doneItem.setAlpha(0.0f);
        this.doneItem.setTranslationX(-AndroidUtilities.dp(12.0f));
        this.doneItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(i6), 3));
        this.doneItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$2(view);
            }
        });
        if (baseFragment != null) {
            i = i6;
            ActionBarMenuItem actionBarMenuItem5 = new ActionBarMenuItem(context2, null, 0, getThemedColor(i5), false, resourcesProvider2);
            this.searchItem = actionBarMenuItem5;
            actionBarMenuItem5.setLongClickEnabled(false);
            this.searchItem.setIcon(R.drawable.ic_ab_search);
            this.searchItem.setContentDescription(LocaleController.getString(R.string.Search));
            this.searchItem.setVisibility(4);
            this.searchItem.setAlpha(0.0f);
            this.searchItem.setTranslationX(-AndroidUtilities.dp(42.0f));
            this.searchItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(i), 6));
            this.searchItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ChatAttachAlert.this.lambda$new$3(z2, view);
                }
            });
        } else {
            i = i6;
        }
        ActionBarMenuItem actionBarMenuItem6 = new ActionBarMenuItem(context2, null, 0, getThemedColor(i5), false, resourcesProvider2);
        this.optionsItem = actionBarMenuItem6;
        actionBarMenuItem6.setLongClickEnabled(false);
        this.optionsItem.setIcon(i7);
        this.optionsItem.setContentDescription(LocaleController.getString(i8));
        this.optionsItem.setVisibility(8);
        this.optionsItem.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 3));
        this.optionsItem.addSubItem(1, R.drawable.msg_addbot, LocaleController.getString(R.string.StickerCreateEmpty)).setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$4(resourcesProvider2, view);
            }
        });
        this.optionsItem.setMenuYOffset(AndroidUtilities.dp(-12.0f));
        this.optionsItem.setAdditionalXOffset(AndroidUtilities.dp(12.0f));
        this.optionsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$5(view);
            }
        });
        AnonymousClass9 anonymousClass9 = new FrameLayout(context2) {
            AnonymousClass9(final Context context2) {
                super(context2);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override
            public void setAlpha(float f) {
                super.setAlpha(f);
                ChatAttachAlert.this.updateSelectedPosition(0);
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }
        };
        this.headerView = anonymousClass9;
        anonymousClass9.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ChatAttachAlert.this.lambda$new$6(view);
            }
        });
        this.headerView.setAlpha(0.0f);
        this.headerView.setVisibility(4);
        LinearLayout linearLayout = new LinearLayout(context2);
        this.selectedView = linearLayout;
        linearLayout.setOrientation(0);
        this.selectedView.setGravity(16);
        TextView textView = new TextView(context2);
        this.selectedTextView = textView;
        textView.setTextColor(getThemedColor(i5));
        this.selectedTextView.setTextSize(1, 16.0f);
        this.selectedTextView.setTypeface(AndroidUtilities.bold());
        this.selectedTextView.setGravity(19);
        this.selectedTextView.setMaxLines(1);
        this.selectedTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.selectedView.addView(this.selectedTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.selectedArrowImageView = new ImageView(context2);
        Drawable mutate = getContext().getResources().getDrawable(R.drawable.attach_arrow_right).mutate();
        int themedColor = getThemedColor(i5);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        mutate.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        this.selectedArrowImageView.setImageDrawable(mutate);
        this.selectedArrowImageView.setVisibility(8);
        this.selectedView.addView(this.selectedArrowImageView, LayoutHelper.createLinear(-2, -2, 16, 4, 1, 0, 0));
        this.selectedView.setAlpha(1.0f);
        this.headerView.addView(this.selectedView, LayoutHelper.createFrame(-2, -1.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.mediaPreviewView = linearLayout2;
        linearLayout2.setOrientation(0);
        this.mediaPreviewView.setGravity(16);
        ImageView imageView = new ImageView(context2);
        Drawable mutate2 = getContext().getResources().getDrawable(R.drawable.attach_arrow_left).mutate();
        mutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor(i5), mode));
        imageView.setImageDrawable(mutate2);
        this.mediaPreviewView.addView(imageView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 4, 0));
        TextView textView2 = new TextView(context2);
        this.mediaPreviewTextView = textView2;
        textView2.setTextColor(getThemedColor(i5));
        this.mediaPreviewTextView.setTextSize(1, 16.0f);
        this.mediaPreviewTextView.setTypeface(AndroidUtilities.bold());
        this.mediaPreviewTextView.setGravity(19);
        this.mediaPreviewTextView.setText(LocaleController.getString("AttachMediaPreview", R.string.AttachMediaPreview));
        this.mediaPreviewView.setAlpha(0.0f);
        this.mediaPreviewView.addView(this.mediaPreviewTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.headerView.addView(this.mediaPreviewView, LayoutHelper.createFrame(-2, -1.0f));
        AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = new ChatAttachAlertPhotoLayout(this, context2, z, z3, resourcesProvider2);
        this.photoLayout = chatAttachAlertPhotoLayout;
        attachAlertLayoutArr[0] = chatAttachAlertPhotoLayout;
        chatAttachAlertPhotoLayout.setTranslationX(0.0f);
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = this.photoLayout;
        this.currentAttachLayout = chatAttachAlertPhotoLayout2;
        this.selectedId = 1L;
        this.containerView.addView(chatAttachAlertPhotoLayout2, LayoutHelper.createFrame(-1, -1.0f));
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, -2.0f, 51, 23.0f, 0.0f, 12.0f, 0.0f));
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.containerView.addView(this.selectedMenuItem, LayoutHelper.createFrame(48, 48, 53));
        ActionBarMenuItem actionBarMenuItem7 = this.searchItem;
        if (actionBarMenuItem7 != null) {
            this.containerView.addView(actionBarMenuItem7, LayoutHelper.createFrame(48, 48, 53));
        }
        ActionBarMenuItem actionBarMenuItem8 = this.optionsItem;
        if (actionBarMenuItem8 != null) {
            this.headerView.addView(actionBarMenuItem8, LayoutHelper.createFrame(32, 32.0f, 21, 0.0f, 0.0f, 0.0f, 8.0f));
        }
        this.containerView.addView(this.doneItem, LayoutHelper.createFrame(-2, 48, 53));
        View view = new View(context2);
        this.actionBarShadow = view;
        view.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        View view2 = new View(context2);
        this.shadow = view2;
        view2.setBackgroundResource(R.drawable.attach_shadow);
        this.shadow.getBackground().setColorFilter(new PorterDuffColorFilter(-16777216, mode));
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 84.0f));
        AnonymousClass10 anonymousClass10 = new RecyclerListView(context2) {
            AnonymousClass10(final Context context2) {
                super(context2);
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                ChatAttachAlert.this.currentAttachLayout.onButtonsTranslationYUpdated();
            }
        };
        this.buttonsRecyclerView = anonymousClass10;
        ButtonsAdapter buttonsAdapter = new ButtonsAdapter(context2);
        this.buttonsAdapter = buttonsAdapter;
        anonymousClass10.setAdapter(buttonsAdapter);
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
        this.buttonsLayoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.buttonsRecyclerView.setVerticalScrollBarEnabled(false);
        this.buttonsRecyclerView.setHorizontalScrollBarEnabled(false);
        this.buttonsRecyclerView.setItemAnimator(null);
        this.buttonsRecyclerView.setLayoutAnimation(null);
        this.buttonsRecyclerView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.buttonsRecyclerView.setBackgroundColor(getThemedColor(i4));
        this.buttonsRecyclerView.setImportantForAccessibility(1);
        this.containerView.addView(this.buttonsRecyclerView, LayoutHelper.createFrame(-1, 84, 83));
        this.buttonsRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view3, int i9) {
                ChatAttachAlert.this.lambda$new$12(resourcesProvider2, view3, i9);
            }
        });
        this.buttonsRecyclerView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view3, int i9) {
                boolean lambda$new$13;
                lambda$new$13 = ChatAttachAlert.this.lambda$new$13(view3, i9);
                return lambda$new$13;
            }
        });
        TextView textView3 = new TextView(context2);
        this.botMainButtonTextView = textView3;
        textView3.setVisibility(8);
        this.botMainButtonTextView.setAlpha(0.0f);
        this.botMainButtonTextView.setSingleLine();
        this.botMainButtonTextView.setGravity(17);
        this.botMainButtonTextView.setTypeface(AndroidUtilities.bold());
        int dp = AndroidUtilities.dp(16.0f);
        this.botMainButtonTextView.setPadding(dp, 0, dp, 0);
        this.botMainButtonTextView.setTextSize(1, 14.0f);
        this.botMainButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                ChatAttachAlert.this.lambda$new$14(view3);
            }
        });
        this.containerView.addView(this.botMainButtonTextView, LayoutHelper.createFrame(-1, 48, 83));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        this.botProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.botProgressView.setAlpha(0.0f);
        this.botProgressView.setScaleX(0.1f);
        this.botProgressView.setScaleY(0.1f);
        this.botProgressView.setVisibility(8);
        this.containerView.addView(this.botProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        AnonymousClass11 anonymousClass11 = new AnonymousClass11(context2, z);
        this.frameLayout2 = anonymousClass11;
        anonymousClass11.setWillNotDraw(false);
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setAlpha(0.0f);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2, 83));
        this.frameLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view3, MotionEvent motionEvent) {
                boolean lambda$new$15;
                lambda$new$15 = ChatAttachAlert.lambda$new$15(view3, motionEvent);
                return lambda$new$15;
            }
        });
        NumberTextView numberTextView = new NumberTextView(context2);
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        numberTextView.setTextSize(15);
        numberTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        numberTextView.setTypeface(AndroidUtilities.bold());
        numberTextView.setCenterAlign(true);
        this.frameLayout2.addView(numberTextView, LayoutHelper.createFrame(56, 20.0f, 85, 3.0f, 0.0f, 14.0f, 78.0f));
        this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        AnonymousClass12 anonymousClass12 = new AnonymousClass12(context2, this.sizeNotifierFrameLayout, null, 1, true, resourcesProvider2);
        this.commentTextView = anonymousClass12;
        anonymousClass12.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.commentTextView.onResume();
        this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() {
            private boolean processChange;
            private boolean wasEmpty;

            class AnonymousClass1 extends AnimatorListenerAdapter {
                AnonymousClass1() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.captionLimitView.setVisibility(8);
                }
            }

            AnonymousClass13() {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int i9;
                boolean z5 = true;
                if (this.wasEmpty != TextUtils.isEmpty(editable)) {
                    if (ChatAttachAlert.this.currentAttachLayout != null) {
                        ChatAttachAlert.this.currentAttachLayout.onSelectedItemsCountChanged(ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount());
                    }
                    this.wasEmpty = !this.wasEmpty;
                }
                if (this.processChange) {
                    for (ImageSpan imageSpan : (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class)) {
                        editable.removeSpan(imageSpan);
                    }
                    Emoji.replaceEmoji((CharSequence) editable, ChatAttachAlert.this.commentTextView.getEditText().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    this.processChange = false;
                }
                ChatAttachAlert.this.codepointCount = Character.codePointCount(editable, 0, editable.length());
                if (ChatAttachAlert.this.currentLimit <= 0 || (i9 = ChatAttachAlert.this.currentLimit - ChatAttachAlert.this.codepointCount) > 100) {
                    ChatAttachAlert.this.captionLimitView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(100L).setListener(new AnimatorListenerAdapter() {
                        AnonymousClass1() {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            ChatAttachAlert.this.captionLimitView.setVisibility(8);
                        }
                    });
                } else {
                    if (i9 < -9999) {
                        i9 = -9999;
                    }
                    ChatAttachAlert.this.captionLimitView.setNumber(i9, ChatAttachAlert.this.captionLimitView.getVisibility() == 0);
                    if (ChatAttachAlert.this.captionLimitView.getVisibility() != 0) {
                        ChatAttachAlert.this.captionLimitView.setVisibility(0);
                        ChatAttachAlert.this.captionLimitView.setAlpha(0.0f);
                        ChatAttachAlert.this.captionLimitView.setScaleX(0.5f);
                        ChatAttachAlert.this.captionLimitView.setScaleY(0.5f);
                    }
                    ChatAttachAlert.this.captionLimitView.animate().setListener(null).cancel();
                    ChatAttachAlert.this.captionLimitView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).start();
                    if (i9 < 0) {
                        ChatAttachAlert.this.captionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_text_RedRegular));
                        z5 = false;
                    } else {
                        ChatAttachAlert.this.captionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
                    }
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (chatAttachAlert.sendButtonEnabled != z5) {
                    chatAttachAlert.sendButtonEnabled = z5;
                    chatAttachAlert.writeButton.invalidate();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i9, int i22, int i32) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i9, int i22, int i32) {
                if (i32 - i22 >= 1) {
                    this.processChange = true;
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (chatAttachAlert.mentionContainer == null) {
                    chatAttachAlert.createMentionsContainer();
                }
                if (ChatAttachAlert.this.mentionContainer.getAdapter() != null) {
                    ChatAttachAlert.this.mentionContainer.getAdapter().lambda$searchUsernameOrHashtag$7(charSequence, ChatAttachAlert.this.commentTextView.getEditText().getSelectionStart(), null, false, false);
                }
            }
        });
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 84.0f, 0.0f));
        this.frameLayout2.setClipChildren(false);
        this.commentTextView.setClipChildren(false);
        AnonymousClass14 anonymousClass14 = new FrameLayout(context2) {
            AnonymousClass14(final Context context2) {
                super(context2);
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                String formatPluralString;
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout) {
                    formatPluralString = LocaleController.formatPluralString("AccDescrSendPhotos", ChatAttachAlert.this.photoLayout.getSelectedItemsCount(), new Object[0]);
                } else {
                    if (ChatAttachAlert.this.currentAttachLayout != ChatAttachAlert.this.documentLayout) {
                        if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.audioLayout) {
                            formatPluralString = LocaleController.formatPluralString("AccDescrSendAudio", ChatAttachAlert.this.audioLayout.getSelectedItemsCount(), new Object[0]);
                        }
                        accessibilityNodeInfo.setClassName(Button.class.getName());
                        accessibilityNodeInfo.setLongClickable(true);
                        accessibilityNodeInfo.setClickable(true);
                    }
                    formatPluralString = LocaleController.formatPluralString("AccDescrSendFiles", ChatAttachAlert.this.documentLayout.getSelectedItemsCount(), new Object[0]);
                }
                accessibilityNodeInfo.setText(formatPluralString);
                accessibilityNodeInfo.setClassName(Button.class.getName());
                accessibilityNodeInfo.setLongClickable(true);
                accessibilityNodeInfo.setClickable(true);
            }
        };
        this.writeButtonContainer = anonymousClass14;
        anonymousClass14.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        AnonymousClass15 anonymousClass15 = new ChatActivityEnterView.SendButton(context2, R.drawable.attach_send, resourcesProvider2) {
            AnonymousClass15(final Context context2, int i9, final Theme.ResourcesProvider resourcesProvider2) {
                super(context2, i9, resourcesProvider2);
            }

            @Override
            public int getFillColor() {
                return ChatAttachAlert.this.getThemedColor(Theme.key_dialogFloatingButton);
            }

            @Override
            public boolean isInScheduleMode() {
                return super.isInScheduleMode();
            }

            @Override
            public boolean isInactive() {
                return !ChatAttachAlert.this.sendButtonEnabled;
            }

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public boolean shouldDrawBackground() {
                return true;
            }
        };
        this.writeButton = anonymousClass15;
        anonymousClass15.center = true;
        anonymousClass15.setImportantForAccessibility(2);
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(64, 64.0f, 51, -4.0f, -4.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                ChatAttachAlert.this.lambda$new$17(baseFragment, resourcesProvider2, view3);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view3) {
                boolean lambda$new$26;
                lambda$new$26 = ChatAttachAlert.this.lambda$new$26(context2, resourcesProvider2, baseFragment, view3);
                return lambda$new$26;
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.bold());
        AnonymousClass16 anonymousClass16 = new View(context2) {
            AnonymousClass16(final Context context2) {
                super(context2);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                String format = String.format("%d", Integer.valueOf(Math.max(1, ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount())));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(ChatAttachAlert.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int themedColor2 = ChatAttachAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck);
                TextPaint textPaint = ChatAttachAlert.this.textPaint;
                double alpha = Color.alpha(themedColor2);
                double d = ChatAttachAlert.this.sendButtonEnabledProgress;
                Double.isNaN(d);
                Double.isNaN(alpha);
                textPaint.setColor(ColorUtils.setAlphaComponent(themedColor2, (int) (alpha * ((d * 0.42d) + 0.58d))));
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogBackground));
                int i9 = max / 2;
                ChatAttachAlert.this.rect.set(measuredWidth - i9, 0.0f, i9 + measuredWidth, getMeasuredHeight());
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ChatAttachAlert.this.paint);
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_chat_attachCheckBoxBackground));
                ChatAttachAlert.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ChatAttachAlert.this.paint);
                canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), ChatAttachAlert.this.textPaint);
            }
        };
        this.selectedCountView = anonymousClass16;
        anonymousClass16.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        if (z) {
            checkColors();
            this.navBarColorKey = -1;
        }
        PasscodeView passcodeView = new PasscodeView(context2);
        this.passcodeView = passcodeView;
        this.containerView.addView(passcodeView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void applyAttachButtonColors(View view) {
        TextView textView;
        int themedColor;
        int i;
        float f;
        if (view instanceof AttachButton) {
            AttachButton attachButton = (AttachButton) view;
            textView = attachButton.textView;
            themedColor = getThemedColor(Theme.key_dialogTextGray2);
            i = getThemedColor(attachButton.textKey);
            f = attachButton.checkedState;
        } else {
            if (!(view instanceof AttachBotButton)) {
                return;
            }
            AttachBotButton attachBotButton = (AttachBotButton) view;
            textView = attachBotButton.nameTextView;
            themedColor = getThemedColor(Theme.key_dialogTextGray2);
            i = attachBotButton.textColor;
            f = attachBotButton.checkedState;
        }
        textView.setTextColor(ColorUtils.blendARGB(themedColor, i, f));
    }

    public void createMentionsContainer() {
        this.mentionContainer = new MentionsContainerView(getContext(), UserConfig.getInstance(this.currentAccount).getClientUserId(), 0L, LaunchActivity.getLastFragment(), null, this.resourcesProvider) {
            AnonymousClass25(Context context, long j, long j2, BaseFragment baseFragment, SizeNotifierFrameLayout sizeNotifierFrameLayout, Theme.ResourcesProvider resourcesProvider) {
                super(context, j, j2, baseFragment, sizeNotifierFrameLayout, resourcesProvider);
            }

            @Override
            protected void onAnimationScroll() {
                if (ChatAttachAlert.this.photoLayout != null) {
                    ChatAttachAlert.this.photoLayout.checkCameraViewPosition();
                }
            }

            @Override
            protected void onScrolled(boolean z, boolean z2) {
                if (ChatAttachAlert.this.photoLayout != null) {
                    ChatAttachAlert.this.photoLayout.checkCameraViewPosition();
                }
            }
        };
        setupMentionContainer();
        this.mentionContainer.withDelegate(new MentionsContainerView.Delegate() {
            AnonymousClass26() {
            }

            @Override
            public void addEmojiToRecent(String str) {
                MentionsContainerView.Delegate.CC.$default$addEmojiToRecent(this, str);
            }

            @Override
            public Paint.FontMetricsInt getFontMetrics() {
                return ChatAttachAlert.this.commentTextView.getEditText().getPaint().getFontMetricsInt();
            }

            @Override
            public void onStickerSelected(TLRPC.TL_document tL_document, String str, Object obj) {
                MentionsContainerView.Delegate.CC.$default$onStickerSelected(this, tL_document, str, obj);
            }

            @Override
            public void replaceText(int i, int i2, CharSequence charSequence, boolean z) {
                ChatAttachAlert.this.replaceWithText(i, i2, charSequence, z);
            }

            @Override
            public void sendBotInlineResult(TLRPC.BotInlineResult botInlineResult, boolean z, int i) {
                MentionsContainerView.Delegate.CC.$default$sendBotInlineResult(this, botInlineResult, z, i);
            }
        });
        ViewGroup viewGroup = this.containerView;
        viewGroup.addView(this.mentionContainer, viewGroup.indexOfChild(this.frameLayout2), LayoutHelper.createFrame(-1, -1, 83));
        this.mentionContainer.setTranslationY(-this.commentTextView.getHeight());
        setupMentionContainer();
    }

    public int getScrollOffsetY(int i) {
        AttachAlertLayout attachAlertLayout = this.nextAttachLayout;
        if (attachAlertLayout == null || !((this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (attachAlertLayout instanceof ChatAttachAlertPhotoLayoutPreview))) {
            return this.scrollOffsetY[i];
        }
        int[] iArr = this.scrollOffsetY;
        return AndroidUtilities.lerp(iArr[0], iArr[1], this.translationProgress);
    }

    public boolean isCaptionAbove() {
        AttachAlertLayout attachAlertLayout;
        return this.captionAbove && ((attachAlertLayout = this.currentAttachLayout) == this.photoLayout || attachAlertLayout == this.photoPreviewLayout);
    }

    private boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground)) > 0.699999988079071d;
    }

    public void lambda$dismiss$44(DialogInterface dialogInterface, int i) {
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    public void lambda$dismiss$45(DialogInterface dialogInterface) {
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(1.5f);
        this.appearSpringAnimation.getSpring().setStiffness(1500.0f);
        this.appearSpringAnimation.start();
    }

    public void lambda$dismiss$46(DialogInterface dialogInterface) {
        this.confirmationAlertShown = false;
    }

    public void lambda$dismiss$47(int i) {
        this.navBarColorKey = -1;
        this.navBarColor = i;
        this.containerView.invalidate();
    }

    public void lambda$init$43(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        ((ChatActivity) this.baseFragment).didSelectLocation(messageMedia, i, z, i2);
    }

    public void lambda$makeFocusable$42(final EditTextBoldCursor editTextBoldCursor, boolean z) {
        setFocusable(true);
        editTextBoldCursor.requestFocus();
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                }
            });
        }
    }

    public void lambda$new$0(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    public void lambda$new$1(View view) {
        this.selectedMenuItem.toggleSubMenu();
    }

    public void lambda$new$10(final AttachBotButton attachBotButton, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlert.this.lambda$new$9(attachBotButton);
            }
        });
    }

    public void lambda$new$11(final AttachBotButton attachBotButton, Boolean bool) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(attachBotButton.attachMenuBot.bot_id);
        tL_messages_toggleBotInAttachMenu.enabled = true;
        tL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatAttachAlert.this.lambda$new$10(attachBotButton, tLObject, tL_error);
            }
        }, 66);
    }

    public void lambda$new$12(org.telegram.ui.ActionBar.Theme.ResourcesProvider r12, android.view.View r13, int r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.lambda$new$12(org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.view.View, int):void");
    }

    public boolean lambda$new$13(View view, int i) {
        if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (!this.destroyed && attachBotButton.currentUser != null) {
                onLongClickBotButton(attachBotButton.attachMenuBot, attachBotButton.currentUser);
                return true;
            }
        }
        return false;
    }

    public void lambda$new$14(View view) {
        ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout;
        long j = this.selectedId;
        if (j >= 0 || (chatAttachAlertBotWebViewLayout = (ChatAttachAlertBotWebViewLayout) this.botAttachLayouts.get(-j)) == null) {
            return;
        }
        chatAttachAlertBotWebViewLayout.getWebViewContainer().onMainButtonPressed();
    }

    public static boolean lambda$new$15(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$new$16(boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i, 0L, isCaptionAbove());
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i, 0L, isCaptionAbove());
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    public void lambda$new$17(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view) {
        if (this.currentLimit - this.codepointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView);
            try {
                this.captionLimitView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium <= this.codepointCount) {
                return;
            }
            showCaptionLimitBulletin(baseFragment);
            return;
        }
        if (this.editingMessageObject == null) {
            BaseFragment baseFragment2 = this.baseFragment;
            if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                    @Override
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlert.this.lambda$new$16(z, i);
                    }
                }, resourcesProvider);
                return;
            }
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(true, 0, 0L, isCaptionAbove());
            return;
        }
        attachAlertLayout.sendSelectedItems(true, 0, 0L, isCaptionAbove());
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    public void lambda$new$18(long j, boolean z, int i) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i, j, isCaptionAbove());
            return;
        }
        attachAlertLayout.sendSelectedItems(z, i, j, isCaptionAbove());
        this.allowPassConfirmationAlert = true;
        dismiss();
    }

    public void lambda$new$19(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view) {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        final long selectedEffect = messageSendPreview != null ? messageSendPreview.getSelectedEffect() : 0L;
        forceKeyboardOnDismiss();
        MessageSendPreview messageSendPreview2 = this.messageSendPreview;
        if (messageSendPreview2 != null) {
            messageSendPreview2.dismiss(true);
            this.messageSendPreview = null;
        }
        if (this.currentLimit - this.codepointCount < 0) {
            AndroidUtilities.shakeView(this.captionLimitView);
            try {
                this.captionLimitView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium <= this.codepointCount) {
                return;
            }
            showCaptionLimitBulletin(baseFragment);
            return;
        }
        if (this.editingMessageObject == null) {
            BaseFragment baseFragment2 = this.baseFragment;
            if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                    @Override
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlert.this.lambda$new$18(selectedEffect, z, i);
                    }
                }, resourcesProvider);
                this.captionAbove = false;
            }
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(true, 0, selectedEffect, isCaptionAbove());
        } else {
            attachAlertLayout.sendSelectedItems(true, 0, selectedEffect, isCaptionAbove());
            this.allowPassConfirmationAlert = true;
            dismiss();
        }
        this.captionAbove = false;
    }

    public void lambda$new$2(View view) {
        this.currentAttachLayout.onMenuItemClick(40);
    }

    public void lambda$new$20(MessageObject messageObject, MessagePreviewView.ToggleButton toggleButton, View view) {
        MessagePreviewView.ToggleButton toggleButton2;
        boolean z = this.captionAbove;
        boolean z2 = !z;
        this.captionAbove = z2;
        messageObject.messageOwner.invert_media = z2;
        toggleButton.setState(z, true);
        this.messageSendPreview.changeMessage(messageObject);
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null && (toggleButton2 = chatAttachAlertPhotoLayout.captionItem) != null) {
            toggleButton2.setState(!this.captionAbove, true);
        }
        this.messageSendPreview.scrollTo(!this.captionAbove);
    }

    public void lambda$new$21(boolean z, int i) {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        long selectedEffect = messageSendPreview != null ? messageSendPreview.getSelectedEffect() : 0L;
        MessageSendPreview messageSendPreview2 = this.messageSendPreview;
        if (messageSendPreview2 != null) {
            messageSendPreview2.dismiss(true);
            this.messageSendPreview = null;
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i, selectedEffect, isCaptionAbove());
        } else {
            attachAlertLayout.sendSelectedItems(z, i, selectedEffect, isCaptionAbove());
            dismiss();
        }
    }

    public void lambda$new$22(long j, Theme.ResourcesProvider resourcesProvider) {
        AlertsCreator.createScheduleDatePickerDialog(getContext(), j, new AlertsCreator.ScheduleDatePickerDelegate() {
            @Override
            public final void didSelectDate(boolean z, int i) {
                ChatAttachAlert.this.lambda$new$21(z, i);
            }
        }, resourcesProvider);
    }

    public void lambda$new$23() {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        long selectedEffect = messageSendPreview != null ? messageSendPreview.getSelectedEffect() : 0L;
        MessageSendPreview messageSendPreview2 = this.messageSendPreview;
        if (messageSendPreview2 != null) {
            messageSendPreview2.dismiss(true);
            this.messageSendPreview = null;
        }
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(false, 0, selectedEffect, isCaptionAbove());
        } else {
            attachAlertLayout.sendSelectedItems(false, 0, selectedEffect, isCaptionAbove());
            dismiss();
        }
    }

    public void lambda$new$24(ActionBarMenuSubItem actionBarMenuSubItem, Long l, Runnable runnable) {
        runnable.run();
        this.photoLayout.setStarsPrice(l.longValue());
        if (l.longValue() > 0) {
            actionBarMenuSubItem.setText(LocaleController.getString(R.string.PaidMediaPriceButton));
            actionBarMenuSubItem.setSubtext(LocaleController.formatPluralString("Stars", (int) l.longValue(), new Object[0]));
            this.messageSendPreview.setStars(l.longValue());
        } else {
            actionBarMenuSubItem.setText(LocaleController.getString(R.string.PaidMediaButton));
            actionBarMenuSubItem.setSubtext(null);
            this.messageSendPreview.setStars(0L);
        }
    }

    public void lambda$new$25(Context context, final ActionBarMenuSubItem actionBarMenuSubItem, Theme.ResourcesProvider resourcesProvider, View view) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout == null) {
            return;
        }
        StarsIntroActivity.showMediaPriceSheet(context, chatAttachAlertPhotoLayout.getStarsPrice(), true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ChatAttachAlert.this.lambda$new$24(actionBarMenuSubItem, (Long) obj, (Runnable) obj2);
            }
        }, resourcesProvider);
    }

    public boolean lambda$new$26(final android.content.Context r31, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r32, final org.telegram.ui.ActionBar.BaseFragment r33, android.view.View r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.lambda$new$26(android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider, org.telegram.ui.ActionBar.BaseFragment, android.view.View):boolean");
    }

    public void lambda$new$3(boolean z, View view) {
        if (this.avatarPicker != 0) {
            this.delegate.openAvatarsSearch();
            dismiss();
            return;
        }
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        PhotoPickerSearchActivity photoPickerSearchActivity = new PhotoPickerSearchActivity(hashMap, arrayList, 0, true, (ChatActivity) this.baseFragment);
        photoPickerSearchActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            private boolean sendPressed;
            final ArrayList val$order;
            final HashMap val$photos;

            AnonymousClass7(HashMap hashMap2, ArrayList arrayList2) {
                r2 = hashMap2;
                r3 = arrayList2;
            }

            @Override
            public void actionButtonPressed(boolean z2, boolean z22, int i) {
                if (z2 || r2.isEmpty() || this.sendPressed) {
                    return;
                }
                this.sendPressed = true;
                ArrayList arrayList2 = new ArrayList();
                for (int i2 = 0; i2 < r3.size(); i2++) {
                    Object obj = r2.get(r3.get(i2));
                    SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                    arrayList2.add(sendingMediaInfo);
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                    String str = searchImage.imagePath;
                    if (str != null) {
                        sendingMediaInfo.path = str;
                    } else {
                        sendingMediaInfo.searchImage = searchImage;
                    }
                    sendingMediaInfo.thumbPath = searchImage.thumbPath;
                    sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                    CharSequence charSequence = searchImage.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = searchImage.entities;
                    sendingMediaInfo.masks = searchImage.stickers;
                    sendingMediaInfo.ttl = searchImage.ttl;
                    TLRPC.BotInlineResult botInlineResult = searchImage.inlineResult;
                    if (botInlineResult != null && searchImage.type == 1) {
                        sendingMediaInfo.inlineResult = botInlineResult;
                        sendingMediaInfo.params = searchImage.params;
                    }
                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                }
                ((ChatActivity) ChatAttachAlert.this.baseFragment).didSelectSearchPhotos(arrayList2, z22, i);
            }

            @Override
            public boolean canFinishFragment() {
                return PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$canFinishFragment(this);
            }

            @Override
            public void onCaptionChanged(CharSequence charSequence) {
            }

            @Override
            public void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            @Override
            public void selectedPhotosChanged() {
            }
        });
        photoPickerSearchActivity.setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        if (z) {
            this.baseFragment.showAsSheet(photoPickerSearchActivity);
        } else {
            this.baseFragment.presentFragment(photoPickerSearchActivity);
        }
        dismiss();
    }

    public void lambda$new$4(Theme.ResourcesProvider resourcesProvider, View view) {
        this.optionsItem.toggleSubMenu();
        PhotoViewer.getInstance().setParentActivity(this.baseFragment, resourcesProvider);
        PhotoViewer.getInstance().setParentAlert(this);
        PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        if (!this.delegate.needEnterComment()) {
            AndroidUtilities.hideKeyboard(this.baseFragment.getFragmentView().findFocus());
            AndroidUtilities.hideKeyboard(getContainer().findFocus());
        }
        File makeCacheFile = StoryEntry.makeCacheFile(this.currentAccount, "webp");
        android.graphics.Point point = AndroidUtilities.displaySize;
        int i = point.x;
        int i2 = point.y;
        if (i > 1080 || i2 > 1080) {
            float min = Math.min(i, i2) / 1080.0f;
            i = (int) (i * min);
            i2 = (int) (i2 * min);
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        try {
            createBitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(makeCacheFile));
        } catch (Throwable th) {
            FileLog.e(th);
        }
        createBitmap.recycle();
        ArrayList arrayList = new ArrayList();
        MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0L, makeCacheFile.getAbsolutePath(), 0, false, 0, 0, 0L);
        arrayList.add(photoEntry);
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        AnonymousClass8 anonymousClass8 = new PhotoViewer.EmptyPhotoViewerProvider() {
            final MediaController.PhotoEntry val$entry;

            AnonymousClass8(MediaController.PhotoEntry photoEntry2) {
                r2 = photoEntry2;
            }

            @Override
            public boolean allowCaption() {
                return false;
            }

            @Override
            public void sendButtonPressed(int i3, VideoEditedInfo videoEditedInfo, boolean z, int i22, boolean z2) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.sent = true;
                if (chatAttachAlert.delegate == null) {
                    return;
                }
                r2.editedInfo = videoEditedInfo;
                ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
                ChatAttachAlertPhotoLayout.selectedPhotos.clear();
                ChatAttachAlertPhotoLayout.selectedPhotosOrder.add(0);
                ChatAttachAlertPhotoLayout.selectedPhotos.put(0, r2);
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                chatAttachAlert2.delegate.didPressedButton(7, true, z, i22, 0L, chatAttachAlert2.isCaptionAbove(), z2);
            }
        };
        BaseFragment baseFragment = this.baseFragment;
        photoViewer.openPhotoForSelect(arrayList, 0, 11, false, anonymousClass8, baseFragment instanceof ChatActivity ? (ChatActivity) baseFragment : null);
        if (this.isStickerMode) {
            PhotoViewer.getInstance().enableStickerMode(null, true, this.customStickerHandler);
        }
    }

    public void lambda$new$5(View view) {
        this.optionsItem.toggleSubMenu();
    }

    public void lambda$new$6(View view) {
        updatePhotoPreview(this.currentAttachLayout != this.photoPreviewLayout);
    }

    public void lambda$new$7(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        ((ChatActivity) this.baseFragment).didSelectLocation(messageMedia, i, z, i2);
    }

    public void lambda$new$8(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        ((ChatActivity) this.baseFragment).sendPoll(tL_messageMediaPoll, hashMap, z, i);
    }

    public void lambda$new$9(AttachBotButton attachBotButton) {
        TLRPC.TL_attachMenuBot tL_attachMenuBot = attachBotButton.attachMenuBot;
        attachBotButton.attachMenuBot.side_menu_disclaimer_needed = false;
        tL_attachMenuBot.inactive = false;
        showBotLayout(attachBotButton.attachMenuBot.bot_id, true);
        MediaDataController.getInstance(this.currentAccount).updateAttachMenuBotsInCache();
    }

    public void lambda$onCustomOpenAnimation$37(ValueAnimator valueAnimator) {
        this.navigationBarAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        BottomSheet.ContainerView containerView = this.container;
        if (containerView != null) {
            containerView.invalidate();
        }
    }

    public void lambda$onCustomOpenAnimation$38(AnimationNotificationsLocker animationNotificationsLocker, BottomSheet.BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.currentSheetAnimation = null;
        this.appearSpringAnimation = null;
        animationNotificationsLocker.unlock();
        this.currentSheetAnimationType = 0;
        if (bottomSheetDelegateInterface != null) {
            bottomSheetDelegateInterface.onOpenAnimationEnd();
        }
        if (this.useHardwareLayer) {
            this.container.setLayerType(0, null);
        }
        if (this.isFullscreen) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags &= -1025;
            getWindow().setAttributes(attributes);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 512);
    }

    public void lambda$onCustomOpenAnimation$39(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet == null || animatorSet.isRunning()) {
            return;
        }
        runnable.run();
    }

    public void lambda$onCustomOpenAnimation$40(ValueAnimator valueAnimator) {
        setNavBarAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$onLongClickBotButton$28(TLRPC.TL_attachMenuBot tL_attachMenuBot) {
        MediaDataController.getInstance(this.currentAccount).loadAttachMenuBots(false, true);
        if (this.currentAttachLayout == this.botAttachLayouts.get(tL_attachMenuBot.bot_id)) {
            showLayout(this.photoLayout);
        }
    }

    public void lambda$onLongClickBotButton$29(final TLRPC.TL_attachMenuBot tL_attachMenuBot, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlert.this.lambda$onLongClickBotButton$28(tL_attachMenuBot);
            }
        });
    }

    public void lambda$onLongClickBotButton$30(final TLRPC.TL_attachMenuBot tL_attachMenuBot, TLRPC.User user, DialogInterface dialogInterface, int i) {
        if (tL_attachMenuBot == null) {
            MediaDataController.getInstance(this.currentAccount).removeInline(user.id);
            return;
        }
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
        tL_messages_toggleBotInAttachMenu.enabled = false;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatAttachAlert.this.lambda$onLongClickBotButton$29(tL_attachMenuBot, tLObject, tL_error);
            }
        }, 66);
    }

    public void lambda$openAudioLayout$35(ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2) {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment != null && (baseFragment instanceof ChatActivity)) {
            ((ChatActivity) baseFragment).sendAudio(arrayList, charSequence, z, i, j, z2);
            return;
        }
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.sendAudio(arrayList, charSequence, z, i, j, z2);
        }
    }

    public void lambda$openColorsLayout$36(Object obj) {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.onWallpaperSelected(obj);
        }
    }

    public void lambda$showCaptionLimitBulletin$27(BaseFragment baseFragment) {
        dismiss(true);
        if (baseFragment != null) {
            baseFragment.presentFragment(new PremiumPreviewFragment("caption_limit"));
        }
    }

    public void lambda$showLayout$31() {
        AttachAlertLayout attachAlertLayout;
        ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview;
        if (Build.VERSION.SDK_INT >= 20) {
            this.container.setLayerType(0, null);
        }
        this.viewChangeAnimator = null;
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        if (attachAlertLayout2 != this.photoLayout && (attachAlertLayout = this.nextAttachLayout) != (chatAttachAlertPhotoLayoutPreview = this.photoPreviewLayout) && attachAlertLayout2 != attachAlertLayout && attachAlertLayout2 != chatAttachAlertPhotoLayoutPreview) {
            this.containerView.removeView(attachAlertLayout2);
        }
        this.currentAttachLayout.setVisibility(8);
        this.currentAttachLayout.onHidden();
        this.nextAttachLayout.onShown();
        this.currentAttachLayout = this.nextAttachLayout;
        this.nextAttachLayout = null;
        int[] iArr = this.scrollOffsetY;
        iArr[0] = iArr[1];
    }

    public void lambda$showLayout$32(float f, float f2, boolean z, DynamicAnimation dynamicAnimation, float f3, float f4) {
        float f5 = f3 / 500.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION.set(this.currentAttachLayout, Float.valueOf(f5));
        this.actionBar.setAlpha(AndroidUtilities.lerp(f, f2, f5));
        updateLayout(this.currentAttachLayout, false, 0);
        updateLayout(this.nextAttachLayout, false, 0);
        if (!(this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || z) {
            f5 = 1.0f - f5;
        }
        float clamp = Utilities.clamp(f5, 1.0f, 0.0f);
        this.mediaPreviewView.setAlpha(clamp);
        float f6 = 1.0f - clamp;
        this.selectedView.setAlpha(f6);
        this.selectedView.setTranslationX(clamp * (-AndroidUtilities.dp(16.0f)));
        this.mediaPreviewView.setTranslationX(f6 * AndroidUtilities.dp(16.0f));
    }

    public void lambda$showLayout$33(boolean z, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        this.currentAttachLayout.onHideShowProgress(1.0f);
        this.nextAttachLayout.onHideShowProgress(1.0f);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.nextAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        this.containerView.invalidate();
        this.actionBar.setTag(z ? 1 : null);
        runnable.run();
    }

    public void lambda$showLayout$34(AttachAlertLayout attachAlertLayout, final Runnable runnable) {
        final boolean z = this.nextAttachLayout.getCurrentItemTop() <= attachAlertLayout.getButtonsHideOffset();
        final float alpha = this.actionBar.getAlpha();
        final float f = z ? 1.0f : 0.0f;
        SpringAnimation springAnimation = new SpringAnimation(new FloatValueHolder(0.0f));
        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f2, float f3) {
                ChatAttachAlert.this.lambda$showLayout$32(alpha, f, z, dynamicAnimation, f2, f3);
            }
        });
        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f2, float f3) {
                ChatAttachAlert.this.lambda$showLayout$33(z, runnable, dynamicAnimation, z2, f2, f3);
            }
        });
        springAnimation.setSpring(new SpringForce(500.0f));
        springAnimation.getSpring().setDampingRatio(1.0f);
        springAnimation.getSpring().setStiffness(1000.0f);
        springAnimation.start();
        this.viewChangeAnimator = springAnimation;
    }

    public void openAudioLayout(boolean z) {
        if (!this.musicEnabled && z) {
            ChatAttachRestrictedLayout chatAttachRestrictedLayout = new ChatAttachRestrictedLayout(3, this, getContext(), this.resourcesProvider);
            this.restrictedLayout = chatAttachRestrictedLayout;
            showLayout(chatAttachRestrictedLayout);
        }
        if (this.audioLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertAudioLayout chatAttachAlertAudioLayout = new ChatAttachAlertAudioLayout(this, getContext(), this.resourcesProvider);
            this.audioLayout = chatAttachAlertAudioLayout;
            attachAlertLayoutArr[3] = chatAttachAlertAudioLayout;
            chatAttachAlertAudioLayout.setDelegate(new ChatAttachAlertAudioLayout.AudioSelectDelegate() {
                @Override
                public final void didSelectAudio(ArrayList arrayList, CharSequence charSequence, boolean z2, int i, long j, boolean z3) {
                    ChatAttachAlert.this.lambda$openAudioLayout$35(arrayList, charSequence, z2, i, j, z3);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            TLRPC.Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
            this.audioLayout.setMaxSelectedFiles(((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) ? -1 : 1);
        }
        if (z) {
            showLayout(this.audioLayout);
        }
    }

    private void openContactsLayout() {
        if (!this.plainTextEnabled) {
            ChatAttachRestrictedLayout chatAttachRestrictedLayout = new ChatAttachRestrictedLayout(5, this, getContext(), this.resourcesProvider);
            this.restrictedLayout = chatAttachRestrictedLayout;
            showLayout(chatAttachRestrictedLayout);
        }
        if (this.contactsLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = new ChatAttachAlertContactsLayout(this, getContext(), this.resourcesProvider);
            this.contactsLayout = chatAttachAlertContactsLayout;
            attachAlertLayoutArr[2] = chatAttachAlertContactsLayout;
            chatAttachAlertContactsLayout.setDelegate(new ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate() {
                AnonymousClass18() {
                }

                @Override
                public void didSelectContact(TLRPC.User user, boolean z, int i, long j, boolean z2) {
                    ((ChatActivity) ChatAttachAlert.this.baseFragment).sendContact(user, z, i, j, z2);
                }

                @Override
                public void didSelectContacts(ArrayList arrayList, String str, boolean z, int i, long j, boolean z2) {
                    ((ChatActivity) ChatAttachAlert.this.baseFragment).sendContacts(arrayList, str, z, i, j, z2);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            TLRPC.Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
            this.contactsLayout.setMultipleSelectionAllowed(currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled);
        }
        showLayout(this.contactsLayout);
    }

    private void openDocumentsLayout(boolean z) {
        if (!this.documentsEnabled && z) {
            ChatAttachRestrictedLayout chatAttachRestrictedLayout = new ChatAttachRestrictedLayout(4, this, getContext(), this.resourcesProvider);
            this.restrictedLayout = chatAttachRestrictedLayout;
            showLayout(chatAttachRestrictedLayout);
        }
        boolean z2 = false;
        if (this.documentLayout == null) {
            int i = this.isSoundPicker ? 2 : 0;
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = new ChatAttachAlertDocumentLayout(this, getContext(), i, this.resourcesProvider);
            this.documentLayout = chatAttachAlertDocumentLayout;
            attachAlertLayoutArr[4] = chatAttachAlertDocumentLayout;
            chatAttachAlertDocumentLayout.setDelegate(new ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate() {
                AnonymousClass19() {
                }

                @Override
                public void didSelectFiles(ArrayList arrayList, String str, ArrayList arrayList2, boolean z3, int i2, long j, boolean z22) {
                    if (ChatAttachAlert.this.documentsDelegate != null) {
                        ChatAttachAlert.this.documentsDelegate.didSelectFiles(arrayList, str, arrayList2, z3, i2, j, z22);
                        return;
                    }
                    Object obj = ChatAttachAlert.this.baseFragment;
                    if (obj instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) {
                        ((ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) obj).didSelectFiles(arrayList, str, arrayList2, z3, i2, j, z22);
                    } else if (obj instanceof PassportActivity) {
                        ((PassportActivity) obj).didSelectFiles(arrayList, str, z3, i2, j, z22);
                    }
                }

                @Override
                public void didSelectPhotos(ArrayList arrayList, boolean z3, int i2) {
                    if (ChatAttachAlert.this.documentsDelegate != null) {
                        ChatAttachAlert.this.documentsDelegate.didSelectPhotos(arrayList, z3, i2);
                        return;
                    }
                    BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).didSelectPhotos(arrayList, z3, i2);
                    } else if (baseFragment instanceof PassportActivity) {
                        ((PassportActivity) baseFragment).didSelectPhotos(arrayList, z3, i2);
                    }
                }

                @Override
                public void startDocumentSelectActivity() {
                    ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate documentSelectActivityDelegate;
                    if (ChatAttachAlert.this.documentsDelegate != null) {
                        documentSelectActivityDelegate = ChatAttachAlert.this.documentsDelegate;
                    } else {
                        Object obj = ChatAttachAlert.this.baseFragment;
                        if (!(obj instanceof ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate)) {
                            if (obj instanceof PassportActivity) {
                                ((PassportActivity) obj).startDocumentSelectActivity();
                                return;
                            }
                            return;
                        }
                        documentSelectActivityDelegate = (ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate) obj;
                    }
                    documentSelectActivityDelegate.startDocumentSelectActivity();
                }

                @Override
                public void startMusicSelectActivity() {
                    ChatAttachAlert.this.openAudioLayout(true);
                }
            });
        }
        BaseFragment baseFragment = this.baseFragment;
        int i2 = 1;
        if (baseFragment instanceof ChatActivity) {
            TLRPC.Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout2 = this.documentLayout;
            if ((currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) && this.editingMessageObject == null) {
                i2 = -1;
            }
            chatAttachAlertDocumentLayout2.setMaxSelectedFiles(i2);
        } else {
            this.documentLayout.setMaxSelectedFiles(this.maxSelectedPhotos);
            ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout3 = this.documentLayout;
            if (!this.isSoundPicker && !this.allowEnterCaption) {
                z2 = true;
            }
            chatAttachAlertDocumentLayout3.setCanSelectOnlyImageFiles(z2);
        }
        ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout4 = this.documentLayout;
        chatAttachAlertDocumentLayout4.isSoundPicker = this.isSoundPicker;
        if (z) {
            showLayout(chatAttachAlertDocumentLayout4);
        }
    }

    private void openQuickRepliesLayout() {
        if (this.quickRepliesLayout == null) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            ChatAttachAlertQuickRepliesLayout chatAttachAlertQuickRepliesLayout = new ChatAttachAlertQuickRepliesLayout(this, getContext(), this.resourcesProvider);
            this.quickRepliesLayout = chatAttachAlertQuickRepliesLayout;
            attachAlertLayoutArr[7] = chatAttachAlertQuickRepliesLayout;
        }
        showLayout(this.quickRepliesLayout);
    }

    public void removeFromRoot() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
        }
        if (this.actionBar.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        }
        this.contactsLayout = null;
        this.quickRepliesLayout = null;
        this.audioLayout = null;
        this.pollLayout = null;
        this.locationLayout = null;
        this.documentLayout = null;
        int i = 1;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                updateActionBarVisibility(false, false);
                super.dismissInternal();
                return;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null) {
                attachAlertLayout.onDestroy();
                this.containerView.removeView(this.layouts[i]);
                this.layouts[i] = null;
            }
            i++;
        }
    }

    public void replaceWithText(int i, int i2, CharSequence charSequence, boolean z) {
        if (this.commentTextView == null) {
            return;
        }
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.commentTextView.getText());
            spannableStringBuilder.replace(i, i2 + i, charSequence);
            if (z) {
                Emoji.replaceEmoji((CharSequence) spannableStringBuilder, this.commentTextView.getEditText().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.commentTextView.setText(spannableStringBuilder);
            this.commentTextView.setSelection(i + charSequence.length());
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void sendPressed(boolean z, int i, long j, boolean z2) {
        if (this.buttonPressed) {
            return;
        }
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            TLRPC.Chat currentChat = chatActivity.getCurrentChat();
            if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("silent_" + chatActivity.getDialogId(), !z).commit();
            }
        }
        if (checkCaption(this.commentTextView.getText())) {
            return;
        }
        applyCaption();
        this.buttonPressed = true;
        this.delegate.didPressedButton(7, true, z, i, j, z2, false);
    }

    private void setNavBarAlpha(float f) {
        this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundGray), Math.min(255, Math.max(0, (int) (f * 255.0f))));
        AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
        AndroidUtilities.setLightNavigationBar(getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(this.navBarColor)) > 0.721d);
        getContainer().invalidate();
    }

    private void showCaptionLimitBulletin(final BaseFragment baseFragment) {
        if ((baseFragment instanceof ChatActivity) && ChatObject.isChannelAndNotMegaGroup(((ChatActivity) baseFragment).getCurrentChat())) {
            BulletinFactory.of(this.sizeNotifierFrameLayout, this.resourcesProvider).createCaptionLimitBulletin(MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium, new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlert.this.lambda$showCaptionLimitBulletin$27(baseFragment);
                }
            }).show();
        }
    }

    private boolean showCommentTextView(boolean r18, boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.showCommentTextView(boolean, boolean):boolean");
    }

    private void showLayout(AttachAlertLayout attachAlertLayout, long j) {
        showLayout(attachAlertLayout, j, true);
    }

    private void showLayout(final org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout r12, long r13, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.showLayout(org.telegram.ui.Components.ChatAttachAlert$AttachAlertLayout, long, boolean):void");
    }

    private void updateActionBarVisibility(boolean z, boolean z2) {
        AttachAlertLayout attachAlertLayout;
        if (!(z && this.actionBar.getTag() == null) && (z || this.actionBar.getTag() == null)) {
            return;
        }
        this.actionBar.setTag(z ? 1 : null);
        AnimatorSet animatorSet = this.actionBarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.actionBarAnimation = null;
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        boolean z3 = actionBarMenuItem != null && this.avatarSearch;
        boolean z4 = (this.isPhotoPicker || this.storyMediaPicker || (this.avatarPicker == 0 && this.menuShowed) || this.currentAttachLayout != this.photoLayout || (!this.photosEnabled && !this.videosEnabled)) ? false : true;
        if (this.currentAttachLayout == this.restrictedLayout) {
            z3 = false;
            z4 = false;
        }
        if (z) {
            if (z3) {
                actionBarMenuItem.setVisibility(0);
            }
            if (z4) {
                this.selectedMenuItem.setVisibility(0);
                this.selectedMenuItem.setClickable(true);
            }
        } else if (this.typeButtonsAvailable && this.frameLayout2.getTag() == null) {
            this.buttonsRecyclerView.setVisibility(0);
        }
        if (getWindow() != null && this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), z ? isLightStatusBar() : this.baseFragment.isLightStatusBar());
        }
        if (z2) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(Math.abs((z ? 1.0f : 0.0f) - this.actionBar.getAlpha()) * 180.0f);
            ArrayList arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, (Property<ActionBar, Float>) property, z ? 1.0f : 0.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.actionBarShadow, (Property<View, Float>) property, z ? 1.0f : 0.0f));
            if (z3) {
                arrayList.add(ObjectAnimator.ofFloat(this.searchItem, (Property<ActionBarMenuItem, Float>) property, z ? 1.0f : 0.0f));
            }
            if (z4) {
                arrayList.add(ObjectAnimator.ofFloat(this.selectedMenuItem, (Property<ActionBarMenuItem, Float>) property, z ? 1.0f : 0.0f));
            }
            this.actionBarAnimation.playTogether(arrayList);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                final boolean val$show;

                AnonymousClass23(boolean z5) {
                    r2 = z5;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    ChatAttachAlert.this.actionBarAnimation = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.actionBarAnimation != null) {
                        if (r2) {
                            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                            if (chatAttachAlert.typeButtonsAvailable) {
                                if (chatAttachAlert.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                                    ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        ActionBarMenuItem actionBarMenuItem2 = ChatAttachAlert.this.searchItem;
                        if (actionBarMenuItem2 != null) {
                            actionBarMenuItem2.setVisibility(4);
                        }
                        ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                        if (chatAttachAlert2.avatarPicker == 0 && chatAttachAlert2.menuShowed) {
                            return;
                        }
                        ChatAttachAlert.this.selectedMenuItem.setVisibility(4);
                    }
                }
            });
            this.actionBarAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.actionBarAnimation.setDuration(380L);
            this.actionBarAnimation.start();
            return;
        }
        if (z5 && this.typeButtonsAvailable && ((attachAlertLayout = this.currentAttachLayout) == null || attachAlertLayout.shouldHideBottomButtons())) {
            this.buttonsRecyclerView.setVisibility(4);
        }
        this.actionBar.setAlpha(z5 ? 1.0f : 0.0f);
        this.actionBarShadow.setAlpha(z5 ? 1.0f : 0.0f);
        if (z3) {
            this.searchItem.setAlpha(z5 ? 1.0f : 0.0f);
        }
        if (z4) {
            this.selectedMenuItem.setAlpha(z5 ? 1.0f : 0.0f);
            this.selectedMenuItem.setScaleX(z5 ? 1.0f : 0.6f);
            this.selectedMenuItem.setScaleY(z5 ? 1.0f : 0.6f);
        }
        if (z5) {
            return;
        }
        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setVisibility(4);
        }
        if (this.avatarPicker == 0 && this.menuShowed) {
            return;
        }
        this.selectedMenuItem.setVisibility(4);
    }

    public void updateSelectedPosition(int r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateSelectedPosition(int):void");
    }

    public void applyCaption() {
        if (this.commentTextView.length() <= 0) {
            return;
        }
        this.currentAttachLayout.applyCaption(this.commentTextView.getText());
    }

    public void avatarFor(ImageUpdater.AvatarFor avatarFor) {
        this.setAvatarFor = avatarFor;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    protected boolean canDismissWithTouchOutside() {
        return this.currentAttachLayout.canDismissWithTouchOutside();
    }

    @Override
    protected void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            SpringAnimation springAnimation = this.appearSpringAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            AnimatorSet animatorSet2 = this.buttonsAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    public boolean checkCanRemoveRestrictionsByBoosts() {
        BaseFragment baseFragment = this.baseFragment;
        return (baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).checkCanRemoveRestrictionsByBoosts();
    }

    public boolean checkCaption(CharSequence charSequence) {
        BaseFragment baseFragment = this.baseFragment;
        if (!(baseFragment instanceof ChatActivity)) {
            return false;
        }
        return ChatActivityEnterView.checkPremiumAnimatedEmoji(this.currentAccount, ((ChatActivity) baseFragment).getDialogId(), this.baseFragment, this.sizeNotifierFrameLayout, charSequence);
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(i));
        }
        this.selectedTextView.setTextColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack));
        this.mediaPreviewTextView.setTextColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack));
        this.doneItem.getTextView().setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        this.selectedMenuItem.setIconColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack));
        Theme.setDrawableColor(this.selectedMenuItem.getBackground(), getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItemsSelector : Theme.key_dialogButtonSelector));
        ActionBarMenuItem actionBarMenuItem = this.selectedMenuItem;
        int i2 = Theme.key_actionBarDefaultSubmenuItem;
        actionBarMenuItem.setPopupItemsColor(getThemedColor(i2), false);
        this.selectedMenuItem.setPopupItemsColor(getThemedColor(i2), true);
        this.selectedMenuItem.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setIconColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack));
            Theme.setDrawableColor(this.searchItem.getBackground(), getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItemsSelector : Theme.key_dialogButtonSelector));
        }
        this.commentTextView.updateColors();
        this.actionBarShadow.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.buttonsRecyclerView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.buttonsRecyclerView.setBackgroundColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground));
        this.frameLayout2.setBackgroundColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground));
        this.actionBar.setBackgroundColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBar : Theme.key_dialogBackground));
        this.actionBar.setItemsColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItemsSelector : Theme.key_dialogButtonSelector), false);
        this.actionBar.setTitleColor(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarItems : Theme.key_dialogTextBlack));
        Theme.setDrawableColor(this.shadowDrawable, getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_listViewBackground : Theme.key_dialogBackground));
        this.containerView.invalidate();
        int i3 = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i3 >= attachAlertLayoutArr.length) {
                break;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i3];
            if (attachAlertLayout != null) {
                attachAlertLayout.checkColors();
            }
            i3++;
        }
        if (Build.VERSION.SDK_INT < 30) {
            fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
            return;
        }
        this.navBarColorKey = -1;
        this.navBarColor = getThemedColor(Theme.key_dialogBackgroundGray);
        AndroidUtilities.setNavigationBarColor(getWindow(), getThemedColor(Theme.key_dialogBackground), false);
        AndroidUtilities.setLightNavigationBar(getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(this.navBarColor)) > 0.721d);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.reloadInlineHints && i != NotificationCenter.attachMenuBotsDidLoad && i != NotificationCenter.quickRepliesUpdated) {
            if (i == NotificationCenter.currentUserPremiumStatusChanged) {
                this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
            }
        } else {
            ButtonsAdapter buttonsAdapter = this.buttonsAdapter;
            if (buttonsAdapter != null) {
                buttonsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void dismiss() {
        if (this.currentAttachLayout.onDismiss() || isDismissed()) {
            return;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        this.botAttachLayouts.clear();
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment == null) {
            baseFragment = LaunchActivity.getLastFragment();
        }
        if (!this.allowPassConfirmationAlert && baseFragment != null && this.currentAttachLayout.getSelectedItemsCount() > 0 && !this.isPhotoPicker) {
            if (this.confirmationAlertShown) {
                return;
            }
            this.confirmationAlertShown = true;
            AlertDialog create = new AlertDialog.Builder(baseFragment.getParentActivity(), this.resourcesProvider).setTitle(LocaleController.getString("DiscardSelectionAlertTitle", R.string.DiscardSelectionAlertTitle)).setMessage(LocaleController.getString("DiscardSelectionAlertMessage", R.string.DiscardSelectionAlertMessage)).setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatAttachAlert.this.lambda$dismiss$44(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatAttachAlert.this.lambda$dismiss$45(dialogInterface);
                }
            }).setOnPreDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    ChatAttachAlert.this.lambda$dismiss$46(dialogInterface);
                }
            }).create();
            create.show();
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(getThemedColor(Theme.key_text_RedBold));
                return;
            }
            return;
        }
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null && this.currentAttachLayout != attachAlertLayout) {
                attachAlertLayout.onDismiss();
            }
            i++;
        }
        AndroidUtilities.setNavigationBarColor(getWindow(), ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundGray), 0), true, new AndroidUtilities.IntColorCallback() {
            @Override
            public final void run(int i2) {
                ChatAttachAlert.this.lambda$dismiss$47(i2);
            }
        });
        if (baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), baseFragment.isLightStatusBar());
        }
        this.captionLimitBulletinShown = false;
        super.dismiss();
        this.allowPassConfirmationAlert = false;
    }

    @Override
    public void dismiss(boolean z) {
        if (z) {
            this.allowPassConfirmationAlert = z;
        }
        dismiss();
    }

    @Override
    public void dismissInternal() {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate != null) {
            chatAttachViewDelegate.doOnIdle(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlert.this.removeFromRoot();
                }
            });
        } else {
            removeFromRoot();
        }
    }

    @Override
    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        this.currentAttachLayout.onDismissWithButtonClick(i);
    }

    public void enableDefaultMode() {
        this.typeButtonsAvailable = true;
        this.buttonsRecyclerView.setVisibility(0);
        this.shadow.setVisibility(0);
        this.avatarPicker = 0;
        this.isPhotoPicker = false;
        this.isStickerMode = false;
        this.customStickerHandler = null;
        if (this.optionsItem != null) {
            this.selectedTextView.setTranslationY(0.0f);
            this.optionsItem.setVisibility(8);
        }
    }

    public void enableStickerMode(Utilities.Callback2 callback2) {
        this.selectedTextView.setText(LocaleController.getString(R.string.ChoosePhotoForSticker));
        this.typeButtonsAvailable = false;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
        this.avatarPicker = 1;
        this.isPhotoPicker = true;
        this.isStickerMode = true;
        this.customStickerHandler = callback2;
        if (this.optionsItem != null) {
            this.selectedTextView.setTranslationY(-AndroidUtilities.dp(8.0f));
            this.optionsItem.setVisibility(0);
            this.optionsItem.setClickable(true);
            this.optionsItem.setAlpha(1.0f);
            this.optionsItem.setScaleX(1.0f);
            this.optionsItem.setScaleY(1.0f);
        }
    }

    public ImageUpdater.AvatarFor getAvatarFor() {
        return this.setAvatarFor;
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    public TLRPC.Chat getChat() {
        BaseFragment baseFragment = this.baseFragment;
        return baseFragment instanceof ChatActivity ? ((ChatActivity) baseFragment).getCurrentChat() : MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId));
    }

    public float getClipLayoutBottom() {
        return this.frameLayout2.getMeasuredHeight() - ((this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(84.0f)) * (1.0f - this.frameLayout2.getAlpha()));
    }

    public EditTextEmoji getCommentTextView() {
        return this.commentTextView;
    }

    public int getCommentTextViewTop() {
        return this.commentTextViewLocation[1];
    }

    public AttachAlertLayout getCurrentAttachLayout() {
        return this.currentAttachLayout;
    }

    public ChatAttachAlertDocumentLayout getDocumentLayout() {
        return this.documentLayout;
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public ChatAttachAlertPhotoLayout getPhotoLayout() {
        return this.photoLayout;
    }

    public ChatAttachAlertPhotoLayoutPreview getPhotoPreviewLayout() {
        return this.photoPreviewLayout;
    }

    public TextView getSelectedTextView() {
        return this.selectedTextView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions;
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                arrayList.add(new ThemeDescription(this.container, 0, null, null, null, null, Theme.key_dialogBackgroundGray));
                return arrayList;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null && (themeDescriptions = attachAlertLayout.getThemeDescriptions()) != null) {
                arrayList.addAll(themeDescriptions);
            }
            i++;
        }
    }

    public boolean hasCaption() {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout == null) {
            return false;
        }
        HashMap<Object, Object> selectedPhotos = chatAttachAlertPhotoLayout.getSelectedPhotos();
        ArrayList<Object> selectedPhotosOrder = this.photoLayout.getSelectedPhotosOrder();
        if (selectedPhotos.isEmpty()) {
            return false;
        }
        boolean z = false;
        for (int i = 0; i < Math.ceil(selectedPhotos.size() / 10.0f); i++) {
            int i2 = i * 10;
            int min = Math.min(10, selectedPhotos.size() - i2);
            Utilities.random.nextLong();
            for (int i3 = 0; i3 < min; i3++) {
                int i4 = i2 + i3;
                if (i4 < selectedPhotosOrder.size()) {
                    CharSequence charSequence = ((MediaController.PhotoEntry) selectedPhotos.get(selectedPhotosOrder.get(i4))).caption;
                    String charSequence2 = charSequence == null ? "" : charSequence.toString();
                    if (this.commentTextView != null && TextUtils.isEmpty(charSequence2) && i3 == 0) {
                        charSequence2 = this.commentTextView.getText().toString();
                    }
                    if (TextUtils.isEmpty(charSequence2)) {
                        continue;
                    } else {
                        if (z) {
                            return false;
                        }
                        z = true;
                    }
                }
            }
        }
        return z;
    }

    public void init() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.init():void");
    }

    public void makeFocusable(final EditTextBoldCursor editTextBoldCursor, final boolean z) {
        ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
        if (chatAttachViewDelegate == null || this.enterCommentEventSent) {
            return;
        }
        boolean needEnterComment = chatAttachViewDelegate.needEnterComment();
        this.enterCommentEventSent = true;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlert.this.lambda$makeFocusable$42(editTextBoldCursor, z);
            }
        }, needEnterComment ? 200L : 0L);
    }

    public void onActivityResultFragment(int i, Intent intent, String str) {
        this.photoLayout.onActivityResultFragment(i, intent, str);
    }

    @Override
    public void onBackPressed() {
        if (this.passcodeView.getVisibility() == 0) {
            if (getOwnerActivity() != null) {
                getOwnerActivity().finish();
            }
        } else {
            if (this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
                return;
            }
            if (this.currentAttachLayout.onBackPressed()) {
                return;
            }
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
                super.onBackPressed();
            } else {
                this.commentTextView.hidePopup(true);
            }
        }
    }

    @Override
    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.currentAttachLayout.onContainerViewTouchEvent(motionEvent);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.baseFragment != null) {
            AndroidUtilities.setLightStatusBar(getWindow(), this.baseFragment.isLightStatusBar());
        }
    }

    @Override
    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return this.photoLayout.onCustomLayout(view, i, i2, i3, i4);
    }

    @Override
    protected boolean onCustomMeasure(View view, int i, int i2) {
        return this.photoLayout.onCustomMeasure(view, i, i2);
    }

    @Override
    public boolean onCustomOpenAnimation() {
        this.photoLayout.setTranslationX(0.0f);
        this.mediaPreviewView.setAlpha(0.0f);
        this.selectedView.setAlpha(1.0f);
        this.containerView.setTranslationY(this.containerView.getMeasuredHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        this.buttonsAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, (Property<ChatAttachAlert, Float>) this.ATTACH_ALERT_PROGRESS, 0.0f, 400.0f));
        this.buttonsAnimation.setDuration(400L);
        this.buttonsAnimation.setStartDelay(20L);
        this.ATTACH_ALERT_PROGRESS.set(this, Float.valueOf(0.0f));
        this.buttonsAnimation.start();
        ValueAnimator valueAnimator = this.navigationBarAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.navigationBarAlpha, 1.0f);
        this.navigationBarAnimation = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$37(valueAnimator2);
            }
        });
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(0.75f);
        this.appearSpringAnimation.getSpring().setStiffness(350.0f);
        this.appearSpringAnimation.start();
        if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
            this.container.setLayerType(2, null);
        }
        this.currentSheetAnimationType = 1;
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.currentSheetAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofInt(this.backDrawable, (Property<ColorDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, this.dimBehind ? this.dimBehindAlpha : 0));
        this.currentSheetAnimation.setDuration(400L);
        this.currentSheetAnimation.setStartDelay(20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        final AnimationNotificationsLocker animationNotificationsLocker = new AnimationNotificationsLocker();
        final BottomSheet.BottomSheetDelegateInterface bottomSheetDelegateInterface = super.delegate;
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$38(animationNotificationsLocker, bottomSheetDelegateInterface);
            }
        };
        this.appearSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$39(runnable, dynamicAnimation, z, f, f2);
            }
        });
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
            final Runnable val$onAnimationEnd;

            AnonymousClass22(final Runnable runnable2) {
                r2 = runnable2;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator)) {
                    return;
                }
                ((BottomSheet) ChatAttachAlert.this).currentSheetAnimation = null;
                ((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType = 0;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator) || ChatAttachAlert.this.appearSpringAnimation == null || ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                    return;
                }
                r2.run();
            }
        });
        animationNotificationsLocker.lock();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        setNavBarAlpha(0.0f);
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatAttachAlert.this.lambda$onCustomOpenAnimation$40(valueAnimator2);
            }
        });
        ofFloat2.setStartDelay(25L);
        ofFloat2.setDuration(200L);
        ofFloat2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat2.start();
        return true;
    }

    public void onDestroy() {
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null) {
                attachAlertLayout.onDestroy();
            }
            i++;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.quickRepliesUpdated);
        this.destroyed = true;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override
    protected void onDismissWithTouchOutside() {
        if (this.currentAttachLayout.onDismissWithTouchOutside()) {
            dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.currentAttachLayout.onSheetKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onLongClickBotButton(final TLRPC.TL_attachMenuBot tL_attachMenuBot, final TLRPC.User user) {
        String userName = tL_attachMenuBot != null ? tL_attachMenuBot.short_name : UserObject.getUserName(user);
        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots().bots.iterator();
        while (it.hasNext() && it.next().bot_id != user.id) {
        }
        String formatString = LocaleController.formatString("BotRemoveFromMenu", R.string.BotRemoveFromMenu, userName);
        AlertDialog.Builder title = new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.BotRemoveFromMenuTitle));
        if (tL_attachMenuBot == null) {
            formatString = LocaleController.formatString("BotRemoveInlineFromMenu", R.string.BotRemoveInlineFromMenu, userName);
        }
        title.setMessage(AndroidUtilities.replaceTags(formatString)).setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatAttachAlert.this.lambda$onLongClickBotButton$30(tL_attachMenuBot, user, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
    }

    @Override
    public void onOpenAnimationEnd() {
        MediaController.AlbumEntry albumEntry = this.baseFragment instanceof ChatActivity ? MediaController.allMediaAlbumEntry : MediaController.allPhotosAlbumEntry;
        if (Build.VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        this.currentAttachLayout.onOpenAnimationEnd();
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
        this.openTransitionFinished = true;
        if (this.videosEnabled || this.photosEnabled) {
            return;
        }
        checkCanRemoveRestrictionsByBoosts();
    }

    public void onPause() {
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                this.paused = true;
                return;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null) {
                attachAlertLayout.onPause();
            }
            i++;
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlertLocationLayout chatAttachAlertLocationLayout;
        if (i == 5 && iArr != null && iArr.length > 0 && iArr[0] == 0) {
            openContactsLayout();
        } else if (i == 30 && (chatAttachAlertLocationLayout = this.locationLayout) != null && this.currentAttachLayout == chatAttachAlertLocationLayout && isShowing()) {
            this.locationLayout.openShareLiveLocation();
        }
    }

    public void onResume() {
        int i = 0;
        this.paused = false;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null) {
                attachAlertLayout.onResume();
            }
            i++;
        }
        if (isShowing()) {
            this.delegate.needEnterComment();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = getContext();
        if ((context instanceof ContextWrapper) && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).addOverlayPasscodeView(this.passcodeView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Context context = getContext();
        if ((context instanceof ContextWrapper) && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).removeOverlayPasscodeView(this.passcodeView);
        }
    }

    public void openColorsLayout() {
        if (this.colorsLayout == null) {
            ChatAttachAlertColorsLayout chatAttachAlertColorsLayout = new ChatAttachAlertColorsLayout(this, getContext(), this.resourcesProvider);
            this.colorsLayout = chatAttachAlertColorsLayout;
            chatAttachAlertColorsLayout.setDelegate(new Consumer() {
                @Override
                public final void accept(Object obj) {
                    ChatAttachAlert.this.lambda$openColorsLayout$36(obj);
                }
            });
        }
        showLayout(this.colorsLayout);
    }

    public void presentFragment(PhotoPickerActivity photoPickerActivity) {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment == null && (baseFragment = LaunchActivity.getLastFragment()) == null) {
            return;
        }
        baseFragment.presentFragment(photoPickerActivity);
    }

    @Override
    public void setAllowDrawContent(boolean z) {
        super.setAllowDrawContent(z);
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
            if (attachAlertLayout != chatAttachAlertPhotoLayout || chatAttachAlertPhotoLayout == null || chatAttachAlertPhotoLayout.cameraExpanded) {
                return;
            }
            chatAttachAlertPhotoLayout.pauseCamera(!z || this.sent);
        }
    }

    public void setAllowEnterCaption(boolean z) {
        this.allowEnterCaption = z;
    }

    @Override
    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
    }

    public void setAvatarPicker(int i, boolean z) {
        TextView textView;
        int i2;
        this.avatarPicker = i;
        this.avatarSearch = z;
        if (i != 0) {
            this.typeButtonsAvailable = false;
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == null || attachAlertLayout == this.photoLayout) {
                this.buttonsRecyclerView.setVisibility(8);
                this.shadow.setVisibility(8);
            }
            if (this.avatarPicker == 2) {
                textView = this.selectedTextView;
                i2 = R.string.ChoosePhotoOrVideo;
            } else {
                textView = this.selectedTextView;
                i2 = R.string.ChoosePhoto;
            }
            textView.setText(LocaleController.getString(i2));
        } else {
            this.typeButtonsAvailable = true;
        }
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.updateAvatarPicker();
        }
    }

    public void setCanOpenPreview(boolean z) {
        this.canOpenPreview = z;
        this.selectedArrowImageView.setVisibility((!z || this.avatarPicker == 2) ? 8 : 0);
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
    }

    public void setDocumentsDelegate(ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.documentsDelegate = documentSelectActivityDelegate;
    }

    public void setEditingMessageObject(int i, MessageObject messageObject) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout;
        if (messageObject != null && (chatAttachAlertPhotoLayout = this.photoLayout) != null) {
            chatAttachAlertPhotoLayout.clearSelectedPhotos();
        }
        if (this.editingMessageObject == messageObject && this.editType == i) {
            return;
        }
        this.editingMessageObject = messageObject;
        if (messageObject != null && messageObject.hasValidGroupId()) {
            i = this.editingMessageObject.isMusic() ? 2 : this.editingMessageObject.isDocument() ? 1 : 0;
        }
        this.editType = i;
        if (this.editingMessageObject != null) {
            this.maxSelectedPhotos = 1;
            this.allowOrder = false;
        } else {
            this.maxSelectedPhotos = -1;
            this.allowOrder = true;
        }
        this.buttonsAdapter.notifyDataSetChanged();
        updateCountButton(0);
    }

    public void setImageUpdater(ImageUpdater imageUpdater) {
        this.parentImageUpdater = imageUpdater;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        if (this.editingMessageObject != null) {
            return;
        }
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public void setOpenWithFrontFaceCamera(boolean z) {
        this.openWithFrontFaceCamera = z;
    }

    public void setSoundPicker() {
        this.isSoundPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
        this.selectedTextView.setText(LocaleController.getString(R.string.ChoosePhotoOrVideo));
    }

    public void setStoryAudioPicker() {
        this.isStoryAudioPicker = true;
    }

    public void setStoryLocationPicker() {
        this.isStoryLocationPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
    }

    public void setStoryLocationPicker(double d, double d2) {
        this.storyLocationPickerLatLong = new double[]{d, d2};
        this.isStoryLocationPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
    }

    public void setStoryLocationPicker(boolean z, File file) {
        this.storyLocationPickerFileIsVideo = z;
        this.storyLocationPickerPhotoFile = file;
        this.isStoryLocationPicker = true;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
    }

    public void setStoryMediaPicker() {
        this.storyMediaPicker = true;
        this.typeButtonsAvailable = false;
        this.selectedTextView.setText(LocaleController.getString(R.string.ChoosePhotoOrVideo));
    }

    protected void setupMentionContainer() {
        this.mentionContainer.getAdapter().setAllowStickers(false);
        this.mentionContainer.getAdapter().setAllowBots(false);
        this.mentionContainer.getAdapter().setAllowChats(false);
        this.mentionContainer.getAdapter().setSearchInDailogs(true);
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            this.mentionContainer.getAdapter().setUserOrChat(chatActivity.getCurrentUser(), chatActivity.getCurrentChat());
            this.mentionContainer.getAdapter().setChatInfo(chatActivity.getCurrentChatInfo());
            this.mentionContainer.getAdapter().setNeedUsernames(chatActivity.getCurrentChat() != null);
        } else {
            this.mentionContainer.getAdapter().setChatInfo(null);
            this.mentionContainer.getAdapter().setNeedUsernames(false);
        }
        this.mentionContainer.getAdapter().setNeedBotContext(false);
    }

    public void setupPhotoPicker(String str) {
        this.avatarPicker = 1;
        this.isPhotoPicker = true;
        this.avatarSearch = false;
        this.typeButtonsAvailable = false;
        this.videosEnabled = false;
        this.buttonsRecyclerView.setVisibility(8);
        this.shadow.setVisibility(8);
        this.selectedTextView.setText(str);
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.updateAvatarPicker();
        }
    }

    @Override
    protected boolean shouldOverlayCameraViewOverNavBar() {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        return attachAlertLayout == chatAttachAlertPhotoLayout && chatAttachAlertPhotoLayout.cameraExpanded;
    }

    @Override
    public void show() {
        super.show();
        this.buttonPressed = false;
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            this.calcMandatoryInsets = ((ChatActivity) baseFragment).isKeyboardVisible();
        }
        this.openTransitionFinished = false;
        if (Build.VERSION.SDK_INT >= 30) {
            this.navBarColorKey = -1;
            this.navBarColor = ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundGray), 0);
            AndroidUtilities.setNavigationBarColor(getWindow(), this.navBarColor, false);
            AndroidUtilities.setLightNavigationBar(getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(this.navBarColor)) > 0.721d);
        }
    }

    public void showBotLayout(long r17, java.lang.String r19, boolean r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.showBotLayout(long, java.lang.String, boolean, boolean):void");
    }

    public void showBotLayout(long j, boolean z) {
        showBotLayout(j, null, false, z);
    }

    public void showLayout(AttachAlertLayout attachAlertLayout) {
        long j = this.selectedId;
        ChatAttachRestrictedLayout chatAttachRestrictedLayout = this.restrictedLayout;
        if (attachAlertLayout == chatAttachRestrictedLayout) {
            j = chatAttachRestrictedLayout.id;
        } else if (attachAlertLayout == this.photoLayout) {
            j = 1;
        } else if (attachAlertLayout == this.audioLayout) {
            j = 3;
        } else if (attachAlertLayout == this.documentLayout) {
            j = 4;
        } else if (attachAlertLayout == this.contactsLayout) {
            j = 5;
        } else if (attachAlertLayout == this.locationLayout) {
            j = 6;
        } else if (attachAlertLayout == this.pollLayout) {
            j = 9;
        } else if (attachAlertLayout == this.colorsLayout) {
            j = 10;
        } else if (attachAlertLayout == this.quickRepliesLayout) {
            j = 11;
        }
        showLayout(attachAlertLayout, j);
    }

    public void updateCommentTextViewPosition() {
        this.commentTextView.getLocationOnScreen(this.commentTextViewLocation);
        if (this.mentionContainer != null) {
            float f = -this.commentTextView.getHeight();
            if (this.mentionContainer.getY() != f) {
                this.mentionContainer.setTranslationY(f);
                this.mentionContainer.invalidate();
            }
        }
    }

    public void updateCountButton(int r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updateCountButton(int):void");
    }

    public void updateLayout(AttachAlertLayout attachAlertLayout, boolean z, int i) {
        int currentItemTop;
        if (attachAlertLayout == null || (currentItemTop = attachAlertLayout.getCurrentItemTop()) == Integer.MAX_VALUE) {
            return;
        }
        boolean z2 = false;
        boolean z3 = attachAlertLayout == this.currentAttachLayout && currentItemTop <= attachAlertLayout.getButtonsHideOffset();
        this.pinnedToTop = z3;
        AttachAlertLayout attachAlertLayout2 = this.currentAttachLayout;
        if (attachAlertLayout2 != this.photoPreviewLayout && this.keyboardVisible && z) {
            boolean z4 = attachAlertLayout2 instanceof ChatAttachAlertBotWebViewLayout;
        }
        if (attachAlertLayout == attachAlertLayout2) {
            updateActionBarVisibility(z3, true);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) attachAlertLayout.getLayoutParams();
        int dp = currentItemTop + ((layoutParams == null ? 0 : layoutParams.topMargin) - AndroidUtilities.dp(11.0f));
        AttachAlertLayout attachAlertLayout3 = this.currentAttachLayout;
        int i2 = attachAlertLayout3 == attachAlertLayout ? 0 : 1;
        if ((attachAlertLayout3 instanceof ChatAttachAlertPhotoLayoutPreview) || (this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
            Object obj = this.viewChangeAnimator;
            if ((obj instanceof SpringAnimation) && ((SpringAnimation) obj).isRunning()) {
                z2 = true;
            }
        }
        int[] iArr = this.scrollOffsetY;
        int i3 = iArr[i2];
        if (i3 == dp && !z2) {
            if (i != 0) {
                this.previousScrollOffsetY = i3;
            }
        } else {
            this.previousScrollOffsetY = i3;
            iArr[i2] = dp;
            updateSelectedPosition(i2);
            this.containerView.invalidate();
        }
    }

    public void updatePhotoPreview(boolean z) {
        if (!z) {
            showLayout(this.photoLayout);
            return;
        }
        if (this.canOpenPreview) {
            if (this.photoPreviewLayout == null) {
                Context context = getContext();
                Theme.ResourcesProvider resourcesProvider = this.parentThemeDelegate;
                if (resourcesProvider == null) {
                    resourcesProvider = this.resourcesProvider;
                }
                ChatAttachAlertPhotoLayoutPreview chatAttachAlertPhotoLayoutPreview = new ChatAttachAlertPhotoLayoutPreview(this, context, resourcesProvider);
                this.photoPreviewLayout = chatAttachAlertPhotoLayoutPreview;
                chatAttachAlertPhotoLayoutPreview.bringToFront();
            }
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            AttachAlertLayout attachAlertLayout2 = this.photoPreviewLayout;
            if (attachAlertLayout == attachAlertLayout2) {
                attachAlertLayout2 = this.photoLayout;
            }
            showLayout(attachAlertLayout2);
        }
    }
}
