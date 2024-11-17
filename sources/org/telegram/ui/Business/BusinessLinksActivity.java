package org.telegram.ui.Business;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialogDecor;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.BusinessLinksActivity;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.URLSpanCopyToClipboard;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.LaunchActivity;

public class BusinessLinksActivity extends UniversalFragment implements NotificationCenter.NotificationCenterDelegate {
    private static AlertDialog currentDialog;

    public static class BusinessLinkView extends FrameLayout {
        private TLRPC.TL_businessChatLink businessLink;
        private final SimpleTextView clicksCountTextView;
        private final ImageView imageView;
        private final SpoilersTextView messagePreviewTextView;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private final SimpleTextView titleTextView;

        public BusinessLinkView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            setWillNotDraw(false);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(R.drawable.msg_limit_links);
            imageView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f));
            imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(36.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    BusinessLinksActivity.BusinessLinkView.this.lambda$new$0(view);
                }
            });
            addView(imageView, LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388627, 14.0f, 0.0f, 14.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleTextView = simpleTextView;
            simpleTextView.setTextSize(15);
            simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(simpleTextView, LayoutHelper.createFrameRelatively(-1.0f, 20.0f, 55, 64.0f, 10.0f, 14.0f, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.clicksCountTextView = simpleTextView2;
            simpleTextView2.setTextSize(14);
            int i = Theme.key_windowBackgroundWhiteGrayText2;
            simpleTextView2.setTextColor(Theme.getColor(i));
            simpleTextView2.setGravity(LocaleController.isRTL ? 3 : 5);
            addView(simpleTextView2, LayoutHelper.createFrameRelatively(-1.0f, 18.0f, 55, 64.0f, 10.66f, 14.0f, 0.0f));
            SpoilersTextView spoilersTextView = new SpoilersTextView(context);
            this.messagePreviewTextView = spoilersTextView;
            spoilersTextView.setTextSize(1, 13.0f);
            spoilersTextView.setMaxLines(1);
            spoilersTextView.setEllipsize(TextUtils.TruncateAt.END);
            spoilersTextView.setTextColor(Theme.getColor(i, resourcesProvider));
            spoilersTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            spoilersTextView.allowClickSpoilers = false;
            spoilersTextView.setUseAlphaForEmoji(false);
            NotificationCenter.listenEmojiLoading(spoilersTextView);
            addView(spoilersTextView, LayoutHelper.createFrameRelatively(-1.0f, 20.0f, 87, 64.0f, 0.0f, 14.0f, 6.0f));
        }

        public void lambda$new$0(View view) {
            TLRPC.TL_businessChatLink tL_businessChatLink = this.businessLink;
            if (tL_businessChatLink != null) {
                AndroidUtilities.addToClipboard(tL_businessChatLink.link);
                BulletinFactory.of(LaunchActivity.getLastFragment()).createCopyLinkBulletin().show();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                Paint themePaint = Theme.getThemePaint("paintDivider", this.resourcesProvider);
                if (themePaint == null) {
                    themePaint = Theme.dividerPaint;
                }
                canvas.drawRect(AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 64.0f), getMeasuredHeight() - 1, getWidth() - AndroidUtilities.dp(LocaleController.isRTL ? 64.0f : 0.0f), getMeasuredHeight(), themePaint);
            }
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (LocaleController.isRTL) {
                this.titleTextView.setPadding(this.clicksCountTextView.getTextWidth(), 0, 0, 0);
            } else {
                this.titleTextView.setPadding(0, 0, this.clicksCountTextView.getTextWidth(), 0);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void set(BusinessLinkWrapper businessLinkWrapper, boolean z) {
            SimpleTextView simpleTextView;
            String stripHttps;
            this.needDivider = z;
            TLRPC.TL_businessChatLink tL_businessChatLink = businessLinkWrapper.link;
            this.businessLink = tL_businessChatLink;
            if (TextUtils.isEmpty(tL_businessChatLink.title)) {
                simpleTextView = this.titleTextView;
                stripHttps = BusinessLinksController.stripHttps(this.businessLink.link);
            } else {
                simpleTextView = this.titleTextView;
                stripHttps = this.businessLink.title;
            }
            simpleTextView.setText(stripHttps);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.businessLink.message);
            TLRPC.TL_businessChatLink tL_businessChatLink2 = this.businessLink;
            MediaDataController.addTextStyleRuns(tL_businessChatLink2.entities, tL_businessChatLink2.message, spannableStringBuilder);
            CharSequence replaceEmoji = Emoji.replaceEmoji(spannableStringBuilder, this.messagePreviewTextView.getPaint().getFontMetricsInt(), false);
            MessageObject.replaceAnimatedEmoji(replaceEmoji, this.businessLink.entities, this.messagePreviewTextView.getPaint().getFontMetricsInt());
            this.messagePreviewTextView.setText(replaceEmoji);
            int i = this.businessLink.views;
            if (i == 0) {
                this.clicksCountTextView.setText(LocaleController.formatString(R.string.NoClicks, new Object[0]));
            } else {
                this.clicksCountTextView.setText(LocaleController.formatPluralString("Clicks", i, new Object[0]));
            }
            this.clicksCountTextView.requestLayout();
            invalidate();
        }
    }

    public static class BusinessLinkWrapper {
        TLRPC.TL_businessChatLink link;

        public BusinessLinkWrapper(TLRPC.TL_businessChatLink tL_businessChatLink) {
            this.link = tL_businessChatLink;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            BusinessLinkWrapper businessLinkWrapper = (BusinessLinkWrapper) obj;
            TLRPC.TL_businessChatLink tL_businessChatLink = this.link;
            int i = tL_businessChatLink.views;
            TLRPC.TL_businessChatLink tL_businessChatLink2 = businessLinkWrapper.link;
            return i == tL_businessChatLink2.views && TextUtils.equals(tL_businessChatLink.link, tL_businessChatLink2.link) && TextUtils.equals(this.link.title, businessLinkWrapper.link.title) && TextUtils.equals(this.link.message, businessLinkWrapper.link.message) && MediaDataController.entitiesEqual(this.link.entities, businessLinkWrapper.link.entities);
        }
    }

    public static boolean closeRenameAlert() {
        AlertDialog alertDialog = currentDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return false;
        }
        currentDialog.dismiss();
        return true;
    }

    private static int getPrivacyType(ArrayList arrayList) {
        char c = 65535;
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.PrivacyRule privacyRule = (TLRPC.PrivacyRule) arrayList.get(i);
            if (!(privacyRule instanceof TLRPC.TL_privacyValueAllowChatParticipants)) {
                if (!(privacyRule instanceof TLRPC.TL_privacyValueDisallowChatParticipants)) {
                    if (!(privacyRule instanceof TLRPC.TL_privacyValueAllowUsers)) {
                        if (!(privacyRule instanceof TLRPC.TL_privacyValueDisallowUsers)) {
                            if (!(privacyRule instanceof TLRPC.TL_privacyValueAllowPremium) && c == 65535) {
                                c = privacyRule instanceof TLRPC.TL_privacyValueAllowAll ? (char) 0 : privacyRule instanceof TLRPC.TL_privacyValueDisallowAll ? (char) 1 : (char) 2;
                            }
                        }
                    }
                }
                z = true;
            }
        }
        if (c == 0 || (c == 65535 && z)) {
            return 0;
        }
        return c == 2 ? 2 : 1;
    }

    public void lambda$onLongClick$10(TLRPC.TL_businessChatLink tL_businessChatLink) {
        openRenameAlert(getContext(), this.currentAccount, tL_businessChatLink, this.resourceProvider, false);
    }

    public void lambda$onLongClick$11(TLRPC.TL_businessChatLink tL_businessChatLink, DialogInterface dialogInterface, int i) {
        BusinessLinksController.getInstance(this.currentAccount).deleteLinkUndoable(this, tL_businessChatLink.link);
    }

    public void lambda$onLongClick$12(final TLRPC.TL_businessChatLink tL_businessChatLink) {
        AlertDialog create = new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString(R.string.BusinessLinksDeleteTitle)).setMessage(LocaleController.getString(R.string.BusinessLinksDeleteMessage)).setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                BusinessLinksActivity.this.lambda$onLongClick$11(tL_businessChatLink, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_text_RedBold));
        }
    }

    public static void lambda$onLongClick$8(TLRPC.TL_businessChatLink tL_businessChatLink) {
        AndroidUtilities.addToClipboard(tL_businessChatLink.link);
        BulletinFactory.of(LaunchActivity.getLastFragment()).createCopyLinkBulletin().show();
    }

    public void lambda$onLongClick$9(TLRPC.TL_businessChatLink tL_businessChatLink) {
        Intent intent = new Intent(getContext(), (Class<?>) LaunchActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", tL_businessChatLink.link);
        startActivityForResult(intent, 500);
    }

    public static boolean lambda$openRenameAlert$0(EditTextBoldCursor editTextBoldCursor, int i, TLRPC.TL_businessChatLink tL_businessChatLink, AlertDialog[] alertDialogArr, View view, TextView textView, int i2, KeyEvent keyEvent) {
        if (i2 != 6) {
            return false;
        }
        String obj = editTextBoldCursor.getText().toString();
        if (obj.length() > 32) {
            AndroidUtilities.shakeView(editTextBoldCursor);
            return true;
        }
        BusinessLinksController.getInstance(i).editLinkTitle(tL_businessChatLink.link, obj);
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (alertDialogArr[0] == currentDialog) {
            currentDialog = null;
        }
        if (view != null) {
            view.requestFocus();
        }
        return true;
    }

    public static void lambda$openRenameAlert$1(EditTextBoldCursor editTextBoldCursor, int i, TLRPC.TL_businessChatLink tL_businessChatLink, DialogInterface dialogInterface, int i2) {
        String obj = editTextBoldCursor.getText().toString();
        if (obj.length() > 32) {
            AndroidUtilities.shakeView(editTextBoldCursor);
        } else {
            BusinessLinksController.getInstance(i).editLinkTitle(tL_businessChatLink.link, obj);
            dialogInterface.dismiss();
        }
    }

    public static void lambda$openRenameAlert$3(View view, DialogInterface dialogInterface) {
        currentDialog = null;
        if (view != null) {
            view.requestFocus();
        }
    }

    public static void lambda$openRenameAlert$4(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void lambda$openRenameAlert$5(View view, EditTextBoldCursor editTextBoldCursor, Runnable runnable) {
        if (view != null) {
            view.requestFocus();
        }
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        AndroidUtilities.runOnUIThread(runnable, 80L);
    }

    public static void lambda$openRenameAlert$7(View view, EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        if (view != null) {
            view.clearFocus();
        }
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void openRenameAlert(Context context, final int i, final TLRPC.TL_businessChatLink tL_businessChatLink, final Theme.ResourcesProvider resourcesProvider, boolean z) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        Activity findActivity = AndroidUtilities.findActivity(context);
        final View currentFocus = findActivity != null ? findActivity.getCurrentFocus() : null;
        boolean z2 = lastFragment != null && (lastFragment.getFragmentView() instanceof SizeNotifierFrameLayout) && ((SizeNotifierFrameLayout) lastFragment.getFragmentView()).measureKeyboardHeight() > AndroidUtilities.dp(20.0f) && !z;
        final AlertDialog[] alertDialogArr = new AlertDialog[1];
        ?? builder = z2 ? new AlertDialogDecor.Builder(context, resourcesProvider) : new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.BusinessLinksRenameTitle));
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            AnimatedTextView.AnimatedTextDrawable limit;
            AnimatedColor limitColor = new AnimatedColor(this);
            private int limitCount;

            {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
                this.limit = animatedTextDrawable;
                animatedTextDrawable.setAnimationProperties(0.2f, 0L, 160L, CubicBezierInterpolator.EASE_OUT_QUINT);
                this.limit.setTextSize(AndroidUtilities.dp(15.33f));
                this.limit.setCallback(this);
                this.limit.setGravity(5);
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                this.limit.setTextColor(this.limitColor.set(Theme.getColor(this.limitCount < 0 ? Theme.key_text_RedRegular : Theme.key_dialogSearchHint, resourcesProvider)));
                this.limit.setBounds(getScrollX(), 0, getScrollX() + getWidth(), getHeight());
                this.limit.draw(canvas);
            }

            @Override
            public void onMeasure(int i2, int i3) {
                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), 1073741824));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                super.onTextChanged(charSequence, i2, i3, i4);
                if (this.limit != null) {
                    this.limitCount = 32 - charSequence.length();
                    this.limit.cancelAnimation();
                    AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.limit;
                    String str = "";
                    if (this.limitCount <= 4) {
                        str = "" + this.limitCount;
                    }
                    animatedTextDrawable.setText(str);
                }
            }

            @Override
            protected boolean verifyDrawable(Drawable drawable) {
                return drawable == this.limit || super.verifyDrawable(drawable);
            }
        };
        MediaDataController.getInstance(i).fetchNewEmojiKeywords(AndroidUtilities.getCurrentKeyboardLanguage(), true);
        editTextBoldCursor.setInputType(49153);
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setText(tL_businessChatLink.title);
        int i2 = Theme.key_dialogTextBlack;
        editTextBoldCursor.setTextColor(Theme.getColor(i2, resourcesProvider));
        editTextBoldCursor.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourcesProvider));
        editTextBoldCursor.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor));
        editTextBoldCursor.setHintText(LocaleController.getString(R.string.BusinessLinksNamePlaceholder));
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setFocusable(true);
        editTextBoldCursor.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider), Theme.getColor(Theme.key_text_RedRegular, resourcesProvider));
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setBackgroundDrawable(null);
        editTextBoldCursor.setPadding(0, 0, AndroidUtilities.dp(42.0f), 0);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(i2, resourcesProvider));
        textView.setTextSize(1, 16.0f);
        textView.setText(LocaleController.getString(R.string.BusinessLinksRenameMessage));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 24.0f, 5.0f, 24.0f, 12.0f));
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 10.0f));
        builder.setView(linearLayout);
        builder.setWidth(AndroidUtilities.dp(292.0f));
        final View view = currentFocus;
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView2, int i3, KeyEvent keyEvent) {
                boolean lambda$openRenameAlert$0;
                lambda$openRenameAlert$0 = BusinessLinksActivity.lambda$openRenameAlert$0(EditTextBoldCursor.this, i, tL_businessChatLink, alertDialogArr, view, textView2, i3, keyEvent);
                return lambda$openRenameAlert$0;
            }
        });
        builder.setPositiveButton(LocaleController.getString(R.string.Done), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                BusinessLinksActivity.lambda$openRenameAlert$1(EditTextBoldCursor.this, i, tL_businessChatLink, dialogInterface, i3);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i3) {
                dialogInterface.dismiss();
            }
        });
        if (z2) {
            AlertDialog create = builder.create();
            currentDialog = create;
            alertDialogArr[0] = create;
            create.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    BusinessLinksActivity.lambda$openRenameAlert$3(currentFocus, dialogInterface);
                }
            });
            currentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public final void onShow(DialogInterface dialogInterface) {
                    BusinessLinksActivity.lambda$openRenameAlert$4(EditTextBoldCursor.this, dialogInterface);
                }
            });
            currentDialog.showDelayed(250L);
        } else {
            builder.overrideDismissListener(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    BusinessLinksActivity.lambda$openRenameAlert$5(currentFocus, editTextBoldCursor, (Runnable) obj);
                }
            });
            AlertDialog create2 = builder.create();
            alertDialogArr[0] = create2;
            create2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                }
            });
            alertDialogArr[0].setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public final void onShow(DialogInterface dialogInterface) {
                    BusinessLinksActivity.lambda$openRenameAlert$7(currentFocus, editTextBoldCursor, dialogInterface);
                }
            });
            alertDialogArr[0].show();
        }
        alertDialogArr[0].setDismissDialogByButtons(false);
        editTextBoldCursor.setSelection(editTextBoldCursor.getText().length());
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i == NotificationCenter.businessLinksUpdated || i == NotificationCenter.privacyRulesUpdated) {
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
                return;
            }
            universalAdapter.update(true);
            return;
        }
        if (i != NotificationCenter.businessLinkCreated) {
            if (i == NotificationCenter.needDeleteBusinessLink) {
                BusinessLinksController.getInstance(this.currentAccount).deleteLinkUndoable(this, ((TLRPC.TL_businessChatLink) objArr[0]).link);
            }
        } else {
            TLRPC.TL_businessChatLink tL_businessChatLink = (TLRPC.TL_businessChatLink) objArr[0];
            Bundle bundle = new Bundle();
            bundle.putInt("chatMode", 6);
            bundle.putString("business_link", tL_businessChatLink.link);
            presentFragment(new ChatActivity(bundle));
        }
    }

    @Override
    protected void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(LocaleController.getString(R.string.BusinessLinksInfo), R.raw.biz_links));
        universalAdapter.whiteSectionStart();
        if (BusinessLinksController.getInstance(this.currentAccount).canAddNew()) {
            arrayList.add(UItem.asButton(1, R.drawable.menu_link_create, LocaleController.getString(R.string.BusinessLinksAdd)).accent());
        }
        Iterator it = BusinessLinksController.getInstance(this.currentAccount).links.iterator();
        while (it.hasNext()) {
            arrayList.add(UItem.asBusinessChatLink(new BusinessLinkWrapper((TLRPC.TL_businessChatLink) it.next())));
        }
        universalAdapter.whiteSectionEnd();
        TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        String str = MessagesController.getInstance(this.currentAccount).linkPrefix + "/";
        ArrayList arrayList2 = new ArrayList(2);
        String publicUsername = UserObject.getPublicUsername(currentUser);
        if (publicUsername != null) {
            arrayList2.add(str + publicUsername);
        }
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(6);
        ArrayList<TLRPC.PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
        if (!TextUtils.isEmpty(currentUser.phone) && privacyRules != null && privacyRules2 != null && (getPrivacyType(privacyRules) != 1 || getPrivacyType(privacyRules2) != 2)) {
            arrayList2.add(str + "+" + currentUser.phone);
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        String formatString = arrayList2.size() == 2 ? LocaleController.formatString(R.string.BusinessLinksFooterTwoLinks, arrayList2.get(0), arrayList2.get(1)) : LocaleController.formatString(R.string.BusinessLinksFooterOneLink, arrayList2.get(0));
        SpannableString spannableString = new SpannableString(formatString);
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            String str2 = (String) it2.next();
            int indexOf = formatString.indexOf(str2);
            if (indexOf > -1) {
                spannableString.setSpan(new URLSpanCopyToClipboard("https://" + str2, this), indexOf, str2.length() + indexOf, 33);
            }
        }
        arrayList.add(UItem.asShadow(spannableString));
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.BusinessLinks);
    }

    @Override
    public boolean onBackPressed() {
        if (closeRenameAlert()) {
            return false;
        }
        return super.onBackPressed();
    }

    @Override
    protected void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.id == 1) {
            BusinessLinksController.getInstance(this.currentAccount).createEmptyLink();
            return;
        }
        if (uItem.viewType == 29) {
            Object obj = uItem.object;
            if (obj instanceof BusinessLinkWrapper) {
                Bundle bundle = new Bundle();
                bundle.putInt("chatMode", 6);
                bundle.putString("business_link", ((BusinessLinkWrapper) obj).link.link);
                presentFragment(new ChatActivity(bundle));
            }
        }
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.businessLinksUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.businessLinkCreated);
        getNotificationCenter().addObserver(this, NotificationCenter.needDeleteBusinessLink);
        getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
        BusinessLinksController.getInstance(this.currentAccount).load(true);
        ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.businessLinksUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.businessLinkCreated);
        getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteBusinessLink);
        getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
        Bulletin.hideVisible();
        super.onFragmentDestroy();
    }

    @Override
    protected boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.viewType != 29) {
            return false;
        }
        Object obj = uItem.object;
        if (!(obj instanceof BusinessLinkWrapper)) {
            return false;
        }
        final TLRPC.TL_businessChatLink tL_businessChatLink = ((BusinessLinkWrapper) obj).link;
        ItemOptions makeOptions = ItemOptions.makeOptions(this, view);
        makeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.Copy), new Runnable() {
            @Override
            public final void run() {
                BusinessLinksActivity.lambda$onLongClick$8(TLRPC.TL_businessChatLink.this);
            }
        });
        makeOptions.add(R.drawable.msg_share, LocaleController.getString(R.string.LinkActionShare), new Runnable() {
            @Override
            public final void run() {
                BusinessLinksActivity.this.lambda$onLongClick$9(tL_businessChatLink);
            }
        });
        makeOptions.add(R.drawable.msg_edit, LocaleController.getString(R.string.Rename), new Runnable() {
            @Override
            public final void run() {
                BusinessLinksActivity.this.lambda$onLongClick$10(tL_businessChatLink);
            }
        });
        makeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.Delete), true, new Runnable() {
            @Override
            public final void run() {
                BusinessLinksActivity.this.lambda$onLongClick$12(tL_businessChatLink);
            }
        });
        makeOptions.show();
        return true;
    }
}
