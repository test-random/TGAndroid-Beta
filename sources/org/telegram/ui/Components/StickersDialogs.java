package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.recorder.EmojiBottomSheet;

public abstract class StickersDialogs {
    private static int getThemedColor(int i, Theme.ResourcesProvider resourcesProvider) {
        return Theme.getColor(i, resourcesProvider);
    }

    public static void lambda$openStickerPickerDialog$10(final AlertDialog alertDialog, final int i, final TLRPC.Document document, final Object obj, final TLRPC.TL_stickers_addStickerToSet tL_stickers_addStickerToSet, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StickersDialogs.lambda$openStickerPickerDialog$9(AlertDialog.this, tLObject, i, document, tL_error, obj, tL_stickers_addStickerToSet);
            }
        });
    }

    public static Boolean lambda$openStickerPickerDialog$11(final int i, Context context, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, final Object obj, final TLRPC.Document document, Boolean bool) {
        String findAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(document, "😀", Integer.valueOf(i));
        String str = TextUtils.isEmpty(findAnimatedEmojiEmoticon) ? "😀" : findAnimatedEmojiEmoticon;
        final AlertDialog alertDialog = new AlertDialog(context, 3);
        final TLRPC.TL_stickers_addStickerToSet tL_stickers_addStickerToSet = new TLRPC.TL_stickers_addStickerToSet();
        tL_stickers_addStickerToSet.stickerset = MediaDataController.getInputStickerSet(tL_messages_stickerSet.set);
        tL_stickers_addStickerToSet.sticker = MediaDataController.getInputStickerSetItem(document, str);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_stickers_addStickerToSet, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersDialogs.lambda$openStickerPickerDialog$10(AlertDialog.this, i, document, obj, tL_stickers_addStickerToSet, tLObject, tL_error);
            }
        });
        try {
            alertDialog.showDelayed(350L);
        } catch (Exception unused) {
        }
        return Boolean.TRUE;
    }

    public static void lambda$openStickerPickerDialog$8(TLObject tLObject, TLRPC.Document document) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
        int i = NotificationCenter.customStickerCreated;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationNameOnUIThread(i, bool, tLObject, document, null, bool);
    }

    public static void lambda$openStickerPickerDialog$9(AlertDialog alertDialog, final TLObject tLObject, int i, final TLRPC.Document document, TLRPC.TL_error tL_error, Object obj, TLRPC.TL_stickers_addStickerToSet tL_stickers_addStickerToSet) {
        alertDialog.dismiss();
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) tLObject;
            MediaDataController.getInstance(i).putStickerSet(tL_messages_stickerSet);
            if (!MediaDataController.getInstance(i).isStickerPackInstalled(tL_messages_stickerSet.set.id)) {
                MediaDataController.getInstance(i).toggleStickerSet(null, tLObject, 2, null, false, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StickersDialogs.lambda$openStickerPickerDialog$8(TLObject.this, document);
                }
            }, 250L);
            return;
        }
        if (tL_error != null) {
            if (FileRefController.isFileRefError(tL_error.text)) {
                FileRefController.getInstance(i).requestReference(obj, tL_stickers_addStickerToSet);
            } else {
                BulletinFactory.showError(tL_error);
            }
        }
    }

    public static void lambda$showAddStickerDialog$12(ActionBarPopupWindow actionBarPopupWindow, ArrayList arrayList, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        actionBarPopupWindow.dismiss();
        if (((Integer) arrayList.get(intValue)).intValue() == 1) {
            openStickerPickerDialog(tL_messages_stickerSet, baseFragment, resourcesProvider);
        } else {
            ((ChatActivity) baseFragment).openAttachMenuForCreatingSticker();
            ContentPreviewViewer.getInstance().setStickerSetForCustomSticker(tL_messages_stickerSet);
        }
    }

    public static void lambda$showDeleteForEveryOneDialog$5() {
    }

    public static void lambda$showDeleteForEveryOneDialog$6(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StickersDialogs.lambda$showDeleteForEveryOneDialog$5();
            }
        });
    }

    public static void lambda$showDeleteForEveryOneDialog$7(Runnable runnable, TLRPC.StickerSet stickerSet, DialogInterface dialogInterface, int i) {
        runnable.run();
        TLRPC.TL_stickers_deleteStickerSet tL_stickers_deleteStickerSet = new TLRPC.TL_stickers_deleteStickerSet();
        tL_stickers_deleteStickerSet.stickerset = MediaDataController.getInputStickerSet(stickerSet);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_stickers_deleteStickerSet, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersDialogs.lambda$showDeleteForEveryOneDialog$6(tLObject, tL_error);
            }
        });
    }

    public static CharSequence lambda$showNameEditorDialog$0(EditTextBoldCursor editTextBoldCursor, CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        return (charSequence.length() > 0 && Character.isWhitespace(charSequence.charAt(0)) && (TextUtils.isEmpty(editTextBoldCursor.getText()) || i3 == 0)) ? "" : charSequence;
    }

    public static void lambda$showNameEditorDialog$1(AlertDialog alertDialog, DialogInterface dialogInterface, EditTextBoldCursor editTextBoldCursor, Boolean bool) {
        alertDialog.dismiss();
        if (bool.booleanValue()) {
            dialogInterface.dismiss();
            return;
        }
        editTextBoldCursor.setErrorText(".");
        AndroidUtilities.shakeViewSpring(editTextBoldCursor, -6.0f);
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void lambda$showNameEditorDialog$2(final EditTextBoldCursor editTextBoldCursor, Utilities.Callback2 callback2, Context context, boolean z, final DialogInterface dialogInterface, int i) {
        String trim = editTextBoldCursor.getText().toString().trim();
        if (TextUtils.isEmpty(trim) || TextUtils.isEmpty(AndroidUtilities.translitSafe(trim.toString()))) {
            editTextBoldCursor.setErrorText(".");
            AndroidUtilities.shakeViewSpring(editTextBoldCursor, -6.0f);
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
            AndroidUtilities.showKeyboard(editTextBoldCursor);
            return;
        }
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        if (callback2 == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(context, 3, z ? null : new DarkThemeResourceProvider());
        alertDialog.showDelayed(250L);
        callback2.run(trim, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StickersDialogs.lambda$showNameEditorDialog$1(AlertDialog.this, dialogInterface, editTextBoldCursor, (Boolean) obj);
            }
        });
    }

    public static void lambda$showNameEditorDialog$3(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface, int i) {
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        dialogInterface.dismiss();
    }

    public static boolean lambda$showNameEditorDialog$4(AlertDialog alertDialog, TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        alertDialog.getButton(-1).callOnClick();
        return true;
    }

    private static void openStickerPickerDialog(final TLRPC.TL_messages_stickerSet tL_messages_stickerSet, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        final int i = UserConfig.selectedAccount;
        final Context context = baseFragment.getContext();
        EmojiBottomSheet emojiBottomSheet = new EmojiBottomSheet(context, true, resourcesProvider, false);
        emojiBottomSheet.whenDocumentSelected(new Utilities.Callback3Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3) {
                Boolean lambda$openStickerPickerDialog$11;
                lambda$openStickerPickerDialog$11 = StickersDialogs.lambda$openStickerPickerDialog$11(i, context, tL_messages_stickerSet, obj, (TLRPC.Document) obj2, (Boolean) obj3);
                return lambda$openStickerPickerDialog$11;
            }
        });
        if (baseFragment.visibleDialog != null) {
            emojiBottomSheet.show();
        } else {
            baseFragment.showDialog(emojiBottomSheet);
        }
    }

    public static void showAddStickerDialog(final TLRPC.TL_messages_stickerSet tL_messages_stickerSet, View view, final BaseFragment baseFragment, final Theme.ResourcesProvider resourcesProvider) {
        if (baseFragment == null) {
            return;
        }
        Context context = baseFragment.getContext();
        if (!(baseFragment instanceof ChatActivity)) {
            openStickerPickerDialog(tL_messages_stickerSet, baseFragment, resourcesProvider);
            return;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, R.drawable.popup_fixed_alert3, resourcesProvider, 0);
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(LocaleController.getString(R.string.StickersCreateNewSticker));
        arrayList3.add(Integer.valueOf(R.drawable.menu_sticker_add));
        arrayList2.add(0);
        arrayList.add(LocaleController.getString(R.string.StickersAddAnExistingSticker));
        arrayList3.add(Integer.valueOf(R.drawable.menu_sticker_select));
        arrayList2.add(1);
        final ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StickersDialogs.lambda$showAddStickerDialog$12(ActionBarPopupWindow.this, arrayList2, tL_messages_stickerSet, baseFragment, resourcesProvider, view2);
            }
        };
        for (int i = 0; i < arrayList.size(); i++) {
            ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, ((Integer) arrayList3.get(i)).intValue(), (CharSequence) arrayList.get(i), false, resourcesProvider);
            addItem.setTag(Integer.valueOf(i));
            addItem.setOnClickListener(onClickListener);
        }
        actionBarPopupWindow.setDismissAnimationDuration(100);
        actionBarPopupWindow.setScaleOut(true);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        actionBarPopupWindow.showAtLocation(view, 0, (iArr[0] + (view.getMeasuredWidth() / 2)) - (actionBarPopupWindowLayout.getMeasuredWidth() / 2), (iArr[1] + (view.getMeasuredHeight() / 2)) - (actionBarPopupWindowLayout.getMeasuredHeight() / 2));
        actionBarPopupWindow.dimBehind();
    }

    public static void showDeleteForEveryOneDialog(final TLRPC.StickerSet stickerSet, Theme.ResourcesProvider resourcesProvider, Context context, final Runnable runnable) {
        if (stickerSet == null) {
            return;
        }
        AlertDialog create = new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString(R.string.StickersDeleteStickerSetTitle)).setMessage(LocaleController.getString(R.string.StickersDeleteStickerSetDescription)).setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                StickersDialogs.lambda$showDeleteForEveryOneDialog$7(runnable, stickerSet, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        create.show();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_text_RedBold, resourcesProvider));
        }
    }

    public static void showNameEditorDialog(TLRPC.StickerSet stickerSet, Theme.ResourcesProvider resourcesProvider, final Context context, final Utilities.Callback2 callback2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        final boolean z = stickerSet != null;
        builder.setTitle(LocaleController.getString(z ? R.string.EditStickerPack : R.string.NewStickerPack));
        builder.setMessage(LocaleController.getString(R.string.StickersChooseNameForStickerPack));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(20.0f), 0);
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
            }
        };
        int i = Theme.key_dialogTextBlack;
        editTextBoldCursor.setTextColor(getThemedColor(i, resourcesProvider));
        editTextBoldCursor.setInputType(16385);
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(getThemedColor(i, resourcesProvider));
        editTextBoldCursor.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor, resourcesProvider));
        editTextBoldCursor.setHeaderHintColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setFocusable(true);
        editTextBoldCursor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50), new InputFilter() {
            @Override
            public final CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                CharSequence lambda$showNameEditorDialog$0;
                lambda$showNameEditorDialog$0 = StickersDialogs.lambda$showNameEditorDialog$0(EditTextBoldCursor.this, charSequence, i2, i3, spanned, i4, i5);
                return lambda$showNameEditorDialog$0;
            }
        }});
        editTextBoldCursor.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField, resourcesProvider), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider), getThemedColor(Theme.key_text_RedRegular, resourcesProvider));
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.requestFocus();
        editTextBoldCursor.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 0.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 28.0f), 0);
        frameLayout.addView(editTextBoldCursor);
        final NumberTextView numberTextView = new NumberTextView(context);
        numberTextView.setCenterAlign(true);
        numberTextView.setTextSize(15);
        numberTextView.setNumber(50, false);
        numberTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        numberTextView.setImportantForAccessibility(2);
        frameLayout.addView(numberTextView, LayoutHelper.createFrame(26, 20.0f, (LocaleController.isRTL ? 3 : 5) | 16, 0.0f, 2.0f, 4.0f, 0.0f));
        editTextBoldCursor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                NumberTextView.this.setNumber(50 - Character.codePointCount(editable, 0, editable.length()), true);
                editTextBoldCursor.setErrorText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        if (z) {
            editTextBoldCursor.setText(stickerSet.title);
            editTextBoldCursor.setSelection(stickerSet.title.length());
        }
        builder.setView(frameLayout);
        builder.setCustomViewOffset(4);
        builder.setPositiveButton(LocaleController.getString(z ? R.string.Done : R.string.Create), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                StickersDialogs.lambda$showNameEditorDialog$2(EditTextBoldCursor.this, callback2, context, z, dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                StickersDialogs.lambda$showNameEditorDialog$3(EditTextBoldCursor.this, dialogInterface, i2);
            }
        });
        final AlertDialog show = builder.show();
        show.setDismissDialogByButtons(false);
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                boolean lambda$showNameEditorDialog$4;
                lambda$showNameEditorDialog$4 = StickersDialogs.lambda$showNameEditorDialog$4(AlertDialog.this, textView, i2, keyEvent);
                return lambda$showNameEditorDialog$4;
            }
        });
    }
}
