package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.IMapsProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.LocationActivity;

public class SharingLiveLocationCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private LocationController.SharingLocationInfo currentInfo;
    private TextView distanceTextView;
    private int distanceTextViewHeight;
    private boolean distanceTextViewSingle;
    private Drawable foreverDrawable;
    private int foreverDrawableColor;
    private Runnable invalidateRunnable;
    private double lastLat;
    private double lastLong;
    private CharSequence lastName;
    private LocationActivity.LiveLocation liveLocation;
    private boolean loading;
    private SpannableString loadingString;
    private Location location;
    private SimpleTextView nameTextView;
    private int padding;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;

    public SharingLiveLocationCell(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        View view;
        int i2;
        float f;
        int i3;
        float f2;
        int i4;
        float f3;
        float f4;
        this.rect = new RectF();
        this.location = new Location("network");
        this.currentAccount = UserConfig.selectedAccount;
        this.invalidateRunnable = new Runnable() {
            @Override
            public void run() {
                SharingLiveLocationCell.this.invalidate(((int) r0.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
                AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000L);
            }
        };
        this.lastName = "";
        this.resourcesProvider = resourcesProvider;
        this.padding = i;
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        NotificationCenter.listenEmojiLoading(simpleTextView);
        this.nameTextView.setTextSize(16);
        this.nameTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setScrollNonFitText(true);
        if (z) {
            BackupImageView backupImageView2 = this.avatarImageView;
            boolean z2 = LocaleController.isRTL;
            addView(backupImageView2, LayoutHelper.createFrame(42, 42.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 15.0f, 12.0f, z2 ? 15.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView2 = this.nameTextView;
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? i : 73.0f, 12.0f, z3 ? 73.0f : 16.0f, 0.0f));
            TextView textView = new TextView(context);
            this.distanceTextView = textView;
            textView.setSingleLine();
            this.distanceTextViewSingle = true;
            this.distanceTextView.setTextSize(1, 14.0f);
            this.distanceTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            view = this.distanceTextView;
            boolean z4 = LocaleController.isRTL;
            i2 = (z4 ? 5 : 3) | 48;
            f = z4 ? i : 73.0f;
            i3 = -1;
            f2 = 33.0f;
            if (z4) {
                i4 = -1;
                f3 = 33.0f;
                f4 = 73.0f;
            }
            i4 = i3;
            f3 = f2;
            f4 = i;
        } else {
            BackupImageView backupImageView3 = this.avatarImageView;
            boolean z5 = LocaleController.isRTL;
            addView(backupImageView3, LayoutHelper.createFrame(42, 42.0f, (z5 ? 5 : 3) | 48, z5 ? 0.0f : 15.0f, 6.0f, z5 ? 15.0f : 0.0f, 0.0f));
            view = this.nameTextView;
            boolean z6 = LocaleController.isRTL;
            i2 = (z6 ? 5 : 3) | 48;
            f = z6 ? i : 74.0f;
            i3 = -2;
            f2 = 17.0f;
            if (z6) {
                i4 = -2;
                f3 = 17.0f;
                f4 = 74.0f;
            }
            i4 = i3;
            f3 = f2;
            f4 = i;
        }
        addView(view, LayoutHelper.createFrame(i4, -2.0f, i2, f, f3, f4, 0.0f));
        setWillNotDraw(false);
    }

    private CharSequence getName(final double d, final double d2) {
        if (this.loading) {
            return this.lastName;
        }
        if (Math.abs(this.lastLat - d) > 1.0E-6d || Math.abs(this.lastLong - d2) > 1.0E-6d || TextUtils.isEmpty(this.lastName)) {
            this.loading = true;
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    SharingLiveLocationCell.this.lambda$getName$1(d, d2);
                }
            });
        }
        return this.lastName;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$getName$0(double d, double d2) {
        this.lastLat = d;
        this.lastLong = d2;
        this.loading = false;
        CharSequence replaceEmoji = Emoji.replaceEmoji(this.lastName, this.nameTextView.getPaint().getFontMetricsInt(), false);
        this.lastName = replaceEmoji;
        this.nameTextView.setText(replaceEmoji);
    }

    public void lambda$getName$1(final double d, final double d2) {
        List<Address> fromLocation;
        String str;
        try {
            fromLocation = new Geocoder(ApplicationLoader.applicationContext, LocaleController.getInstance().getCurrentLocale()).getFromLocation(d, d2, 1);
        } catch (Exception unused) {
        }
        if (!fromLocation.isEmpty()) {
            Address address = fromLocation.get(0);
            StringBuilder sb = new StringBuilder();
            HashSet hashSet = new HashSet();
            hashSet.add(address.getSubAdminArea());
            hashSet.add(address.getAdminArea());
            hashSet.add(address.getLocality());
            hashSet.add(address.getCountryName());
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (!TextUtils.isEmpty(str2)) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(str2);
                }
            }
            this.lastName = sb.toString();
            String countryCodeToEmoji = LocationController.countryCodeToEmoji(address.getCountryCode());
            if (countryCodeToEmoji != null && Emoji.getEmojiDrawable(countryCodeToEmoji) != null) {
                str = countryCodeToEmoji + " " + ((Object) this.lastName);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SharingLiveLocationCell.this.lambda$getName$0(d, d2);
                }
            });
        }
        String detectOcean = LocationController.detectOcean(d2, d);
        this.lastName = detectOcean;
        if (detectOcean == null) {
            str = "";
        } else {
            str = "🌊 " + ((Object) this.lastName);
        }
        this.lastName = str;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SharingLiveLocationCell.this.lambda$getName$0(d, d2);
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i;
        int i2;
        LocationController.SharingLocationInfo sharingLocationInfo = this.currentInfo;
        if (sharingLocationInfo == null && this.liveLocation == null) {
            return;
        }
        if (sharingLocationInfo != null) {
            i2 = sharingLocationInfo.stopTime;
            i = sharingLocationInfo.period;
        } else {
            TLRPC.Message message = this.liveLocation.object;
            int i3 = message.date;
            i = message.media.period;
            i2 = i3 + i;
        }
        boolean z = i == Integer.MAX_VALUE;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (i2 >= currentTime || z) {
            float abs = z ? 1.0f : Math.abs(i2 - currentTime) / i;
            if (LocaleController.isRTL) {
                this.rect.set(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(this.distanceTextView != null ? 18.0f : 12.0f), AndroidUtilities.dp(43.0f), AndroidUtilities.dp(this.distanceTextView != null ? 48.0f : 42.0f));
            } else {
                this.rect.set(getMeasuredWidth() - AndroidUtilities.dp(43.0f), AndroidUtilities.dp(this.distanceTextView != null ? 18.0f : 12.0f), getMeasuredWidth() - AndroidUtilities.dp(13.0f), AndroidUtilities.dp(this.distanceTextView != null ? 48.0f : 42.0f));
            }
            int themedColor = getThemedColor(this.distanceTextView == null ? Theme.key_dialog_liveLocationProgress : Theme.key_location_liveLocationProgress);
            Theme.chat_radialProgress2Paint.setColor(themedColor);
            Theme.chat_livePaint.setColor(themedColor);
            int alpha = Theme.chat_radialProgress2Paint.getAlpha();
            Theme.chat_radialProgress2Paint.setAlpha((int) (alpha * 0.2f));
            canvas.drawArc(this.rect, -90.0f, 360.0f, false, Theme.chat_radialProgress2Paint);
            Theme.chat_radialProgress2Paint.setAlpha(alpha);
            canvas.drawArc(this.rect, -90.0f, abs * (-360.0f), false, Theme.chat_radialProgress2Paint);
            Theme.chat_radialProgress2Paint.setAlpha(alpha);
            if (!z) {
                String formatLocationLeftTime = LocaleController.formatLocationLeftTime(i2 - currentTime);
                canvas.drawText(formatLocationLeftTime, this.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
                return;
            }
            if (this.foreverDrawable == null) {
                this.foreverDrawable = getContext().getResources().getDrawable(R.drawable.filled_location_forever).mutate();
            }
            if (Theme.chat_livePaint.getColor() != this.foreverDrawableColor) {
                Drawable drawable = this.foreverDrawable;
                int color = Theme.chat_livePaint.getColor();
                this.foreverDrawableColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
            this.foreverDrawable.setBounds(((int) this.rect.centerX()) - (this.foreverDrawable.getIntrinsicWidth() / 2), ((int) this.rect.centerY()) - (this.foreverDrawable.getIntrinsicHeight() / 2), ((int) this.rect.centerX()) + (this.foreverDrawable.getIntrinsicWidth() / 2), ((int) this.rect.centerY()) + (this.foreverDrawable.getIntrinsicHeight() / 2));
            this.foreverDrawable.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f) + ((this.distanceTextView == null || this.distanceTextViewSingle) ? 0 : (-AndroidUtilities.dp(20.0f)) + this.distanceTextViewHeight), 1073741824));
    }

    public void setDialog(long j, TLRPC.TL_channelLocation tL_channelLocation) {
        String str;
        this.currentAccount = UserConfig.selectedAccount;
        String str2 = tL_channelLocation.address;
        this.avatarDrawable = null;
        if (DialogObject.isUserDialog(j)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable(user);
                str = UserObject.getUserName(user);
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            }
            str = "";
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
            if (chat != null) {
                AvatarDrawable avatarDrawable = new AvatarDrawable(chat);
                this.avatarDrawable = avatarDrawable;
                String str3 = chat.title;
                this.avatarImageView.setForUserOrChat(chat, avatarDrawable);
                str = str3;
            }
            str = "";
        }
        this.nameTextView.setText(str);
        this.location.setLatitude(tL_channelLocation.geo_point.lat);
        this.location.setLongitude(tL_channelLocation.geo_point._long);
        TextView textView = this.distanceTextView;
        this.distanceTextViewSingle = true;
        textView.setSingleLine(true);
        this.distanceTextView.setText(str2);
    }

    public void setDialog(LocationController.SharingLocationInfo sharingLocationInfo) {
        SimpleTextView simpleTextView;
        String str;
        TLRPC.Chat chat;
        this.currentInfo = sharingLocationInfo;
        this.currentAccount = sharingLocationInfo.account;
        this.avatarImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
        if (DialogObject.isUserDialog(sharingLocationInfo.did)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(sharingLocationInfo.did));
            if (user == 0) {
                return;
            }
            this.avatarDrawable.setInfo(this.currentAccount, user);
            simpleTextView = this.nameTextView;
            str = ContactsController.formatName(user.first_name, user.last_name);
            chat = user;
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-sharingLocationInfo.did));
            if (chat2 == null) {
                return;
            }
            this.avatarDrawable.setInfo(this.currentAccount, chat2);
            simpleTextView = this.nameTextView;
            str = chat2.title;
            chat = chat2;
        }
        simpleTextView.setText(str);
        this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
    }

    public void setDialog(MessageObject messageObject, Location location, boolean z) {
        CharSequence charSequence;
        TLRPC.Message message;
        if (messageObject != null && (message = messageObject.messageOwner) != null && message.local_id == -1) {
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
            int themedColor = getThemedColor(Theme.key_location_placeLocationBackground);
            CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), themedColor, themedColor), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
            this.nameTextView.setText(Emoji.replaceEmoji(MessagesController.getInstance(this.currentAccount).getPeerName(DialogObject.getPeerDialogId(messageObject.messageOwner.peer_id)), this.nameTextView.getPaint().getFontMetricsInt(), false));
            TextView textView = this.distanceTextView;
            this.distanceTextViewSingle = false;
            textView.setSingleLine(false);
            String str = messageObject.messageOwner.media.address;
            this.distanceTextViewHeight = new StaticLayout(str, this.distanceTextView.getPaint(), AndroidUtilities.displaySize.x - AndroidUtilities.dp(this.padding + 73), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false).getHeight();
            this.distanceTextView.setText(str);
            requestLayout();
            return;
        }
        TextView textView2 = this.distanceTextView;
        this.distanceTextViewSingle = true;
        textView2.setSingleLine(true);
        long fromChatId = messageObject.getFromChatId();
        if (messageObject.isForwarded()) {
            fromChatId = MessageObject.getPeerId(messageObject.messageOwner.fwd_from.from_id);
        }
        this.currentAccount = messageObject.currentAccount;
        String str2 = !TextUtils.isEmpty(messageObject.messageOwner.media.address) ? messageObject.messageOwner.media.address : null;
        boolean isEmpty = TextUtils.isEmpty(messageObject.messageOwner.media.title);
        if (isEmpty) {
            this.avatarDrawable = null;
            if (fromChatId > 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromChatId));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable(user);
                    charSequence = UserObject.getUserName(user);
                    this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                }
                TLRPC.GeoPoint geoPoint = messageObject.messageOwner.media.geo;
                charSequence = getName(geoPoint.lat, geoPoint._long);
                isEmpty = false;
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromChatId));
                if (chat != null) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable(chat);
                    this.avatarDrawable = avatarDrawable;
                    String str3 = chat.title;
                    this.avatarImageView.setForUserOrChat(chat, avatarDrawable);
                    charSequence = str3;
                }
                TLRPC.GeoPoint geoPoint2 = messageObject.messageOwner.media.geo;
                charSequence = getName(geoPoint2.lat, geoPoint2._long);
                isEmpty = false;
            }
        } else {
            charSequence = "";
        }
        if (TextUtils.isEmpty(charSequence)) {
            if (this.loadingString == null) {
                SpannableString spannableString = new SpannableString("dkaraush has been here");
                this.loadingString = spannableString;
                spannableString.setSpan(new LoadingSpan(this.nameTextView, AndroidUtilities.dp(100.0f), 0, this.resourcesProvider), 0, this.loadingString.length(), 33);
            }
            charSequence = this.loadingString;
        }
        if (!isEmpty) {
            if (!TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
                charSequence = messageObject.messageOwner.media.title;
            }
            Drawable drawable2 = getResources().getDrawable(R.drawable.pin);
            drawable2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
            int themedColor2 = getThemedColor(Theme.key_location_placeLocationBackground);
            CombinedDrawable combinedDrawable2 = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), themedColor2, themedColor2), drawable2);
            combinedDrawable2.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable2.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable2);
        }
        this.nameTextView.setText(charSequence);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (location != null) {
            float distanceTo = this.location.distanceTo(location);
            TextView textView3 = this.distanceTextView;
            String formatDistance = LocaleController.formatDistance(distanceTo, 0);
            if (str2 != null) {
                formatDistance = String.format("%s - %s", str2, formatDistance);
            }
            textView3.setText(formatDistance);
            return;
        }
        TextView textView4 = this.distanceTextView;
        if (str2 != null) {
            textView4.setText(str2);
        } else if (z) {
            textView4.setText("");
        } else {
            textView4.setText(LocaleController.getString(R.string.Loading));
        }
    }

    public void setDialog(LocationActivity.LiveLocation liveLocation, Location location) {
        SimpleTextView simpleTextView;
        String str;
        TLRPC.Chat chat;
        this.liveLocation = liveLocation;
        if (DialogObject.isUserDialog(liveLocation.id)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(liveLocation.id));
            if (user != 0) {
                this.avatarDrawable.setInfo(this.currentAccount, user);
                simpleTextView = this.nameTextView;
                str = ContactsController.formatName(user.first_name, user.last_name);
                chat = user;
                simpleTextView.setText(str);
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-liveLocation.id));
            if (chat2 != null) {
                this.avatarDrawable.setInfo(this.currentAccount, chat2);
                simpleTextView = this.nameTextView;
                str = chat2.title;
                chat = chat2;
                simpleTextView.setText(str);
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        }
        IMapsProvider.LatLng position = liveLocation.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        int i = liveLocation.object.edit_date;
        String formatLocationUpdateDate = LocaleController.formatLocationUpdateDate(i != 0 ? i : r6.date);
        if (location != null) {
            this.distanceTextView.setText(String.format("%s - %s", formatLocationUpdateDate, LocaleController.formatDistance(this.location.distanceTo(location), 0)));
        } else {
            this.distanceTextView.setText(formatLocationUpdateDate);
        }
    }
}
