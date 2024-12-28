package org.telegram.ui.Components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stories.recorder.HintView2;

public final class BulletinFactory {
    private final FrameLayout containerLayout;
    private final BaseFragment fragment;
    private final Theme.ResourcesProvider resourcesProvider;

    public static final class FileType {
        private static final FileType[] $VALUES;
        public static final FileType AUDIO;
        public static final FileType AUDIOS;
        public static final FileType GIF;
        public static final FileType GIF_TO_DOWNLOADS;
        public static final FileType MEDIA;
        public static final FileType PHOTO;
        public static final FileType PHOTOS;
        public static final FileType PHOTO_TO_DOWNLOADS;
        public static final FileType UNKNOWN;
        public static final FileType UNKNOWNS;
        public static final FileType VIDEO;
        public static final FileType VIDEOS;
        public static final FileType VIDEO_TO_DOWNLOADS;
        private final Icon icon;
        private final String localeKey;
        private final int localeRes;
        private final boolean plural;

        public enum Icon {
            SAVED_TO_DOWNLOADS(R.raw.ic_download, 2, "Box", "Arrow"),
            SAVED_TO_GALLERY(R.raw.ic_save_to_gallery, 0, "Box", "Arrow", "Mask", "Arrow 2", "Splash"),
            SAVED_TO_MUSIC(R.raw.ic_save_to_music, 2, "Box", "Arrow"),
            SAVED_TO_GIFS(R.raw.ic_save_to_gifs, 0, "gif");

            private final String[] layers;
            private final int paddingBottom;
            private final int resId;

            Icon(int i, int i2, String... strArr) {
                this.resId = i;
                this.paddingBottom = i2;
                this.layers = strArr;
            }
        }

        private static FileType[] $values() {
            return new FileType[]{PHOTO, PHOTOS, VIDEO, VIDEOS, MEDIA, PHOTO_TO_DOWNLOADS, VIDEO_TO_DOWNLOADS, GIF, GIF_TO_DOWNLOADS, AUDIO, AUDIOS, UNKNOWN, UNKNOWNS};
        }

        static {
            int i = R.string.PhotoSavedHint;
            Icon icon = Icon.SAVED_TO_GALLERY;
            PHOTO = new FileType("PHOTO", 0, "PhotoSavedHint", i, icon);
            PHOTOS = new FileType("PHOTOS", 1, "PhotosSavedHint", icon);
            VIDEO = new FileType("VIDEO", 2, "VideoSavedHint", R.string.VideoSavedHint, icon);
            VIDEOS = new FileType("VIDEOS", 3, "VideosSavedHint", icon);
            MEDIA = new FileType("MEDIA", 4, "MediaSavedHint", icon);
            int i2 = R.string.PhotoSavedToDownloadsHintLinked;
            Icon icon2 = Icon.SAVED_TO_DOWNLOADS;
            PHOTO_TO_DOWNLOADS = new FileType("PHOTO_TO_DOWNLOADS", 5, "PhotoSavedToDownloadsHintLinked", i2, icon2);
            VIDEO_TO_DOWNLOADS = new FileType("VIDEO_TO_DOWNLOADS", 6, "VideoSavedToDownloadsHintLinked", R.string.VideoSavedToDownloadsHintLinked, icon2);
            GIF = new FileType("GIF", 7, "GifSavedHint", R.string.GifSavedHint, Icon.SAVED_TO_GIFS);
            GIF_TO_DOWNLOADS = new FileType("GIF_TO_DOWNLOADS", 8, "GifSavedToDownloadsHintLinked", R.string.GifSavedToDownloadsHintLinked, icon2);
            int i3 = R.string.AudioSavedHint;
            Icon icon3 = Icon.SAVED_TO_MUSIC;
            AUDIO = new FileType("AUDIO", 9, "AudioSavedHint", i3, icon3);
            AUDIOS = new FileType("AUDIOS", 10, "AudiosSavedHint", icon3);
            UNKNOWN = new FileType("UNKNOWN", 11, "FileSavedHintLinked", R.string.FileSavedHintLinked, icon2);
            UNKNOWNS = new FileType("UNKNOWNS", 12, "FilesSavedHintLinked", icon2);
            $VALUES = $values();
        }

        private FileType(String str, int i, String str2, int i2, Icon icon) {
            this.localeKey = str2;
            this.localeRes = i2;
            this.icon = icon;
            this.plural = false;
        }

        private FileType(String str, int i, String str2, Icon icon) {
            this.localeKey = str2;
            this.icon = icon;
            this.localeRes = 0;
            this.plural = true;
        }

        public String getText(int i) {
            return this.plural ? LocaleController.formatPluralString(this.localeKey, i, new Object[0]) : LocaleController.getString(this.localeKey, this.localeRes);
        }

        public static FileType valueOf(String str) {
            return (FileType) Enum.valueOf(FileType.class, str);
        }

        public static FileType[] values() {
            return (FileType[]) $VALUES.clone();
        }
    }

    public static class UndoObject {
        public Runnable onAction;
        public Runnable onUndo;
        public CharSequence undoText;
    }

    private BulletinFactory(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        this.containerLayout = frameLayout;
        this.fragment = null;
        this.resourcesProvider = resourcesProvider;
    }

    private BulletinFactory(BaseFragment baseFragment) {
        if (baseFragment == null || baseFragment.getLastStoryViewer() == null || !baseFragment.getLastStoryViewer().attachedToParent()) {
            this.fragment = baseFragment;
            this.containerLayout = null;
            this.resourcesProvider = baseFragment != null ? baseFragment.getResourceProvider() : null;
        } else {
            this.fragment = null;
            this.containerLayout = baseFragment.getLastStoryViewer().getContainerForBulletin();
            this.resourcesProvider = baseFragment.getLastStoryViewer().getResourceProvider();
        }
    }

    public static boolean canShowBulletin(BaseFragment baseFragment) {
        return (baseFragment == null || baseFragment.getParentActivity() == null || baseFragment.getLayoutContainer() == null) ? false : true;
    }

