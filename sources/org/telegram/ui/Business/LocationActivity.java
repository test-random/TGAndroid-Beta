package org.telegram.ui.Business;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ClipRoundedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.LocationActivity;

public class LocationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private String address;
    private boolean clearVisible;
    private TLRPC.TL_businessLocation currentLocation;
    private ActionBarMenuItem doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private EditTextBoldCursor editText;
    private FrameLayout editTextContainer;
    private TLRPC.GeoPoint geo;
    private boolean ignoreEditText;
    private UniversalRecyclerView listView;
    private boolean mapAddress;
    private ClipRoundedDrawable mapLoadingDrawable;
    private View mapMarker;
    private BackupImageView mapPreview;
    private FrameLayout mapPreviewContainer;
    private boolean valueSet;
    final int MAX_NAME_LENGTH = 96;
    private int shiftDp = -4;
    private final int BUTTON_MAP = 1;
    private final int BUTTON_CLEAR = 2;

    public void checkDone(boolean z) {
        if (this.doneButton == null) {
            return;
        }
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        if (z) {
            this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f).scaleY(hasChanges ? 1.0f : 0.0f).setDuration(180L).start();
        } else {
            this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
            this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
            this.doneButton.setScaleY(hasChanges ? 1.0f : 0.0f);
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || universalRecyclerView.adapter == null) {
            return;
        }
        if (this.clearVisible != ((this.currentLocation == null || (this.geo == null && TextUtils.isEmpty(this.address))) ? false : true)) {
            this.listView.adapter.update(true);
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(LocaleController.getString(R.string.BusinessLocationInfo), R.raw.biz_map));
        arrayList.add(UItem.asCustom(this.editTextContainer));
        arrayList.add(UItem.asShadow(null));
        boolean z = false;
        arrayList.add(UItem.asCheck(1, LocaleController.getString(R.string.BusinessLocationMap)).setChecked(this.geo != null));
        if (this.geo != null) {
            arrayList.add(UItem.asCustom(this.mapPreviewContainer));
        }
        arrayList.add(UItem.asShadow(null));
        if (this.currentLocation != null && (this.geo != null || !TextUtils.isEmpty(this.address))) {
            z = true;
        }
        this.clearVisible = z;
        if (z) {
            arrayList.add(UItem.asButton(2, LocaleController.getString(R.string.BusinessLocationClear)).red());
            arrayList.add(UItem.asShadow(null));
        }
        checkDone(true);
    }

    public void lambda$onBackPressed$2(AlertDialog alertDialog, int i) {
        processDone();
    }

    public void lambda$onBackPressed$3(AlertDialog alertDialog, int i) {
        lambda$onBackPressed$323();
    }

    public void lambda$onClick$4(TLRPC.TL_error tL_error, TLObject tLObject) {
        this.doneButtonDrawable.animateToProgress(0.0f);
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else if (tLObject instanceof TLRPC.TL_boolFalse) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        } else {
            lambda$onBackPressed$323();
        }
    }

    public void lambda$onClick$5(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LocationActivity.this.lambda$onClick$4(tL_error, tLObject);
            }
        });
    }

    public void lambda$onClick$6(AlertDialog alertDialog, int i) {
        this.doneButtonDrawable.animateToProgress(1.0f);
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        TL_account.updateBusinessLocation updatebusinesslocation = new TL_account.updateBusinessLocation();
        if (userFull != null) {
            userFull.business_location = null;
            userFull.flags2 &= -3;
        }
        getConnectionsManager().sendRequest(updatebusinesslocation, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationActivity.this.lambda$onClick$5(tLObject, tL_error);
            }
        });
    }

    public void lambda$processDone$0(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.showError(tL_error);
        } else if (!(tLObject instanceof TLRPC.TL_boolFalse)) {
            lambda$onBackPressed$323();
        } else {
            this.doneButtonDrawable.animateToProgress(0.0f);
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$processDone$1(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LocationActivity.this.lambda$processDone$0(tL_error, tLObject);
            }
        });
    }

    public void lambda$showLocationAlert$7(org.telegram.ui.LocationActivity locationActivity, TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        this.geo = messageMedia.geo;
        if ((TextUtils.isEmpty(this.address) && !TextUtils.isEmpty(locationActivity.getAddressName())) || this.mapAddress) {
            this.mapAddress = true;
            String addressName = locationActivity.getAddressName();
            this.address = addressName;
            if (addressName == null) {
                this.address = "";
            }
            EditTextBoldCursor editTextBoldCursor = this.editText;
            if (editTextBoldCursor != null) {
                this.ignoreEditText = true;
                editTextBoldCursor.setText(this.address);
                EditTextBoldCursor editTextBoldCursor2 = this.editText;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.getText().length());
                this.ignoreEditText = false;
            }
        }
        updateMapPreview();
        this.listView.adapter.update(true);
        checkDone(true);
    }

    public void lambda$showLocationAlert$8(AlertDialog alertDialog, org.telegram.ui.LocationActivity locationActivity) {
        alertDialog.dismiss();
        presentFragment(locationActivity);
    }

    public void lambda$showLocationAlert$9(final org.telegram.ui.LocationActivity locationActivity, final AlertDialog alertDialog) {
        try {
            List<Address> fromLocationName = new Geocoder(getContext(), LocaleController.getInstance().getCurrentLocale()).getFromLocationName(this.address, 1);
            if (!fromLocationName.isEmpty()) {
                Address address = fromLocationName.get(0);
                TLRPC.TL_channelLocation tL_channelLocation = new TLRPC.TL_channelLocation();
                tL_channelLocation.address = this.address;
                TLRPC.TL_geoPoint tL_geoPoint = new TLRPC.TL_geoPoint();
                tL_channelLocation.geo_point = tL_geoPoint;
                tL_geoPoint.lat = address.getLatitude();
                tL_channelLocation.geo_point._long = address.getLongitude();
                locationActivity.setInitialLocation(tL_channelLocation);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                LocationActivity.this.lambda$showLocationAlert$8(alertDialog, locationActivity);
            }
        });
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 == 1 || uItem.view == this.mapPreviewContainer) {
            if (this.geo == null || uItem.view == this.mapPreviewContainer) {
                showLocationAlert();
                return;
            } else {
                this.geo = null;
                this.listView.adapter.update(true);
                return;
            }
        }
        if (i2 == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.BusinessLocationClearTitle));
            builder.setMessage(LocaleController.getString(R.string.BusinessLocationClearMessage));
            builder.setPositiveButton(LocaleController.getString(R.string.Remove), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i3) {
                    LocationActivity.this.lambda$onClick$6(alertDialog, i3);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    public void processDone() {
        if (this.doneButtonDrawable.getProgress() > 0.0f) {
            return;
        }
        boolean z = this.geo == null && TextUtils.isEmpty(this.address);
        if (!z) {
            if (!hasChanges()) {
                lambda$onBackPressed$323();
                return;
            }
            String str = this.address;
            String trim = str == null ? "" : str.trim();
            if (TextUtils.isEmpty(trim) || trim.length() > 96) {
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                EditTextBoldCursor editTextBoldCursor = this.editText;
                int i = -this.shiftDp;
                this.shiftDp = i;
                AndroidUtilities.shakeViewSpring(editTextBoldCursor, i);
                return;
            }
        }
        this.doneButtonDrawable.animateToProgress(1.0f);
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        TL_account.updateBusinessLocation updatebusinesslocation = new TL_account.updateBusinessLocation();
        if (!z) {
            if (this.geo != null) {
                updatebusinesslocation.flags |= 2;
                TLRPC.TL_inputGeoPoint tL_inputGeoPoint = new TLRPC.TL_inputGeoPoint();
                updatebusinesslocation.geo_point = tL_inputGeoPoint;
                TLRPC.GeoPoint geoPoint = this.geo;
                tL_inputGeoPoint.lat = geoPoint.lat;
                tL_inputGeoPoint._long = geoPoint._long;
            }
            updatebusinesslocation.flags |= 1;
            updatebusinesslocation.address = this.address;
            if (userFull != null) {
                userFull.flags2 |= 2;
                TLRPC.TL_businessLocation tL_businessLocation = new TLRPC.TL_businessLocation();
                userFull.business_location = tL_businessLocation;
                tL_businessLocation.address = this.address;
                if (this.geo != null) {
                    tL_businessLocation.flags = 1 | tL_businessLocation.flags;
                    tL_businessLocation.geo_point = new TLRPC.TL_geoPoint();
                    TLRPC.GeoPoint geoPoint2 = userFull.business_location.geo_point;
                    TLRPC.GeoPoint geoPoint3 = this.geo;
                    geoPoint2.lat = geoPoint3.lat;
                    geoPoint2._long = geoPoint3._long;
                }
            }
        } else if (userFull != null) {
            userFull.flags2 &= -3;
            userFull.business_location = null;
        }
        getConnectionsManager().sendRequest(updatebusinesslocation, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LocationActivity.this.lambda$processDone$1(tLObject, tL_error);
            }
        });
        getMessagesStorage().updateUserInfo(userFull, false);
    }

    private void setValue() {
        String str;
        UniversalAdapter universalAdapter;
        if (this.valueSet) {
            return;
        }
        TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        if (userFull == null) {
            getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, getClassGuid());
            return;
        }
        TLRPC.TL_businessLocation tL_businessLocation = userFull.business_location;
        this.currentLocation = tL_businessLocation;
        if (tL_businessLocation != null) {
            this.geo = tL_businessLocation.geo_point;
            str = tL_businessLocation.address;
        } else {
            this.geo = null;
            str = "";
        }
        this.address = str;
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            this.ignoreEditText = true;
            editTextBoldCursor.setText(this.address);
            EditTextBoldCursor editTextBoldCursor2 = this.editText;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.getText().length());
            this.ignoreEditText = false;
        }
        updateMapPreview();
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        this.valueSet = true;
    }

    private void showLocationAlert() {
        final org.telegram.ui.LocationActivity locationActivity = new org.telegram.ui.LocationActivity(8);
        if (this.geo != null) {
            TLRPC.TL_channelLocation tL_channelLocation = new TLRPC.TL_channelLocation();
            tL_channelLocation.address = this.address;
            tL_channelLocation.geo_point = this.geo;
            locationActivity.setInitialLocation(tL_channelLocation);
        }
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
            @Override
            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                LocationActivity.this.lambda$showLocationAlert$7(locationActivity, messageMedia, i, z, i2);
            }
        });
        if (this.geo != null || TextUtils.isEmpty(this.address)) {
            presentFragment(locationActivity);
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.showDelayed(200L);
        Utilities.searchQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LocationActivity.this.lambda$showLocationAlert$9(locationActivity, alertDialog);
            }
        });
    }

    private void updateMapPreview() {
        BackupImageView backupImageView;
        View view = this.mapMarker;
        if (view == null || (backupImageView = this.mapPreview) == null) {
            return;
        }
        if (this.geo == null) {
            backupImageView.setImageBitmap(null);
            return;
        }
        view.setAlpha(0.0f);
        this.mapMarker.setTranslationY(-AndroidUtilities.dp(12.0f));
        int measuredWidth = this.mapPreview.getMeasuredWidth() <= 0 ? AndroidUtilities.displaySize.x : this.mapPreview.getMeasuredWidth();
        float f = AndroidUtilities.density;
        int i = (int) (measuredWidth / f);
        int min = Math.min(2, (int) Math.ceil(f));
        BackupImageView backupImageView2 = this.mapPreview;
        TLRPC.GeoPoint geoPoint = this.geo;
        backupImageView2.setImage(ImageLocation.getForWebFile(WebFile.createWithGeoPoint(geoPoint.lat, geoPoint._long, 0L, min * i, min * 240, 15, min)), i + "_240", this.mapLoadingDrawable, 0, (Object) null);
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.BusinessLocation));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    if (LocationActivity.this.onBackPressed()) {
                        LocationActivity.this.lambda$onBackPressed$323();
                    }
                } else if (i == 1) {
                    LocationActivity.this.processDone();
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
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext()) {
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
                this.limit.setTextColor(this.limitColor.set(Theme.getColor(this.limitCount < 0 ? Theme.key_text_RedRegular : Theme.key_dialogSearchHint, LocationActivity.this.getResourceProvider())));
                this.limit.setBounds(getScrollX(), 0, getScrollX() + getWidth(), getHeight());
                this.limit.draw(canvas);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                super.onTextChanged(charSequence, i2, i3, i4);
                if (this.limit != null) {
                    this.limitCount = 96 - charSequence.length();
                    this.limit.cancelAnimation();
                    AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.limit;
                    String str = "";
                    if (this.limitCount <= 12) {
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
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 17.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        EditTextBoldCursor editTextBoldCursor2 = this.editText;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        editTextBoldCursor2.setTextColor(Theme.getColor(i2));
        this.editText.setBackgroundDrawable(null);
        this.editText.setMaxLines(5);
        this.editText.setSingleLine(false);
        this.editText.setPadding(0, 0, AndroidUtilities.dp(42.0f), 0);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.editText.setInputType(180225);
        this.editText.setHint(LocaleController.getString(R.string.BusinessLocationAddress));
        this.editText.setCursorColor(Theme.getColor(i2));
        this.editText.setCursorSize(AndroidUtilities.dp(19.0f));
        this.editText.setCursorWidth(1.5f);
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (LocationActivity.this.ignoreEditText) {
                    return;
                }
                LocationActivity.this.mapAddress = false;
                LocationActivity.this.address = editable.toString();
                LocationActivity.this.checkDone(true);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }
        });
        this.editText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i3, int i4, Spanned spanned, int i5, int i6) {
                if (charSequence == null) {
                    return null;
                }
                String charSequence2 = charSequence.toString();
                if (charSequence2.contains("\n")) {
                    return charSequence2.replaceAll("\n", "");
                }
                return null;
            }
        }});
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.editTextContainer = frameLayout2;
        frameLayout2.addView(this.editText, LayoutHelper.createFrame(-1, -1.0f, 48, 21.0f, 15.0f, 21.0f, 15.0f));
        FrameLayout frameLayout3 = this.editTextContainer;
        int i3 = Theme.key_windowBackgroundWhite;
        frameLayout3.setBackgroundColor(getThemedColor(i3));
        EditTextBoldCursor editTextBoldCursor3 = this.editText;
        if (editTextBoldCursor3 != null) {
            this.ignoreEditText = true;
            editTextBoldCursor3.setText(this.address);
            EditTextBoldCursor editTextBoldCursor4 = this.editText;
            editTextBoldCursor4.setSelection(editTextBoldCursor4.getText().length());
            this.ignoreEditText = false;
        }
        this.mapPreview = new BackupImageView(context) {
            @Override
            protected ImageReceiver createImageReciever() {
                return new ImageReceiver(this) {
                    @Override
                    public boolean setImageBitmapByKey(Drawable drawable, String str, int i4, boolean z, int i5) {
                        if (drawable != null && i4 != 1) {
                            LocationActivity.this.mapMarker.animate().alpha(1.0f).translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_BACK).setDuration(250L).start();
                        }
                        return super.setImageBitmapByKey(drawable, str, i4, z, i5);
                    }
                };
            }

            @Override
            protected void onMeasure(int i4, int i5) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i4), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(240.0f), 1073741824));
            }

            @Override
            protected boolean verifyDrawable(Drawable drawable) {
                return drawable == LocationActivity.this.mapLoadingDrawable || super.verifyDrawable(drawable);
            }
        };
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(R.raw.map_placeholder, Theme.key_chat_outLocationIcon, 0.2f);
        svgThumb.setColorKey(i2, getResourceProvider());
        svgThumb.setAspectCenter(true);
        svgThumb.setParent(this.mapPreview.getImageReceiver());
        ClipRoundedDrawable clipRoundedDrawable = new ClipRoundedDrawable(svgThumb);
        this.mapLoadingDrawable = clipRoundedDrawable;
        clipRoundedDrawable.setCallback(this.mapPreview);
        this.mapPreview.setBackgroundColor(getThemedColor(i3));
        this.mapMarker = new View(context) {
            final AvatarDrawable avatarDrawable;
            final ImageReceiver avatarImage;
            final Drawable pin = getContext().getResources().getDrawable(R.drawable.map_pin_photo).mutate();

            {
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                this.avatarDrawable = avatarDrawable;
                ImageReceiver imageReceiver = new ImageReceiver(this);
                this.avatarImage = imageReceiver;
                avatarDrawable.setInfo(LocationActivity.this.getUserConfig().getCurrentUser());
                imageReceiver.setForUserOrChat(LocationActivity.this.getUserConfig().getCurrentUser(), avatarDrawable);
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                this.pin.setBounds(0, 0, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(85.0f));
                this.pin.draw(canvas);
                this.avatarImage.setRoundRadius(AndroidUtilities.dp(62.0f));
                this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f));
                this.avatarImage.draw(canvas);
            }

            @Override
            protected void onMeasure(int i4, int i5) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), 1073741824));
            }
        };
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.mapPreviewContainer = frameLayout4;
        frameLayout4.addView(this.mapPreview, LayoutHelper.createFrame(-1, -1.0f));
        this.mapPreviewContainer.addView(this.mapMarker, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, -31.0f, 0.0f, 0.0f));
        updateMapPreview();
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                LocationActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                LocationActivity.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
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
        if (i == NotificationCenter.userInfoDidLoad) {
            setValue();
        }
    }

    public boolean hasChanges() {
        TLRPC.GeoPoint geoPoint;
        if (((this.geo == null && TextUtils.isEmpty(this.address)) ? false : true) != (this.currentLocation != null)) {
            return true;
        }
        boolean z = (this.geo == null && TextUtils.isEmpty(this.address)) ? false : true;
        TLRPC.TL_businessLocation tL_businessLocation = this.currentLocation;
        if (z != ((tL_businessLocation == null || (tL_businessLocation.geo_point instanceof TLRPC.TL_geoPointEmpty)) ? false : true)) {
            return true;
        }
        if (!TextUtils.equals(this.address, tL_businessLocation != null ? tL_businessLocation.address : "")) {
            return true;
        }
        TLRPC.GeoPoint geoPoint2 = this.geo;
        boolean z2 = geoPoint2 != null;
        TLRPC.TL_businessLocation tL_businessLocation2 = this.currentLocation;
        if (z2 != ((tL_businessLocation2 == null || tL_businessLocation2.geo_point == null) ? false : true)) {
            return true;
        }
        return geoPoint2 != null && (tL_businessLocation2 == null || (geoPoint = tL_businessLocation2.geo_point) == null || !((geoPoint instanceof TLRPC.TL_geoPointEmpty) || (geoPoint2.lat == geoPoint.lat && geoPoint2._long == geoPoint._long)));
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return !hasChanges();
    }

    @Override
    public boolean onBackPressed() {
        boolean z = this.geo == null && TextUtils.isEmpty(this.address);
        if (!hasChanges() || z) {
            return super.onBackPressed();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.UnsavedChanges));
        builder.setMessage(LocaleController.getString(R.string.BusinessLocationUnsavedChanges));
        builder.setPositiveButton(LocaleController.getString(R.string.ApplyTheme), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                LocationActivity.this.lambda$onBackPressed$2(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                LocationActivity.this.lambda$onBackPressed$3(alertDialog, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        super.onFragmentDestroy();
    }
}
