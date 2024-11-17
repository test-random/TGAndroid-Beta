package org.telegram.ui.bots;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AttachableDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Text;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.bots.SetupEmojiStatusSheet;

public abstract class SetupEmojiStatusSheet {

    public static class UserEmojiStatusDrawable extends Drawable implements AttachableDrawable, NotificationCenter.NotificationCenterDelegate {
        private final AnimatedFloat animatedSwap;
        private boolean attached;
        private final Paint backgroundPaint;
        private final Paint backgroundPaint2;
        private int currentStatus;
        private final AnimatedEmojiDrawable[] emojis;
        private final boolean highlight;
        private final RectF rect;
        private final ImageReceiver statusImageReceiver;
        private final Text text;
        private final ImageReceiver userImageReceiver;
        private View view;
        private boolean waitingForStatuses;

        public UserEmojiStatusDrawable(TLRPC.User user) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            Paint paint2 = new Paint(1);
            this.backgroundPaint2 = paint2;
            ImageReceiver imageReceiver = new ImageReceiver();
            this.userImageReceiver = imageReceiver;
            this.statusImageReceiver = new ImageReceiver();
            this.currentStatus = 1;
            this.emojis = new AnimatedEmojiDrawable[2];
            this.rect = new RectF();
            this.animatedSwap = new AnimatedFloat(new Runnable() {
                @Override
                public final void run() {
                    SetupEmojiStatusSheet.UserEmojiStatusDrawable.this.invalidateSelf();
                }
            }, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.highlight = false;
            int i = Theme.key_windowBackgroundWhite;
            paint.setColor(Theme.getColor(i));
            paint2.setColor(Theme.getColor(i));
            paint2.setShadowLayer(AndroidUtilities.dp(2.33f), 0.0f, AndroidUtilities.dp(2.0f), Theme.multAlpha(-16777216, 0.18f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            imageReceiver.setForUserOrChat(user, avatarDrawable);
            imageReceiver.setRoundRadius(AndroidUtilities.dp(16.0f));
            setRandomStatus();
            this.text = new Text(UserObject.getUserName(user), 14.0f);
        }

        public UserEmojiStatusDrawable(TLRPC.User user, TLRPC.Document document) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            Paint paint2 = new Paint(1);
            this.backgroundPaint2 = paint2;
            ImageReceiver imageReceiver = new ImageReceiver();
            this.userImageReceiver = imageReceiver;
            ImageReceiver imageReceiver2 = new ImageReceiver();
            this.statusImageReceiver = imageReceiver2;
            this.currentStatus = 1;
            this.emojis = new AnimatedEmojiDrawable[2];
            this.rect = new RectF();
            this.animatedSwap = new AnimatedFloat(new Runnable() {
                @Override
                public final void run() {
                    SetupEmojiStatusSheet.UserEmojiStatusDrawable.this.invalidateSelf();
                }
            }, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.highlight = true;
            int i = Theme.key_windowBackgroundWhite;
            paint.setColor(Theme.getColor(i));
            paint2.setColor(Theme.getColor(i));
            paint2.setShadowLayer(AndroidUtilities.dp(2.33f), 0.0f, AndroidUtilities.dp(2.0f), Theme.multAlpha(-16777216, 0.18f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            imageReceiver.setForUserOrChat(user, avatarDrawable);
            imageReceiver.setRoundRadius(AndroidUtilities.dp(16.0f));
            imageReceiver2.setImage(ImageLocation.getForDocument(document), "120_120", ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 120), document), "120_120", DocumentObject.getSvgThumb(document.thumbs, Theme.key_windowBackgroundGray, 0.35f), 0L, null, null, 0);
            this.text = new Text(UserObject.getUserName(user), 14.0f);
        }

        public void lambda$setRandomStatus$0() {
            if (this.attached) {
                setRandomStatus();
            }
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.groupStickersDidLoad && this.waitingForStatuses && this.attached) {
                this.waitingForStatuses = false;
                setRandomStatus();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            AnimatedEmojiDrawable animatedEmojiDrawable;
            AnimatedEmojiDrawable animatedEmojiDrawable2;
            Rect bounds = getBounds();
            float dp = (AndroidUtilities.dp(((this.highlight ? 48 : 28) + 38) + 6.66f) + this.text.getCurrentWidth()) / 2.0f;
            float dp2 = AndroidUtilities.dp(32.0f) / 2.0f;
            this.rect.set(bounds.centerX() - dp, bounds.centerY() - dp2, bounds.centerX() + dp, bounds.centerY() + dp2);
            canvas.drawRoundRect(this.rect, dp2, dp2, this.backgroundPaint);
            ImageReceiver imageReceiver = this.userImageReceiver;
            RectF rectF = this.rect;
            imageReceiver.setImageCoords(rectF.left, rectF.top, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
            this.userImageReceiver.draw(canvas);
            this.text.draw(canvas, AndroidUtilities.dp(36.0f) + this.rect.left, this.rect.centerY(), Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), 1.0f);
            if (this.highlight) {
                float dp3 = this.rect.right - AndroidUtilities.dp(22.66f);
                canvas.drawCircle(dp3, this.rect.centerY(), AndroidUtilities.dp(24.0f), this.backgroundPaint2);
                this.statusImageReceiver.setImageCoords(dp3 - AndroidUtilities.dp(16.0f), this.rect.centerY() - AndroidUtilities.dp(16.0f), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
                this.statusImageReceiver.draw(canvas);
                return;
            }
            float f = this.animatedSwap.set(this.currentStatus);
            canvas.save();
            canvas.translate((int) (this.rect.right - AndroidUtilities.dp(30.66f)), (int) (this.rect.centerY() - AndroidUtilities.dp(12.0f)));
            if (f < 1.0f && (animatedEmojiDrawable2 = this.emojis[0]) != null) {
                canvas.save();
                canvas.translate(0.0f, (this.currentStatus == 0 ? -1 : 1) * AndroidUtilities.dp(9.0f) * f);
                float f2 = 1.0f - f;
                float f3 = (f2 * 0.4f) + 0.6f;
                canvas.scale(f3, f3, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
                animatedEmojiDrawable2.setBounds(0, 0, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                animatedEmojiDrawable2.setAlpha((int) (f2 * 255.0f));
                animatedEmojiDrawable2.draw(canvas);
                canvas.restore();
            }
            if (f > 0.0f && (animatedEmojiDrawable = this.emojis[1]) != null) {
                canvas.save();
                canvas.translate(0.0f, (this.currentStatus == 1 ? -1 : 1) * AndroidUtilities.dp(9.0f) * (1.0f - f));
                float f4 = (0.4f * f) + 0.6f;
                canvas.scale(f4, f4, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
                animatedEmojiDrawable.setBounds(0, 0, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                animatedEmojiDrawable.setAlpha((int) (f * 255.0f));
                animatedEmojiDrawable.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void onAttachedToWindow(ImageReceiver imageReceiver) {
            this.attached = true;
            this.userImageReceiver.onAttachedToWindow();
            this.statusImageReceiver.onAttachedToWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.recentEmojiStatusesUpdate);
            AnimatedEmojiDrawable animatedEmojiDrawable = this.emojis[0];
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(this.view);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.emojis[1];
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.addView(this.view);
            }
        }

        @Override
        public void onDetachedFromWindow(ImageReceiver imageReceiver) {
            this.attached = false;
            this.userImageReceiver.onDetachedFromWindow();
            this.statusImageReceiver.onDetachedFromWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.recentEmojiStatusesUpdate);
            AnimatedEmojiDrawable animatedEmojiDrawable = this.emojis[0];
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.removeView(this.view);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.emojis[1];
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView(this.view);
            }
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public void setParent(View view) {
            this.view = view;
            this.statusImageReceiver.setParentView(view);
            this.userImageReceiver.setParentView(view);
        }

        public void setRandomStatus() {
            AnimatedEmojiDrawable animatedEmojiDrawable;
            TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(UserConfig.selectedAccount).getStickerSet(new TLRPC.TL_inputStickerSetEmojiDefaultStatuses(), false);
            if (stickerSet == null || stickerSet.documents.isEmpty()) {
                this.waitingForStatuses = true;
                return;
            }
            double random = Math.random();
            double size = stickerSet.documents.size();
            Double.isNaN(size);
            TLRPC.Document document = stickerSet.documents.get((int) Math.floor(random * size));
            int i = 1 - this.currentStatus;
            this.currentStatus = i;
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.emojis[i];
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView(this.view);
            }
            this.emojis[this.currentStatus] = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, 9, document);
            this.emojis[this.currentStatus].setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.SRC_IN));
            if (this.attached && (animatedEmojiDrawable = this.emojis[this.currentStatus]) != null) {
                animatedEmojiDrawable.addView(this.view);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SetupEmojiStatusSheet.UserEmojiStatusDrawable.this.lambda$setRandomStatus$0();
                }
            }, 2500L);
        }
    }

