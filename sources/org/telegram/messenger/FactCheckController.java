package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialogDecor;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.LaunchActivity;

public class FactCheckController {
    private static AlertDialog currentDialog;
    private boolean clearedExpiredInDatabase;
    public final int currentAccount;
    private static volatile FactCheckController[] Instance = new FactCheckController[4];
    private static final Object[] lockObjects = new Object[4];
    private final LongSparseArray<TLRPC.TL_factCheck> localCache = new LongSparseArray<>();
    private final LongSparseArray<HashMap<Key, Utilities.Callback<TLRPC.TL_factCheck>>> toload = new LongSparseArray<>();
    private final ArrayList<Key> loading = new ArrayList<>();
    private final Runnable loadMissingRunnable = new Runnable() {
        @Override
        public final void run() {
            FactCheckController.this.loadMissing();
        }
    };

    public static class Key {
        public final long dialogId;
        public final long hash;
        public final int messageId;

        private Key(long j, int i, long j2) {
            this.dialogId = j;
            this.messageId = i;
            this.hash = j2;
        }

        public static Key of(MessageObject messageObject) {
            TLRPC.Message message;
            if (messageObject == null || (message = messageObject.messageOwner) == null || message.factcheck == null) {
                return null;
            }
            return new Key(messageObject.getDialogId(), messageObject.getId(), messageObject.messageOwner.factcheck.hash);
        }

        public int hashCode() {
            return FactCheckController$Key$$ExternalSyntheticBackport0.m(this.hash);
        }
    }

