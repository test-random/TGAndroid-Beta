package org.telegram.ui.Business;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class GreetMessagesActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public TLRPC.TL_businessGreetingMessage currentValue;
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    public boolean enabled;
    public boolean exclude;
    private UniversalRecyclerView listView;
    private BusinessRecipientsHelper recipientsHelper;
    private boolean valueSet;
    private final int[] daysOfInactivity = {7, 14, 21, 28};
    private int shiftDp = -4;
    public int inactivityDays = 7;
    private final String[] daysOfInactivityTexts = new String[4];

    public GreetMessagesActivity() {
        int i = 0;
        while (true) {
            int[] iArr = this.daysOfInactivity;
            if (i >= iArr.length) {
                return;
            }
            this.daysOfInactivityTexts[i] = LocaleController.formatPluralString("DaysSchedule", iArr[i], new Object[0]);
            i++;
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

    public void chooseInactivity(int i) {
        this.inactivityDays = this.daysOfInactivity[i];
        checkDone(true);
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(LocaleController.getString(R.string.BusinessGreetInfo), "RestrictedEmoji", "👋"));
        arrayList.add(UItem.asCheck(1, LocaleController.getString(R.string.BusinessGreetSend)).setChecked(this.enabled));
        arrayList.add(UItem.asShadow(null));
        if (this.enabled) {
            QuickRepliesController.QuickReply findReply = QuickRepliesController.getInstance(this.currentAccount).findReply("hello");
            arrayList.add(findReply != null ? UItem.asLargeQuickReply(findReply) : UItem.asButton(2, R.drawable.msg2_chats_add, LocaleController.getString(R.string.BusinessGreetCreate)).accent());
            arrayList.add(UItem.asShadow(null));
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BusinessRecipients)));
            arrayList.add(UItem.asRadio(3, LocaleController.getString(R.string.BusinessChatsAllPrivateExcept)).setChecked(this.exclude));
            arrayList.add(UItem.asRadio(4, LocaleController.getString(R.string.BusinessChatsOnlySelected)).setChecked(true ^ this.exclude));
            arrayList.add(UItem.asShadow(null));
            this.recipientsHelper.fillItems(arrayList);
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessGreetRecipientsInfo)));
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BusinessGreetPeriod)));
            int i = 0;
            while (true) {
                int[] iArr = this.daysOfInactivity;
                if (i >= iArr.length) {
                    i = -1;
                    break;
                } else if (iArr[i] == this.inactivityDays) {
                    break;
                } else {
                    i++;
                }
            }
            arrayList.add(UItem.asSlideView(this.daysOfInactivityTexts, i, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    GreetMessagesActivity.this.chooseInactivity(((Integer) obj).intValue());
                }
            }));
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessGreetPeriodInfo)));
        }
    }

    public void lambda$createView$0() {
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void lambda$onBackPressed$3(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public void lambda$onBackPressed$4(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    public void lambda$processDone$1(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.showError(tL_error);
        } else if (!(tLObject instanceof TLRPC.TL_boolFalse)) {
            lambda$onBackPressed$321();
        } else {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$processDone$2(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GreetMessagesActivity.this.lambda$processDone$1(tL_error, tLObject);
            }
        });
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (this.recipientsHelper.onClick(uItem)) {
            return;
        }
        int i2 = uItem.id;
        if (i2 == 2 || uItem.viewType == 17) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", getUserConfig().getClientUserId());
            bundle.putInt("chatMode", 5);
            bundle.putString("quick_reply", "hello");
            presentFragment(new ChatActivity(bundle));
            return;
        }
        if (i2 == 1) {
            this.enabled = !this.enabled;
        } else if (i2 == 3) {
            BusinessRecipientsHelper businessRecipientsHelper = this.recipientsHelper;
            this.exclude = true;
            businessRecipientsHelper.setExclude(true);
        } else {
            if (i2 != 4) {
                return;
            }
            BusinessRecipientsHelper businessRecipientsHelper2 = this.recipientsHelper;
            this.exclude = false;
            businessRecipientsHelper2.setExclude(false);
        }
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void processDone() {
        if (this.doneButtonDrawable.getProgress() > 0.0f) {
            return;
        }
        if (!hasChanges()) {
            lambda$onBackPressed$321();
            return;
        }
        QuickRepliesController.QuickReply findReply = QuickRepliesController.getInstance(this.currentAccount).findReply("hello");
        boolean z = this.enabled;
        if (z && findReply == null) {
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
            View findViewByItemId = this.listView.findViewByItemId(2);
            int i = -this.shiftDp;
            this.shiftDp = i;
            AndroidUtilities.shakeViewSpring(findViewByItemId, i);
            return;
        }
        if (!z || this.recipientsHelper.validate(this.listView)) {
            this.doneButtonDrawable.animateToProgress(1.0f);
            TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
            TLRPC.TL_account_updateBusinessGreetingMessage tL_account_updateBusinessGreetingMessage = new TLRPC.TL_account_updateBusinessGreetingMessage();
            if (this.enabled) {
                TLRPC.TL_inputBusinessGreetingMessage tL_inputBusinessGreetingMessage = new TLRPC.TL_inputBusinessGreetingMessage();
                tL_account_updateBusinessGreetingMessage.message = tL_inputBusinessGreetingMessage;
                tL_inputBusinessGreetingMessage.shortcut_id = findReply.id;
                tL_inputBusinessGreetingMessage.recipients = this.recipientsHelper.getInputValue();
                tL_account_updateBusinessGreetingMessage.message.no_activity_days = this.inactivityDays;
                tL_account_updateBusinessGreetingMessage.flags |= 1;
                if (userFull != null) {
                    userFull.flags2 |= 4;
                    TLRPC.TL_businessGreetingMessage tL_businessGreetingMessage = new TLRPC.TL_businessGreetingMessage();
                    userFull.business_greeting_message = tL_businessGreetingMessage;
                    tL_businessGreetingMessage.shortcut_id = findReply.id;
                    tL_businessGreetingMessage.recipients = this.recipientsHelper.getValue();
                    userFull.business_greeting_message.no_activity_days = this.inactivityDays;
                }
            } else if (userFull != null) {
                userFull.flags2 &= -5;
                userFull.business_greeting_message = null;
            }
            getConnectionsManager().sendRequest(tL_account_updateBusinessGreetingMessage, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    GreetMessagesActivity.this.lambda$processDone$2(tLObject, tL_error);
                }
            });
            getMessagesStorage().updateUserInfo(userFull, false);
        }
    }

    private void setValue() {
        UniversalAdapter universalAdapter;
        if (this.valueSet) {
            return;
        }
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        if (userFull == null) {
            getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, getClassGuid());
            return;
        }
        TLRPC.TL_businessGreetingMessage tL_businessGreetingMessage = userFull.business_greeting_message;
        this.currentValue = tL_businessGreetingMessage;
        this.enabled = tL_businessGreetingMessage != null;
        this.inactivityDays = tL_businessGreetingMessage != null ? tL_businessGreetingMessage.no_activity_days : 7;
        this.exclude = tL_businessGreetingMessage != null ? tL_businessGreetingMessage.recipients.exclude_selected : true;
        BusinessRecipientsHelper businessRecipientsHelper = this.recipientsHelper;
        if (businessRecipientsHelper != null) {
            businessRecipientsHelper.setValue(tL_businessGreetingMessage == null ? null : tL_businessGreetingMessage.recipients);
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        checkDone(true);
        this.valueSet = true;
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.BusinessGreet));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    if (GreetMessagesActivity.this.onBackPressed()) {
                        GreetMessagesActivity.this.lambda$onBackPressed$321();
                    }
                } else if (i == 1) {
                    GreetMessagesActivity.this.processDone();
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
        BusinessRecipientsHelper businessRecipientsHelper = new BusinessRecipientsHelper(this, new Runnable() {
            @Override
            public final void run() {
                GreetMessagesActivity.this.lambda$createView$0();
            }
        });
        this.recipientsHelper = businessRecipientsHelper;
        businessRecipientsHelper.doNotExcludeNewChats();
        BusinessRecipientsHelper businessRecipientsHelper2 = this.recipientsHelper;
        TLRPC.TL_businessGreetingMessage tL_businessGreetingMessage = this.currentValue;
        businessRecipientsHelper2.setValue(tL_businessGreetingMessage == null ? null : tL_businessGreetingMessage.recipients);
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                GreetMessagesActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                GreetMessagesActivity.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        frameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        setValue();
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.quickRepliesUpdated) {
            if (i == NotificationCenter.userInfoDidLoad) {
                setValue();
            }
        } else {
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
                universalAdapter.update(true);
            }
            checkDone(true);
        }
    }

    public boolean hasChanges() {
        if (!this.valueSet) {
            return false;
        }
        boolean z = this.enabled;
        TLRPC.TL_businessGreetingMessage tL_businessGreetingMessage = this.currentValue;
        if (z != (tL_businessGreetingMessage != null)) {
            return true;
        }
        if (z && tL_businessGreetingMessage != null) {
            if (tL_businessGreetingMessage.no_activity_days != this.inactivityDays || tL_businessGreetingMessage.recipients.exclude_selected != this.exclude) {
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
        if (!this.enabled) {
            processDone();
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.UnsavedChanges));
        builder.setMessage(LocaleController.getString(R.string.BusinessGreetUnsavedChanges));
        builder.setPositiveButton(LocaleController.getString(R.string.ApplyTheme), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                GreetMessagesActivity.this.lambda$onBackPressed$3(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                GreetMessagesActivity.this.lambda$onBackPressed$4(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.quickRepliesUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
        QuickRepliesController.getInstance(this.currentAccount).load();
        setValue();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.quickRepliesUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        super.onFragmentDestroy();
    }
}
