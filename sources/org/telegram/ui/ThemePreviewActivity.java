package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.util.Consumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.Stories.recorder.SliderView;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;

public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    private Theme.ThemeAccent accent;
    private ActionBar actionBar2;
    private HintView animationHint;
    private BlurButton applyButton1;
    private BlurButton applyButton2;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    private Theme.ThemeInfo applyingTheme;
    private FrameLayout backgroundButtonsContainer;
    private WallpaperCheckBoxView[] backgroundCheckBoxView;
    private int backgroundColor;
    private int backgroundGradientColor1;
    private int backgroundGradientColor2;
    private int backgroundGradientColor3;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private BackgroundView backgroundImage;
    private BackgroundView[] backgroundImages;
    private ImageView backgroundPlayAnimationImageView;
    private FrameLayout backgroundPlayAnimationView;
    private AnimatorSet backgroundPlayViewAnimator;
    private int backgroundRotation;
    private int backupAccentColor;
    private int backupAccentColor2;
    private long backupBackgroundGradientOverrideColor1;
    private long backupBackgroundGradientOverrideColor2;
    private long backupBackgroundGradientOverrideColor3;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private float backupIntensity;
    private int backupMyMessagesAccentColor;
    private boolean backupMyMessagesAnimated;
    private int backupMyMessagesGradientAccentColor1;
    private int backupMyMessagesGradientAccentColor2;
    private int backupMyMessagesGradientAccentColor3;
    private String backupSlug;
    private final PorterDuff.Mode blendMode;
    private Bitmap blurredBitmap;
    private BitmapDrawable blurredDrawable;
    public TL_stories.TL_premium_boostsStatus boostsStatus;
    private FrameLayout bottomOverlayChat;
    private TextView cancelButton;
    private View changeDayNightView;
    private ValueAnimator changeDayNightViewAnimator;
    private ValueAnimator changeDayNightViewAnimator2;
    private float changeDayNightViewProgress;
    private int checkColor;
    private boolean checkedBoostsLevel;
    private boolean checkingBoostsLevel;
    private ColorPicker colorPicker;
    private int colorType;
    float croppedWidth;
    private float currentIntensity;
    float currentScrollOffset;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private ActionBarMenuItem dayNightItem;
    float defaultScrollOffset;
    private WallpaperActivityDelegate delegate;
    private boolean deleteOnCancel;
    long dialogId;
    private DialogsAdapter dialogsAdapter;
    private float dimAmount;
    private SliderView dimmingSlider;
    private FrameLayout dimmingSliderContainer;
    private TextView doneButton;
    private View dotsContainer;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private boolean editingTheme;
    private ImageView floatingButton;
    private FrameLayout frameLayout;
    GestureDetector2 gestureDetector2;
    private boolean hasScrollingBackground;
    private String imageFilter;
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    private boolean isBlurred;
    private boolean isMotion;
    private WeakReference lastDrawableToBlur;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private TLRPC.TL_wallPaper lastSelectedPattern;
    private int lastSizeHash;
    private RecyclerListView listView;
    private RecyclerListView listView2;
    private String loadingFile;
    private File loadingFileObject;
    private TLRPC.PhotoSize loadingSize;
    private ColoredImageSpan lockSpan;
    float maxScrollOffset;
    private int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    private FrameLayout messagesButtonsContainer;
    private WallpaperCheckBoxView[] messagesCheckBoxView;
    private ImageView messagesPlayAnimationImageView;
    private FrameLayout messagesPlayAnimationView;
    private AnimatorSet messagesPlayViewAnimator;
    private AnimatorSet motionAnimation;
    Theme.MessageDrawable msgOutDrawable;
    Theme.MessageDrawable msgOutDrawableSelected;
    Theme.MessageDrawable msgOutMediaDrawable;
    Theme.MessageDrawable msgOutMediaDrawableSelected;
    private boolean nightTheme;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    DayNightSwitchDelegate onSwitchDayNightDelegate;
    private Bitmap originalBitmap;
    private FrameLayout page1;
    private FrameLayout page2;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private int patternColor;
    private FrameLayout[] patternLayout;
    private TextView patternTitleView;
    private AnimatorSet patternViewAnimation;
    private ArrayList patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer;
    private TextView[] patternsCancelButton;
    private HashMap patternsDict;
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor1;
    private int previousBackgroundGradientColor2;
    private int previousBackgroundGradientColor3;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC.TL_wallPaper previousSelectedPattern;
    private float progressToDarkTheme;
    private boolean progressVisible;
    private boolean removeBackgroundOverride;
    private boolean rotatePreview;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    private final int screenType;
    private Scroller scroller;
    private TLRPC.TL_wallPaper selectedPattern;
    boolean self;
    MessageObject serverWallpaper;
    private boolean setupFinished;
    private Drawable sheetDrawable;
    private boolean shouldShowBrightnessControll;
    private boolean shouldShowDayNightIcon;
    private boolean showColor;
    private RLottieDrawable sunDrawable;
    public final ThemeDelegate themeDelegate;
    private List themeDescriptions;
    private UndoView undoView;
    public boolean useDefaultThemeForButtons;
    private ValueAnimator valueAnimator;
    private ViewPager viewPager;
    private boolean wasScroll;
    private long watchForKeyboardEndTime;

    public class AnonymousClass27 implements ColorPicker.ColorPickerDelegate {
        AnonymousClass27() {
        }

        public void lambda$deleteTheme$0(AlertDialog alertDialog, int i) {
            Theme.deleteThemeAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.accent, true);
            Theme.applyPreviousTheme();
            Theme.refreshThemeColors();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, -1);
            ThemePreviewActivity.this.lambda$onBackPressed$323();
        }

        @Override
        public void deleteTheme() {
            if (ThemePreviewActivity.this.getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ThemePreviewActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.DeleteThemeTitle));
            builder.setMessage(LocaleController.getString(R.string.DeleteThemeAlert));
            builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i) {
                    ThemePreviewActivity.AnonymousClass27.this.lambda$deleteTheme$0(alertDialog, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            ThemePreviewActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(ThemePreviewActivity.this.getThemedColor(Theme.key_text_RedBold));
            }
        }

        @Override
        public int getDefaultColor(int i) {
            Theme.ThemeAccent themeAccent;
            if (ThemePreviewActivity.this.colorType == 3 && ThemePreviewActivity.this.applyingTheme.firstAccentIsDefault && i == 0 && (themeAccent = (Theme.ThemeAccent) ThemePreviewActivity.this.applyingTheme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID)) != null) {
                return themeAccent.myMessagesAccentColor;
            }
            return 0;
        }

        @Override
        public void openThemeCreate(boolean z) {
            if (!z) {
                AlertsCreator.createThemeCreateDialog(ThemePreviewActivity.this, 1, null, null);
                return;
            }
            if (ThemePreviewActivity.this.accent.info == null) {
                ThemePreviewActivity.this.lambda$onBackPressed$323();
                MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).saveThemeToServer(ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needShareTheme, ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                return;
            }
            String str = "https://" + MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).linkPrefix + "/addtheme/" + ThemePreviewActivity.this.accent.info.slug;
            ThemePreviewActivity.this.showDialog(new ShareAlert(ThemePreviewActivity.this.getParentActivity(), null, str, false, str, false));
        }

        @Override
        public void setColor(int i, int i2, boolean z) {
            if (ThemePreviewActivity.this.screenType == 2) {
                ThemePreviewActivity.this.setBackgroundColor(i, i2, z, true);
            } else {
                ThemePreviewActivity.this.scheduleApplyColor(i, i2, z);
            }
        }
    }

    public class AnonymousClass9 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass9() {
        }

        public void lambda$onItemClick$0(ValueAnimator valueAnimator) {
            ThemePreviewActivity.this.progressToDarkTheme = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ThemePreviewActivity.this.backgroundImage.invalidate();
            ThemePreviewActivity.this.bottomOverlayChat.invalidate();
            ThemePreviewActivity.this.dimmingSlider.setAlpha(ThemePreviewActivity.this.progressToDarkTheme);
            ThemePreviewActivity.this.dimmingSliderContainer.invalidate();
            ThemePreviewActivity.this.invalidateBlur();
        }

        @Override
        public void onItemClick(int i) {
            File file;
            Theme.ThemeAccent accent;
            String url;
            String str;
            int i2 = 0;
            if (i == -1) {
                if (ThemePreviewActivity.this.checkDiscard()) {
                    ThemePreviewActivity.this.cancelThemeApply(false);
                    return;
                }
                return;
            }
            if (i >= 1 && i <= 3) {
                ThemePreviewActivity.this.selectColorType(i);
                return;
            }
            if (i == 4) {
                if (ThemePreviewActivity.this.removeBackgroundOverride) {
                    Theme.resetCustomWallpaper(false);
                }
                File pathToWallpaper = ThemePreviewActivity.this.accent.getPathToWallpaper();
                if (pathToWallpaper != null) {
                    pathToWallpaper.delete();
                }
                ThemePreviewActivity.this.accent.patternSlug = ThemePreviewActivity.this.selectedPattern != null ? ThemePreviewActivity.this.selectedPattern.slug : "";
                ThemePreviewActivity.this.accent.patternIntensity = ThemePreviewActivity.this.currentIntensity;
                ThemePreviewActivity.this.accent.patternMotion = ThemePreviewActivity.this.isMotion;
                if (((int) ThemePreviewActivity.this.accent.backgroundOverrideColor) == 0) {
                    ThemePreviewActivity.this.accent.backgroundOverrideColor = 4294967296L;
                }
                if (((int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1) == 0) {
                    ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1 = 4294967296L;
                }
                if (((int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2) == 0) {
                    ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2 = 4294967296L;
                }
                if (((int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3) == 0) {
                    ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3 = 4294967296L;
                }
                ThemePreviewActivity.this.saveAccentWallpaper();
                NotificationCenter.getGlobalInstance().removeObserver(ThemePreviewActivity.this, NotificationCenter.wallpapersDidLoad);
                Theme.saveThemeAccents(ThemePreviewActivity.this.applyingTheme, true, false, false, true);
                Theme.applyPreviousTheme();
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, -1);
                ThemePreviewActivity.this.lambda$onBackPressed$323();
                return;
            }
            if (i == 5) {
                if (ThemePreviewActivity.this.getParentActivity() == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                if (ThemePreviewActivity.this.isBlurred) {
                    sb.append("blur");
                }
                if (ThemePreviewActivity.this.isMotion) {
                    if (sb.length() > 0) {
                        sb.append("+");
                    }
                    sb.append("motion");
                }
                if (ThemePreviewActivity.this.currentWallpaper instanceof TLRPC.TL_wallPaper) {
                    String str2 = "https://" + MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).linkPrefix + "/bg/" + ((TLRPC.TL_wallPaper) ThemePreviewActivity.this.currentWallpaper).slug;
                    if (sb.length() <= 0) {
                        str = str2;
                        ThemePreviewActivity.this.showDialog(new ShareAlert(ThemePreviewActivity.this.getParentActivity(), null, str, false, str, false) {
                            @Override
                            public void onSend(LongSparseArray longSparseArray, int i3, TLRPC.TL_forumTopic tL_forumTopic) {
                                if (longSparseArray.size() == 1) {
                                    ThemePreviewActivity.this.undoView.showWithAction(((TLRPC.Dialog) longSparseArray.valueAt(0)).id, 61, Integer.valueOf(i3));
                                } else {
                                    ThemePreviewActivity.this.undoView.showWithAction(0L, 61, Integer.valueOf(i3), Integer.valueOf(longSparseArray.size()), (Runnable) null, (Runnable) null);
                                }
                            }
                        });
                        return;
                    } else {
                        url = str2 + "?mode=" + sb.toString();
                    }
                } else if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    WallpapersListActivity.ColorWallpaper colorWallpaper = new WallpapersListActivity.ColorWallpaper(ThemePreviewActivity.this.selectedPattern != null ? ThemePreviewActivity.this.selectedPattern.slug : "c", ThemePreviewActivity.this.backgroundColor, ThemePreviewActivity.this.backgroundGradientColor1, ThemePreviewActivity.this.backgroundGradientColor2, ThemePreviewActivity.this.backgroundGradientColor3, ThemePreviewActivity.this.backgroundRotation, ThemePreviewActivity.this.currentIntensity, ThemePreviewActivity.this.isMotion, null);
                    colorWallpaper.pattern = ThemePreviewActivity.this.selectedPattern;
                    url = colorWallpaper.getUrl();
                } else {
                    if (!BuildVars.DEBUG_PRIVATE_VERSION || (accent = Theme.getActiveTheme().getAccent(false)) == null) {
                        return;
                    }
                    WallpapersListActivity.ColorWallpaper colorWallpaper2 = new WallpapersListActivity.ColorWallpaper(accent.patternSlug, (int) accent.backgroundOverrideColor, (int) accent.backgroundGradientOverrideColor1, (int) accent.backgroundGradientOverrideColor2, (int) accent.backgroundGradientOverrideColor3, accent.backgroundRotation, accent.patternIntensity, accent.patternMotion, null);
                    int size = ThemePreviewActivity.this.patterns.size();
                    while (true) {
                        if (i2 >= size) {
                            break;
                        }
                        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) ThemePreviewActivity.this.patterns.get(i2);
                        if (tL_wallPaper.pattern && accent.patternSlug.equals(tL_wallPaper.slug)) {
                            colorWallpaper2.pattern = tL_wallPaper;
                            break;
                        }
                        i2++;
                    }
                    url = colorWallpaper2.getUrl();
                }
                str = url;
                ThemePreviewActivity.this.showDialog(new ShareAlert(ThemePreviewActivity.this.getParentActivity(), null, str, false, str, false) {
                    @Override
                    public void onSend(LongSparseArray longSparseArray, int i3, TLRPC.TL_forumTopic tL_forumTopic) {
                        if (longSparseArray.size() == 1) {
                            ThemePreviewActivity.this.undoView.showWithAction(((TLRPC.Dialog) longSparseArray.valueAt(0)).id, 61, Integer.valueOf(i3));
                        } else {
                            ThemePreviewActivity.this.undoView.showWithAction(0L, 61, Integer.valueOf(i3), Integer.valueOf(longSparseArray.size()), (Runnable) null, (Runnable) null);
                        }
                    }
                });
                return;
            }
            if (i != 6) {
                if (i == 7 && (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.FileWallpaper) && (file = ((WallpapersListActivity.FileWallpaper) ThemePreviewActivity.this.currentWallpaper).originalPath) != null) {
                    final MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0L, file.getAbsolutePath(), 0, false, 0, 0, 0L);
                    photoEntry.isVideo = false;
                    photoEntry.thumbPath = null;
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(photoEntry);
                    PhotoViewer.getInstance().setParentActivity(ThemePreviewActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 3, false, new PhotoViewer.EmptyPhotoViewerProvider() {
                        @Override
                        public boolean allowCaption() {
                            return false;
                        }

                        @Override
                        public void sendButtonPressed(int i3, VideoEditedInfo videoEditedInfo, boolean z, int i4, boolean z2) {
                            if (photoEntry.imagePath != null) {
                                File file2 = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
                                Point realScreenSize = AndroidUtilities.getRealScreenSize();
                                Bitmap loadBitmap = ImageLoader.loadBitmap(photoEntry.imagePath, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
                                try {
                                    loadBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(file2));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                File file3 = new File(photoEntry.imagePath);
                                ThemePreviewActivity.this.currentWallpaper = new WallpapersListActivity.FileWallpaper("", file3, file3);
                                ThemePreviewActivity.this.currentWallpaperBitmap = loadBitmap;
                                ThemePreviewActivity.this.lastSizeHash = 0;
                                ThemePreviewActivity.this.backgroundImage.requestLayout();
                                ThemePreviewActivity.this.setCurrentImage(false);
                                ThemePreviewActivity.this.blurredBitmap = null;
                                ThemePreviewActivity.this.updateBlurred();
                            }
                        }
                    }, null);
                    return;
                }
                return;
            }
            if (SharedConfig.dayNightWallpaperSwitchHint <= 3) {
                SharedConfig.dayNightWallpaperSwitchHint = 10;
                SharedConfig.increaseDayNightWallpaperSiwtchHint();
            }
            boolean isDark = ThemePreviewActivity.this.onSwitchDayNightDelegate.isDark();
            DayNightSwitchDelegate dayNightSwitchDelegate = ThemePreviewActivity.this.onSwitchDayNightDelegate;
            if (dayNightSwitchDelegate != null) {
                if (!dayNightSwitchDelegate.supportsAnimation()) {
                    ThemePreviewActivity.this.toggleTheme();
                    return;
                }
                ThemePreviewActivity.this.onSwitchDayNightDelegate.switchDayNight(true);
                ThemePreviewActivity.this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
                if (isDark) {
                    ThemePreviewActivity.this.sunDrawable.setCustomEndFrame(0);
                } else {
                    ThemePreviewActivity.this.sunDrawable.setCustomEndFrame(36);
                }
                ThemePreviewActivity.this.sunDrawable.start();
                if (ThemePreviewActivity.this.shouldShowBrightnessControll) {
                    DayNightSwitchDelegate dayNightSwitchDelegate2 = ThemePreviewActivity.this.onSwitchDayNightDelegate;
                    if (dayNightSwitchDelegate2 == null || !dayNightSwitchDelegate2.isDark()) {
                        ThemePreviewActivity.this.dimmingSlider.animateValueTo(0.0f);
                    } else {
                        ThemePreviewActivity.this.dimmingSlider.setVisibility(0);
                        ThemePreviewActivity.this.dimmingSlider.animateValueTo(ThemePreviewActivity.this.dimAmount);
                    }
                    if (ThemePreviewActivity.this.changeDayNightViewAnimator2 != null) {
                        ThemePreviewActivity.this.changeDayNightViewAnimator2.removeAllListeners();
                        ThemePreviewActivity.this.changeDayNightViewAnimator2.cancel();
                    }
                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                    themePreviewActivity.changeDayNightViewAnimator2 = ValueAnimator.ofFloat(themePreviewActivity.progressToDarkTheme, ThemePreviewActivity.this.onSwitchDayNightDelegate.isDark() ? 1.0f : 0.0f);
                    ThemePreviewActivity.this.changeDayNightViewAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ThemePreviewActivity.AnonymousClass9.this.lambda$onItemClick$0(valueAnimator);
                        }
                    });
                    ThemePreviewActivity.this.changeDayNightViewAnimator2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (ThemePreviewActivity.this.onSwitchDayNightDelegate.isDark()) {
                                return;
                            }
                            ThemePreviewActivity.this.dimmingSlider.setVisibility(8);
                        }
                    });
                    ThemePreviewActivity.this.changeDayNightViewAnimator2.setDuration(250L);
                    ThemePreviewActivity.this.changeDayNightViewAnimator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ThemePreviewActivity.this.changeDayNightViewAnimator2.start();
                }
            }
        }
    }

    public class BackgroundView extends BackupImageView {
        public Drawable background;
        boolean drawBackground;
        public float tx;
        public float ty;

        public BackgroundView(Context context) {
            super(context);
            this.drawBackground = true;
        }

        @Override
        public Drawable getBackground() {
            return this.background;
        }

        @Override
        public void onDraw(Canvas canvas) {
            this.tx = 0.0f;
            this.ty = 0.0f;
            if (this.drawBackground) {
                Drawable drawable = this.background;
                if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                    drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                } else if (drawable instanceof BitmapDrawable) {
                    if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        this.background.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / f), (int) Math.ceil(getMeasuredHeight() / f));
                        this.background.draw(canvas);
                        canvas.restore();
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float max = Math.max(getMeasuredWidth() / this.background.getIntrinsicWidth(), measuredHeight / this.background.getIntrinsicHeight());
                        int ceil = (int) Math.ceil(this.background.getIntrinsicWidth() * max * ThemePreviewActivity.this.parallaxScale);
                        int ceil2 = (int) Math.ceil(this.background.getIntrinsicHeight() * max * ThemePreviewActivity.this.parallaxScale);
                        int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                        int i = (measuredHeight - ceil2) / 2;
                        this.ty = i;
                        this.background.setBounds(measuredWidth, i, ceil + measuredWidth, ceil2 + i);
                    }
                }
                this.background.draw(canvas);
            }
            if (ThemePreviewActivity.this.hasScrollingBackground) {
                if (!ThemePreviewActivity.this.scroller.isFinished() && ThemePreviewActivity.this.scroller.computeScrollOffset()) {
                    float startX = ThemePreviewActivity.this.scroller.getStartX();
                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                    if (startX < themePreviewActivity.maxScrollOffset && themePreviewActivity.scroller.getStartX() > 0) {
                        ThemePreviewActivity.this.currentScrollOffset = r1.scroller.getCurrX();
                    }
                    ThemePreviewActivity.this.invalidateBlur();
                    invalidate();
                }
                canvas.save();
                float f2 = -ThemePreviewActivity.this.currentScrollOffset;
                this.tx = f2;
                canvas.translate(f2, 0.0f);
                super.onDraw(canvas);
                canvas.restore();
            } else {
                super.onDraw(canvas);
            }
            if (!ThemePreviewActivity.this.shouldShowBrightnessControll || ThemePreviewActivity.this.dimAmount <= 0.0f) {
                return;
            }
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (ThemePreviewActivity.this.dimAmount * 255.0f * ThemePreviewActivity.this.progressToDarkTheme)));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
            themePreviewActivity.parallaxScale = themePreviewActivity.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
            if (ThemePreviewActivity.this.isMotion) {
                setScaleX(ThemePreviewActivity.this.parallaxScale);
                setScaleY(ThemePreviewActivity.this.parallaxScale);
            }
            ThemePreviewActivity themePreviewActivity2 = ThemePreviewActivity.this;
            themePreviewActivity2.progressVisible = themePreviewActivity2.screenType == 2 && getMeasuredWidth() <= getMeasuredHeight();
            int measuredWidth = getMeasuredWidth() + (getMeasuredHeight() << 16);
            if (ThemePreviewActivity.this.lastSizeHash != measuredWidth) {
                ThemePreviewActivity.this.hasScrollingBackground = false;
                if (ThemePreviewActivity.this.currentWallpaperBitmap != null) {
                    int width = (int) (ThemePreviewActivity.this.currentWallpaperBitmap.getWidth() * (getMeasuredHeight() / ThemePreviewActivity.this.currentWallpaperBitmap.getHeight()));
                    if (width - getMeasuredWidth() > 100) {
                        ThemePreviewActivity.this.hasScrollingBackground = true;
                        ThemePreviewActivity.this.croppedWidth = (int) (getMeasuredWidth() * (ThemePreviewActivity.this.currentWallpaperBitmap.getHeight() / getMeasuredHeight()));
                        ThemePreviewActivity themePreviewActivity3 = ThemePreviewActivity.this;
                        float measuredWidth2 = (width - getMeasuredWidth()) / 2.0f;
                        themePreviewActivity3.currentScrollOffset = measuredWidth2;
                        themePreviewActivity3.defaultScrollOffset = measuredWidth2;
                        ThemePreviewActivity themePreviewActivity4 = ThemePreviewActivity.this;
                        themePreviewActivity4.maxScrollOffset = themePreviewActivity4.currentScrollOffset * 2.0f;
                        setSize(width, getMeasuredHeight());
                        this.drawFromStart = true;
                        ThemePreviewActivity.this.invalidateBlur();
                    }
                }
                if (!ThemePreviewActivity.this.hasScrollingBackground) {
                    setSize(-1, -1);
                    this.drawFromStart = false;
                }
            }
            ThemePreviewActivity.this.lastSizeHash = measuredWidth;
        }

        @Override
        public void setBackground(Drawable drawable) {
            this.background = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return this.background == drawable || super.verifyDrawable(drawable);
        }
    }

    public class BlurButton extends View {
        private final ColorFilter colorFilter;
        private final Paint dimPaint;
        private final Paint dimPaint2;
        private CircularProgressDrawable loadingDrawable;
        private float loadingT;
        private final Drawable rippleDrawable;
        private Text subtext;
        private boolean subtextShown;
        private AnimatedFloat subtextShownT;
        private Text text;

        public BlurButton(Context context) {
            super(context);
            this.subtextShownT = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
            Drawable createRadSelectorDrawable = Theme.createRadSelectorDrawable(285212671, 8, 8);
            this.rippleDrawable = createRadSelectorDrawable;
            this.dimPaint = new Paint(1);
            this.dimPaint2 = new Paint(1);
            this.loadingT = 0.0f;
            createRadSelectorDrawable.setCallback(this);
            ColorMatrix colorMatrix = new ColorMatrix();
            AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.35f);
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.9f);
            this.colorFilter = new ColorMatrixColorFilter(colorMatrix);
        }

        public CharSequence getText() {
            Text text = this.text;
            if (text != null) {
                return text.getText();
            }
            return null;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Text text;
            float dp = AndroidUtilities.dp(8.0f);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            Theme.applyServiceShaderMatrixForView(this, ThemePreviewActivity.this.backgroundImage, ThemePreviewActivity.this.themeDelegate);
            Paint paint = ThemePreviewActivity.this.themeDelegate.getPaint("paintChatActionBackground");
            ColorFilter colorFilter = paint.getColorFilter();
            paint.setColorFilter(this.colorFilter);
            canvas.drawRoundRect(rectF, dp, dp, paint);
            paint.setColorFilter(colorFilter);
            if (ThemePreviewActivity.this.shouldShowBrightnessControll && ThemePreviewActivity.this.dimAmount > 0.0f) {
                this.dimPaint2.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (ThemePreviewActivity.this.dimAmount * 255.0f * ThemePreviewActivity.this.progressToDarkTheme)));
                canvas.drawRoundRect(rectF, dp, dp, this.dimPaint2);
            }
            this.dimPaint.setColor(520093695);
            canvas.drawRoundRect(rectF, dp, dp, this.dimPaint);
            if (this.loadingT > 0.0f) {
                if (this.loadingDrawable == null) {
                    this.loadingDrawable = new CircularProgressDrawable(-1);
                }
                int dp2 = (int) ((1.0f - this.loadingT) * AndroidUtilities.dp(-24.0f));
                this.loadingDrawable.setBounds(0, dp2, getWidth(), getHeight() + dp2);
                this.loadingDrawable.setAlpha((int) (this.loadingT * 255.0f));
                this.loadingDrawable.draw(canvas);
                invalidate();
            }
            float f = this.subtextShownT.set(this.subtextShown);
            if (this.loadingT < 1.0f && (text = this.text) != null) {
                text.ellipsize(getWidth() - AndroidUtilities.dp(14.0f)).draw(canvas, (getWidth() - this.text.getWidth()) / 2.0f, ((getHeight() / 2.0f) + (this.loadingT * AndroidUtilities.dp(24.0f))) - (AndroidUtilities.dp(7.0f) * f), -1, 1.0f - this.loadingT);
            }
            if (this.loadingT < 1.0f && this.subtext != null) {
                canvas.save();
                canvas.scale(f, f, getWidth() / 2.0f, (getHeight() / 2.0f) + AndroidUtilities.dp(11.0f));
                this.subtext.ellipsize(getWidth() - AndroidUtilities.dp(14.0f)).draw(canvas, (getWidth() - this.subtext.getWidth()) / 2.0f, (getHeight() / 2.0f) + (this.loadingT * AndroidUtilities.dp(24.0f)) + AndroidUtilities.dp(11.0f), Theme.multAlpha(-1, 0.75f), 1.0f - this.loadingT);
                canvas.restore();
            }
            this.rippleDrawable.setBounds(0, 0, getWidth(), getHeight());
            this.rippleDrawable.draw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z;
            if (motionEvent.getAction() == 0) {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.rippleDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                this.rippleDrawable.setState(new int[]{16842910, 16842919});
                z = true;
            } else {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.rippleDrawable.setState(StateSet.NOTHING);
                }
                z = false;
            }
            return super.onTouchEvent(motionEvent) || z;
        }

        public void setSubText(CharSequence charSequence, boolean z) {
            if (charSequence != null) {
                this.subtext = new Text(charSequence, 12.0f);
            }
            boolean z2 = charSequence != null;
            this.subtextShown = z2;
            if (!z) {
                this.subtextShownT.set(z2, true);
            }
            invalidate();
        }

        public void setText(CharSequence charSequence) {
            this.text = new Text(charSequence, 14.0f, AndroidUtilities.bold());
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return drawable == this.rippleDrawable || super.verifyDrawable(drawable);
        }
    }

    public interface DayNightSwitchDelegate {
        boolean isDark();

        boolean supportsAnimation();

        void switchDayNight(boolean z);
    }

    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList dialogs = new ArrayList();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
            customDialog.name = LocaleController.getString(R.string.ThemePreviewDialog1);
            customDialog.message = LocaleController.getString(R.string.ThemePreviewDialogMessage1);
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = 2;
            this.dialogs.add(customDialog);
            DialogCell.CustomDialog customDialog2 = new DialogCell.CustomDialog();
            customDialog2.name = LocaleController.getString(R.string.ThemePreviewDialog2);
            customDialog2.message = LocaleController.getString(R.string.ThemePreviewDialogMessage2);
            customDialog2.id = 1;
            customDialog2.unread_count = 2;
            customDialog2.pinned = false;
            customDialog2.muted = false;
            customDialog2.type = 0;
            customDialog2.date = currentTimeMillis - 3600;
            customDialog2.verified = false;
            customDialog2.isMedia = false;
            customDialog2.sent = -1;
            this.dialogs.add(customDialog2);
            DialogCell.CustomDialog customDialog3 = new DialogCell.CustomDialog();
            customDialog3.name = LocaleController.getString(R.string.ThemePreviewDialog3);
            customDialog3.message = LocaleController.getString(R.string.ThemePreviewDialogMessage3);
            customDialog3.id = 2;
            customDialog3.unread_count = 3;
            customDialog3.pinned = false;
            customDialog3.muted = true;
            customDialog3.type = 0;
            customDialog3.date = currentTimeMillis - 7200;
            customDialog3.verified = false;
            customDialog3.isMedia = true;
            customDialog3.sent = -1;
            this.dialogs.add(customDialog3);
            DialogCell.CustomDialog customDialog4 = new DialogCell.CustomDialog();
            customDialog4.name = LocaleController.getString(R.string.ThemePreviewDialog4);
            customDialog4.message = LocaleController.getString(R.string.ThemePreviewDialogMessage4);
            customDialog4.id = 3;
            customDialog4.unread_count = 0;
            customDialog4.pinned = false;
            customDialog4.muted = false;
            customDialog4.type = 2;
            customDialog4.date = currentTimeMillis - 10800;
            customDialog4.verified = false;
            customDialog4.isMedia = false;
            customDialog4.sent = -1;
            this.dialogs.add(customDialog4);
            DialogCell.CustomDialog customDialog5 = new DialogCell.CustomDialog();
            customDialog5.name = LocaleController.getString(R.string.ThemePreviewDialog5);
            customDialog5.message = LocaleController.getString(R.string.ThemePreviewDialogMessage5);
            customDialog5.id = 4;
            customDialog5.unread_count = 0;
            customDialog5.pinned = false;
            customDialog5.muted = false;
            customDialog5.type = 1;
            customDialog5.date = currentTimeMillis - 14400;
            customDialog5.verified = false;
            customDialog5.isMedia = false;
            customDialog5.sent = 2;
            this.dialogs.add(customDialog5);
            DialogCell.CustomDialog customDialog6 = new DialogCell.CustomDialog();
            customDialog6.name = LocaleController.getString(R.string.ThemePreviewDialog6);
            customDialog6.message = LocaleController.getString(R.string.ThemePreviewDialogMessage6);
            customDialog6.id = 5;
            customDialog6.unread_count = 0;
            customDialog6.pinned = false;
            customDialog6.muted = false;
            customDialog6.type = 0;
            customDialog6.date = currentTimeMillis - 18000;
            customDialog6.verified = false;
            customDialog6.isMedia = false;
            customDialog6.sent = -1;
            this.dialogs.add(customDialog6);
            DialogCell.CustomDialog customDialog7 = new DialogCell.CustomDialog();
            customDialog7.name = LocaleController.getString(R.string.ThemePreviewDialog7);
            customDialog7.message = LocaleController.getString(R.string.ThemePreviewDialogMessage7);
            customDialog7.id = 6;
            customDialog7.unread_count = 0;
            customDialog7.pinned = false;
            customDialog7.muted = false;
            customDialog7.type = 0;
            customDialog7.date = currentTimeMillis - 21600;
            customDialog7.verified = true;
            customDialog7.isMedia = false;
            customDialog7.sent = -1;
            this.dialogs.add(customDialog7);
            DialogCell.CustomDialog customDialog8 = new DialogCell.CustomDialog();
            customDialog8.name = LocaleController.getString(R.string.ThemePreviewDialog8);
            customDialog8.message = LocaleController.getString(R.string.ThemePreviewDialogMessage8);
            customDialog8.id = 0;
            customDialog8.unread_count = 0;
            customDialog8.pinned = false;
            customDialog8.muted = false;
            customDialog8.type = 0;
            customDialog8.date = currentTimeMillis - 25200;
            customDialog8.verified = true;
            customDialog8.isMedia = false;
            customDialog8.sent = -1;
            this.dialogs.add(customDialog8);
        }

        @Override
        public int getItemCount() {
            return this.dialogs.size();
        }

        @Override
        public int getItemViewType(int i) {
            return i == this.dialogs.size() ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                dialogCell.useSeparator = i != getItemCount() - 1;
                dialogCell.setDialog((DialogCell.CustomDialog) this.dialogs.get(i));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View dialogCell = i == 0 ? new DialogCell(null, this.mContext, false, false) : new LoadingCell(this.mContext);
            dialogCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(dialogCell);
        }
    }

    private class MessageDrawable extends Theme.MessageDrawable {
        public MessageDrawable(int i, boolean z, boolean z2) {
            super(i, z, z2);
        }

        @Override
        public void setTop(int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2) {
            if (ThemePreviewActivity.this.setupFinished) {
                return;
            }
            super.setTop(i, i2, i3, i4, i5, i6, z, z2);
        }

        @Override
        public void setTop(int i, int i2, int i3, boolean z, boolean z2) {
            if (ThemePreviewActivity.this.setupFinished) {
                return;
            }
            super.setTop(i, i2, i3, z, z2);
        }
    }

    public class MessagesAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList messages;
        private boolean showSecretMessages;

        public MessagesAdapter(Context context) {
            MessageObject messageObject;
            ArrayList arrayList;
            MessageObject messageObject2;
            this.showSecretMessages = ThemePreviewActivity.this.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList();
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            int i = currentTimeMillis - 3600;
            if (ThemePreviewActivity.this.screenType == 2) {
                if (ThemePreviewActivity.this.dialogId >= 0) {
                    TLRPC.TL_message tL_message = new TLRPC.TL_message();
                    tL_message.message = LocaleController.getString(ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper ? R.string.BackgroundColorSinglePreviewLine2 : R.string.BackgroundPreviewLine2);
                    tL_message.date = currentTimeMillis - 3540;
                    tL_message.dialog_id = 1L;
                    tL_message.flags = 259;
                    tL_message.id = 1;
                    tL_message.media = new TLRPC.TL_messageMediaEmpty();
                    tL_message.out = true;
                    TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                    tL_message.from_id = tL_peerUser;
                    tL_peerUser.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                    TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
                    tL_message.peer_id = tL_peerUser2;
                    tL_peerUser2.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                    MessageObject messageObject3 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message, true, false) {
                        @Override
                        public boolean needDrawAvatar() {
                            return false;
                        }
                    };
                    messageObject3.eventId = 1L;
                    messageObject3.resetLayout();
                    this.messages.add(messageObject3);
                }
                TLRPC.TL_message tL_message2 = new TLRPC.TL_message();
                TLRPC.Chat chat = ThemePreviewActivity.this.dialogId < 0 ? ThemePreviewActivity.this.getMessagesController().getChat(Long.valueOf(-ThemePreviewActivity.this.dialogId)) : null;
                if (chat != null) {
                    tL_message2.message = LocaleController.getString(R.string.ChannelBackgroundMessagePreview);
                    TLRPC.TL_message tL_message3 = new TLRPC.TL_message();
                    tL_message3.message = LocaleController.getString(R.string.ChannelBackgroundMessageReplyText);
                    TLRPC.Chat chat2 = chat;
                    messageObject2 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message3, true, false) {
                        @Override
                        public boolean needDrawAvatar() {
                            return false;
                        }
                    };
                    TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                    tL_message2.from_id = tL_peerChannel;
                    tL_peerChannel.channel_id = chat2.id;
                    TLRPC.TL_peerChannel tL_peerChannel2 = new TLRPC.TL_peerChannel();
                    tL_message2.peer_id = tL_peerChannel2;
                    tL_peerChannel2.channel_id = chat2.id;
                } else {
                    tL_message2.message = LocaleController.getString(ThemePreviewActivity.this.dialogId != 0 ? R.string.BackgroundColorSinglePreviewLine3 : ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper ? R.string.BackgroundColorSinglePreviewLine1 : R.string.BackgroundPreviewLine1);
                    tL_message2.from_id = new TLRPC.TL_peerUser();
                    TLRPC.TL_peerUser tL_peerUser3 = new TLRPC.TL_peerUser();
                    tL_message2.peer_id = tL_peerUser3;
                    tL_peerUser3.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                    messageObject2 = null;
                }
                int i2 = currentTimeMillis - 3540;
                tL_message2.date = i2;
                tL_message2.dialog_id = 1L;
                tL_message2.flags = 265;
                tL_message2.id = 1;
                tL_message2.media = new TLRPC.TL_messageMediaEmpty();
                tL_message2.out = false;
                MessageObject messageObject4 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message2, messageObject2, true, false) {
                    @Override
                    public boolean needDrawAvatar() {
                        return false;
                    }
                };
                if (messageObject2 != null) {
                    messageObject4.customReplyName = LocaleController.getString(R.string.ChannelBackgroundMessageReplyName);
                }
                messageObject4.eventId = 1L;
                messageObject4.resetLayout();
                this.messages.add(messageObject4);
                if (ThemePreviewActivity.this.dialogId == 0 || ThemePreviewActivity.this.serverWallpaper != null) {
                    return;
                }
                TLRPC.User user = ThemePreviewActivity.this.getMessagesController().getUser(Long.valueOf(ThemePreviewActivity.this.dialogId));
                TLRPC.TL_message tL_message4 = new TLRPC.TL_message();
                tL_message4.message = "";
                MessageObject messageObject5 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message4, true, false);
                messageObject5.eventId = 1L;
                messageObject5.contentType = 5;
                this.messages.add(messageObject5);
                TLRPC.TL_message tL_message5 = new TLRPC.TL_message();
                tL_message5.message = user != null ? LocaleController.formatString(R.string.ChatBackgroundHint, UserObject.getFirstName(user)) : LocaleController.getString(R.string.ChannelBackgroundHint);
                tL_message5.date = i2;
                tL_message5.dialog_id = 1L;
                tL_message5.flags = 265;
                tL_message5.from_id = new TLRPC.TL_peerUser();
                tL_message5.id = 1;
                tL_message5.media = new TLRPC.TL_messageMediaEmpty();
                tL_message5.out = false;
                TLRPC.TL_peerUser tL_peerUser4 = new TLRPC.TL_peerUser();
                tL_message5.peer_id = tL_peerUser4;
                tL_peerUser4.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                messageObject = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message5, true, false);
                messageObject.eventId = 1L;
                messageObject.resetLayout();
                messageObject.contentType = 1;
            } else {
                if (ThemePreviewActivity.this.screenType == 1) {
                    TLRPC.TL_message tL_message6 = new TLRPC.TL_message();
                    TLRPC.TL_messageMediaDocument tL_messageMediaDocument = new TLRPC.TL_messageMediaDocument();
                    tL_message6.media = tL_messageMediaDocument;
                    tL_messageMediaDocument.document = new TLRPC.TL_document();
                    TLRPC.Document document = tL_message6.media.document;
                    document.mime_type = "audio/mp3";
                    document.file_reference = new byte[0];
                    document.id = -2147483648L;
                    document.size = 2621440L;
                    document.dc_id = Integer.MIN_VALUE;
                    TLRPC.TL_documentAttributeFilename tL_documentAttributeFilename = new TLRPC.TL_documentAttributeFilename();
                    tL_documentAttributeFilename.file_name = LocaleController.getString(R.string.NewThemePreviewReply2) + ".mp3";
                    tL_message6.media.document.attributes.add(tL_documentAttributeFilename);
                    int i3 = currentTimeMillis + (-3540);
                    tL_message6.date = i3;
                    tL_message6.dialog_id = 1L;
                    tL_message6.flags = 259;
                    TLRPC.TL_peerUser tL_peerUser5 = new TLRPC.TL_peerUser();
                    tL_message6.from_id = tL_peerUser5;
                    tL_peerUser5.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    tL_message6.id = 1;
                    tL_message6.out = true;
                    TLRPC.TL_peerUser tL_peerUser6 = new TLRPC.TL_peerUser();
                    tL_message6.peer_id = tL_peerUser6;
                    tL_peerUser6.user_id = 0L;
                    MessageObject messageObject6 = new MessageObject(UserConfig.selectedAccount, tL_message6, true, false);
                    if (BuildVars.DEBUG_PRIVATE_VERSION) {
                        TLRPC.TL_message tL_message7 = new TLRPC.TL_message();
                        tL_message7.message = "this is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text";
                        tL_message7.date = currentTimeMillis - 2640;
                        tL_message7.dialog_id = 1L;
                        tL_message7.flags = 259;
                        TLRPC.TL_peerUser tL_peerUser7 = new TLRPC.TL_peerUser();
                        tL_message7.from_id = tL_peerUser7;
                        tL_peerUser7.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                        tL_message7.id = 1;
                        tL_message7.media = new TLRPC.TL_messageMediaEmpty();
                        tL_message7.out = true;
                        TLRPC.TL_peerUser tL_peerUser8 = new TLRPC.TL_peerUser();
                        tL_message7.peer_id = tL_peerUser8;
                        tL_peerUser8.user_id = 0L;
                        MessageObject messageObject7 = new MessageObject(UserConfig.selectedAccount, tL_message7, true, false);
                        messageObject7.resetLayout();
                        messageObject7.eventId = 1L;
                        this.messages.add(messageObject7);
                    }
                    TLRPC.TL_message tL_message8 = new TLRPC.TL_message();
                    String string = LocaleController.getString(R.string.NewThemePreviewLine3);
                    StringBuilder sb = new StringBuilder(string);
                    int indexOf = string.indexOf(42);
                    int lastIndexOf = string.lastIndexOf(42);
                    if (indexOf != -1 && lastIndexOf != -1) {
                        sb.replace(lastIndexOf, lastIndexOf + 1, "");
                        sb.replace(indexOf, indexOf + 1, "");
                        TLRPC.TL_messageEntityTextUrl tL_messageEntityTextUrl = new TLRPC.TL_messageEntityTextUrl();
                        tL_messageEntityTextUrl.offset = indexOf;
                        tL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                        tL_messageEntityTextUrl.url = "https://telegram.org";
                        tL_message8.entities.add(tL_messageEntityTextUrl);
                    }
                    tL_message8.message = sb.toString();
                    tL_message8.date = currentTimeMillis - 2640;
                    tL_message8.dialog_id = 1L;
                    tL_message8.flags = 259;
                    TLRPC.TL_peerUser tL_peerUser9 = new TLRPC.TL_peerUser();
                    tL_message8.from_id = tL_peerUser9;
                    tL_peerUser9.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    tL_message8.id = 1;
                    tL_message8.media = new TLRPC.TL_messageMediaEmpty();
                    tL_message8.out = true;
                    TLRPC.TL_peerUser tL_peerUser10 = new TLRPC.TL_peerUser();
                    tL_message8.peer_id = tL_peerUser10;
                    tL_peerUser10.user_id = 0L;
                    MessageObject messageObject8 = new MessageObject(UserConfig.selectedAccount, tL_message8, true, false);
                    messageObject8.resetLayout();
                    messageObject8.eventId = 1L;
                    this.messages.add(messageObject8);
                    TLRPC.TL_message tL_message9 = new TLRPC.TL_message();
                    tL_message9.message = LocaleController.getString(R.string.NewThemePreviewLine1);
                    tL_message9.date = i3;
                    tL_message9.dialog_id = 1L;
                    tL_message9.flags = 265;
                    tL_message9.from_id = new TLRPC.TL_peerUser();
                    tL_message9.id = 1;
                    TLRPC.TL_messageReplyHeader tL_messageReplyHeader = new TLRPC.TL_messageReplyHeader();
                    tL_message9.reply_to = tL_messageReplyHeader;
                    tL_messageReplyHeader.flags |= 16;
                    tL_messageReplyHeader.reply_to_msg_id = 5;
                    tL_message9.media = new TLRPC.TL_messageMediaEmpty();
                    tL_message9.out = false;
                    TLRPC.TL_peerUser tL_peerUser11 = new TLRPC.TL_peerUser();
                    tL_message9.peer_id = tL_peerUser11;
                    tL_peerUser11.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    MessageObject messageObject9 = new MessageObject(UserConfig.selectedAccount, tL_message9, true, false);
                    messageObject9.customReplyName = LocaleController.getString(R.string.NewThemePreviewName);
                    messageObject8.customReplyName = "Test User";
                    messageObject9.eventId = 1L;
                    messageObject9.resetLayout();
                    messageObject9.replyMessageObject = messageObject6;
                    messageObject8.replyMessageObject = messageObject9;
                    this.messages.add(messageObject9);
                    this.messages.add(messageObject6);
                    TLRPC.TL_message tL_message10 = new TLRPC.TL_message();
                    tL_message10.date = currentTimeMillis - 3480;
                    tL_message10.dialog_id = 1L;
                    tL_message10.flags = 259;
                    tL_message10.out = false;
                    tL_message10.from_id = new TLRPC.TL_peerUser();
                    tL_message10.id = 1;
                    TLRPC.TL_messageMediaDocument tL_messageMediaDocument2 = new TLRPC.TL_messageMediaDocument();
                    tL_message10.media = tL_messageMediaDocument2;
                    tL_messageMediaDocument2.flags |= 3;
                    tL_messageMediaDocument2.document = new TLRPC.TL_document();
                    TLRPC.Document document2 = tL_message10.media.document;
                    document2.mime_type = "audio/ogg";
                    document2.file_reference = new byte[0];
                    TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
                    tL_documentAttributeAudio.flags = 1028;
                    tL_documentAttributeAudio.duration = 3.0d;
                    tL_documentAttributeAudio.voice = true;
                    tL_documentAttributeAudio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                    tL_message10.media.document.attributes.add(tL_documentAttributeAudio);
                    tL_message10.out = true;
                    TLRPC.TL_peerUser tL_peerUser12 = new TLRPC.TL_peerUser();
                    tL_message10.peer_id = tL_peerUser12;
                    tL_peerUser12.user_id = 0L;
                    MessageObject messageObject10 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message10, true, false);
                    messageObject10.audioProgressSec = 1;
                    messageObject10.audioProgress = 0.3f;
                    messageObject10.useCustomPhoto = true;
                    this.messages.add(messageObject10);
                    return;
                }
                if (this.showSecretMessages) {
                    TLRPC.TL_user tL_user = new TLRPC.TL_user();
                    tL_user.id = 2147483647L;
                    tL_user.first_name = "Me";
                    TLRPC.TL_user tL_user2 = new TLRPC.TL_user();
                    tL_user2.id = 2147483646L;
                    tL_user2.first_name = "Serj";
                    ArrayList<TLRPC.User> arrayList2 = new ArrayList<>();
                    arrayList2.add(tL_user);
                    arrayList2.add(tL_user2);
                    MessagesController.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).putUsers(arrayList2, true);
                    TLRPC.TL_message tL_message11 = new TLRPC.TL_message();
                    tL_message11.message = "Guess why Half-Life 3 was never released.";
                    int i4 = currentTimeMillis - 2640;
                    tL_message11.date = i4;
                    tL_message11.dialog_id = -1L;
                    tL_message11.flags = 259;
                    tL_message11.id = 2147483646;
                    tL_message11.media = new TLRPC.TL_messageMediaEmpty();
                    tL_message11.out = false;
                    TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
                    tL_message11.peer_id = tL_peerChat;
                    tL_peerChat.chat_id = 1L;
                    TLRPC.TL_peerUser tL_peerUser13 = new TLRPC.TL_peerUser();
                    tL_message11.from_id = tL_peerUser13;
                    tL_peerUser13.user_id = tL_user2.id;
                    this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message11, true, false));
                    TLRPC.TL_message tL_message12 = new TLRPC.TL_message();
                    tL_message12.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                    tL_message12.date = i4;
                    tL_message12.dialog_id = -1L;
                    tL_message12.flags = 259;
                    tL_message12.id = 1;
                    tL_message12.media = new TLRPC.TL_messageMediaEmpty();
                    tL_message12.out = false;
                    TLRPC.TL_peerChat tL_peerChat2 = new TLRPC.TL_peerChat();
                    tL_message12.peer_id = tL_peerChat2;
                    tL_peerChat2.chat_id = 1L;
                    TLRPC.TL_peerUser tL_peerUser14 = new TLRPC.TL_peerUser();
                    tL_message12.from_id = tL_peerUser14;
                    tL_peerUser14.user_id = tL_user2.id;
                    this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message12, true, false));
                    TLRPC.TL_message tL_message13 = new TLRPC.TL_message();
                    tL_message13.message = "Is source code for Android coming anytime soon?";
                    tL_message13.date = currentTimeMillis - 3000;
                    tL_message13.dialog_id = -1L;
                    tL_message13.flags = 259;
                    tL_message13.id = 1;
                    tL_message13.media = new TLRPC.TL_messageMediaEmpty();
                    tL_message13.out = false;
                    TLRPC.TL_peerChat tL_peerChat3 = new TLRPC.TL_peerChat();
                    tL_message13.peer_id = tL_peerChat3;
                    tL_peerChat3.chat_id = 1L;
                    TLRPC.TL_peerUser tL_peerUser15 = new TLRPC.TL_peerUser();
                    tL_message13.from_id = tL_peerUser15;
                    tL_peerUser15.user_id = tL_user.id;
                    arrayList = this.messages;
                    messageObject = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message13, true, false);
                    arrayList.add(messageObject);
                }
                TLRPC.TL_message tL_message14 = new TLRPC.TL_message();
                tL_message14.message = LocaleController.getString(R.string.ThemePreviewLine1);
                int i5 = currentTimeMillis - 3540;
                tL_message14.date = i5;
                tL_message14.dialog_id = 1L;
                tL_message14.flags = 259;
                TLRPC.TL_peerUser tL_peerUser16 = new TLRPC.TL_peerUser();
                tL_message14.from_id = tL_peerUser16;
                tL_peerUser16.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tL_message14.id = 1;
                tL_message14.media = new TLRPC.TL_messageMediaEmpty();
                tL_message14.out = true;
                TLRPC.TL_peerUser tL_peerUser17 = new TLRPC.TL_peerUser();
                tL_message14.peer_id = tL_peerUser17;
                tL_peerUser17.user_id = 0L;
                MessageObject messageObject11 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message14, true, false);
                TLRPC.TL_message tL_message15 = new TLRPC.TL_message();
                tL_message15.message = LocaleController.getString(R.string.ThemePreviewLine2);
                tL_message15.date = currentTimeMillis - 2640;
                tL_message15.dialog_id = 1L;
                tL_message15.flags = 259;
                TLRPC.TL_peerUser tL_peerUser18 = new TLRPC.TL_peerUser();
                tL_message15.from_id = tL_peerUser18;
                tL_peerUser18.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tL_message15.id = 1;
                tL_message15.media = new TLRPC.TL_messageMediaEmpty();
                tL_message15.out = true;
                TLRPC.TL_peerUser tL_peerUser19 = new TLRPC.TL_peerUser();
                tL_message15.peer_id = tL_peerUser19;
                tL_peerUser19.user_id = 0L;
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message15, true, false));
                TLRPC.TL_message tL_message16 = new TLRPC.TL_message();
                tL_message16.date = currentTimeMillis - 3470;
                tL_message16.dialog_id = 1L;
                tL_message16.flags = 259;
                tL_message16.from_id = new TLRPC.TL_peerUser();
                tL_message16.id = 5;
                TLRPC.TL_messageMediaDocument tL_messageMediaDocument3 = new TLRPC.TL_messageMediaDocument();
                tL_message16.media = tL_messageMediaDocument3;
                tL_messageMediaDocument3.flags |= 3;
                tL_messageMediaDocument3.document = new TLRPC.TL_document();
                TLRPC.Document document3 = tL_message16.media.document;
                document3.mime_type = "audio/mp4";
                document3.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio2 = new TLRPC.TL_documentAttributeAudio();
                tL_documentAttributeAudio2.duration = 243.0d;
                tL_documentAttributeAudio2.performer = LocaleController.getString(R.string.ThemePreviewSongPerformer);
                tL_documentAttributeAudio2.title = LocaleController.getString(R.string.ThemePreviewSongTitle);
                tL_message16.media.document.attributes.add(tL_documentAttributeAudio2);
                tL_message16.out = false;
                TLRPC.TL_peerUser tL_peerUser20 = new TLRPC.TL_peerUser();
                tL_message16.peer_id = tL_peerUser20;
                tL_peerUser20.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                this.messages.add(new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message16, true, false));
                TLRPC.TL_message tL_message17 = new TLRPC.TL_message();
                tL_message17.message = LocaleController.getString(R.string.ThemePreviewLine3);
                tL_message17.date = i5;
                tL_message17.dialog_id = 1L;
                tL_message17.flags = 265;
                tL_message17.from_id = new TLRPC.TL_peerUser();
                tL_message17.id = 1;
                TLRPC.TL_messageReplyHeader tL_messageReplyHeader2 = new TLRPC.TL_messageReplyHeader();
                tL_message17.reply_to = tL_messageReplyHeader2;
                tL_messageReplyHeader2.flags |= 16;
                tL_messageReplyHeader2.reply_to_msg_id = 5;
                tL_message17.media = new TLRPC.TL_messageMediaEmpty();
                tL_message17.out = false;
                TLRPC.TL_peerUser tL_peerUser21 = new TLRPC.TL_peerUser();
                tL_message17.peer_id = tL_peerUser21;
                tL_peerUser21.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                MessageObject messageObject12 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message17, true, false);
                messageObject12.customReplyName = LocaleController.getString(R.string.ThemePreviewLine3Reply);
                messageObject12.replyMessageObject = messageObject11;
                this.messages.add(messageObject12);
                TLRPC.TL_message tL_message18 = new TLRPC.TL_message();
                tL_message18.date = currentTimeMillis - 3480;
                tL_message18.dialog_id = 1L;
                tL_message18.flags = 259;
                TLRPC.TL_peerUser tL_peerUser22 = new TLRPC.TL_peerUser();
                tL_message18.from_id = tL_peerUser22;
                tL_peerUser22.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                tL_message18.id = 1;
                TLRPC.TL_messageMediaDocument tL_messageMediaDocument4 = new TLRPC.TL_messageMediaDocument();
                tL_message18.media = tL_messageMediaDocument4;
                tL_messageMediaDocument4.flags |= 3;
                tL_messageMediaDocument4.document = new TLRPC.TL_document();
                TLRPC.Document document4 = tL_message18.media.document;
                document4.mime_type = "audio/ogg";
                document4.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio3 = new TLRPC.TL_documentAttributeAudio();
                tL_documentAttributeAudio3.flags = 1028;
                tL_documentAttributeAudio3.duration = 3.0d;
                tL_documentAttributeAudio3.voice = true;
                tL_documentAttributeAudio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tL_message18.media.document.attributes.add(tL_documentAttributeAudio3);
                tL_message18.out = true;
                TLRPC.TL_peerUser tL_peerUser23 = new TLRPC.TL_peerUser();
                tL_message18.peer_id = tL_peerUser23;
                tL_peerUser23.user_id = 0L;
                MessageObject messageObject13 = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message18, true, false);
                messageObject13.audioProgressSec = 1;
                messageObject13.audioProgress = 0.3f;
                messageObject13.useCustomPhoto = true;
                this.messages.add(messageObject13);
                this.messages.add(messageObject11);
                TLRPC.TL_message tL_message19 = new TLRPC.TL_message();
                tL_message19.date = currentTimeMillis - 3590;
                tL_message19.dialog_id = 1L;
                tL_message19.flags = 257;
                tL_message19.from_id = new TLRPC.TL_peerUser();
                tL_message19.id = 1;
                TLRPC.TL_messageMediaPhoto tL_messageMediaPhoto = new TLRPC.TL_messageMediaPhoto();
                tL_message19.media = tL_messageMediaPhoto;
                tL_messageMediaPhoto.flags |= 3;
                tL_messageMediaPhoto.photo = new TLRPC.TL_photo();
                TLRPC.Photo photo = tL_message19.media.photo;
                photo.file_reference = new byte[0];
                photo.has_stickers = false;
                photo.id = 1L;
                photo.access_hash = 0L;
                photo.date = i;
                TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
                tL_photoSize.size = 0;
                tL_photoSize.w = 500;
                tL_photoSize.h = 302;
                tL_photoSize.type = "s";
                tL_photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                tL_message19.media.photo.sizes.add(tL_photoSize);
                tL_message19.message = LocaleController.getString(R.string.ThemePreviewLine4);
                tL_message19.out = false;
                TLRPC.TL_peerUser tL_peerUser24 = new TLRPC.TL_peerUser();
                tL_message19.peer_id = tL_peerUser24;
                tL_peerUser24.user_id = UserConfig.getInstance(((BaseFragment) ThemePreviewActivity.this).currentAccount).getClientUserId();
                messageObject = new MessageObject(((BaseFragment) ThemePreviewActivity.this).currentAccount, tL_message19, true, false);
                messageObject.useCustomPhoto = true;
            }
            arrayList = this.messages;
            arrayList.add(messageObject);
        }

        private boolean hasButtons() {
            if (ThemePreviewActivity.this.messagesButtonsContainer != null && ThemePreviewActivity.this.screenType == 1 && ThemePreviewActivity.this.colorType == 3 && ThemePreviewActivity.this.accent.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            if (ThemePreviewActivity.this.backgroundButtonsContainer != null) {
                if (ThemePreviewActivity.this.screenType == 2) {
                    return true;
                }
                if (ThemePreviewActivity.this.screenType == 1 && ThemePreviewActivity.this.colorType == 2) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getItemCount() {
            int size = this.messages.size();
            return hasButtons() ? size + 1 : size;
        }

        @Override
        public int getItemViewType(int i) {
            if (hasButtons()) {
                if (i == 0) {
                    return ThemePreviewActivity.this.colorType == 3 ? 3 : 2;
                }
                i--;
            }
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return ((MessageObject) this.messages.get(i)).contentType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            FrameLayout frameLayout3;
            if (i == 0) {
                ?? chatMessageCell = new ChatMessageCell(this.mContext, ((BaseFragment) ThemePreviewActivity.this).currentAccount, false, null, new Theme.ResourcesProvider() {
                    @Override
                    public void applyServiceShaderMatrix(int i2, int i3, float f, float f2) {
                        ThemeDelegate themeDelegate = ThemePreviewActivity.this.themeDelegate;
                        if (themeDelegate != null) {
                            themeDelegate.applyServiceShaderMatrix(i2, i3, f, f2);
                        } else {
                            Theme.applyServiceShaderMatrix(i2, i3, f, f2);
                        }
                    }

                    @Override
                    public ColorFilter getAnimatedEmojiColorFilter() {
                        ColorFilter colorFilter;
                        colorFilter = Theme.chat_animatedEmojiTextColorFilter;
                        return colorFilter;
                    }

                    @Override
                    public int getColor(int i2) {
                        return ThemePreviewActivity.this.themeDelegate.getColor(i2);
                    }

                    @Override
                    public int getColorOrDefault(int i2) {
                        return ThemePreviewActivity.this.themeDelegate.getColorOrDefault(i2);
                    }

                    @Override
                    public int getCurrentColor(int i2) {
                        return ThemePreviewActivity.this.themeDelegate.getCurrentColor(i2);
                    }

                    @Override
                    public Drawable getDrawable(String str) {
                        if (str.equals("drawableMsgOut")) {
                            return ThemePreviewActivity.this.msgOutDrawable;
                        }
                        if (str.equals("drawableMsgOutSelected")) {
                            return ThemePreviewActivity.this.msgOutDrawableSelected;
                        }
                        if (str.equals("drawableMsgOutMedia")) {
                            return ThemePreviewActivity.this.msgOutMediaDrawable;
                        }
                        if (str.equals("drawableMsgOutMediaSelected")) {
                            return ThemePreviewActivity.this.msgOutMediaDrawableSelected;
                        }
                        ThemeDelegate themeDelegate = ThemePreviewActivity.this.themeDelegate;
                        return themeDelegate != null ? themeDelegate.getDrawable(str) : Theme.getThemeDrawable(str);
                    }

                    @Override
                    public Paint getPaint(String str) {
                        return ThemePreviewActivity.this.themeDelegate.getPaint(str);
                    }

                    @Override
                    public boolean hasGradientService() {
                        return ThemePreviewActivity.this.themeDelegate.hasGradientService();
                    }

                    @Override
                    public boolean isDark() {
                        return ThemePreviewActivity.this.themeDelegate.isDark();
                    }

                    @Override
                    public void setAnimatedColor(int i2, int i3) {
                        Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i2, i3);
                    }
                });
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
                    public boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i2, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, chat, i2, f, f2);
                    }

                    @Override
                    public boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, user, f, f2);
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
                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i2, float f, float f2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, chat, i2, f, f2, z);
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
                    public void didPressFactCheckWhat(ChatMessageCell chatMessageCell2, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressFactCheckWhat(this, chatMessageCell2, i2, i3);
                    }

                    @Override
                    public void didPressGiveawayChatButton(ChatMessageCell chatMessageCell2, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressGiveawayChatButton(this, chatMessageCell2, i2);
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
                    public void didPressHint(ChatMessageCell chatMessageCell2, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i2);
                    }

                    @Override
                    public void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                    }

                    @Override
                    public void didPressInstantButton(ChatMessageCell chatMessageCell2, int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i2);
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
                    public void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i2, float f, float f2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i2, f, f2, z);
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
                    public void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, user, f, f2, z);
                    }

                    @Override
                    public void didPressUserStatus(ChatMessageCell chatMessageCell2, TLRPC.User user, TLRPC.Document document, String str) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserStatus(this, chatMessageCell2, user, document, str);
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
                    public void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i2, int i3, int i4) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i2, i3, i4);
                    }

                    @Override
                    public void didPressWebPage(ChatMessageCell chatMessageCell2, TLRPC.WebPage webPage, String str, boolean z) {
                        Browser.openUrl(chatMessageCell2.getContext(), str);
                    }

                    @Override
                    public void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    @Override
                    public boolean doNotShowLoadingReply(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$doNotShowLoadingReply(this, messageObject);
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
                    public boolean isProgressLoading(ChatMessageCell chatMessageCell2, int i2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isProgressLoading(this, chatMessageCell2, i2);
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
                    public void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject, str, str2, str3, str4, i2, i3);
                    }

                    @Override
                    public boolean needPlayMessage(ChatMessageCell chatMessageCell2, MessageObject messageObject, boolean z) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, chatMessageCell2, messageObject, z);
                    }

                    @Override
                    public void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    @Override
                    public void needShowPremiumBulletin(int i2) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumBulletin(this, i2);
                    }

                    @Override
                    public boolean onAccessibilityAction(int i2, Bundle bundle) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i2, bundle);
                    }

                    @Override
                    public void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    @Override
                    public void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    @Override
                    public boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2, boolean z) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2, z);
                    }

                    @Override
                    public boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
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
                frameLayout3 = chatMessageCell;
            } else if (i == 1) {
                ?? chatActionCell = new ChatActionCell(this.mContext, false, ThemePreviewActivity.this.themeDelegate);
                chatActionCell.setDelegate(new ChatActionCell.ChatActionCellDelegate() {
                    @Override
                    public boolean canDrawOutboundsContent() {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    @Override
                    public void didClickButton(ChatActionCell chatActionCell2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didClickButton(this, chatActionCell2);
                    }

                    @Override
                    public void didClickImage(ChatActionCell chatActionCell2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didClickImage(this, chatActionCell2);
                    }

                    @Override
                    public boolean didLongPress(ChatActionCell chatActionCell2, float f, float f2) {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$didLongPress(this, chatActionCell2, f, f2);
                    }

                    @Override
                    public void didOpenPremiumGift(ChatActionCell chatActionCell2, TLRPC.TL_premiumGiftOption tL_premiumGiftOption, String str, boolean z) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didOpenPremiumGift(this, chatActionCell2, tL_premiumGiftOption, str, z);
                    }

                    @Override
                    public void didOpenPremiumGiftChannel(ChatActionCell chatActionCell2, String str, boolean z) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didOpenPremiumGiftChannel(this, chatActionCell2, str, z);
                    }

                    @Override
                    public void didPressReaction(ChatActionCell chatActionCell2, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReaction(this, chatActionCell2, reactionCount, z, f, f2);
                    }

                    @Override
                    public void didPressReplyMessage(ChatActionCell chatActionCell2, int i2) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$didPressReplyMessage(this, chatActionCell2, i2);
                    }

                    @Override
                    public void forceUpdate(ChatActionCell chatActionCell2, boolean z) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$forceUpdate(this, chatActionCell2, z);
                    }

                    @Override
                    public BaseFragment getBaseFragment() {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$getBaseFragment(this);
                    }

                    @Override
                    public long getDialogId() {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$getDialogId(this);
                    }

                    @Override
                    public long getTopicId() {
                        return ChatActionCell.ChatActionCellDelegate.CC.$default$getTopicId(this);
                    }

                    @Override
                    public void needOpenInviteLink(TLRPC.TL_chatInviteExported tL_chatInviteExported) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenInviteLink(this, tL_chatInviteExported);
                    }

                    @Override
                    public void needOpenUserProfile(long j) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needOpenUserProfile(this, j);
                    }

                    @Override
                    public void needShowEffectOverlay(ChatActionCell chatActionCell2, TLRPC.Document document, TLRPC.VideoSize videoSize) {
                        ChatActionCell.ChatActionCellDelegate.CC.$default$needShowEffectOverlay(this, chatActionCell2, document, videoSize);
                    }
                });
                frameLayout3 = chatActionCell;
            } else {
                if (i == 2) {
                    if (ThemePreviewActivity.this.backgroundButtonsContainer.getParent() != null) {
                        ((ViewGroup) ThemePreviewActivity.this.backgroundButtonsContainer.getParent()).removeView(ThemePreviewActivity.this.backgroundButtonsContainer);
                    }
                    FrameLayout frameLayout4 = new FrameLayout(this.mContext) {
                        @Override
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), 1073741824));
                        }
                    };
                    frameLayout = ThemePreviewActivity.this.backgroundButtonsContainer;
                    frameLayout2 = frameLayout4;
                } else if (i == 5) {
                    frameLayout3 = new View(ThemePreviewActivity.this.getContext()) {
                        @Override
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), 1073741824));
                        }
                    };
                } else {
                    if (ThemePreviewActivity.this.messagesButtonsContainer.getParent() != null) {
                        ((ViewGroup) ThemePreviewActivity.this.messagesButtonsContainer.getParent()).removeView(ThemePreviewActivity.this.messagesButtonsContainer);
                    }
                    FrameLayout frameLayout5 = new FrameLayout(this.mContext) {
                        @Override
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), 1073741824));
                        }
                    };
                    frameLayout = ThemePreviewActivity.this.messagesButtonsContainer;
                    frameLayout2 = frameLayout5;
                }
                frameLayout2.addView(frameLayout, LayoutHelper.createFrame(-1, 76, 17));
                frameLayout3 = frameLayout2;
            }
            frameLayout3.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(frameLayout3);
        }
    }

    public class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            if (ThemePreviewActivity.this.patterns != null) {
                return ThemePreviewActivity.this.patterns.size();
            }
            return 0;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageReceiver imageReceiver;
            BlendMode blendMode;
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            patternCell.setPattern((TLRPC.TL_wallPaper) ThemePreviewActivity.this.patterns.get(i));
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
            if (Build.VERSION.SDK_INT >= 29) {
                int i2 = 0;
                if (ThemePreviewActivity.this.screenType == 1) {
                    int defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                    int i3 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    if (i3 != 0 || ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2 == 0) {
                        i2 = i3 != 0 ? i3 : defaultAccentColor;
                    }
                } else if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    i2 = ThemePreviewActivity.this.backgroundGradientColor2;
                }
                if (i2 == 0 || ThemePreviewActivity.this.currentIntensity < 0.0f) {
                    imageReceiver = patternCell.getImageReceiver();
                    blendMode = null;
                } else {
                    imageReceiver = ThemePreviewActivity.this.backgroundImage.getImageReceiver();
                    blendMode = BlendMode.SOFT_LIGHT;
                }
                imageReceiver.setBlendMode(blendMode);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCell.PatternCellDelegate() {
                @Override
                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientAngle() {
                    return ThemePreviewActivity.this.screenType == 2 ? ThemePreviewActivity.this.backgroundRotation : ThemePreviewActivity.this.accent.backgroundRotation;
                }

                @Override
                public int getBackgroundGradientColor1() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor1;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientColor2() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor2;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getBackgroundGradientColor3() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor3;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                    int i2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3;
                    return i2 != 0 ? i2 : defaultAccentColor;
                }

                @Override
                public int getCheckColor() {
                    return ThemePreviewActivity.this.checkColor;
                }

                @Override
                public float getIntensity() {
                    return ThemePreviewActivity.this.currentIntensity;
                }

                @Override
                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }

                @Override
                public TLRPC.TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }
            }));
        }
    }

    public class ThemeDelegate implements Theme.ResourcesProvider {
        public final Paint chat_actionBackgroundGradientDarkenPaint;
        public final TextPaint chat_actionTextPaint;
        public final TextPaint chat_actionTextPaint2;
        public final TextPaint chat_botButtonPaint;
        public Theme.ResourcesProvider parentProvider;
        private Bitmap serviceBitmap;
        private Matrix serviceBitmapMatrix;
        public BitmapShader serviceBitmapShader;
        private final SparseIntArray currentColors = new SparseIntArray();
        public final Paint chat_actionBackgroundPaint = new Paint(3);
        public final Paint chat_actionBackgroundSelectedPaint = new Paint(3);

        public ThemeDelegate() {
            Paint paint = new Paint(3);
            this.chat_actionBackgroundGradientDarkenPaint = paint;
            TextPaint textPaint = new TextPaint();
            this.chat_actionTextPaint = textPaint;
            TextPaint textPaint2 = new TextPaint();
            this.chat_actionTextPaint2 = textPaint2;
            TextPaint textPaint3 = new TextPaint();
            this.chat_botButtonPaint = textPaint3;
            textPaint.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
            textPaint2.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
            textPaint3.setTextSize(AndroidUtilities.dp(15.0f));
            textPaint3.setTypeface(AndroidUtilities.bold());
            paint.setColor(352321536);
        }

        public void applyChatServiceMessageColor() {
            applyChatServiceMessageColor(null, null, null, null);
        }

        public void applyChatServiceMessageColor(int[] iArr, Drawable drawable, Drawable drawable2, Float f) {
            float f2;
            int i = Theme.key_chat_serviceBackground;
            int color = getColor(i);
            int color2 = getColor(Theme.key_chat_serviceBackgroundSelected);
            if (drawable == null) {
                drawable = drawable2;
            }
            boolean z = drawable instanceof MotionBackgroundDrawable;
            if ((z || (drawable instanceof BitmapDrawable)) && SharedConfig.getDevicePerformanceClass() != 0 && LiteMode.isEnabled(32)) {
                Bitmap bitmap = z ? ((MotionBackgroundDrawable) drawable).getBitmap() : drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
                if (this.serviceBitmap != bitmap) {
                    this.serviceBitmap = bitmap;
                    Bitmap bitmap2 = this.serviceBitmap;
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    this.serviceBitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
                    if (this.serviceBitmapMatrix == null) {
                        this.serviceBitmapMatrix = new Matrix();
                    }
                }
                this.chat_actionTextPaint.setColor(-1);
                this.chat_actionTextPaint2.setColor(-1);
                this.chat_actionTextPaint.linkColor = -1;
                this.chat_botButtonPaint.setColor(-1);
            } else {
                this.serviceBitmap = null;
                this.serviceBitmapShader = null;
                TextPaint textPaint = this.chat_actionTextPaint;
                int i2 = Theme.key_chat_serviceText;
                textPaint.setColor(getColor(i2));
                this.chat_actionTextPaint2.setColor(getColor(i2));
                this.chat_actionTextPaint.linkColor = getColor(Theme.key_chat_serviceLink);
            }
            this.chat_actionBackgroundPaint.setColor(color);
            this.chat_actionBackgroundSelectedPaint.setColor(color2);
            if (this.serviceBitmapShader == null || !(this.currentColors.indexOfKey(i) < 0 || z || (drawable instanceof BitmapDrawable))) {
                this.chat_actionBackgroundPaint.setColorFilter(null);
                this.chat_actionBackgroundPaint.setShader(null);
                this.chat_actionBackgroundSelectedPaint.setColorFilter(null);
                this.chat_actionBackgroundSelectedPaint.setShader(null);
                return;
            }
            ColorMatrix colorMatrix = new ColorMatrix();
            if (!z) {
                colorMatrix.setSaturation(1.6f);
                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isDark() ? 0.9f : 0.84f);
                f2 = isDark() ? -0.04f : 0.06f;
            } else if (((MotionBackgroundDrawable) drawable).getIntensity() >= 0.0f) {
                colorMatrix.setSaturation(1.6f);
                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isDark() ? 0.97f : 0.92f);
                if (isDark()) {
                    f2 = 0.12f;
                }
                f2 = -0.06f;
            } else {
                colorMatrix.setSaturation(1.1f);
                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isDark() ? 0.4f : 0.8f);
                if (isDark()) {
                    f2 = 0.08f;
                }
                f2 = -0.06f;
            }
            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, f2);
            if (z) {
                float intensity = ((MotionBackgroundDrawable) drawable).getIntensity();
                if (f != null) {
                    intensity = f.floatValue();
                }
                if (intensity >= 0.0f) {
                    colorMatrix.setSaturation(1.8f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.97f);
                } else {
                    colorMatrix.setSaturation(0.5f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.35f);
                }
                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.03f);
            } else {
                colorMatrix.setSaturation(1.6f);
                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.97f);
                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, 0.06f);
            }
            this.chat_actionBackgroundPaint.setShader(this.serviceBitmapShader);
            this.chat_actionBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            this.chat_actionBackgroundPaint.setAlpha(255);
            this.chat_actionBackgroundSelectedPaint.setShader(this.serviceBitmapShader);
            ColorMatrix colorMatrix2 = new ColorMatrix(colorMatrix);
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix2, 0.85f);
            this.chat_actionBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix2));
            this.chat_actionBackgroundSelectedPaint.setAlpha(255);
        }

        @Override
        public void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            BitmapShader bitmapShader;
            Bitmap bitmap = this.serviceBitmap;
            if (bitmap == null || (bitmapShader = this.serviceBitmapShader) == null) {
                Theme.applyServiceShaderMatrix(i, i2, f, f2);
            } else {
                Theme.applyServiceShaderMatrix(bitmap, bitmapShader, this.serviceBitmapMatrix, i, i2, f, f2);
            }
        }

        @Override
        public ColorFilter getAnimatedEmojiColorFilter() {
            ColorFilter colorFilter;
            colorFilter = Theme.chat_animatedEmojiTextColorFilter;
            return colorFilter;
        }

        @Override
        public int getColor(int i) {
            Theme.ResourcesProvider resourcesProvider = this.parentProvider;
            return resourcesProvider != null ? resourcesProvider.getColor(i) : Theme.getColor(i);
        }

        @Override
        public int getColorOrDefault(int i) {
            int color;
            color = getColor(i);
            return color;
        }

        @Override
        public int getCurrentColor(int i) {
            int color;
            Theme.ResourcesProvider resourcesProvider = this.parentProvider;
            if (resourcesProvider != null) {
                return resourcesProvider.getCurrentColor(i);
            }
            color = getColor(i);
            return color;
        }

        @Override
        public Drawable getDrawable(String str) {
            Theme.ResourcesProvider resourcesProvider = this.parentProvider;
            return resourcesProvider != null ? resourcesProvider.getDrawable(str) : Theme.getThemeDrawable(str);
        }

        @Override
        public Paint getPaint(String str) {
            Paint themePaint;
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -1490966183:
                    if (str.equals("paintChatActionText2")) {
                        c = 0;
                        break;
                    }
                    break;
                case 561929466:
                    if (str.equals("paintChatActionBackground")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1712385955:
                    if (str.equals("paintChatBotButton")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1790254137:
                    if (str.equals("paintChatActionBackgroundDarken")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1897339317:
                    if (str.equals("paintChatActionBackgroundSelected")) {
                        c = 4;
                        break;
                    }
                    break;
                case 2030114297:
                    if (str.equals("paintChatActionText")) {
                        c = 5;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return this.chat_actionTextPaint2;
                case 1:
                    return this.chat_actionBackgroundPaint;
                case 2:
                    return this.chat_botButtonPaint;
                case 3:
                    return this.chat_actionBackgroundGradientDarkenPaint;
                case 4:
                    return this.chat_actionBackgroundSelectedPaint;
                case 5:
                    return this.chat_actionTextPaint;
                default:
                    Theme.ResourcesProvider resourcesProvider = this.parentProvider;
                    if (resourcesProvider != null) {
                        return resourcesProvider.getPaint(str);
                    }
                    themePaint = Theme.getThemePaint(str);
                    return themePaint;
            }
        }

        @Override
        public boolean hasGradientService() {
            Theme.ResourcesProvider resourcesProvider = this.parentProvider;
            return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
        }

        @Override
        public boolean isDark() {
            DayNightSwitchDelegate dayNightSwitchDelegate = ThemePreviewActivity.this.onSwitchDayNightDelegate;
            if (dayNightSwitchDelegate != null) {
                return dayNightSwitchDelegate.isDark();
            }
            Theme.ResourcesProvider resourcesProvider = this.parentProvider;
            return resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        }

        @Override
        public void setAnimatedColor(int i, int i2) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i, i2);
        }
    }

    public interface WallpaperActivityDelegate {
        void didSetNewBackground(TLRPC.WallPaper wallPaper);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap) {
        this(obj, bitmap, false, false);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap, boolean z, boolean z2) {
        this.themeDelegate = new ThemeDelegate() {
            @Override
            public boolean isDark() {
                DayNightSwitchDelegate dayNightSwitchDelegate = ThemePreviewActivity.this.onSwitchDayNightDelegate;
                return dayNightSwitchDelegate != null ? dayNightSwitchDelegate.isDark() : super.isDark();
            }
        };
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0();
            }
        };
        this.backgroundImages = new BackgroundView[2];
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap();
        this.currentIntensity = 0.5f;
        this.dimAmount = 0.0f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.self = true;
        this.gestureDetector2 = new GestureDetector2(getContext(), new GestureDetector2.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                if (ThemePreviewActivity.this.scroller == null) {
                    return true;
                }
                ThemePreviewActivity.this.scroller.abortAnimation();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (ThemePreviewActivity.this.scroller == null) {
                    return true;
                }
                ThemePreviewActivity.this.scroller.abortAnimation();
                ThemePreviewActivity.this.scroller.fling((int) ThemePreviewActivity.this.currentScrollOffset, 0, Math.round(-f), Math.round(f2), 0, (int) ThemePreviewActivity.this.maxScrollOffset, 0, Integer.MAX_VALUE);
                ThemePreviewActivity.this.backgroundImage.postInvalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (ThemePreviewActivity.this.scroller != null) {
                    ThemePreviewActivity.this.scroller.abortAnimation();
                }
                ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                themePreviewActivity.currentScrollOffset = Utilities.clamp(themePreviewActivity.currentScrollOffset + f, themePreviewActivity.maxScrollOffset, 0.0f);
                ThemePreviewActivity.this.invalidateBlur();
                ThemePreviewActivity.this.backgroundImage.invalidate();
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onUp(MotionEvent motionEvent) {
            }
        });
        this.checkingBoostsLevel = false;
        this.checkedBoostsLevel = false;
        this.screenType = 2;
        this.showColor = z2;
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        this.rotatePreview = z;
        if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
            this.isMotion = colorWallpaper.motion;
            TLRPC.TL_wallPaper tL_wallPaper = colorWallpaper.pattern;
            this.selectedPattern = tL_wallPaper;
            if (tL_wallPaper != null) {
                float f = colorWallpaper.intensity;
                this.currentIntensity = f;
                if (f < 0.0f && !Theme.getActiveTheme().isDark()) {
                    this.currentIntensity *= -1.0f;
                }
            }
        }
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean z, int i, boolean z2, boolean z3) {
        this.themeDelegate = new ThemeDelegate() {
            @Override
            public boolean isDark() {
                DayNightSwitchDelegate dayNightSwitchDelegate = ThemePreviewActivity.this.onSwitchDayNightDelegate;
                return dayNightSwitchDelegate != null ? dayNightSwitchDelegate.isDark() : super.isDark();
            }
        };
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0();
            }
        };
        this.backgroundImages = new BackgroundView[2];
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap();
        this.currentIntensity = 0.5f;
        this.dimAmount = 0.0f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.self = true;
        this.gestureDetector2 = new GestureDetector2(getContext(), new GestureDetector2.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                if (ThemePreviewActivity.this.scroller == null) {
                    return true;
                }
                ThemePreviewActivity.this.scroller.abortAnimation();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (ThemePreviewActivity.this.scroller == null) {
                    return true;
                }
                ThemePreviewActivity.this.scroller.abortAnimation();
                ThemePreviewActivity.this.scroller.fling((int) ThemePreviewActivity.this.currentScrollOffset, 0, Math.round(-f), Math.round(f2), 0, (int) ThemePreviewActivity.this.maxScrollOffset, 0, Integer.MAX_VALUE);
                ThemePreviewActivity.this.backgroundImage.postInvalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (ThemePreviewActivity.this.scroller != null) {
                    ThemePreviewActivity.this.scroller.abortAnimation();
                }
                ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                themePreviewActivity.currentScrollOffset = Utilities.clamp(themePreviewActivity.currentScrollOffset + f, themePreviewActivity.maxScrollOffset, 0.0f);
                ThemePreviewActivity.this.invalidateBlur();
                ThemePreviewActivity.this.backgroundImage.invalidate();
                return true;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onUp(MotionEvent motionEvent) {
            }
        });
        this.checkingBoostsLevel = false;
        this.checkedBoostsLevel = false;
        this.screenType = i;
        this.nightTheme = z3;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        this.editingTheme = z2;
        if (i == 1) {
            Theme.ThemeAccent accent = themeInfo.getAccent(!z2);
            this.accent = accent;
            if (accent != null) {
                this.useDefaultThemeForButtons = false;
                this.backupAccentColor = accent.accentColor;
                this.backupAccentColor2 = accent.accentColor2;
                this.backupMyMessagesAccentColor = accent.myMessagesAccentColor;
                this.backupMyMessagesGradientAccentColor1 = accent.myMessagesGradientAccentColor1;
                this.backupMyMessagesGradientAccentColor2 = accent.myMessagesGradientAccentColor2;
                this.backupMyMessagesGradientAccentColor3 = accent.myMessagesGradientAccentColor3;
                this.backupMyMessagesAnimated = accent.myMessagesAnimated;
                this.backupBackgroundOverrideColor = accent.backgroundOverrideColor;
                this.backupBackgroundGradientOverrideColor1 = accent.backgroundGradientOverrideColor1;
                this.backupBackgroundGradientOverrideColor2 = accent.backgroundGradientOverrideColor2;
                this.backupBackgroundGradientOverrideColor3 = accent.backgroundGradientOverrideColor3;
                this.backupIntensity = accent.patternIntensity;
                this.backupSlug = accent.patternSlug;
                this.backupBackgroundRotation = accent.backgroundRotation;
            }
        } else {
            if (i == 0) {
                this.useDefaultThemeForButtons = false;
            }
            Theme.ThemeAccent accent2 = themeInfo.getAccent(false);
            this.accent = accent2;
            if (accent2 != null) {
                this.selectedPattern = accent2.pattern;
            }
        }
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.goingToPreviewTheme, new Object[0]);
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.motionAnimation = animatorSet2;
        if (this.isMotion) {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundImage, (Property<BackgroundView, Float>) View.SCALE_X, this.parallaxScale), ObjectAnimator.ofFloat(this.backgroundImage, (Property<BackgroundView, Float>) View.SCALE_Y, this.parallaxScale));
        } else {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundImage, (Property<BackgroundView, Float>) View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.backgroundImage, (Property<BackgroundView, Float>) View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.backgroundImage, (Property<BackgroundView, Float>) View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.backgroundImage, (Property<BackgroundView, Float>) View.TRANSLATION_Y, 0.0f));
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void applyColor(int r8, int r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.applyColor(int, int):void");
    }

    private void applyWallpaperBackground(boolean r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.applyWallpaperBackground(boolean):void");
    }

    public void cancelThemeApply(boolean z) {
        if (this.screenType == 2) {
            if (z) {
                return;
            }
            lambda$onBackPressed$323();
            return;
        }
        Theme.applyPreviousTheme();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        if (this.screenType == 1) {
            if (this.editingTheme) {
                Theme.ThemeAccent themeAccent = this.accent;
                themeAccent.accentColor = this.backupAccentColor;
                themeAccent.accentColor2 = this.backupAccentColor2;
                themeAccent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                themeAccent.myMessagesGradientAccentColor1 = this.backupMyMessagesGradientAccentColor1;
                themeAccent.myMessagesGradientAccentColor2 = this.backupMyMessagesGradientAccentColor2;
                themeAccent.myMessagesGradientAccentColor3 = this.backupMyMessagesGradientAccentColor3;
                themeAccent.myMessagesAnimated = this.backupMyMessagesAnimated;
                themeAccent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                themeAccent.backgroundGradientOverrideColor1 = this.backupBackgroundGradientOverrideColor1;
                themeAccent.backgroundGradientOverrideColor2 = this.backupBackgroundGradientOverrideColor2;
                themeAccent.backgroundGradientOverrideColor3 = this.backupBackgroundGradientOverrideColor3;
                themeAccent.backgroundRotation = this.backupBackgroundRotation;
                themeAccent.patternSlug = this.backupSlug;
                themeAccent.patternIntensity = this.backupIntensity;
            }
            Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
        } else {
            if (this.accent != null) {
                Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
            }
            this.parentLayout.rebuildAllFragmentViews(false, false);
            if (this.deleteOnCancel) {
                Theme.ThemeInfo themeInfo = this.applyingTheme;
                if (themeInfo.pathToFile != null && !Theme.isThemeInstalled(themeInfo)) {
                    new File(this.applyingTheme.pathToFile).delete();
                }
            }
        }
        if (z) {
            return;
        }
        lambda$onBackPressed$323();
    }

    private BitmapDrawable checkBlur(Drawable drawable) {
        ColorFilter colorFilter;
        WeakReference weakReference = this.lastDrawableToBlur;
        if (weakReference != null && weakReference.get() == drawable) {
            return this.blurredDrawable;
        }
        WeakReference weakReference2 = this.lastDrawableToBlur;
        if (weakReference2 != null) {
            weakReference2.clear();
        }
        this.lastDrawableToBlur = null;
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            this.blurredDrawable = null;
            return null;
        }
        this.lastDrawableToBlur = new WeakReference(drawable);
        int intrinsicWidth = (int) ((drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight()) * 24.0f);
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, 24, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, intrinsicWidth, 24);
        if (Build.VERSION.SDK_INT >= 21) {
            colorFilter = drawable.getColorFilter();
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1.3f);
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.94f);
            drawable.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            drawable.draw(new Canvas(createBitmap));
            drawable.setColorFilter(colorFilter);
        } else {
            drawable.draw(new Canvas(createBitmap));
        }
        Utilities.blurBitmap(createBitmap, 3, 1, createBitmap.getWidth(), createBitmap.getHeight(), createBitmap.getRowBytes());
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(), createBitmap);
        this.blurredDrawable = bitmapDrawable;
        bitmapDrawable.setFilterBitmap(true);
        return this.blurredDrawable;
    }

    private void checkBoostsLevel() {
        if (this.dialogId >= 0 || this.checkingBoostsLevel || this.checkedBoostsLevel || this.boostsStatus != null) {
            return;
        }
        this.checkingBoostsLevel = true;
        getMessagesController().getBoostsController().getBoostsStats(this.dialogId, new Consumer() {
            @Override
            public final void accept(Object obj) {
                ThemePreviewActivity.this.lambda$checkBoostsLevel$1((TL_stories.TL_premium_boostsStatus) obj);
            }
        });
    }

    public boolean checkDiscard() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    private void createServiceMessageLocal(TLRPC.WallPaper wallPaper, boolean z) {
        TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
        tL_messageService.random_id = SendMessagesHelper.getInstance(this.currentAccount).getNextRandomId();
        tL_messageService.dialog_id = this.dialogId;
        tL_messageService.unread = true;
        tL_messageService.out = true;
        int newMessageId = getUserConfig().getNewMessageId();
        tL_messageService.id = newMessageId;
        tL_messageService.local_id = newMessageId;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        if (ChatObject.isChannel(chat)) {
            TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
            tL_messageService.from_id = tL_peerChannel;
            tL_peerChannel.channel_id = chat.id;
            TLRPC.TL_peerChannel tL_peerChannel2 = new TLRPC.TL_peerChannel();
            tL_messageService.peer_id = tL_peerChannel2;
            tL_peerChannel2.channel_id = chat.id;
        } else {
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            tL_messageService.from_id = tL_peerUser;
            tL_peerUser.user_id = getUserConfig().getClientUserId();
            TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
            tL_messageService.peer_id = tL_peerUser2;
            tL_peerUser2.user_id = this.dialogId;
        }
        tL_messageService.flags |= 256;
        tL_messageService.date = getConnectionsManager().getCurrentTime();
        TLRPC.TL_messageActionSetChatWallPaper tL_messageActionSetChatWallPaper = new TLRPC.TL_messageActionSetChatWallPaper();
        tL_messageService.action = tL_messageActionSetChatWallPaper;
        tL_messageActionSetChatWallPaper.wallpaper = wallPaper;
        tL_messageActionSetChatWallPaper.for_both = z;
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(new MessageObject(this.currentAccount, tL_messageService, false, false));
        new ArrayList().add(tL_messageService);
        MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(this.dialogId, arrayList, 0);
    }

    public int getButtonsColor(int i) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(i) : getThemedColor(i);
    }

    private int getCustomWallpaperLevelMin() {
        return ChatObject.isChannelAndNotMegaGroup(-this.dialogId, this.currentAccount) ? getMessagesController().channelCustomWallpaperLevelMin : getMessagesController().groupCustomWallpaperLevelMin;
    }

    private boolean hasChanges(int i) {
        int defaultAccentColor;
        long j;
        if (this.editingTheme) {
            return false;
        }
        if (i == 1 || i == 2) {
            long j2 = this.backupBackgroundOverrideColor;
            if (j2 == 0) {
                int defaultAccentColor2 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
                int i2 = (int) this.accent.backgroundOverrideColor;
                if (i2 == 0) {
                    i2 = defaultAccentColor2;
                }
                if (i2 != defaultAccentColor2) {
                    return true;
                }
            } else if (j2 != this.accent.backgroundOverrideColor) {
                return true;
            }
            long j3 = this.backupBackgroundGradientOverrideColor1;
            if (j3 == 0 && this.backupBackgroundGradientOverrideColor2 == 0 && this.backupBackgroundGradientOverrideColor3 == 0) {
                for (int i3 = 0; i3 < 3; i3++) {
                    if (i3 == 0) {
                        defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                        j = this.accent.backgroundGradientOverrideColor1;
                    } else if (i3 == 1) {
                        defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                        j = this.accent.backgroundGradientOverrideColor2;
                    } else {
                        defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                        j = this.accent.backgroundGradientOverrideColor3;
                    }
                    int i4 = (int) j;
                    if (i4 == 0 && j != 0) {
                        i4 = 0;
                    } else if (i4 == 0) {
                        i4 = defaultAccentColor;
                    }
                    if (i4 != defaultAccentColor) {
                        return true;
                    }
                }
            } else {
                Theme.ThemeAccent themeAccent = this.accent;
                if (j3 != themeAccent.backgroundGradientOverrideColor1 || this.backupBackgroundGradientOverrideColor2 != themeAccent.backgroundGradientOverrideColor2 || this.backupBackgroundGradientOverrideColor3 != themeAccent.backgroundGradientOverrideColor3) {
                    return true;
                }
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (i == 1 || i == 3) {
            int i5 = this.backupAccentColor;
            Theme.ThemeAccent themeAccent2 = this.accent;
            if (i5 != themeAccent2.accentColor2) {
                return true;
            }
            int i6 = this.backupMyMessagesAccentColor;
            if (i6 == 0) {
                int i7 = themeAccent2.myMessagesAccentColor;
                if (i7 != 0 && i7 != themeAccent2.accentColor) {
                    return true;
                }
            } else if (i6 != themeAccent2.myMessagesAccentColor) {
                return true;
            }
            int i8 = this.backupMyMessagesGradientAccentColor1;
            if (i8 != 0) {
                if (i8 != themeAccent2.myMessagesGradientAccentColor1) {
                    return true;
                }
            } else if (themeAccent2.myMessagesGradientAccentColor1 != 0) {
                return true;
            }
            int i9 = this.backupMyMessagesGradientAccentColor2;
            if (i9 != 0) {
                if (i9 != themeAccent2.myMessagesGradientAccentColor2) {
                    return true;
                }
            } else if (themeAccent2.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            int i10 = this.backupMyMessagesGradientAccentColor3;
            if (i10 != 0) {
                if (i10 != themeAccent2.myMessagesGradientAccentColor3) {
                    return true;
                }
            } else if (themeAccent2.myMessagesGradientAccentColor3 != 0) {
                return true;
            }
            if (this.backupMyMessagesAnimated != themeAccent2.myMessagesAnimated) {
                return true;
            }
        }
        return false;
    }

    public void invalidateBlur() {
        FrameLayout frameLayout = this.dimmingSliderContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        FrameLayout frameLayout2 = this.backgroundButtonsContainer;
        if (frameLayout2 != null) {
            int childCount = frameLayout2.getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.backgroundButtonsContainer.getChildAt(i).invalidate();
            }
        }
        FrameLayout frameLayout3 = this.messagesButtonsContainer;
        if (frameLayout3 != null) {
            int childCount2 = frameLayout3.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                this.messagesButtonsContainer.getChildAt(i2).invalidate();
            }
        }
        if (this.backgroundCheckBoxView != null) {
            int i3 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                if (i3 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                WallpaperCheckBoxView wallpaperCheckBoxView = wallpaperCheckBoxViewArr[i3];
                if (wallpaperCheckBoxView != null) {
                    wallpaperCheckBoxView.setDimAmount(this.shouldShowBrightnessControll ? this.dimAmount * this.progressToDarkTheme : 0.0f);
                    this.backgroundCheckBoxView[i3].invalidate();
                }
                i3++;
            }
        }
        if (this.listView != null) {
            for (int i4 = 0; i4 < this.listView.getChildCount(); i4++) {
                View childAt = this.listView.getChildAt(i4);
                if (childAt instanceof ChatActionCell) {
                    setVisiblePart((ChatActionCell) childAt);
                    childAt.invalidate();
                }
            }
        }
        if (this.listView2 != null) {
            for (int i5 = 0; i5 < this.listView2.getChildCount(); i5++) {
                View childAt2 = this.listView2.getChildAt(i5);
                if (childAt2 instanceof ChatActionCell) {
                    setVisiblePart((ChatActionCell) childAt2);
                    childAt2.invalidate();
                }
            }
        }
        BlurButton blurButton = this.applyButton1;
        if (blurButton != null) {
            blurButton.invalidate();
        }
        BlurButton blurButton2 = this.applyButton2;
        if (blurButton2 != null) {
            blurButton2.invalidate();
        }
        FrameLayout frameLayout4 = this.bottomOverlayChat;
        if (frameLayout4 != null) {
            frameLayout4.invalidate();
        }
    }

    public void lambda$applyWallpaperBackground$19() {
        presentFragment(StatisticActivity.create(getMessagesController().getChat(Long.valueOf(-this.dialogId))));
    }

    public void lambda$applyWallpaperBackground$20(ChannelBoostsController.CanApplyBoost canApplyBoost) {
        if (getContext() == null) {
            return;
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, getContext(), 23, this.currentAccount, getResourceProvider());
        limitReachedBottomSheet.setCanApplyBoost(canApplyBoost);
        limitReachedBottomSheet.setBoostsStats(this.boostsStatus, true);
        limitReachedBottomSheet.setDialogId(this.dialogId);
        if (!insideBottomSheet()) {
            limitReachedBottomSheet.showStatisticButtonInLink(new Runnable() {
                @Override
                public final void run() {
                    ThemePreviewActivity.this.lambda$applyWallpaperBackground$19();
                }
            });
        }
        showDialog(limitReachedBottomSheet);
    }

    public static void lambda$applyWallpaperBackground$21() {
    }

    public void lambda$checkBoostsLevel$1(TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        this.boostsStatus = tL_premium_boostsStatus;
        this.checkedBoostsLevel = true;
        updateApplyButton1(true);
        this.checkingBoostsLevel = false;
    }

    public void lambda$checkDiscard$25(AlertDialog alertDialog, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    public void lambda$checkDiscard$26(AlertDialog alertDialog, int i) {
        cancelThemeApply(false);
    }

    public void lambda$createView$10(Float f) {
        this.dimAmount = f.floatValue();
        this.backgroundImage.invalidate();
        invalidateBlur();
    }

    public void lambda$createView$11(int r4, org.telegram.ui.Components.WallpaperCheckBoxView r5, android.view.View r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.lambda$createView$11(int, org.telegram.ui.Components.WallpaperCheckBoxView, android.view.View):void");
    }

    public void lambda$createView$12(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.messagesButtonsContainer.getAlpha() == 1.0f && i == 0) {
            wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
            this.accent.myMessagesAnimated = wallpaperCheckBoxView.isChecked();
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
        }
    }

    public void lambda$createView$13(int i, View view) {
        if (this.patternViewAnimation != null) {
            return;
        }
        if (i == 0) {
            this.backgroundRotation = this.previousBackgroundRotation;
            setBackgroundColor(this.previousBackgroundGradientColor3, 3, true, true);
            setBackgroundColor(this.previousBackgroundGradientColor2, 2, true, true);
            setBackgroundColor(this.previousBackgroundGradientColor1, 1, true, true);
            setBackgroundColor(this.previousBackgroundColor, 0, true, true);
        } else {
            TLRPC.TL_wallPaper tL_wallPaper = this.previousSelectedPattern;
            this.selectedPattern = tL_wallPaper;
            if (tL_wallPaper == null) {
                this.backgroundImage.setImageDrawable(null);
            } else {
                BackgroundView backgroundView = this.backgroundImage;
                ImageLocation forDocument = ImageLocation.getForDocument(tL_wallPaper.document);
                String str = this.imageFilter;
                TLRPC.TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                backgroundView.setImage(forDocument, str, null, null, "jpg", tL_wallPaper2.document.size, 1, tL_wallPaper2);
            }
            this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, false);
            float f = this.previousIntensity;
            this.currentIntensity = f;
            this.intensitySeekBar.setProgress(f);
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            updateButtonState(false, true);
            updateSelectedPattern(true);
        }
        if (this.screenType == 2) {
            showPatternsView(i, false, true);
            return;
        }
        if (this.selectedPattern == null) {
            if (this.isMotion) {
                this.isMotion = false;
                this.backgroundCheckBoxView[0].setChecked(false, true);
                animateMotionChange();
            }
            updateMotionButton();
        }
        showPatternsView(0, true, true);
    }

    public void lambda$createView$14(int i, View view) {
        if (this.patternViewAnimation != null) {
            return;
        }
        if (this.screenType == 2) {
            showPatternsView(i, false, true);
        } else {
            showPatternsView(0, true, true);
        }
    }

    public void lambda$createView$15(View view, int i) {
        boolean z = this.selectedPattern != null;
        selectPattern(i);
        if (z == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        int left = view.getLeft();
        int right = view.getRight();
        int dp = AndroidUtilities.dp(52.0f);
        int i2 = left - dp;
        if (i2 < 0) {
            this.patternsListView.smoothScrollBy(i2, 0);
            return;
        }
        int i3 = right + dp;
        if (i3 > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy(i3 - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    public void lambda$createView$16() {
        this.watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
        this.frameLayout.invalidate();
    }

    public void lambda$createView$17(View view) {
        cancelThemeApply(false);
    }

    public void lambda$createView$18(View view) {
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme == null) {
            return;
        }
        int i = previousTheme.prevAccentId;
        Theme.ThemeAccent accent = i >= 0 ? (Theme.ThemeAccent) previousTheme.themeAccentsMap.get(i) : previousTheme.getAccent(false);
        if (this.accent != null) {
            saveAccentWallpaper();
            Theme.saveThemeAccents(this.applyingTheme, true, false, false, false);
            Theme.clearPreviousTheme();
            Theme.applyTheme(this.applyingTheme, this.nightTheme);
            this.parentLayout.rebuildAllFragmentViews(false, false);
        } else {
            this.parentLayout.rebuildAllFragmentViews(false, false);
            File file = new File(this.applyingTheme.pathToFile);
            Theme.ThemeInfo themeInfo = this.applyingTheme;
            Theme.applyThemeFile(file, themeInfo.name, themeInfo.info, false);
            MessagesController.getInstance(this.applyingTheme.account).saveTheme(this.applyingTheme, null, false, false);
            SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            edit.putString("lastDayTheme", this.applyingTheme.getKey());
            edit.commit();
        }
        BaseFragment baseFragment = (BaseFragment) getParentLayout().getFragmentStack().get(Math.max(0, getParentLayout().getFragmentStack().size() - 2));
        lambda$onBackPressed$323();
        if (this.screenType == 0) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didApplyNewTheme, previousTheme, accent, Boolean.valueOf(this.deleteOnCancel));
        }
        Theme.turnOffAutoNight(baseFragment);
    }

    public void lambda$createView$2() {
        if (getParentActivity() == null || getContext() == null) {
            return;
        }
        SharedConfig.increaseDayNightWallpaperSiwtchHint();
        HintView hintView = new HintView(getContext(), 7, true);
        hintView.setAlpha(0.0f);
        hintView.setVisibility(4);
        hintView.setShowingDuration(4000L);
        this.frameLayout.addView(hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 4.0f, 0.0f, 4.0f, 0.0f));
        hintView.setText(LocaleController.getString(this.onSwitchDayNightDelegate.isDark() ? R.string.PreviewWallpaperDay : R.string.PreviewWallpaperNight));
        hintView.setBackgroundColor(-366530760, -1);
        hintView.showForView(this.dayNightItem, true);
        hintView.setExtraTranslationY(-AndroidUtilities.dp(14.0f));
    }

    public static void lambda$createView$3(View view, int i) {
    }

    public void lambda$createView$4(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
            return;
        }
        Drawable drawable = imageReceiver.getDrawable();
        if (!z || drawable == null) {
            return;
        }
        this.themeDelegate.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable), checkBlur(drawable), drawable, Float.valueOf(this.currentIntensity));
        if (!z2 && this.isBlurred && this.blurredBitmap == null) {
            this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
            updateBlurred();
            this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
        }
        invalidateBlur();
    }

    public void lambda$createView$5(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public void lambda$createView$6(View view, int i, float f, float f2) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            selectColorType(chatMessageCell.isInsideBackground(f, f2) ? chatMessageCell.getMessageObject().isOutOwner() ? 3 : 1 : 2);
        }
    }

    public void lambda$createView$7(int i, int i2, float f) {
        if (this.isMotion) {
            this.backgroundImage.getBackground();
            float scaleX = this.motionAnimation != null ? (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f) : 1.0f;
            this.backgroundImage.setTranslationX(i * scaleX);
            this.backgroundImage.setTranslationY(i2 * scaleX);
        }
    }

    public void lambda$createView$8(View view) {
        applyWallpaperBackground(false);
    }

    public void lambda$createView$9(View view) {
        applyWallpaperBackground(true);
    }

    public void lambda$didReceivedNotification$28(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
            if (tL_wallPaper.pattern) {
                this.selectedPattern = tL_wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter = this.patternsAdapter;
                if (patternsAdapter != null) {
                    patternsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void lambda$didReceivedNotification$29(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$didReceivedNotification$28(tLObject);
            }
        });
    }

    public void lambda$didReceivedNotification$30(TLObject tLObject) {
        Theme.ThemeAccent themeAccent;
        TLRPC.TL_wallPaper tL_wallPaper;
        String str;
        if (tLObject instanceof TL_account.TL_wallPapers) {
            TL_account.TL_wallPapers tL_wallPapers = (TL_account.TL_wallPapers) tLObject;
            this.patterns.clear();
            this.patternsDict.clear();
            int size = tL_wallPapers.wallpapers.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                if (tL_wallPapers.wallpapers.get(i) instanceof TLRPC.TL_wallPaper) {
                    TLRPC.TL_wallPaper tL_wallPaper2 = (TLRPC.TL_wallPaper) tL_wallPapers.wallpapers.get(i);
                    if (tL_wallPaper2.pattern) {
                        TLRPC.Document document = tL_wallPaper2.document;
                        if (document != null && !this.patternsDict.containsKey(Long.valueOf(document.id))) {
                            this.patterns.add(tL_wallPaper2);
                            this.patternsDict.put(Long.valueOf(tL_wallPaper2.document.id), tL_wallPaper2);
                        }
                        Theme.ThemeAccent themeAccent2 = this.accent;
                        if (themeAccent2 != null && (str = themeAccent2.patternSlug) != null && str.equals(tL_wallPaper2.slug)) {
                            this.selectedPattern = tL_wallPaper2;
                            setCurrentImage(false);
                            updateButtonState(false, false);
                        } else if (this.accent == null) {
                            TLRPC.TL_wallPaper tL_wallPaper3 = this.selectedPattern;
                            if (tL_wallPaper3 != null) {
                                String str2 = tL_wallPaper3.slug;
                                if (str2 != null) {
                                    if (!str2.equals(tL_wallPaper2.slug)) {
                                    }
                                }
                            }
                        }
                        z = true;
                    }
                }
            }
            if (!z && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tL_wallPapers.wallpapers, 1);
        }
        if (this.selectedPattern != null || (themeAccent = this.accent) == null || TextUtils.isEmpty(themeAccent.patternSlug)) {
            return;
        }
        TL_account.getWallPaper getwallpaper = new TL_account.getWallPaper();
        TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
        tL_inputWallPaperSlug.slug = this.accent.patternSlug;
        getwallpaper.wallpaper = tL_inputWallPaperSlug;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(getwallpaper, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                ThemePreviewActivity.this.lambda$didReceivedNotification$29(tLObject2, tL_error);
            }
        }), this.classGuid);
    }

    public void lambda$didReceivedNotification$31(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$didReceivedNotification$30(tLObject);
            }
        });
    }

    public void lambda$getThemeDescriptionsInternal$33() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        int i = 0;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            this.dropDownContainer.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false);
        }
        Drawable drawable = this.sheetDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundWhite), PorterDuff.Mode.MULTIPLY));
        }
        FrameLayout frameLayout = this.bottomOverlayChat;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        if (this.onSwitchDayNightDelegate != null) {
            INavigationLayout iNavigationLayout = this.parentLayout;
            if (iNavigationLayout == null || iNavigationLayout.getBottomSheet() == null) {
                setNavigationBarColor(getThemedColor(Theme.key_dialogBackground));
            } else {
                this.parentLayout.getBottomSheet().fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
                if (this.screenType == 2 && this.dialogId != 0) {
                    this.parentLayout.getBottomSheet().setOverlayNavBarColor(-16777216);
                }
            }
        }
        if (this.backgroundCheckBoxView != null) {
            int i2 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                if (i2 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                WallpaperCheckBoxView wallpaperCheckBoxView = wallpaperCheckBoxViewArr[i2];
                if (wallpaperCheckBoxView != null) {
                    wallpaperCheckBoxView.invalidate();
                }
                i2++;
            }
        }
        if (this.messagesCheckBoxView != null) {
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr2 = this.messagesCheckBoxView;
                if (i >= wallpaperCheckBoxViewArr2.length) {
                    break;
                }
                WallpaperCheckBoxView wallpaperCheckBoxView2 = wallpaperCheckBoxViewArr2[i];
                if (wallpaperCheckBoxView2 != null) {
                    wallpaperCheckBoxView2.invalidate();
                }
                i++;
            }
        }
        TextView textView = this.patternTitleView;
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.invalidate();
        }
    }

    public void lambda$new$0() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
        this.lastPickedColorNum = -1;
    }

    public void lambda$selectColorType$22(AlertDialog alertDialog, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0L;
            themeAccent.backgroundGradientOverrideColor1 = 0L;
            themeAccent.backgroundGradientOverrideColor2 = 0L;
            themeAccent.backgroundGradientOverrideColor3 = 0L;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    public void lambda$selectColorType$23(AlertDialog alertDialog, int i) {
        if (Theme.isCustomWallpaperColor()) {
            Theme.ThemeAccent themeAccent = this.accent;
            Theme.OverrideWallpaperInfo overrideWallpaperInfo = themeAccent.overrideWallpaper;
            themeAccent.backgroundOverrideColor = overrideWallpaperInfo.color;
            themeAccent.backgroundGradientOverrideColor1 = overrideWallpaperInfo.gradientColor1;
            themeAccent.backgroundGradientOverrideColor2 = overrideWallpaperInfo.gradientColor2;
            themeAccent.backgroundGradientOverrideColor3 = overrideWallpaperInfo.gradientColor3;
            themeAccent.backgroundRotation = overrideWallpaperInfo.rotation;
            String str = overrideWallpaperInfo.slug;
            themeAccent.patternSlug = str;
            float f = overrideWallpaperInfo.intensity;
            themeAccent.patternIntensity = f;
            this.currentIntensity = f;
            if (str != null && !"c".equals(str)) {
                int size = this.patterns.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(i2);
                    if (tL_wallPaper.pattern && this.accent.patternSlug.equals(tL_wallPaper.slug)) {
                        this.selectedPattern = tL_wallPaper;
                        break;
                    }
                    i2++;
                }
            } else {
                this.selectedPattern = null;
            }
            this.removeBackgroundOverride = true;
            this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        Drawable background = this.backgroundImage.getBackground();
        if (background instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) background;
            motionBackgroundDrawable.setPatternBitmap(100, null);
            if (Theme.getActiveTheme().isDark()) {
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(motionBackgroundDrawable.getBitmap());
                }
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
            } else {
                float f2 = this.currentIntensity;
                if (f2 < 0.0f) {
                    this.currentIntensity = -f2;
                }
            }
        }
        SeekBarView seekBarView2 = this.intensitySeekBar;
        if (seekBarView2 != null) {
            seekBarView2.setProgress(this.currentIntensity);
        }
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    public void lambda$selectColorType$24(AlertDialog alertDialog, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0L;
            themeAccent.backgroundGradientOverrideColor1 = 0L;
            themeAccent.backgroundGradientOverrideColor2 = 0L;
            themeAccent.backgroundGradientOverrideColor3 = 0L;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    public void lambda$showAnimationHint$32(SharedPreferences sharedPreferences) {
        if (this.colorType != 3) {
            return;
        }
        sharedPreferences.edit().putBoolean("bganimationhint", true).commit();
        this.animationHint.showForView(this.messagesCheckBoxView[0], true);
    }

    public static boolean lambda$toggleTheme$34(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$toggleTheme$35(ValueAnimator valueAnimator) {
        this.progressToDarkTheme = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.backgroundImage.invalidate();
        this.bottomOverlayChat.invalidate();
        this.dimmingSlider.setAlpha(this.progressToDarkTheme);
        this.dimmingSliderContainer.invalidate();
        invalidateBlur();
    }

    public void lambda$toggleTheme$36() {
        this.onSwitchDayNightDelegate.switchDayNight(false);
        setForceDark(this.themeDelegate.isDark(), true);
        setCurrentImage(false);
        invalidateBlur();
        updateBlurred();
        if (this.themeDescriptions != null) {
            for (int i = 0; i < this.themeDescriptions.size(); i++) {
                ((ThemeDescription) this.themeDescriptions.get(i)).setColor(getThemedColor(((ThemeDescription) this.themeDescriptions.get(i)).getCurrentKey()), false, false);
            }
        }
        if (this.shouldShowBrightnessControll) {
            DayNightSwitchDelegate dayNightSwitchDelegate = this.onSwitchDayNightDelegate;
            if (dayNightSwitchDelegate == null || !dayNightSwitchDelegate.isDark()) {
                this.dimmingSlider.animateValueTo(0.0f);
            } else {
                this.dimmingSlider.setVisibility(0);
                this.dimmingSlider.animateValueTo(this.dimAmount);
            }
            ValueAnimator valueAnimator = this.changeDayNightViewAnimator2;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.changeDayNightViewAnimator2.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToDarkTheme, this.onSwitchDayNightDelegate.isDark() ? 1.0f : 0.0f);
            this.changeDayNightViewAnimator2 = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ThemePreviewActivity.this.lambda$toggleTheme$35(valueAnimator2);
                }
            });
            this.changeDayNightViewAnimator2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (ThemePreviewActivity.this.onSwitchDayNightDelegate.isDark()) {
                        return;
                    }
                    ThemePreviewActivity.this.dimmingSlider.setVisibility(8);
                }
            });
            this.changeDayNightViewAnimator2.setDuration(250L);
            this.changeDayNightViewAnimator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.changeDayNightViewAnimator2.start();
        }
    }

    public void onColorsRotate() {
        if (this.screenType == 2) {
            int i = this.backgroundRotation + 45;
            while (true) {
                this.backgroundRotation = i;
                int i2 = this.backgroundRotation;
                if (i2 < 360) {
                    setBackgroundColor(this.backgroundColor, 0, true, true);
                    return;
                }
                i = i2 - 360;
            }
        } else {
            Theme.ThemeAccent themeAccent = this.accent;
            if (themeAccent == null) {
                return;
            }
            int i3 = themeAccent.backgroundRotation + 45;
            while (true) {
                themeAccent.backgroundRotation = i3;
                themeAccent = this.accent;
                int i4 = themeAccent.backgroundRotation;
                if (i4 < 360) {
                    Theme.refreshThemeColors();
                    return;
                }
                i3 = i4 - 360;
            }
        }
    }

    public void saveAccentWallpaper() {
        FileOutputStream fileOutputStream;
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent == null || TextUtils.isEmpty(themeAccent.patternSlug)) {
            return;
        }
        try {
            File pathToWallpaper = this.accent.getPathToWallpaper();
            Drawable background = this.backgroundImage.getBackground();
            Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
            if (background instanceof MotionBackgroundDrawable) {
                fileOutputStream = new FileOutputStream(pathToWallpaper);
                bitmap.compress(Bitmap.CompressFormat.PNG, 87, fileOutputStream);
            } else {
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                background.draw(canvas);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (this.currentIntensity * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                fileOutputStream = new FileOutputStream(pathToWallpaper);
                createBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
            }
            fileOutputStream.close();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void scheduleApplyColor(int i, int i2, boolean z) {
        if (i2 != -1) {
            int i3 = this.lastPickedColorNum;
            if (i3 != -1 && i3 != i2) {
                this.applyColorAction.run();
            }
            this.lastPickedColor = i;
            this.lastPickedColorNum = i2;
            if (z) {
                this.applyColorAction.run();
                return;
            } else {
                if (this.applyColorScheduled) {
                    return;
                }
                this.applyColorScheduled = true;
                this.fragmentView.postDelayed(this.applyColorAction, 16L);
                return;
            }
        }
        int i4 = this.colorType;
        if (i4 == 1 || i4 == 2) {
            long j = this.backupBackgroundOverrideColor;
            if (j != 0) {
                this.accent.backgroundOverrideColor = j;
            } else {
                this.accent.backgroundOverrideColor = 0L;
            }
            long j2 = this.backupBackgroundGradientOverrideColor1;
            if (j2 != 0) {
                this.accent.backgroundGradientOverrideColor1 = j2;
            } else {
                this.accent.backgroundGradientOverrideColor1 = 0L;
            }
            long j3 = this.backupBackgroundGradientOverrideColor2;
            if (j3 != 0) {
                this.accent.backgroundGradientOverrideColor2 = j3;
            } else {
                this.accent.backgroundGradientOverrideColor2 = 0L;
            }
            long j4 = this.backupBackgroundGradientOverrideColor3;
            if (j4 != 0) {
                this.accent.backgroundGradientOverrideColor3 = j4;
            } else {
                this.accent.backgroundGradientOverrideColor3 = 0L;
            }
            this.accent.backgroundRotation = this.backupBackgroundRotation;
            if (i4 == 2) {
                int defaultAccentColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
                int defaultAccentColor2 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to1);
                int defaultAccentColor3 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to2);
                int defaultAccentColor4 = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to3);
                Theme.ThemeAccent themeAccent = this.accent;
                int i5 = (int) themeAccent.backgroundGradientOverrideColor1;
                int i6 = (int) themeAccent.backgroundGradientOverrideColor2;
                int i7 = (int) themeAccent.backgroundGradientOverrideColor3;
                int i8 = (int) themeAccent.backgroundOverrideColor;
                ColorPicker colorPicker = this.colorPicker;
                if (i7 != 0) {
                    defaultAccentColor4 = i7;
                }
                colorPicker.setColor(defaultAccentColor4, 3);
                ColorPicker colorPicker2 = this.colorPicker;
                if (i6 != 0) {
                    defaultAccentColor3 = i6;
                }
                colorPicker2.setColor(defaultAccentColor3, 2);
                ColorPicker colorPicker3 = this.colorPicker;
                if (i5 != 0) {
                    defaultAccentColor2 = i5;
                }
                colorPicker3.setColor(defaultAccentColor2, 1);
                ColorPicker colorPicker4 = this.colorPicker;
                if (i8 != 0) {
                    defaultAccentColor = i8;
                }
                colorPicker4.setColor(defaultAccentColor, 0);
            }
        }
        int i9 = this.colorType;
        if (i9 == 1 || i9 == 3) {
            int i10 = this.backupMyMessagesAccentColor;
            if (i10 != 0) {
                this.accent.myMessagesAccentColor = i10;
            } else {
                this.accent.myMessagesAccentColor = 0;
            }
            int i11 = this.backupMyMessagesGradientAccentColor1;
            if (i11 != 0) {
                this.accent.myMessagesGradientAccentColor1 = i11;
            } else {
                this.accent.myMessagesGradientAccentColor1 = 0;
            }
            int i12 = this.backupMyMessagesGradientAccentColor2;
            if (i12 != 0) {
                this.accent.myMessagesGradientAccentColor2 = i12;
            } else {
                this.accent.myMessagesGradientAccentColor2 = 0;
            }
            int i13 = this.backupMyMessagesGradientAccentColor3;
            if (i13 != 0) {
                this.accent.myMessagesGradientAccentColor3 = i13;
            } else {
                this.accent.myMessagesGradientAccentColor3 = 0;
            }
            if (i9 == 3) {
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                ColorPicker colorPicker5 = this.colorPicker;
                Theme.ThemeAccent themeAccent2 = this.accent;
                int i14 = themeAccent2.myMessagesAccentColor;
                if (i14 == 0) {
                    i14 = themeAccent2.accentColor;
                }
                colorPicker5.setColor(i14, 0);
            }
        }
        Theme.refreshThemeColors();
        this.listView2.invalidateViews();
    }

    public void selectColorType(int i) {
        selectColorType(i, true);
    }

    private void selectColorType(int i, boolean z) {
        ColorPicker colorPicker;
        float f;
        String string;
        AlertDialog.OnButtonClickListener onButtonClickListener;
        if (getParentActivity() == null || this.colorType == i || this.patternViewAnimation != null || this.accent == null) {
            return;
        }
        if (z && i == 2 && (Theme.hasCustomWallpaper() || this.accent.backgroundOverrideColor == 4294967296L)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.ChangeChatBackground));
            if (!Theme.hasCustomWallpaper() || Theme.isCustomWallpaperColor()) {
                builder.setMessage(LocaleController.getString(R.string.ChangeColorToColor));
                builder.setPositiveButton(LocaleController.getString(R.string.Reset), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i2) {
                        ThemePreviewActivity.this.lambda$selectColorType$22(alertDialog, i2);
                    }
                });
                string = LocaleController.getString(R.string.Continue);
                onButtonClickListener = new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i2) {
                        ThemePreviewActivity.this.lambda$selectColorType$23(alertDialog, i2);
                    }
                };
            } else {
                builder.setMessage(LocaleController.getString(R.string.ChangeWallpaperToColor));
                builder.setPositiveButton(LocaleController.getString(R.string.Change), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i2) {
                        ThemePreviewActivity.this.lambda$selectColorType$24(alertDialog, i2);
                    }
                });
                string = LocaleController.getString(R.string.Cancel);
                onButtonClickListener = null;
            }
            builder.setNegativeButton(string, onButtonClickListener);
            showDialog(builder.create());
            return;
        }
        int i2 = this.colorType;
        this.colorType = i;
        if (i == 1) {
            this.dropDown.setText(LocaleController.getString(R.string.ColorPickerMainColor));
            this.colorPicker.setType(1, hasChanges(1), 2, this.accent.accentColor2 != 0 ? 2 : 1, false, 0, false);
            this.colorPicker.setColor(this.accent.accentColor, 0);
            int i3 = this.accent.accentColor2;
            if (i3 != 0) {
                this.colorPicker.setColor(i3, 1);
            }
            if (i2 == 2 || (i2 == 3 && this.accent.myMessagesGradientAccentColor2 != 0)) {
                this.messagesAdapter.notifyItemRemoved(0);
            }
        } else if (i == 2) {
            this.dropDown.setText(LocaleController.getString(R.string.ColorPickerBackground));
            int themedColor = getThemedColor(Theme.key_chat_wallpaper);
            int i4 = Theme.key_chat_wallpaper_gradient_to1;
            int themedColor2 = Theme.hasThemeKey(i4) ? getThemedColor(i4) : 0;
            int i5 = Theme.key_chat_wallpaper_gradient_to2;
            int themedColor3 = Theme.hasThemeKey(i5) ? getThemedColor(i5) : 0;
            int i6 = Theme.key_chat_wallpaper_gradient_to3;
            int themedColor4 = Theme.hasThemeKey(i6) ? getThemedColor(i6) : 0;
            Theme.ThemeAccent themeAccent = this.accent;
            long j = themeAccent.backgroundGradientOverrideColor1;
            int i7 = (int) j;
            if (i7 == 0 && j != 0) {
                themedColor2 = 0;
            }
            long j2 = themeAccent.backgroundGradientOverrideColor2;
            int i8 = (int) j2;
            if (i8 == 0 && j2 != 0) {
                themedColor3 = 0;
            }
            long j3 = themeAccent.backgroundGradientOverrideColor3;
            int i9 = (int) j3;
            if (i9 == 0 && j3 != 0) {
                themedColor4 = 0;
            }
            int i10 = (int) themeAccent.backgroundOverrideColor;
            this.colorPicker.setType(2, hasChanges(2), 4, (i7 == 0 && themedColor2 == 0) ? 1 : (i9 == 0 && themedColor4 == 0) ? (i8 == 0 && themedColor3 == 0) ? 2 : 3 : 4, false, this.accent.backgroundRotation, false);
            ColorPicker colorPicker2 = this.colorPicker;
            if (i9 == 0) {
                i9 = themedColor4;
            }
            colorPicker2.setColor(i9, 3);
            ColorPicker colorPicker3 = this.colorPicker;
            if (i8 == 0) {
                i8 = themedColor3;
            }
            colorPicker3.setColor(i8, 2);
            ColorPicker colorPicker4 = this.colorPicker;
            if (i7 == 0) {
                i7 = themedColor2;
            }
            colorPicker4.setColor(i7, 1);
            ColorPicker colorPicker5 = this.colorPicker;
            if (i10 != 0) {
                themedColor = i10;
            }
            colorPicker5.setColor(themedColor, 0);
            if (i2 == 1 || this.accent.myMessagesGradientAccentColor2 == 0) {
                this.messagesAdapter.notifyItemInserted(0);
            } else {
                this.messagesAdapter.notifyItemChanged(0);
            }
            this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
        } else if (i == 3) {
            this.dropDown.setText(LocaleController.getString(R.string.ColorPickerMyMessages));
            Theme.ThemeAccent themeAccent2 = this.accent;
            this.colorPicker.setType(2, hasChanges(3), 4, themeAccent2.myMessagesGradientAccentColor1 != 0 ? themeAccent2.myMessagesGradientAccentColor3 != 0 ? 4 : themeAccent2.myMessagesGradientAccentColor2 != 0 ? 3 : 2 : 1, true, 0, false);
            this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
            this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
            this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
            ColorPicker colorPicker6 = this.colorPicker;
            Theme.ThemeAccent themeAccent3 = this.accent;
            int i11 = themeAccent3.myMessagesAccentColor;
            if (i11 == 0) {
                i11 = themeAccent3.accentColor;
            }
            colorPicker6.setColor(i11, 0);
            this.messagesCheckBoxView[1].setColor(0, this.accent.myMessagesAccentColor);
            this.messagesCheckBoxView[1].setColor(1, this.accent.myMessagesGradientAccentColor1);
            this.messagesCheckBoxView[1].setColor(2, this.accent.myMessagesGradientAccentColor2);
            this.messagesCheckBoxView[1].setColor(3, this.accent.myMessagesGradientAccentColor3);
            if (this.accent.myMessagesGradientAccentColor2 != 0) {
                MessagesAdapter messagesAdapter = this.messagesAdapter;
                if (i2 == 1) {
                    messagesAdapter.notifyItemInserted(0);
                } else {
                    messagesAdapter.notifyItemChanged(0);
                }
            } else if (i2 == 2) {
                this.messagesAdapter.notifyItemRemoved(0);
            }
            this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
            showAnimationHint();
        }
        if (i == 1 || i == 3) {
            if (i2 == 2 && this.patternLayout[1].getVisibility() == 0) {
                showPatternsView(0, true, true);
            }
            if (i == 1) {
                if (this.applyingTheme.isDark()) {
                    this.colorPicker.setMinBrightness(0.2f);
                    return;
                }
                this.colorPicker.setMinBrightness(0.05f);
                colorPicker = this.colorPicker;
                f = 0.8f;
                colorPicker.setMaxBrightness(f);
            }
        }
        this.colorPicker.setMinBrightness(0.0f);
        colorPicker = this.colorPicker;
        f = 1.0f;
        colorPicker.setMaxBrightness(f);
    }

    private void selectPattern(int i) {
        TLRPC.TL_wallPaper tL_wallPaper = (i < 0 || i >= this.patterns.size()) ? this.lastSelectedPattern : (TLRPC.TL_wallPaper) this.patterns.get(i);
        if (tL_wallPaper == null) {
            return;
        }
        ValueAnimator valueAnimator = this.valueAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.valueAnimator.cancel();
        }
        BackgroundView[] backgroundViewArr = this.backgroundImages;
        BackgroundView backgroundView = backgroundViewArr[0];
        BackgroundView backgroundView2 = backgroundViewArr[1];
        backgroundViewArr[0] = backgroundView2;
        backgroundViewArr[1] = backgroundView;
        this.page2.removeView(backgroundView2);
        this.page2.addView(this.backgroundImages[0], this.page2.indexOfChild(this.backgroundImages[1]) + 1);
        BackgroundView[] backgroundViewArr2 = this.backgroundImages;
        BackgroundView backgroundView3 = backgroundViewArr2[0];
        this.backgroundImage = backgroundView3;
        backgroundView3.setBackground(backgroundViewArr2[1].getBackground());
        updateIntensity();
        this.backgroundImages[1].setVisibility(0);
        this.backgroundImages[1].setAlpha(1.0f);
        this.backgroundImage.setVisibility(0);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.valueAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ThemePreviewActivity.this.backgroundImage.setAlpha(((Float) valueAnimator2.getAnimatedValue()).floatValue());
            }
        });
        this.valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                ThemePreviewActivity.this.backgroundImage.invalidate();
                ThemePreviewActivity.this.backgroundImages[1].setVisibility(8);
                ThemePreviewActivity.this.valueAnimator = null;
            }
        });
        this.valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.valueAnimator.setDuration(300L);
        this.valueAnimator.start();
        this.backgroundImage.getImageReceiver().setCrossfadeDuration(300);
        this.backgroundImage.getImageReceiver().setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, null, null, null, tL_wallPaper.document.size, "jpg", tL_wallPaper, 1);
        this.backgroundImage.onNewImageSet();
        this.selectedPattern = tL_wallPaper;
        this.isMotion = this.backgroundCheckBoxView[2].isChecked();
        updateButtonState(false, true);
    }

    public void setBackgroundColor(int i, int i2, boolean z, boolean z2) {
        int i3;
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (i2 == 0) {
            this.backgroundColor = i;
        } else if (i2 == 1) {
            this.backgroundGradientColor1 = i;
        } else if (i2 == 2) {
            this.backgroundGradientColor2 = i;
        } else if (i2 == 3) {
            this.backgroundGradientColor3 = i;
        }
        updatePlayAnimationView(z2);
        if (this.backgroundCheckBoxView != null) {
            int i4 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                if (i4 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                WallpaperCheckBoxView wallpaperCheckBoxView = wallpaperCheckBoxViewArr[i4];
                if (wallpaperCheckBoxView != null) {
                    wallpaperCheckBoxView.setColor(i2, i);
                }
                i4++;
            }
        }
        if (this.backgroundGradientColor2 != 0) {
            if (this.intensitySeekBar != null && Theme.getActiveTheme().isDark()) {
                this.intensitySeekBar.setTwoSided(true);
            }
            Drawable background = this.backgroundImage.getBackground();
            if (background instanceof MotionBackgroundDrawable) {
                motionBackgroundDrawable = (MotionBackgroundDrawable) background;
            } else {
                motionBackgroundDrawable = new MotionBackgroundDrawable();
                motionBackgroundDrawable.setParentView(this.backgroundImage);
                if (this.rotatePreview) {
                    motionBackgroundDrawable.rotatePreview(false);
                }
            }
            motionBackgroundDrawable.setColors(this.backgroundColor, this.backgroundGradientColor1, this.backgroundGradientColor2, this.backgroundGradientColor3);
            this.backgroundImage.setBackground(motionBackgroundDrawable);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
            this.checkColor = 754974720;
        } else {
            if (this.backgroundGradientColor1 != 0) {
                this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1}));
                i3 = AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor1);
            } else {
                this.backgroundImage.setBackgroundColor(this.backgroundColor);
                i3 = this.backgroundColor;
            }
            int patternColor = AndroidUtilities.getPatternColor(i3);
            this.checkColor = patternColor;
            this.patternColor = patternColor;
        }
        int i5 = Theme.key_chat_serviceBackground;
        if (!Theme.hasThemeKey(i5) || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
            ThemeDelegate themeDelegate = this.themeDelegate;
            int i6 = this.checkColor;
            themeDelegate.applyChatServiceMessageColor(new int[]{i6, i6, i6, i6}, this.backgroundImage.getBackground(), this.backgroundImage.getBackground(), Float.valueOf(this.currentIntensity));
        } else if (Theme.getCachedWallpaperNonBlocking() instanceof MotionBackgroundDrawable) {
            int themedColor = getThemedColor(i5);
            this.themeDelegate.applyChatServiceMessageColor(new int[]{themedColor, themedColor, themedColor, themedColor}, this.backgroundImage.getBackground(), this.backgroundImage.getBackground(), Float.valueOf(this.currentIntensity));
        }
        ImageView imageView = this.backgroundPlayAnimationImageView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.messagesPlayAnimationImageView;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_serviceText), PorterDuff.Mode.MULTIPLY));
        }
        BackgroundView backgroundView = this.backgroundImage;
        if (backgroundView != null) {
            backgroundView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
            this.backgroundImage.invalidate();
            if (Theme.getActiveTheme().isDark() && (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
                }
            } else {
                this.backgroundImage.getImageReceiver().setGradientBitmap(null);
                SeekBarView seekBarView2 = this.intensitySeekBar;
                if (seekBarView2 != null) {
                    seekBarView2.setTwoSided(false);
                }
            }
            SeekBarView seekBarView3 = this.intensitySeekBar;
            if (seekBarView3 != null) {
                seekBarView3.setProgress(this.currentIntensity);
            }
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout = this.backgroundButtonsContainer;
        if (frameLayout != null) {
            int childCount = frameLayout.getChildCount();
            for (int i7 = 0; i7 < childCount; i7++) {
                this.backgroundButtonsContainer.getChildAt(i7).invalidate();
            }
        }
        FrameLayout frameLayout2 = this.messagesButtonsContainer;
        if (frameLayout2 != null) {
            int childCount2 = frameLayout2.getChildCount();
            for (int i8 = 0; i8 < childCount2; i8++) {
                this.messagesButtonsContainer.getChildAt(i8).invalidate();
            }
        }
    }

    public void setCurrentImage(boolean r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.setCurrentImage(boolean):void");
    }

    private void setCurrentServerWallpaper(MessageObject messageObject) {
        this.serverWallpaper = messageObject;
    }

    private void setVisiblePart(ChatActionCell chatActionCell) {
        float f;
        if (this.backgroundImage == null) {
            return;
        }
        if (this.themeDelegate.serviceBitmap != null) {
            float width = this.themeDelegate.serviceBitmap.getWidth();
            f = ((this.backgroundImage.getMeasuredWidth() - (width * Math.max(this.backgroundImage.getMeasuredWidth() / width, this.backgroundImage.getMeasuredHeight() / this.themeDelegate.serviceBitmap.getHeight()))) / 2.0f) + this.currentScrollOffset + 0.0f;
        } else {
            f = this.currentScrollOffset + 0.0f;
        }
        chatActionCell.setVisiblePart(chatActionCell.getY() - ((-this.backgroundImage.ty) + 0.0f), f, this.backgroundImage.getMeasuredHeight(), this.shouldShowBrightnessControll ? this.dimAmount * this.progressToDarkTheme : 0.0f);
    }

    private void showAnimationHint() {
        if (this.page2 == null || this.messagesCheckBoxView == null || this.accent.myMessagesGradientAccentColor2 == 0) {
            return;
        }
        final SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (globalMainSettings.getBoolean("bganimationhint", false)) {
            return;
        }
        if (this.animationHint == null) {
            HintView hintView = new HintView(getParentActivity(), 8);
            this.animationHint = hintView;
            hintView.setShowingDuration(5000L);
            this.animationHint.setAlpha(0.0f);
            this.animationHint.setVisibility(4);
            this.animationHint.setText(LocaleController.getString(R.string.BackgroundAnimateInfo));
            this.animationHint.setExtraTranslationY(AndroidUtilities.dp(6.0f));
            this.frameLayout.addView(this.animationHint, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$showAnimationHint$32(globalMainSettings);
            }
        }, 500L);
    }

    public static void showFor(final ChatActivity chatActivity, MessageObject messageObject) {
        TLRPC.WallPaper wallPaper;
        TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
        if (messageAction instanceof TLRPC.TL_messageActionSetChatWallPaper) {
            TLRPC.WallPaper wallPaper2 = ((TLRPC.TL_messageActionSetChatWallPaper) messageAction).wallpaper;
            if (wallPaper2.pattern || wallPaper2.document == null) {
                String str = wallPaper2.slug;
                TLRPC.WallPaperSettings wallPaperSettings = wallPaper2.settings;
                WallpapersListActivity.ColorWallpaper colorWallpaper = new WallpapersListActivity.ColorWallpaper(str, wallPaperSettings.background_color, wallPaperSettings.second_background_color, wallPaperSettings.third_background_color, wallPaperSettings.fourth_background_color, AndroidUtilities.getWallpaperRotation(wallPaperSettings.rotation, false), r3.intensity / 100.0f, wallPaper2.settings.motion, null);
                if (wallPaper2 instanceof TLRPC.TL_wallPaper) {
                    colorWallpaper.pattern = (TLRPC.TL_wallPaper) wallPaper2;
                }
                wallPaper = colorWallpaper;
            } else {
                wallPaper = wallPaper2;
            }
            final boolean isDark = Theme.getActiveTheme().isDark();
            ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(wallPaper, null, true, false) {
                @Override
                public void onFragmentClosed() {
                    super.onFragmentClosed();
                    ChatActivity.ThemeDelegate themeDelegate = chatActivity.themeDelegate;
                    themeDelegate.setCurrentTheme(themeDelegate.getCurrentTheme(), chatActivity.themeDelegate.getCurrentWallpaper(), false, Boolean.valueOf(isDark));
                }
            };
            TLRPC.WallPaperSettings wallPaperSettings2 = wallPaper2.settings;
            if (wallPaperSettings2 != null) {
                themePreviewActivity.setInitialModes(wallPaperSettings2.blur, wallPaperSettings2.motion, wallPaperSettings2.intensity / 100.0f);
            }
            themePreviewActivity.setCurrentServerWallpaper(messageObject);
            themePreviewActivity.setDialogId(messageObject.getDialogId());
            themePreviewActivity.setResourceProvider(chatActivity.themeDelegate);
            themePreviewActivity.setOnSwitchDayNightDelegate(new DayNightSwitchDelegate(isDark, chatActivity) {
                boolean forceDark;
                final ChatActivity val$chatActivity;
                final boolean val$initialIsDark;

                {
                    this.val$initialIsDark = isDark;
                    this.val$chatActivity = chatActivity;
                    this.forceDark = isDark;
                }

                @Override
                public boolean isDark() {
                    return this.forceDark;
                }

                @Override
                public boolean supportsAnimation() {
                    return true;
                }

                @Override
                public void switchDayNight(boolean z) {
                    this.forceDark = !this.forceDark;
                    ChatActivity.ThemeDelegate themeDelegate = this.val$chatActivity.themeDelegate;
                    themeDelegate.setCurrentTheme(themeDelegate.getCurrentTheme(), this.val$chatActivity.themeDelegate.getCurrentWallpaper(), z, Boolean.valueOf(this.forceDark));
                }
            });
            chatActivity.presentFragment(themePreviewActivity);
        }
    }

    public void showPatternsView(final int r19, final boolean r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.showPatternsView(int, boolean, boolean):void");
    }

    private void updateApplyButton1(boolean z) {
        BlurButton blurButton;
        int i;
        String formatString;
        long j = this.dialogId;
        if (j > 0) {
            blurButton = this.applyButton1;
            i = R.string.ApplyWallpaperForMe;
        } else {
            if (j < 0) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
                if (chat == null) {
                    blurButton = this.applyButton1;
                    formatString = LocaleController.formatString(R.string.ApplyWallpaperForChannel, LocaleController.getString(R.string.AccDescrChannel).toLowerCase());
                    blurButton.setText(formatString);
                }
                this.applyButton1.setText(LocaleController.formatString(R.string.ApplyWallpaperForChannel, chat.title));
                TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus = this.boostsStatus;
                if (tL_premium_boostsStatus == null || tL_premium_boostsStatus.level >= getCustomWallpaperLevelMin()) {
                    if (this.boostsStatus == null) {
                        checkBoostsLevel();
                        return;
                    } else {
                        this.applyButton1.setSubText(null, z);
                        return;
                    }
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("l");
                if (this.lockSpan == null) {
                    ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_switch_lock);
                    this.lockSpan = coloredImageSpan;
                    coloredImageSpan.setTopOffset(1);
                }
                spannableStringBuilder.setSpan(this.lockSpan, 0, 1, 33);
                spannableStringBuilder.append((CharSequence) " ").append((CharSequence) LocaleController.formatPluralString("ReactionLevelRequiredBtn", getCustomWallpaperLevelMin(), new Object[0]));
                this.applyButton1.setSubText(spannableStringBuilder, z);
                return;
            }
            blurButton = this.applyButton1;
            i = R.string.ApplyWallpaper;
        }
        formatString = LocaleController.getString(i);
        blurButton.setText(formatString);
    }

    public void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.originalBitmap = bitmap;
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.originalBitmap = imageReceiver.getBitmap();
                    bitmap = imageReceiver.getBitmap();
                }
            }
            this.blurredBitmap = Utilities.blurWallpaper(bitmap);
        }
        if (!this.isBlurred) {
            setCurrentImage(false);
            return;
        }
        Bitmap bitmap2 = this.blurredBitmap;
        if (bitmap2 != null) {
            this.backgroundImage.setImageBitmap(bitmap2);
        }
    }

    private void updateButtonState(boolean r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.updateButtonState(boolean, boolean):void");
    }

    public void updateIntensity() {
        this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
        this.backgroundImage.invalidate();
        this.patternsListView.invalidateViews();
        if (this.currentIntensity >= 0.0f) {
            this.backgroundImage.getImageReceiver().setGradientBitmap(null);
        } else {
            if (Build.VERSION.SDK_INT >= 29) {
                this.backgroundImage.getImageReceiver().setBlendMode(null);
            }
            if (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable) {
                this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
            }
        }
        ThemeDelegate themeDelegate = this.themeDelegate;
        int i = this.checkColor;
        themeDelegate.applyChatServiceMessageColor(new int[]{i, i, i, i}, this.backgroundImage.getBackground(), this.backgroundImage.getBackground(), Float.valueOf(this.currentIntensity));
        invalidateBlur();
    }

    private void updateMotionButton() {
        int i = this.screenType;
        if (i == 1 || i == 2) {
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.backgroundCheckBoxView[2].setChecked(false, true);
            }
            this.backgroundCheckBoxView[this.selectedPattern != null ? (char) 2 : (char) 0].setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
            Property property = View.ALPHA;
            animatorSet.playTogether(ObjectAnimator.ofFloat(wallpaperCheckBoxView, (Property<WallpaperCheckBoxView, Float>) property, this.selectedPattern != null ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.backgroundCheckBoxView[0], (Property<WallpaperCheckBoxView, Float>) property, this.selectedPattern != null ? 0.0f : 1.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ThemePreviewActivity.this.backgroundCheckBoxView[ThemePreviewActivity.this.selectedPattern != null ? (char) 0 : (char) 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200L);
            animatorSet.start();
            return;
        }
        boolean isEnabled = this.backgroundCheckBoxView[0].isEnabled();
        TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
        if (isEnabled == (tL_wallPaper != null)) {
            return;
        }
        if (tL_wallPaper == null) {
            this.backgroundCheckBoxView[0].setChecked(false, true);
        }
        this.backgroundCheckBoxView[0].setEnabled(this.selectedPattern != null);
        if (this.selectedPattern != null) {
            this.backgroundCheckBoxView[0].setVisibility(0);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backgroundCheckBoxView[1].getLayoutParams();
        AnimatorSet animatorSet2 = new AnimatorSet();
        int dp = (layoutParams.width + AndroidUtilities.dp(9.0f)) / 2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[0], (Property<WallpaperCheckBoxView, Float>) View.ALPHA, this.selectedPattern == null ? 0.0f : 1.0f));
        WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
        Property property2 = View.TRANSLATION_X;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, (Property<WallpaperCheckBoxView, Float>) property2, this.selectedPattern != null ? 0.0f : dp));
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[1], (Property<WallpaperCheckBoxView, Float>) property2, this.selectedPattern == null ? -dp : 0.0f));
        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet2.setDuration(200L);
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (ThemePreviewActivity.this.selectedPattern == null) {
                    ThemePreviewActivity.this.backgroundCheckBoxView[0].setVisibility(4);
                }
            }
        });
        animatorSet2.start();
    }

    private void updatePlayAnimationView(boolean r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.updatePlayAnimationView(boolean):void");
    }

    private void updateSelectedPattern(boolean z) {
        int childCount = this.patternsListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.patternsListView.getChildAt(i);
            if (childAt instanceof PatternCell) {
                ((PatternCell) childAt).updateSelected(z);
            }
        }
    }

    @Override
    public android.view.View createView(android.content.Context r39) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.createView(android.content.Context):android.view.View");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.TL_wallPaper tL_wallPaper;
        String str;
        if (i == NotificationCenter.chatWasBoostedByUser) {
            if (this.dialogId == ((Long) objArr[2]).longValue()) {
                this.boostsStatus = (TL_stories.TL_premium_boostsStatus) objArr[0];
                updateApplyButton1(true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView == null) {
                return;
            }
            int childCount = recyclerListView.getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = this.listView.getChildAt(i3);
                if (childAt instanceof DialogCell) {
                    ((DialogCell) childAt).update(0);
                }
            }
            return;
        }
        if (i == NotificationCenter.invalidateMotionBackground) {
            RecyclerListView recyclerListView2 = this.listView2;
            if (recyclerListView2 != null) {
                recyclerListView2.invalidateViews();
                return;
            }
            return;
        }
        if (i == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = (String) objArr[0];
                    return;
                }
                return;
            }
            return;
        }
        long j = 0;
        if (i != NotificationCenter.wallpapersDidLoad) {
            if (i != NotificationCenter.wallpaperSettedToUser || this.dialogId == 0) {
                return;
            }
            lambda$onBackPressed$323();
            return;
        }
        ArrayList arrayList = (ArrayList) objArr[0];
        this.patterns.clear();
        this.patternsDict.clear();
        int size = arrayList.size();
        boolean z = false;
        for (int i4 = 0; i4 < size; i4++) {
            TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) arrayList.get(i4);
            if ((wallPaper instanceof TLRPC.TL_wallPaper) && wallPaper.pattern) {
                TLRPC.Document document = wallPaper.document;
                if (document != null && !this.patternsDict.containsKey(Long.valueOf(document.id))) {
                    this.patterns.add(wallPaper);
                    this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                }
                Theme.ThemeAccent themeAccent = this.accent;
                if (themeAccent != null && (str = themeAccent.patternSlug) != null && str.equals(wallPaper.slug)) {
                    this.selectedPattern = (TLRPC.TL_wallPaper) wallPaper;
                    setCurrentImage(false);
                    updateButtonState(false, false);
                } else if (this.accent == null) {
                    TLRPC.TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                    if (tL_wallPaper2 != null) {
                        String str2 = tL_wallPaper2.slug;
                        if (str2 != null) {
                            if (!str2.equals(wallPaper.slug)) {
                            }
                        }
                    }
                }
                z = true;
            }
        }
        if (!z && (tL_wallPaper = this.selectedPattern) != null) {
            this.patterns.add(0, tL_wallPaper);
        }
        PatternsAdapter patternsAdapter = this.patternsAdapter;
        if (patternsAdapter != null) {
            patternsAdapter.notifyDataSetChanged();
        }
        int size2 = arrayList.size();
        for (int i5 = 0; i5 < size2; i5++) {
            TLRPC.WallPaper wallPaper2 = (TLRPC.WallPaper) arrayList.get(i5);
            if (wallPaper2 instanceof TLRPC.TL_wallPaper) {
                j = MediaDataController.calcHash(j, wallPaper2.id);
            }
        }
        TL_account.getWallPapers getwallpapers = new TL_account.getWallPapers();
        getwallpapers.hash = j;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(getwallpapers, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ThemePreviewActivity.this.lambda$didReceivedNotification$31(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return this.themeDelegate;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        return this.shouldShowDayNightIcon ? getThemeDescriptionsInternal() : super.getThemeDescriptions();
    }

    public ArrayList getThemeDescriptionsInternal() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ThemePreviewActivity.this.lambda$getThemeDescriptionsInternal$33();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList arrayList = new ArrayList();
        FrameLayout frameLayout = this.page1;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(frameLayout, i, null, null, null, themeDescriptionDelegate, i2));
        ViewPager viewPager = this.viewPager;
        int i3 = ThemeDescription.FLAG_LISTGLOWCOLOR;
        int i4 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(viewPager, i3, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i4));
        ActionBar actionBar = this.actionBar;
        int i5 = ThemeDescription.FLAG_AB_SELECTORCOLOR;
        int i6 = Theme.key_actionBarDefaultSelector;
        arrayList.add(new ThemeDescription(actionBar, i5, null, null, null, null, i6));
        ActionBar actionBar2 = this.actionBar;
        int i7 = ThemeDescription.FLAG_AB_TITLECOLOR;
        int i8 = Theme.key_actionBarDefaultTitle;
        arrayList.add(new ThemeDescription(actionBar2, i7, null, null, null, null, i8));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, i8));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubtitle));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        if (!this.useDefaultThemeForButtons) {
            arrayList.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i2));
            TextView textView = this.cancelButton;
            int i9 = ThemeDescription.FLAG_TEXTCOLOR;
            int i10 = Theme.key_chat_fieldOverlayText;
            arrayList.add(new ThemeDescription(textView, i9, null, null, null, null, i10));
            arrayList.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i10));
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.provideThemeDescriptions(arrayList);
        }
        if (this.patternLayout != null) {
            for (int i11 = 0; i11 < this.patternLayout.length; i11++) {
                arrayList.add(new ThemeDescription(this.patternLayout[i11], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
                arrayList.add(new ThemeDescription(this.patternLayout[i11], 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            }
            for (int i12 = 0; i12 < this.patternsButtonsContainer.length; i12++) {
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i12], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i12], 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            }
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
            for (int i13 = 0; i13 < this.patternsSaveButton.length; i13++) {
                arrayList.add(new ThemeDescription(this.patternsSaveButton[i13], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
            }
            for (int i14 = 0; i14 < this.patternsCancelButton.length; i14++) {
                arrayList.add(new ThemeDescription(this.patternsCancelButton[i14], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
            }
            arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
            arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
            arrayList.add(new ThemeDescription(this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
            Drawable[] shadowDrawables = Theme.chat_msgInDrawable.getShadowDrawables();
            int i15 = Theme.key_chat_inBubbleShadow;
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, shadowDrawables, null, i15));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, i15));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
            Drawable[] shadowDrawables2 = Theme.chat_msgOutDrawable.getShadowDrawables();
            int i16 = Theme.key_chat_outBubbleShadow;
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, shadowDrawables2, null, i16));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, i16));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextIn));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        }
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_divider));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_dialogBackgroundGray));
        for (int i17 = 0; i17 < arrayList.size(); i17++) {
            ((ThemeDescription) arrayList.get(i17)).resourcesProvider = getResourceProvider();
        }
        return arrayList;
    }

    public boolean insideBottomSheet() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        return (iNavigationLayout == null || iNavigationLayout.getBottomSheet() == null) ? false : true;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        if (this.screenType != 2) {
            return false;
        }
        return !this.hasScrollingBackground || motionEvent == null || motionEvent.getY() <= ((float) (AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight()));
    }

    @Override
    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return false;
        }
        cancelThemeApply(true);
        return true;
    }

    @Override
    public void onBottomSheetCreated() {
        super.onBottomSheetCreated();
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout == null || iNavigationLayout.getBottomSheet() == null) {
            return;
        }
        this.parentLayout.getBottomSheet().fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
        if (this.screenType != 2 || this.dialogId == 0) {
            return;
        }
        this.parentLayout.getBottomSheet().setOverlayNavBarColor(-16777216);
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatWasBoostedByUser);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.invalidateMotionBackground);
        getNotificationCenter().addObserver(this, NotificationCenter.wallpaperSettedToUser);
        int i = this.screenType;
        if (i == 1 || i == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        int i2 = this.screenType;
        if (i2 == 2 || i2 == 1) {
            Theme.setChangingWallpaper(true);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            Point point = AndroidUtilities.displaySize;
            int min = Math.min(point.x, point.y);
            Point point2 = AndroidUtilities.displaySize;
            this.imageFilter = ((int) (min / AndroidUtilities.density)) + "_" + ((int) (Math.max(point2.x, point2.y) / AndroidUtilities.density)) + "_f";
            Point point3 = AndroidUtilities.displaySize;
            this.maxWallpaperSize = Math.max(point3.x, point3.y);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
            if (this.patterns == null) {
                this.patterns = new ArrayList();
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatWasBoostedByUser);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.invalidateMotionBackground);
        getNotificationCenter().removeObserver(this, NotificationCenter.wallpaperSettedToUser);
        FrameLayout frameLayout = this.frameLayout;
        if (frameLayout != null && this.onGlobalLayoutListener != null) {
            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if ((i == 2 || i == 1) && this.onSwitchDayNightDelegate == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    Theme.setChangingWallpaper(false);
                }
            });
        }
        int i2 = this.screenType;
        if (i2 == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            this.themeDelegate.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
        } else if (i2 == 1 || i2 == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (this.screenType != 0 || this.accent != null) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
        checkBlur(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
        Theme.disallowChangeServiceMessageColor = false;
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        MessagesAdapter messagesAdapter = this.messagesAdapter;
        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        Theme.disallowChangeServiceMessageColor = true;
    }

    @Override
    public void onSuccessDownload(String str) {
        updateButtonState(false, true);
    }

    @Override
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (z || this.screenType != 2) {
            return;
        }
        this.themeDelegate.applyChatServiceMessageColor();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setDialogId(long j) {
        this.dialogId = j;
        this.self = j == 0 || j == getUserConfig().getClientUserId();
    }

    public void setForceDark(boolean z, boolean z2) {
        if (z2) {
            RLottieDrawable rLottieDrawable = this.sunDrawable;
            rLottieDrawable.setCustomEndFrame(z ? rLottieDrawable.getFramesCount() : 0);
            RLottieDrawable rLottieDrawable2 = this.sunDrawable;
            if (rLottieDrawable2 != null) {
                rLottieDrawable2.start();
                return;
            }
            return;
        }
        int framesCount = z ? this.sunDrawable.getFramesCount() - 1 : 0;
        this.sunDrawable.setCurrentFrame(framesCount, false, true);
        this.sunDrawable.setCustomEndFrame(framesCount);
        ActionBarMenuItem actionBarMenuItem = this.dayNightItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.invalidate();
        }
    }

    public void setInitialModes(boolean z, boolean z2, float f) {
        this.isBlurred = z;
        this.isMotion = z2;
        this.dimAmount = f;
    }

    public void setOnSwitchDayNightDelegate(DayNightSwitchDelegate dayNightSwitchDelegate) {
        this.onSwitchDayNightDelegate = dayNightSwitchDelegate;
    }

    public void setPatterns(ArrayList arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) this.currentWallpaper;
            if (colorWallpaper.patternId != 0) {
                int size = arrayList.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(i);
                    if (tL_wallPaper.id == colorWallpaper.patternId) {
                        this.selectedPattern = tL_wallPaper;
                        break;
                    }
                    i++;
                }
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    @Override
    public void setResourceProvider(Theme.ResourcesProvider resourcesProvider) {
        this.themeDelegate.parentProvider = resourcesProvider;
    }

    public void toggleTheme() {
        if (this.changeDayNightView != null) {
            return;
        }
        FrameLayout frameLayout = (FrameLayout) (insideBottomSheet() ? this.parentLayout.getBottomSheet().getWindow() : getParentActivity().getWindow()).getDecorView();
        final Bitmap createBitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(createBitmap);
        this.dayNightItem.setAlpha(0.0f);
        frameLayout.draw(canvas);
        this.dayNightItem.setAlpha(1.0f);
        final Paint paint = new Paint(1);
        paint.setColor(-16777216);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        final Paint paint2 = new Paint(1);
        paint2.setFilterBitmap(true);
        int[] iArr = new int[2];
        this.dayNightItem.getLocationInWindow(iArr);
        final float f = iArr[0];
        final float f2 = iArr[1];
        final float measuredWidth = f + (this.dayNightItem.getMeasuredWidth() / 2.0f);
        final float measuredHeight = f2 + (this.dayNightItem.getMeasuredHeight() / 2.0f);
        final float max = Math.max(createBitmap.getHeight(), createBitmap.getWidth()) + AndroidUtilities.navigationBarHeight;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        paint2.setShader(new BitmapShader(createBitmap, tileMode, tileMode));
        View view = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas2) {
                super.onDraw(canvas2);
                if (ThemePreviewActivity.this.themeDelegate.isDark()) {
                    if (ThemePreviewActivity.this.changeDayNightViewProgress > 0.0f) {
                        canvas.drawCircle(measuredWidth, measuredHeight, max * ThemePreviewActivity.this.changeDayNightViewProgress, paint);
                    }
                    canvas2.drawBitmap(createBitmap, 0.0f, 0.0f, paint2);
                } else {
                    canvas2.drawCircle(measuredWidth, measuredHeight, max * (1.0f - ThemePreviewActivity.this.changeDayNightViewProgress), paint2);
                }
                canvas2.save();
                canvas2.translate(f, f2);
                ThemePreviewActivity.this.dayNightItem.draw(canvas2);
                canvas2.restore();
            }
        };
        this.changeDayNightView = view;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$toggleTheme$34;
                lambda$toggleTheme$34 = ThemePreviewActivity.lambda$toggleTheme$34(view2, motionEvent);
                return lambda$toggleTheme$34;
            }
        });
        this.changeDayNightViewProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.changeDayNightViewAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean changedNavigationBarColor = false;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ThemePreviewActivity.this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                ThemePreviewActivity.this.changeDayNightView.invalidate();
                if (this.changedNavigationBarColor || ThemePreviewActivity.this.changeDayNightViewProgress <= 0.5f) {
                    return;
                }
                this.changedNavigationBarColor = true;
            }
        });
        this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (ThemePreviewActivity.this.changeDayNightView != null) {
                    if (ThemePreviewActivity.this.changeDayNightView.getParent() != null) {
                        ((ViewGroup) ThemePreviewActivity.this.changeDayNightView.getParent()).removeView(ThemePreviewActivity.this.changeDayNightView);
                    }
                    ThemePreviewActivity.this.changeDayNightView = null;
                }
                ThemePreviewActivity.this.changeDayNightViewAnimator = null;
                super.onAnimationEnd(animator);
            }
        });
        this.changeDayNightViewAnimator.setDuration(400L);
        this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        this.changeDayNightViewAnimator.start();
        frameLayout.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemePreviewActivity.this.lambda$toggleTheme$36();
            }
        });
    }
}
