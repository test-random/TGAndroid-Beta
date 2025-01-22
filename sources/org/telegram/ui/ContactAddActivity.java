package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.utils.PhotoUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.LNavigation.NavigationExt;
import org.telegram.ui.PhotoViewer;

public class ContactAddActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private boolean addContact;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private CheckBoxCell checkBoxCell;
    private ContactAddActivityDelegate delegate;
    private MessagesController.DialogPhotos dialogPhotos;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private String firstNameFromCard;
    private ImageUpdater imageUpdater;
    private TextView infoTextView;
    private EditTextBoldCursor lastNameField;
    private String lastNameFromCard;
    private LinearLayout linearLayout;
    private TextView nameTextView;
    private boolean needAddException;
    private BackupImageView oldAvatarView;
    TextCell oldPhotoCell;
    private TextView onlineTextView;
    boolean paused;
    private String phone;
    private int photoSelectedType;
    private int photoSelectedTypeFinal;
    private TLRPC.Photo prevAvatar;
    private Theme.ResourcesProvider resourcesProvider;
    MessageObject suggestPhotoMessageFinal;
    private long user_id;

    public interface ContactAddActivityDelegate {
        void didAddToContacts();
    }

    public ContactAddActivity(Bundle bundle) {
        super(bundle);
        this.imageUpdater = new ImageUpdater(true, 0, true);
    }

    public ContactAddActivity(Bundle bundle, Theme.ResourcesProvider resourcesProvider) {
        super(bundle);
        this.resourcesProvider = resourcesProvider;
        this.imageUpdater = new ImageUpdater(true, 0, true);
    }

    private void createServiceMessageLocal(TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2, boolean z) {
        TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
        tL_messageService.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
        tL_messageService.dialog_id = this.user_id;
        tL_messageService.unread = true;
        tL_messageService.out = true;
        int newMessageId = getUserConfig().getNewMessageId();
        tL_messageService.id = newMessageId;
        tL_messageService.local_id = newMessageId;
        TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
        tL_messageService.from_id = tL_peerUser;
        tL_peerUser.user_id = getUserConfig().getClientUserId();
        tL_messageService.flags |= 256;
        TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
        tL_messageService.peer_id = tL_peerUser2;
        tL_peerUser2.user_id = this.user_id;
        tL_messageService.date = getConnectionsManager().getCurrentTime();
        TLRPC.TL_messageActionSuggestProfilePhoto tL_messageActionSuggestProfilePhoto = new TLRPC.TL_messageActionSuggestProfilePhoto();
        tL_messageService.action = tL_messageActionSuggestProfilePhoto;
        TLRPC.TL_photo tL_photo = new TLRPC.TL_photo();
        tL_messageActionSuggestProfilePhoto.photo = tL_photo;
        tL_photo.sizes.add(photoSize);
        tL_messageActionSuggestProfilePhoto.photo.sizes.add(photoSize2);
        tL_messageActionSuggestProfilePhoto.video = z;
        tL_messageActionSuggestProfilePhoto.photo.file_reference = new byte[0];
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        MessageObject messageObject = new MessageObject(this.currentAccount, tL_messageService, false, false);
        this.suggestPhotoMessageFinal = messageObject;
        arrayList.add(messageObject);
        new ArrayList().add(tL_messageService);
        MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(this.user_id, arrayList, 0);
        getMessagesController().photoSuggestion.put(tL_messageService.local_id, this.imageUpdater);
    }

    private String getPhone() {
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.user_id));
        return (user == null || TextUtils.isEmpty(user.phone)) ? this.phone : user.phone;
    }

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean lambda$createView$1(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5) {
            return false;
        }
        this.lastNameField.requestFocus();
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return true;
    }

    public void lambda$createView$10(TLRPC.User user) {
        this.avatar = null;
        sendPhotoChangedRequest(null, null, null, null, null, 0.0d, 2);
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.user_id));
        user2.photo.personal = false;
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
        if (userFull != null) {
            userFull.personal_photo = null;
            userFull.flags &= -2097153;
            getMessagesStorage().updateUserInfo(userFull, true);
        }
        TLRPC.Photo photo = this.prevAvatar;
        if (photo != null) {
            user2.photo.photo_id = photo.id;
            ArrayList<TLRPC.PhotoSize> arrayList = photo.sizes;
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
            if (closestPhotoSizeWithSize != null) {
                user2.photo.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                user2.photo.photo_big = closestPhotoSizeWithSize2.location;
            }
        } else {
            user2.photo = null;
            user2.flags &= -33;
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(user);
        getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
        updateCustomPhotoInfo();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_AVATAR));
    }

    public void lambda$createView$11(Context context, final TLRPC.User user, View view) {
        AlertsCreator.createSimpleAlert(context, LocaleController.getString(R.string.ResetToOriginalPhotoTitle), LocaleController.formatString("ResetToOriginalPhotoMessage", R.string.ResetToOriginalPhotoMessage, user.first_name), LocaleController.getString(R.string.Reset), new Runnable() {
            @Override
            public final void run() {
                ContactAddActivity.this.lambda$createView$10(user);
            }
        }, this.resourcesProvider).show();
    }

    public boolean lambda$createView$2(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.doneButton.performClick();
        return true;
    }

    public void lambda$createView$3(View view) {
        this.checkBoxCell.setChecked(!r3.isChecked(), true);
    }

    public static void lambda$createView$4() {
    }

    public void lambda$createView$5(RLottieDrawable rLottieDrawable, TextCell textCell, DialogInterface dialogInterface) {
        if (this.imageUpdater.isUploadingImage()) {
            rLottieDrawable.setCurrentFrame(0, false);
        } else {
            rLottieDrawable.setCustomEndFrame(85);
            textCell.imageView.playAnimation();
        }
    }

    public void lambda$createView$6(TLRPC.User user, final RLottieDrawable rLottieDrawable, final TextCell textCell, View view) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        this.photoSelectedType = 1;
        this.imageUpdater.setUser(user);
        this.imageUpdater.openMenu(((user == null || (userProfilePhoto = user.photo) == null) ? null : userProfilePhoto.photo_small) != null, new Runnable() {
            @Override
            public final void run() {
                ContactAddActivity.lambda$createView$4();
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                ContactAddActivity.this.lambda$createView$5(rLottieDrawable, textCell, dialogInterface);
            }
        }, 2);
        rLottieDrawable.setCurrentFrame(0);
        rLottieDrawable.setCustomEndFrame(43);
        textCell.imageView.playAnimation();
    }

    public static void lambda$createView$7() {
    }

    public void lambda$createView$8(RLottieDrawable rLottieDrawable, TextCell textCell, DialogInterface dialogInterface) {
        if (this.imageUpdater.isUploadingImage()) {
            rLottieDrawable.setCurrentFrame(0, false);
        } else {
            rLottieDrawable.setCustomEndFrame(86);
            textCell.imageView.playAnimation();
        }
    }

    public void lambda$createView$9(TLRPC.User user, final RLottieDrawable rLottieDrawable, final TextCell textCell, View view) {
        TLRPC.UserProfilePhoto userProfilePhoto;
        this.photoSelectedType = 2;
        this.imageUpdater.setUser(user);
        this.imageUpdater.openMenu(((user == null || (userProfilePhoto = user.photo) == null) ? null : userProfilePhoto.photo_small) != null, new Runnable() {
            @Override
            public final void run() {
                ContactAddActivity.lambda$createView$7();
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                ContactAddActivity.this.lambda$createView$8(rLottieDrawable, textCell, dialogInterface);
            }
        }, 1);
        rLottieDrawable.setCurrentFrame(0);
        rLottieDrawable.setCustomEndFrame(43);
        textCell.imageView.playAnimation();
    }

    public void lambda$didUploadFailed$14() {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didUploadFailed(this);
        if (this.suggestPhotoMessageFinal != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(Integer.valueOf(this.suggestPhotoMessageFinal.getId()));
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, 0L, Boolean.FALSE);
        }
    }

    public boolean lambda$didUploadPhoto$12(BaseFragment baseFragment) {
        if (baseFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            if (chatActivity.getDialogId() == this.user_id && chatActivity.getChatMode() == 0) {
                chatActivity.scrollToLastMessage(true, false);
                return true;
            }
        }
        return false;
    }

    public void lambda$didUploadPhoto$13(TLRPC.PhotoSize photoSize, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.PhotoSize photoSize2, TLRPC.VideoSize videoSize, double d, boolean z) {
        if (this.imageUpdater.isCanceled()) {
            return;
        }
        int i = this.photoSelectedTypeFinal;
        if (i == 2) {
            this.avatar = photoSize.location;
        } else if (i == 1) {
            NavigationExt.backToFragment(this, new NavigationExt.FragmentConsumer() {
                @Override
                public final boolean consume(BaseFragment baseFragment) {
                    boolean lambda$didUploadPhoto$12;
                    lambda$didUploadPhoto$12 = ContactAddActivity.this.lambda$didUploadPhoto$12(baseFragment);
                    return lambda$didUploadPhoto$12;
                }
            });
        }
        if (inputFile == null && inputFile2 == null) {
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, getMessagesController().getUser(Long.valueOf(this.user_id)));
            if (this.photoSelectedTypeFinal == 2) {
                showAvatarProgress(true, false);
            } else {
                createServiceMessageLocal(photoSize, photoSize2, z);
            }
        } else {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.user_id));
            if (this.suggestPhotoMessageFinal == null && user != null) {
                PhotoUtilities.applyPhotoToUser(photoSize, photoSize2, inputFile2 != null, user, true);
                ArrayList arrayList = new ArrayList();
                arrayList.add(user);
                getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_AVATAR));
            }
            sendPhotoChangedRequest(this.avatar, photoSize2.location, inputFile, inputFile2, videoSize, d, this.photoSelectedTypeFinal);
            showAvatarProgress(false, true);
        }
        updateCustomPhotoInfo();
    }

    public void lambda$getThemeDescriptions$17() {
        TLRPC.User user;
        if (this.avatarImage == null || (user = getMessagesController().getUser(Long.valueOf(this.user_id))) == null) {
            return;
        }
        this.avatarDrawable.setInfo(this.currentAccount, user);
        this.avatarImage.invalidate();
    }

    public void lambda$sendPhotoChangedRequest$15(TLRPC.FileLocation fileLocation, TLRPC.InputFile inputFile, TLObject tLObject, TLRPC.FileLocation fileLocation2, int i) {
        BulletinFactory of;
        String formatString;
        if (this.suggestPhotoMessageFinal != null) {
            return;
        }
        if ((fileLocation == null && inputFile == null) || tLObject == null) {
            return;
        }
        TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
        ArrayList<TLRPC.PhotoSize> arrayList = tL_photos_photo.photo.sizes;
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.user_id));
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
        if (userFull != null) {
            userFull.personal_photo = tL_photos_photo.photo;
            userFull.flags |= 2097152;
            getMessagesStorage().updateUserInfo(userFull, true);
        }
        if (user != null) {
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 100);
            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000);
            if (closestPhotoSizeWithSize != null && fileLocation != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(fileLocation, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                ImageLoader.getInstance().replaceImageInCache(fileLocation.volume_id + "_" + fileLocation.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUser(user, 1), false);
            }
            if (closestPhotoSizeWithSize2 != null && fileLocation2 != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(fileLocation2, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
            }
            PhotoUtilities.applyPhotoToUser(tL_photos_photo.photo, user, true);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
            getMessagesController().getDialogPhotos(this.user_id).addPhotoAtStart(tL_photos_photo.photo);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_AVATAR));
            if (getParentActivity() != null) {
                if (i == 2) {
                    of = BulletinFactory.of(this);
                    formatString = LocaleController.formatString("UserCustomPhotoSeted", R.string.UserCustomPhotoSeted, user.first_name);
                } else {
                    of = BulletinFactory.of(this);
                    formatString = LocaleController.formatString("UserCustomPhotoSeted", R.string.UserCustomPhotoSeted, user.first_name);
                }
                of.createUsersBulletin(arrayList2, AndroidUtilities.replaceTags(formatString)).show();
            }
        }
        this.avatar = null;
        updateCustomPhotoInfo();
    }

    public void lambda$sendPhotoChangedRequest$16(final TLRPC.FileLocation fileLocation, final TLRPC.InputFile inputFile, final TLRPC.FileLocation fileLocation2, final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ContactAddActivity.this.lambda$sendPhotoChangedRequest$15(fileLocation, inputFile, tLObject, fileLocation2, i);
            }
        });
    }

    private void sendPhotoChangedRequest(final TLRPC.FileLocation fileLocation, final TLRPC.FileLocation fileLocation2, TLRPC.InputFile inputFile, final TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, double d, final int i) {
        int i2;
        TLRPC.TL_photos_uploadContactProfilePhoto tL_photos_uploadContactProfilePhoto = new TLRPC.TL_photos_uploadContactProfilePhoto();
        tL_photos_uploadContactProfilePhoto.user_id = getMessagesController().getInputUser(this.user_id);
        if (inputFile != null) {
            tL_photos_uploadContactProfilePhoto.file = inputFile;
            tL_photos_uploadContactProfilePhoto.flags |= 1;
        }
        if (inputFile2 != null) {
            tL_photos_uploadContactProfilePhoto.video = inputFile2;
            int i3 = tL_photos_uploadContactProfilePhoto.flags;
            tL_photos_uploadContactProfilePhoto.video_start_ts = d;
            tL_photos_uploadContactProfilePhoto.flags = i3 | 6;
        }
        if (videoSize != null) {
            tL_photos_uploadContactProfilePhoto.flags |= 32;
            tL_photos_uploadContactProfilePhoto.video_emoji_markup = videoSize;
        }
        if (i == 1) {
            tL_photos_uploadContactProfilePhoto.suggest = true;
            i2 = tL_photos_uploadContactProfilePhoto.flags | 8;
        } else {
            tL_photos_uploadContactProfilePhoto.save = true;
            i2 = tL_photos_uploadContactProfilePhoto.flags | 16;
        }
        tL_photos_uploadContactProfilePhoto.flags = i2;
        getConnectionsManager().sendRequest(tL_photos_uploadContactProfilePhoto, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ContactAddActivity.this.lambda$sendPhotoChangedRequest$16(fileLocation, inputFile2, fileLocation2, i, tLObject, tL_error);
            }
        });
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarProgressView == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.avatarAnimation = null;
        }
        if (!z2) {
            if (z) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                this.avatarOverlay.setAlpha(1.0f);
                this.avatarOverlay.setVisibility(0);
                return;
            }
            this.avatarProgressView.setAlpha(0.0f);
            this.avatarProgressView.setVisibility(4);
            this.avatarOverlay.setAlpha(0.0f);
            this.avatarOverlay.setVisibility(4);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.avatarAnimation = animatorSet2;
        if (z) {
            this.avatarProgressView.setVisibility(0);
            this.avatarOverlay.setVisibility(0);
            AnimatorSet animatorSet3 = this.avatarAnimation;
            RadialProgressView radialProgressView = this.avatarProgressView;
            Property property = View.ALPHA;
            animatorSet3.playTogether(ObjectAnimator.ofFloat(radialProgressView, (Property<RadialProgressView, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.avatarOverlay, (Property<View, Float>) property, 1.0f));
        } else {
            RadialProgressView radialProgressView2 = this.avatarProgressView;
            Property property2 = View.ALPHA;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(radialProgressView2, (Property<RadialProgressView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.avatarOverlay, (Property<View, Float>) property2, 0.0f));
        }
        this.avatarAnimation.setDuration(180L);
        this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                ContactAddActivity.this.avatarAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ContactAddActivity.this.avatarAnimation == null || ContactAddActivity.this.avatarProgressView == null) {
                    return;
                }
                if (!z) {
                    ContactAddActivity.this.avatarProgressView.setVisibility(4);
                    ContactAddActivity.this.avatarOverlay.setVisibility(4);
                }
                ContactAddActivity.this.avatarAnimation = null;
            }
        });
        this.avatarAnimation.start();
    }

    private void updateAvatarLayout() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactAddActivity.updateAvatarLayout():void");
    }

    private void updateCustomPhotoInfo() {
        if (this.addContact) {
            return;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.user_id));
        if (this.fragmentBeginToShow) {
            TransitionManager.beginDelayedTransition(this.linearLayout);
        }
        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
        if (userProfilePhoto == null || !userProfilePhoto.personal) {
            this.oldPhotoCell.setVisibility(8);
        } else {
            this.oldPhotoCell.setVisibility(0);
            TLRPC.Photo photo = this.prevAvatar;
            if (photo != null) {
                this.oldAvatarView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 1000), this.prevAvatar), "50_50", this.avatarDrawable, (Object) null);
            }
        }
        if (this.avatarDrawable == null) {
            this.avatarDrawable = new AvatarDrawable(user);
        }
        TLRPC.FileLocation fileLocation = this.avatar;
        if (fileLocation == null) {
            this.avatarImage.setForUserOrChat(user, this.avatarDrawable);
        } else {
            this.avatarImage.setImage(ImageLocation.getForLocal(fileLocation), "50_50", this.avatarDrawable, getMessagesController().getUser(Long.valueOf(this.user_id)));
        }
    }

    @Override
    public boolean canFinishFragment() {
        return this.photoSelectedTypeFinal != 1;
    }

    @Override
    public View createView(final Context context) {
        ActionBar actionBar;
        int i;
        String str;
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue, this.resourcesProvider), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon, this.resourcesProvider), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            actionBar = this.actionBar;
            i = R.string.NewContact;
        } else {
            actionBar = this.actionBar;
            i = R.string.EditContact;
        }
        actionBar.setTitle(LocaleController.getString(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    ContactAddActivity.this.lambda$onBackPressed$323();
                    return;
                }
                if (i2 != 1 || ContactAddActivity.this.firstNameField.getText().length() == 0) {
                    return;
                }
                TLRPC.User user = ContactAddActivity.this.getMessagesController().getUser(Long.valueOf(ContactAddActivity.this.user_id));
                user.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                user.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                user.contact = true;
                ContactAddActivity.this.getMessagesController().putUser(user, false);
                ContactAddActivity.this.getContactsController().addContact(user, ContactAddActivity.this.checkBoxCell != null && ContactAddActivity.this.checkBoxCell.isChecked());
                MessagesController.getNotificationsSettings(((BaseFragment) ContactAddActivity.this).currentAccount).edit().putInt("dialog_bar_vis3" + ContactAddActivity.this.user_id, 3).commit();
                ContactAddActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
                ContactAddActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.peerSettingsDidLoad, Long.valueOf(ContactAddActivity.this.user_id));
                ContactAddActivity.this.lambda$onBackPressed$323();
                if (ContactAddActivity.this.delegate != null) {
                    ContactAddActivity.this.delegate.didAddToContacts();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, LocaleController.getString(R.string.Done).toUpperCase());
        ScrollView scrollView = new ScrollView(context);
        this.fragmentView = scrollView;
        scrollView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        ((ScrollView) this.fragmentView).addView(this.linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = ContactAddActivity.lambda$createView$0(view, motionEvent);
                return lambda$createView$0;
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 24.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(30.0f));
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(60, 60, (LocaleController.isRTL ? 5 : 3) | 48));
        final Paint paint = new Paint(1);
        paint.setColor(1426063360);
        View view = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                if (ContactAddActivity.this.avatarImage == null || !ContactAddActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    return;
                }
                paint.setAlpha((int) (ContactAddActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, paint);
            }
        };
        this.avatarOverlay = view;
        frameLayout.addView(view, LayoutHelper.createFrame(60, 60, (LocaleController.isRTL ? 5 : 3) | 48));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.avatarProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(30.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(60, 60, (LocaleController.isRTL ? 5 : 3) | 48));
        showAvatarProgress(false, false);
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i2, this.resourcesProvider));
        this.nameTextView.setTextSize(1, 20.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        TextView textView2 = this.nameTextView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        TextView textView3 = this.nameTextView;
        boolean z = LocaleController.isRTL;
        frameLayout.addView(textView3, LayoutHelper.createFrame(-2, -2.0f, (z ? 5 : 3) | 48, z ? 0.0f : 80.0f, 3.0f, z ? 80.0f : 0.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.onlineTextView = textView4;
        textView4.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3, this.resourcesProvider));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(truncateAt);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView5 = this.onlineTextView;
        boolean z2 = LocaleController.isRTL;
        frameLayout.addView(textView5, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 80.0f, 32.0f, z2 ? 80.0f : 0.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            protected Theme.ResourcesProvider getResourcesProvider() {
                return ContactAddActivity.this.resourcesProvider;
            }
        };
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
        int i3 = Theme.key_windowBackgroundWhiteHintText;
        editTextBoldCursor2.setHintTextColor(Theme.getColor(i3, this.resourcesProvider));
        this.firstNameField.setTextColor(Theme.getColor(i2, this.resourcesProvider));
        this.firstNameField.setBackgroundDrawable(null);
        EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
        int i4 = Theme.key_windowBackgroundWhiteInputField;
        int themedColor = getThemedColor(i4);
        int i5 = Theme.key_windowBackgroundWhiteInputFieldActivated;
        int themedColor2 = getThemedColor(i5);
        int i6 = Theme.key_text_RedRegular;
        editTextBoldCursor3.setLineColors(themedColor, themedColor2, getThemedColor(i6));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString(R.string.FirstName));
        this.firstNameField.setCursorColor(Theme.getColor(i2, this.resourcesProvider));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView6, int i7, KeyEvent keyEvent) {
                boolean lambda$createView$1;
                lambda$createView$1 = ContactAddActivity.this.lambda$createView$1(textView6, i7, keyEvent);
                return lambda$createView$1;
            }
        });
        this.firstNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            boolean focused;

            @Override
            public void onFocusChange(View view2, boolean z3) {
                if (!ContactAddActivity.this.paused && !z3 && this.focused) {
                    FileLog.d("changed");
                }
                this.focused = z3;
            }
        });
        this.firstNameField.setText(this.firstNameFromCard);
        EditTextBoldCursor editTextBoldCursor4 = new EditTextBoldCursor(context) {
            @Override
            protected Theme.ResourcesProvider getResourcesProvider() {
                return ContactAddActivity.this.resourcesProvider;
            }
        };
        this.lastNameField = editTextBoldCursor4;
        editTextBoldCursor4.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(i3, this.resourcesProvider));
        this.lastNameField.setTextColor(Theme.getColor(i2, this.resourcesProvider));
        this.lastNameField.setBackgroundDrawable(null);
        this.lastNameField.setLineColors(getThemedColor(i4), getThemedColor(i5), getThemedColor(i6));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        this.lastNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString(R.string.LastName));
        this.lastNameField.setCursorColor(Theme.getColor(i2, this.resourcesProvider));
        this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        this.linearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView6, int i7, KeyEvent keyEvent) {
                boolean lambda$createView$2;
                lambda$createView$2 = ContactAddActivity.this.lambda$createView$2(textView6, i7, keyEvent);
                return lambda$createView$2;
            }
        });
        this.lastNameField.setText(this.lastNameFromCard);
        final TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.user_id));
        if (user != null && this.firstNameFromCard == null && this.lastNameFromCard == null) {
            if (user.phone == null && (str = this.phone) != null) {
                user.phone = PhoneFormat.stripExceptNumbers(str);
            }
            this.firstNameField.setText(user.first_name);
            EditTextBoldCursor editTextBoldCursor5 = this.firstNameField;
            editTextBoldCursor5.setSelection(editTextBoldCursor5.length());
            this.lastNameField.setText(user.last_name);
        }
        TextView textView6 = new TextView(context);
        this.infoTextView = textView6;
        textView6.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.infoTextView.setTextSize(1, 14.0f);
        this.infoTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        if (this.addContact) {
            if (!this.needAddException || TextUtils.isEmpty(getPhone())) {
                this.linearLayout.addView(this.infoTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            }
            if (this.needAddException) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 0);
                this.checkBoxCell = checkBoxCell;
                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.checkBoxCell.setText(AndroidUtilities.replaceCharSequence("%1$s", AndroidUtilities.replaceTags(LocaleController.getString(R.string.SharePhoneNumberWith)), Emoji.replaceEmoji(UserObject.getFirstName(user), this.infoTextView.getPaint().getFontMetricsInt(), false)), "", true, false);
                this.checkBoxCell.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                this.checkBoxCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ContactAddActivity.this.lambda$createView$3(view2);
                    }
                });
                this.linearLayout.addView(this.checkBoxCell, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
            }
        } else {
            final TextCell textCell = new TextCell(context, this.resourcesProvider);
            String formatString = LocaleController.formatString("SuggestUserPhoto", R.string.SuggestUserPhoto, user.first_name);
            int i7 = R.drawable.msg_addphoto;
            textCell.setTextAndIcon((CharSequence) formatString, i7, true);
            textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            int i8 = Theme.key_windowBackgroundWhiteBlueIcon;
            int i9 = Theme.key_windowBackgroundWhiteBlueButton;
            textCell.setColors(i8, i9);
            int i10 = R.raw.photo_suggest_icon;
            final RLottieDrawable rLottieDrawable = new RLottieDrawable(i10, "" + i10, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
            textCell.imageView.setTranslationX((float) (-AndroidUtilities.dp(8.0f)));
            textCell.imageView.setAnimation(rLottieDrawable);
            textCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ContactAddActivity.this.lambda$createView$6(user, rLottieDrawable, textCell, view2);
                }
            });
            this.linearLayout.addView(textCell, LayoutHelper.createLinear(-1, -2, 0, 0, 18, 0, 0));
            final TextCell textCell2 = new TextCell(context, this.resourcesProvider);
            textCell2.setTextAndIcon((CharSequence) LocaleController.formatString("UserSetPhoto", R.string.UserSetPhoto, user.first_name), i7, false);
            textCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textCell2.setColors(i8, i9);
            int i11 = R.raw.camera_outline;
            final RLottieDrawable rLottieDrawable2 = new RLottieDrawable(i11, "" + i11, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
            textCell2.imageView.setTranslationX((float) (-AndroidUtilities.dp(8.0f)));
            textCell2.imageView.setAnimation(rLottieDrawable2);
            textCell2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ContactAddActivity.this.lambda$createView$9(user, rLottieDrawable2, textCell2, view2);
                }
            });
            this.linearLayout.addView(textCell2, LayoutHelper.createLinear(-1, -2, 0, 0, 0, 0, 0));
            this.oldAvatarView = new BackupImageView(context);
            this.oldPhotoCell = new TextCell(context, this.resourcesProvider) {
                @Override
                public void onLayout(boolean z3, int i12, int i13, int i14, int i15) {
                    super.onLayout(z3, i12, i13, i14, i15);
                    int dp = AndroidUtilities.dp(21.0f);
                    int measuredHeight = (getMeasuredHeight() - ContactAddActivity.this.oldAvatarView.getMeasuredHeight()) / 2;
                    ContactAddActivity.this.oldAvatarView.layout(dp, measuredHeight, ContactAddActivity.this.oldAvatarView.getMeasuredWidth() + dp, ContactAddActivity.this.oldAvatarView.getMeasuredHeight() + measuredHeight);
                }

                @Override
                public void onMeasure(int i12, int i13) {
                    super.onMeasure(i12, i13);
                    ContactAddActivity.this.oldAvatarView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
                    ContactAddActivity.this.oldAvatarView.setRoundRadius(AndroidUtilities.dp(30.0f));
                }
            };
            if (this.avatarDrawable == null) {
                this.avatarDrawable = new AvatarDrawable(user);
            }
            this.oldAvatarView.setForUserOrChat(user.photo, this.avatarDrawable);
            this.oldPhotoCell.addView(this.oldAvatarView, LayoutHelper.createFrame(30, 30.0f, 16, 21.0f, 0.0f, 21.0f, 0.0f));
            this.oldPhotoCell.setText(LocaleController.getString(R.string.ResetToOriginalPhoto), false);
            this.oldPhotoCell.getImageView().setVisibility(0);
            this.oldPhotoCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.oldPhotoCell.setColors(i8, i9);
            this.oldPhotoCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ContactAddActivity.this.lambda$createView$11(context, user, view2);
                }
            });
            this.linearLayout.addView(this.oldPhotoCell, LayoutHelper.createLinear(-1, -2, 0, 0, 0, 0, 0));
            TLRPC.UserFull userFull = getMessagesController().getUserFull(this.user_id);
            if (userFull != null) {
                TLRPC.Photo photo = userFull.profile_photo;
                this.prevAvatar = photo;
                if (photo == null) {
                    this.prevAvatar = userFull.fallback_photo;
                }
            }
            updateCustomPhotoInfo();
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessagesController.DialogPhotos dialogPhotos;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & intValue) == 0 && (intValue & MessagesController.UPDATE_MASK_STATUS) == 0) {
                return;
            }
            updateAvatarLayout();
            return;
        }
        if (i == NotificationCenter.dialogPhotosUpdate && (dialogPhotos = (MessagesController.DialogPhotos) objArr[0]) == this.dialogPhotos) {
            ArrayList arrayList = new ArrayList(dialogPhotos.photos);
            int i3 = 0;
            while (i3 < arrayList.size()) {
                if (arrayList.get(i3) == null) {
                    arrayList.remove(i3);
                    i3--;
                }
                i3++;
            }
            if (arrayList.size() > 0) {
                this.prevAvatar = (TLRPC.Photo) arrayList.get(0);
                updateCustomPhotoInfo();
            }
        }
    }

    @Override
    public void didStartUpload(boolean z, boolean z2) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        this.photoSelectedTypeFinal = this.photoSelectedType;
        radialProgressView.setProgress(0.0f);
    }

    @Override
    public void didUploadFailed() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ContactAddActivity.this.lambda$didUploadFailed$14();
            }
        });
    }

    @Override
    public void didUploadPhoto(final TLRPC.InputFile inputFile, final TLRPC.InputFile inputFile2, final double d, String str, final TLRPC.PhotoSize photoSize, final TLRPC.PhotoSize photoSize2, final boolean z, final TLRPC.VideoSize videoSize) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ContactAddActivity.this.lambda$didUploadPhoto$13(photoSize2, inputFile, inputFile2, photoSize, videoSize, d, z);
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
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ContactAddActivity.this.lambda$getThemeDescriptions$17();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        TextView textView = this.nameTextView;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(textView, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i2));
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        int i3 = ThemeDescription.FLAG_HINTTEXTCOLOR;
        int i4 = Theme.key_windowBackgroundWhiteHintText;
        arrayList.add(new ThemeDescription(editTextBoldCursor, i3, null, null, null, null, i4));
        EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
        int i5 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i6 = Theme.key_windowBackgroundWhiteInputField;
        arrayList.add(new ThemeDescription(editTextBoldCursor2, i5, null, null, null, null, i6));
        EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
        int i7 = ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
        int i8 = Theme.key_windowBackgroundWhiteInputFieldActivated;
        arrayList.add(new ThemeDescription(editTextBoldCursor3, i7, null, null, null, null, i8));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, i8));
        arrayList.add(new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, themeDescriptionDelegate, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogPhotosUpdate);
        this.user_id = getArguments().getLong("user_id", 0L);
        this.phone = getArguments().getString("phone");
        this.firstNameFromCard = getArguments().getString("first_name_card");
        this.lastNameFromCard = getArguments().getString("last_name_card");
        this.addContact = getArguments().getBoolean("addContact", false);
        this.needAddException = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("dialog_bar_exception" + this.user_id, false);
        TLRPC.User user = this.user_id != 0 ? getMessagesController().getUser(Long.valueOf(this.user_id)) : null;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.parentFragment = this;
            imageUpdater.setDelegate(this);
        }
        this.dialogPhotos = MessagesController.getInstance(this.currentAccount).getDialogPhotos(this.user_id);
        return user != null && super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogPhotosUpdate);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.paused = true;
        this.imageUpdater.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAvatarLayout();
        this.imageUpdater.onResume();
    }

    @Override
    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(f);
    }

    public void setDelegate(ContactAddActivityDelegate contactAddActivityDelegate) {
        this.delegate = contactAddActivityDelegate;
    }

    @Override
    public boolean supportsBulletin() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$supportsBulletin(this);
    }
}
