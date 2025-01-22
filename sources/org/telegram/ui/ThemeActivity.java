package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.ChatListCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemeTypeCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SwipeGestureSettingsView;
import org.telegram.ui.PeerColorActivity;
import org.telegram.ui.ThemeActivity;
import org.telegram.ui.web.SearchEngine;
import org.telegram.ui.web.WebBrowserSettings;

public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int appIconHeaderRow;
    private int appIconSelectorRow;
    private int appIconShadowRow;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int backgroundRow;
    private int bluetoothScoRow;
    private int browserRow;
    private int bubbleRadiusHeaderRow;
    private int bubbleRadiusInfoRow;
    private int bubbleRadiusRow;
    private int changeUserColor;
    private int chatBlurRow;
    private int chatListHeaderRow;
    private int chatListInfoRow;
    private int chatListRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int createNewThemeRow;
    private int currentType;
    private ArrayList darkThemes = new ArrayList();
    private ArrayList defaultThemes = new ArrayList();
    private int directShareRow;
    private int distanceRow;
    private int editThemeRow;
    private int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener;
    boolean hasThemeAccents;
    private boolean highlightSensitiveRow;
    boolean lastIsDarkTheme;
    private int lastShadowRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int liteModeInfoRow;
    private int liteModeRow;
    private int mediaSoundHeaderRow;
    private int mediaSoundSectionRow;
    private ActionBarMenuItem menuItem;
    private GpsLocationListener networkLocationListener;
    private int newThemeInfoRow;
    private int nextMediaTapRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
    private int nightSystemDefaultRow;
    private int nightThemeRow;
    private int nightTypeInfoRow;
    private int otherHeaderRow;
    private int otherSectionRow;
    private int pauseOnMediaRow;
    private int pauseOnRecordRow;
    private int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    private int raiseToListenRow;
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryOption1Row;
    private int saveToGalleryOption2Row;
    private int saveToGallerySectionRow;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int searchEngineRow;
    private int selectThemeHeaderRow;
    private int sendByEnterRow;
    private int sensitiveContentRow;
    private int settings2Row;
    private int settingsRow;
    private Theme.ThemeAccent sharingAccent;
    private AlertDialog sharingProgressDialog;
    private Theme.ThemeInfo sharingTheme;
    private int stickersInfoRow;
    private int stickersRow;
    private int stickersSectionRow;
    private RLottieDrawable sunDrawable;
    private int swipeGestureHeaderRow;
    private int swipeGestureInfoRow;
    private int swipeGestureRow;
    private int textSizeHeaderRow;
    private int textSizeRow;
    private int themeAccentListRow;
    private int themeHeaderRow;
    private int themeInfoRow;
    private int themeListRow;
    private int themeListRow2;
    private int themePreviewRow;
    private ThemesHorizontalListCell themesHorizontalListCell;
    private boolean updateDistance;
    private boolean updateRecordViaSco;
    private boolean updateSearchEngine;
    private boolean updatingLocation;

    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        public void lambda$onItemClick$0(AlertDialog alertDialog, int i) {
            boolean fontSize = ThemeActivity.this.setFontSize(AndroidUtilities.isTablet() ? 18 : 16);
            if (ThemeActivity.this.setBubbleRadius(17, true)) {
                fontSize = true;
            }
            if (fontSize) {
                ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.textSizeRow, new Object());
                ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.bubbleRadiusRow, new Object());
            }
            if (ThemeActivity.this.themesHorizontalListCell != null) {
                Theme.ThemeInfo theme = Theme.getTheme("Blue");
                Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
                Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) theme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
                if (themeAccent != null) {
                    Theme.OverrideWallpaperInfo overrideWallpaperInfo = new Theme.OverrideWallpaperInfo();
                    overrideWallpaperInfo.slug = "d";
                    overrideWallpaperInfo.fileName = "Blue_99_wp.jpg";
                    overrideWallpaperInfo.originalFileName = "Blue_99_wp.jpg";
                    themeAccent.overrideWallpaper = overrideWallpaperInfo;
                    theme.setOverrideWallpaper(overrideWallpaperInfo);
                }
                if (theme != currentTheme) {
                    theme.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                    Theme.saveThemeAccents(theme, true, false, true, false);
                    ThemeActivity.this.themesHorizontalListCell.selectTheme(theme);
                    ThemeActivity.this.themesHorizontalListCell.smoothScrollToPosition(0);
                    return;
                }
                if (theme.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                    Theme.reloadWallpaper(true);
                } else {
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, currentTheme, Boolean.valueOf(ThemeActivity.this.currentType == 1), null, Integer.valueOf(Theme.DEFALT_THEME_ACCENT_ID));
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.themeAccentListRow);
                }
            }
        }

        @Override
        public void onItemClick(int r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.AnonymousClass1.onItemClick(int):void");
        }
    }

    public class BubbleRadiusCell extends FrameLayout {
        private int endRadius;
        private SeekBarView sizeBar;
        private int startRadius;
        private TextPaint textPaint;

        public BubbleRadiusCell(Context context) {
            super(context);
            this.startRadius = 0;
            this.endRadius = 17;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setSeparatorsCount((this.endRadius - this.startRadius) + 1);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(BubbleRadiusCell.this.startRadius + ((BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius) * BubbleRadiusCell.this.sizeBar.getProgress())));
                }

                @Override
                public int getStepsCount() {
                    return BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius;
                }

                @Override
                public void onSeekBarDrag(boolean z, float f) {
                    ThemeActivity.this.setBubbleRadius(Math.round(r4.startRadius + ((BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius) * f)), false);
                }

                @Override
                public void onSeekBarPressed(boolean z) {
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + SharedConfig.bubbleRadius, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
            SeekBarView seekBarView = this.sizeBar;
            int i3 = SharedConfig.bubbleRadius;
            int i4 = this.startRadius;
            seekBarView.setProgress((i3 - i4) / (this.endRadius - i4));
        }

        @Override
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
        }
    }

    public class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
        }

        GpsLocationListener(ThemeActivity themeActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                return;
            }
            ThemeActivity.this.stopLocationUpdate();
            ThemeActivity.this.updateSunTime(location, false);
        }

        @Override
        public void onProviderDisabled(String str) {
        }

        @Override
        public void onProviderEnabled(String str) {
        }

        @Override
        public void onStatusChanged(String str, int i, Bundle bundle) {
        }
    }

    public static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private boolean checked;
        private float checkedState;
        private Theme.ThemeAccent currentAccent;
        private Theme.ThemeInfo currentTheme;
        private final Paint paint;

        InnerAccentView(Context context) {
            super(context);
            this.paint = new Paint(1);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float dp = AndroidUtilities.dp(20.0f);
            float measuredWidth = getMeasuredWidth() * 0.5f;
            float measuredHeight = getMeasuredHeight() * 0.5f;
            this.paint.setColor(this.currentAccent.accentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), this.paint);
            if (this.checkedState != 0.0f) {
                this.paint.setColor(-1);
                this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle(measuredWidth - (AndroidUtilities.dp(7.0f) * this.checkedState), measuredHeight, AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle((AndroidUtilities.dp(7.0f) * this.checkedState) + measuredWidth, measuredHeight, AndroidUtilities.dp(2.0f), this.paint);
            }
            int i = this.currentAccent.myMessagesAccentColor;
            if (i == 0 || this.checkedState == 1.0f) {
                return;
            }
            this.paint.setColor(i);
            canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(8.0f) * (1.0f - this.checkedState), this.paint);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.checked);
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824));
        }

        public void setCheckedState(float f) {
            this.checkedState = f;
            invalidate();
        }

        void setThemeAndColor(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
            this.currentTheme = themeInfo;
            this.currentAccent = themeAccent;
            updateCheckedState(false);
        }

        void updateCheckedState(boolean z) {
            this.checked = this.currentTheme.currentAccentId == this.currentAccent.id;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (!z) {
                setCheckedState(this.checked ? 1.0f : 0.0f);
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", this.checked ? 1.0f : 0.0f);
            this.checkAnimator = ofFloat;
            ofFloat.setDuration(200L);
            this.checkAnimator.start();
        }
    }

    private static class InnerCustomAccentView extends View {
        private int[] colors;
        private final Paint paint;

        InnerCustomAccentView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.colors = new int[7];
        }

        public void setTheme(Theme.ThemeInfo themeInfo) {
            if (themeInfo.defaultAccentCount >= 8) {
                this.colors = new int[]{themeInfo.getAccentColor(6), themeInfo.getAccentColor(4), themeInfo.getAccentColor(7), themeInfo.getAccentColor(2), themeInfo.getAccentColor(0), themeInfo.getAccentColor(5), themeInfo.getAccentColor(3)};
            } else {
                this.colors = new int[7];
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float measuredWidth = getMeasuredWidth() * 0.5f;
            float measuredHeight = getMeasuredHeight() * 0.5f;
            float dp = AndroidUtilities.dp(5.0f);
            float dp2 = AndroidUtilities.dp(20.0f) - dp;
            this.paint.setStyle(Paint.Style.FILL);
            int i = 0;
            this.paint.setColor(this.colors[0]);
            canvas.drawCircle(measuredWidth, measuredHeight, dp, this.paint);
            double d = 0.0d;
            while (i < 6) {
                float sin = (((float) Math.sin(d)) * dp2) + measuredWidth;
                float cos = measuredHeight - (((float) Math.cos(d)) * dp2);
                i++;
                this.paint.setColor(this.colors[i]);
                canvas.drawCircle(sin, cos, dp, this.paint);
                d += 1.0471975511965976d;
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setEnabled(true);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), 1073741824));
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private boolean first = true;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public void lambda$onCreateViewHolder$2(org.telegram.ui.ThemeActivity.ThemeAccentsListAdapter r10, org.telegram.ui.Components.RecyclerListView r11, android.view.View r12, int r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.lambda$onCreateViewHolder$2(org.telegram.ui.ThemeActivity$ThemeAccentsListAdapter, org.telegram.ui.Components.RecyclerListView, android.view.View, int):void");
        }

        public void lambda$onCreateViewHolder$3(ThemeAccentsListAdapter themeAccentsListAdapter, Theme.ThemeAccent themeAccent, AlertDialog alertDialog, int i) {
            if (Theme.deleteThemeAccent(themeAccentsListAdapter.currentTheme, themeAccent, true)) {
                Theme.refreshThemeColors();
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, Theme.getActiveTheme(), Boolean.valueOf(ThemeActivity.this.currentType == 1), null, -1);
            }
        }

        public void lambda$onCreateViewHolder$4(final Theme.ThemeAccent themeAccent, final ThemeAccentsListAdapter themeAccentsListAdapter, DialogInterface dialogInterface, int i) {
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if (i == 0) {
                AlertsCreator.createThemeCreateDialog(ThemeActivity.this, i == 1 ? 2 : 1, themeAccent.parentTheme, themeAccent);
                return;
            }
            if (i == 1) {
                if (themeAccent.info == null) {
                    ThemeActivity.this.getMessagesController().saveThemeToServer(themeAccent.parentTheme, themeAccent);
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needShareTheme, themeAccent.parentTheme, themeAccent);
                    return;
                }
                String str = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + themeAccent.info.slug;
                ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), null, str, false, str, false));
                return;
            }
            if (i == 2) {
                ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeAccent.parentTheme, themeAccent, false));
                return;
            }
            if (i != 3 || ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
            builder.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i2) {
                    ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$3(themeAccentsListAdapter, themeAccent, alertDialog, i2);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog create = builder.create();
            ThemeActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }

        public boolean lambda$onCreateViewHolder$5(final ThemeAccentsListAdapter themeAccentsListAdapter, View view, int i) {
            if (i >= 0 && i < themeAccentsListAdapter.themeAccents.size()) {
                final Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (themeAccent.id >= 100 && !themeAccent.isDefault) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                    String string = LocaleController.getString("OpenInEditor", R.string.OpenInEditor);
                    String string2 = LocaleController.getString("ShareTheme", R.string.ShareTheme);
                    TLRPC.TL_theme tL_theme = themeAccent.info;
                    builder.setItems(new CharSequence[]{string, string2, (tL_theme == null || !tL_theme.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl), LocaleController.getString("DeleteTheme", R.string.DeleteTheme)}, new int[]{R.drawable.msg_edit, R.drawable.msg_share, R.drawable.msg_link, R.drawable.msg_delete}, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$4(themeAccent, themeAccentsListAdapter, dialogInterface, i2);
                        }
                    });
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor(Theme.key_text_RedBold), Theme.getColor(Theme.key_text_RedRegular));
                    return true;
                }
            }
            return false;
        }

        public void lambda$showOptionsForTheme$0(Theme.ThemeInfo themeInfo, AlertDialog alertDialog, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ((BaseFragment) ThemeActivity.this).parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeListUpdated, new Object[0]);
        }

        public void lambda$showOptionsForTheme$1(final org.telegram.ui.ActionBar.Theme.ThemeInfo r8, android.content.DialogInterface r9, int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.lambda$showOptionsForTheme$1(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        public void showOptionsForTheme(final Theme.ThemeInfo themeInfo) {
            int[] iArr;
            CharSequence[] charSequenceArr;
            if (ThemeActivity.this.getParentActivity() != null) {
                if ((themeInfo.info == null || themeInfo.themeLoaded) && ThemeActivity.this.currentType != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                    boolean z = false;
                    if (themeInfo.pathToFile == null) {
                        charSequenceArr = new CharSequence[]{null, LocaleController.getString("ExportTheme", R.string.ExportTheme)};
                        iArr = new int[]{0, R.drawable.msg_shareout};
                    } else {
                        TLRPC.TL_theme tL_theme = themeInfo.info;
                        boolean z2 = tL_theme == null || !tL_theme.isDefault;
                        String string = LocaleController.getString("ShareFile", R.string.ShareFile);
                        String string2 = LocaleController.getString("ExportTheme", R.string.ExportTheme);
                        TLRPC.TL_theme tL_theme2 = themeInfo.info;
                        String string3 = (tL_theme2 == null || (!tL_theme2.isDefault && tL_theme2.creator)) ? LocaleController.getString("Edit", R.string.Edit) : null;
                        TLRPC.TL_theme tL_theme3 = themeInfo.info;
                        CharSequence[] charSequenceArr2 = {string, string2, string3, (tL_theme3 == null || !tL_theme3.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl), z2 ? LocaleController.getString("Delete", R.string.Delete) : null};
                        z = z2;
                        iArr = new int[]{R.drawable.msg_share, R.drawable.msg_shareout, R.drawable.msg_edit, R.drawable.msg_link, R.drawable.msg_delete};
                        charSequenceArr = charSequenceArr2;
                    }
                    builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.ListAdapter.this.lambda$showOptionsForTheme$1(themeInfo, dialogInterface, i);
                        }
                    });
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    if (z) {
                        create.setItemColor(create.getItemsCount() - 1, Theme.getColor(Theme.key_text_RedBold), Theme.getColor(Theme.key_text_RedRegular));
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.distanceRow || i == ThemeActivity.this.scheduleToRow || i == ThemeActivity.this.scheduleUpdateLocationRow || i == ThemeActivity.this.contactsReimportRow || i == ThemeActivity.this.contactsSortRow || i == ThemeActivity.this.bluetoothScoRow || i == ThemeActivity.this.searchEngineRow) {
                return 1;
            }
            if (i == ThemeActivity.this.automaticBrightnessInfoRow || i == ThemeActivity.this.scheduleLocationInfoRow || i == ThemeActivity.this.swipeGestureInfoRow || i == ThemeActivity.this.stickersInfoRow || i == ThemeActivity.this.liteModeInfoRow) {
                return 2;
            }
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow || i == ThemeActivity.this.settings2Row || i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.chatListInfoRow || i == ThemeActivity.this.bubbleRadiusInfoRow || i == ThemeActivity.this.saveToGallerySectionRow || i == ThemeActivity.this.appIconShadowRow || i == ThemeActivity.this.lastShadowRow || i == ThemeActivity.this.stickersSectionRow || i == ThemeActivity.this.mediaSoundSectionRow || i == ThemeActivity.this.otherSectionRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow || i == ThemeActivity.this.nightSystemDefaultRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow || i == ThemeActivity.this.settingsRow || i == ThemeActivity.this.themeHeaderRow || i == ThemeActivity.this.textSizeHeaderRow || i == ThemeActivity.this.chatListHeaderRow || i == ThemeActivity.this.bubbleRadiusHeaderRow || i == ThemeActivity.this.swipeGestureHeaderRow || i == ThemeActivity.this.selectThemeHeaderRow || i == ThemeActivity.this.appIconHeaderRow || i == ThemeActivity.this.mediaSoundHeaderRow || i == ThemeActivity.this.otherHeaderRow) {
                return 5;
            }
            if (i == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (i == ThemeActivity.this.scheduleLocationRow || i == ThemeActivity.this.sendByEnterRow || i == ThemeActivity.this.raiseToSpeakRow || i == ThemeActivity.this.raiseToListenRow || i == ThemeActivity.this.pauseOnRecordRow || i == ThemeActivity.this.directShareRow || i == ThemeActivity.this.chatBlurRow || i == ThemeActivity.this.pauseOnMediaRow || i == ThemeActivity.this.nextMediaTapRow || i == ThemeActivity.this.sensitiveContentRow) {
                return 7;
            }
            if (i == ThemeActivity.this.textSizeRow) {
                return 8;
            }
            if (i == ThemeActivity.this.chatListRow) {
                return 9;
            }
            if (i == ThemeActivity.this.nightThemeRow || i == ThemeActivity.this.browserRow) {
                return 10;
            }
            if (i == ThemeActivity.this.themeListRow) {
                return 11;
            }
            if (i == ThemeActivity.this.themeAccentListRow) {
                return 12;
            }
            if (i == ThemeActivity.this.bubbleRadiusRow) {
                return 13;
            }
            if (i == ThemeActivity.this.backgroundRow || i == ThemeActivity.this.editThemeRow || i == ThemeActivity.this.createNewThemeRow || i == ThemeActivity.this.liteModeRow || i == ThemeActivity.this.stickersRow) {
                return 14;
            }
            if (i == ThemeActivity.this.swipeGestureRow) {
                return 15;
            }
            if (i == ThemeActivity.this.themePreviewRow) {
                return 16;
            }
            if (i == ThemeActivity.this.themeListRow2) {
                return 17;
            }
            if (i == ThemeActivity.this.saveToGalleryOption1Row || i == ThemeActivity.this.saveToGalleryOption2Row) {
                return 19;
            }
            if (i == ThemeActivity.this.appIconSelectorRow) {
                return 20;
            }
            return i == ThemeActivity.this.changeUserColor ? 21 : 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 1 || itemViewType == 4 || itemViewType == 7 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12 || itemViewType == 14 || itemViewType == 18 || itemViewType == 20 || itemViewType == 21;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r24, int r25) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View notificationsCheckCell;
            ViewGroup.LayoutParams layoutParams;
            View view;
            View view2;
            int i2 = 0;
            switch (i) {
                case 1:
                    notificationsCheckCell = new TextSettingsCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 2:
                    View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    view2 = textInfoPrivacyCell;
                    break;
                case 3:
                    view2 = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    notificationsCheckCell = new ThemeTypeCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 5:
                    notificationsCheckCell = new HeaderCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 6:
                    notificationsCheckCell = new BrightnessControlCell(this.mContext, i2) {
                        @Override
                        protected void didChangedValue(float f) {
                            int i3 = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int i4 = (int) (f * 100.0f);
                            Theme.autoNightBrighnessThreshold = f;
                            if (i3 != i4) {
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 7:
                    notificationsCheckCell = new TextCheckCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 8:
                    notificationsCheckCell = new TextSizeCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 9:
                    notificationsCheckCell = new ChatListCell(this.mContext) {
                        @Override
                        protected void didSelectChatType(boolean z) {
                            SharedConfig.setUseThreeLinesLayout(z);
                        }
                    };
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 10:
                    notificationsCheckCell = new NotificationsCheckCell(this.mContext, 21, 60, true);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 11:
                    this.first = true;
                    ThemeActivity themeActivity = ThemeActivity.this;
                    Context context = this.mContext;
                    ThemeActivity themeActivity2 = ThemeActivity.this;
                    themeActivity.themesHorizontalListCell = new ThemesHorizontalListCell(context, themeActivity2, themeActivity2.currentType, ThemeActivity.this.defaultThemes, ThemeActivity.this.darkThemes) {
                        @Override
                        protected void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
                            ThemeActivity.this.listAdapter.showOptionsForTheme(themeInfo);
                        }

                        @Override
                        protected void updateRows() {
                            ThemeActivity.this.updateRows(false);
                        }
                    };
                    ThemeActivity.this.themesHorizontalListCell.setDrawDivider(ThemeActivity.this.hasThemeAccents);
                    ThemeActivity.this.themesHorizontalListCell.setFocusable(false);
                    View view3 = ThemeActivity.this.themesHorizontalListCell;
                    layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(148.0f));
                    view = view3;
                    view.setLayoutParams(layoutParams);
                    view2 = view;
                    break;
                case 12:
                    final TintRecyclerListView tintRecyclerListView = new TintRecyclerListView(this.mContext) {
                        @Override
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    tintRecyclerListView.setFocusable(false);
                    tintRecyclerListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    tintRecyclerListView.setItemAnimator(null);
                    tintRecyclerListView.setLayoutAnimation(null);
                    tintRecyclerListView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
                    tintRecyclerListView.setClipToPadding(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
                    linearLayoutManager.setOrientation(0);
                    tintRecyclerListView.setLayoutManager(linearLayoutManager);
                    final ThemeAccentsListAdapter themeAccentsListAdapter = new ThemeAccentsListAdapter(this.mContext);
                    tintRecyclerListView.setAdapter(themeAccentsListAdapter);
                    tintRecyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                        @Override
                        public final void onItemClick(View view4, int i3) {
                            ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$2(themeAccentsListAdapter, tintRecyclerListView, view4, i3);
                        }
                    });
                    tintRecyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
                        @Override
                        public final boolean onItemClick(View view4, int i3) {
                            boolean lambda$onCreateViewHolder$5;
                            lambda$onCreateViewHolder$5 = ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$5(themeAccentsListAdapter, view4, i3);
                            return lambda$onCreateViewHolder$5;
                        }
                    });
                    layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(62.0f));
                    view = tintRecyclerListView;
                    view.setLayoutParams(layoutParams);
                    view2 = view;
                    break;
                case 13:
                    notificationsCheckCell = new BubbleRadiusCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 14:
                case 18:
                default:
                    notificationsCheckCell = new TextCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view2 = notificationsCheckCell;
                    break;
                case 15:
                    view2 = new SwipeGestureSettingsView(this.mContext, ((BaseFragment) ThemeActivity.this).currentAccount);
                    break;
                case 16:
                    View view4 = new ThemePreviewMessagesCell(this.mContext, ((BaseFragment) ThemeActivity.this).parentLayout, i2) {
                        @Override
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    view4.setImportantForAccessibility(4);
                    view2 = view4;
                    break;
                case 17:
                    Context context2 = this.mContext;
                    ThemeActivity themeActivity3 = ThemeActivity.this;
                    View defaultThemesPreviewCell = new DefaultThemesPreviewCell(context2, themeActivity3, themeActivity3.currentType);
                    defaultThemesPreviewCell.setFocusable(false);
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                    view = defaultThemesPreviewCell;
                    view.setLayoutParams(layoutParams);
                    view2 = view;
                    break;
                case 19:
                    view2 = new RadioButtonCell(this.mContext);
                    break;
                case 20:
                    Context context3 = this.mContext;
                    ThemeActivity themeActivity4 = ThemeActivity.this;
                    view2 = new AppIconsSelectorCell(context3, themeActivity4, ((BaseFragment) themeActivity4).currentAccount);
                    break;
                case 21:
                    view2 = new PeerColorActivity.ChangeNameColorCell(((BaseFragment) ThemeActivity.this).currentAccount, 0L, this.mContext, ThemeActivity.this.getResourceProvider());
                    break;
            }
            return new RecyclerListView.Holder(view2);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 4) {
                ((ThemeTypeCell) viewHolder.itemView).setTypeChecked(viewHolder.getAdapterPosition() == Theme.selectedAutoNightType);
            }
            if (itemViewType == 2 || itemViewType == 3) {
                return;
            }
            viewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
    }

    public class TextSizeCell extends FrameLayout {
        private int endFontSize;
        private int lastWidth;
        private ThemePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private int startFontSize;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            this.startFontSize = 12;
            this.endFontSize = 30;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setSeparatorsCount((this.endFontSize - this.startFontSize) + 1);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(TextSizeCell.this.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * TextSizeCell.this.sizeBar.getProgress())));
                }

                @Override
                public int getStepsCount() {
                    return TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize;
                }

                @Override
                public void onSeekBarDrag(boolean z, float f) {
                    ThemeActivity.this.setFontSize(Math.round(r4.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * f)));
                }

                @Override
                public void onSeekBarPressed(boolean z) {
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, ((BaseFragment) ThemeActivity.this).parentLayout, 0);
            this.messagesCell = themePreviewMessagesCell;
            themePreviewMessagesCell.setImportantForAccessibility(4);
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText("" + SharedConfig.fontSize, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int size = View.MeasureSpec.getSize(i);
            if (this.lastWidth != size) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.fontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress((i3 - i4) / (this.endFontSize - i4));
                this.lastWidth = size;
            }
        }

        @Override
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
        }
    }

    public class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
        private Theme.ThemeInfo currentTheme;
        private Context mContext;
        private ArrayList themeAccents;

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            notifyDataSetChanged();
        }

        public int findCurrentAccent() {
            return this.themeAccents.indexOf(this.currentTheme.getAccent(false));
        }

        @Override
        public int getItemCount() {
            if (this.themeAccents.isEmpty()) {
                return 0;
            }
            return this.themeAccents.size() + 1;
        }

        @Override
        public int getItemViewType(int i) {
            return i == getItemCount() - 1 ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.themeAccents = new ArrayList(this.currentTheme.themeAccents);
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                ((InnerAccentView) viewHolder.itemView).setThemeAndColor(this.currentTheme, (Theme.ThemeAccent) this.themeAccents.get(i));
            } else {
                if (itemViewType != 1) {
                    return;
                }
                ((InnerCustomAccentView) viewHolder.itemView).setTheme(this.currentTheme);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return i != 0 ? new RecyclerListView.Holder(new InnerCustomAccentView(this.mContext)) : new RecyclerListView.Holder(new InnerAccentView(this.mContext));
        }
    }

    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    public ThemeActivity(int i) {
        AnonymousClass1 anonymousClass1 = null;
        this.gpsLocationListener = new GpsLocationListener(this, anonymousClass1);
        this.networkLocationListener = new GpsLocationListener(this, anonymousClass1);
        this.currentType = i;
        updateRows(true);
    }

    public void createNewTheme() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
        builder.setMessage(LocaleController.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("CreateTheme", R.string.CreateTheme), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                ThemeActivity.this.lambda$createNewTheme$14(alertDialog, i);
            }
        });
        showDialog(builder.create());
    }

    public void editTheme() {
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        presentFragment(new ThemePreviewActivity(currentTheme, false, 1, currentTheme.getAccent(false).id >= 100, this.currentType == 1));
    }

    public String getLocationSunString() {
        int i = Theme.autoNightSunriseTime;
        int i2 = i / 60;
        String format = String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i - (i2 * 60)));
        int i3 = Theme.autoNightSunsetTime;
        int i4 = i3 / 60;
        return LocaleController.formatString("AutoNightUpdateLocationInfo", R.string.AutoNightUpdateLocationInfo, String.format("%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i3 - (i4 * 60))), format);
    }

    public void lambda$createNewTheme$14(AlertDialog alertDialog, int i) {
        AlertsCreator.createThemeCreateDialog(this, 0, null, null);
    }

    public void lambda$createView$10(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
        String string;
        String format;
        int i4 = (i2 * 60) + i3;
        if (i == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = i4;
            string = LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom);
            format = String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3));
        } else {
            Theme.autoNightDayEndTime = i4;
            string = LocaleController.getString("AutoNightTo", R.string.AutoNightTo);
            format = String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3));
        }
        textSettingsCell.setTextAndValue(string, format, true);
    }

    public void lambda$createView$11(Context context, final View view, final int i, float f, float f2) {
        BaseFragment liteModeSettingsActivity;
        int i2;
        String str;
        TextCheckCell textCheckCell;
        boolean chatBlurEnabled;
        AlertDialog.Builder builder;
        AlertDialog create;
        final AtomicReference atomicReference;
        LinearLayout linearLayout;
        AlertDialog.Builder builder2;
        int i3;
        String string;
        boolean z;
        if (i != this.enableAnimationsRow) {
            if (i == this.backgroundRow) {
                liteModeSettingsActivity = new WallpapersListActivity(0);
            } else if (i == this.changeUserColor) {
                liteModeSettingsActivity = new PeerColorActivity(0L).setOnApplied(this);
            } else {
                if (i != this.sendByEnterRow) {
                    if (i == this.raiseToSpeakRow) {
                        SharedConfig.toggleRaiseToSpeak();
                        if (!(view instanceof TextCheckCell)) {
                            return;
                        }
                        textCheckCell = (TextCheckCell) view;
                        chatBlurEnabled = SharedConfig.raiseToSpeak;
                    } else if (i == this.nextMediaTapRow) {
                        SharedConfig.toggleNextMediaTap();
                        if (!(view instanceof TextCheckCell)) {
                            return;
                        }
                        textCheckCell = (TextCheckCell) view;
                        chatBlurEnabled = SharedConfig.nextMediaTap;
                    } else {
                        if (i == this.raiseToListenRow) {
                            SharedConfig.toggleRaiseToListen();
                            if (view instanceof TextCheckCell) {
                                ((TextCheckCell) view).setChecked(SharedConfig.raiseToListen);
                            }
                            if (!SharedConfig.raiseToListen && this.raiseToSpeakRow != -1) {
                                for (int i4 = 0; i4 < this.listView.getChildCount(); i4++) {
                                    View childAt = this.listView.getChildAt(i4);
                                    if ((childAt instanceof TextCheckCell) && this.listView.getChildAdapterPosition(childAt) == this.raiseToSpeakRow) {
                                        ((TextCheckCell) childAt).setChecked(false);
                                    }
                                }
                            }
                            updateRows(false);
                            return;
                        }
                        if (i == this.pauseOnRecordRow) {
                            SharedConfig.togglePauseMusicOnRecord();
                            if (!(view instanceof TextCheckCell)) {
                                return;
                            }
                            textCheckCell = (TextCheckCell) view;
                            chatBlurEnabled = SharedConfig.pauseMusicOnRecord;
                        } else {
                            if (i != this.pauseOnMediaRow) {
                                if (i != this.distanceRow) {
                                    if (i == this.searchEngineRow) {
                                        if (getParentActivity() == null) {
                                            return;
                                        }
                                        atomicReference = new AtomicReference();
                                        linearLayout = new LinearLayout(context);
                                        linearLayout.setOrientation(1);
                                        ArrayList searchEngines = SearchEngine.getSearchEngines();
                                        int size = searchEngines.size();
                                        CharSequence[] charSequenceArr = new CharSequence[size];
                                        final int i5 = 0;
                                        while (i5 < size) {
                                            charSequenceArr[i5] = ((SearchEngine) searchEngines.get(i5)).name;
                                            RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                                            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                                            radioColorCell.setTextAndValue(charSequenceArr[i5], i5 == SharedConfig.searchEngineType);
                                            radioColorCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
                                            linearLayout.addView(radioColorCell);
                                            radioColorCell.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public final void onClick(View view2) {
                                                    ThemeActivity.this.lambda$createView$4(i5, atomicReference, view2);
                                                }
                                            });
                                            i5++;
                                        }
                                        builder2 = new AlertDialog.Builder(getParentActivity());
                                        i3 = R.string.SearchEngine;
                                    } else if (i == this.bluetoothScoRow) {
                                        if (getParentActivity() == null) {
                                            return;
                                        }
                                        atomicReference = new AtomicReference();
                                        linearLayout = new LinearLayout(context);
                                        linearLayout.setOrientation(1);
                                        RadioColorCell radioColorCell2 = new RadioColorCell(getParentActivity());
                                        radioColorCell2.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                        int i6 = Theme.key_radioBackground;
                                        int color = Theme.getColor(i6);
                                        int i7 = Theme.key_dialogRadioBackgroundChecked;
                                        radioColorCell2.setCheckColor(color, Theme.getColor(i7));
                                        radioColorCell2.setTextAndValue(LocaleController.getString(R.string.MicrophoneForVoiceMessagesBuiltIn), true ^ SharedConfig.recordViaSco);
                                        int i8 = Theme.key_listSelector;
                                        radioColorCell2.setBackground(Theme.createSelectorDrawable(Theme.getColor(i8), 2));
                                        linearLayout.addView(radioColorCell2);
                                        radioColorCell2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public final void onClick(View view2) {
                                                ThemeActivity.this.lambda$createView$5(atomicReference, view2);
                                            }
                                        });
                                        RadioColorCell radioColorCell3 = new RadioColorCell(getParentActivity());
                                        radioColorCell3.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                        radioColorCell3.setCheckColor(Theme.getColor(i6), Theme.getColor(i7));
                                        radioColorCell3.setTextAndText2AndValue(LocaleController.getString(R.string.MicrophoneForVoiceMessagesScoIfConnected), LocaleController.getString(R.string.MicrophoneForVoiceMessagesScoHint), SharedConfig.recordViaSco);
                                        radioColorCell3.setBackground(Theme.createSelectorDrawable(Theme.getColor(i8), 2));
                                        linearLayout.addView(radioColorCell3);
                                        radioColorCell3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public final void onClick(View view2) {
                                                ThemeActivity.this.lambda$createView$7(atomicReference, view2);
                                            }
                                        });
                                        builder2 = new AlertDialog.Builder(getParentActivity());
                                        i3 = R.string.MicrophoneForVoiceMessages;
                                    } else {
                                        if (i != this.directShareRow) {
                                            if (i != this.sensitiveContentRow) {
                                                if (i == this.contactsReimportRow) {
                                                    return;
                                                }
                                                if (i == this.contactsSortRow) {
                                                    if (getParentActivity() == null) {
                                                        return;
                                                    }
                                                    builder = new AlertDialog.Builder(getParentActivity());
                                                    builder.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                                                    builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public final void onClick(DialogInterface dialogInterface, int i9) {
                                                            ThemeActivity.this.lambda$createView$9(i, dialogInterface, i9);
                                                        }
                                                    });
                                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                                    create = builder.create();
                                                } else if (i == this.chatBlurRow) {
                                                    SharedConfig.toggleChatBlur();
                                                    if (!(view instanceof TextCheckCell)) {
                                                        return;
                                                    }
                                                    textCheckCell = (TextCheckCell) view;
                                                    chatBlurEnabled = SharedConfig.chatBlurEnabled();
                                                } else if (i == this.nightThemeRow) {
                                                    if ((LocaleController.isRTL && f <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && f >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                                                        NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                                                        if (Theme.selectedAutoNightType == 0) {
                                                            Theme.selectedAutoNightType = 2;
                                                            notificationsCheckCell.setChecked(true);
                                                        } else {
                                                            Theme.selectedAutoNightType = 0;
                                                            notificationsCheckCell.setChecked(false);
                                                        }
                                                        Theme.saveAutoNightThemeConfig();
                                                        Theme.checkAutoNightThemeConditions(true);
                                                        boolean z2 = Theme.selectedAutoNightType != 0;
                                                        String currentNightThemeName = z2 ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                                                        if (z2) {
                                                            int i9 = Theme.selectedAutoNightType;
                                                            if (i9 == 1) {
                                                                i2 = R.string.AutoNightScheduled;
                                                                str = "AutoNightScheduled";
                                                            } else if (i9 == 3) {
                                                                i2 = R.string.AutoNightSystemDefault;
                                                                str = "AutoNightSystemDefault";
                                                            } else {
                                                                i2 = R.string.AutoNightAdaptive;
                                                                str = "AutoNightAdaptive";
                                                            }
                                                            currentNightThemeName = LocaleController.getString(str, i2) + " " + currentNightThemeName;
                                                        }
                                                        notificationsCheckCell.setTextAndValueAndIconAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), currentNightThemeName, R.drawable.msg2_night_auto, z2, 0, false, true);
                                                        return;
                                                    }
                                                    liteModeSettingsActivity = new ThemeActivity(1);
                                                } else {
                                                    if (i != this.browserRow) {
                                                        if (i == this.nightDisabledRow) {
                                                            if (Theme.selectedAutoNightType == 0) {
                                                                return;
                                                            } else {
                                                                Theme.selectedAutoNightType = 0;
                                                            }
                                                        } else if (i == this.nightScheduledRow) {
                                                            if (Theme.selectedAutoNightType == 1) {
                                                                return;
                                                            }
                                                            Theme.selectedAutoNightType = 1;
                                                            if (Theme.autoNightScheduleByLocation) {
                                                                updateSunTime(null, true);
                                                            }
                                                        } else if (i == this.nightAutomaticRow) {
                                                            if (Theme.selectedAutoNightType == 2) {
                                                                return;
                                                            } else {
                                                                Theme.selectedAutoNightType = 2;
                                                            }
                                                        } else if (i != this.nightSystemDefaultRow) {
                                                            if (i == this.scheduleLocationRow) {
                                                                boolean z3 = !Theme.autoNightScheduleByLocation;
                                                                Theme.autoNightScheduleByLocation = z3;
                                                                ((TextCheckCell) view).setChecked(z3);
                                                                updateRows(true);
                                                                if (Theme.autoNightScheduleByLocation) {
                                                                    updateSunTime(null, true);
                                                                }
                                                                Theme.checkAutoNightThemeConditions();
                                                                return;
                                                            }
                                                            if (i == this.scheduleFromRow || i == this.scheduleToRow) {
                                                                if (getParentActivity() == null) {
                                                                    return;
                                                                }
                                                                int i10 = i == this.scheduleFromRow ? Theme.autoNightDayStartTime : Theme.autoNightDayEndTime;
                                                                int i11 = i10 / 60;
                                                                int i12 = i10 - (i11 * 60);
                                                                final TextSettingsCell textSettingsCell = (TextSettingsCell) view;
                                                                showDialog(new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener() {
                                                                    @Override
                                                                    public final void onTimeSet(TimePicker timePicker, int i13, int i14) {
                                                                        ThemeActivity.this.lambda$createView$10(i, textSettingsCell, timePicker, i13, i14);
                                                                    }
                                                                }, i11, i12, true));
                                                                return;
                                                            }
                                                            if (i == this.scheduleUpdateLocationRow) {
                                                                updateSunTime(null, true);
                                                                return;
                                                            }
                                                            if (i == this.createNewThemeRow) {
                                                                createNewTheme();
                                                                return;
                                                            }
                                                            if (i == this.editThemeRow) {
                                                                editTheme();
                                                                return;
                                                            } else if (i == this.stickersRow) {
                                                                liteModeSettingsActivity = new StickersActivity(0, null);
                                                            } else if (i != this.liteModeRow) {
                                                                return;
                                                            } else {
                                                                liteModeSettingsActivity = new LiteModeSettingsActivity();
                                                            }
                                                        } else if (Theme.selectedAutoNightType == 3) {
                                                            return;
                                                        } else {
                                                            Theme.selectedAutoNightType = 3;
                                                        }
                                                        updateRows(true);
                                                        Theme.checkAutoNightThemeConditions();
                                                        return;
                                                    }
                                                    if ((LocaleController.isRTL && f <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && f >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                                                        SharedConfig.toggleInappBrowser();
                                                        ((NotificationsCheckCell) view).setChecked(SharedConfig.inappBrowser);
                                                        return;
                                                    }
                                                    liteModeSettingsActivity = new WebBrowserSettings(null);
                                                }
                                            } else if (getMessagesController().showSensitiveContent()) {
                                                getMessagesController().setContentSettings(false);
                                                if (!(view instanceof TextCheckCell)) {
                                                    return;
                                                }
                                                textCheckCell = (TextCheckCell) view;
                                                chatBlurEnabled = getMessagesController().showSensitiveContent();
                                            } else {
                                                builder = new AlertDialog.Builder(context, this.resourceProvider).setTitle(LocaleController.getString(R.string.ConfirmSensitiveContentTitle)).setMessage(LocaleController.getString(R.string.ConfirmSensitiveContentText)).setPositiveButton(LocaleController.getString(R.string.Confirm), new AlertDialog.OnButtonClickListener() {
                                                    @Override
                                                    public final void onClick(AlertDialog alertDialog, int i13) {
                                                        ThemeActivity.this.lambda$createView$8(view, alertDialog, i13);
                                                    }
                                                }).setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                                                create = builder.create();
                                            }
                                            showDialog(create);
                                            return;
                                        }
                                        SharedConfig.toggleDirectShare();
                                        if (!(view instanceof TextCheckCell)) {
                                            return;
                                        }
                                        textCheckCell = (TextCheckCell) view;
                                        chatBlurEnabled = SharedConfig.directShare;
                                    }
                                    string = LocaleController.getString(i3);
                                } else {
                                    if (getParentActivity() == null) {
                                        return;
                                    }
                                    atomicReference = new AtomicReference();
                                    linearLayout = new LinearLayout(context);
                                    linearLayout.setOrientation(1);
                                    CharSequence[] charSequenceArr2 = {LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic), LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers), LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles)};
                                    final int i13 = 0;
                                    while (i13 < 3) {
                                        RadioColorCell radioColorCell4 = new RadioColorCell(getParentActivity());
                                        radioColorCell4.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                        radioColorCell4.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                                        radioColorCell4.setTextAndValue(charSequenceArr2[i13], i13 == SharedConfig.distanceSystemType);
                                        radioColorCell4.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
                                        linearLayout.addView(radioColorCell4);
                                        radioColorCell4.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public final void onClick(View view2) {
                                                ThemeActivity.this.lambda$createView$3(i13, atomicReference, view2);
                                            }
                                        });
                                        i13++;
                                    }
                                    builder2 = new AlertDialog.Builder(getParentActivity());
                                    string = LocaleController.getString("DistanceUnitsTitle", R.string.DistanceUnitsTitle);
                                }
                                create = builder2.setTitle(string).setView(linearLayout).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create();
                                atomicReference.set(create);
                                showDialog(create);
                                return;
                            }
                            SharedConfig.togglePauseMusicOnMedia();
                            if (!(view instanceof TextCheckCell)) {
                                return;
                            }
                            textCheckCell = (TextCheckCell) view;
                            chatBlurEnabled = SharedConfig.pauseMusicOnMedia;
                        }
                    }
                    textCheckCell.setChecked(chatBlurEnabled);
                }
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                z = globalMainSettings.getBoolean("send_by_enter", false);
                SharedPreferences.Editor edit = globalMainSettings.edit();
                edit.putBoolean("send_by_enter", !z);
                edit.commit();
                if (!(view instanceof TextCheckCell)) {
                    return;
                }
            }
            presentFragment(liteModeSettingsActivity);
            return;
        }
        SharedPreferences globalMainSettings2 = MessagesController.getGlobalMainSettings();
        z = globalMainSettings2.getBoolean("view_animations", true);
        SharedPreferences.Editor edit2 = globalMainSettings2.edit();
        boolean z4 = !z;
        edit2.putBoolean("view_animations", z4);
        SharedConfig.setAnimationsEnabled(z4);
        edit2.commit();
        if (!(view instanceof TextCheckCell)) {
            return;
        }
        textCheckCell = (TextCheckCell) view;
        chatBlurEnabled = !z;
        textCheckCell.setChecked(chatBlurEnabled);
    }

    public int lambda$createView$12() {
        return this.sensitiveContentRow;
    }

    public void lambda$createView$13() {
        this.listView.highlightRow(new RecyclerListView.IntReturnCallback() {
            @Override
            public final int run() {
                int lambda$createView$12;
                lambda$createView$12 = ThemeActivity.this.lambda$createView$12();
                return lambda$createView$12;
            }
        });
    }

    public void lambda$createView$2(TL_account.contentSettings contentsettings) {
        ListAdapter listAdapter;
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || !recyclerListView.isAttachedToWindow() || (listAdapter = this.listAdapter) == null) {
            return;
        }
        int i = this.sensitiveContentRow;
        boolean z = false;
        boolean z2 = i >= 0;
        if (contentsettings != null && contentsettings.sensitive_can_change) {
            z = true;
        }
        if (z2 == z) {
            listAdapter.notifyItemChanged(i);
        } else {
            updateRows(true);
        }
    }

    public void lambda$createView$3(int i, AtomicReference atomicReference, View view) {
        SharedConfig.setDistanceSystemType(i);
        this.updateDistance = true;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.distanceRow);
        }
        ((Dialog) atomicReference.get()).dismiss();
    }

    public void lambda$createView$4(int i, AtomicReference atomicReference, View view) {
        SharedConfig.setSearchEngineType(i);
        this.updateSearchEngine = true;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.searchEngineRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.searchEngineRow);
        }
        ((Dialog) atomicReference.get()).dismiss();
    }

    public void lambda$createView$5(AtomicReference atomicReference, View view) {
        SharedConfig.recordViaSco = false;
        SharedConfig.saveConfig();
        this.updateRecordViaSco = true;
        ((Dialog) atomicReference.get()).dismiss();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.bluetoothScoRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.bluetoothScoRow);
        }
    }

    public void lambda$createView$6(AtomicReference atomicReference, Boolean bool) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (bool.booleanValue()) {
            return;
        }
        SharedConfig.recordViaSco = false;
        SharedConfig.saveConfig();
        this.updateRecordViaSco = true;
        ((Dialog) atomicReference.get()).dismiss();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || !recyclerListView.isAttachedToWindow() || (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.bluetoothScoRow)) == null) {
            return;
        }
        this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.bluetoothScoRow);
    }

    public void lambda$createView$7(final AtomicReference atomicReference, View view) {
        PermissionRequest.ensurePermission(R.raw.permission_request_microphone, R.string.PermissionNoBluetoothWithHint, "android.permission.BLUETOOTH_CONNECT", new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ThemeActivity.this.lambda$createView$6(atomicReference, (Boolean) obj);
            }
        });
        SharedConfig.recordViaSco = true;
        SharedConfig.saveConfig();
        this.updateRecordViaSco = true;
        ((Dialog) atomicReference.get()).dismiss();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.bluetoothScoRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.bluetoothScoRow);
        }
    }

    public void lambda$createView$8(View view, AlertDialog alertDialog, int i) {
        getMessagesController().setContentSettings(true);
        if (view instanceof TextCheckCell) {
            ((TextCheckCell) view).setChecked(getMessagesController().showSensitiveContent());
        }
    }

    public void lambda$createView$9(int i, DialogInterface dialogInterface, int i2) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(i);
        }
    }

    public void lambda$didReceivedNotification$1(DialogInterface dialogInterface) {
        this.sharingProgressDialog = null;
        this.sharingTheme = null;
        this.sharingAccent = null;
    }

    public void lambda$getThemeDescriptions$18() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) childAt).getAdapter().notifyDataSetChanged();
            } else if (childAt instanceof PeerColorActivity.ChangeNameColorCell) {
                ((PeerColorActivity.ChangeNameColorCell) childAt).updateColors();
            }
        }
        for (int i2 = 0; i2 < this.listView.getCachedChildCount(); i2++) {
            View cachedChildAt = this.listView.getCachedChildAt(i2);
            if (cachedChildAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) cachedChildAt).getAdapter().notifyDataSetChanged();
            } else if (cachedChildAt instanceof PeerColorActivity.ChangeNameColorCell) {
                ((PeerColorActivity.ChangeNameColorCell) cachedChildAt).updateColors();
            }
        }
        for (int i3 = 0; i3 < this.listView.getHiddenChildCount(); i3++) {
            View hiddenChildAt = this.listView.getHiddenChildAt(i3);
            if (hiddenChildAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) hiddenChildAt).getAdapter().notifyDataSetChanged();
            } else if (hiddenChildAt instanceof PeerColorActivity.ChangeNameColorCell) {
                ((PeerColorActivity.ChangeNameColorCell) hiddenChildAt).updateColors();
            }
        }
        for (int i4 = 0; i4 < this.listView.getAttachedScrapChildCount(); i4++) {
            View attachedScrapChildAt = this.listView.getAttachedScrapChildAt(i4);
            if (attachedScrapChildAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) attachedScrapChildAt).getAdapter().notifyDataSetChanged();
            } else if (attachedScrapChildAt instanceof PeerColorActivity.ChangeNameColorCell) {
                ((PeerColorActivity.ChangeNameColorCell) attachedScrapChildAt).updateColors();
            }
        }
    }

    public static int lambda$updateRows$0(Theme.ThemeInfo themeInfo, Theme.ThemeInfo themeInfo2) {
        return Integer.compare(themeInfo.sortIndex, themeInfo2.sortIndex);
    }

    public void lambda$updateSunTime$15(AlertDialog alertDialog, int i) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception unused) {
        }
    }

    public void lambda$updateSunTime$16(String str) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = str;
        if (str == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude));
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow)) == null) {
            return;
        }
        View view = holder.itemView;
        if (view instanceof TextSettingsCell) {
            ((TextSettingsCell) view).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
        }
    }

    public void lambda$updateSunTime$17() {
        final String str;
        List<Address> fromLocation;
        try {
            fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
        } catch (Exception unused) {
        }
        if (fromLocation.size() > 0) {
            str = fromLocation.get(0).getLocality();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ThemeActivity.this.lambda$updateSunTime$16(str);
                }
            });
        }
        str = null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemeActivity.this.lambda$updateSunTime$16(str);
            }
        });
    }

    public boolean setBubbleRadius(int i, boolean z) {
        if (i == SharedConfig.bubbleRadius) {
            return false;
        }
        SharedConfig.bubbleRadius = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("bubbleRadius", SharedConfig.bubbleRadius);
        edit.commit();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof TextSizeCell) {
                TextSizeCell textSizeCell = (TextSizeCell) view;
                ChatMessageCell[] cells = textSizeCell.messagesCell.getCells();
                for (int i2 = 0; i2 < cells.length; i2++) {
                    cells[i2].getMessageObject().resetLayout();
                    cells[i2].requestLayout();
                }
                textSizeCell.invalidate();
            }
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.bubbleRadiusRow);
        if (findViewHolderForAdapterPosition2 != null) {
            View view2 = findViewHolderForAdapterPosition2.itemView;
            if (view2 instanceof BubbleRadiusCell) {
                BubbleRadiusCell bubbleRadiusCell = (BubbleRadiusCell) view2;
                if (z) {
                    bubbleRadiusCell.requestLayout();
                } else {
                    bubbleRadiusCell.invalidate();
                }
            }
        }
        updateMenuItem();
        return true;
    }

    public boolean setFontSize(int i) {
        if (i == SharedConfig.fontSize) {
            return false;
        }
        SharedConfig.fontSize = i;
        SharedConfig.fontSizeIsDefault = false;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (sharedPreferences == null) {
            return false;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("fons_size", SharedConfig.fontSize);
        edit.commit();
        Theme.createCommonMessageResources();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof TextSizeCell) {
                ChatMessageCell[] cells = ((TextSizeCell) view).messagesCell.getCells();
                for (int i2 = 0; i2 < cells.length; i2++) {
                    cells[i2].getMessageObject().resetLayout();
                    cells[i2].requestLayout();
                }
            }
        }
        updateMenuItem();
        return true;
    }

    private void startLocationUpdate() {
        if (this.updatingLocation) {
            return;
        }
        this.updatingLocation = true;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        try {
            locationManager.requestLocationUpdates("gps", 1L, 0.0f, this.gpsLocationListener);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            locationManager.requestLocationUpdates("network", 1L, 0.0f, this.networkLocationListener);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void updateMenuItem() {
        Theme.OverrideWallpaperInfo overrideWallpaperInfo;
        if (this.menuItem == null) {
            return;
        }
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        Theme.ThemeAccent accent = currentTheme.getAccent(false);
        ArrayList arrayList = currentTheme.themeAccents;
        if (arrayList == null || arrayList.isEmpty() || accent == null || accent.id < 100) {
            this.menuItem.hideSubItem(2);
            this.menuItem.hideSubItem(3);
        } else {
            this.menuItem.showSubItem(2);
            this.menuItem.showSubItem(3);
        }
        int i = AndroidUtilities.isTablet() ? 18 : 16;
        Theme.ThemeInfo currentTheme2 = Theme.getCurrentTheme();
        if (SharedConfig.fontSize == i && SharedConfig.bubbleRadius == 17 && currentTheme2.firstAccentIsDefault && currentTheme2.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID && (accent == null || (overrideWallpaperInfo = accent.overrideWallpaper) == null || "d".equals(overrideWallpaperInfo.slug))) {
            this.menuItem.hideSubItem(4);
        } else {
            this.menuItem.showSubItem(4);
        }
    }

    public void updateRows(boolean r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.updateRows(boolean):void");
    }

    public void updateSunTime(Location location, boolean z) {
        Activity parentActivity;
        int checkSelfPermission;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null) {
            checkSelfPermission = parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
            if (checkSelfPermission != 0) {
                parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                return;
            }
        }
        if (getParentActivity() != null) {
            if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                return;
            }
            try {
                if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
                    builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                    builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new AlertDialog.OnButtonClickListener() {
                        @Override
                        public final void onClick(AlertDialog alertDialog, int i) {
                            ThemeActivity.this.lambda$updateSunTime$15(alertDialog, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            location = locationManager.getLastKnownLocation("gps");
            if (location == null) {
                location = locationManager.getLastKnownLocation("network");
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation("passive");
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        if (location == null || z) {
            startLocationUpdate();
            if (location == null) {
                return;
            }
        }
        Theme.autoNightLocationLatitude = location.getLatitude();
        Theme.autoNightLocationLongitude = location.getLongitude();
        int[] calculateSunriseSunset = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
        Theme.autoNightSunriseTime = calculateSunriseSunset[0];
        Theme.autoNightSunsetTime = calculateSunriseSunset[1];
        Theme.autoNightCityName = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = calendar.get(5);
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                ThemeActivity.this.lambda$updateSunTime$17();
            }
        });
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null) {
            View view = holder.itemView;
            if (view instanceof TextInfoPrivacyCell) {
                ((TextInfoPrivacyCell) view).setText(getLocationSunString());
            }
        }
        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    public void checkCurrentDayNight() {
        if (this.currentType != 3) {
            return;
        }
        boolean z = !Theme.isCurrentThemeDay();
        if (this.lastIsDarkTheme != z) {
            this.lastIsDarkTheme = z;
            this.sunDrawable.setCustomEndFrame(z ? r1.getFramesCount() - 1 : 0);
            this.menuItem.getIconView().playAnimation();
        }
        if (this.themeListRow2 >= 0) {
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                if (this.listView.getChildAt(i) instanceof DefaultThemesPreviewCell) {
                    ((DefaultThemesPreviewCell) this.listView.getChildAt(i)).updateDayNightMode();
                }
            }
        }
    }

    @Override
    public View createView(final Context context) {
        this.lastIsDarkTheme = !Theme.isCurrentThemeDay();
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        int i = this.currentType;
        if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("BrowseThemes", R.string.BrowseThemes));
            ActionBarMenu createMenu = this.actionBar.createMenu();
            int i2 = R.raw.sun;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
            this.sunDrawable = rLottieDrawable;
            if (this.lastIsDarkTheme) {
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1);
            } else {
                rLottieDrawable.setCurrentFrame(0);
            }
            this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.menuItem = createMenu.addItem(5, this.sunDrawable);
        } else if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_other);
            this.menuItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            this.menuItem.addSubItem(2, R.drawable.msg_share, LocaleController.getString("ShareTheme", R.string.ShareTheme));
            this.menuItem.addSubItem(3, R.drawable.msg_edit, LocaleController.getString("EditThemeColors", R.string.EditThemeColors));
            this.menuItem.addSubItem(1, R.drawable.msg_palette, LocaleController.getString("CreateNewThemeMenu", R.string.CreateNewThemeMenu));
            this.menuItem.addSubItem(4, R.drawable.msg_reset, LocaleController.getString("ThemeResetToDefaults", R.string.ThemeResetToDefaults));
            if (getMessagesController().getContentSettings() == null) {
                getMessagesController().getContentSettings(new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ThemeActivity.this.lambda$createView$2((TL_account.contentSettings) obj);
                    }
                });
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i3) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i3);
            }

            @Override
            public void onDoubleTap(View view, int i3, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i3, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i3, float f, float f2) {
                ThemeActivity.this.lambda$createView$11(context, view, i3, f, f2);
            }
        });
        if (this.currentType == 0) {
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setDurations(350L);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            this.listView.setItemAnimator(defaultItemAnimator);
        }
        if (this.highlightSensitiveRow) {
            updateRows(false);
            this.highlightSensitiveRow = false;
            this.listView.scrollToPosition(this.listAdapter.getItemCount() - 1);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ThemeActivity.this.lambda$createView$13();
                }
            }, 200L);
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        AlertDialog alertDialog;
        int i4;
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
            return;
        }
        if (i == NotificationCenter.didSetNewWallpapper || i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            updateMenuItem();
            return;
        }
        if (i == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter == null || (i4 = this.themeAccentListRow) == -1) {
                return;
            }
            listAdapter.notifyItemChanged(i4, new Object());
            return;
        }
        if (i == NotificationCenter.themeListUpdated) {
            updateRows(true);
            return;
        }
        if (i == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) objArr[0];
            Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) objArr[1];
            if (themeInfo != this.sharingTheme || themeAccent != this.sharingAccent) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("https://");
            sb.append(getMessagesController().linkPrefix);
            sb.append("/addtheme/");
            sb.append((themeAccent != null ? themeAccent.info : themeInfo.info).slug);
            String sb2 = sb.toString();
            showDialog(new ShareAlert(getParentActivity(), null, sb2, false, sb2, false));
            alertDialog = this.sharingProgressDialog;
            if (alertDialog == null) {
                return;
            }
        } else {
            if (i != NotificationCenter.themeUploadError) {
                if (i != NotificationCenter.needShareTheme) {
                    if (i == NotificationCenter.needSetDayNightTheme) {
                        updateMenuItem();
                        checkCurrentDayNight();
                        return;
                    } else {
                        if (i != NotificationCenter.emojiPreviewThemesChanged || (i3 = this.themeListRow2) < 0) {
                            return;
                        }
                        this.listAdapter.notifyItemChanged(i3);
                        return;
                    }
                }
                if (getParentActivity() == null || this.isPaused) {
                    return;
                }
                this.sharingTheme = (Theme.ThemeInfo) objArr[0];
                this.sharingAccent = (Theme.ThemeAccent) objArr[1];
                AlertDialog alertDialog2 = new AlertDialog(getParentActivity(), 3);
                this.sharingProgressDialog = alertDialog2;
                alertDialog2.setCanCancel(true);
                showDialog(this.sharingProgressDialog, new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ThemeActivity.this.lambda$didReceivedNotification$1(dialogInterface);
                    }
                });
                return;
            }
            Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) objArr[0];
            Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) objArr[1];
            if (themeInfo2 != this.sharingTheme || themeAccent2 != this.sharingAccent || (alertDialog = this.sharingProgressDialog) != null) {
                return;
            }
        }
        alertDialog.dismiss();
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, BubbleRadiusCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class, TextCell.class, PeerColorActivity.ChangeNameColorCell.class, SwipeGestureSettingsView.class, DefaultThemesPreviewCell.class, AppIconsSelectorCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i4 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        int i5 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        int i6 = Theme.key_windowBackgroundWhiteValueText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int i7 = Theme.key_windowBackgroundWhiteBlueText4;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        int i8 = Theme.key_switchTrack;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        int i9 = Theme.key_switchTrackChecked;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        int i10 = Theme.key_windowBackgroundWhiteGrayIcon;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i10));
        int i11 = Theme.key_player_progressBackground;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        int i12 = Theme.key_player_progress;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addedIcon));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i12));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i11));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, Theme.key_radioBackground));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, Theme.key_radioBackgroundChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i8));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i9));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        Drawable[] shadowDrawables = Theme.chat_msgInDrawable.getShadowDrawables();
        int i13 = Theme.key_chat_inBubbleShadow;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, shadowDrawables, null, i13));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, i13));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, i13));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_messageTextIn));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_messageTextOut));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyLine));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outTimeText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, i5));
        int i14 = Theme.key_windowBackgroundWhiteHintText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, i14));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, i6));
        arrayList.addAll(SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ThemeActivity.this.lambda$getThemeDescriptions$18();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, i14, i5, i6));
        return arrayList;
    }

    public ThemeActivity highlightSensitiveRow() {
        this.highlightSensitiveRow = true;
        return this;
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiPreviewThemesChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        if (this.currentType == 0) {
            Theme.loadRemoteThemes(this.currentAccount, true);
            Theme.checkCurrentRemoteTheme(true);
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeListUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiPreviewThemesChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
        Theme.saveAutoNightThemeConfig();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
    }
}
