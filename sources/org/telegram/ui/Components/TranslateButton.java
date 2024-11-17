package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.RestrictedLanguagesSelectActivity;

public abstract class TranslateButton extends FrameLayout {
    private boolean[] accusative;
    private final int currentAccount;
    private final long dialogId;
    private final BaseFragment fragment;
    private ImageView menuView;
    private Theme.ResourcesProvider resourcesProvider;
    private AnimatedTextView textView;
    private final Drawable translateDrawable;
    public final SpannableString translateIcon;

    public TranslateButton(Context context, final int i, long j, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.accusative = new boolean[1];
        this.currentAccount = i;
        this.dialogId = j;
        this.fragment = baseFragment;
        this.resourcesProvider = resourcesProvider;
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, false);
        this.textView = animatedTextView;
        animatedTextView.setAnimationProperties(0.3f, 0L, 450L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.textView.setTextSize(AndroidUtilities.dp(15.0f));
        this.textView.setTypeface(AndroidUtilities.bold());
        this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.textView.setGravity(1);
        this.textView.setIgnoreRTL(!LocaleController.isRTL);
        AnimatedTextView animatedTextView2 = this.textView;
        animatedTextView2.adaptWidth = false;
        animatedTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TranslateButton.this.lambda$new$0(view);
            }
        });
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
        Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_translate).mutate();
        this.translateDrawable = mutate;
        mutate.setBounds(0, AndroidUtilities.dp(-8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(12.0f));
        SpannableString spannableString = new SpannableString("x");
        this.translateIcon = spannableString;
        spannableString.setSpan(new ImageSpan(mutate, 0), 0, 1, 33);
        ImageView imageView = new ImageView(context);
        this.menuView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.menuView.setImageResource(R.drawable.msg_mini_customize);
        this.menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TranslateButton.this.lambda$new$1(i, view);
            }
        });
        addView(this.menuView, LayoutHelper.createFrame(32, 32.0f, 21, 0.0f, 0.0f, 8.0f, 0.0f));
        updateColors();
    }

    public TranslateButton(Context context, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        this(context, chatActivity.getCurrentAccount(), chatActivity.getDialogId(), chatActivity, resourcesProvider);
    }

    public void lambda$new$0(View view) {
        onButtonClick();
    }

    public void lambda$new$1(int i, View view) {
        if (UserConfig.getInstance(i).isPremium()) {
            onMenuClick();
        } else {
            onCloseClick();
        }
    }

    public static void lambda$onMenuClick$2(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int i, View view) {
        actionBarPopupWindowLayout.getSwipeBack().openForeground(i);
    }

    public static void lambda$onMenuClick$3(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, View view) {
        actionBarPopupWindowLayout.getSwipeBack().closeForeground();
    }

    public void lambda$onMenuClick$4(TranslateController translateController, String str, ActionBarPopupWindow actionBarPopupWindow, View view) {
        translateController.setDialogTranslateTo(this.dialogId, str);
        actionBarPopupWindow.dismiss();
        updateText();
    }

    public void lambda$onMenuClick$5(TranslateController translateController, String str, ActionBarPopupWindow actionBarPopupWindow, View view) {
        translateController.setDialogTranslateTo(this.dialogId, str);
        actionBarPopupWindow.dismiss();
        updateText();
    }

    public void lambda$onMenuClick$6() {
        this.fragment.presentFragment(new RestrictedLanguagesSelectActivity());
    }

    public void lambda$onMenuClick$7(String str, TranslateController translateController, String str2, ActionBarPopupWindow actionBarPopupWindow, View view) {
        RestrictedLanguagesSelectActivity.toggleLanguage(str, true);
        translateController.checkRestrictedLanguagesUpdate();
        translateController.setHideTranslateDialog(this.dialogId, true);
        BulletinFactory.of(this.fragment).createSimpleBulletin(R.raw.msg_translate, TranslateAlert2.capitalFirst(AndroidUtilities.replaceTags(this.accusative[0] ? LocaleController.formatString("AddedToDoNotTranslate", R.string.AddedToDoNotTranslate, str2) : LocaleController.formatString("AddedToDoNotTranslateOther", R.string.AddedToDoNotTranslateOther, str2))), LocaleController.getString(R.string.Settings), new Runnable() {
            @Override
            public final void run() {
                TranslateButton.this.lambda$onMenuClick$6();
            }
        }).show();
        actionBarPopupWindow.dismiss();
    }

    public void lambda$onMenuClick$8(TranslateController translateController) {
        translateController.setHideTranslateDialog(this.dialogId, false);
    }

    public void lambda$onMenuClick$9(final TranslateController translateController, ActionBarPopupWindow actionBarPopupWindow, View view) {
        translateController.setHideTranslateDialog(this.dialogId, true);
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId));
        BulletinFactory.of(this.fragment).createSimpleBulletin(R.raw.msg_translate, AndroidUtilities.replaceTags(LocaleController.getString((chat == null || !ChatObject.isChannelAndNotMegaGroup(chat)) ? chat != null ? R.string.TranslationBarHiddenForGroup : R.string.TranslationBarHiddenForChat : R.string.TranslationBarHiddenForChannel)), LocaleController.getString(R.string.Undo), new Runnable() {
            @Override
            public final void run() {
                TranslateButton.this.lambda$onMenuClick$8(translateController);
            }
        }).show();
        actionBarPopupWindow.dismiss();
    }

    protected abstract void onButtonClick();

    protected abstract void onCloseClick();

    protected void onMenuClick() {
        String capitalFirst;
        final TranslateController translateController = MessagesController.getInstance(this.currentAccount).getTranslateController();
        final ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert2, this.resourcesProvider, 1);
        final ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, this.resourcesProvider));
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        ScrollView scrollView = new ScrollView(getContext()) {
            AnimatedFloat alphaFloat = new AnimatedFloat(this, 350, CubicBezierInterpolator.EASE_OUT_QUINT);
            Drawable topShadowDrawable;
            private boolean wasCanScrollVertically;

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                float f = this.alphaFloat.set(canScrollVertically(-1) ? 1.0f : 0.0f) * 0.5f;
                if (f > 0.0f) {
                    if (this.topShadowDrawable == null) {
                        this.topShadowDrawable = getContext().getResources().getDrawable(R.drawable.header_shadow);
                    }
                    this.topShadowDrawable.setBounds(0, getScrollY(), getWidth(), getScrollY() + this.topShadowDrawable.getIntrinsicHeight());
                    this.topShadowDrawable.setAlpha((int) (f * 255.0f));
                    this.topShadowDrawable.draw(canvas);
                }
            }

            @Override
            public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
                super.onNestedScroll(view, i, i2, i3, i4);
                boolean canScrollVertically = canScrollVertically(-1);
                if (this.wasCanScrollVertically != canScrollVertically) {
                    invalidate();
                    this.wasCanScrollVertically = canScrollVertically;
                }
            }
        };
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        scrollView.addView(linearLayout2);
        linearLayout2.setOrientation(1);
        actionBarPopupWindowLayout.swipeBackGravityRight = true;
        final int addViewToSwipeBack = actionBarPopupWindowLayout.addViewToSwipeBack(linearLayout);
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, false, this.resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.TranslateTo), R.drawable.msg_translate);
        actionBarMenuSubItem.setSubtext(TranslateAlert2.capitalFirst(TranslateAlert2.languageName(translateController.getDialogTranslateTo(this.dialogId))));
        actionBarMenuSubItem.setItemHeight(56);
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TranslateButton.lambda$onMenuClick$2(ActionBarPopupWindow.ActionBarPopupWindowLayout.this, addViewToSwipeBack, view);
            }
        });
        actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
        ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(getContext(), true, false, this.resourcesProvider);
        actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.Back), R.drawable.ic_ab_back);
        actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TranslateButton.lambda$onMenuClick$3(ActionBarPopupWindow.ActionBarPopupWindowLayout.this, view);
            }
        });
        linearLayout.addView(actionBarMenuSubItem2);
        linearLayout.addView(scrollView, LayoutHelper.createLinear(-1, 420));
        final String dialogDetectedLanguage = translateController.getDialogDetectedLanguage(this.dialogId);
        TranslateAlert2.languageName(dialogDetectedLanguage);
        final String languageName = TranslateAlert2.languageName(dialogDetectedLanguage, this.accusative);
        String dialogTranslateTo = translateController.getDialogTranslateTo(this.dialogId);
        ArrayList<TranslateController.Language> suggestedLanguages = TranslateController.getSuggestedLanguages(dialogTranslateTo);
        ArrayList<TranslateController.Language> languages = TranslateController.getLanguages();
        linearLayout2.addView(new ActionBarPopupWindow.GapView(getContext(), this.resourcesProvider), LayoutHelper.createLinear(-1, 8));
        if (dialogTranslateTo != null && (capitalFirst = TranslateAlert2.capitalFirst(TranslateAlert2.languageName(dialogTranslateTo))) != null) {
            ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(getContext(), 2, false, false, this.resourcesProvider);
            actionBarMenuSubItem3.setChecked(true);
            actionBarMenuSubItem3.setText(capitalFirst);
            linearLayout2.addView(actionBarMenuSubItem3);
        }
        Iterator<TranslateController.Language> it = suggestedLanguages.iterator();
        while (it.hasNext()) {
            TranslateController.Language next = it.next();
            final String str = next.code;
            if (!TextUtils.equals(str, dialogDetectedLanguage)) {
                ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(getContext(), 2, false, false, this.resourcesProvider);
                boolean z = dialogTranslateTo != null && dialogTranslateTo.equals(str);
                actionBarMenuSubItem4.setChecked(z);
                actionBarMenuSubItem4.setText(next.displayName);
                if (!z) {
                    actionBarMenuSubItem4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            TranslateButton.this.lambda$onMenuClick$4(translateController, str, actionBarPopupWindow, view);
                        }
                    });
                }
                linearLayout2.addView(actionBarMenuSubItem4);
            }
        }
        linearLayout2.addView(new ActionBarPopupWindow.GapView(getContext(), this.resourcesProvider), LayoutHelper.createLinear(-1, 8));
        Iterator<TranslateController.Language> it2 = languages.iterator();
        while (it2.hasNext()) {
            TranslateController.Language next2 = it2.next();
            final String str2 = next2.code;
            if (!TextUtils.equals(str2, dialogDetectedLanguage)) {
                ActionBarMenuSubItem actionBarMenuSubItem5 = new ActionBarMenuSubItem(getContext(), 2, false, false, this.resourcesProvider);
                boolean z2 = dialogTranslateTo != null && dialogTranslateTo.equals(str2);
                actionBarMenuSubItem5.setChecked(z2);
                actionBarMenuSubItem5.setText(next2.displayName);
                if (!z2) {
                    actionBarMenuSubItem5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            TranslateButton.this.lambda$onMenuClick$5(translateController, str2, actionBarPopupWindow, view);
                        }
                    });
                }
                linearLayout2.addView(actionBarMenuSubItem5);
            }
        }
        actionBarPopupWindowLayout.addView(new ActionBarPopupWindow.GapView(getContext(), this.resourcesProvider), LayoutHelper.createLinear(-1, 8));
        if (languageName != null) {
            ActionBarMenuSubItem actionBarMenuSubItem6 = new ActionBarMenuSubItem(getContext(), true, false, this.resourcesProvider);
            actionBarMenuSubItem6.setTextAndIcon(this.accusative[0] ? LocaleController.formatString("DoNotTranslateLanguage", R.string.DoNotTranslateLanguage, languageName) : LocaleController.formatString("DoNotTranslateLanguageOther", R.string.DoNotTranslateLanguageOther, languageName), R.drawable.msg_block2);
            actionBarMenuSubItem6.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    TranslateButton.this.lambda$onMenuClick$7(dialogDetectedLanguage, translateController, languageName, actionBarPopupWindow, view);
                }
            });
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem6);
        }
        ActionBarMenuSubItem actionBarMenuSubItem7 = new ActionBarMenuSubItem(getContext(), true, false, this.resourcesProvider);
        actionBarMenuSubItem7.setTextAndIcon(LocaleController.getString(R.string.Hide), R.drawable.msg_cancel);
        actionBarMenuSubItem7.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                TranslateButton.this.lambda$onMenuClick$9(translateController, actionBarPopupWindow, view);
            }
        });
        actionBarPopupWindowLayout.addView(actionBarMenuSubItem7);
        actionBarPopupWindow.setPauseNotifications(true);
        actionBarPopupWindow.setDismissAnimationDuration(220);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.setSoftInputMode(0);
        ImageView imageView = this.menuView;
        actionBarPopupWindow.showAsDropDown(imageView, 0, (-imageView.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
    }

    public void updateColors() {
        AnimatedTextView animatedTextView = this.textView;
        int i = Theme.key_chat_addContact;
        animatedTextView.setTextColor(Theme.getColor(i, this.resourcesProvider));
        this.textView.setBackground(Theme.createSelectorDrawable(Theme.getColor(i, this.resourcesProvider) & 436207615, 3));
        this.menuView.setBackground(Theme.createSelectorDrawable(Theme.getColor(i, this.resourcesProvider) & 436207615, 7));
        ImageView imageView = this.menuView;
        int color = Theme.getColor(i, this.resourcesProvider);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
        this.translateDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i, this.resourcesProvider), mode));
    }

    public void updateText() {
        TranslateController translateController = MessagesController.getInstance(this.currentAccount).getTranslateController();
        if (translateController.isTranslatingDialog(this.dialogId)) {
            this.textView.setText(TextUtils.concat(this.translateIcon, " ", LocaleController.getString(R.string.ShowOriginalButton)));
        } else {
            String dialogTranslateTo = translateController.getDialogTranslateTo(this.dialogId);
            if (dialogTranslateTo == null) {
                dialogTranslateTo = "en";
            }
            String languageName = TranslateAlert2.languageName(dialogTranslateTo, this.accusative);
            this.textView.setText(TextUtils.concat(this.translateIcon, " ", this.accusative[0] ? LocaleController.formatString("TranslateToButton", R.string.TranslateToButton, languageName) : LocaleController.formatString("TranslateToButtonOther", R.string.TranslateToButtonOther, languageName)));
        }
        this.menuView.setImageResource(UserConfig.getInstance(this.currentAccount).isPremium() ? R.drawable.msg_mini_customize : R.drawable.msg_close);
    }
}
