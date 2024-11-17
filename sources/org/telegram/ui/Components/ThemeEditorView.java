package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeColors;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.LaunchActivity;

public class ThemeEditorView {
    private static volatile ThemeEditorView Instance;
    private ArrayList currentThemeDesription;
    private int currentThemeDesriptionPosition;
    private DecelerateInterpolator decelerateInterpolator;
    private EditorAlert editorAlert;
    private boolean hidden;
    private Activity parentActivity;
    private SharedPreferences preferences;
    private Theme.ThemeInfo themeInfo;
    private WallpaperUpdater wallpaperUpdater;
    private WindowManager.LayoutParams windowLayoutParams;
    private WindowManager windowManager;
    private FrameLayout windowView;
    private final int editorWidth = AndroidUtilities.dp(54.0f);
    private final int editorHeight = AndroidUtilities.dp(54.0f);

    public class AnonymousClass1 extends FrameLayout {
        private boolean dragging;
        private float startX;
        private float startY;

        AnonymousClass1(Context context) {
            super(context);
        }

        public static void lambda$onTouchEvent$0(DialogInterface dialogInterface) {
        }

        public void lambda$onTouchEvent$1(DialogInterface dialogInterface) {
            ThemeEditorView.this.editorAlert = null;
            ThemeEditorView.this.show();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent r11) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.AnonymousClass1.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }

    public class EditorAlert extends BottomSheet {
        private boolean animationInProgress;
        private FrameLayout bottomLayout;
        private FrameLayout bottomSaveLayout;
        private AnimatorSet colorChangeAnimation;
        private ColorPicker colorPicker;
        private FrameLayout frameLayout;
        private boolean ignoreTextChange;
        private LinearLayoutManager layoutManager;
        private ListAdapter listAdapter;
        private RecyclerListView listView;
        private int previousScrollPosition;
        private int scrollOffsetY;
        private SearchAdapter searchAdapter;
        private EmptyTextProgressView searchEmptyView;
        private SearchField searchField;
        private View[] shadow;
        private AnimatorSet[] shadowAnimation;
        private Drawable shadowDrawable;
        private boolean startedColorChange;
        private int topBeforeSwitch;

        public class ColorPicker extends FrameLayout {
            private float alpha;
            private LinearGradient alphaGradient;
            private boolean alphaPressed;
            private Drawable circleDrawable;
            private Paint circlePaint;
            private boolean circlePressed;
            private EditTextBoldCursor[] colorEditText;
            private LinearGradient colorGradient;
            private float[] colorHSV;
            private boolean colorPressed;
            private Bitmap colorWheelBitmap;
            private Paint colorWheelPaint;
            private int colorWheelRadius;
            private DecelerateInterpolator decelerateInterpolator;
            private float[] hsvTemp;
            private LinearLayout linearLayout;
            private final int paramValueSliderWidth;
            private Paint valueSliderPaint;

            public ColorPicker(android.content.Context r21) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.<init>(org.telegram.ui.Components.ThemeEditorView$EditorAlert, android.content.Context):void");
            }

            private Bitmap createColorWheelBitmap(int i, int i2) {
                Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                int[] iArr = new int[13];
                float[] fArr = {0.0f, 1.0f, 1.0f};
                for (int i3 = 0; i3 < 13; i3++) {
                    fArr[0] = ((i3 * 30) + 180) % 360;
                    iArr[i3] = Color.HSVToColor(fArr);
                }
                iArr[12] = iArr[0];
                float f = i / 2;
                float f2 = i2 / 2;
                this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient(f, f2, iArr, (float[]) null), new RadialGradient(f, f2, this.colorWheelRadius, -1, 16777215, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER));
                new Canvas(createBitmap).drawCircle(f, f2, this.colorWheelRadius, this.colorWheelPaint);
                return createBitmap;
            }

            private void drawPointerArrow(Canvas canvas, int i, int i2, int i3) {
                int dp = AndroidUtilities.dp(13.0f);
                this.circleDrawable.setBounds(i - dp, i2 - dp, i + dp, dp + i2);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                float f = i;
                float f2 = i2;
                canvas.drawCircle(f, f2, AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(i3);
                canvas.drawCircle(f, f2, AndroidUtilities.dp(9.0f), this.circlePaint);
            }

            public static boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(textView);
                return true;
            }

