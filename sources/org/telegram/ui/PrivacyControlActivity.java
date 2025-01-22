package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PrivacyControlActivity;

public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private int alwaysShareRow;
    private TLRPC.PhotoSize avatarForRest;
    private TLRPC.Photo avatarForRestPhoto;
    private RLottieDrawable cameraDrawable;
    private ArrayList currentMinus;
    private int currentPhotoForRestRow;
    private ArrayList currentPlus;
    private final boolean[] currentPlusMiniapps;
    private final boolean[] currentPlusPremium;
    private boolean currentReadValue;
    private int currentSubType;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private int everybodyRow;
    ImageUpdater imageUpdater;
    private ArrayList initialMinus;
    private ArrayList initialPlus;
    private final boolean[] initialPlusMiniapps;
    private final boolean[] initialPlusPremium;
    private int initialRulesSubType;
    private int initialRulesType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private MessageCell messageCell;
    private int messageRow;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private BackupImageView oldAvatarView;
    private TextCell oldPhotoCell;
    private int p2pDetailRow;
    private int p2pRow;
    private int p2pSectionRow;
    private int phoneContactsRow;
    private int phoneDetailRow;
    private int phoneEverybodyRow;
    private int phoneSectionRow;
    private int photoForRestDescriptionRow;
    private int photoForRestRow;
    private boolean prevSubtypeContacts;
    private int readDetailRow;
    private int readPremiumDetailRow;
    private int readPremiumRow;
    private int readRow;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private boolean selectedReadValue;
    private TextCell setAvatarCell;
    private int setBirthdayRow;
    private int shakeDp;
    private int shareDetailRow;
    private int shareSectionRow;

    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }

        @Override
        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2);
        }

        @Override
        public boolean areItemsTheSame(int i, int i2) {
            int i3 = this.oldPositionToItem.get(i, -1);
            return i3 == this.newPositionToItem.get(i2, -1) && i3 >= 0;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, PrivacyControlActivity.this.messageRow, sparseIntArray);
            put(2, PrivacyControlActivity.this.sectionRow, sparseIntArray);
            put(3, PrivacyControlActivity.this.everybodyRow, sparseIntArray);
            put(4, PrivacyControlActivity.this.myContactsRow, sparseIntArray);
            put(5, PrivacyControlActivity.this.nobodyRow, sparseIntArray);
            put(6, PrivacyControlActivity.this.detailRow, sparseIntArray);
            put(7, PrivacyControlActivity.this.shareSectionRow, sparseIntArray);
            put(8, PrivacyControlActivity.this.alwaysShareRow, sparseIntArray);
            put(9, PrivacyControlActivity.this.neverShareRow, sparseIntArray);
            put(10, PrivacyControlActivity.this.shareDetailRow, sparseIntArray);
            put(11, PrivacyControlActivity.this.phoneSectionRow, sparseIntArray);
            put(12, PrivacyControlActivity.this.phoneEverybodyRow, sparseIntArray);
            put(13, PrivacyControlActivity.this.phoneContactsRow, sparseIntArray);
            put(14, PrivacyControlActivity.this.phoneDetailRow, sparseIntArray);
            put(15, PrivacyControlActivity.this.photoForRestRow, sparseIntArray);
            put(16, PrivacyControlActivity.this.currentPhotoForRestRow, sparseIntArray);
            put(17, PrivacyControlActivity.this.photoForRestDescriptionRow, sparseIntArray);
            put(18, PrivacyControlActivity.this.p2pSectionRow, sparseIntArray);
            put(19, PrivacyControlActivity.this.p2pRow, sparseIntArray);
            put(20, PrivacyControlActivity.this.p2pDetailRow, sparseIntArray);
            put(21, PrivacyControlActivity.this.readRow, sparseIntArray);
            put(22, PrivacyControlActivity.this.readDetailRow, sparseIntArray);
            put(23, PrivacyControlActivity.this.readPremiumRow, sparseIntArray);
            put(24, PrivacyControlActivity.this.readPremiumDetailRow, sparseIntArray);
        }

        @Override
        public int getNewListSize() {
            return PrivacyControlActivity.this.rowCount;
        }

        @Override
        public int getOldListSize() {
            return this.oldRowCount;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        private int getUsersCount(ArrayList arrayList) {
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                long longValue = ((Long) arrayList.get(i2)).longValue();
                if (longValue > 0) {
                    i++;
                } else {
                    TLRPC.Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Long.valueOf(-longValue));
                    if (chat != null) {
                        i += chat.participants_count;
                    }
                }
            }
            return i;
        }

        public void lambda$onBindViewHolder$0() {
            PrivacyControlActivity.this.presentFragment(new PremiumPreviewFragment("noncontacts"));
        }

        public void lambda$onBindViewHolder$1(TLObject tLObject, TLRPC.UserFull userFull, TL_account.TL_birthday tL_birthday, TLRPC.TL_error tL_error) {
            Bulletin createSimpleBulletin;
            String str;
            if (tLObject instanceof TLRPC.TL_boolTrue) {
                createSimpleBulletin = BulletinFactory.of(PrivacyControlActivity.this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.PrivacyBirthdaySetDone)).setDuration(5000);
            } else {
                if (userFull != null) {
                    int i = userFull.flags2;
                    userFull.flags2 = tL_birthday == null ? i & (-33) : i | 32;
                    userFull.birthday = tL_birthday;
                    PrivacyControlActivity.this.getMessagesStorage().updateUserInfo(userFull, false);
                }
                if (tL_error != null && (str = tL_error.text) != null && str.startsWith("FLOOD_WAIT_")) {
                    if (PrivacyControlActivity.this.getContext() != null) {
                        PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
                        privacyControlActivity.showDialog(new AlertDialog.Builder(privacyControlActivity.getContext(), ((BaseFragment) PrivacyControlActivity.this).resourceProvider).setTitle(LocaleController.getString(R.string.PrivacyBirthdayTooOftenTitle)).setMessage(LocaleController.getString(R.string.PrivacyBirthdayTooOftenMessage)).setPositiveButton(LocaleController.getString(R.string.OK), null).create());
                        return;
                    }
                    return;
                }
                createSimpleBulletin = BulletinFactory.of(PrivacyControlActivity.this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.UnknownError));
            }
            createSimpleBulletin.show();
        }

        public void lambda$onBindViewHolder$2(final TLRPC.UserFull userFull, final TL_account.TL_birthday tL_birthday, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PrivacyControlActivity.ListAdapter.this.lambda$onBindViewHolder$1(tLObject, userFull, tL_birthday, tL_error);
                }
            });
        }

        public void lambda$onBindViewHolder$3(TL_account.TL_birthday tL_birthday) {
            TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
            updatebirthday.flags |= 1;
            updatebirthday.birthday = tL_birthday;
            final TLRPC.UserFull userFull = PrivacyControlActivity.this.getMessagesController().getUserFull(PrivacyControlActivity.this.getUserConfig().getClientUserId());
            final TL_account.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
            if (userFull != null) {
                userFull.flags2 |= 32;
                userFull.birthday = tL_birthday;
                PrivacyControlActivity.this.getMessagesStorage().updateUserInfo(userFull, false);
            }
            PrivacyControlActivity.this.getMessagesController().invalidateContentSettings();
            PrivacyControlActivity.this.getConnectionsManager().sendRequest(updatebirthday, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PrivacyControlActivity.ListAdapter.this.lambda$onBindViewHolder$2(userFull, tL_birthday2, tLObject, tL_error);
                }
            }, 1024);
            MessagesController.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).removeSuggestion(0L, "BIRTHDAY_SETUP");
            NotificationCenter.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.premiumPromoUpdated, new Object[0]);
            PrivacyControlActivity.this.updateRows(true);
        }

        public void lambda$onBindViewHolder$4() {
            PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
            privacyControlActivity.showDialog(AlertsCreator.createBirthdayPickerDialog(privacyControlActivity.getContext(), LocaleController.getString(R.string.EditProfileBirthdayTitle), LocaleController.getString(R.string.EditProfileBirthdayButton), null, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    PrivacyControlActivity.ListAdapter.this.lambda$onBindViewHolder$3((TL_account.TL_birthday) obj);
                }
            }, null, PrivacyControlActivity.this.getResourceProvider()).create());
        }

        @Override
        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == PrivacyControlActivity.this.alwaysShareRow || i == PrivacyControlActivity.this.neverShareRow || i == PrivacyControlActivity.this.p2pRow || i == PrivacyControlActivity.this.readPremiumRow) {
                return 0;
            }
            if (i == PrivacyControlActivity.this.shareDetailRow || i == PrivacyControlActivity.this.detailRow || i == PrivacyControlActivity.this.p2pDetailRow || i == PrivacyControlActivity.this.photoForRestDescriptionRow || i == PrivacyControlActivity.this.readDetailRow || i == PrivacyControlActivity.this.readPremiumDetailRow || i == PrivacyControlActivity.this.setBirthdayRow) {
                return 1;
            }
            if (i == PrivacyControlActivity.this.sectionRow || i == PrivacyControlActivity.this.shareSectionRow || i == PrivacyControlActivity.this.p2pSectionRow || i == PrivacyControlActivity.this.phoneSectionRow) {
                return 2;
            }
            if (i == PrivacyControlActivity.this.everybodyRow || i == PrivacyControlActivity.this.myContactsRow || i == PrivacyControlActivity.this.nobodyRow || i == PrivacyControlActivity.this.phoneEverybodyRow || i == PrivacyControlActivity.this.phoneContactsRow) {
                return 3;
            }
            if (i == PrivacyControlActivity.this.messageRow) {
                return 4;
            }
            if (i == PrivacyControlActivity.this.phoneDetailRow) {
                return 5;
            }
            if (i == PrivacyControlActivity.this.photoForRestRow) {
                return 6;
            }
            if (i == PrivacyControlActivity.this.currentPhotoForRestRow) {
                return 7;
            }
            return i == PrivacyControlActivity.this.readRow ? 8 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PrivacyControlActivity.this.nobodyRow || adapterPosition == PrivacyControlActivity.this.everybodyRow || adapterPosition == PrivacyControlActivity.this.myContactsRow || adapterPosition == PrivacyControlActivity.this.neverShareRow || adapterPosition == PrivacyControlActivity.this.alwaysShareRow || (adapterPosition == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).getLoadingPrivacyInfo(3)) || adapterPosition == PrivacyControlActivity.this.currentPhotoForRestRow || adapterPosition == PrivacyControlActivity.this.photoForRestDescriptionRow || adapterPosition == PrivacyControlActivity.this.photoForRestRow || adapterPosition == PrivacyControlActivity.this.readRow || adapterPosition == PrivacyControlActivity.this.readPremiumRow;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            BackupImageView backupImageView;
            ImageLocation forLocal;
            int i2;
            switch (i) {
                case 0:
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    textSettingsCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    textSettingsCell = new HeaderCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    textSettingsCell = new RadioCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    textSettingsCell = PrivacyControlActivity.this.messageCell;
                    break;
                case 5:
                default:
                    textSettingsCell = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    textSettingsCell.setBackgroundDrawable(combinedDrawable);
                    break;
                case 6:
                    PrivacyControlActivity.this.setAvatarCell = new TextCell(PrivacyControlActivity.this.getContext());
                    if (PrivacyControlActivity.this.avatarForRest == null) {
                        PrivacyControlActivity.this.setAvatarCell.setTextAndIcon((CharSequence) LocaleController.formatString("SetPhotoForRest", R.string.SetPhotoForRest, new Object[0]), R.drawable.msg_addphoto, false);
                    } else {
                        PrivacyControlActivity.this.setAvatarCell.setTextAndIcon((CharSequence) LocaleController.formatString("UpdatePhotoForRest", R.string.UpdatePhotoForRest, new Object[0]), R.drawable.msg_addphoto, true);
                    }
                    PrivacyControlActivity.this.setAvatarCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    PrivacyControlActivity.this.setAvatarCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
                    int i3 = R.raw.camera_outline;
                    privacyControlActivity.cameraDrawable = new RLottieDrawable(i3, "" + i3, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
                    PrivacyControlActivity.this.setAvatarCell.imageView.setTranslationX((float) (-AndroidUtilities.dp(8.0f)));
                    PrivacyControlActivity.this.setAvatarCell.imageView.setAnimation(PrivacyControlActivity.this.cameraDrawable);
                    PrivacyControlActivity.this.setAvatarCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    textSettingsCell = PrivacyControlActivity.this.setAvatarCell;
                    break;
                case 7:
                    PrivacyControlActivity.this.oldAvatarView = new BackupImageView(PrivacyControlActivity.this.getContext());
                    PrivacyControlActivity.this.oldPhotoCell = new TextCell(PrivacyControlActivity.this.getContext()) {
                        @Override
                        public void onLayout(boolean z, int i4, int i5, int i6, int i7) {
                            super.onLayout(z, i4, i5, i6, i7);
                            int dp = AndroidUtilities.dp(21.0f);
                            int measuredHeight = (getMeasuredHeight() - PrivacyControlActivity.this.oldAvatarView.getMeasuredHeight()) / 2;
                            PrivacyControlActivity.this.oldAvatarView.layout(dp, measuredHeight, PrivacyControlActivity.this.oldAvatarView.getMeasuredWidth() + dp, PrivacyControlActivity.this.oldAvatarView.getMeasuredHeight() + measuredHeight);
                        }

                        @Override
                        public void onMeasure(int i4, int i5) {
                            super.onMeasure(i4, i5);
                            PrivacyControlActivity.this.oldAvatarView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
                            PrivacyControlActivity.this.oldAvatarView.setRoundRadius(AndroidUtilities.dp(30.0f));
                        }
                    };
                    if (PrivacyControlActivity.this.avatarForRest != null) {
                        if (PrivacyControlActivity.this.avatarForRestPhoto != null) {
                            backupImageView = PrivacyControlActivity.this.oldAvatarView;
                            forLocal = ImageLocation.getForPhoto(PrivacyControlActivity.this.avatarForRest, PrivacyControlActivity.this.avatarForRestPhoto);
                            i2 = ((BaseFragment) PrivacyControlActivity.this).currentAccount;
                        } else {
                            backupImageView = PrivacyControlActivity.this.oldAvatarView;
                            forLocal = ImageLocation.getForLocal(PrivacyControlActivity.this.avatarForRest.location);
                            i2 = ((BaseFragment) PrivacyControlActivity.this).currentAccount;
                        }
                        backupImageView.setImage(forLocal, "50_50", (Drawable) null, UserConfig.getInstance(i2).getCurrentUser());
                    }
                    PrivacyControlActivity.this.oldPhotoCell.addView(PrivacyControlActivity.this.oldAvatarView, LayoutHelper.createFrame(30, 30.0f, 16, 21.0f, 0.0f, 21.0f, 0.0f));
                    PrivacyControlActivity.this.oldPhotoCell.setText(LocaleController.getString(R.string.RemovePublicPhoto), false);
                    PrivacyControlActivity.this.oldPhotoCell.getImageView().setVisibility(0);
                    PrivacyControlActivity.this.oldPhotoCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    TextCell textCell = PrivacyControlActivity.this.oldPhotoCell;
                    int i4 = Theme.key_text_RedRegular;
                    textCell.setColors(i4, i4);
                    PrivacyControlActivity.this.oldPhotoCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    textSettingsCell = PrivacyControlActivity.this.oldPhotoCell;
                    break;
                case 8:
                    textSettingsCell = new TextCheckCell(this.mContext, ((BaseFragment) PrivacyControlActivity.this).resourceProvider);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(textSettingsCell);
        }
    }

    public class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        private ChatMessageCell cell;
        private HintView hintView;
        private final Runnable invalidateRunnable;
        private MessageObject messageObject;
        private Drawable shadowDrawable;

        public MessageCell(Context context) {
            super(context);
            this.invalidateRunnable = new Runnable() {
                @Override
                public final void run() {
                    PrivacyControlActivity.MessageCell.this.invalidate();
                }
            };
            setWillNotDraw(false);
            setClipToPadding(false);
            this.shadowDrawable = Theme.getThemedDrawableByKey(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            TLRPC.User user = MessagesController.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).getUser(Long.valueOf(UserConfig.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).getClientUserId()));
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.message = LocaleController.getString(R.string.PrivacyForwardsMessageLine);
            tL_message.date = currentTimeMillis - 3540;
            tL_message.dialog_id = 1L;
            tL_message.flags = 261;
            tL_message.from_id = new TLRPC.TL_peerUser();
            tL_message.id = 1;
            TLRPC.TL_messageFwdHeader tL_messageFwdHeader = new TLRPC.TL_messageFwdHeader();
            tL_message.fwd_from = tL_messageFwdHeader;
            tL_messageFwdHeader.from_name = ContactsController.formatName(user.first_name, user.last_name);
            tL_message.media = new TLRPC.TL_messageMediaEmpty();
            tL_message.out = false;
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            tL_message.peer_id = tL_peerUser;
            tL_peerUser.user_id = UserConfig.getInstance(((BaseFragment) PrivacyControlActivity.this).currentAccount).getClientUserId();
            MessageObject messageObject = new MessageObject(((BaseFragment) PrivacyControlActivity.this).currentAccount, tL_message, true, false);
            this.messageObject = messageObject;
            messageObject.eventId = 1L;
            messageObject.resetLayout();
            ChatMessageCell chatMessageCell = new ChatMessageCell(context, ((BaseFragment) PrivacyControlActivity.this).currentAccount);
            this.cell = chatMessageCell;
            chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                @Override
                public boolean canDrawOutboundsContent() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                }

                @Override
                public boolean canPerformActions() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                }

                @Override
                public boolean canPerformReply() {
                    boolean canPerformActions;
                    canPerformActions = canPerformActions();
                    return canPerformActions;
                }

                @Override
                public void didLongPress(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell2, f, f2);
                }

                @Override
                public void didLongPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell2, keyboardButton);
                }

                @Override
                public boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, chat, i, f, f2);
                }

                @Override
                public boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user2, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, user2, f, f2);
                }

                @Override
                public void didPressAboutRevenueSharingAds() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAboutRevenueSharingAds(this);
                }

                @Override
                public boolean didPressAnimatedEmoji(ChatMessageCell chatMessageCell2, AnimatedEmojiSpan animatedEmojiSpan) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAnimatedEmoji(this, chatMessageCell2, animatedEmojiSpan);
                }

                @Override
                public void didPressBoostCounter(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBoostCounter(this, chatMessageCell2);
                }

                @Override
                public void didPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell2, keyboardButton);
                }

                @Override
                public void didPressCancelSendButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell2);
                }

                @Override
                public void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i, float f, float f2, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, chat, i, f, f2, z);
                }

                @Override
                public void didPressChannelRecommendation(ChatMessageCell chatMessageCell2, TLObject tLObject, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelRecommendation(this, chatMessageCell2, tLObject, z);
                }

                @Override
                public void didPressChannelRecommendationsClose(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelRecommendationsClose(this, chatMessageCell2);
                }

                @Override
                public void didPressCodeCopy(ChatMessageCell chatMessageCell2, MessageObject.TextLayoutBlock textLayoutBlock) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCodeCopy(this, chatMessageCell2, textLayoutBlock);
                }

                @Override
                public void didPressCommentButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell2);
                }

                @Override
                public void didPressDialogButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressDialogButton(this, chatMessageCell2);
                }

                @Override
                public void didPressEffect(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressEffect(this, chatMessageCell2);
                }

                @Override
                public void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressExtendedMediaPreview(this, chatMessageCell2, keyboardButton);
                }

                @Override
                public void didPressFactCheck(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheck(this, chatMessageCell2);
                }

                @Override
                public void didPressFactCheckWhat(ChatMessageCell chatMessageCell2, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheckWhat(this, chatMessageCell2, i, i2);
                }

                @Override
                public void didPressGiveawayChatButton(ChatMessageCell chatMessageCell2, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGiveawayChatButton(this, chatMessageCell2, i);
                }

                @Override
                public void didPressGroupImage(ChatMessageCell chatMessageCell2, ImageReceiver imageReceiver, TLRPC.MessageExtendedMedia messageExtendedMedia, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGroupImage(this, chatMessageCell2, imageReceiver, messageExtendedMedia, f, f2);
                }

                @Override
                public void didPressHiddenForward(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell2);
                }

                @Override
                public void didPressHint(ChatMessageCell chatMessageCell2, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i);
                }

                @Override
                public void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                }

                @Override
                public void didPressInstantButton(ChatMessageCell chatMessageCell2, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i);
                }

                @Override
                public void didPressMoreChannelRecommendations(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressMoreChannelRecommendations(this, chatMessageCell2);
                }

                @Override
                public void didPressOther(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell2, f, f2);
                }

                @Override
                public void didPressReaction(ChatMessageCell chatMessageCell2, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell2, reactionCount, z, f, f2);
                }

                @Override
                public void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i, float f, float f2, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i, f, f2, z);
                }

                @Override
                public void didPressRevealSensitiveContent(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressRevealSensitiveContent(this, chatMessageCell2);
                }

                @Override
                public void didPressSideButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell2);
                }

                @Override
                public void didPressSponsoredClose(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSponsoredClose(this, chatMessageCell2);
                }

                @Override
                public void didPressSponsoredInfo(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSponsoredInfo(this, chatMessageCell2, f, f2);
                }

                @Override
                public void didPressTime(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell2);
                }

                @Override
                public void didPressTopicButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTopicButton(this, chatMessageCell2);
                }

                @Override
                public void didPressUrl(ChatMessageCell chatMessageCell2, CharacterStyle characterStyle, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell2, characterStyle, z);
                }

                @Override
                public void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user2, float f, float f2, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, user2, f, f2, z);
                }

                @Override
                public void didPressUserStatus(ChatMessageCell chatMessageCell2, TLRPC.User user2, TLRPC.Document document, String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserStatus(this, chatMessageCell2, user2, document, str);
                }

                @Override
                public void didPressViaBot(ChatMessageCell chatMessageCell2, String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell2, str);
                }

                @Override
                public void didPressViaBotNotInline(ChatMessageCell chatMessageCell2, long j) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell2, j);
                }

                @Override
                public void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i, int i2, int i3) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i, i2, i3);
                }

                @Override
                public void didPressWebPage(ChatMessageCell chatMessageCell2, TLRPC.WebPage webPage, String str, boolean z) {
                    Browser.openUrl(chatMessageCell2.getContext(), str);
                }

                @Override
                public void didStartVideoStream(MessageObject messageObject2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject2);
                }

                @Override
                public boolean doNotShowLoadingReply(MessageObject messageObject2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$doNotShowLoadingReply(this, messageObject2);
                }

                @Override
                public void forceUpdate(ChatMessageCell chatMessageCell2, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$forceUpdate(this, chatMessageCell2, z);
                }

                @Override
                public String getAdminRank(long j) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                }

                @Override
                public PinchToZoomHelper getPinchToZoomHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                }

                @Override
                public String getProgressLoadingBotButtonUrl(ChatMessageCell chatMessageCell2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getProgressLoadingBotButtonUrl(this, chatMessageCell2);
                }

                @Override
                public CharacterStyle getProgressLoadingLink(ChatMessageCell chatMessageCell2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getProgressLoadingLink(this, chatMessageCell2);
                }

                @Override
                public TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                }

                @Override
                public boolean hasSelectedMessages() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                }

                @Override
                public void invalidateBlur() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                }

                @Override
                public boolean isLandscape() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                }

                @Override
                public boolean isProgressLoading(ChatMessageCell chatMessageCell2, int i) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isProgressLoading(this, chatMessageCell2, i);
                }

                @Override
                public boolean isReplyOrSelf() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isReplyOrSelf(this);
                }

                @Override
                public boolean keyboardIsOpened() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                }

                @Override
                public void needOpenWebView(MessageObject messageObject2, String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject2, str, str2, str3, str4, i, i2);
                }

                @Override
                public boolean needPlayMessage(ChatMessageCell chatMessageCell2, MessageObject messageObject2, boolean z) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, chatMessageCell2, messageObject2, z);
                }

                @Override
                public void needReloadPolls() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                }

                @Override
                public void needShowPremiumBulletin(int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumBulletin(this, i);
                }

                @Override
                public boolean onAccessibilityAction(int i, Bundle bundle) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i, bundle);
                }

                @Override
                public void onDiceFinished() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                }

                @Override
                public void setShouldNotRepeatSticker(MessageObject messageObject2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject2);
                }

                @Override
                public boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2, boolean z) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2, z);
                }

                @Override
                public boolean shouldRepeatSticker(MessageObject messageObject2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject2);
                }

                @Override
                public boolean shouldShowDialogButton(ChatMessageCell chatMessageCell2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldShowDialogButton(this, chatMessageCell2);
                }

                @Override
                public boolean shouldShowTopicButton(ChatMessageCell chatMessageCell2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldShowTopicButton(this, chatMessageCell2);
                }

                @Override
                public void videoTimerReached() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                }
            });
            ChatMessageCell chatMessageCell2 = this.cell;
            chatMessageCell2.isChat = false;
            chatMessageCell2.setFullyDraw(true);
            this.cell.setMessageObject(this.messageObject, null, false, false);
            addView(this.cell, LayoutHelper.createLinear(-1, -2));
            HintView hintView = new HintView(context, 1, true);
            this.hintView = hintView;
            addView(hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        @Override
        protected void dispatchSetPressed(boolean z) {
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
            if (cachedWallpaperNonBlocking != null && this.backgroundDrawable != cachedWallpaperNonBlocking) {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            Drawable drawable = this.backgroundDrawable;
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof BackgroundGradientDrawable) {
                    this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable2).drawExactBoundsSize(canvas, this);
                } else {
                    drawable2.draw(canvas);
                }
            } else if (drawable instanceof BitmapDrawable) {
                if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                    canvas.save();
                    float f = 2.0f / AndroidUtilities.density;
                    canvas.scale(f, f);
                    this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / f), (int) Math.ceil(getMeasuredHeight() / f));
                } else {
                    int measuredHeight = getMeasuredHeight();
                    float max = Math.max(getMeasuredWidth() / this.backgroundDrawable.getIntrinsicWidth(), measuredHeight / this.backgroundDrawable.getIntrinsicHeight());
                    int ceil = (int) Math.ceil(this.backgroundDrawable.getIntrinsicWidth() * max);
                    int ceil2 = (int) Math.ceil(this.backgroundDrawable.getIntrinsicHeight() * max);
                    int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                    int i = (measuredHeight - ceil2) / 2;
                    canvas.save();
                    canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                    this.backgroundDrawable.setBounds(measuredWidth, i, ceil + measuredWidth, ceil2 + i);
                }
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                super.onDraw(canvas);
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }
    }

    public PrivacyControlActivity(int i) {
        this(i, false);
    }

    public PrivacyControlActivity(int i, boolean z) {
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        this.initialPlus = new ArrayList();
        this.initialMinus = new ArrayList();
        this.initialPlusPremium = new boolean[2];
        this.initialPlusMiniapps = new boolean[3];
        this.currentPlusPremium = new boolean[2];
        this.currentPlusMiniapps = new boolean[3];
        this.shakeDp = 4;
        this.rulesType = i;
        if (z) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
        if (this.rulesType == 4) {
            ImageUpdater imageUpdater = new ImageUpdater(false, 0, true);
            this.imageUpdater = imageUpdater;
            imageUpdater.parentFragment = this;
            imageUpdater.setDelegate(this);
            TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().clientUserId);
            if (!UserObject.hasFallbackPhoto(userFull) || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(userFull.fallback_photo.sizes, 1000)) == null) {
                return;
            }
            this.avatarForRest = closestPhotoSizeWithSize;
            this.avatarForRestPhoto = userFull.fallback_photo;
        }
    }

    private void applyCurrentPrivacySettings() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.applyCurrentPrivacySettings():void");
    }

    public boolean checkDiscard() {
        if (this.doneButton.getAlpha() != 1.0f) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.UserRestrictionsApplyChanges));
        builder.setMessage(LocaleController.getString(R.string.PrivacySettingsChangedAlert));
        builder.setPositiveButton(LocaleController.getString(R.string.ApplyTheme), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$19(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                PrivacyControlActivity.this.lambda$checkDiscard$20(alertDialog, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    private void checkPrivacy() {
        int i;
        Boolean bool;
        ArrayList arrayList;
        ArrayList<Long> arrayList2;
        int i2 = this.rulesType;
        if (i2 == 10) {
            TLRPC.TL_globalPrivacySettings globalPrivacySettings = ContactsController.getInstance(this.currentAccount).getGlobalPrivacySettings();
            int i3 = (globalPrivacySettings == null || !globalPrivacySettings.new_noncontact_peers_require_premium) ? 0 : 2;
            this.currentType = i3;
            this.initialRulesType = i3;
            this.currentMinus = new ArrayList();
            this.currentPlus = new ArrayList();
            return;
        }
        boolean[] zArr = this.currentPlusPremium;
        boolean[] zArr2 = this.initialPlusPremium;
        boolean z = i2 == 1;
        zArr2[0] = z;
        zArr[0] = z;
        zArr2[1] = false;
        zArr[1] = false;
        boolean[] zArr3 = this.currentPlusMiniapps;
        boolean[] zArr4 = this.initialPlusMiniapps;
        zArr4[0] = false;
        zArr3[0] = false;
        boolean z2 = i2 == 12;
        zArr4[1] = z2;
        zArr3[1] = z2;
        zArr4[2] = false;
        zArr3[2] = false;
        this.currentPlus = new ArrayList();
        this.currentMinus = new ArrayList();
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            Boolean bool2 = null;
            char c = 65535;
            boolean z3 = false;
            boolean z4 = false;
            for (int i4 = 0; i4 < privacyRules.size(); i4++) {
                TLRPC.PrivacyRule privacyRule = privacyRules.get(i4);
                if (privacyRule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants tL_privacyValueAllowChatParticipants = (TLRPC.TL_privacyValueAllowChatParticipants) privacyRule;
                    int size = tL_privacyValueAllowChatParticipants.chats.size();
                    for (int i5 = 0; i5 < size; i5++) {
                        this.currentPlus.add(Long.valueOf(-tL_privacyValueAllowChatParticipants.chats.get(i5).longValue()));
                    }
                } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants tL_privacyValueDisallowChatParticipants = (TLRPC.TL_privacyValueDisallowChatParticipants) privacyRule;
                    int size2 = tL_privacyValueDisallowChatParticipants.chats.size();
                    for (int i6 = 0; i6 < size2; i6++) {
                        this.currentMinus.add(Long.valueOf(-tL_privacyValueDisallowChatParticipants.chats.get(i6).longValue()));
                    }
                } else {
                    if (privacyRule instanceof TLRPC.TL_privacyValueAllowUsers) {
                        arrayList = this.currentPlus;
                        arrayList2 = ((TLRPC.TL_privacyValueAllowUsers) privacyRule).users;
                    } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                        arrayList = this.currentMinus;
                        arrayList2 = ((TLRPC.TL_privacyValueDisallowUsers) privacyRule).users;
                    } else if (privacyRule instanceof TLRPC.TL_privacyValueAllowPremium) {
                        z3 = true;
                    } else {
                        if (privacyRule instanceof TLRPC.TL_privacyValueAllowBots) {
                            bool = Boolean.TRUE;
                        } else if (privacyRule instanceof TLRPC.TL_privacyValueDisallowBots) {
                            bool = Boolean.FALSE;
                        } else {
                            boolean z5 = privacyRule instanceof TLRPC.TL_privacyValueAllowAll;
                            if (!z5) {
                                boolean z6 = privacyRule instanceof TLRPC.TL_privacyValueDisallowAll;
                                if (!z6 || z4) {
                                    if (privacyRule instanceof TLRPC.TL_privacyValueAllowContacts) {
                                        c = 2;
                                        z4 = true;
                                    } else if (c == 65535) {
                                        if (!z5) {
                                            if (!z6 || z4) {
                                                c = 2;
                                            }
                                        }
                                    }
                                }
                                c = 1;
                            }
                            c = 0;
                        }
                        bool2 = bool;
                    }
                    arrayList.addAll(arrayList2);
                }
            }
            if (c == 0 || (c == 65535 && (this.currentMinus.size() > 0 || !(bool2 == null || bool2.booleanValue())))) {
                i = 2;
                this.currentType = 0;
            } else {
                i = 2;
                if (c != 2) {
                    if (c != 65535 || this.currentMinus.size() <= 0 || this.currentPlus.size() <= 0) {
                        if (c == 1 || (c == 65535 && (this.currentPlus.size() > 0 || (bool2 != null && bool2.booleanValue())))) {
                            this.currentType = 1;
                        }
                        i = 2;
                    } else {
                        i = 2;
                    }
                }
                this.currentType = i;
            }
            int i7 = this.currentType;
            char c2 = i7 == i ? (char) 0 : (char) 1;
            boolean[] zArr5 = this.currentPlusPremium;
            this.initialPlusPremium[c2] = z3;
            zArr5[c2] = z3;
            boolean[] zArr6 = this.currentPlusMiniapps;
            boolean[] zArr7 = this.initialPlusMiniapps;
            boolean z7 = bool2 != null;
            zArr7[i7] = z7;
            zArr6[i7] = z7;
            View view = this.doneButton;
            if (view != null) {
                view.setAlpha(0.0f);
                this.doneButton.setScaleX(0.0f);
                this.doneButton.setScaleY(0.0f);
                this.doneButton.setEnabled(false);
            }
        }
        this.initialPlus.clear();
        this.initialMinus.clear();
        this.initialRulesType = this.currentType;
        this.initialPlus.addAll(this.currentPlus);
        this.initialMinus.addAll(this.currentMinus);
        if (this.rulesType == 6) {
            ArrayList<TLRPC.PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules2 != null && privacyRules2.size() != 0) {
                int i8 = 0;
                while (true) {
                    if (i8 >= privacyRules2.size()) {
                        break;
                    }
                    TLRPC.PrivacyRule privacyRule2 = privacyRules2.get(i8);
                    if (privacyRule2 instanceof TLRPC.TL_privacyValueAllowAll) {
                        break;
                    }
                    if (privacyRule2 instanceof TLRPC.TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else {
                        if (privacyRule2 instanceof TLRPC.TL_privacyValueAllowContacts) {
                            this.currentSubType = 1;
                            break;
                        }
                        i8++;
                    }
                }
            }
            this.currentSubType = 0;
            this.initialRulesSubType = this.currentSubType;
        }
        if (this.rulesType == 0) {
            TLRPC.TL_globalPrivacySettings globalPrivacySettings2 = getContactsController().getGlobalPrivacySettings();
            boolean z8 = globalPrivacySettings2 != null && globalPrivacySettings2.hide_read_marks;
            this.currentReadValue = z8;
            this.selectedReadValue = z8;
        }
        updateRows(false);
    }

    private boolean hasChanges() {
        ArrayList arrayList;
        if (this.rulesType == 0 && ((this.currentType != 0 || ((arrayList = this.currentMinus) != null && !arrayList.isEmpty())) && this.currentReadValue != this.selectedReadValue)) {
            return true;
        }
        int i = this.initialRulesType;
        int i2 = this.currentType;
        if (i != i2) {
            return true;
        }
        if (this.rulesType == 6 && i2 == 1 && this.initialRulesSubType != this.currentSubType) {
            return true;
        }
        if (i2 != 0) {
            if (this.initialPlusPremium[i2 == 2 ? (char) 0 : (char) 1] != this.currentPlusPremium[i2 == 2 ? (char) 0 : (char) 1]) {
                return true;
            }
        }
        if (this.initialPlusMiniapps[i2] != this.currentPlusMiniapps[i2] || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
            return true;
        }
        Collections.sort(this.initialPlus);
        Collections.sort(this.currentPlus);
        if (!this.initialPlus.equals(this.currentPlus)) {
            return true;
        }
        Collections.sort(this.initialMinus);
        Collections.sort(this.currentMinus);
        return !this.initialMinus.equals(this.currentMinus);
    }

    public void lambda$applyCurrentPrivacySettings$10(TLRPC.TL_error tL_error, TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings, TL_account.setGlobalPrivacySettings setglobalprivacysettings) {
        if (tL_error != null) {
            showErrorAlert();
            return;
        }
        if (tL_globalPrivacySettings != null) {
            tL_globalPrivacySettings.new_noncontact_peers_require_premium = setglobalprivacysettings.settings.new_noncontact_peers_require_premium;
        }
        lambda$onBackPressed$323();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.privacyRulesUpdated, new Object[0]);
    }

    public void lambda$applyCurrentPrivacySettings$11(final TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings, final TL_account.setGlobalPrivacySettings setglobalprivacysettings, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$10(tL_error, tL_globalPrivacySettings, setglobalprivacysettings);
            }
        });
    }

    public void lambda$applyCurrentPrivacySettings$12(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(((TL_account.privacyRules) tLObject).rules, 7);
        }
    }

    public void lambda$applyCurrentPrivacySettings$13(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$12(tL_error, tLObject);
            }
        });
    }

    public void lambda$applyCurrentPrivacySettings$14(AlertDialog alertDialog, TLRPC.TL_error tL_error, TLObject tLObject) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (tL_error != null) {
            showErrorAlert();
            return;
        }
        TL_account.privacyRules privacyrules = (TL_account.privacyRules) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(privacyrules.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(privacyrules.chats, false);
        ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyrules.rules, this.rulesType);
        lambda$onBackPressed$323();
    }

    public void lambda$applyCurrentPrivacySettings$15(final AlertDialog alertDialog, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$14(alertDialog, tL_error, tLObject);
            }
        });
    }

    public void lambda$applyCurrentPrivacySettings$16(TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings, TL_account.setGlobalPrivacySettings setglobalprivacysettings) {
        boolean z = setglobalprivacysettings.settings.hide_read_marks;
        this.currentReadValue = z;
        tL_globalPrivacySettings.hide_read_marks = z;
    }

    public void lambda$applyCurrentPrivacySettings$17(final TLRPC.TL_globalPrivacySettings tL_globalPrivacySettings, final TL_account.setGlobalPrivacySettings setglobalprivacysettings, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacyControlActivity.this.lambda$applyCurrentPrivacySettings$16(tL_globalPrivacySettings, setglobalprivacysettings);
            }
        });
    }

    public void lambda$checkDiscard$19(AlertDialog alertDialog, int i) {
        processDone();
    }

    public void lambda$checkDiscard$20(AlertDialog alertDialog, int i) {
        lambda$onBackPressed$323();
    }

    public void lambda$createView$3() {
        TLRPC.Photo photo;
        this.avatarForRest = null;
        this.avatarForRestPhoto = null;
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().clientUserId);
        if (userFull == null || (photo = userFull.fallback_photo) == null) {
            return;
        }
        userFull.flags &= -4194305;
        userFull.fallback_photo = null;
        getMessagesStorage().updateUserInfo(userFull, true);
        updateAvatarForRestInfo();
        updateRows(true);
        TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
        tL_inputPhoto.id = photo.id;
        tL_inputPhoto.access_hash = photo.access_hash;
        byte[] bArr = photo.file_reference;
        tL_inputPhoto.file_reference = bArr;
        if (bArr == null) {
            tL_inputPhoto.file_reference = new byte[0];
        }
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(tL_inputPhoto);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
    }

    public static void lambda$createView$4() {
    }

    public void lambda$createView$5(DialogInterface dialogInterface) {
        if (this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCurrentFrame(0, false);
        } else {
            this.cameraDrawable.setCustomEndFrame(86);
            this.setAvatarCell.imageView.playAnimation();
        }
    }

    public void lambda$createView$6() {
        presentFragment(new PremiumPreviewFragment("noncontacts"));
    }

    public void lambda$createView$7() {
        presentFragment(new PremiumPreviewFragment("settings"));
    }

    public void lambda$createView$8(int i, boolean z, boolean z2, boolean z3, ArrayList arrayList) {
        int i2 = 0;
        if (i == this.neverShareRow) {
            this.currentMinus = arrayList;
            this.currentPlusMiniapps[this.currentType] = z && z3;
            while (i2 < this.currentMinus.size()) {
                this.currentPlus.remove(this.currentMinus.get(i2));
                i2++;
            }
        } else {
            boolean[] zArr = this.currentPlusPremium;
            int i3 = this.currentType;
            zArr[i3 == 2 ? (char) 0 : (char) 1] = z2;
            this.currentPlusMiniapps[i3] = z && z3;
            this.currentPlus = arrayList;
            while (i2 < this.currentPlus.size()) {
                this.currentMinus.remove(this.currentPlus.get(i2));
                i2++;
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    public void lambda$createView$9(View view, final int i) {
        boolean z;
        int i2;
        BaseFragment premiumPreviewFragment;
        BulletinFactory of;
        int i3;
        String string;
        SpannableStringBuilder replaceTags;
        String string2;
        Runnable runnable;
        if (i == this.currentPhotoForRestRow) {
            AlertDialog create = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.RemovePublicPhoto), LocaleController.getString(R.string.RemovePhotoForRestDescription), LocaleController.getString(R.string.Remove), new Runnable() {
                @Override
                public final void run() {
                    PrivacyControlActivity.this.lambda$createView$3();
                }
            }, null).create();
            create.show();
            create.redPositive();
            return;
        }
        int i4 = 0;
        r1 = false;
        boolean z2 = false;
        if (i == this.photoForRestRow) {
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null) {
                imageUpdater.openMenu(false, new Runnable() {
                    @Override
                    public final void run() {
                        PrivacyControlActivity.lambda$createView$4();
                    }
                }, new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        PrivacyControlActivity.this.lambda$createView$5(dialogInterface);
                    }
                }, 0);
                this.cameraDrawable.setCurrentFrame(0);
                this.cameraDrawable.setCustomEndFrame(43);
                this.setAvatarCell.imageView.playAnimation();
                return;
            }
            return;
        }
        if (this.rulesType == 10 && i == this.myContactsRow && !getMessagesController().newNoncontactPeersRequirePremiumWithoutOwnpremium && !getUserConfig().isPremium()) {
            of = BulletinFactory.of(this);
            i3 = R.raw.star_premium_2;
            string = LocaleController.getString(R.string.OptionPremiumRequiredTitle);
            replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.OptionPremiumRequiredMessage));
            string2 = LocaleController.getString(R.string.OptionPremiumRequiredButton);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PrivacyControlActivity.this.lambda$createView$6();
                }
            };
        } else {
            if (this.rulesType != 8 || (!(i == this.myContactsRow || i == this.nobodyRow) || getUserConfig().isPremium())) {
                int i5 = this.nobodyRow;
                if (i == i5 || i == this.everybodyRow || i == this.myContactsRow) {
                    if (i == i5) {
                        i4 = 1;
                    } else if (i != this.everybodyRow) {
                        i4 = 2;
                    }
                    if (i4 == this.currentType) {
                        return;
                    }
                    this.currentType = i4;
                    Bulletin.hideVisible();
                } else {
                    if (i != this.phoneContactsRow && i != this.phoneEverybodyRow) {
                        int i6 = this.neverShareRow;
                        if (i != i6 && i != this.alwaysShareRow) {
                            if (i == this.p2pRow) {
                                premiumPreviewFragment = new PrivacyControlActivity(3);
                            } else if (i == this.readRow) {
                                this.selectedReadValue = !this.selectedReadValue;
                                updateDoneButton();
                                ((TextCheckCell) view).setChecked(this.selectedReadValue);
                                return;
                            } else if (i != this.readPremiumRow) {
                                return;
                            } else {
                                premiumPreviewFragment = new PremiumPreviewFragment("lastseen");
                            }
                            presentFragment(premiumPreviewFragment);
                            return;
                        }
                        ArrayList arrayList = i == i6 ? this.currentMinus : this.currentPlus;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(i == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                        bundle.putInt("chatAddType", this.rulesType != 0 ? 1 : 0);
                        if (i == this.alwaysShareRow && this.rulesType == 1) {
                            bundle.putBoolean("allowPremium", true);
                        }
                        final boolean z3 = this.rulesType == 12 && ((i2 = this.currentType) != 1 ? !(i2 != 2 ? !(i2 == 0 && i == this.neverShareRow) : i != this.alwaysShareRow) : i == this.alwaysShareRow);
                        bundle.putBoolean("allowMiniapps", z3);
                        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
                        if (i == this.alwaysShareRow) {
                            if (this.currentPlusPremium[this.currentType == 2 ? (char) 0 : (char) 1]) {
                                z = true;
                                if (z3 && this.currentPlusMiniapps[this.currentType]) {
                                    z2 = true;
                                }
                                groupCreateActivity.select(arrayList, z, z2);
                                groupCreateActivity.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate() {
                                    @Override
                                    public final void didSelectUsers(boolean z4, boolean z5, ArrayList arrayList2) {
                                        PrivacyControlActivity.this.lambda$createView$8(i, z3, z4, z5, arrayList2);
                                    }
                                });
                                presentFragment(groupCreateActivity);
                                return;
                            }
                        }
                        z = false;
                        if (z3) {
                            z2 = true;
                        }
                        groupCreateActivity.select(arrayList, z, z2);
                        groupCreateActivity.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate() {
                            @Override
                            public final void didSelectUsers(boolean z4, boolean z5, ArrayList arrayList2) {
                                PrivacyControlActivity.this.lambda$createView$8(i, z3, z4, z5, arrayList2);
                            }
                        });
                        presentFragment(groupCreateActivity);
                        return;
                    }
                    int i7 = i != this.phoneEverybodyRow ? 1 : 0;
                    if (i7 == this.currentSubType) {
                        return;
                    } else {
                        this.currentSubType = i7;
                    }
                }
                updateDoneButton();
                updateRows(true);
                return;
            }
            of = BulletinFactory.of(this);
            i3 = R.raw.star_premium_2;
            string = LocaleController.getString(R.string.OptionPremiumRequiredTitle);
            replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.OptionPremiumRequiredMessage));
            string2 = LocaleController.getString(R.string.OptionPremiumRequiredButton);
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PrivacyControlActivity.this.lambda$createView$7();
                }
            };
        }
        of.createSimpleBulletin(i3, string, replaceTags, string2, runnable).show();
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        int i8 = -this.shakeDp;
        this.shakeDp = i8;
        AndroidUtilities.shakeViewSpring(view, i8);
    }

    public void lambda$didUploadPhoto$0(TLObject tLObject) {
        if (tLObject != null) {
            TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
            TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().clientUserId);
            userFull.flags |= 4194304;
            userFull.fallback_photo = tL_photos_photo.photo;
            getMessagesStorage().updateUserInfo(userFull, true);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_photos_photo.photo.sizes, 100);
            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tL_photos_photo.photo.sizes, 1000);
            if (closestPhotoSizeWithSize != null && this.avatarForRest != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatarForRest, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                ImageLoader.getInstance().replaceImageInCache(this.avatarForRest.location.volume_id + "_" + this.avatarForRest.location.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForLocal(closestPhotoSizeWithSize.location), false);
            }
            if (closestPhotoSizeWithSize2 == null || this.avatarForRest == null) {
                return;
            }
            FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatarForRest.location, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
        }
    }

    public void lambda$didUploadPhoto$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacyControlActivity.this.lambda$didUploadPhoto$0(tLObject);
            }
        });
    }

    public void lambda$didUploadPhoto$2(TLRPC.PhotoSize photoSize, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, double d, TLRPC.VideoSize videoSize, TLRPC.PhotoSize photoSize2) {
        this.avatarForRest = photoSize;
        this.avatarForRestPhoto = null;
        updateAvatarForRestInfo();
        if (inputFile != null || inputFile2 != null) {
            TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
            if (inputFile != null) {
                tL_photos_uploadProfilePhoto.file = inputFile;
                tL_photos_uploadProfilePhoto.flags |= 1;
            }
            if (inputFile2 != null) {
                tL_photos_uploadProfilePhoto.video = inputFile2;
                int i = tL_photos_uploadProfilePhoto.flags;
                tL_photos_uploadProfilePhoto.video_start_ts = d;
                tL_photos_uploadProfilePhoto.flags = i | 6;
            }
            if (videoSize != null) {
                tL_photos_uploadProfilePhoto.video_emoji_markup = videoSize;
                tL_photos_uploadProfilePhoto.flags |= 16;
            }
            tL_photos_uploadProfilePhoto.fallback = true;
            tL_photos_uploadProfilePhoto.flags |= 8;
            getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PrivacyControlActivity.this.lambda$didUploadPhoto$1(tLObject, tL_error);
                }
            });
            TLRPC.TL_user tL_user = new TLRPC.TL_user();
            TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
            tL_user.photo = tL_userProfilePhoto;
            tL_userProfilePhoto.photo_small = photoSize.location;
            tL_userProfilePhoto.photo_big = photoSize2.location;
            tL_user.first_name = getUserConfig().getCurrentUser().first_name;
            tL_user.last_name = getUserConfig().getCurrentUser().last_name;
            tL_user.access_hash = getUserConfig().getCurrentUser().access_hash;
            BulletinFactory.of(this).createUsersBulletin(Collections.singletonList(tL_user), LocaleController.getString(R.string.PhotoForRestTooltip)).show();
        }
        updateRows(false);
    }

    public void lambda$processDone$18(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i) {
        applyCurrentPrivacySettings();
        sharedPreferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    public void processDone() {
        if (getParentActivity() == null) {
            return;
        }
        if (this.currentType != 0 && this.rulesType == 0 && !getUserConfig().isPremium()) {
            final SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("privacyAlertShowed", false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString(this.rulesType == 1 ? R.string.WhoCanAddMeInfo : R.string.CustomHelp));
                builder.setTitle(LocaleController.getString(R.string.AppName));
                builder.setPositiveButton(LocaleController.getString(R.string.OK), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i) {
                        PrivacyControlActivity.this.lambda$processDone$18(globalMainSettings, alertDialog, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                showDialog(builder.create());
                return;
            }
        }
        applyCurrentPrivacySettings();
    }

    private void setMessageText() {
        HintView hintView;
        int i;
        TLRPC.Peer peer;
        MessageCell messageCell = this.messageCell;
        if (messageCell != null) {
            messageCell.messageObject.messageOwner.fwd_from.from_id = new TLRPC.TL_peerUser();
            int i2 = this.currentType;
            long j = 1;
            if (i2 == 0) {
                hintView = this.messageCell.hintView;
                i = R.string.PrivacyForwardsEverybody;
            } else {
                if (i2 == 1) {
                    this.messageCell.hintView.setOverrideText(LocaleController.getString(R.string.PrivacyForwardsNobody));
                    peer = this.messageCell.messageObject.messageOwner.fwd_from.from_id;
                    j = 0;
                    peer.user_id = j;
                    this.messageCell.cell.forceResetMessageObject();
                }
                hintView = this.messageCell.hintView;
                i = R.string.PrivacyForwardsContacts;
            }
            hintView.setOverrideText(LocaleController.getString(i));
            peer = this.messageCell.messageObject.messageOwner.fwd_from.from_id;
            peer.user_id = j;
            this.messageCell.cell.forceResetMessageObject();
        }
    }

    private void showErrorAlert() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setMessage(LocaleController.getString(R.string.PrivacyFloodControlError));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        showDialog(builder.create());
    }

    private void updateAvatarForRestInfo() {
        TLRPC.PhotoSize photoSize;
        TextCell textCell = this.setAvatarCell;
        if (textCell != null) {
            TLRPC.PhotoSize photoSize2 = this.avatarForRest;
            SimpleTextView textView = textCell.getTextView();
            if (photoSize2 == null) {
                textView.setText(LocaleController.formatString("SetPhotoForRest", R.string.SetPhotoForRest, new Object[0]));
                this.setAvatarCell.setNeedDivider(false);
            } else {
                textView.setText(LocaleController.formatString("UpdatePhotoForRest", R.string.UpdatePhotoForRest, new Object[0]));
                this.setAvatarCell.setNeedDivider(true);
            }
        }
        BackupImageView backupImageView = this.oldAvatarView;
        if (backupImageView == null || (photoSize = this.avatarForRest) == null) {
            return;
        }
        TLRPC.Photo photo = this.avatarForRestPhoto;
        backupImageView.setImage(photo != null ? ImageLocation.getForPhoto(photoSize, photo) : ImageLocation.getForLocal(photoSize.location), "50_50", (Drawable) null, UserConfig.getInstance(this.currentAccount).getCurrentUser());
    }

    private void updateDoneButton() {
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f).scaleY(hasChanges ? 1.0f : 0.0f).setDuration(180L).start();
    }

    public void updateRows(boolean r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.updateRows(boolean):void");
    }

    @Override
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    @Override
    public boolean canFinishFragment() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$canFinishFragment(this);
    }

    @Override
    public android.view.View createView(android.content.Context r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyControlActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageCell messageCell;
        if (i == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            this.listView.invalidateViews();
        } else {
            if (i != NotificationCenter.didSetNewWallpapper || (messageCell = this.messageCell) == null) {
                return;
            }
            messageCell.invalidate();
        }
    }

    @Override
    public void didStartUpload(boolean z, boolean z2) {
    }

    @Override
    public void didUploadFailed() {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didUploadFailed(this);
    }

    @Override
    public void didUploadPhoto(final TLRPC.InputFile inputFile, final TLRPC.InputFile inputFile2, final double d, String str, final TLRPC.PhotoSize photoSize, final TLRPC.PhotoSize photoSize2, boolean z, final TLRPC.VideoSize videoSize) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PrivacyControlActivity.this.lambda$didUploadPhoto$2(photoSize2, inputFile, inputFile2, d, videoSize, photoSize);
            }
        });
    }

    @Override
    public PhotoViewer.PlaceProviderObject getCloseIntoObject() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getCloseIntoObject(this);
    }

    @Override
    public String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        int i5 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i5));
        int i6 = Theme.key_windowBackgroundGray;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        RecyclerListView recyclerListView = this.listView;
        Drawable[] shadowDrawables = Theme.chat_msgInDrawable.getShadowDrawables();
        int i7 = Theme.key_chat_inBubbleShadow;
        arrayList.add(new ThemeDescription(recyclerListView, 0, null, null, shadowDrawables, null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        RecyclerListView recyclerListView2 = this.listView;
        Drawable[] shadowDrawables2 = Theme.chat_msgOutDrawable.getShadowDrawables();
        int i8 = Theme.key_chat_outBubbleShadow;
        arrayList.add(new ThemeDescription(recyclerListView2, 0, null, null, shadowDrawables2, null, i8));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, i8));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_messageTextIn));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_messageTextOut));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inTimeSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outTimeSelectedText));
        return arrayList;
    }

    @Override
    public boolean onBackPressed() {
        return checkDiscard();
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    public void onPause() {
        super.onPause();
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRows(false);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onResume();
        }
    }

    @Override
    public void onUploadProgressChanged(float f) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$onUploadProgressChanged(this, f);
    }

    @Override
    public boolean supportsBulletin() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$supportsBulletin(this);
    }
}
