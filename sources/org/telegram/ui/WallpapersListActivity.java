package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;

public class WallpapersListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList actionModeViews;
    private ColorWallpaper addedColorWallpaper;
    private FileWallpaper addedFileWallpaper;
    private ArrayList allWallPapers;
    private HashMap allWallPapersDict;
    private ColorWallpaper catsWallpaper;
    private Paint colorFramePaint;
    private Paint colorPaint;
    private int columnsCount;
    private int currentType;
    private final long dialogId;
    private int galleryHintRow;
    private int galleryRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private HashMap localDict;
    private ArrayList localWallPapers;
    private ArrayList patterns;
    private HashMap patternsDict;
    private AlertDialog progressDialog;
    private int resetInfoRow;
    private int resetRow;
    private int resetSectionRow;
    private int rowCount;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ActionBarMenuItem searchItem;
    private int sectionRow;
    private boolean selectedBackgroundBlurred;
    private boolean selectedBackgroundMotion;
    private String selectedBackgroundSlug;
    private int selectedColor;
    private int selectedGradientColor1;
    private int selectedGradientColor2;
    private int selectedGradientColor3;
    private int selectedGradientRotation;
    private float selectedIntensity;
    private NumberTextView selectedMessagesCountTextView;
    private final LongSparseArray selectedWallPapers;
    private int setColorRow;
    private FileWallpaper themeWallpaper;
    private int totalWallpaperRows;
    private WallpaperUpdater updater;
    private int uploadImageRow;
    private int wallPaperStartRow;
    private ArrayList wallPapers;
    private static final int[][] defaultColorsLight = {new int[]{-2368069, -9722489, -2762611, -7817084}, new int[]{-7487253, -4599318, -3755537, -1320977}, new int[]{-6832405, -5117462, -3755537, -1067044}, new int[]{-7676942, -7827988, -1859606, -9986835}, new int[]{-5190165, -6311702, -4461867, -5053475}, new int[]{-2430264, -6114049, -1258497, -4594945}, new int[]{-2298990, -7347754, -9985038, -8006011}, new int[]{-1399954, -990074, -876865, -1523602}, new int[]{-15438, -1916673, -6222, -471346}, new int[]{-2891798}, new int[]{-5913125}, new int[]{-9463352}, new int[]{-2956375}, new int[]{-5974898}, new int[]{-8537234}, new int[]{-1647186}, new int[]{-2769263}, new int[]{-3431303}, new int[]{-1326919}, new int[]{-2054243}, new int[]{-3573648}, new int[]{-1328696}, new int[]{-2056777}, new int[]{-2984557}, new int[]{-2440467}, new int[]{-2906649}, new int[]{-4880430}, new int[]{-4013331}, new int[]{-5921305}, new int[]{-8421424}, new int[]{-4005139}, new int[]{-5908761}, new int[]{-8406320}, new int[]{-2702663}, new int[]{-6518654}, new int[]{-16777216}};
    private static final int[][] defaultColorsDark = {new int[]{-14797481, -15394250, -14924974, -14006975}, new int[]{-14867905, -14870478, -14997181, -15460815}, new int[]{-14666695, -15720408, -14861254, -15260107}, new int[]{-14932175, -15066075, -14208965, -15000799}, new int[]{-12968902, -14411460, -13029826, -15067598}, new int[]{-13885157, -12307670, -14542561, -12899018}, new int[]{-14797481, -15196106, -14924974, -15325638}, new int[]{-15658442, -15449521, -16047308, -12897955}, new int[]{-13809610, -15258855, -13221071, -15715791}, new int[]{-14865092}, new int[]{-15656154}, new int[]{-16051170}, new int[]{-14731745}, new int[]{-15524075}, new int[]{-15853808}, new int[]{-13685209}, new int[]{-14014945}, new int[]{-15132649}, new int[]{-12374480}, new int[]{-13755362}, new int[]{-14740716}, new int[]{-12374468}, new int[]{-13755352}, new int[]{-14740709}, new int[]{-12833213}, new int[]{-14083026}, new int[]{-14872031}, new int[]{-13554109}, new int[]{-14803922}, new int[]{-15461855}, new int[]{-13680833}, new int[]{-14602960}, new int[]{-15458784}, new int[]{-14211804}, new int[]{-15132906}, new int[]{-16777216}};
    private static final int[] searchColors = {-16746753, -65536, -30208, -13824, -16718798, -14702165, -9240406, -409915, -9224159, -16777216, -10725281, -1};
    private static final String[] searchColorsNames = {"Blue", "Red", "Orange", "Yellow", "Green", "Teal", "Purple", "Pink", "Brown", "Black", "Gray", "White"};
    private static final int[] searchColorsNamesR = {R.string.Blue, R.string.Red, R.string.Orange, R.string.Yellow, R.string.Green, R.string.Teal, R.string.Purple, R.string.Pink, R.string.Brown, R.string.Black, R.string.Gray, R.string.White};

    public class AnonymousClass1 implements WallpaperUpdater.WallpaperUpdaterDelegate {
        AnonymousClass1() {
        }

        public void lambda$didSelectWallpaper$0(TLRPC.WallPaper wallPaper) {
            WallpapersListActivity.this.removeSelfFromStack();
        }

        @Override
        public void didSelectWallpaper(File file, Bitmap bitmap, boolean z) {
            ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(new FileWallpaper("", file, file), bitmap);
            themePreviewActivity.setDialogId(WallpapersListActivity.this.dialogId);
            if (WallpapersListActivity.this.dialogId != 0) {
                themePreviewActivity.setDelegate(new ThemePreviewActivity.WallpaperActivityDelegate() {
                    @Override
                    public final void didSetNewBackground(TLRPC.WallPaper wallPaper) {
                        WallpapersListActivity.AnonymousClass1.this.lambda$didSelectWallpaper$0(wallPaper);
                    }
                });
            }
            WallpapersListActivity.this.presentFragment(themePreviewActivity, z);
        }

        @Override
        public void needOpenColorPicker() {
        }
    }

    public class AnonymousClass2 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass2() {
        }

        public void lambda$onItemClick$0(int[] iArr) {
            int i = iArr[0] - 1;
            iArr[0] = i;
            if (i == 0) {
                WallpapersListActivity.this.loadWallpapers(true);
            }
        }

        public void lambda$onItemClick$1(final int[] iArr, TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$0(iArr);
                }
            });
        }

        public void lambda$onItemClick$2(DialogInterface dialogInterface, int i) {
            TLRPC.TL_inputWallPaper tL_inputWallPaper;
            WallpapersListActivity.this.progressDialog = new AlertDialog(WallpapersListActivity.this.getParentActivity(), 3);
            WallpapersListActivity.this.progressDialog.setCanCancel(false);
            WallpapersListActivity.this.progressDialog.show();
            new ArrayList();
            final int[] iArr = {0};
            for (int i2 = 0; i2 < WallpapersListActivity.this.selectedWallPapers.size(); i2++) {
                Object valueAt = WallpapersListActivity.this.selectedWallPapers.valueAt(i2);
                if (valueAt instanceof ColorWallpaper) {
                    ColorWallpaper colorWallpaper = (ColorWallpaper) valueAt;
                    TLRPC.WallPaper wallPaper = colorWallpaper.parentWallpaper;
                    if (wallPaper == null || wallPaper.id >= 0) {
                        valueAt = wallPaper;
                    } else {
                        WallpapersListActivity.this.getMessagesStorage().deleteWallpaper(colorWallpaper.parentWallpaper.id);
                        WallpapersListActivity.this.localWallPapers.remove(colorWallpaper);
                        WallpapersListActivity.this.localDict.remove(colorWallpaper.getHash());
                    }
                }
                if (valueAt instanceof TLRPC.WallPaper) {
                    iArr[0] = iArr[0] + 1;
                    TLRPC.WallPaper wallPaper2 = (TLRPC.WallPaper) valueAt;
                    TLRPC.TL_account_saveWallPaper tL_account_saveWallPaper = new TLRPC.TL_account_saveWallPaper();
                    tL_account_saveWallPaper.settings = new TLRPC.TL_wallPaperSettings();
                    tL_account_saveWallPaper.unsave = true;
                    if (valueAt instanceof TLRPC.TL_wallPaperNoFile) {
                        TLRPC.TL_inputWallPaperNoFile tL_inputWallPaperNoFile = new TLRPC.TL_inputWallPaperNoFile();
                        tL_inputWallPaperNoFile.id = wallPaper2.id;
                        tL_inputWallPaper = tL_inputWallPaperNoFile;
                    } else {
                        TLRPC.TL_inputWallPaper tL_inputWallPaper2 = new TLRPC.TL_inputWallPaper();
                        tL_inputWallPaper2.id = wallPaper2.id;
                        tL_inputWallPaper2.access_hash = wallPaper2.access_hash;
                        tL_inputWallPaper = tL_inputWallPaper2;
                    }
                    tL_account_saveWallPaper.wallpaper = tL_inputWallPaper;
                    String str = wallPaper2.slug;
                    if (str != null && str.equals(WallpapersListActivity.this.selectedBackgroundSlug)) {
                        WallpapersListActivity.this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
                        Theme.getActiveTheme().setOverrideWallpaper(null);
                        Theme.reloadWallpaper(true);
                    }
                    ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).sendRequest(tL_account_saveWallPaper, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$1(iArr, tLObject, tL_error);
                        }
                    });
                }
            }
            if (iArr[0] == 0) {
                WallpapersListActivity.this.loadWallpapers(true);
            }
            WallpapersListActivity.this.selectedWallPapers.clear();
            ((BaseFragment) WallpapersListActivity.this).actionBar.hideActionMode();
            ((BaseFragment) WallpapersListActivity.this).actionBar.closeSearchField();
        }

        public boolean lambda$onItemClick$3(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
            long j;
            String url;
            StringBuilder sb = new StringBuilder();
            for (int i2 = 0; i2 < WallpapersListActivity.this.selectedWallPapers.size(); i2++) {
                Object valueAt = WallpapersListActivity.this.selectedWallPapers.valueAt(i2);
                if (valueAt instanceof TLRPC.TL_wallPaper) {
                    url = AndroidUtilities.getWallPaperUrl(valueAt);
                } else if (valueAt instanceof ColorWallpaper) {
                    url = ((ColorWallpaper) valueAt).getUrl();
                }
                if (!TextUtils.isEmpty(url)) {
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    sb.append(url);
                }
            }
            WallpapersListActivity.this.selectedWallPapers.clear();
            ((BaseFragment) WallpapersListActivity.this).actionBar.hideActionMode();
            ((BaseFragment) WallpapersListActivity.this).actionBar.closeSearchField();
            if (arrayList.size() > 1 || ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId == UserConfig.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).getClientUserId() || charSequence != null) {
                WallpapersListActivity.this.updateRowsSelection();
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    long j2 = ((MessagesStorage.TopicKey) arrayList.get(i3)).dialogId;
                    if (charSequence != null) {
                        j = j2;
                        SendMessagesHelper.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence.toString(), j2, null, null, null, true, null, null, null, true, 0, null, false));
                    } else {
                        j = j2;
                    }
                    if (!TextUtils.isEmpty(sb)) {
                        SendMessagesHelper.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(sb.toString(), j, null, null, null, true, null, null, null, true, 0, null, false));
                    }
                }
                dialogsActivity.lambda$onBackPressed$319();
            } else {
                long j3 = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                if (DialogObject.isEncryptedDialog(j3)) {
                    bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j3));
                } else {
                    if (DialogObject.isUserDialog(j3)) {
                        bundle.putLong("user_id", j3);
                    } else if (DialogObject.isChatDialog(j3)) {
                        bundle.putLong("chat_id", -j3);
                    }
                    if (!MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                        return true;
                    }
                }
                NotificationCenter.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
                WallpapersListActivity.this.presentFragment(new ChatActivity(bundle), true);
                SendMessagesHelper.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(sb.toString(), j3, null, null, null, true, null, null, null, true, 0, null, false));
            }
            return true;
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (!((BaseFragment) WallpapersListActivity.this).actionBar.isActionModeShowed()) {
                    WallpapersListActivity.this.lambda$onBackPressed$319();
                    return;
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                ((BaseFragment) WallpapersListActivity.this).actionBar.hideActionMode();
                WallpapersListActivity.this.updateRowsSelection();
                return;
            }
            if (i != 4) {
                if (i == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 3);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                        @Override
                        public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
                            boolean lambda$onItemClick$3;
                            lambda$onItemClick$3 = WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$3(dialogsActivity2, arrayList, charSequence, z, z2, i2, topicsFragment);
                            return lambda$onItemClick$3;
                        }
                    });
                    WallpapersListActivity.this.presentFragment(dialogsActivity);
                    return;
                }
                return;
            }
            if (WallpapersListActivity.this.getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(WallpapersListActivity.this.getParentActivity());
            builder.setTitle(LocaleController.formatPluralString("DeleteBackground", WallpapersListActivity.this.selectedWallPapers.size(), new Object[0]));
            builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", R.string.DeleteChatBackgroundsAlert, new Object[0]));
            builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$2(dialogInterface, i2);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            WallpapersListActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }
    }

    private class ColorCell extends View {
        private int color;

        public ColorCell(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            WallpapersListActivity.this.colorPaint.setColor(this.color);
            canvas.drawCircle(AndroidUtilities.dp(25.0f), AndroidUtilities.dp(31.0f), AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorPaint);
            if (this.color == Theme.getColor(Theme.key_windowBackgroundWhite)) {
                canvas.drawCircle(AndroidUtilities.dp(25.0f), AndroidUtilities.dp(31.0f), AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorFramePaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int i) {
            this.color = i;
        }
    }

    public static class ColorWallpaper {
        public int color;
        public Bitmap defaultCache;
        public int gradientColor1;
        public int gradientColor2;
        public int gradientColor3;
        public int gradientRotation;
        public float intensity;
        public boolean isGradient;
        public boolean motion;
        public TLRPC.WallPaper parentWallpaper;
        public File path;
        public TLRPC.TL_wallPaper pattern;
        public long patternId;
        public String slug;

        public ColorWallpaper(String str, int i, int i2, int i3) {
            this.slug = str;
            this.color = i | (-16777216);
            int i4 = i2 == 0 ? 0 : (-16777216) | i2;
            this.gradientColor1 = i4;
            this.gradientRotation = i4 == 0 ? 0 : i3;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(String str, int i, int i2, int i3, int i4) {
            this.slug = str;
            this.color = i | (-16777216);
            this.gradientColor1 = i2 == 0 ? 0 : i2 | (-16777216);
            this.gradientColor2 = i3 == 0 ? 0 : i3 | (-16777216);
            this.gradientColor3 = i4 != 0 ? i4 | (-16777216) : 0;
            this.intensity = 1.0f;
            this.isGradient = true;
        }

        public ColorWallpaper(String str, int i, int i2, int i3, int i4, int i5, float f, boolean z, File file) {
            this.slug = str;
            this.color = i | (-16777216);
            int i6 = i2 == 0 ? 0 : i2 | (-16777216);
            this.gradientColor1 = i6;
            this.gradientColor2 = i3 == 0 ? 0 : i3 | (-16777216);
            this.gradientColor3 = i4 != 0 ? i4 | (-16777216) : 0;
            this.gradientRotation = i6 == 0 ? 45 : i5;
            this.intensity = f;
            this.path = file;
            this.motion = z;
        }

        public String getHash() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(this.color));
            sb.append(this.gradientColor1);
            sb.append(this.gradientColor2);
            sb.append(this.gradientColor3);
            sb.append(this.gradientRotation);
            sb.append(this.intensity);
            String str = this.slug;
            if (str == null) {
                str = "";
            }
            sb.append(str);
            return Utilities.MD5(sb.toString());
        }

        public java.lang.String getUrl() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.ColorWallpaper.getUrl():java.lang.String");
        }
    }

    public static class FileWallpaper {
        public File originalPath;
        public File path;
        public int resId;
        public String slug;
        public int thumbResId;

        public FileWallpaper(String str, int i, int i2) {
            this.slug = str;
            this.resId = i;
            this.thumbResId = i2;
        }

        public FileWallpaper(String str, File file, File file2) {
            this.slug = str;
            this.path = file;
            this.originalPath = file2;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == WallpapersListActivity.this.uploadImageRow || i == WallpapersListActivity.this.galleryRow || i == WallpapersListActivity.this.setColorRow || i == WallpapersListActivity.this.resetRow) {
                return 0;
            }
            if (i == WallpapersListActivity.this.sectionRow || i == WallpapersListActivity.this.resetSectionRow) {
                return 1;
            }
            return (i == WallpapersListActivity.this.resetInfoRow || i == WallpapersListActivity.this.galleryHintRow) ? 3 : 2;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textCell;
            View shadowSectionCell;
            CombinedDrawable combinedDrawable;
            if (i != 0) {
                if (i == 1) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, WallpapersListActivity.this.wallPaperStartRow == -1 ? R.drawable.greydivider_bottom : R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                } else if (i != 3) {
                    textCell = new WallpaperCell(this.mContext) {
                        @Override
                        protected void onWallpaperClick(Object obj, int i2) {
                            WallpapersListActivity.this.onItemClick(this, obj, i2);
                        }

                        @Override
                        protected boolean onWallpaperLongClick(Object obj, int i2) {
                            return WallpapersListActivity.this.onItemLongClick(this, obj, i2);
                        }
                    };
                } else {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }
                combinedDrawable.setFullsize(true);
                shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                textCell = shadowSectionCell;
            } else {
                textCell = new TextCell(this.mContext);
            }
            return new RecyclerListView.Holder(textCell);
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int imageReqId;
        private RecyclerListView innerListView;
        private String lastSearchImageString;
        private String lastSearchString;
        private int lastSearchToken;
        private Context mContext;
        private String nextImagesSearchOffset;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private String selectedColor;
        private ArrayList searchResult = new ArrayList();
        private HashMap searchResultKeys = new HashMap();
        private boolean bingSearchEndReached = true;

        private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
            private CategoryAdapterRecycler() {
            }

            CategoryAdapterRecycler(SearchAdapter searchAdapter, AnonymousClass1 anonymousClass1) {
                this();
            }

            @Override
            public int getItemCount() {
                return WallpapersListActivity.searchColors.length;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((ColorCell) viewHolder.itemView).setColor(WallpapersListActivity.searchColors[i]);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                SearchAdapter searchAdapter = SearchAdapter.this;
                return new RecyclerListView.Holder(new ColorCell(searchAdapter.mContext));
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        private void doSearch(String str) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            searchImages(str, "", true);
            this.lastSearchString = str;
            notifyDataSetChanged();
        }

        public void lambda$onCreateViewHolder$5(View view, int i) {
            String string = LocaleController.getString(R.string.BackgroundSearchColor);
            SpannableString spannableString = new SpannableString(string + " " + LocaleController.getString(WallpapersListActivity.searchColorsNames[i], WallpapersListActivity.searchColorsNamesR[i]));
            spannableString.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_actionBarDefaultSubtitle)), string.length(), spannableString.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannableString);
            WallpapersListActivity.this.searchItem.setSearchFieldHint(null);
            WallpapersListActivity.this.searchItem.setSearchFieldText("", true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[i];
            processSearch("", true);
        }

        public void lambda$processSearch$0(String str) {
            doSearch(str);
            this.searchRunnable = null;
        }

        public void lambda$searchBotUser$1(TLObject tLObject) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
            WallpapersListActivity.this.getMessagesStorage().putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            searchImages(str, "", false);
        }

        public void lambda$searchBotUser$2(final TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        WallpapersListActivity.SearchAdapter.this.lambda$searchBotUser$1(tLObject);
                    }
                });
            }
        }

        public void lambda$searchImages$3(int i, TLObject tLObject) {
            if (i != this.lastSearchToken) {
                return;
            }
            this.imageReqId = 0;
            int size = this.searchResult.size();
            if (tLObject != null) {
                TLRPC.messages_BotResults messages_botresults = (TLRPC.messages_BotResults) tLObject;
                this.nextImagesSearchOffset = messages_botresults.next_offset;
                int size2 = messages_botresults.results.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC.BotInlineResult botInlineResult = messages_botresults.results.get(i2);
                    if ("photo".equals(botInlineResult.type) && !this.searchResultKeys.containsKey(botInlineResult.id)) {
                        MediaController.SearchImage searchImage = new MediaController.SearchImage();
                        TLRPC.Photo photo = botInlineResult.photo;
                        if (photo != null) {
                            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, 320);
                            if (closestPhotoSizeWithSize != null) {
                                searchImage.width = closestPhotoSizeWithSize.w;
                                searchImage.height = closestPhotoSizeWithSize.h;
                                searchImage.photoSize = closestPhotoSizeWithSize;
                                searchImage.photo = botInlineResult.photo;
                                searchImage.size = closestPhotoSizeWithSize.size;
                                searchImage.thumbPhotoSize = closestPhotoSizeWithSize2;
                                searchImage.id = botInlineResult.id;
                                searchImage.type = 0;
                                this.searchResult.add(searchImage);
                                this.searchResultKeys.put(searchImage.id, searchImage);
                            }
                        } else if (botInlineResult.content != null) {
                            int i3 = 0;
                            while (true) {
                                if (i3 >= botInlineResult.content.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute documentAttribute = botInlineResult.content.attributes.get(i3);
                                if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                                    searchImage.width = documentAttribute.w;
                                    searchImage.height = documentAttribute.h;
                                    break;
                                }
                                i3++;
                            }
                            TLRPC.WebDocument webDocument = botInlineResult.thumb;
                            searchImage.thumbUrl = webDocument != null ? webDocument.url : null;
                            TLRPC.WebDocument webDocument2 = botInlineResult.content;
                            searchImage.imageUrl = webDocument2.url;
                            searchImage.size = webDocument2.size;
                            searchImage.id = botInlineResult.id;
                            searchImage.type = 0;
                            this.searchResult.add(searchImage);
                            this.searchResultKeys.put(searchImage.id, searchImage);
                        }
                    }
                }
                this.bingSearchEndReached = size == this.searchResult.size() || this.nextImagesSearchOffset == null;
            }
            if (size != this.searchResult.size()) {
                int i4 = size % WallpapersListActivity.this.columnsCount;
                float f = size;
                int ceil = (int) Math.ceil(f / WallpapersListActivity.this.columnsCount);
                if (i4 != 0) {
                    notifyItemChanged(((int) Math.ceil(f / WallpapersListActivity.this.columnsCount)) - 1);
                }
                WallpapersListActivity.this.searchAdapter.notifyItemRangeInserted(ceil, ((int) Math.ceil(this.searchResult.size() / WallpapersListActivity.this.columnsCount)) - ceil);
            }
            WallpapersListActivity.this.searchEmptyView.showTextView();
        }

        public void lambda$searchImages$4(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    WallpapersListActivity.SearchAdapter.this.lambda$searchImages$3(i, tLObject);
                }
            });
        }

        public void processSearch(final String str, boolean z) {
            if (str != null && this.selectedColor != null) {
                str = "#color" + this.selectedColor + " " + str;
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResult.clear();
                this.searchResultKeys.clear();
                this.bingSearchEndReached = true;
                this.lastSearchString = null;
                if (this.imageReqId != 0) {
                    ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).cancelRequest(this.imageReqId, true);
                    this.imageReqId = 0;
                }
                WallpapersListActivity.this.searchEmptyView.showTextView();
            } else {
                WallpapersListActivity.this.searchEmptyView.showProgress();
                if (z) {
                    doSearch(str);
                } else {
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public final void run() {
                            WallpapersListActivity.SearchAdapter.this.lambda$processSearch$0(str);
                        }
                    };
                    this.searchRunnable = runnable2;
                    AndroidUtilities.runOnUIThread(runnable2, 500L);
                }
            }
            notifyDataSetChanged();
        }

        private void searchBotUser() {
            if (this.searchingUser) {
                return;
            }
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).imageSearchBot;
            ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WallpapersListActivity.SearchAdapter.this.lambda$searchBotUser$2(tLObject, tL_error);
                }
            });
        }

        private void searchImages(String str, String str2, boolean z) {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
            this.lastSearchImageString = str;
            TLObject userOrChat = MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).getUserOrChat(MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).imageSearchBot);
            if (!(userOrChat instanceof TLRPC.User)) {
                if (z) {
                    searchBotUser();
                    return;
                }
                return;
            }
            TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
            tL_messages_getInlineBotResults.query = "#wallpaper " + str;
            tL_messages_getInlineBotResults.bot = MessagesController.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).getInputUser((TLRPC.User) userOrChat);
            tL_messages_getInlineBotResults.offset = str2;
            tL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty();
            final int i = this.lastSearchToken + 1;
            this.lastSearchToken = i;
            this.imageReqId = ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).sendRequest(tL_messages_getInlineBotResults, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WallpapersListActivity.SearchAdapter.this.lambda$searchImages$4(i, tLObject, tL_error);
                }
            });
            ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).bindRequestToGuid(this.imageReqId, ((BaseFragment) WallpapersListActivity.this).classGuid);
        }

        public void clearColor() {
            this.selectedColor = null;
            processSearch(null, true);
        }

        @Override
        public int getItemCount() {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                return 2;
            }
            return (int) Math.ceil(this.searchResult.size() / WallpapersListActivity.this.columnsCount);
        }

        @Override
        public int getItemViewType(int i) {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                return i == 0 ? 2 : 1;
            }
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 2;
        }

        public void loadMoreResults() {
            if (this.bingSearchEndReached || this.imageReqId != 0) {
                return;
            }
            searchImages(this.lastSearchString, this.nextImagesSearchOffset, true);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType != 2) {
                    return;
                }
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString(R.string.SearchByColor));
                return;
            }
            WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
            int i2 = i * WallpapersListActivity.this.columnsCount;
            wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, i2 == 0, i2 / WallpapersListActivity.this.columnsCount == ((int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)))) - 1);
            for (int i3 = 0; i3 < WallpapersListActivity.this.columnsCount; i3++) {
                int i4 = i2 + i3;
                wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, i3, i4 < this.searchResult.size() ? this.searchResult.get(i4) : null, "", null, false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = null;
                Object[] objArr = 0;
                if (i == 1) {
                    RecyclerListView recyclerListView = new RecyclerListView(this.mContext) {
                        @Override
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    recyclerListView.setItemAnimator(null);
                    recyclerListView.setLayoutAnimation(null);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext) {
                        @Override
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    recyclerListView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                    recyclerListView.setClipToPadding(false);
                    linearLayoutManager.setOrientation(0);
                    recyclerListView.setLayoutManager(linearLayoutManager);
                    recyclerListView.setAdapter(new CategoryAdapterRecycler(this, objArr == true ? 1 : 0));
                    recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                        @Override
                        public final void onItemClick(View view2, int i2) {
                            WallpapersListActivity.SearchAdapter.this.lambda$onCreateViewHolder$5(view2, i2);
                        }
                    });
                    this.innerListView = recyclerListView;
                    view = recyclerListView;
                } else if (i == 2) {
                    view = new GraySectionCell(this.mContext);
                }
            } else {
                view = new WallpaperCell(this.mContext) {
                    @Override
                    protected void onWallpaperClick(Object obj, int i2) {
                        WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(obj, null, true, false));
                    }
                };
            }
            view.setLayoutParams(i == 1 ? new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(60.0f)) : new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onDestroy() {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(((BaseFragment) WallpapersListActivity.this).currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
    }

    public WallpapersListActivity(int i) {
        this(i, 0L);
    }

    public WallpapersListActivity(int i, long j) {
        this.actionModeViews = new ArrayList();
        this.columnsCount = 3;
        this.selectedBackgroundSlug = "";
        this.allWallPapers = new ArrayList();
        this.allWallPapersDict = new HashMap();
        this.localDict = new HashMap();
        this.wallPapers = new ArrayList();
        this.localWallPapers = new ArrayList();
        this.patterns = new ArrayList();
        this.patternsDict = new HashMap();
        this.selectedWallPapers = new LongSparseArray();
        this.currentType = i;
        this.dialogId = j;
    }

    public static void fillDefaultColors(ArrayList arrayList, boolean z) {
        for (int[] iArr : z ? defaultColorsDark : defaultColorsLight) {
            arrayList.add(iArr.length == 1 ? new ColorWallpaper("c", iArr[0], 0, 45) : new ColorWallpaper("c", iArr[0], iArr[1], iArr[2], iArr[3]));
        }
    }

    private void fillWallpapersWithCustom() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.fillWallpapersWithCustom():void");
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    WallpapersListActivity.this.fixLayoutInternal();
                    if (WallpapersListActivity.this.listView == null) {
                        return true;
                    }
                    WallpapersListActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    public void fixLayoutInternal() {
        if (getParentActivity() == null) {
            return;
        }
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if (!AndroidUtilities.isTablet() && (rotation == 3 || rotation == 1)) {
            this.columnsCount = 5;
        } else {
            this.columnsCount = 3;
        }
        updateRows();
    }

    private String getWallPaperSlug(Object obj) {
        if (obj instanceof TLRPC.TL_wallPaper) {
            return ((TLRPC.TL_wallPaper) obj).slug;
        }
        if (obj instanceof ColorWallpaper) {
            return ((ColorWallpaper) obj).slug;
        }
        if (obj instanceof FileWallpaper) {
            return ((FileWallpaper) obj).slug;
        }
        return null;
    }

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$1() {
        loadWallpapers(false);
    }

    public void lambda$createView$2(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WallpapersListActivity.this.lambda$createView$1();
            }
        });
    }

    public void lambda$createView$3(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.show();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWallPapers(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.lambda$createView$2(tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$4(View view, int i) {
        if (getParentActivity() == null || this.listView.getAdapter() == this.searchAdapter) {
            return;
        }
        if (i == this.uploadImageRow) {
            this.updater.openGallery();
            return;
        }
        if (i == this.setColorRow) {
            WallpapersListActivity wallpapersListActivity = new WallpapersListActivity(1);
            wallpapersListActivity.patterns = this.patterns;
            presentFragment(wallpapersListActivity);
        } else if (i == this.resetRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.ResetChatBackgroundsAlertTitle));
            builder.setMessage(LocaleController.getString(R.string.ResetChatBackgroundsAlert));
            builder.setPositiveButton(LocaleController.getString(R.string.Reset), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    WallpapersListActivity.this.lambda$createView$3(dialogInterface, i2);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }
    }

    public int lambda$fillWallpapersWithCustom$9(long j, String str, boolean z, Object obj, Object obj2) {
        if (obj instanceof ColorWallpaper) {
            obj = ((ColorWallpaper) obj).parentWallpaper;
        }
        if (obj2 instanceof ColorWallpaper) {
            obj2 = ((ColorWallpaper) obj2).parentWallpaper;
        }
        if (!(obj instanceof TLRPC.WallPaper) || !(obj2 instanceof TLRPC.WallPaper)) {
            return 0;
        }
        TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) obj;
        TLRPC.WallPaper wallPaper2 = (TLRPC.WallPaper) obj2;
        if (j != 0) {
            if (wallPaper.id == j) {
                return -1;
            }
            if (wallPaper2.id == j) {
                return 1;
            }
        } else {
            if (str.equals(wallPaper.slug)) {
                return -1;
            }
            if (str.equals(wallPaper2.slug)) {
                return 1;
            }
        }
        if (!z) {
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(wallPaper.slug)) {
                return -1;
            }
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(wallPaper2.slug)) {
                return 1;
            }
        }
        int indexOf = this.allWallPapers.indexOf(wallPaper);
        int indexOf2 = this.allWallPapers.indexOf(wallPaper2);
        boolean z2 = wallPaper.dark;
        if (!(z2 && wallPaper2.dark) && (z2 || wallPaper2.dark)) {
            return (!z2 || wallPaper2.dark) ? z ? 1 : -1 : z ? -1 : 1;
        }
        if (indexOf > indexOf2) {
            return 1;
        }
        return indexOf < indexOf2 ? -1 : 0;
    }

    public void lambda$loadWallpapers$7(TLObject tLObject, boolean z) {
        int i;
        TLRPC.WallPaperSettings wallPaperSettings;
        boolean z2;
        TLRPC.WallPaperSettings wallPaperSettings2;
        TLRPC.WallPaperSettings wallPaperSettings3;
        TLRPC.Document document;
        if (tLObject instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers tL_account_wallPapers = (TLRPC.TL_account_wallPapers) tLObject;
            this.patterns.clear();
            this.patternsDict.clear();
            int i2 = this.currentType;
            if (i2 != 1 && i2 != 2) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(tL_account_wallPapers.wallpapers);
                this.wallPapers.addAll(this.localWallPapers);
            }
            int size = tL_account_wallPapers.wallpapers.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.WallPaper wallPaper = tL_account_wallPapers.wallpapers.get(i3);
                if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(wallPaper.slug)) {
                    if ((wallPaper instanceof TLRPC.TL_wallPaper) && !(wallPaper.document instanceof TLRPC.TL_documentEmpty)) {
                        this.allWallPapersDict.put(wallPaper.slug, wallPaper);
                        if (wallPaper.pattern && (document = wallPaper.document) != null && !this.patternsDict.containsKey(Long.valueOf(document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        int i4 = this.currentType;
                        if (i4 != 1 && ((!(z2 = wallPaper.pattern) || ((wallPaperSettings3 = wallPaper.settings) != null && wallPaperSettings3.background_color != 0)) && ((i4 != 2 || z2) && (Theme.isCurrentThemeDark() || (wallPaperSettings2 = wallPaper.settings) == null || wallPaperSettings2.intensity >= 0)))) {
                            this.wallPapers.add(wallPaper);
                        }
                    } else if (wallPaper.settings.background_color != 0 && (Theme.isCurrentThemeDark() || (wallPaperSettings = wallPaper.settings) == null || wallPaperSettings.intensity >= 0)) {
                        TLRPC.WallPaperSettings wallPaperSettings4 = wallPaper.settings;
                        int i5 = wallPaperSettings4.second_background_color;
                        ColorWallpaper colorWallpaper = (i5 == 0 || (i = wallPaperSettings4.third_background_color) == 0) ? new ColorWallpaper(null, wallPaperSettings4.background_color, i5, wallPaperSettings4.rotation) : new ColorWallpaper(null, wallPaperSettings4.background_color, i5, i, wallPaperSettings4.fourth_background_color);
                        colorWallpaper.slug = wallPaper.slug;
                        TLRPC.WallPaperSettings wallPaperSettings5 = wallPaper.settings;
                        colorWallpaper.intensity = wallPaperSettings5.intensity / 100.0f;
                        colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(wallPaperSettings5.rotation, false);
                        colorWallpaper.parentWallpaper = wallPaper;
                        this.wallPapers.add(colorWallpaper);
                    }
                }
            }
            fillWallpapersWithCustom();
            getMessagesStorage().putWallpapers(tL_account_wallPapers.wallpapers, 1);
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            if (z) {
                return;
            }
            this.listView.smoothScrollToPosition(0);
        }
    }

    public void lambda$loadWallpapers$8(final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WallpapersListActivity.this.lambda$loadWallpapers$7(tLObject, z);
            }
        });
    }

    public void lambda$onItemClick$5(TLRPC.WallPaper wallPaper) {
        removeSelfFromStack();
    }

    public static void lambda$showAsSheet$6() {
        PhotoViewer.getInstance().closePhoto(false, false);
    }

    public void loadWallpapers(final boolean z) {
        long j = 0;
        if (!z) {
            int size = this.allWallPapers.size();
            long j2 = 0;
            for (int i = 0; i < size; i++) {
                Object obj = this.allWallPapers.get(i);
                if (obj instanceof TLRPC.WallPaper) {
                    long j3 = ((TLRPC.WallPaper) obj).id;
                    if (j3 >= 0) {
                        j2 = MediaDataController.calcHash(j2, j3);
                    }
                }
            }
            j = j2;
        }
        TLRPC.TL_account_getWallPapers tL_account_getWallPapers = new TLRPC.TL_account_getWallPapers();
        tL_account_getWallPapers.hash = j;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getWallPapers, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.lambda$loadWallpapers$8(z, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public void onItemClick(WallpaperCell wallpaperCell, Object obj, int i) {
        WallpaperCell wallpaperCell2;
        int i2;
        Object obj2 = obj;
        boolean z = false;
        if (this.actionBar.isActionModeShowed()) {
            Object obj3 = obj2 instanceof ColorWallpaper ? ((ColorWallpaper) obj2).parentWallpaper : obj2;
            if (obj3 instanceof TLRPC.WallPaper) {
                TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) obj3;
                if (this.selectedWallPapers.indexOfKey(wallPaper.id) >= 0) {
                    this.selectedWallPapers.remove(wallPaper.id);
                } else {
                    this.selectedWallPapers.put(wallPaper.id, obj2);
                }
                if (this.selectedWallPapers.size() == 0) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedWallPapers.size(), true);
                }
                this.scrolling = false;
                if (this.selectedWallPapers.indexOfKey(wallPaper.id) >= 0) {
                    wallpaperCell2 = wallpaperCell;
                    i2 = i;
                    z = true;
                } else {
                    wallpaperCell2 = wallpaperCell;
                    i2 = i;
                }
                wallpaperCell2.setChecked(i2, z, true);
                return;
            }
            return;
        }
        String wallPaperSlug = getWallPaperSlug(obj2);
        boolean z2 = obj2 instanceof TLRPC.TL_wallPaper;
        Object obj4 = obj2;
        if (z2) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj2;
            obj4 = obj2;
            if (tL_wallPaper.pattern) {
                String str = tL_wallPaper.slug;
                TLRPC.WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
                ColorWallpaper colorWallpaper = new ColorWallpaper(str, wallPaperSettings.background_color, wallPaperSettings.second_background_color, wallPaperSettings.third_background_color, wallPaperSettings.fourth_background_color, AndroidUtilities.getWallpaperRotation(wallPaperSettings.rotation, false), r2.intensity / 100.0f, tL_wallPaper.settings.motion, null);
                colorWallpaper.pattern = tL_wallPaper;
                colorWallpaper.parentWallpaper = tL_wallPaper;
                obj4 = colorWallpaper;
            }
        }
        ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(obj4, null, true, false) {
            @Override
            public boolean insideBottomSheet() {
                return true;
            }
        };
        if (this.currentType == 1 || this.dialogId != 0) {
            themePreviewActivity.setDelegate(new ThemePreviewActivity.WallpaperActivityDelegate() {
                @Override
                public final void didSetNewBackground(TLRPC.WallPaper wallPaper2) {
                    WallpapersListActivity.this.lambda$onItemClick$5(wallPaper2);
                }
            });
        }
        if (this.selectedBackgroundSlug.equals(wallPaperSlug)) {
            themePreviewActivity.setInitialModes(this.selectedBackgroundBlurred, this.selectedBackgroundMotion, this.selectedIntensity);
        }
        themePreviewActivity.setPatterns(this.patterns);
        themePreviewActivity.setDialogId(this.dialogId);
        showAsSheet(themePreviewActivity);
    }

    public boolean onItemLongClick(WallpaperCell wallpaperCell, Object obj, int i) {
        int i2 = this.currentType;
        if (i2 != 2 && i2 != 3) {
            Object obj2 = obj instanceof ColorWallpaper ? ((ColorWallpaper) obj).parentWallpaper : obj;
            if (!this.actionBar.isActionModeShowed() && getParentActivity() != null && (obj2 instanceof TLRPC.WallPaper)) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                this.selectedWallPapers.put(((TLRPC.WallPaper) obj2).id, obj);
                this.selectedMessagesCountTextView.setNumber(1, false);
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                for (int i3 = 0; i3 < this.actionModeViews.size(); i3++) {
                    View view = (View) this.actionModeViews.get(i3);
                    AndroidUtilities.clearDrawableAnimation(view);
                    arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
                }
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(250L);
                animatorSet.start();
                this.scrolling = false;
                this.actionBar.showActionMode();
                wallpaperCell.setChecked(i, true, true);
                return true;
            }
        }
        return false;
    }

    private void showAsSheet(ThemePreviewActivity themePreviewActivity) {
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        themePreviewActivity.setResourceProvider(this.resourceProvider);
        bottomSheetParams.onOpenAnimationFinished = new Runnable() {
            @Override
            public final void run() {
                WallpapersListActivity.lambda$showAsSheet$6();
            }
        };
        bottomSheetParams.occupyNavigationBar = true;
        showAsSheet(themePreviewActivity, bottomSheetParams);
    }

    private void updateRows() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.updateRows():void");
    }

    public void updateRowsSelection() {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof WallpaperCell) {
                WallpaperCell wallpaperCell = (WallpaperCell) childAt;
                for (int i2 = 0; i2 < 5; i2++) {
                    wallpaperCell.setChecked(i2, false, true);
                }
            }
        }
    }

    @Override
    public android.view.View createView(android.content.Context r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.WallPaperSettings wallPaperSettings;
        int i3;
        boolean z;
        TLRPC.WallPaperSettings wallPaperSettings2;
        TLRPC.WallPaperSettings wallPaperSettings3;
        if (i != NotificationCenter.wallpapersDidLoad) {
            if (i != NotificationCenter.didSetNewWallpapper) {
                if (i == NotificationCenter.wallpapersNeedReload) {
                    getMessagesStorage().getWallpapers();
                    return;
                }
                return;
            }
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
                return;
            }
            return;
        }
        ArrayList arrayList = (ArrayList) objArr[0];
        this.patterns.clear();
        this.patternsDict.clear();
        int i4 = this.currentType;
        if (i4 != 1 && i4 != 2) {
            this.wallPapers.clear();
            this.localWallPapers.clear();
            this.localDict.clear();
            this.allWallPapers.clear();
            this.allWallPapersDict.clear();
            this.allWallPapers.addAll(arrayList);
        }
        int size = arrayList.size();
        ArrayList arrayList2 = null;
        for (int i5 = 0; i5 < size; i5++) {
            TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) arrayList.get(i5);
            if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(wallPaper.slug)) {
                if (wallPaper instanceof TLRPC.TL_wallPaper) {
                    TLRPC.Document document = wallPaper.document;
                    if (!(document instanceof TLRPC.TL_documentEmpty)) {
                        if (wallPaper.pattern && document != null && !this.patternsDict.containsKey(Long.valueOf(document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        this.allWallPapersDict.put(wallPaper.slug, wallPaper);
                        int i6 = this.currentType;
                        if (i6 != 1 && ((!(z = wallPaper.pattern) || ((wallPaperSettings3 = wallPaper.settings) != null && wallPaperSettings3.background_color != 0)) && ((i6 != 2 || z) && (Theme.isCurrentThemeDark() || (wallPaperSettings2 = wallPaper.settings) == null || wallPaperSettings2.intensity >= 0)))) {
                            this.wallPapers.add(wallPaper);
                        }
                    }
                }
                TLRPC.WallPaperSettings wallPaperSettings4 = wallPaper.settings;
                int i7 = wallPaperSettings4.background_color;
                if (i7 != 0) {
                    int i8 = wallPaperSettings4.second_background_color;
                    ColorWallpaper colorWallpaper = (i8 == 0 || (i3 = wallPaperSettings4.third_background_color) == 0) ? new ColorWallpaper(null, i7, i8, wallPaperSettings4.rotation) : new ColorWallpaper(null, i7, i8, i3, wallPaperSettings4.fourth_background_color);
                    colorWallpaper.slug = wallPaper.slug;
                    TLRPC.WallPaperSettings wallPaperSettings5 = wallPaper.settings;
                    colorWallpaper.intensity = wallPaperSettings5.intensity / 100.0f;
                    colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(wallPaperSettings5.rotation, false);
                    colorWallpaper.parentWallpaper = wallPaper;
                    if (wallPaper.id < 0) {
                        String hash = colorWallpaper.getHash();
                        if (this.localDict.containsKey(hash)) {
                            if (arrayList2 == null) {
                                arrayList2 = new ArrayList();
                            }
                            arrayList2.add(wallPaper);
                        } else {
                            this.localWallPapers.add(colorWallpaper);
                            this.localDict.put(hash, colorWallpaper);
                        }
                    }
                    if (Theme.isCurrentThemeDark() || (wallPaperSettings = wallPaper.settings) == null || wallPaperSettings.intensity >= 0) {
                        this.wallPapers.add(colorWallpaper);
                    }
                }
            }
        }
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            for (int i9 = 0; i9 < size2; i9++) {
                getMessagesStorage().deleteWallpaper(((TLRPC.WallPaper) arrayList2.get(i9)).id);
            }
        }
        if (this.dialogId == 0) {
            this.selectedBackgroundSlug = Theme.getSelectedBackgroundSlug();
        }
        fillWallpapersWithCustom();
        loadWallpapers(false);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        View view = this.fragmentView;
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(view, 0, null, null, null, null, i));
        View view2 = this.fragmentView;
        int i2 = Theme.key_windowBackgroundGray;
        arrayList.add(new ThemeDescription(view2, 0, null, null, null, null, i2));
        ActionBar actionBar = this.actionBar;
        int i3 = ThemeDescription.FLAG_BACKGROUND;
        int i4 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i3, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        int i5 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i5));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i));
        return arrayList;
    }

    @Override
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.updater.onActivityResult(i, i2, intent);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter globalInstance;
        int i;
        int i2 = this.currentType;
        if (i2 != 0 && i2 != 2) {
            fillDefaultColors(this.wallPapers, Theme.isCurrentThemeDark());
            if (this.currentType == 1 && this.patterns.isEmpty()) {
                globalInstance = NotificationCenter.getGlobalInstance();
                i = NotificationCenter.wallpapersDidLoad;
            }
            return super.onFragmentCreate();
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        globalInstance = NotificationCenter.getGlobalInstance();
        i = NotificationCenter.wallpapersNeedReload;
        globalInstance.addObserver(this, i);
        getMessagesStorage().getWallpapers();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter globalInstance;
        int i;
        int i2 = this.currentType;
        if (i2 != 0 && i2 != 2) {
            if (i2 == 1) {
                globalInstance = NotificationCenter.getGlobalInstance();
                i = NotificationCenter.wallpapersDidLoad;
            }
            this.updater.cleanup();
            super.onFragmentDestroy();
        }
        this.searchAdapter.onDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        globalInstance = NotificationCenter.getGlobalInstance();
        i = NotificationCenter.wallpapersNeedReload;
        globalInstance.removeObserver(this, i);
        this.updater.cleanup();
        super.onFragmentDestroy();
    }

    @Override
    public void onResume() {
        boolean z;
        TLRPC.WallPaper wallPaper;
        super.onResume();
        MessagesController.getGlobalMainSettings();
        Theme.ThemeInfo activeTheme = Theme.getActiveTheme();
        if (this.dialogId != 0) {
            TLRPC.UserFull userFull = getMessagesController().getUserFull(this.dialogId);
            if (userFull != null && (wallPaper = userFull.wallpaper) != null) {
                String str = wallPaper.slug;
                this.selectedBackgroundSlug = str;
                if (str == null) {
                    this.selectedBackgroundSlug = "";
                }
                TLRPC.WallPaperSettings wallPaperSettings = wallPaper.settings;
                if (wallPaperSettings != null) {
                    this.selectedColor = wallPaperSettings.background_color;
                    this.selectedGradientColor1 = wallPaperSettings.second_background_color;
                    this.selectedGradientColor2 = wallPaperSettings.third_background_color;
                    this.selectedGradientColor3 = wallPaperSettings.fourth_background_color;
                    this.selectedGradientRotation = wallPaperSettings.rotation;
                    this.selectedIntensity = wallPaperSettings.intensity;
                    this.selectedBackgroundMotion = wallPaperSettings.motion;
                    z = wallPaperSettings.blur;
                }
            }
            fillWallpapersWithCustom();
            fixLayout();
        }
        Theme.OverrideWallpaperInfo overrideWallpaperInfo = activeTheme.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            String str2 = overrideWallpaperInfo.slug;
            this.selectedBackgroundSlug = str2;
            if (str2 == null) {
                this.selectedBackgroundSlug = "";
            }
            this.selectedColor = overrideWallpaperInfo.color;
            this.selectedGradientColor1 = overrideWallpaperInfo.gradientColor1;
            this.selectedGradientColor2 = overrideWallpaperInfo.gradientColor2;
            this.selectedGradientColor3 = overrideWallpaperInfo.gradientColor3;
            this.selectedGradientRotation = overrideWallpaperInfo.rotation;
            this.selectedIntensity = overrideWallpaperInfo.intensity;
            this.selectedBackgroundMotion = overrideWallpaperInfo.isMotion;
            z = overrideWallpaperInfo.isBlurred;
        } else {
            this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
            z = false;
            this.selectedColor = 0;
            this.selectedGradientColor1 = 0;
            this.selectedGradientColor2 = 0;
            this.selectedGradientColor3 = 0;
            this.selectedGradientRotation = 45;
            this.selectedIntensity = 1.0f;
            this.selectedBackgroundMotion = false;
        }
        this.selectedBackgroundBlurred = z;
        fillWallpapersWithCustom();
        fixLayout();
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.updater.setCurrentPicturePath(bundle.getString("path"));
    }

    @Override
    public void saveSelfArgs(Bundle bundle) {
        String currentPicturePath = this.updater.getCurrentPicturePath();
        if (currentPicturePath != null) {
            bundle.putString("path", currentPicturePath);
        }
    }
}
