package org.telegram.ui.Cells;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ThemeSetUrlActivity;

public abstract class ThemesHorizontalListCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    public static byte[] bytes = new byte[1024];
    private ThemesListAdapter adapter;
    private int currentType;
    private ArrayList customThemes;
    private ArrayList defaultThemes;
    private boolean drawDivider;
    private BaseFragment fragment;
    private LinearLayoutManager horizontalLayoutManager;
    private HashMap loadingThemes;
    private HashMap loadingWallpapers;
    private int prevCount;
    private Theme.ThemeInfo prevThemeInfo;

    public class InnerThemeView extends FrameLayout {
        private ObjectAnimator accentAnimator;
        private boolean accentColorChanged;
        private int accentId;
        private float accentState;
        private int backColor;
        private Drawable backgroundDrawable;
        private Paint bitmapPaint;
        private BitmapShader bitmapShader;
        private RadioButton button;
        private int checkColor;
        private final ArgbEvaluator evaluator;
        private boolean hasWhiteBackground;
        private int inColor;
        private Drawable inDrawable;
        private boolean isFirst;
        private boolean isLast;
        private long lastDrawTime;
        private int loadingColor;
        private Drawable loadingDrawable;
        private int oldBackColor;
        private int oldCheckColor;
        private int oldInColor;
        private int oldOutColor;
        private Drawable optionsDrawable;
        private int outColor;
        private Drawable outDrawable;
        private Paint paint;
        private float placeholderAlpha;
        private boolean pressed;
        private RectF rect;
        private Matrix shaderMatrix;
        private TextPaint textPaint;
        private Theme.ThemeInfo themeInfo;

        public InnerThemeView(Context context) {
            super(context);
            this.rect = new RectF();
            this.paint = new Paint(1);
            this.textPaint = new TextPaint(1);
            this.evaluator = new ArgbEvaluator();
            this.bitmapPaint = new Paint(3);
            this.shaderMatrix = new Matrix();
            setWillNotDraw(false);
            this.inDrawable = context.getResources().getDrawable(R.drawable.minibubble_in).mutate();
            this.outDrawable = context.getResources().getDrawable(R.drawable.minibubble_out).mutate();
            this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            RadioButton radioButton = new RadioButton(context);
            this.button = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 51, 27.0f, 75.0f, 0.0f, 0.0f));
        }

        public void applyTheme() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.applyTheme():void");
        }

        private int blend(int i, int i2) {
            float f = this.accentState;
            return f == 1.0f ? i2 : ((Integer) this.evaluator.evaluate(f, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        }

        private String getThemeName() {
            String name = this.themeInfo.getName();
            return name.toLowerCase().endsWith(".attheme") ? name.substring(0, name.lastIndexOf(46)) : name;
        }

        public void lambda$parseTheme$0(TLObject tLObject) {
            if (!(tLObject instanceof TLRPC.TL_wallPaper)) {
                this.themeInfo.badWallpaper = true;
                return;
            }
            TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) tLObject;
            String attachFileName = FileLoader.getAttachFileName(wallPaper.document);
            if (ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                return;
            }
            ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
            FileLoader.getInstance(this.themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
        }

        public void lambda$parseTheme$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ThemesHorizontalListCell.InnerThemeView.this.lambda$parseTheme$0(tLObject);
                }
            });
        }

        public boolean parseTheme() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.parseTheme():boolean");
        }

        @Override
        protected void onAttachedToWindow() {
            TLRPC.TL_theme tL_theme;
            super.onAttachedToWindow();
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), false);
            Theme.ThemeInfo themeInfo = this.themeInfo;
            if (themeInfo == null || (tL_theme = themeInfo.info) == null || themeInfo.themeLoaded) {
                return;
            }
            if (ThemesHorizontalListCell.this.loadingThemes.containsKey(FileLoader.getAttachFileName(tL_theme.document)) || ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
                return;
            }
            this.themeInfo.themeLoaded = true;
            this.placeholderAlpha = 0.0f;
            parseTheme();
            applyTheme();
        }

        @Override
        protected void onDraw(android.graphics.Canvas r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.onDraw(android.graphics.Canvas):void");
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(getThemeName());
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.button.isChecked());
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
            if (Build.VERSION.SDK_INT >= 21) {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK;
                accessibilityNodeInfo.addAction(accessibilityAction);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString(R.string.AccDescrMoreOptions)));
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((this.isLast ? 22 : 15) + 76 + (this.isFirst ? 22 : 0)), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), 1073741824));
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            Theme.ThemeInfo themeInfo;
            if (this.optionsDrawable == null || (themeInfo = this.themeInfo) == null || !((themeInfo.info == null || themeInfo.themeLoaded) && ThemesHorizontalListCell.this.currentType == 0)) {
                return super.onTouchEvent(motionEvent);
            }
            int action = motionEvent.getAction();
            if (action == 0 || action == 1) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (x > this.rect.centerX() && y < this.rect.centerY() - AndroidUtilities.dp(10.0f)) {
                    if (action == 0) {
                        this.pressed = true;
                    } else {
                        try {
                            performHapticFeedback(3);
                        } catch (Exception unused) {
                        }
                        ThemesHorizontalListCell.this.showOptionsForTheme(this.themeInfo);
                    }
                }
                if (action == 1) {
                    this.pressed = false;
                }
            }
            return this.pressed;
        }

        public void setAccentState(float f) {
            this.accentState = f;
            this.accentColorChanged = true;
            invalidate();
        }

        public void setTheme(Theme.ThemeInfo themeInfo, boolean z, boolean z2) {
            Theme.ThemeInfo themeInfo2;
            TLRPC.TL_theme tL_theme;
            this.themeInfo = themeInfo;
            this.isFirst = z2;
            this.isLast = z;
            this.accentId = themeInfo.currentAccentId;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.button.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(this.isFirst ? 49.0f : 27.0f);
            this.button.setLayoutParams(layoutParams);
            this.placeholderAlpha = 0.0f;
            Theme.ThemeInfo themeInfo3 = this.themeInfo;
            if (themeInfo3.pathToFile != null && !themeInfo3.previewParsed) {
                themeInfo3.setPreviewInColor(Theme.getDefaultColor(Theme.key_chat_inBubble));
                this.themeInfo.setPreviewOutColor(Theme.getDefaultColor(Theme.key_chat_outBubble));
                boolean exists = new File(this.themeInfo.pathToFile).exists();
                if ((!exists || !parseTheme() || !exists) && (tL_theme = (themeInfo2 = this.themeInfo).info) != null) {
                    if (tL_theme.document != null) {
                        themeInfo2.themeLoaded = false;
                        this.placeholderAlpha = 1.0f;
                        Drawable mutate = getResources().getDrawable(R.drawable.msg_theme).mutate();
                        this.loadingDrawable = mutate;
                        int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                        this.loadingColor = color;
                        Theme.setDrawableColor(mutate, color);
                        if (!exists) {
                            String attachFileName = FileLoader.getAttachFileName(this.themeInfo.info.document);
                            if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                                ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
                                FileLoader fileLoader = FileLoader.getInstance(this.themeInfo.account);
                                TLRPC.TL_theme tL_theme2 = this.themeInfo.info;
                                fileLoader.loadFile(tL_theme2.document, tL_theme2, 1, 1);
                            }
                        }
                    } else {
                        Drawable mutate2 = getResources().getDrawable(R.drawable.preview_custom).mutate();
                        this.loadingDrawable = mutate2;
                        int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7);
                        this.loadingColor = color2;
                        Theme.setDrawableColor(mutate2, color2);
                    }
                }
            }
            applyTheme();
        }

        void updateColors(boolean z) {
            int i;
            int i2;
            this.oldInColor = this.inColor;
            this.oldOutColor = this.outColor;
            this.oldBackColor = this.backColor;
            this.oldCheckColor = this.checkColor;
            int i3 = 0;
            Theme.ThemeAccent accent = this.themeInfo.getAccent(false);
            if (accent != null) {
                i3 = accent.accentColor;
                i2 = accent.myMessagesAccentColor;
                if (i2 == 0) {
                    i2 = i3;
                }
                i = (int) accent.backgroundOverrideColor;
                if (i == 0) {
                    i = i3;
                }
            } else {
                i = 0;
                i2 = 0;
            }
            Theme.ThemeInfo themeInfo = this.themeInfo;
            this.inColor = Theme.changeColorAccent(themeInfo, i3, themeInfo.getPreviewInColor());
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            this.outColor = Theme.changeColorAccent(themeInfo2, i2, themeInfo2.getPreviewOutColor());
            Theme.ThemeInfo themeInfo3 = this.themeInfo;
            this.backColor = Theme.changeColorAccent(themeInfo3, i, themeInfo3.getPreviewBackgroundColor());
            this.checkColor = this.outColor;
            this.accentId = this.themeInfo.currentAccentId;
            ObjectAnimator objectAnimator = this.accentAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (!z) {
                setAccentState(1.0f);
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "accentState", 0.0f, 1.0f);
            this.accentAnimator = ofFloat;
            ofFloat.setDuration(200L);
            this.accentAnimator.start();
        }

        public void updateCurrentThemeCheck() {
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), true);
        }
    }

    public class ThemesListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        ThemesListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getItemCount() {
            ThemesHorizontalListCell themesHorizontalListCell = ThemesHorizontalListCell.this;
            return themesHorizontalListCell.prevCount = themesHorizontalListCell.defaultThemes.size() + ThemesHorizontalListCell.this.customThemes.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList arrayList;
            int size;
            InnerThemeView innerThemeView = (InnerThemeView) viewHolder.itemView;
            if (i < ThemesHorizontalListCell.this.defaultThemes.size()) {
                arrayList = ThemesHorizontalListCell.this.defaultThemes;
                size = i;
            } else {
                arrayList = ThemesHorizontalListCell.this.customThemes;
                size = i - ThemesHorizontalListCell.this.defaultThemes.size();
            }
            innerThemeView.setTheme((Theme.ThemeInfo) arrayList.get(size), i == getItemCount() - 1, i == 0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new InnerThemeView(this.mContext));
        }
    }

    public ThemesHorizontalListCell(Context context, BaseFragment baseFragment, int i, ArrayList arrayList, ArrayList arrayList2) {
        super(context);
        this.loadingThemes = new HashMap();
        this.loadingWallpapers = new HashMap();
        this.customThemes = arrayList2;
        this.defaultThemes = arrayList;
        this.currentType = i;
        this.fragment = baseFragment;
        setBackgroundColor(Theme.getColor(i == 2 ? Theme.key_dialogBackground : Theme.key_windowBackgroundWhite));
        setItemAnimator(null);
        setLayoutAnimation(null);
        this.horizontalLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        this.horizontalLayoutManager.setOrientation(0);
        setLayoutManager(this.horizontalLayoutManager);
        ThemesListAdapter themesListAdapter = new ThemesListAdapter(context);
        this.adapter = themesListAdapter;
        setAdapter(themesListAdapter);
        setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                ThemesHorizontalListCell.this.lambda$new$0(view, i2);
            }
        });
        setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i2) {
                boolean lambda$new$1;
                lambda$new$1 = ThemesHorizontalListCell.this.lambda$new$1(view, i2);
                return lambda$new$1;
            }
        });
    }

    public void lambda$didReceivedNotification$2(Theme.ThemeInfo themeInfo) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof InnerThemeView) {
                InnerThemeView innerThemeView = (InnerThemeView) childAt;
                if (innerThemeView.themeInfo == themeInfo && innerThemeView.parseTheme()) {
                    innerThemeView.themeInfo.themeLoaded = true;
                    innerThemeView.applyTheme();
                }
            }
        }
    }

    public void lambda$didReceivedNotification$3(final Theme.ThemeInfo themeInfo, File file) {
        themeInfo.badWallpaper = !themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ThemesHorizontalListCell.this.lambda$didReceivedNotification$2(themeInfo);
            }
        });
    }

    public void lambda$new$0(View view, int i) {
        selectTheme(((InnerThemeView) view).themeInfo);
        int left = view.getLeft();
        int right = view.getRight();
        if (left < 0) {
            smoothScrollBy(left - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
    }

    public boolean lambda$new$1(View view, int i) {
        showOptionsForTheme(((InnerThemeView) view).themeInfo);
        return true;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.fileLoaded) {
            if (i == NotificationCenter.fileLoadFailed) {
                this.loadingThemes.remove((String) objArr[0]);
                return;
            }
            return;
        }
        String str = (String) objArr[0];
        final File file = (File) objArr[1];
        final Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) this.loadingThemes.get(str);
        if (themeInfo != null) {
            this.loadingThemes.remove(str);
            if (this.loadingWallpapers.remove(themeInfo) != null) {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        ThemesHorizontalListCell.this.lambda$didReceivedNotification$3(themeInfo, file);
                    }
                });
            } else {
                lambda$didReceivedNotification$2(themeInfo);
            }
        }
    }

    public void notifyDataSetChanged(int i) {
        if (this.prevCount == this.adapter.getItemCount()) {
            return;
        }
        this.adapter.notifyDataSetChanged();
        if (this.prevThemeInfo != (this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme())) {
            scrollToCurrentTheme(i, false);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getParent() != null && getParent().getParent() != null) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void scrollToCurrentTheme(int i, boolean z) {
        View view;
        if (i == 0 && (view = (View) getParent()) != null) {
            i = view.getMeasuredWidth();
        }
        if (i == 0) {
            return;
        }
        Theme.ThemeInfo currentNightTheme = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
        this.prevThemeInfo = currentNightTheme;
        int indexOf = this.defaultThemes.indexOf(currentNightTheme);
        if (indexOf >= 0 || (indexOf = this.customThemes.indexOf(this.prevThemeInfo) + this.defaultThemes.size()) >= 0) {
            if (z) {
                smoothScrollToPosition(indexOf);
            } else {
                this.horizontalLayoutManager.scrollToPositionWithOffset(indexOf, (i - AndroidUtilities.dp(76.0f)) / 2);
            }
        }
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        TLRPC.TL_theme tL_theme = themeInfo.info;
        if (tL_theme != null) {
            if (!themeInfo.themeLoaded) {
                return;
            }
            if (tL_theme.document == null) {
                BaseFragment baseFragment = this.fragment;
                if (baseFragment != null) {
                    baseFragment.presentFragment(new ThemeSetUrlActivity(themeInfo, null, true));
                    return;
                }
                return;
            }
        }
        if (!TextUtils.isEmpty(themeInfo.assetName)) {
            Theme.PatternsLoader.createLoader(false);
        }
        SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        edit.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
        edit.commit();
        if (this.currentType == 1) {
            if (themeInfo == Theme.getCurrentNightTheme()) {
                return;
            } else {
                Theme.setCurrentNightTheme(themeInfo);
            }
        } else if (themeInfo == Theme.getCurrentTheme()) {
            return;
        } else {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, null, -1);
        }
        updateRows();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof InnerThemeView) {
                ((InnerThemeView) childAt).updateCurrentThemeCheck();
            }
        }
        EmojiThemes.saveCustomTheme(themeInfo, themeInfo.currentAccentId);
        if (this.currentType != 1) {
            Theme.turnOffAutoNight(this.fragment);
        }
    }

    @Override
    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        invalidateViews();
    }

    public void setDrawDivider(boolean z) {
        this.drawDivider = z;
    }

    protected void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
    }

    protected abstract void updateRows();
}