    static {
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    private FactCheckController(int i) {
        this.currentAccount = i;
    }

    private void clearExpiredInDatabase() {
        if (this.clearedExpiredInDatabase) {
            return;
        }
        this.clearedExpiredInDatabase = true;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                FactCheckController.lambda$clearExpiredInDatabase$7(MessagesStorage.this);
            }
        });
    }

    private void getFromDatabase(final ArrayList<Key> arrayList, final Utilities.Callback<ArrayList<TLRPC.TL_factCheck>> callback) {
        if (callback == null) {
            return;
        }
        if (arrayList == null || arrayList.isEmpty()) {
            callback.run(new ArrayList<>());
        } else {
            final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FactCheckController.lambda$getFromDatabase$5(MessagesStorage.this, arrayList, callback);
                }
            });
        }
    }

    public static FactCheckController getInstance(int i) {
        FactCheckController factCheckController = Instance[i];
        if (factCheckController == null) {
            synchronized (lockObjects[i]) {
                try {
                    factCheckController = Instance[i];
                    if (factCheckController == null) {
                        FactCheckController[] factCheckControllerArr = Instance;
                        FactCheckController factCheckController2 = new FactCheckController(i);
                        factCheckControllerArr[i] = factCheckController2;
                        factCheckController = factCheckController2;
                    }
                } finally {
                }
            }
        }
        return factCheckController;
    }

    public void lambda$applyFactCheck$14(TLObject tLObject) {
        MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
    }

    public void lambda$applyFactCheck$15(final TLObject tLObject, TLRPC.TL_textWithEntities tL_textWithEntities, boolean z, AlertDialog alertDialog) {
        if (tLObject instanceof TLRPC.Updates) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    FactCheckController.this.lambda$applyFactCheck$14(tLObject);
                }
            });
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                boolean z2 = tL_textWithEntities == null || TextUtils.isEmpty(tL_textWithEntities.text);
                if (z2 || !z) {
                    BulletinFactory.of(safeLastFragment).createSimpleBulletin(z2 ? R.raw.ic_delete : R.raw.contact_check, LocaleController.getString(z2 ? R.string.FactCheckDeleted : R.string.FactCheckEdited)).show();
                }
            }
        }
        alertDialog.dismiss();
    }

    public void lambda$applyFactCheck$16(final TLRPC.TL_textWithEntities tL_textWithEntities, final boolean z, final AlertDialog alertDialog, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FactCheckController.this.lambda$applyFactCheck$15(tLObject, tL_textWithEntities, z, alertDialog);
            }
        });
    }

    public static void lambda$clearExpiredInDatabase$7(MessagesStorage messagesStorage) {
        try {
            messagesStorage.getDatabase().executeFast("DELETE FROM fact_checks WHERE expires > " + System.currentTimeMillis()).stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$getFactCheck$0(Key key, MessageObject messageObject, TLRPC.TL_factCheck tL_factCheck) {
        this.localCache.put(key.hash, tL_factCheck);
        messageObject.messageOwner.factcheck = tL_factCheck;
    }

    public static void lambda$getFromDatabase$5(MessagesStorage messagesStorage, ArrayList arrayList, final Utilities.Callback callback) {
        final ArrayList arrayList2 = new ArrayList();
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase database = messagesStorage.getDatabase();
                ArrayList arrayList3 = new ArrayList();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    arrayList3.add(Long.valueOf(((Key) it.next()).hash));
                    arrayList2.add(null);
                }
                sQLiteCursor = database.queryFinalized("SELECT data FROM fact_checks WHERE hash IN (" + TextUtils.join(", ", arrayList3) + ")", new Object[0]);
                while (true) {
                    if (!sQLiteCursor.next()) {
                        break;
                    }
                    NativeByteBuffer byteBufferValue = sQLiteCursor.byteBufferValue(0);
                    TLRPC.TL_factCheck TLdeserialize = TLRPC.TL_factCheck.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                    if (TLdeserialize != null) {
                        int i = -1;
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            if (TLdeserialize.hash == ((Key) arrayList.get(i2)).hash) {
                                i = i2;
                            }
                        }
                        if (i >= 0 && i < arrayList2.size()) {
                            arrayList2.set(i, TLdeserialize);
                        }
                    }
                }
                sQLiteCursor.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLiteCursor != null) {
                    sQLiteCursor.dispose();
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(arrayList2);
                }
            });
        } catch (Throwable th) {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
            throw th;
        }
    }

    public void lambda$loadMissing$1(TLObject tLObject, TLRPC.TL_getFactCheck tL_getFactCheck, ArrayList arrayList, HashMap hashMap) {
        ArrayList arrayList2 = new ArrayList();
        if (tLObject instanceof Vector) {
            ArrayList<T> arrayList3 = ((Vector) tLObject).objects;
            for (int i = 0; i < arrayList3.size(); i++) {
                if (arrayList3.get(i) instanceof TLRPC.TL_factCheck) {
                    arrayList2.add((TLRPC.TL_factCheck) arrayList3.get(i));
                }
            }
        }
        HashMap hashMap2 = new HashMap();
        for (int i2 = 0; i2 < Math.min(tL_getFactCheck.msg_id.size(), arrayList2.size()); i2++) {
            Integer num = tL_getFactCheck.msg_id.get(i2);
            num.intValue();
            hashMap2.put(num, (TLRPC.TL_factCheck) arrayList2.get(i2));
        }
        int i3 = 0;
        for (int i4 = 0; i4 < tL_getFactCheck.msg_id.size(); i4++) {
            Key key = (Key) arrayList.get(i4);
            Integer num2 = tL_getFactCheck.msg_id.get(i4);
            num2.intValue();
            TLRPC.TL_factCheck tL_factCheck = (TLRPC.TL_factCheck) hashMap2.get(num2);
            Utilities.Callback callback = (Utilities.Callback) hashMap.get(key);
            if (tL_factCheck != null && !tL_factCheck.need_check && callback != null) {
                callback.run(tL_factCheck);
                i3++;
                this.loading.remove(key);
            }
        }
        if (i3 > 0) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.factCheckLoaded, new Object[0]);
        }
    }

    public void lambda$loadMissing$2(final TLRPC.TL_getFactCheck tL_getFactCheck, final ArrayList arrayList, final HashMap hashMap, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FactCheckController.this.lambda$loadMissing$1(tLObject, tL_getFactCheck, arrayList, hashMap);
            }
        });
    }

    public void lambda$loadMissing$3(long j, ArrayList arrayList, final HashMap hashMap, ArrayList arrayList2) {
        final TLRPC.TL_getFactCheck tL_getFactCheck = new TLRPC.TL_getFactCheck();
        tL_getFactCheck.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        final ArrayList arrayList3 = new ArrayList();
        int i = 0;
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            Key key = (Key) arrayList.get(i2);
            TLRPC.TL_factCheck tL_factCheck = (TLRPC.TL_factCheck) arrayList2.get(i2);
            if (tL_factCheck == null) {
                arrayList3.add(key);
                tL_getFactCheck.msg_id.add(Integer.valueOf(key.messageId));
            } else {
                this.loading.remove(key);
                Utilities.Callback callback = (Utilities.Callback) hashMap.get(key);
                if (callback != null) {
                    callback.run(tL_factCheck);
                    i++;
                }
            }
        }
        if (i > 0) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.factCheckLoaded, new Object[0]);
        }
        if (tL_getFactCheck.msg_id.isEmpty()) {
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getFactCheck, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FactCheckController.this.lambda$loadMissing$2(tL_getFactCheck, arrayList3, hashMap, tLObject, tL_error);
            }
        });
    }

    public static void lambda$openFactCheckEditor$10(View view, DialogInterface dialogInterface) {
        currentDialog = null;
        view.requestFocus();
    }

    public static void lambda$openFactCheckEditor$11(EditTextCaption editTextCaption, DialogInterface dialogInterface) {
        editTextCaption.requestFocus();
        AndroidUtilities.showKeyboard(editTextCaption);
    }

    public static void lambda$openFactCheckEditor$13(EditTextCaption editTextCaption, DialogInterface dialogInterface) {
        editTextCaption.requestFocus();
        AndroidUtilities.showKeyboard(editTextCaption);
    }

    public void lambda$openFactCheckEditor$8(EditTextCaption editTextCaption, int i, MessageObject messageObject, boolean z, DialogInterface dialogInterface, int i2) {
        if (editTextCaption.getText().toString().length() > i) {
            AndroidUtilities.shakeView(editTextCaption);
            return;
        }
        TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
        CharSequence[] charSequenceArr = {editTextCaption.getText()};
        tL_textWithEntities.entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, true);
        CharSequence charSequence = charSequenceArr[0];
        tL_textWithEntities.text = charSequence == null ? "" : charSequence.toString();
        applyFactCheck(messageObject, tL_textWithEntities, z);
        dialogInterface.dismiss();
    }

    public static void lambda$saveToDatabase$6(MessagesStorage messagesStorage, TLRPC.TL_factCheck tL_factCheck) {
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                sQLitePreparedStatement = messagesStorage.getDatabase().executeFast("REPLACE INTO fact_checks VALUES(?, ?, ?)");
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, tL_factCheck.hash);
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_factCheck.getObjectSize());
                tL_factCheck.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatement.bindByteBuffer(2, nativeByteBuffer);
                sQLitePreparedStatement.bindLong(3, System.currentTimeMillis() + 889032704);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement != null) {
                    sQLitePreparedStatement.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void loadMissing() {
        while (this.toload.size() > 0) {
            final long keyAt = this.toload.keyAt(0);
            final HashMap<Key, Utilities.Callback<TLRPC.TL_factCheck>> valueAt = this.toload.valueAt(0);
            this.toload.removeAt(0);
            final ArrayList<Key> arrayList = new ArrayList<>(valueAt.keySet());
            this.loading.addAll(arrayList);
            getFromDatabase(arrayList, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    FactCheckController.this.lambda$loadMissing$3(keyAt, arrayList, valueAt, (ArrayList) obj);
                }
            });
        }
        this.toload.clear();
    }

    private void saveToDatabase(final TLRPC.TL_factCheck tL_factCheck) {
        if (tL_factCheck == null) {
            return;
        }
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                FactCheckController.lambda$saveToDatabase$6(MessagesStorage.this, tL_factCheck);
            }
        });
        clearExpiredInDatabase();
    }

    private void scheduleLoadMissing() {
        AndroidUtilities.cancelRunOnUIThread(this.loadMissingRunnable);
        AndroidUtilities.runOnUIThread(this.loadMissingRunnable, 80L);
    }

    public void applyFactCheck(MessageObject messageObject, final TLRPC.TL_textWithEntities tL_textWithEntities, final boolean z) {
        TLRPC.TL_deleteFactCheck tL_deleteFactCheck;
        if (tL_textWithEntities != null && !TextUtils.isEmpty(tL_textWithEntities.text)) {
            TLRPC.TL_editFactCheck tL_editFactCheck = new TLRPC.TL_editFactCheck();
            tL_editFactCheck.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(messageObject.getDialogId());
            tL_editFactCheck.msg_id = messageObject.getId();
            tL_editFactCheck.text = tL_textWithEntities;
            tL_deleteFactCheck = tL_editFactCheck;
        } else {
            if (z) {
                return;
            }
            TLRPC.TL_deleteFactCheck tL_deleteFactCheck2 = new TLRPC.TL_deleteFactCheck();
            tL_deleteFactCheck2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(messageObject.getDialogId());
            tL_deleteFactCheck2.msg_id = messageObject.getId();
            tL_deleteFactCheck = tL_deleteFactCheck2;
        }
        Context context = LaunchActivity.instance;
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        final AlertDialog alertDialog = new AlertDialog(context, 3);
        alertDialog.showDelayed(320L);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_deleteFactCheck, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FactCheckController.this.lambda$applyFactCheck$16(tL_textWithEntities, z, alertDialog, tLObject, tL_error);
            }
        });
    }

    public TLRPC.TL_factCheck getFactCheck(final MessageObject messageObject) {
        TLRPC.Message message;
        TLRPC.TL_factCheck tL_factCheck;
        if (messageObject == null || (message = messageObject.messageOwner) == null || (tL_factCheck = message.factcheck) == null) {
            return null;
        }
        if (tL_factCheck.need_check) {
            final Key of = Key.of(messageObject);
            if (of == null || of.messageId < 0) {
                return null;
            }
            TLRPC.TL_factCheck tL_factCheck2 = this.localCache.get(of.hash);
            if (tL_factCheck2 != null) {
                messageObject.messageOwner.factcheck = tL_factCheck2;
                return tL_factCheck2;
            }
            if (!this.loading.contains(of)) {
                HashMap<Key, Utilities.Callback<TLRPC.TL_factCheck>> hashMap = this.toload.get(of.dialogId);
                if (hashMap == null) {
                    LongSparseArray<HashMap<Key, Utilities.Callback<TLRPC.TL_factCheck>>> longSparseArray = this.toload;
                    long j = of.dialogId;
                    HashMap<Key, Utilities.Callback<TLRPC.TL_factCheck>> hashMap2 = new HashMap<>();
                    longSparseArray.put(j, hashMap2);
                    hashMap = hashMap2;
                }
                if (!hashMap.containsKey(of)) {
                    hashMap.put(of, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            FactCheckController.this.lambda$getFactCheck$0(of, messageObject, (TLRPC.TL_factCheck) obj);
                        }
                    });
                    scheduleLoadMissing();
                }
            }
        } else if (this.localCache.get(tL_factCheck.hash) == null) {
            LongSparseArray<TLRPC.TL_factCheck> longSparseArray2 = this.localCache;
            TLRPC.TL_factCheck tL_factCheck3 = messageObject.messageOwner.factcheck;
            longSparseArray2.put(tL_factCheck3.hash, tL_factCheck3);
            saveToDatabase(messageObject.messageOwner.factcheck);
        }
        return messageObject.messageOwner.factcheck;
    }

    public void openFactCheckEditor(Context context, final Theme.ResourcesProvider resourcesProvider, final MessageObject messageObject, boolean z) {
        ?? r1;
        TLRPC.TL_textWithEntities tL_textWithEntities;
        TLRPC.Message message;
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        Activity findActivity = AndroidUtilities.findActivity(context);
        final View currentFocus = findActivity != null ? findActivity.getCurrentFocus() : null;
        boolean z2 = lastFragment != null && (lastFragment.getFragmentView() instanceof SizeNotifierFrameLayout) && ((SizeNotifierFrameLayout) lastFragment.getFragmentView()).measureKeyboardHeight() > AndroidUtilities.dp(20.0f) && !z;
        final AlertDialog[] alertDialogArr = new AlertDialog[1];
        AlertDialog.Builder builder = z2 ? new AlertDialogDecor.Builder(context, resourcesProvider) : new AlertDialog.Builder(context, resourcesProvider);
        final TextView[] textViewArr = new TextView[1];
        boolean z3 = messageObject == null || (message = messageObject.messageOwner) == null || message.factcheck == null;
        builder.setTitle(LocaleController.getString(R.string.FactCheckDialog));
        final int i = MessagesController.getInstance(this.currentAccount).factcheckLengthLimit;
        final EditTextCaption editTextCaption = new EditTextCaption(context, resourcesProvider) {
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
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                int i2 = R.id.menu_bold;
                if (menu.findItem(i2) != null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    menu.removeItem(16908341);
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.Bold));
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableStringBuilder.length(), 33);
                int i3 = R.id.menu_groupbolditalic;
                menu.add(i3, i2, 6, spannableStringBuilder);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(LocaleController.getString(R.string.Italic));
                spannableStringBuilder2.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf")), 0, spannableStringBuilder2.length(), 33);
                menu.add(i3, R.id.menu_italic, 7, spannableStringBuilder2);
                menu.add(i3, R.id.menu_link, 8, LocaleController.getString(R.string.CreateLink));
                menu.add(i3, R.id.menu_regular, 9, LocaleController.getString(R.string.Regular));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                super.onTextChanged(charSequence, i2, i3, i4);
                if (this.limit != null) {
                    this.limitCount = i - charSequence.length();
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
        editTextCaption.lineYFix = true;
        final boolean z4 = z3;
        final View view = currentFocus;
        editTextCaption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                if (i2 != 6) {
                    return false;
                }
                if (editTextCaption.getText().toString().length() > i) {
                    AndroidUtilities.shakeView(editTextCaption);
                    return true;
                }
                TLRPC.TL_textWithEntities tL_textWithEntities2 = new TLRPC.TL_textWithEntities();
                CharSequence[] charSequenceArr = {editTextCaption.getText()};
                tL_textWithEntities2.entities = MediaDataController.getInstance(FactCheckController.this.currentAccount).getEntities(charSequenceArr, true);
                CharSequence charSequence = charSequenceArr[0];
                tL_textWithEntities2.text = charSequence == null ? "" : charSequence.toString();
                FactCheckController.this.applyFactCheck(messageObject, tL_textWithEntities2, z4);
                AlertDialog alertDialog = alertDialogArr[0];
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                if (alertDialogArr[0] == FactCheckController.currentDialog) {
                    AlertDialog unused = FactCheckController.currentDialog = null;
                }
                View view2 = view;
                if (view2 != null) {
                    view2.requestFocus();
                }
                return true;
            }
        });
        MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(AndroidUtilities.getCurrentKeyboardLanguage(), true);
        editTextCaption.setTextSize(1, 18.0f);
        editTextCaption.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        editTextCaption.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourcesProvider));
        editTextCaption.setHintText(LocaleController.getString(R.string.FactCheckPlaceholder));
        editTextCaption.setFocusable(true);
        editTextCaption.setInputType(147457);
        editTextCaption.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider), Theme.getColor(Theme.key_text_RedRegular, resourcesProvider));
        editTextCaption.setImeOptions(6);
        editTextCaption.setBackgroundDrawable(null);
        editTextCaption.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f));
        final TLRPC.TL_factCheck factCheck = messageObject.getFactCheck();
        if (factCheck != null && (tL_textWithEntities = factCheck.text) != null) {
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(tL_textWithEntities.text);
            MessageObject.addEntitiesToText(valueOf, factCheck.text.entities, false, true, false, false);
            editTextCaption.setText(valueOf);
        }
        editTextCaption.addTextChangedListener(new TextWatcher() {
            boolean ignoreTextChange;

            @Override
            public void afterTextChanged(Editable editable) {
                if (this.ignoreTextChange) {
                    return;
                }
                int length = editable.length();
                int i2 = i;
                boolean z5 = true;
                if (length > i2) {
                    this.ignoreTextChange = true;
                    editable.delete(i2, editable.length());
                    AndroidUtilities.shakeView(editTextCaption);
                    try {
                        editTextCaption.performHapticFeedback(3, 2);
                    } catch (Exception unused) {
                    }
                    this.ignoreTextChange = false;
                }
                if (textViewArr[0] != null) {
                    if (editable.length() <= 0 && factCheck != null) {
                        z5 = false;
                    }
                    textViewArr[0].setText(LocaleController.getString(z5 ? R.string.Done : R.string.Remove));
                    textViewArr[0].setTextColor(Theme.getColor(z5 ? Theme.key_dialogButton : Theme.key_text_RedBold));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.addView(editTextCaption, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 10.0f));
        builder.makeCustomMaxHeight();
        builder.setView(linearLayout);
        builder.setWidth(AndroidUtilities.dp(292.0f));
        final boolean z5 = z3;
        builder.setPositiveButton(LocaleController.getString(R.string.Done), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                FactCheckController.this.lambda$openFactCheckEditor$8(editTextCaption, i, messageObject, z5, dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        if (z2) {
            currentDialog = create;
            alertDialogArr[0] = create;
            create.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    FactCheckController.lambda$openFactCheckEditor$10(currentFocus, dialogInterface);
                }
            });
            currentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public final void onShow(DialogInterface dialogInterface) {
                    FactCheckController.lambda$openFactCheckEditor$11(EditTextCaption.this, dialogInterface);
                }
            });
            currentDialog.showDelayed(250L);
            r1 = 0;
        } else {
            r1 = 0;
            alertDialogArr[0] = create;
            create.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    AndroidUtilities.hideKeyboard(EditTextCaption.this);
                }
            });
            alertDialogArr[0].setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public final void onShow(DialogInterface dialogInterface) {
                    FactCheckController.lambda$openFactCheckEditor$13(EditTextCaption.this, dialogInterface);
                }
            });
            alertDialogArr[0].show();
        }
        alertDialogArr[r1].setDismissDialogByButtons(r1);
        View button = alertDialogArr[r1].getButton(-1);
        if (button instanceof TextView) {
            textViewArr[r1] = (TextView) button;
        }
        editTextCaption.setSelection(editTextCaption.getText().length());
    }
}