            private void startColorChange(boolean z) {
                if (EditorAlert.this.startedColorChange == z) {
                    return;
                }
                if (EditorAlert.this.colorChangeAnimation != null) {
                    EditorAlert.this.colorChangeAnimation.cancel();
                }
                EditorAlert.this.startedColorChange = z;
                EditorAlert.this.colorChangeAnimation = new AnimatorSet();
                EditorAlert.this.colorChangeAnimation.playTogether(ObjectAnimator.ofInt(((BottomSheet) EditorAlert.this).backDrawable, (Property<ColorDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, z ? 0 : 51), ObjectAnimator.ofFloat(((BottomSheet) EditorAlert.this).containerView, (Property<ViewGroup, Float>) View.ALPHA, z ? 0.2f : 1.0f));
                EditorAlert.this.colorChangeAnimation.setDuration(150L);
                EditorAlert.this.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                EditorAlert.this.colorChangeAnimation.start();
            }

            public int getColor() {
                return (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                float f;
                float f2;
                int width = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int height = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                Bitmap bitmap = this.colorWheelBitmap;
                int i = this.colorWheelRadius;
                canvas.drawBitmap(bitmap, width - i, height - i, (Paint) null);
                double radians = (float) Math.toRadians(this.colorHSV[0]);
                double d = -Math.cos(radians);
                double d2 = this.colorHSV[1];
                Double.isNaN(d2);
                double d3 = d * d2;
                double d4 = this.colorWheelRadius;
                Double.isNaN(d4);
                int i2 = ((int) (d3 * d4)) + width;
                double d5 = -Math.sin(radians);
                float[] fArr = this.colorHSV;
                float f3 = fArr[1];
                double d6 = f3;
                Double.isNaN(d6);
                double d7 = d5 * d6;
                double d8 = this.colorWheelRadius;
                Double.isNaN(d8);
                float[] fArr2 = this.hsvTemp;
                fArr2[0] = fArr[0];
                fArr2[1] = f3;
                fArr2[2] = 1.0f;
                drawPointerArrow(canvas, i2, ((int) (d7 * d8)) + height, Color.HSVToColor(fArr2));
                int i3 = this.colorWheelRadius;
                int i4 = width + i3 + this.paramValueSliderWidth;
                int i5 = height - i3;
                int dp = AndroidUtilities.dp(9.0f);
                int i6 = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    this.colorGradient = new LinearGradient(i4, i5, i4 + dp, i5 + i6, new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.colorGradient);
                float f4 = i5;
                float f5 = i5 + i6;
                canvas.drawRect(i4, f4, i4 + dp, f5, this.valueSliderPaint);
                int i7 = dp / 2;
                float[] fArr3 = this.colorHSV;
                float f6 = i6;
                drawPointerArrow(canvas, i4 + i7, (int) ((fArr3[2] * f6) + f4), Color.HSVToColor(fArr3));
                int i8 = i4 + (this.paramValueSliderWidth * 2);
                if (this.alphaGradient == null) {
                    int HSVToColor = Color.HSVToColor(this.hsvTemp);
                    f = f5;
                    f2 = f4;
                    this.alphaGradient = new LinearGradient(i8, f4, i8 + dp, f, new int[]{HSVToColor, HSVToColor & 16777215}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    f = f5;
                    f2 = f4;
                }
                this.valueSliderPaint.setShader(this.alphaGradient);
                canvas.drawRect(i8, f2, dp + i8, f, this.valueSliderPaint);
                drawPointerArrow(canvas, i8 + i7, (int) (f2 + ((1.0f - this.alpha) * f6)), (Color.HSVToColor(this.colorHSV) & 16777215) | (((int) (this.alpha * 255.0f)) << 24));
            }

            @Override
            protected void onMeasure(int i, int i2) {
                int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                measureChild(this.linearLayout, i, i2);
                setMeasuredDimension(min, min);
            }

            @Override
            protected void onSizeChanged(int i, int i2, int i3, int i4) {
                int max = Math.max(1, ((i / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                this.colorWheelRadius = max;
                int i5 = max * 2;
                this.colorWheelBitmap = createColorWheelBitmap(i5, i5);
                this.colorGradient = null;
                this.alphaGradient = null;
            }

            @Override
            public boolean onTouchEvent(android.view.MotionEvent r16) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public void setColor(int i) {
                int red = Color.red(i);
                int green = Color.green(i);
                int blue = Color.blue(i);
                int alpha = Color.alpha(i);
                if (!EditorAlert.this.ignoreTextChange) {
                    EditorAlert.this.ignoreTextChange = true;
                    this.colorEditText[0].setText("" + red);
                    this.colorEditText[1].setText("" + green);
                    this.colorEditText[2].setText("" + blue);
                    this.colorEditText[3].setText("" + alpha);
                    for (int i2 = 0; i2 < 4; i2++) {
                        EditTextBoldCursor editTextBoldCursor = this.colorEditText[i2];
                        editTextBoldCursor.setSelection(editTextBoldCursor.length());
                    }
                    EditorAlert.this.ignoreTextChange = false;
                }
                this.alphaGradient = null;
                this.colorGradient = null;
                this.alpha = alpha / 255.0f;
                Color.colorToHSV(i, this.colorHSV);
                invalidate();
            }
        }

        public class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private ArrayList items = new ArrayList();

            public ListAdapter(Context context, ArrayList arrayList) {
                this.context = context;
                HashMap hashMap = new HashMap();
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ThemeDescription themeDescription = (ThemeDescription) arrayList.get(i);
                    int currentKey = themeDescription.getCurrentKey();
                    ArrayList arrayList2 = (ArrayList) hashMap.get(Integer.valueOf(currentKey));
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        hashMap.put(Integer.valueOf(currentKey), arrayList2);
                        this.items.add(arrayList2);
                    }
                    arrayList2.add(themeDescription);
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    int i2 = Theme.key_windowBackgroundGray;
                    if (hashMap.containsKey(Integer.valueOf(i2))) {
                        return;
                    }
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(new ThemeDescription(null, 0, null, null, null, null, i2));
                    this.items.add(arrayList3);
                }
            }

            public ArrayList getItem(int i) {
                if (i < 0 || i >= this.items.size()) {
                    return null;
                }
                return (ArrayList) this.items.get(i);
            }

            @Override
            public int getItemCount() {
                if (this.items.isEmpty()) {
                    return 0;
                }
                return this.items.size() + 1;
            }

            @Override
            public int getItemViewType(int i) {
                return i == 0 ? 1 : 0;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 0) {
                    ThemeDescription themeDescription = (ThemeDescription) ((ArrayList) this.items.get(i - 1)).get(0);
                    ((TextColorThemeCell) viewHolder.itemView).setTextAndColor(themeDescription.getTitle(), themeDescription.getCurrentKey() != Theme.key_chat_wallpaper ? themeDescription.getSetColor() : 0);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View textColorThemeCell;
                RecyclerView.LayoutParams layoutParams;
                if (i != 0) {
                    textColorThemeCell = new View(this.context);
                    layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f));
                } else {
                    textColorThemeCell = new TextColorThemeCell(this.context);
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                }
                textColorThemeCell.setLayoutParams(layoutParams);
                return new RecyclerListView.Holder(textColorThemeCell);
            }
        }