    public static Bulletin createAddedAsAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserAddedAsAdminHint", R.string.UserAddedAsAdminHint, str)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createBanBulletin(BaseFragment baseFragment, boolean z) {
        int i;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        if (z) {
            lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
            i = R.string.UserBlocked;
        } else {
            lottieLayout.setAnimation(R.raw.ic_unban, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            i = R.string.UserUnblocked;
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.getString(i)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createCopyLinkBulletin(FrameLayout frameLayout) {
        return of(frameLayout, null).createCopyLinkBulletin();
    }

    public static Bulletin createCopyLinkBulletin(BaseFragment baseFragment) {
        return of(baseFragment).createCopyLinkBulletin();
    }

    public static Bulletin createForwardedBulletin(Context context, FrameLayout frameLayout, int i, long j, int i2, int i3, int i4) {
        SpannableStringBuilder replaceTags;
        String formatString;
        String string;
        BulletinFactory$$ExternalSyntheticLambda0 bulletinFactory$$ExternalSyntheticLambda0;
        final Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(context, null, i3, i4);
        int i5 = 300;
        if (i > 1) {
            Object[] objArr = new Object[0];
            replaceTags = AndroidUtilities.replaceTags(i2 <= 1 ? LocaleController.formatPluralString("FwdMessageToManyChats", i, objArr) : LocaleController.formatPluralString("FwdMessagesToManyChats", i, objArr));
            lottieLayout.setAnimation(R.raw.forward, 30, 30, new String[0]);
        } else if (j == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
            if (i2 <= 1) {
                string = LocaleController.getString(R.string.FwdMessageToSavedMessages);
                bulletinFactory$$ExternalSyntheticLambda0 = new BulletinFactory$$ExternalSyntheticLambda0();
            } else {
                string = LocaleController.getString(R.string.FwdMessagesToSavedMessages);
                bulletinFactory$$ExternalSyntheticLambda0 = new BulletinFactory$$ExternalSyntheticLambda0();
            }
            SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(string, bulletinFactory$$ExternalSyntheticLambda0);
            lottieLayout.setAnimation(R.raw.saved_messages, 30, 30, new String[0]);
            replaceTags = replaceSingleTag;
            i5 = -1;
        } else {
            if (DialogObject.isChatDialog(j)) {
                TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-j));
                formatString = i2 <= 1 ? LocaleController.formatString("FwdMessageToGroup", R.string.FwdMessageToGroup, chat.title) : LocaleController.formatString("FwdMessagesToGroup", R.string.FwdMessagesToGroup, chat.title);
            } else {
                TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(j));
                formatString = i2 <= 1 ? LocaleController.formatString("FwdMessageToUser", R.string.FwdMessageToUser, UserObject.getFirstName(user)) : LocaleController.formatString("FwdMessagesToUser", R.string.FwdMessagesToUser, UserObject.getFirstName(user));
            }
            replaceTags = AndroidUtilities.replaceTags(formatString);
            lottieLayout.setAnimation(R.raw.forward, 30, 30, new String[0]);
        }
        lottieLayout.textView.setText(replaceTags);
        if (i5 > 0) {
            lottieLayout.postDelayed(new Runnable() {
                @Override
                public final void run() {
                    Bulletin.LottieLayout.this.performHapticFeedback(3, 2);
                }
            }, i5);
        }
        return Bulletin.make(frameLayout, lottieLayout, 1500);
    }

    public static Bulletin createInviteSentBulletin(Context context, FrameLayout frameLayout, int i, long j, int i2, int i3, int i4) {
        SpannableStringBuilder replaceTags;
        String formatString;
        final Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(context, null, i3, i4);
        int i5 = 300;
        if (i > 1) {
            replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString("InvLinkToChats", R.string.InvLinkToChats, LocaleController.formatPluralString("Chats", i, new Object[0])));
            lottieLayout.setAnimation(R.raw.forward, 30, 30, new String[0]);
        } else if (j == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
            replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.InvLinkToSavedMessages));
            lottieLayout.setAnimation(R.raw.saved_messages, 30, 30, new String[0]);
            i5 = -1;
        } else {
            if (DialogObject.isChatDialog(j)) {
                formatString = LocaleController.formatString("InvLinkToGroup", R.string.InvLinkToGroup, MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-j)).title);
            } else {
                formatString = LocaleController.formatString("InvLinkToUser", R.string.InvLinkToUser, UserObject.getFirstName(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(j))));
            }
            replaceTags = AndroidUtilities.replaceTags(formatString);
            lottieLayout.setAnimation(R.raw.forward, 30, 30, new String[0]);
        }
        lottieLayout.textView.setText(replaceTags);
        if (i5 > 0) {
            lottieLayout.postDelayed(new Runnable() {
                @Override
                public final void run() {
                    Bulletin.LottieLayout.this.performHapticFeedback(3, 2);
                }
            }, i5);
        }
        return Bulletin.make(frameLayout, lottieLayout, 1500);
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, int i) {
        return createMuteBulletin(baseFragment, i, 0, (Theme.ResourcesProvider) null);
    }

    public static org.telegram.ui.Components.Bulletin createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment r5, int r6, int r7, org.telegram.ui.ActionBar.Theme.ResourcesProvider r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BulletinFactory.createMuteBulletin(org.telegram.ui.ActionBar.BaseFragment, int, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):org.telegram.ui.Components.Bulletin");
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
        Object[] objArr = new Object[0];
        lottieLayout.textView.setText(z ? LocaleController.formatPluralString("NotificationsMutedHintChats", i, objArr) : LocaleController.formatPluralString("NotificationsUnmutedHintChats", i, objArr));
        if (z) {
            lottieLayout.setAnimation(R.raw.ic_mute, "Body Main", "Body Top", "Line", "Curve Big", "Curve Small");
        } else {
            lottieLayout.setAnimation(R.raw.ic_unmute, "BODY", "Wibe Big", "Wibe Big 3", "Wibe Small");
        }
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createMuteBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return createMuteBulletin(baseFragment, z ? 3 : 4, 0, resourcesProvider);
    }

    public static Bulletin createPinMessageBulletin(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        return createPinMessageBulletin(baseFragment, true, null, null, resourcesProvider);
    }

    private static Bulletin createPinMessageBulletin(BaseFragment baseFragment, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
        lottieLayout.setAnimation(z ? R.raw.ic_pin : R.raw.ic_unpin, 28, 28, "Pin", "Line");
        lottieLayout.textView.setText(LocaleController.getString(z ? "MessagePinnedHint" : "MessageUnpinnedHint", z ? R.string.MessagePinnedHint : R.string.MessageUnpinnedHint));
        if (!z) {
            lottieLayout.setButton(new Bulletin.UndoButton(baseFragment.getParentActivity(), true, resourcesProvider).setUndoAction(runnable).setDelayedAction(runnable2));
        }
        return Bulletin.make(baseFragment, lottieLayout, z ? 1500 : 5000);
    }

    public static Bulletin createPromoteToAdminBulletin(BaseFragment baseFragment, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserSetAsAdminHint", R.string.UserSetAsAdminHint, str)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createRemoveFromChatBulletin(BaseFragment baseFragment, TLRPC.User user, String str) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), baseFragment.getResourceProvider());
        lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("UserRemovedFromChatHint", R.string.UserRemovedFromChatHint, user.deleted ? LocaleController.formatString("HiddenName", R.string.HiddenName, new Object[0]) : user.first_name, str)));
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, int i, boolean z, int i2, int i3) {
        return of(frameLayout, null).createDownloadBulletin(z ? i > 1 ? FileType.VIDEOS : FileType.VIDEO : i > 1 ? FileType.PHOTOS : FileType.PHOTO, i, i2, i3);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, boolean z, int i, int i2) {
        return of(frameLayout, null).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, 1, i, i2);
    }

    public static Bulletin createSaveToGalleryBulletin(FrameLayout frameLayout, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return of(frameLayout, resourcesProvider).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, resourcesProvider);
    }

    public static Bulletin createSaveToGalleryBulletin(BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return of(baseFragment).createDownloadBulletin(z ? FileType.VIDEO : FileType.PHOTO, resourcesProvider);
    }

    public static Bulletin createSoundEnabledBulletin(BaseFragment baseFragment, int i, Theme.ResourcesProvider resourcesProvider) {
        String string;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
        boolean z = true;
        if (i == 0) {
            string = LocaleController.getString(R.string.SoundOnHint);
        } else {
            if (i != 1) {
                throw new IllegalArgumentException();
            }
            string = LocaleController.getString(R.string.SoundOffHint);
            z = false;
        }
        if (z) {
            lottieLayout.setAnimation(R.raw.sound_on, new String[0]);
        } else {
            lottieLayout.setAnimation(R.raw.sound_off, new String[0]);
        }
        lottieLayout.textView.setText(string);
        return Bulletin.make(baseFragment, lottieLayout, 1500);
    }

    public static Bulletin createUnpinAllMessagesBulletin(BaseFragment baseFragment, int i, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout;
        if (baseFragment.getParentActivity() == null) {
            if (runnable2 == null) {
                return null;
            }
            runnable2.run();
            return null;
        }
        if (z) {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(baseFragment.getParentActivity(), resourcesProvider);
            twoLineLottieLayout.setAnimation(R.raw.ic_unpin, 28, 28, "Pin", "Line");
            twoLineLottieLayout.titleTextView.setText(LocaleController.getString(R.string.PinnedMessagesHidden));
            twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString(R.string.PinnedMessagesHiddenInfo));
            lottieLayout = twoLineLottieLayout;
        } else {
            Bulletin.LottieLayout lottieLayout2 = new Bulletin.LottieLayout(baseFragment.getParentActivity(), resourcesProvider);
            lottieLayout2.setAnimation(R.raw.ic_unpin, 28, 28, "Pin", "Line");
            lottieLayout2.textView.setText(LocaleController.formatPluralString("MessagesUnpinned", i, new Object[0]));
            lottieLayout = lottieLayout2;
        }
        lottieLayout.setButton(new Bulletin.UndoButton(baseFragment.getParentActivity(), true, resourcesProvider).setUndoAction(runnable).setDelayedAction(runnable2));
        return Bulletin.make(baseFragment, lottieLayout, 5000);
    }

    public static Bulletin createUnpinMessageBulletin(BaseFragment baseFragment, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        return createPinMessageBulletin(baseFragment, false, runnable, runnable2, resourcesProvider);
    }

    private Context getContext() {
        FrameLayout frameLayout;
        Context context;
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            context = baseFragment.getParentActivity();
            if (context == null && this.fragment.getLayoutContainer() != null) {
                frameLayout = this.fragment.getLayoutContainer();
                context = frameLayout.getContext();
            }
        } else {
            frameLayout = this.containerLayout;
            if (frameLayout == null) {
                context = null;
            }
            context = frameLayout.getContext();
        }
        return context == null ? ApplicationLoader.applicationContext : context;
    }

    public static BulletinFactory global() {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return of(Bulletin.BulletinWindow.make(ApplicationLoader.applicationContext), null);
        }
        Dialog dialog = safeLastFragment.visibleDialog;
        return dialog instanceof BottomSheet ? of(((BottomSheet) dialog).container, safeLastFragment.getResourceProvider()) : of(safeLastFragment);
    }

    public static void lambda$createContainsEmojiBulletin$2(int i, final Bulletin bulletin, long j, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        final CharSequence string;
        TLRPC.StickerSet stickerSet;
        if (tL_messages_stickerSet == null || (stickerSet = tL_messages_stickerSet.set) == null) {
            string = LocaleController.getString(R.string.AddEmojiNotFound);
        } else {
            string = AndroidUtilities.replaceTags(i == 1 ? LocaleController.formatString("TopicContainsEmojiPackSingle", R.string.TopicContainsEmojiPackSingle, stickerSet.title) : i == 2 ? LocaleController.formatString("StoryContainsEmojiPackSingle", R.string.StoryContainsEmojiPackSingle, stickerSet.title) : LocaleController.formatString("MessageContainsEmojiPackSingle", R.string.MessageContainsEmojiPackSingle, stickerSet.title));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Bulletin.this.onLoaded(string);
            }
        }, Math.max(1L, 750 - (System.currentTimeMillis() - j)));
    }

    public static void lambda$createDownloadBulletin$4() {
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity == null || launchActivity.isFinishing()) {
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW_DOWNLOADS");
        intent.setFlags(268468224);
        LaunchActivity.instance.startActivity(intent);
    }

    public static BulletinFactory of(FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        return new BulletinFactory(frameLayout, resourcesProvider);
    }

    public static BulletinFactory of(BaseFragment baseFragment) {
        return new BulletinFactory(baseFragment);
    }

    public static void showError(TLRPC.TL_error tL_error) {
        if (LaunchActivity.isActive) {
            global().createErrorBulletin(LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text)).show();
        }
    }

    public Bulletin create(Bulletin.Layout layout, int i) {
        BaseFragment baseFragment = this.fragment;
        return baseFragment != null ? Bulletin.make(baseFragment, layout, i) : Bulletin.make(this.containerLayout, layout, i);
    }

    public Bulletin createAdReportedBulletin(CharSequence charSequence) {
        if (getContext() == null) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(R.raw.ic_admin, "Shield");
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        lottieLayout.textView.setText(charSequence);
        return Bulletin.make(this.fragment, lottieLayout, 2750);
    }

    public Bulletin createBanBulletin(boolean z) {
        int i;
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        if (z) {
            lottieLayout.setAnimation(R.raw.ic_ban, "Hand");
            i = R.string.UserBlocked;
        } else {
            lottieLayout.setAnimation(R.raw.ic_unban, "Main", "Finger 1", "Finger 2", "Finger 3", "Finger 4");
            i = R.string.UserUnblocked;
        }
        lottieLayout.textView.setText(AndroidUtilities.replaceTags(LocaleController.getString(i)));
        return create(lottieLayout, 1500);
    }

    public Bulletin createCaptionLimitBulletin(int i, final Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), null);
        lottieLayout.setAnimation(R.raw.caption_limit, new String[0]);
        String formatPluralString = LocaleController.formatPluralString("ChannelCaptionLimitPremiumPromo", i, new Object[0]);
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(AndroidUtilities.replaceTags(formatPluralString));
        int indexOf = formatPluralString.indexOf(42);
        int i2 = indexOf + 1;
        int indexOf2 = formatPluralString.indexOf(42, i2);
        valueOf.replace(indexOf, indexOf2 + 1, (CharSequence) formatPluralString.substring(i2, indexOf2));
        valueOf.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                runnable.run();
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(false);
            }
        }, indexOf, indexOf2 - 1, 33);
        lottieLayout.textView.setText(valueOf);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        return create(lottieLayout, 5000);
    }

    public Bulletin createChatsBulletin(List list, CharSequence charSequence, CharSequence charSequence2) {
        int i;
        int dp;
        View view;
        View view2;
        AvatarsImageView avatarsImageView;
        float f;
        Bulletin.UsersLayout usersLayout = new Bulletin.UsersLayout(getContext(), charSequence2 != null, this.resourcesProvider);
        if (list != null) {
            i = 0;
            for (int i2 = 0; i2 < list.size() && i < 3; i2++) {
                TLObject tLObject = (TLObject) list.get(i2);
                if (tLObject != null) {
                    int i3 = i + 1;
                    usersLayout.avatarsImageView.setCount(i3);
                    usersLayout.avatarsImageView.setObject(i, UserConfig.selectedAccount, tLObject);
                    i = i3;
                }
            }
            if (list.size() == 1) {
                usersLayout.avatarsImageView.setTranslationX(AndroidUtilities.dp(4.0f));
                avatarsImageView = usersLayout.avatarsImageView;
                f = 1.2f;
            } else {
                avatarsImageView = usersLayout.avatarsImageView;
                f = 1.0f;
            }
            avatarsImageView.setScaleX(f);
            usersLayout.avatarsImageView.setScaleY(f);
        } else {
            i = 0;
        }
        usersLayout.avatarsImageView.commitTransition(false);
        TextView textView = usersLayout.textView;
        if (charSequence2 != null) {
            textView.setSingleLine(true);
            usersLayout.textView.setMaxLines(1);
            usersLayout.textView.setText(charSequence);
            usersLayout.subtitleView.setText(charSequence2);
            usersLayout.subtitleView.setSingleLine(true);
            usersLayout.subtitleView.setMaxLines(1);
            if (usersLayout.linearLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                dp = AndroidUtilities.dp(74 - ((3 - i) * 12));
                if (LocaleController.isRTL) {
                    view2 = usersLayout.linearLayout;
                    ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).rightMargin = dp;
                } else {
                    view = usersLayout.linearLayout;
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dp;
                }
            }
        } else {
            textView.setSingleLine(false);
            usersLayout.textView.setMaxLines(2);
            usersLayout.textView.setText(charSequence);
            if (usersLayout.textView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                dp = AndroidUtilities.dp(74 - ((3 - i) * 12));
                if (LocaleController.isRTL) {
                    view2 = usersLayout.textView;
                    ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).rightMargin = dp;
                } else {
                    view = usersLayout.textView;
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dp;
                }
            }
        }
        if (LocaleController.isRTL) {
            usersLayout.avatarsImageView.setTranslationX(AndroidUtilities.dp(32 - ((i - 1) * 12)));
        }
        return create(usersLayout, 5000);
    }

    public Bulletin createContainsEmojiBulletin(TLRPC.Document document, final int i, final Utilities.Callback callback) {
        LoadingSpan loadingSpan;
        TLRPC.StickerSet stickerSet;
        final TLRPC.InputStickerSet inputStickerSet = MessageObject.getInputStickerSet(document);
        if (inputStickerSet == null) {
            return null;
        }
        TLRPC.TL_messages_stickerSet stickerSet2 = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSet(inputStickerSet, true);
        if (stickerSet2 != null && (stickerSet = stickerSet2.set) != null) {
            return createEmojiBulletin(document, AndroidUtilities.replaceTags(i == 1 ? LocaleController.formatString("TopicContainsEmojiPackSingle", R.string.TopicContainsEmojiPackSingle, stickerSet.title) : i == 2 ? LocaleController.formatString("StoryContainsEmojiPackSingle", R.string.StoryContainsEmojiPackSingle, stickerSet.title) : LocaleController.formatString("MessageContainsEmojiPackSingle", R.string.MessageContainsEmojiPackSingle, stickerSet.title)), LocaleController.getString(R.string.ViewAction), new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(inputStickerSet);
                }
            });
        }
        SpannableStringBuilder spannableStringBuilder = i == 1 ? new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("TopicContainsEmojiPackSingle", R.string.TopicContainsEmojiPackSingle, "<{LOADING}>"))) : i == 2 ? new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("StoryContainsEmojiPackSingle", R.string.StoryContainsEmojiPackSingle, "<{LOADING}>"))) : new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("MessageContainsEmojiPackSingle", R.string.MessageContainsEmojiPackSingle, "<{LOADING}>")));
        int indexOf = spannableStringBuilder.toString().indexOf("<{LOADING}>");
        if (indexOf >= 0) {
            loadingSpan = new LoadingSpan(null, AndroidUtilities.dp(100.0f), AndroidUtilities.dp(2.0f), this.resourcesProvider);
            spannableStringBuilder.setSpan(loadingSpan, indexOf, indexOf + 11, 33);
            int i2 = Theme.key_undo_infoColor;
            loadingSpan.setColors(ColorUtils.setAlphaComponent(Theme.getColor(i2, this.resourcesProvider), 32), ColorUtils.setAlphaComponent(Theme.getColor(i2, this.resourcesProvider), 72));
        } else {
            loadingSpan = null;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final Bulletin createEmojiLoadingBulletin = createEmojiLoadingBulletin(document, spannableStringBuilder, LocaleController.getString(R.string.ViewAction), new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(inputStickerSet);
            }
        });
        if (loadingSpan != null && (createEmojiLoadingBulletin.getLayout() instanceof Bulletin.LoadingLottieLayout)) {
            loadingSpan.setView(((Bulletin.LoadingLottieLayout) createEmojiLoadingBulletin.getLayout()).textLoadingView);
        }
        MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSet(inputStickerSet, null, false, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                BulletinFactory.lambda$createContainsEmojiBulletin$2(i, createEmojiLoadingBulletin, currentTimeMillis, (TLRPC.TL_messages_stickerSet) obj);
            }
        });
        return createEmojiLoadingBulletin;
    }

    public Bulletin createCopyBulletin(String str) {
        return createCopyBulletin(str, null);
    }

    public Bulletin createCopyBulletin(String str, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), null);
        lottieLayout.setAnimation(R.raw.copy, 36, 36, "NULL ROTATION", "Back", "Front");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin() {
        return createCopyLinkBulletin(false, this.resourcesProvider);
    }

    public Bulletin createCopyLinkBulletin(String str, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        lottieLayout.textView.setText(str);
        return create(lottieLayout, 1500);
    }

    public Bulletin createCopyLinkBulletin(boolean z, Theme.ResourcesProvider resourcesProvider) {
        if (!AndroidUtilities.shouldShowClipboardToast()) {
            return new Bulletin.EmptyBulletin();
        }
        if (!z) {
            Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
            lottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
            lottieLayout.textView.setText(LocaleController.getString(R.string.LinkCopied));
            return create(lottieLayout, 1500);
        }
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider);
        twoLineLottieLayout.setAnimation(R.raw.voip_invite, 36, 36, "Wibe", "Circle");
        twoLineLottieLayout.titleTextView.setText(LocaleController.getString(R.string.LinkCopied));
        twoLineLottieLayout.subtitleTextView.setText(LocaleController.getString(R.string.LinkCopiedPrivateInfo));
        return create(twoLineLottieLayout, 2750);
    }

    public Bulletin createDownloadBulletin(FileType fileType) {
        return createDownloadBulletin(fileType, this.resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3) {
        return createDownloadBulletin(fileType, i, i2, i3, null);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, int i2, int i3, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = (i2 == 0 || i3 == 0) ? new Bulletin.LottieLayout(getContext(), resourcesProvider) : new Bulletin.LottieLayout(getContext(), resourcesProvider, i2, i3);
        lottieLayout.setAnimation(fileType.icon.resId, fileType.icon.layers);
        lottieLayout.textView.setText(AndroidUtilities.replaceSingleTag(fileType.getText(i), new Runnable() {
            @Override
            public final void run() {
                BulletinFactory.lambda$createDownloadBulletin$4();
            }
        }));
        if (fileType.icon.paddingBottom != 0) {
            lottieLayout.setIconPaddingBottom(fileType.icon.paddingBottom);
        }
        return create(lottieLayout, 1500);
    }

    public Bulletin createDownloadBulletin(FileType fileType, int i, Theme.ResourcesProvider resourcesProvider) {
        return createDownloadBulletin(fileType, i, 0, 0, resourcesProvider);
    }

    public Bulletin createDownloadBulletin(FileType fileType, Theme.ResourcesProvider resourcesProvider) {
        return createDownloadBulletin(fileType, 1, resourcesProvider);
    }

    public Bulletin createEmojiBulletin(TLRPC.Document document, CharSequence charSequence) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        if (MessageObject.isTextColorEmoji(document)) {
            lottieLayout.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_infoColor), PorterDuff.Mode.SRC_IN));
        }
        lottieLayout.setAnimation(document, 36, 36, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        return create(lottieLayout, 2750);
    }

    public Bulletin createEmojiBulletin(TLRPC.Document document, CharSequence charSequence, CharSequence charSequence2) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
        if (MessageObject.isTextColorEmoji(document)) {
            twoLineLottieLayout.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_infoColor), PorterDuff.Mode.SRC_IN));
        }
        twoLineLottieLayout.setAnimation(document, 36, 36, new String[0]);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        return create(twoLineLottieLayout, charSequence.length() + charSequence2.length() < 20 ? 1500 : 2750);
    }

    public Bulletin createEmojiBulletin(TLRPC.Document document, CharSequence charSequence, CharSequence charSequence2, Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        if (MessageObject.isTextColorEmoji(document)) {
            lottieLayout.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_infoColor), PorterDuff.Mode.SRC_IN));
        }
        lottieLayout.setAnimation(document, 36, 36, new String[0]);
        if (lottieLayout.imageView.getImageReceiver() != null) {
            lottieLayout.imageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(4.0f));
        }
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence2).setUndoAction(runnable));
        return create(lottieLayout, 2750);
    }

    public Bulletin createEmojiLoadingBulletin(TLRPC.Document document, CharSequence charSequence, CharSequence charSequence2, Runnable runnable) {
        Bulletin.LoadingLottieLayout loadingLottieLayout = new Bulletin.LoadingLottieLayout(getContext(), this.resourcesProvider);
        if (MessageObject.isTextColorEmoji(document)) {
            loadingLottieLayout.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_infoColor), PorterDuff.Mode.SRC_IN));
        }
        loadingLottieLayout.setAnimation(document, 36, 36, new String[0]);
        loadingLottieLayout.textView.setTextSize(1, 14.0f);
        loadingLottieLayout.textView.setSingleLine(false);
        loadingLottieLayout.textView.setMaxLines(3);
        loadingLottieLayout.textLoadingView.setText(charSequence);
        loadingLottieLayout.textLoadingView.setTextSize(1, 14.0f);
        loadingLottieLayout.textLoadingView.setSingleLine(false);
        loadingLottieLayout.textLoadingView.setMaxLines(3);
        loadingLottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence2).setUndoAction(runnable));
        return create(loadingLottieLayout, 2750);
    }

    public Bulletin createErrorBulletin(CharSequence charSequence) {
        return createErrorBulletin(charSequence, null);
    }

    public Bulletin createErrorBulletin(CharSequence charSequence, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createErrorBulletinSubtitle(CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), resourcesProvider);
        twoLineLottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        return create(twoLineLottieLayout, 1500);
    }

    public Bulletin createImageBulletin(int i, CharSequence charSequence) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setBackground(Theme.getColor(Theme.key_undo_background, this.resourcesProvider), 12);
        lottieLayout.imageView.setImageResource(i);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setLines(2);
        lottieLayout.textView.setMaxLines(4);
        TextView textView = lottieLayout.textView;
        textView.setMaxWidth(HintView2.cutInFancyHalf(textView.getText(), lottieLayout.textView.getPaint()));
        lottieLayout.textView.setLineSpacing(AndroidUtilities.dp(1.33f), 1.0f);
        ((ViewGroup.MarginLayoutParams) lottieLayout.textView.getLayoutParams()).rightMargin = AndroidUtilities.dp(12.0f);
        lottieLayout.setWrapWidth();
        return create(lottieLayout, 5000);
    }

    public Bulletin createMessagesTaggedBulletin(int i, TLRPC.Document document, Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(R.raw.tag_icon_3, 36, 36, new String[0]);
        lottieLayout.removeView(lottieLayout.textView);
        AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(lottieLayout.getContext());
        lottieLayout.textView = textViewEmojis;
        textViewEmojis.setTypeface(Typeface.SANS_SERIF);
        lottieLayout.textView.setTextSize(1, 15.0f);
        lottieLayout.textView.setEllipsize(TextUtils.TruncateAt.END);
        lottieLayout.textView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(AndroidUtilities.dp(20.0f));
        SpannableString spannableString = new SpannableString("d");
        spannableString.setSpan(new AnimatedEmojiSpan(document, textPaint.getFontMetricsInt()), 0, spannableString.length(), 33);
        lottieLayout.textView.setText(new SpannableStringBuilder(i > 1 ? LocaleController.formatPluralString("SavedTagMessagesTagged", i, new Object[0]) : LocaleController.getString(R.string.SavedTagMessageTagged)).append((CharSequence) " ").append((CharSequence) spannableString));
        if (runnable != null) {
            lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(LocaleController.getString(R.string.ViewAction)).setUndoAction(runnable));
        }
        lottieLayout.setTextColor(Theme.getColor(Theme.key_undo_infoColor, this.resourcesProvider));
        lottieLayout.addView(lottieLayout.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 2.0f, 8.0f, 0.0f));
        return create(lottieLayout, 2750);
    }

    public Bulletin createReportSent(Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.chats_infotip, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString(R.string.ReportChatSent));
        return create(lottieLayout, 1500);
    }

    public Bulletin createRestrictVoiceMessagesPremiumBulletin() {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), null);
        lottieLayout.setAnimation(R.raw.voip_muted, new String[0]);
        String string = LocaleController.getString(R.string.PrivacyVoiceMessagesPremiumOnly);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
        int indexOf = string.indexOf(42);
        int lastIndexOf = string.lastIndexOf(42);
        if (indexOf >= 0) {
            spannableStringBuilder.replace(indexOf, lastIndexOf + 1, (CharSequence) string.substring(indexOf + 1, lastIndexOf));
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    BulletinFactory.this.fragment.presentFragment(new PremiumPreviewFragment("settings"));
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(false);
                }
            }, indexOf, lastIndexOf - 1, 33);
        }
        lottieLayout.textView.setText(spannableStringBuilder);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 2750);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence) {
        return createSimpleBulletinWithIconSize(i, charSequence, 36);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, int i2) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        if (charSequence != null) {
            String charSequence2 = charSequence.toString();
            SpannableStringBuilder spannableStringBuilder = charSequence instanceof SpannableStringBuilder ? (SpannableStringBuilder) charSequence : new SpannableStringBuilder(charSequence);
            int i3 = 0;
            for (int indexOf = charSequence2.indexOf(10); indexOf >= 0 && indexOf < charSequence.length(); indexOf = charSequence2.indexOf(10, indexOf + 1)) {
                if (i3 >= i2) {
                    spannableStringBuilder.replace(indexOf, indexOf + 1, (CharSequence) " ");
                }
                i3++;
            }
            charSequence = spannableStringBuilder;
        }
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(i2);
        lottieLayout.textView.setText(charSequence);
        return create(lottieLayout, charSequence.length() < 20 ? 1500 : 2750);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, int i2, int i3) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        if (charSequence != null) {
            String charSequence2 = charSequence.toString();
            SpannableStringBuilder spannableStringBuilder = charSequence instanceof SpannableStringBuilder ? (SpannableStringBuilder) charSequence : new SpannableStringBuilder(charSequence);
            int i4 = 0;
            for (int indexOf = charSequence2.indexOf(10); indexOf >= 0 && indexOf < charSequence.length(); indexOf = charSequence2.indexOf(10, indexOf + 1)) {
                if (i4 >= i2) {
                    spannableStringBuilder.replace(indexOf, indexOf + 1, (CharSequence) " ");
                }
                i4++;
            }
            charSequence = spannableStringBuilder;
        }
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(i2);
        return create(lottieLayout, i3);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, CharSequence charSequence2) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
        twoLineLottieLayout.setAnimation(i, 36, 36, new String[0]);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        return create(twoLineLottieLayout, charSequence.length() + charSequence2.length() < 20 ? 1500 : 2750);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, CharSequence charSequence2, int i2, Runnable runnable) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        if (i != 0) {
            lottieLayout.setAnimation(i, 36, 36, new String[0]);
        } else {
            lottieLayout.imageView.setVisibility(4);
            ((ViewGroup.MarginLayoutParams) lottieLayout.textView.getLayoutParams()).leftMargin = AndroidUtilities.dp(16.0f);
        }
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setTextDirection(5);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence2).setUndoAction(runnable));
        return create(lottieLayout, i2);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, Runnable runnable) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
        twoLineLottieLayout.setAnimation(i, 36, 36, new String[0]);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        twoLineLottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(charSequence3).setUndoAction(runnable));
        return create(twoLineLottieLayout, 5000);
    }

    public Bulletin createSimpleBulletin(int i, CharSequence charSequence, CharSequence charSequence2, Runnable runnable) {
        return createSimpleBulletin(i, charSequence, charSequence2, charSequence.length() < 20 ? 1500 : 2750, runnable);
    }

    public Bulletin createSimpleBulletin(Drawable drawable, CharSequence charSequence) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.imageView.setImageDrawable(drawable);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 2750);
    }

    public Bulletin createSimpleBulletin(Drawable drawable, CharSequence charSequence, CharSequence charSequence2) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
        twoLineLottieLayout.imageView.setImageDrawable(drawable);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        return create(twoLineLottieLayout, 2750);
    }

    public Bulletin createSimpleBulletin(Drawable drawable, CharSequence charSequence, CharSequence charSequence2, String str, Runnable runnable) {
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
        twoLineLottieLayout.imageView.setImageDrawable(drawable);
        twoLineLottieLayout.titleTextView.setText(charSequence);
        twoLineLottieLayout.subtitleTextView.setText(charSequence2);
        twoLineLottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(str).setUndoAction(runnable));
        return create(twoLineLottieLayout, 2750);
    }

    public Bulletin createSimpleBulletinDetail(int i, CharSequence charSequence) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, 36, 36, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setMaxLines(4);
        return create(lottieLayout, charSequence.length() < 20 ? 1500 : 2750);
    }

    public Bulletin createSimpleBulletinWithIconSize(int i, CharSequence charSequence, int i2) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        lottieLayout.setAnimation(i, i2, i2, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, charSequence.length() < 20 ? 1500 : 2750);
    }

    public Bulletin createSimpleLargeBulletin(int i, CharSequence charSequence, CharSequence charSequence2) {
        Bulletin.TwoLineLayout twoLineLayout = new Bulletin.TwoLineLayout(getContext(), this.resourcesProvider);
        twoLineLayout.imageView.setImageResource(i);
        twoLineLayout.titleTextView.setText(charSequence);
        twoLineLayout.subtitleTextView.setText(charSequence2);
        twoLineLayout.subtitleTextView.setSingleLine(false);
        twoLineLayout.subtitleTextView.setMaxLines(5);
        return create(twoLineLayout, 5000);
    }

    public Bulletin createStaticEmojiBulletin(TLRPC.Document document, CharSequence charSequence) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
        if (MessageObject.isTextColorEmoji(document)) {
            lottieLayout.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_undo_infoColor), PorterDuff.Mode.SRC_IN));
        }
        lottieLayout.setAnimation(document, 36, 36, new String[0]);
        lottieLayout.imageView.stopAnimation();
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setTextSize(1, 14.0f);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(3);
        return create(lottieLayout, 2750);
    }

    public Bulletin createSuccessBulletin(CharSequence charSequence) {
        return createSuccessBulletin(charSequence, null);
    }

    public Bulletin createSuccessBulletin(CharSequence charSequence, Theme.ResourcesProvider resourcesProvider) {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), resourcesProvider);
        lottieLayout.setAnimation(R.raw.contact_check, new String[0]);
        lottieLayout.textView.setText(charSequence);
        lottieLayout.textView.setSingleLine(false);
        lottieLayout.textView.setMaxLines(2);
        return create(lottieLayout, 1500);
    }

    public Bulletin createUndoBulletin(CharSequence charSequence, CharSequence charSequence2, boolean z, Runnable runnable, Runnable runnable2) {
        Bulletin.LottieLayout lottieLayout;
        if (TextUtils.isEmpty(charSequence2)) {
            Bulletin.LottieLayout lottieLayout2 = new Bulletin.LottieLayout(getContext(), this.resourcesProvider);
            lottieLayout2.textView.setText(charSequence);
            lottieLayout2.textView.setSingleLine(false);
            lottieLayout2.textView.setMaxLines(2);
            lottieLayout = lottieLayout2;
        } else {
            Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(getContext(), this.resourcesProvider);
            twoLineLottieLayout.titleTextView.setText(charSequence);
            twoLineLottieLayout.subtitleTextView.setText(charSequence2);
            lottieLayout = twoLineLottieLayout;
        }
        lottieLayout.setTimer();
        lottieLayout.setButton(new Bulletin.UndoButton(getContext(), true, z, this.resourcesProvider).setText(LocaleController.getString(R.string.Undo)).setUndoAction(runnable).setDelayedAction(runnable2));
        return create(lottieLayout, 5000);
    }

    public Bulletin createUndoBulletin(CharSequence charSequence, Runnable runnable, Runnable runnable2) {
        return createUndoBulletin(charSequence, false, runnable, runnable2);
    }

    public Bulletin createUndoBulletin(CharSequence charSequence, boolean z, Runnable runnable, Runnable runnable2) {
        return createUndoBulletin(charSequence, null, z, runnable, runnable2);
    }

    public Bulletin createUsersAddedBulletin(ArrayList arrayList, TLRPC.Chat chat) {
        SpannableStringBuilder spannableStringBuilder;
        String formatPluralString;
        if (arrayList == null || arrayList.size() == 0) {
            spannableStringBuilder = null;
        } else {
            int size = arrayList.size();
            boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
            if (size != 1) {
                formatPluralString = isChannelAndNotMegaGroup ? LocaleController.formatPluralString("AddedMembersToChannel", arrayList.size(), new Object[0]) : LocaleController.formatPluralString("AddedSubscribersToChannel", arrayList.size(), new Object[0]);
            } else if (isChannelAndNotMegaGroup) {
                formatPluralString = LocaleController.formatString("HasBeenAddedToChannel", R.string.HasBeenAddedToChannel, "**" + UserObject.getFirstName((TLRPC.User) arrayList.get(0)) + "**");
            } else {
                formatPluralString = LocaleController.formatString("HasBeenAddedToGroup", R.string.HasBeenAddedToGroup, "**" + UserObject.getFirstName((TLRPC.User) arrayList.get(0)) + "**");
            }
            spannableStringBuilder = AndroidUtilities.replaceTags(formatPluralString);
        }
        return createUsersBulletin(arrayList, spannableStringBuilder);
    }

    public Bulletin createUsersBulletin(List list, CharSequence charSequence) {
        return createUsersBulletin(list, charSequence, null, null);
    }

    public Bulletin createUsersBulletin(List list, CharSequence charSequence, CharSequence charSequence2) {
        return createUsersBulletin(list, charSequence, charSequence2, null);
    }

    public Bulletin createUsersBulletin(List list, CharSequence charSequence, CharSequence charSequence2, UndoObject undoObject) {
        int i;
        int dp;
        View view;
        View view2;
        Bulletin.UsersLayout usersLayout = new Bulletin.UsersLayout(getContext(), charSequence2 != null, this.resourcesProvider);
        if (list != null) {
            int i2 = 0;
            i = 0;
            for (int i3 = 3; i2 < list.size() && i < i3; i3 = 3) {
                TLObject tLObject = (TLObject) list.get(i2);
                if (tLObject != null) {
                    int i4 = i + 1;
                    usersLayout.avatarsImageView.setCount(i4);
                    usersLayout.avatarsImageView.setObject(i, UserConfig.selectedAccount, tLObject);
                    i = i4;
                }
                i2++;
            }
            if (list.size() == 1) {
                usersLayout.avatarsImageView.setTranslationX(AndroidUtilities.dp(4.0f));
                usersLayout.avatarsImageView.setScaleX(1.2f);
                usersLayout.avatarsImageView.setScaleY(1.2f);
            } else {
                usersLayout.avatarsImageView.setScaleX(1.0f);
                usersLayout.avatarsImageView.setScaleY(1.0f);
            }
        } else {
            i = 0;
        }
        usersLayout.avatarsImageView.commitTransition(false);
        TextView textView = usersLayout.textView;
        if (charSequence2 != null) {
            textView.setSingleLine(true);
            usersLayout.textView.setMaxLines(1);
            usersLayout.textView.setText(charSequence);
            usersLayout.subtitleView.setText(charSequence2);
            usersLayout.subtitleView.setSingleLine(false);
            usersLayout.subtitleView.setMaxLines(3);
            if (usersLayout.linearLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                dp = AndroidUtilities.dp(70 - ((3 - i) * 12));
                if (i == 1) {
                    dp += AndroidUtilities.dp(4.0f);
                }
                if (LocaleController.isRTL) {
                    view2 = usersLayout.linearLayout;
                    ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).rightMargin = dp;
                } else {
                    view = usersLayout.linearLayout;
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dp;
                }
            }
        } else {
            textView.setSingleLine(false);
            usersLayout.textView.setMaxLines(4);
            usersLayout.textView.setText(charSequence);
            if (usersLayout.textView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                dp = AndroidUtilities.dp(70 - ((3 - i) * 12));
                if (i == 1) {
                    usersLayout.textView.setTranslationY(-AndroidUtilities.dp(1.0f));
                    dp += AndroidUtilities.dp(4.0f);
                }
                if (LocaleController.isRTL) {
                    view2 = usersLayout.textView;
                    ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).rightMargin = dp;
                } else {
                    view = usersLayout.textView;
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dp;
                }
            }
        }
        if (undoObject != null) {
            usersLayout.setButton(new Bulletin.UndoButton(getContext(), true, this.resourcesProvider).setText(LocaleController.getString(R.string.Undo)).setUndoAction(undoObject.onUndo).setDelayedAction(undoObject.onAction));
        }
        return create(usersLayout, 5000);
    }

    public Bulletin createUsersBulletin(TLObject tLObject, CharSequence charSequence) {
        return createUsersBulletin(Arrays.asList(tLObject), charSequence, null, null);
    }

    public Bulletin createUsersBulletin(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2) {
        return createUsersBulletin(Arrays.asList(tLObject), charSequence, charSequence2, null);
    }

    public Bulletin makeForError(TLRPC.TL_error tL_error) {
        return !LaunchActivity.isActive ? new Bulletin.EmptyBulletin() : tL_error == null ? createErrorBulletin(LocaleController.formatString(R.string.UnknownError, new Object[0])) : createErrorBulletin(LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text));
    }

    public void showForError(TLRPC.TL_error tL_error) {
        if (LaunchActivity.isActive) {
            createErrorBulletin(tL_error == null ? LocaleController.formatString(R.string.UnknownError, new Object[0]) : LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text)).show();
        }
    }

    public boolean showForwardedBulletinWithTag(long j, int i) {
        String string;
        BulletinFactory$$ExternalSyntheticLambda0 bulletinFactory$$ExternalSyntheticLambda0;
        if (!UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
            return false;
        }
        Bulletin.LottieLayoutWithReactions lottieLayoutWithReactions = new Bulletin.LottieLayoutWithReactions(this.fragment, i);
        if (j != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
            return false;
        }
        if (i <= 1) {
            string = LocaleController.getString(R.string.FwdMessageToSavedMessages);
            bulletinFactory$$ExternalSyntheticLambda0 = new BulletinFactory$$ExternalSyntheticLambda0();
        } else {
            string = LocaleController.getString(R.string.FwdMessagesToSavedMessages);
            bulletinFactory$$ExternalSyntheticLambda0 = new BulletinFactory$$ExternalSyntheticLambda0();
        }
        SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(string, -1, 2, bulletinFactory$$ExternalSyntheticLambda0);
        lottieLayoutWithReactions.setAnimation(R.raw.saved_messages, 36, 36, new String[0]);
        lottieLayoutWithReactions.textView.setText(replaceSingleTag);
        lottieLayoutWithReactions.textView.setSingleLine(false);
        lottieLayoutWithReactions.textView.setMaxLines(2);
        Bulletin create = create(lottieLayoutWithReactions, 3500);
        lottieLayoutWithReactions.setBulletin(create);
        create.hideAfterBottomSheet(false);
        create.show(true);
        return true;
    }
}
