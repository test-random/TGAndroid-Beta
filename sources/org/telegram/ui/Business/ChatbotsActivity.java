package org.telegram.ui.Business;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Business.ChatbotsActivity;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class ChatbotsActivity extends BaseFragment {
    public TL_account.TL_connectedBot currentBot;
    public TL_account.connectedBots currentValue;
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private EditTextBoldCursor editText;
    private FrameLayout editTextContainer;
    private View editTextDivider;
    private FrameLayout emptyView;
    private ImageView emptyViewLoading;
    private TextView emptyViewText;
    public boolean exclude;
    private SpannableStringBuilder introText;
    private String lastQuery;
    private UniversalRecyclerView listView;
    private boolean loading;
    private BusinessRecipientsHelper recipientsHelper;
    private boolean scheduledLoading;
    private SearchAdapterHelper searchHelper;
    private boolean valueSet;
    private boolean wasLoading;
    private int searchId = 0;
    private Runnable search = new Runnable() {
        @Override
        public final void run() {
            ChatbotsActivity.this.lambda$new$3();
        }
    };
    public boolean allowReply = true;
    private TLRPC.User selectedBot = null;
    private LongSparseArray foundBots = new LongSparseArray();

    public class AnonymousClass5 implements SearchAdapterHelper.SearchAdapterHelperDelegate {
        AnonymousClass5() {
        }

        public void lambda$onDataSetChanged$0() {
            ChatbotsActivity.this.listView.adapter.update(true);
            ChatbotsActivity.this.updateSearchLoading();
        }

        @Override
        public boolean canApplySearchResults(int i) {
            return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
        }

        @Override
        public androidx.collection.LongSparseArray getExcludeCallParticipants() {
            return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
        }

        @Override
        public androidx.collection.LongSparseArray getExcludeUsers() {
            return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
        }

        @Override
        public void onDataSetChanged(int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatbotsActivity.AnonymousClass5.this.lambda$onDataSetChanged$0();
                }
            });
        }

        @Override
        public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
            SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
        }
    }

    private void checkDone(boolean z) {
        if (this.doneButton == null) {
            return;
        }
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        if (z) {
            this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f).scaleY(hasChanges ? 1.0f : 0.0f).setDuration(180L).start();
            return;
        }
        this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleY(hasChanges ? 1.0f : 0.0f);
    }

    public void clear(View view) {
        this.selectedBot = null;
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(this.introText, "RestrictedEmoji", "🤖"));
        TLRPC.User user = this.selectedBot;
        if (user != null) {
            arrayList.add(UItem.asAddChat(Long.valueOf(user.id)).setChecked(true).setCloseIcon(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ChatbotsActivity.this.clear(view);
                }
            }));
        } else {
            universalAdapter.whiteSectionStart();
            arrayList.add(UItem.asCustom(this.editTextContainer));
            this.foundBots.clear();
            boolean z = false;
            for (int i = 0; i < this.searchHelper.getLocalServerSearch().size(); i++) {
                TLObject tLObject = (TLObject) this.searchHelper.getLocalServerSearch().get(i);
                if (tLObject instanceof TLRPC.User) {
                    TLRPC.User user2 = (TLRPC.User) tLObject;
                    if (user2.bot) {
                        arrayList.add(UItem.asAddChat(Long.valueOf(user2.id)));
                        this.foundBots.put(user2.id, user2);
                        z = true;
                    }
                }
            }
            for (int i2 = 0; i2 < this.searchHelper.getGlobalSearch().size(); i2++) {
                TLObject tLObject2 = (TLObject) this.searchHelper.getGlobalSearch().get(i2);
                if (tLObject2 instanceof TLRPC.User) {
                    TLRPC.User user3 = (TLRPC.User) tLObject2;
                    if (user3.bot) {
                        arrayList.add(UItem.asAddChat(Long.valueOf(user3.id)));
                        this.foundBots.put(user3.id, user3);
                        z = true;
                    }
                }
            }
            if (this.foundBots.size() <= 0 && (!TextUtils.isEmpty(this.editText.getText().toString()) || this.searchHelper.isSearchInProgress() || this.scheduledLoading)) {
                arrayList.add(UItem.asCustom(this.emptyView));
                z = true;
            }
            this.editTextDivider.setVisibility(z ? 0 : 8);
            universalAdapter.whiteSectionEnd();
        }
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessBotLinkInfo)));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BusinessBotChats)));
        arrayList.add(UItem.asRadio(-1, LocaleController.getString(R.string.BusinessChatsAllPrivateExcept)).setChecked(this.exclude));
        arrayList.add(UItem.asRadio(-2, LocaleController.getString(R.string.BusinessChatsOnlySelected)).setChecked(!this.exclude));
        arrayList.add(UItem.asShadow(null));
        this.recipientsHelper.fillItems(arrayList);
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessBotChatsInfo)));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BusinessBotPermissions)));
        arrayList.add(UItem.asCheck(-5, LocaleController.getString(R.string.BusinessBotPermissionsReply)).setChecked(this.allowReply));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessBotPermissionsInfo)));
        arrayList.add(UItem.asShadow(null));
    }

    public boolean lambda$createView$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.scheduledLoading = false;
        AndroidUtilities.cancelRunOnUIThread(this.search);
        if (TextUtils.isEmpty(this.editText.getText())) {
            this.lastQuery = null;
            this.searchHelper.clear();
            this.listView.adapter.update(true);
        } else {
            AndroidUtilities.runOnUIThread(this.search);
        }
        updateSearchLoading();
        return true;
    }

    public void lambda$createView$1() {
        Browser.openUrl(getContext(), LocaleController.getString(R.string.BusinessBotsInfoLink));
    }

    public void lambda$createView$2() {
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void lambda$new$3() {
        String obj = this.editText.getText().toString();
        String str = this.lastQuery;
        if (str == null || !TextUtils.equals(str, obj)) {
            this.scheduledLoading = false;
            if (TextUtils.isEmpty(obj)) {
                this.lastQuery = null;
                this.searchHelper.clear();
                this.listView.adapter.update(true);
            } else {
                SearchAdapterHelper searchAdapterHelper = this.searchHelper;
                this.lastQuery = obj;
                int i = this.searchId;
                this.searchId = i + 1;
                searchAdapterHelper.queryServerSearch(obj, true, false, true, false, false, 0L, false, 0, i, 0L);
            }
        }
    }

    public void lambda$onBackPressed$7(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public void lambda$onBackPressed$8(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    public void lambda$processDone$4(TLRPC.TL_error tL_error, TLObject tLObject, int[] iArr, ArrayList arrayList) {
        if (tL_error != null) {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.showError(tL_error);
            return;
        }
        if (tLObject instanceof TLRPC.TL_boolFalse) {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
            return;
        }
        int i = iArr[0] + 1;
        iArr[0] = i;
        if (i == arrayList.size()) {
            BusinessChatbotController.getInstance(this.currentAccount).invalidate(true);
            getMessagesController().clearFullUsers();
            lambda$onBackPressed$321();
        }
    }

    public void lambda$processDone$5(final int[] iArr, final ArrayList arrayList, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatbotsActivity.this.lambda$processDone$4(tL_error, tLObject, iArr, arrayList);
            }
        });
    }

    public void lambda$setValue$6(TL_account.connectedBots connectedbots) {
        UniversalAdapter universalAdapter;
        this.currentValue = connectedbots;
        TL_account.TL_connectedBot tL_connectedBot = (connectedbots == null || connectedbots.connected_bots.isEmpty()) ? null : this.currentValue.connected_bots.get(0);
        this.currentBot = tL_connectedBot;
        this.selectedBot = tL_connectedBot == null ? null : getMessagesController().getUser(Long.valueOf(this.currentBot.bot_id));
        TL_account.TL_connectedBot tL_connectedBot2 = this.currentBot;
        this.allowReply = tL_connectedBot2 != null ? tL_connectedBot2.can_reply : true;
        this.exclude = tL_connectedBot2 != null ? tL_connectedBot2.recipients.exclude_selected : true;
        BusinessRecipientsHelper businessRecipientsHelper = this.recipientsHelper;
        if (businessRecipientsHelper != null) {
            businessRecipientsHelper.setValue(tL_connectedBot2 != null ? tL_connectedBot2.recipients : null);
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        checkDone(true);
        this.valueSet = true;
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        TLRPC.User user;
        if (this.recipientsHelper.onClick(uItem)) {
            return;
        }
        int i2 = uItem.id;
        if (i2 == -1) {
            BusinessRecipientsHelper businessRecipientsHelper = this.recipientsHelper;
            this.exclude = true;
            businessRecipientsHelper.setExclude(true);
        } else if (i2 == -2) {
            BusinessRecipientsHelper businessRecipientsHelper2 = this.recipientsHelper;
            this.exclude = false;
            businessRecipientsHelper2.setExclude(false);
        } else {
            if (i2 == -5) {
                boolean z = !this.allowReply;
                this.allowReply = z;
                ((TextCheckCell) view).setChecked(z);
                checkDone(true);
            }
            if (i2 == -6) {
                this.selectedBot = null;
            } else {
                if (uItem.viewType != 13 || (user = (TLRPC.User) this.foundBots.get(uItem.dialogId)) == null) {
                    return;
                }
                if (!user.bot_business) {
                    showDialog(new AlertDialog.Builder(getContext(), this.resourceProvider).setTitle(LocaleController.getString(R.string.BusinessBotNotSupportedTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.BusinessBotNotSupportedMessage))).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
                    return;
                } else {
                    this.selectedBot = user;
                    AndroidUtilities.hideKeyboard(this.editText);
                }
            }
        }
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void processDone() {
        TLRPC.User user;
        if (this.doneButtonDrawable.getProgress() > 0.0f) {
            return;
        }
        if (!hasChanges()) {
            lambda$onBackPressed$321();
            return;
        }
        if (this.recipientsHelper.validate(this.listView)) {
            final ArrayList arrayList = new ArrayList();
            TL_account.TL_connectedBot tL_connectedBot = this.currentBot;
            if (tL_connectedBot != null && ((user = this.selectedBot) == null || tL_connectedBot.bot_id != user.id)) {
                TL_account.updateConnectedBot updateconnectedbot = new TL_account.updateConnectedBot();
                updateconnectedbot.deleted = true;
                updateconnectedbot.bot = getMessagesController().getInputUser(this.currentBot.bot_id);
                updateconnectedbot.recipients = new TL_account.TL_inputBusinessBotRecipients();
                arrayList.add(updateconnectedbot);
            }
            if (this.selectedBot != null) {
                TL_account.updateConnectedBot updateconnectedbot2 = new TL_account.updateConnectedBot();
                updateconnectedbot2.deleted = false;
                updateconnectedbot2.can_reply = this.allowReply;
                updateconnectedbot2.bot = getMessagesController().getInputUser(this.selectedBot);
                updateconnectedbot2.recipients = this.recipientsHelper.getBotInputValue();
                arrayList.add(updateconnectedbot2);
                TL_account.TL_connectedBot tL_connectedBot2 = this.currentBot;
                if (tL_connectedBot2 != null) {
                    tL_connectedBot2.bot_id = this.selectedBot.id;
                    tL_connectedBot2.recipients = this.recipientsHelper.getBotValue();
                    this.currentBot.can_reply = this.allowReply;
                }
            }
            if (arrayList.isEmpty()) {
                lambda$onBackPressed$321();
                return;
            }
            final int[] iArr = {0};
            for (int i = 0; i < arrayList.size(); i++) {
                getConnectionsManager().sendRequest((TLObject) arrayList.get(i), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChatbotsActivity.this.lambda$processDone$5(iArr, arrayList, tLObject, tL_error);
                    }
                });
            }
        }
    }

    public void scheduleSearch() {
        this.scheduledLoading = false;
        AndroidUtilities.cancelRunOnUIThread(this.search);
        if (TextUtils.isEmpty(this.editText.getText())) {
            this.lastQuery = null;
            this.searchHelper.clear();
        } else {
            this.scheduledLoading = true;
            AndroidUtilities.runOnUIThread(this.search, 800L);
        }
        this.listView.adapter.update(true);
        updateSearchLoading();
    }

    private void setValue() {
        if (this.loading || this.valueSet) {
            return;
        }
        this.loading = true;
        BusinessChatbotController.getInstance(this.currentAccount).load(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChatbotsActivity.this.lambda$setValue$6((TL_account.connectedBots) obj);
            }
        });
    }

    public void updateSearchLoading() {
        boolean z = true;
        if (this.wasLoading != (this.searchHelper.isSearchInProgress() || this.scheduledLoading || this.foundBots.size() > 0)) {
            if (!this.searchHelper.isSearchInProgress() && !this.scheduledLoading && this.foundBots.size() <= 0) {
                z = false;
            }
            this.wasLoading = z;
            ViewPropertyAnimator duration = this.emptyViewText.animate().alpha(z ? 0.0f : 1.0f).translationY(z ? -AndroidUtilities.dp(8.0f) : 0.0f).setDuration(320L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            this.emptyViewLoading.animate().alpha(z ? 1.0f : 0.0f).translationY(z ? 0.0f : AndroidUtilities.dp(8.0f)).setDuration(320L).setInterpolator(cubicBezierInterpolator).start();
        }
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.BusinessBots));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    if (ChatbotsActivity.this.onBackPressed()) {
                        ChatbotsActivity.this.lambda$onBackPressed$321();
                    }
                } else if (i == 1) {
                    ChatbotsActivity.this.processDone();
                }
            }
        });
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
        int i = Theme.key_actionBarDefaultIcon;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.MULTIPLY));
        this.doneButtonDrawable = new CrossfadeDrawable(mutate, new CircularProgressDrawable(Theme.getColor(i)));
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, this.doneButtonDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString(R.string.Done));
        checkDone(false);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        new LinearLayout(getContext()).setOrientation(0);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext());
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 17.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        EditTextBoldCursor editTextBoldCursor2 = this.editText;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor2.setTextColor(Theme.getColor(i2));
        this.editText.setBackgroundDrawable(null);
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.editText.setInputType(180224);
        this.editText.setImeOptions(6);
        this.editText.setHint(LocaleController.getString(R.string.BusinessBotLink));
        this.editText.setCursorColor(Theme.getColor(i2));
        this.editText.setCursorSize(AndroidUtilities.dp(19.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = ChatbotsActivity.this.lambda$createView$0(textView, i3, keyEvent);
                return lambda$createView$0;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                ChatbotsActivity.this.scheduleSearch();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.editTextContainer = frameLayout2;
        frameLayout2.addView(this.editText, LayoutHelper.createFrame(-1, -1.0f, 48, 21.0f, 15.0f, 21.0f, 15.0f));
        FrameLayout frameLayout3 = this.editTextContainer;
        int i3 = Theme.key_windowBackgroundWhite;
        frameLayout3.setBackgroundColor(getThemedColor(i3));
        View view = new View(context);
        this.editTextDivider = view;
        view.setBackgroundColor(getThemedColor(Theme.key_divider));
        FrameLayout frameLayout4 = this.editTextContainer;
        View view2 = this.editTextDivider;
        float f = 1.0f / AndroidUtilities.density;
        boolean z = LocaleController.isRTL;
        frameLayout4.addView(view2, LayoutHelper.createFrame(-1, f, 87, z ? 0 : 21, 0.0f, z ? 21 : 0, 0.0f));
        FrameLayout frameLayout5 = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i4, int i5) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i4), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), 1073741824));
            }
        };
        this.emptyView = frameLayout5;
        frameLayout5.setBackgroundColor(getThemedColor(i3));
        TextView textView = new TextView(context);
        this.emptyViewText = textView;
        textView.setText(LocaleController.getString(R.string.BusinessBotNotFound));
        this.emptyViewText.setTextSize(1, 14.0f);
        TextView textView2 = this.emptyViewText;
        int i4 = Theme.key_windowBackgroundWhiteGrayText2;
        textView2.setTextColor(getThemedColor(i4));
        this.emptyView.addView(this.emptyViewText, LayoutHelper.createFrame(-2, -2, 17));
        this.emptyViewLoading = new ImageView(context);
        this.emptyViewLoading.setImageDrawable(new CircularProgressDrawable(getThemedColor(i4)) {
            @Override
            public int getIntrinsicHeight() {
                return (int) (this.size + (this.thickness * 2.0f));
            }

            @Override
            public int getIntrinsicWidth() {
                return (int) (this.size + (this.thickness * 2.0f));
            }
        });
        this.emptyView.addView(this.emptyViewLoading, LayoutHelper.createFrame(-2, -2, 17));
        this.emptyViewLoading.setAlpha(0.0f);
        this.emptyViewLoading.setTranslationY(AndroidUtilities.dp(8.0f));
        SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.BusinessBotsInfo), new Runnable() {
            @Override
            public final void run() {
                ChatbotsActivity.this.lambda$createView$1();
            }
        });
        this.introText = replaceSingleTag;
        int indexOf = replaceSingleTag.toString().indexOf(">");
        if (indexOf >= 0) {
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.arrow_newchat);
            coloredImageSpan.setColorKey(Theme.key_chat_messageLinkIn);
            this.introText.setSpan(coloredImageSpan, indexOf, indexOf + 1, 33);
        }
        SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchHelper = searchAdapterHelper;
        searchAdapterHelper.setDelegate(new AnonymousClass5());
        BusinessRecipientsHelper businessRecipientsHelper = new BusinessRecipientsHelper(this, new Runnable() {
            @Override
            public final void run() {
                ChatbotsActivity.this.lambda$createView$2();
            }
        });
        this.recipientsHelper = businessRecipientsHelper;
        TL_account.TL_connectedBot tL_connectedBot = this.currentBot;
        businessRecipientsHelper.setValue(tL_connectedBot == null ? null : tL_connectedBot.recipients);
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ChatbotsActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                ChatbotsActivity.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        frameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    public boolean hasChanges() {
        if (!this.valueSet) {
            return false;
        }
        TLRPC.User user = this.selectedBot;
        boolean z = user != null;
        TL_account.TL_connectedBot tL_connectedBot = this.currentBot;
        if (z != (tL_connectedBot != null)) {
            return true;
        }
        if ((user == null ? 0L : user.id) != (tL_connectedBot != null ? tL_connectedBot.bot_id : 0L)) {
            return true;
        }
        if (user != null) {
            if (this.allowReply != tL_connectedBot.can_reply) {
                return true;
            }
            BusinessRecipientsHelper businessRecipientsHelper = this.recipientsHelper;
            if (businessRecipientsHelper != null && businessRecipientsHelper.hasChanges()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (!hasChanges()) {
            return super.onBackPressed();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.UnsavedChanges));
        builder.setMessage(LocaleController.getString(R.string.BusinessBotUnsavedChanges));
        builder.setPositiveButton(LocaleController.getString(R.string.ApplyTheme), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatbotsActivity.this.lambda$onBackPressed$7(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatbotsActivity.this.lambda$onBackPressed$8(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        setValue();
        return super.onFragmentCreate();
    }
}