        public class SearchAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int lastSearchId;
            private String lastSearchText;
            private Runnable searchRunnable;
            private ArrayList searchResult = new ArrayList();
            private ArrayList searchNames = new ArrayList();

            public SearchAdapter(Context context) {
                this.context = context;
            }

            public void lambda$updateSearchResults$0(int i, ArrayList arrayList, ArrayList arrayList2) {
                if (i != this.lastSearchId) {
                    return;
                }
                if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.searchAdapter) {
                    EditorAlert editorAlert = EditorAlert.this;
                    editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                    EditorAlert.this.listView.setAdapter(EditorAlert.this.searchAdapter);
                    EditorAlert.this.searchAdapter.notifyDataSetChanged();
                }
                boolean z = !this.searchResult.isEmpty() && arrayList.isEmpty();
                boolean z2 = this.searchResult.isEmpty() && arrayList.isEmpty();
                if (z) {
                    EditorAlert editorAlert2 = EditorAlert.this;
                    editorAlert2.topBeforeSwitch = editorAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                this.searchNames = arrayList2;
                notifyDataSetChanged();
                if (!z2 && !z && EditorAlert.this.topBeforeSwitch > 0) {
                    EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -EditorAlert.this.topBeforeSwitch);
                    EditorAlert.this.topBeforeSwitch = -1000;
                }
                EditorAlert.this.searchEmptyView.showTextView();
            }

            public void lambda$searchDialogs$1(String str, int i) {
                try {
                    String lowerCase = str.trim().toLowerCase();
                    if (lowerCase.length() == 0) {
                        this.lastSearchId = -1;
                        updateSearchResults(new ArrayList(), new ArrayList(), this.lastSearchId);
                        return;
                    }
                    String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
                    if (lowerCase.equals(translitString) || translitString.length() == 0) {
                        translitString = null;
                    }
                    int i2 = (translitString != null ? 1 : 0) + 1;
                    String[] strArr = new String[i2];
                    strArr[0] = lowerCase;
                    if (translitString != null) {
                        strArr[1] = translitString;
                    }
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    int size = EditorAlert.this.listAdapter.items.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        ArrayList arrayList3 = (ArrayList) EditorAlert.this.listAdapter.items.get(i3);
                        String stringName = ThemeColors.getStringName(((ThemeDescription) arrayList3.get(0)).getCurrentKey());
                        String lowerCase2 = stringName.toLowerCase();
                        int i4 = 0;
                        while (true) {
                            if (i4 < i2) {
                                String str2 = strArr[i4];
                                if (lowerCase2.contains(str2)) {
                                    arrayList.add(arrayList3);
                                    arrayList2.add(generateSearchName(stringName, str2));
                                    break;
                                }
                                i4++;
                            }
                        }
                    }
                    updateSearchResults(arrayList, arrayList2, i);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            private void updateSearchResults(final ArrayList arrayList, final ArrayList arrayList2, final int i) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ThemeEditorView.EditorAlert.SearchAdapter.this.lambda$updateSearchResults$0(i, arrayList, arrayList2);
                    }
                });
            }

            public java.lang.CharSequence generateSearchName(java.lang.String r9, java.lang.String r10) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.SearchAdapter.generateSearchName(java.lang.String, java.lang.String):java.lang.CharSequence");
            }

            public ArrayList getItem(int i) {
                if (i < 0 || i >= this.searchResult.size()) {
                    return null;
                }
                return (ArrayList) this.searchResult.get(i);
            }

            @Override
            public int getItemCount() {
                if (this.searchResult.isEmpty()) {
                    return 0;
                }
                return this.searchResult.size() + 1;
            }

            @Override
            public int getItemViewType(int i) {
                return i == 0 ? 1 : 0;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 0) {
                    int i2 = i - 1;
                    ThemeDescription themeDescription = (ThemeDescription) ((ArrayList) this.searchResult.get(i2)).get(0);
                    ((TextColorThemeCell) viewHolder.itemView).setTextAndColor((CharSequence) this.searchNames.get(i2), themeDescription.getCurrentKey() != Theme.key_chat_wallpaper ? themeDescription.getSetColor() : 0);
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View textColorThemeCell;
                RecyclerView.LayoutParams layoutParams;
                if (i != 0) {
                    textColorThemeCell = new View(this.context);
                    layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f));
                } else {
                    textColorThemeCell = new TextColorThemeCell(this.context);
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                }
                textColorThemeCell.setLayoutParams(layoutParams);
                return new RecyclerListView.Holder(textColorThemeCell);
            }

            public void searchDialogs(final String str) {
                if (str == null || !str.equals(this.lastSearchText)) {
                    this.lastSearchText = str;
                    if (this.searchRunnable != null) {
                        Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                        this.searchRunnable = null;
                    }
                    if (str != null && str.length() != 0) {
                        final int i = this.lastSearchId + 1;
                        this.lastSearchId = i;
                        this.searchRunnable = new Runnable() {
                            @Override
                            public final void run() {
                                ThemeEditorView.EditorAlert.SearchAdapter.this.lambda$searchDialogs$1(str, i);
                            }
                        };
                        Utilities.searchQueue.postRunnable(this.searchRunnable, 300L);
                        return;
                    }
                    this.searchResult.clear();
                    EditorAlert editorAlert = EditorAlert.this;
                    editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    notifyDataSetChanged();
                }
            }
        }

        public class SearchField extends FrameLayout {
            private ImageView clearSearchImageView;
            private EditTextBoldCursor searchEditText;

            public SearchField(Context context) {
                super(context);
                View view = new View(context);
                view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -854795));
                addView(view, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
                ImageView imageView = new ImageView(context);
                ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
                imageView.setScaleType(scaleType);
                imageView.setImageResource(R.drawable.smiles_inputsearch);
                imageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
                ImageView imageView2 = new ImageView(context);
                this.clearSearchImageView = imageView2;
                imageView2.setScaleType(scaleType);
                ImageView imageView3 = this.clearSearchImageView;
                CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() {
                    @Override
                    public int getCurrentColor() {
                        return -6182737;
                    }
                };
                imageView3.setImageDrawable(closeProgressDrawable2);
                closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
                this.clearSearchImageView.setScaleX(0.1f);
                this.clearSearchImageView.setScaleY(0.1f);
                this.clearSearchImageView.setAlpha(0.0f);
                addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
                this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ThemeEditorView.EditorAlert.SearchField.this.lambda$new$0(view2);
                    }
                });
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
                    @Override
                    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                        MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.setLocation(obtain.getRawX(), obtain.getRawY() - ((BottomSheet) EditorAlert.this).containerView.getTranslationY());
                        EditorAlert.this.listView.dispatchTouchEvent(obtain);
                        obtain.recycle();
                        return super.dispatchTouchEvent(motionEvent);
                    }
                };
                this.searchEditText = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 16.0f);
                this.searchEditText.setHintTextColor(-6774617);
                this.searchEditText.setTextColor(-14540254);
                this.searchEditText.setBackgroundDrawable(null);
                this.searchEditText.setPadding(0, 0, 0, 0);
                this.searchEditText.setMaxLines(1);
                this.searchEditText.setLines(1);
                this.searchEditText.setSingleLine(true);
                this.searchEditText.setImeOptions(268435459);
                this.searchEditText.setHint(LocaleController.getString(R.string.Search));
                this.searchEditText.setCursorColor(-11491093);
                this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.searchEditText.setCursorWidth(1.5f);
                addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
                this.searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        boolean z = SearchField.this.searchEditText.length() > 0;
                        if (z != (SearchField.this.clearSearchImageView.getAlpha() != 0.0f)) {
                            SearchField.this.clearSearchImageView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).scaleX(z ? 1.0f : 0.1f).scaleY(z ? 1.0f : 0.1f).start();
                        }
                        String obj = SearchField.this.searchEditText.getText().toString();
                        if (obj.length() != 0) {
                            if (EditorAlert.this.searchEmptyView != null) {
                                EditorAlert.this.searchEmptyView.setText(LocaleController.getString(R.string.NoResult));
                            }
                        } else if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.listAdapter) {
                            int currentTop = EditorAlert.this.getCurrentTop();
                            EditorAlert.this.searchEmptyView.setText(LocaleController.getString(R.string.NoChats));
                            EditorAlert.this.searchEmptyView.showTextView();
                            EditorAlert.this.listView.setAdapter(EditorAlert.this.listAdapter);
                            EditorAlert.this.listAdapter.notifyDataSetChanged();
                            if (currentTop > 0) {
                                EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -currentTop);
                            }
                        }
                        if (EditorAlert.this.searchAdapter != null) {
                            EditorAlert.this.searchAdapter.searchDialogs(obj);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }
                });
                this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        boolean lambda$new$1;
                        lambda$new$1 = ThemeEditorView.EditorAlert.SearchField.this.lambda$new$1(textView, i, keyEvent);
                        return lambda$new$1;
                    }
                });
            }

            public void lambda$new$0(View view) {
                this.searchEditText.setText("");
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent == null) {
                    return false;
                }
                if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(this.searchEditText);
                return false;
            }

            @Override
            public void requestDisallowInterceptTouchEvent(boolean z) {
                super.requestDisallowInterceptTouchEvent(z);
            }

            public void showKeyboard() {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            }
        }

        public EditorAlert(Context context, ArrayList arrayList) {
            super(context, true);
            this.shadow = new View[2];
            this.shadowAnimation = new AnimatorSet[2];
            this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
            FrameLayout frameLayout = new FrameLayout(context) {
                private boolean ignoreLayout = false;
                private RectF rect1 = new RectF();
                private Boolean statusBarOpen;

                private void updateLightStatusBar(boolean z) {
                    Boolean bool = this.statusBarOpen;
                    if (bool == null || bool.booleanValue() != z) {
                        boolean z2 = AndroidUtilities.computePerceivedBrightness(EditorAlert.this.getThemedColor(Theme.key_dialogBackground)) > 0.721f;
                        boolean z3 = AndroidUtilities.computePerceivedBrightness(Theme.blendOver(EditorAlert.this.getThemedColor(Theme.key_actionBarDefault), 855638016)) > 0.721f;
                        this.statusBarOpen = Boolean.valueOf(z);
                        if (!z) {
                            z2 = z3;
                        }
                        AndroidUtilities.setLightStatusBar(EditorAlert.this.getWindow(), z2);
                    }
                }

                @Override
                protected void onDraw(android.graphics.Canvas r14) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.EditorAlert.AnonymousClass1.onDraw(android.graphics.Canvas):void");
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() != 0 || EditorAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= EditorAlert.this.scrollOffsetY) {
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                    EditorAlert.this.dismiss();
                    return true;
                }

                @Override
                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    EditorAlert.this.updateLayout();
                }

                @Override
                protected void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    int i3 = Build.VERSION.SDK_INT;
                    if (i3 >= 21 && !((BottomSheet) EditorAlert.this).isFullscreen) {
                        this.ignoreLayout = true;
                        setPadding(((BottomSheet) EditorAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) EditorAlert.this).backgroundPaddingLeft, 0);
                        this.ignoreLayout = false;
                    }
                    int dp = ((size2 - (i3 >= 21 ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(8.0f)) - Math.min(size, size2 - (i3 >= 21 ? AndroidUtilities.statusBarHeight : 0));
                    if (EditorAlert.this.listView.getPaddingTop() != dp) {
                        this.ignoreLayout = true;
                        EditorAlert.this.listView.getPaddingTop();
                        EditorAlert.this.listView.setPadding(0, dp, 0, AndroidUtilities.dp(48.0f));
                        if (EditorAlert.this.colorPicker.getVisibility() == 0) {
                            EditorAlert editorAlert = EditorAlert.this;
                            editorAlert.setScrollOffsetY(editorAlert.listView.getPaddingTop());
                            EditorAlert.this.previousScrollPosition = 0;
                        }
                        this.ignoreLayout = false;
                    }
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
                }

                @Override
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return !EditorAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
                }

                @Override
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.containerView = frameLayout;
            frameLayout.setWillNotDraw(false);
            ViewGroup viewGroup = this.containerView;
            int i = this.backgroundPaddingLeft;
            viewGroup.setPadding(i, 0, i, 0);
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.frameLayout = frameLayout2;
            frameLayout2.setBackgroundColor(-1);
            SearchField searchField = new SearchField(context);
            this.searchField = searchField;
            this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 51));
            RecyclerListView recyclerListView = new RecyclerListView(context) {
                @Override
                protected boolean allowSelectChildAtPosition(float f, float f2) {
                    return f2 >= ((float) ((EditorAlert.this.scrollOffsetY + AndroidUtilities.dp(48.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
                }
            };
            this.listView = recyclerListView;
            recyclerListView.setSelectorDrawableColor(251658240);
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
            this.listView.setClipToPadding(false);
            RecyclerListView recyclerListView2 = this.listView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            this.layoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.listView.setHorizontalScrollBarEnabled(false);
            this.listView.setVerticalScrollBarEnabled(false);
            this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
            RecyclerListView recyclerListView3 = this.listView;
            ListAdapter listAdapter = new ListAdapter(context, arrayList);
            this.listAdapter = listAdapter;
            recyclerListView3.setAdapter(listAdapter);
            this.searchAdapter = new SearchAdapter(context);
            this.listView.setGlowColor(-657673);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i2) {
                    ThemeEditorView.EditorAlert.this.lambda$new$0(view, i2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                    EditorAlert.this.updateLayout();
                }
            });
            EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
            this.searchEmptyView = emptyTextProgressView;
            emptyTextProgressView.setShowAtCenter(true);
            this.searchEmptyView.showTextView();
            this.searchEmptyView.setText(LocaleController.getString(R.string.NoResult));
            this.listView.setEmptyView(this.searchEmptyView);
            this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
            layoutParams.topMargin = AndroidUtilities.dp(58.0f);
            this.shadow[0] = new View(context);
            this.shadow[0].setBackgroundColor(301989888);
            this.shadow[0].setAlpha(0.0f);
            this.shadow[0].setTag(1);
            this.containerView.addView(this.shadow[0], layoutParams);
            this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
            ColorPicker colorPicker = new ColorPicker(context);
            this.colorPicker = colorPicker;
            colorPicker.setVisibility(8);
            this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
            layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
            this.shadow[1] = new View(context);
            this.shadow[1].setBackgroundColor(301989888);
            this.containerView.addView(this.shadow[1], layoutParams2);
            FrameLayout frameLayout3 = new FrameLayout(context);
            this.bottomSaveLayout = frameLayout3;
            frameLayout3.setBackgroundColor(-1);
            this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
            TextView textView = new TextView(context);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(-15095832);
            textView.setGravity(17);
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView.setText(LocaleController.getString(R.string.CloseEditor).toUpperCase());
            textView.setTypeface(AndroidUtilities.bold());
            this.bottomSaveLayout.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.lambda$new$1(view);
                }
            });
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(-15095832);
            textView2.setGravity(17);
            textView2.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
            textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView2.setText(LocaleController.getString(R.string.SaveTheme).toUpperCase());
            textView2.setTypeface(AndroidUtilities.bold());
            this.bottomSaveLayout.addView(textView2, LayoutHelper.createFrame(-2, -1, 53));
            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.lambda$new$2(view);
                }
            });
            FrameLayout frameLayout4 = new FrameLayout(context);
            this.bottomLayout = frameLayout4;
            frameLayout4.setVisibility(8);
            this.bottomLayout.setBackgroundColor(-1);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            TextView textView3 = new TextView(context);
            textView3.setTextSize(1, 14.0f);
            textView3.setTextColor(-15095832);
            textView3.setGravity(17);
            textView3.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
            textView3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView3.setText(LocaleController.getString(R.string.Cancel).toUpperCase());
            textView3.setTypeface(AndroidUtilities.bold());
            this.bottomLayout.addView(textView3, LayoutHelper.createFrame(-2, -1, 51));
            textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.lambda$new$3(view);
                }
            });
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            this.bottomLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
            TextView textView4 = new TextView(context);
            textView4.setTextSize(1, 14.0f);
            textView4.setTextColor(-15095832);
            textView4.setGravity(17);
            textView4.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
            textView4.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView4.setText(LocaleController.getString(R.string.Default).toUpperCase());
            textView4.setTypeface(AndroidUtilities.bold());
            linearLayout.addView(textView4, LayoutHelper.createFrame(-2, -1, 51));
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.lambda$new$4(view);
                }
            });
            TextView textView5 = new TextView(context);
            textView5.setTextSize(1, 14.0f);
            textView5.setTextColor(-15095832);
            textView5.setGravity(17);
            textView5.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
            textView5.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            textView5.setText(LocaleController.getString(R.string.Save).toUpperCase());
            textView5.setTypeface(AndroidUtilities.bold());
            linearLayout.addView(textView5, LayoutHelper.createFrame(-2, -1, 51));
            textView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ThemeEditorView.EditorAlert.this.lambda$new$5(view);
                }
            });
        }

        public int getCurrentTop() {
            if (this.listView.getChildCount() == 0) {
                return -1000;
            }
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            if (holder == null) {
                return -1000;
            }
            int paddingTop = this.listView.getPaddingTop();
            if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                i = childAt.getTop();
            }
            return paddingTop - i;
        }

        public void lambda$new$0(View view, int i) {
            ThemeEditorView themeEditorView;
            ArrayList item;
            if (i == 0) {
                return;
            }
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            ListAdapter listAdapter = this.listAdapter;
            if (adapter == listAdapter) {
                themeEditorView = ThemeEditorView.this;
                item = listAdapter.getItem(i - 1);
            } else {
                themeEditorView = ThemeEditorView.this;
                item = this.searchAdapter.getItem(i - 1);
            }
            themeEditorView.currentThemeDesription = item;
            ThemeEditorView.this.currentThemeDesriptionPosition = i;
            for (int i2 = 0; i2 < ThemeEditorView.this.currentThemeDesription.size(); i2++) {
                ThemeDescription themeDescription = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(i2);
                if (themeDescription.getCurrentKey() == Theme.key_chat_wallpaper) {
                    ThemeEditorView.this.wallpaperUpdater.showAlert(true);
                    return;
                }
                themeDescription.startEditing();
                if (i2 == 0) {
                    this.colorPicker.setColor(themeDescription.getCurrentColor());
                }
            }
            setColorPickerVisible(true);
        }

        public void lambda$new$1(View view) {
            dismiss();
        }

        public void lambda$new$2(View view) {
            Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, true, false, false);
            setOnDismissListener((DialogInterface.OnDismissListener) null);
            dismiss();
            ThemeEditorView.this.close();
        }

        public void lambda$new$3(View view) {
            for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
                ((ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(i)).setPreviousColor();
            }
            setColorPickerVisible(false);
        }

        public void lambda$new$4(View view) {
            for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
                ((ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(i)).setDefaultColor();
            }
            setColorPickerVisible(false);
        }

        public void lambda$new$5(View view) {
            setColorPickerVisible(false);
        }

        private void runShadowAnimation(final int i, final boolean z) {
            if ((!z || this.shadow[i].getTag() == null) && (z || this.shadow[i].getTag() != null)) {
                return;
            }
            this.shadow[i].setTag(z ? null : 1);
            if (z) {
                this.shadow[i].setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation[i];
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.shadowAnimation[i] = new AnimatorSet();
            this.shadowAnimation[i].playTogether(ObjectAnimator.ofFloat(this.shadow[i], (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
            this.shadowAnimation[i].setDuration(150L);
            this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    if (EditorAlert.this.shadowAnimation[i] == null || !EditorAlert.this.shadowAnimation[i].equals(animator)) {
                        return;
                    }
                    EditorAlert.this.shadowAnimation[i] = null;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (EditorAlert.this.shadowAnimation[i] == null || !EditorAlert.this.shadowAnimation[i].equals(animator)) {
                        return;
                    }
                    if (!z) {
                        EditorAlert.this.shadow[i].setVisibility(4);
                    }
                    EditorAlert.this.shadowAnimation[i] = null;
                }
            });
            this.shadowAnimation[i].start();
        }

        public void setColorPickerVisible(boolean z) {
            if (z) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                this.previousScrollPosition = this.scrollOffsetY;
                AnimatorSet animatorSet = new AnimatorSet();
                ColorPicker colorPicker = this.colorPicker;
                Property property = View.ALPHA;
                animatorSet.playTogether(ObjectAnimator.ofFloat(colorPicker, (Property<ColorPicker, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.bottomLayout, (Property<FrameLayout, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.listView, (Property<RecyclerListView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.frameLayout, (Property<FrameLayout, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.shadow[0], (Property<View, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.searchEmptyView, (Property<EmptyTextProgressView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.bottomSaveLayout, (Property<FrameLayout, Float>) property, 0.0f), ObjectAnimator.ofInt(this, "scrollOffsetY", this.listView.getPaddingTop()));
                animatorSet.setDuration(150L);
                animatorSet.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        EditorAlert.this.listView.setVisibility(4);
                        EditorAlert.this.searchField.setVisibility(4);
                        EditorAlert.this.bottomSaveLayout.setVisibility(4);
                        EditorAlert.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                return;
            }
            if (ThemeEditorView.this.parentActivity != null) {
                ((LaunchActivity) ThemeEditorView.this.parentActivity).rebuildAllFragments(false);
            }
            Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, false, false, false);
            if (this.listView.getAdapter() == this.listAdapter) {
                AndroidUtilities.hideKeyboard(getCurrentFocus());
            }
            this.animationInProgress = true;
            this.listView.setVisibility(0);
            this.bottomSaveLayout.setVisibility(0);
            this.searchField.setVisibility(0);
            this.listView.setAlpha(0.0f);
            AnimatorSet animatorSet2 = new AnimatorSet();
            ColorPicker colorPicker2 = this.colorPicker;
            Property property2 = View.ALPHA;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(colorPicker2, (Property<ColorPicker, Float>) property2, 0.0f);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.bottomLayout, (Property<FrameLayout, Float>) property2, 0.0f);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.listView, (Property<RecyclerListView, Float>) property2, 1.0f);
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.frameLayout, (Property<FrameLayout, Float>) property2, 1.0f);
            View view = this.shadow[0];
            animatorSet2.playTogether(ofFloat, ofFloat2, ofFloat3, ofFloat4, ObjectAnimator.ofFloat(view, (Property<View, Float>) property2, view.getTag() == null ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.searchEmptyView, (Property<EmptyTextProgressView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.bottomSaveLayout, (Property<FrameLayout, Float>) property2, 1.0f), ObjectAnimator.ofInt(this, "scrollOffsetY", this.previousScrollPosition));
            animatorSet2.setDuration(150L);
            animatorSet2.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (EditorAlert.this.listView.getAdapter() == EditorAlert.this.searchAdapter) {
                        EditorAlert.this.searchField.showKeyboard();
                    }
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet2.start();
            this.listView.getAdapter().notifyItemChanged(ThemeEditorView.this.currentThemeDesriptionPosition);
        }

        public void updateLayout() {
            if (this.listView.getChildCount() <= 0 || this.listView.getVisibility() != 0 || this.animationInProgress) {
                return;
            }
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int paddingTop = (this.listView.getVisibility() != 0 || this.animationInProgress) ? this.listView.getPaddingTop() : childAt.getTop() - AndroidUtilities.dp(8.0f);
            if (paddingTop <= (-AndroidUtilities.dp(1.0f)) || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(0, true);
            } else {
                runShadowAnimation(0, false);
                i = paddingTop;
            }
            if (this.scrollOffsetY != i) {
                setScrollOffsetY(i);
            }
        }

        @Override
        public boolean canDismissWithSwipe() {
            return false;
        }

        @Override
        public void dismissInternal() {
            super.dismissInternal();
            if (this.searchField.searchEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.searchField.searchEditText);
            }
        }

        public void setScrollOffsetY(int i) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = i;
            recyclerListView.setTopGlowOffset(i);
            this.frameLayout.setTranslationY(this.scrollOffsetY);
            this.colorPicker.setTranslationY(this.scrollOffsetY);
            this.searchEmptyView.setTranslationY(this.scrollOffsetY);
            this.containerView.invalidate();
        }
    }

    public void animateToBoundsMaybe() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeEditorView.animateToBoundsMaybe():void");
    }

    public static ThemeEditorView getInstance() {
        return Instance;
    }

    private static int getSideCoord(boolean z, int i, float f, int i2) {
        int i3;
        android.graphics.Point point = AndroidUtilities.displaySize;
        if (z) {
            i3 = point.x;
        } else {
            i3 = point.y - i2;
            i2 = ActionBar.getCurrentActionBarHeight();
        }
        int dp = i == 0 ? AndroidUtilities.dp(10.0f) : i == 1 ? (i3 - i2) - AndroidUtilities.dp(10.0f) : Math.round((r0 - AndroidUtilities.dp(20.0f)) * f) + AndroidUtilities.dp(10.0f);
        return !z ? dp + ActionBar.getCurrentActionBarHeight() : dp;
    }

    public void hide() {
        if (this.parentActivity == null) {
            return;
        }
        try {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.windowView, (Property<FrameLayout, Float>) View.ALPHA, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.windowView, (Property<FrameLayout, Float>) View.SCALE_X, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.windowView, (Property<FrameLayout, Float>) View.SCALE_Y, 1.0f, 0.0f));
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150L);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (ThemeEditorView.this.windowView != null) {
                        ThemeEditorView.this.windowView.setBackground(null);
                        ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
                    }
                }
            });
            animatorSet.start();
            this.hidden = true;
        } catch (Exception unused) {
        }
    }

    public void show() {
        if (this.parentActivity == null) {
            return;
        }
        AndroidUtilities.setPreferredMaxRefreshRate(this.windowManager, this.windowView, this.windowLayoutParams);
        try {
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.hidden = false;
            showWithAnimation();
        } catch (Exception unused) {
        }
    }

    private void showWithAnimation() {
        this.windowView.setBackgroundResource(R.drawable.theme_picker);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.windowView, (Property<FrameLayout, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.windowView, (Property<FrameLayout, Float>) View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.windowView, (Property<FrameLayout, Float>) View.SCALE_Y, 0.0f, 1.0f));
        animatorSet.setInterpolator(this.decelerateInterpolator);
        animatorSet.setDuration(150L);
        animatorSet.start();
    }

    public void close() {
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception unused) {
        }
        this.parentActivity = null;
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.wallpaperUpdater.cleanup();
        if (this.parentActivity == null || (frameLayout = this.windowView) == null) {
            return;
        }
        try {
            this.windowManager.removeViewImmediate(frameLayout);
            this.windowView = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
        }
        try {
            EditorAlert editorAlert = this.editorAlert;
            if (editorAlert != null) {
                editorAlert.dismiss();
                this.editorAlert = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        this.parentActivity = null;
        Instance = null;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        WallpaperUpdater wallpaperUpdater = this.wallpaperUpdater;
        if (wallpaperUpdater != null) {
            wallpaperUpdater.onActivityResult(i, i2, intent);
        }
    }

    public void onConfigurationChanged() {
        int i = this.preferences.getInt("sidex", 1);
        int i2 = this.preferences.getInt("sidey", 0);
        float f = this.preferences.getFloat("px", 0.0f);
        float f2 = this.preferences.getFloat("py", 0.0f);
        this.windowLayoutParams.x = getSideCoord(true, i, f, this.editorWidth);
        this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.editorHeight);
        try {
            if (this.windowView.getParent() != null) {
                this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void show(Activity activity, Theme.ThemeInfo themeInfo) {
        if (Instance != null) {
            Instance.destroy();
        }
        this.hidden = false;
        this.themeInfo = themeInfo;
        this.windowView = new AnonymousClass1(activity);
        this.windowManager = (WindowManager) activity.getSystemService("window");
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        this.preferences = sharedPreferences;
        int i = sharedPreferences.getInt("sidex", 1);
        int i2 = this.preferences.getInt("sidey", 0);
        float f = this.preferences.getFloat("px", 0.0f);
        float f2 = this.preferences.getFloat("py", 0.0f);
        try {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
            int i3 = this.editorWidth;
            layoutParams.width = i3;
            layoutParams.height = this.editorHeight;
            layoutParams.x = getSideCoord(true, i, f, i3);
            this.windowLayoutParams.y = getSideCoord(false, i2, f2, this.editorHeight);
            WindowManager.LayoutParams layoutParams2 = this.windowLayoutParams;
            layoutParams2.format = -3;
            layoutParams2.gravity = 51;
            layoutParams2.type = 99;
            layoutParams2.flags = 16777736;
            AndroidUtilities.setPreferredMaxRefreshRate(this.windowManager, this.windowView, layoutParams2);
            this.windowManager.addView(this.windowView, this.windowLayoutParams);
            this.wallpaperUpdater = new WallpaperUpdater(activity, null, new WallpaperUpdater.WallpaperUpdaterDelegate() {
                @Override
                public void didSelectWallpaper(File file, Bitmap bitmap, boolean z) {
                    Theme.setThemeWallpaper(ThemeEditorView.this.themeInfo, bitmap, file);
                }

                @Override
                public void needOpenColorPicker() {
                    for (int i4 = 0; i4 < ThemeEditorView.this.currentThemeDesription.size(); i4++) {
                        ThemeDescription themeDescription = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(i4);
                        themeDescription.startEditing();
                        if (i4 == 0) {
                            ThemeEditorView.this.editorAlert.colorPicker.setColor(themeDescription.getCurrentColor());
                        }
                    }
                    ThemeEditorView.this.editorAlert.setColorPickerVisible(true);
                }
            });
            Instance = this;
            this.parentActivity = activity;
            showWithAnimation();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