    public static void askPermission(final int i, long j, final Utilities.Callback2 callback2) {
        final TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
        TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j);
        if (userFull == null) {
            MessagesController.getInstance(i).loadFullUser(user, 0, true, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    SetupEmojiStatusSheet.lambda$askPermission$9(Utilities.Callback2.this, i, user, (TLRPC.UserFull) obj);
                }
            });
        } else {
            askPermission(i, user, userFull, callback2);
        }
    }

    public static void askPermission(final int i, final TLRPC.User user, final TLRPC.UserFull userFull, final Utilities.Callback2 callback2) {
        if (callback2 == null) {
            return;
        }
        if (userFull.bot_can_manage_emoji_status) {
            callback2.run(Boolean.FALSE, "allowed");
            return;
        }
        Context findActivity = AndroidUtilities.findActivity(LaunchActivity.instance);
        if (findActivity == null) {
            findActivity = ApplicationLoader.applicationContext;
        }
        final Context context = findActivity;
        final boolean[] zArr = new boolean[1];
        final boolean[] zArr2 = new boolean[1];
        AlertDialog create = new AlertDialog.Builder(context, null).setTopImage(new UserEmojiStatusDrawable(UserConfig.getInstance(i).getCurrentUser()), Theme.getColor(Theme.key_dialogTopBackground)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotEmojiStatusPermissionRequest, UserObject.getUserName(user), UserObject.getUserName(user)))).setPositiveButton(LocaleController.getString(R.string.BotEmojiStatusPermissionAllow), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                SetupEmojiStatusSheet.lambda$askPermission$12(i, zArr2, zArr, callback2, context, user, userFull, dialogInterface, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.BotEmojiStatusPermissionDecline), null).create();
        create.show();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                SetupEmojiStatusSheet.lambda$askPermission$13(zArr2, zArr, context, i, user, callback2, dialogInterface);
            }
        });
    }

    public static void clear() {
        Context context = ApplicationLoader.applicationContext;
        if (context == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            context.getSharedPreferences("botemojistatus_" + i, 0).edit().clear().apply();
        }
    }

    public static boolean getAccessRequested(Context context, int i, long j) {
        if (context == null) {
            return false;
        }
        return context.getSharedPreferences("botemojistatus_" + i, 0).getBoolean("requested_" + j, false);
    }

    public static void lambda$askPermission$10(TLObject tLObject, boolean[] zArr, Utilities.Callback2 callback2, TLRPC.UserFull userFull) {
        Boolean bool;
        String str;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            userFull.bot_can_manage_emoji_status = true;
            if (zArr[0]) {
                return;
            }
            zArr[0] = true;
            bool = Boolean.TRUE;
            str = "allowed";
        } else {
            if (zArr[0]) {
                return;
            }
            zArr[0] = true;
            bool = Boolean.TRUE;
            str = "cancelled";
        }
        callback2.run(bool, str);
    }

    public static void lambda$askPermission$11(final boolean[] zArr, final Utilities.Callback2 callback2, final TLRPC.UserFull userFull, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SetupEmojiStatusSheet.lambda$askPermission$10(TLObject.this, zArr, callback2, userFull);
            }
        });
    }

    public static void lambda$askPermission$12(int i, boolean[] zArr, final boolean[] zArr2, final Utilities.Callback2 callback2, Context context, TLRPC.User user, final TLRPC.UserFull userFull, DialogInterface dialogInterface, int i2) {
        if (UserConfig.getInstance(i).isPremium()) {
            zArr[0] = true;
            saveAccessRequested(context, i, user.id);
            TL_bots.toggleUserEmojiStatusPermission toggleuseremojistatuspermission = new TL_bots.toggleUserEmojiStatusPermission();
            toggleuseremojistatuspermission.bot = MessagesController.getInstance(i).getInputUser(user);
            toggleuseremojistatuspermission.enabled = true;
            ConnectionsManager.getInstance(i).sendRequest(toggleuseremojistatuspermission, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SetupEmojiStatusSheet.lambda$askPermission$11(zArr2, callback2, userFull, tLObject, tL_error);
                }
            });
            return;
        }
        new PremiumFeatureBottomSheet(new BaseFragment() {
            @Override
            public Context getContext() {
                return AndroidUtilities.findActivity(LaunchActivity.instance);
            }

            @Override
            public int getCurrentAccount() {
                return this.currentAccount;
            }

            @Override
            public Activity getParentActivity() {
                Activity findActivity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
                return findActivity == null ? LaunchActivity.instance : findActivity;
            }
        }, 12, false).show();
        if (zArr[0] || zArr2[0]) {
            return;
        }
        zArr2[0] = true;
        callback2.run(Boolean.TRUE, "cancelled");
    }

    public static void lambda$askPermission$13(boolean[] zArr, boolean[] zArr2, Context context, int i, TLRPC.User user, Utilities.Callback2 callback2, DialogInterface dialogInterface) {
        if (zArr[0] || zArr2[0]) {
            return;
        }
        zArr2[0] = true;
        saveAccessRequested(context, i, user.id);
        callback2.run(Boolean.TRUE, "cancelled");
    }

    public static void lambda$askPermission$8(TLRPC.UserFull userFull, Utilities.Callback2 callback2, int i, TLRPC.User user) {
        if (userFull == null) {
            callback2.run(Boolean.FALSE, "cancelled");
        } else {
            askPermission(i, user, userFull, callback2);
        }
    }

    public static void lambda$askPermission$9(final Utilities.Callback2 callback2, final int i, final TLRPC.User user, final TLRPC.UserFull userFull) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SetupEmojiStatusSheet.lambda$askPermission$8(TLRPC.UserFull.this, callback2, i, user);
            }
        });
    }

    public static void lambda$show$2(int i, TLRPC.User user, final TLRPC.Document document, int i2, final Utilities.Callback2 callback2) {
        show(i, user, document, i2, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                Utilities.Callback2.this.run((String) obj, document);
            }
        });
    }

    public static void lambda$show$3(final int i, final TLRPC.User user, final int i2, final Utilities.Callback2 callback2, final TLRPC.Document document) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SetupEmojiStatusSheet.lambda$show$2(i, user, document, i2, callback2);
            }
        });
    }

    public static void lambda$show$4(TLObject tLObject, boolean[] zArr, Utilities.Callback callback, int i, TLRPC.TL_account_updateEmojiStatus tL_account_updateEmojiStatus) {
        String str;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
            if (currentUser != null) {
                currentUser.emoji_status = tL_account_updateEmojiStatus.emoji_status;
                NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userEmojiStatusUpdated, currentUser);
                MessagesController.getInstance(i).updateEmojiStatusUntilUpdate(currentUser.id, currentUser.emoji_status);
            }
            if (zArr[0]) {
                return;
            }
            zArr[0] = true;
            str = null;
        } else {
            if (zArr[0]) {
                return;
            }
            zArr[0] = true;
            str = "SERVER_ERROR";
        }
        callback.run(str);
    }

    public static void lambda$show$5(final boolean[] zArr, final Utilities.Callback callback, final int i, final TLRPC.TL_account_updateEmojiStatus tL_account_updateEmojiStatus, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SetupEmojiStatusSheet.lambda$show$4(TLObject.this, zArr, callback, i, tL_account_updateEmojiStatus);
            }
        });
    }

    public static void lambda$show$6(final int i, boolean[] zArr, int i2, TLRPC.Document document, final boolean[] zArr2, final Utilities.Callback callback, DialogInterface dialogInterface, int i3) {
        if (!UserConfig.getInstance(i).isPremium()) {
            new PremiumFeatureBottomSheet(new BaseFragment() {
                @Override
                public Context getContext() {
                    return AndroidUtilities.findActivity(LaunchActivity.instance);
                }

                @Override
                public int getCurrentAccount() {
                    return this.currentAccount;
                }

                @Override
                public Activity getParentActivity() {
                    Activity findActivity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
                    return findActivity == null ? LaunchActivity.instance : findActivity;
                }
            }, 12, false).show();
            return;
        }
        zArr[0] = true;
        final TLRPC.TL_account_updateEmojiStatus tL_account_updateEmojiStatus = new TLRPC.TL_account_updateEmojiStatus();
        if (i2 > 0) {
            TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
            tL_emojiStatusUntil.until = ConnectionsManager.getInstance(i).getCurrentTime() + i2;
            tL_emojiStatusUntil.document_id = document.id;
            tL_account_updateEmojiStatus.emoji_status = tL_emojiStatusUntil;
        } else {
            TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
            tL_emojiStatus.document_id = document.id;
            tL_account_updateEmojiStatus.emoji_status = tL_emojiStatus;
        }
        ConnectionsManager.getInstance(i).sendRequest(tL_account_updateEmojiStatus, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SetupEmojiStatusSheet.lambda$show$5(zArr2, callback, i, tL_account_updateEmojiStatus, tLObject, tL_error);
            }
        });
    }

    public static void lambda$show$7(boolean[] zArr, boolean[] zArr2, Utilities.Callback callback, DialogInterface dialogInterface) {
        if (zArr[0] || zArr2[0]) {
            return;
        }
        zArr2[0] = true;
        callback.run("USER_DECLINED");
    }

    public static void saveAccessRequested(Context context, int i, long j) {
        if (context == null) {
            return;
        }
        context.getSharedPreferences("botemojistatus_" + i, 0).edit().putBoolean("requested_" + j, true).apply();
    }

    public static void show(final int i, final TLRPC.User user, long j, final int i2, final Utilities.Callback2 callback2) {
        if (callback2 == null) {
            return;
        }
        final TLRPC.Document findDocument = AnimatedEmojiDrawable.findDocument(i, j);
        if (findDocument != null) {
            show(i, user, findDocument, i2, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    Utilities.Callback2.this.run((String) obj, findDocument);
                }
            });
        } else {
            AnimatedEmojiDrawable.getDocumentFetcher(i).fetchDocument(j, new AnimatedEmojiDrawable.ReceivedDocument() {
                @Override
                public final void run(TLRPC.Document document) {
                    SetupEmojiStatusSheet.lambda$show$3(i, user, i2, callback2, document);
                }
            });
        }
    }

    public static void show(final int i, TLRPC.User user, final TLRPC.Document document, final int i2, final Utilities.Callback callback) {
        String formatString;
        if (callback == null) {
            return;
        }
        if (document == null || (document instanceof TLRPC.TL_documentEmpty)) {
            callback.run("SUGGESTED_EMOJI_INVALID");
            return;
        }
        Context findActivity = AndroidUtilities.findActivity(LaunchActivity.instance);
        if (findActivity == null) {
            findActivity = ApplicationLoader.applicationContext;
        }
        ConnectionsManager.getInstance(i).getCurrentTime();
        TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
        final boolean[] zArr = new boolean[1];
        final boolean[] zArr2 = new boolean[1];
        if (i2 > 0) {
            int i3 = i2 / 86400;
            int i4 = (i2 - (86400 * i3)) / 3600;
            int round = Math.round((r5 - (i4 * 3600)) / 60.0f);
            StringBuilder sb = new StringBuilder();
            if (i3 > 0) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(LocaleController.formatPluralString("BotEmojiStatusSetRequestForDay", i3, new Object[0]));
            }
            if (i4 > 0) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(LocaleController.formatPluralString("BotEmojiStatusSetRequestForHour", i4, new Object[0]));
            }
            if (round > 0) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(LocaleController.formatPluralString("BotEmojiStatusSetRequestForMinute", round, new Object[0]));
            }
            formatString = LocaleController.formatString(R.string.BotEmojiStatusSetRequestFor, UserObject.getUserName(user), sb);
        } else {
            formatString = LocaleController.formatString(R.string.BotEmojiStatusSetRequest, UserObject.getUserName(user));
        }
        AlertDialog create = new AlertDialog.Builder(findActivity, null).setTopImage(new UserEmojiStatusDrawable(currentUser, document), Theme.getColor(Theme.key_dialogTopBackground)).setMessage(AndroidUtilities.replaceTags(formatString)).setPositiveButton(LocaleController.getString(R.string.BotEmojiStatusConfirm), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i5) {
                SetupEmojiStatusSheet.lambda$show$6(i, zArr2, i2, document, zArr, callback, dialogInterface, i5);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        create.show();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                SetupEmojiStatusSheet.lambda$show$7(zArr2, zArr, callback, dialogInterface);
            }
        });
    }
}
