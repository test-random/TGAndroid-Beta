package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SMSJobController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_boolFalse;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TL_smsjobs$TL_smsjobs_eligibleToJoin;
import org.telegram.tgnet.TL_smsjobs$TL_smsjobs_join;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
public class SMSSubscribeSheet {
    private static LongSparseArray<Utilities.Callback<Boolean>> permissionsCallbacks;

    public static void lambda$requestSMSPermissions$13(DialogInterface dialogInterface) {
    }

    public static void lambda$requestSMSPermissions$16(DialogInterface dialogInterface) {
    }

    public static BottomSheet show(final Context context, final TL_smsjobs$TL_smsjobs_eligibleToJoin tL_smsjobs$TL_smsjobs_eligibleToJoin, final Runnable runnable, final Theme.ResourcesProvider resourcesProvider) {
        final BottomSheet bottomSheet = new BottomSheet(context, false, resourcesProvider);
        bottomSheet.fixNavigationBar(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.drawable.large_sms_code);
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_featuredStickers_addButton, resourcesProvider)));
        linearLayout.addView(imageView, LayoutHelper.createLinear(80, 80, 1, 0, 24, 0, 12));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        textView.setTextAlignment(4);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setText(LocaleController.getString((int) R.string.SmsSubscribeTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 30, 0, 30, 6));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        textView2.setTextAlignment(4);
        int i = Theme.key_windowBackgroundWhiteGrayText4;
        textView2.setTextColor(Theme.getColor(i, resourcesProvider));
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsSubscribeMessage)));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 30, 0, 30, 14));
        SMSSubscribeSheet$$ExternalSyntheticLambda15 sMSSubscribeSheet$$ExternalSyntheticLambda15 = new Runnable() {
            @Override
            public final void run() {
                SMSSubscribeSheet.lambda$show$0();
            }
        };
        linearLayout.addView(new FeatureCell(context, R.drawable.menu_feature_sms, LocaleController.getString((int) R.string.SmsSubscribeFeature1Title), LocaleController.formatPluralString("SmsSubscribeFeature1Message", tL_smsjobs$TL_smsjobs_eligibleToJoin == null ? 100 : tL_smsjobs$TL_smsjobs_eligibleToJoin.monthly_sent_sms, new Object[0]), resourcesProvider), LayoutHelper.createLinear(-1, -2, 1, 30, 16, 30, 0));
        linearLayout.addView(new FeatureCell(context, R.drawable.menu_feature_premium, LocaleController.getString((int) R.string.SmsSubscribeFeature2Title), AndroidUtilities.replaceSingleTag(LocaleController.getString((int) R.string.SmsSubscribeFeature2Message), sMSSubscribeSheet$$ExternalSyntheticLambda15), resourcesProvider), LayoutHelper.createLinear(-1, -2, 1, 30, 16, 30, 0));
        linearLayout.addView(new FeatureCell(context, R.drawable.menu_feature_gift, LocaleController.getString((int) R.string.SmsSubscribeFeature3Title), LocaleController.getString((int) R.string.SmsSubscribeFeature3Message), resourcesProvider), LayoutHelper.createLinear(-1, -2, 1, 30, 16, 30, 0));
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                SMSSubscribeSheet.lambda$show$1(TL_smsjobs$TL_smsjobs_eligibleToJoin.this, context);
            }
        };
        final FrameLayout frameLayout = new FrameLayout(context);
        final CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context, false);
        checkBoxSquare.setDuplicateParentStateEnabled(false);
        checkBoxSquare.setFocusable(false);
        checkBoxSquare.setFocusableInTouchMode(false);
        checkBoxSquare.setClickable(false);
        frameLayout.addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 5 : 3) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        int i2 = Theme.key_windowBackgroundWhiteLinkText;
        linksTextView.setLinkTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setMaxLines(2);
        linksTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        linksTextView.setEllipsize(TextUtils.TruncateAt.END);
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString((int) R.string.SmsSubscribeAccept), runnable2));
        boolean z = LocaleController.isRTL;
        frameLayout.addView(linksTextView, LayoutHelper.createFrame(-1, -1.0f, (z ? 5 : 3) | 48, z ? 16.0f : 58.0f, 21.0f, z ? 58.0f : 16.0f, 21.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 9.0f, 0.0f, 9.0f, 0.0f));
        final ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString((int) R.string.SmsSubscribeActivate), false);
        buttonWithCounterView.setEnabled(false);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 14.0f, 0.0f, 14.0f, 0.0f));
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SMSSubscribeSheet.lambda$show$2(CheckBoxSquare.this, buttonWithCounterView, view);
            }
        });
        final float[] fArr = {4.0f};
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SMSSubscribeSheet.lambda$show$6(ButtonWithCounterView.this, frameLayout, fArr, context, bottomSheet, resourcesProvider, view);
            }
        });
        LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView2.setTextColor(Theme.getColor(i, resourcesProvider));
        linksTextView2.setLinkTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView2.setTextSize(1, 12.0f);
        linksTextView2.setTextAlignment(4);
        linksTextView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString((int) R.string.SmsSubscribeActivateText), runnable2));
        linksTextView2.setGravity(17);
        linearLayout.addView(linksTextView2, LayoutHelper.createLinear(-1, -2, 30.0f, 17.0f, 30.0f, 14.0f));
        bottomSheet.setCustomView(linearLayout);
        bottomSheet.show();
        if (runnable != null) {
            bottomSheet.setOnHideListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    runnable.run();
                }
            });
        }
        return bottomSheet;
    }

    public static void lambda$show$0() {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
            bottomSheetParams.transitionFromLeft = true;
            bottomSheetParams.allowNestedScroll = false;
            lastFragment.showAsSheet(new PremiumPreviewFragment("sms"), bottomSheetParams);
        }
    }

    public static void lambda$show$1(TL_smsjobs$TL_smsjobs_eligibleToJoin tL_smsjobs$TL_smsjobs_eligibleToJoin, Context context) {
        if (tL_smsjobs$TL_smsjobs_eligibleToJoin == null) {
            return;
        }
        Browser.openUrl(context, tL_smsjobs$TL_smsjobs_eligibleToJoin.terms_of_use);
    }

    public static void lambda$show$2(CheckBoxSquare checkBoxSquare, ButtonWithCounterView buttonWithCounterView, View view) {
        checkBoxSquare.setChecked(!checkBoxSquare.isChecked(), true);
        buttonWithCounterView.setEnabled(checkBoxSquare.isChecked());
    }

    public static void lambda$show$6(final ButtonWithCounterView buttonWithCounterView, FrameLayout frameLayout, float[] fArr, final Context context, final BottomSheet bottomSheet, final Theme.ResourcesProvider resourcesProvider, View view) {
        final int i = UserConfig.selectedAccount;
        if (!buttonWithCounterView.isEnabled()) {
            float f = -fArr[0];
            fArr[0] = f;
            AndroidUtilities.shakeViewSpring(frameLayout, f);
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
            return;
        }
        SMSJobController.getInstance(i).setState(1);
        requestSMSPermissions(context, new Runnable() {
            @Override
            public final void run() {
                SMSSubscribeSheet.lambda$show$5(i, bottomSheet, context, resourcesProvider, buttonWithCounterView);
            }
        }, false);
    }

    public static void lambda$show$5(final int i, final BottomSheet bottomSheet, final Context context, final Theme.ResourcesProvider resourcesProvider, final ButtonWithCounterView buttonWithCounterView) {
        SMSJobController.getInstance(i).checkSelectedSIMCard();
        if (SMSJobController.getInstance(i).getSelectedSIM() == null) {
            bottomSheet.dismiss();
            SMSJobController.getInstance(i).setState(2);
            new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString((int) R.string.SmsNoSimTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsNoSimMessage))).setPositiveButton(LocaleController.getString((int) R.string.OK), null).show();
            return;
        }
        buttonWithCounterView.setLoading(true);
        ConnectionsManager.getInstance(i).sendRequest(new TL_smsjobs$TL_smsjobs_join(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SMSSubscribeSheet.lambda$show$4(ButtonWithCounterView.this, i, bottomSheet, context, resourcesProvider, tLObject, tLRPC$TL_error);
            }
        });
    }

    public static void lambda$show$4(final ButtonWithCounterView buttonWithCounterView, final int i, final BottomSheet bottomSheet, final Context context, final Theme.ResourcesProvider resourcesProvider, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SMSSubscribeSheet.lambda$show$3(TLRPC$TL_error.this, buttonWithCounterView, tLObject, i, bottomSheet, context, resourcesProvider);
            }
        });
    }

    public static void lambda$show$3(TLRPC$TL_error tLRPC$TL_error, ButtonWithCounterView buttonWithCounterView, TLObject tLObject, int i, BottomSheet bottomSheet, Context context, Theme.ResourcesProvider resourcesProvider) {
        if (tLRPC$TL_error != null) {
            buttonWithCounterView.setLoading(false);
            BulletinFactory.showError(tLRPC$TL_error);
        } else if (tLObject instanceof TLRPC$TL_boolFalse) {
            buttonWithCounterView.setLoading(false);
            BulletinFactory.global().createErrorBulletin(LocaleController.getString((int) R.string.UnknownError)).show();
        } else {
            SMSJobController.getInstance(i).setState(3);
            bottomSheet.dismiss();
            SMSJobController.getInstance(i).loadStatus(true);
            showSubscribed(context, resourcesProvider);
        }
    }

    private static class FeatureCell extends LinearLayout {
        public FeatureCell(Context context, int i, CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            setOrientation(0);
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(i);
            int i2 = Theme.key_dialogTextBlack;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, resourcesProvider), PorterDuff.Mode.SRC_IN));
            addView(imageView, LayoutHelper.createLinear(-2, -2, 48, 0, 4, 17, 0));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 119));
            TextView textView = new TextView(context);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView.setText(charSequence);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 1.0f));
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            linksTextView.setTextSize(1, 14.0f);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
            linksTextView.setText(charSequence2);
            linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(330.0f), View.MeasureSpec.getSize(i)), 1073741824), i2);
        }
    }

    public static BottomSheet showSubscribed(Context context, Theme.ResourcesProvider resourcesProvider) {
        int i = UserConfig.selectedAccount;
        final BottomSheet bottomSheet = new BottomSheet(context, false, resourcesProvider);
        bottomSheet.fixNavigationBar(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAnimation(R.raw.giveaway_results, 120, 120);
        rLottieImageView.getAnimatedDrawable().multiplySpeed(1.8f);
        rLottieImageView.playAnimation();
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(120, 120, 1, 0, 24, 0, 12));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        textView.setTextAlignment(4);
        int i2 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i2, resourcesProvider));
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setText(LocaleController.getString((int) R.string.SmsPremiumActivated));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 30, 0, 30, 14));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        textView2.setTextAlignment(4);
        textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
        TLRPC$User currentUser = UserConfig.getInstance(i).getCurrentUser();
        String str = null;
        String countryFromPhoneNumber = currentUser != null ? SMSJobController.getCountryFromPhoneNumber(context, currentUser.phone) : null;
        if (!TextUtils.isEmpty(countryFromPhoneNumber)) {
            try {
                str = new Locale(BuildConfig.APP_CENTER_HASH, countryFromPhoneNumber).getDisplayCountry();
            } catch (Exception unused) {
            }
        }
        if (str != null) {
            textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.SmsPremiumActivatedText, str)));
        } else {
            textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsPremiumActivatedTextUnknown)));
        }
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 30, 0, 30, 16));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setGravity(17);
        linksTextView.setTextAlignment(4);
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString((int) R.string.SmsPremiumActivatedText2), new Runnable() {
            @Override
            public final void run() {
                SMSSubscribeSheet.lambda$showSubscribed$8(BottomSheet.this);
            }
        }));
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 1, 30, 0, 30, 24));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString((int) R.string.OK), false);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 14.0f, 0.0f, 14.0f, 0.0f));
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BottomSheet.this.dismiss();
            }
        });
        bottomSheet.setCustomView(linearLayout);
        bottomSheet.show();
        final FireworksOverlay fireworksOverlay = new FireworksOverlay(context);
        bottomSheet.getContainer().addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
        fireworksOverlay.postDelayed(new Runnable() {
            @Override
            public final void run() {
                FireworksOverlay.this.start(true);
            }
        }, 720L);
        bottomSheet.setOnHideListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                SMSSubscribeSheet.lambda$showSubscribed$11(FireworksOverlay.this, dialogInterface);
            }
        });
        return bottomSheet;
    }

    public static void lambda$showSubscribed$8(BottomSheet bottomSheet) {
        bottomSheet.dismiss();
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            lastFragment.presentFragment(new SMSStatsActivity());
        }
    }

    public static void lambda$showSubscribed$11(FireworksOverlay fireworksOverlay, DialogInterface dialogInterface) {
        fireworksOverlay.animate().alpha(0.0f).start();
    }

    public static void requestSMSPermissions(final Context context, final Runnable runnable, boolean z) {
        if (permissionsCallbacks == null) {
            permissionsCallbacks = new LongSparseArray<>();
        }
        final Activity findActivity = AndroidUtilities.findActivity(context);
        if (findActivity == null) {
            findActivity = LaunchActivity.instance;
        }
        if (findActivity == null || runnable == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            boolean z2 = true;
            boolean z3 = findActivity.checkSelfPermission("android.permission.SEND_SMS") == 0;
            z2 = (findActivity.checkSelfPermission("android.permission.READ_PHONE_STATE") == 0 && findActivity.checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0) ? false : false;
            if (z3 && z2) {
                runnable.run();
                return;
            } else if (findActivity.shouldShowRequestPermissionRationale("android.permission.SEND_SMS") || findActivity.shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") || findActivity.shouldShowRequestPermissionRationale("android.permission.READ_PHONE_NUMBERS") || z) {
                new AlertDialog.Builder(findActivity).setMessage(AndroidUtilities.replaceTags(LocaleController.getString((z3 || z2) ? !z3 ? R.string.SmsPermissionTextSMSSettings : R.string.SmsPermissionTextPhoneSettings : R.string.SmsPermissionTextSMSPhoneSettings))).setPositiveButton(LocaleController.getString("Settings", R.string.Settings), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SMSSubscribeSheet.lambda$requestSMSPermissions$12(findActivity, dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        SMSSubscribeSheet.lambda$requestSMSPermissions$13(dialogInterface);
                    }
                }).setTopImage(R.drawable.permissions_sms, Theme.getColor(Theme.key_dialogTopBackground)).show();
                return;
            } else {
                new AlertDialog.Builder(findActivity).setMessage(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsPermissionText))).setPositiveButton(LocaleController.getString((int) R.string.Next), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SMSSubscribeSheet.lambda$requestSMSPermissions$15(context, runnable, findActivity, dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString((int) R.string.Cancel), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        SMSSubscribeSheet.lambda$requestSMSPermissions$16(dialogInterface);
                    }
                }).setTopImage(R.drawable.permissions_sms, Theme.getColor(Theme.key_dialogTopBackground)).show();
                return;
            }
        }
        runnable.run();
    }

    public static void lambda$requestSMSPermissions$12(Activity activity, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    public static void lambda$requestSMSPermissions$15(final Context context, final Runnable runnable, Activity activity, DialogInterface dialogInterface, int i) {
        int abs = (int) (Math.abs(Math.random() * 2.147482647E9d) + 1000.0d);
        permissionsCallbacks.put(abs, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                SMSSubscribeSheet.lambda$requestSMSPermissions$14(context, runnable, (Boolean) obj);
            }
        });
        activity.requestPermissions(new String[]{"android.permission.SEND_SMS", "android.permission.READ_PHONE_STATE", "android.permission.READ_PHONE_NUMBERS"}, abs);
    }

    public static void lambda$requestSMSPermissions$14(Context context, Runnable runnable, Boolean bool) {
        if (!bool.booleanValue()) {
            requestSMSPermissions(context, runnable, true);
        } else {
            runnable.run();
        }
    }

    public static boolean checkSMSPermissions(int i, String[] strArr, int[] iArr) {
        long j;
        final Utilities.Callback<Boolean> callback;
        LongSparseArray<Utilities.Callback<Boolean>> longSparseArray = permissionsCallbacks;
        if (longSparseArray == null || (callback = longSparseArray.get(i)) == null) {
            return false;
        }
        permissionsCallbacks.remove(j);
        final boolean z = true;
        for (int i2 : iArr) {
            if (i2 != 0) {
                z = false;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SMSSubscribeSheet.lambda$checkSMSPermissions$17(Utilities.Callback.this, z);
            }
        });
        return true;
    }

    public static void lambda$checkSMSPermissions$17(Utilities.Callback callback, boolean z) {
        callback.run(Boolean.valueOf(z));
    }
}