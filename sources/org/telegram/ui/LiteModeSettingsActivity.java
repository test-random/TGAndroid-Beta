package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BatteryDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.LiteModeSettingsActivity;

public class LiteModeSettingsActivity extends BaseFragment {
    private int FLAGS_CHAT;
    Adapter adapter;
    FrameLayout contentView;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    Bulletin restrictBulletin;
    private Utilities.Callback onPowerAppliedChange = new Utilities.Callback() {
        @Override
        public final void run(Object obj) {
            LiteModeSettingsActivity.this.lambda$new$1((Boolean) obj);
        }
    };
    private boolean[] expanded = new boolean[3];
    private ArrayList oldItems = new ArrayList();
    private ArrayList items = new ArrayList();

    public class Adapter extends AdapterWithDiffUtils {
        private Adapter() {
        }

        @Override
        public int getItemCount() {
            return LiteModeSettingsActivity.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            if (i < 0 || i >= LiteModeSettingsActivity.this.items.size()) {
                return 2;
            }
            return ((Item) LiteModeSettingsActivity.this.items.get(i)).viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 4 || viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 5;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            Drawable drawable;
            Context context;
            int i2;
            if (i < 0 || i >= LiteModeSettingsActivity.this.items.size()) {
                return;
            }
            Item item = (Item) LiteModeSettingsActivity.this.items.get(i);
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((HeaderCell) viewHolder.itemView).setText(item.text);
                return;
            }
            if (itemViewType == 1) {
                ((PowerSaverSlider) viewHolder.itemView).update();
                return;
            }
            if (itemViewType != 2) {
                if (itemViewType == 3 || itemViewType == 4) {
                    int i3 = i + 1;
                    ((SwitchCell) viewHolder.itemView).set(item, i3 < LiteModeSettingsActivity.this.items.size() && ((Item) LiteModeSettingsActivity.this.items.get(i3)).viewType != 2);
                    return;
                } else {
                    if (itemViewType == 5) {
                        TextCell textCell = (TextCell) viewHolder.itemView;
                        if (item.type == 1) {
                            textCell.setTextAndCheck(item.text, MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), false);
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
            if (TextUtils.isEmpty(item.text)) {
                textInfoPrivacyCell.setFixedSize(12);
            } else {
                textInfoPrivacyCell.setFixedSize(0);
            }
            textInfoPrivacyCell.setText(item.text);
            textInfoPrivacyCell.setContentDescription(item.text);
            boolean z = i > 0 && ((Item) LiteModeSettingsActivity.this.items.get(i + (-1))).viewType != 2;
            int i4 = i + 1;
            boolean z2 = i4 < LiteModeSettingsActivity.this.items.size() && ((Item) LiteModeSettingsActivity.this.items.get(i4)).viewType != 2;
            if (z && z2) {
                context = LiteModeSettingsActivity.this.getContext();
                i2 = R.drawable.greydivider;
            } else if (z) {
                context = LiteModeSettingsActivity.this.getContext();
                i2 = R.drawable.greydivider_bottom;
            } else if (!z2) {
                drawable = null;
                textInfoPrivacyCell.setBackground(drawable);
            } else {
                context = LiteModeSettingsActivity.this.getContext();
                i2 = R.drawable.greydivider_top;
            }
            drawable = Theme.getThemedDrawableByKey(context, i2, Theme.key_windowBackgroundGrayShadow);
            textInfoPrivacyCell.setBackground(drawable);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View switchCell;
            Context context = viewGroup.getContext();
            if (i == 0) {
                switchCell = new HeaderCell(context);
            } else {
                if (i != 1) {
                    if (i == 2) {
                        switchCell = new TextInfoPrivacyCell(context) {
                            @Override
                            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                                accessibilityNodeInfo.setEnabled(true);
                            }

                            @Override
                            public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
                                super.onPopulateAccessibilityEvent(accessibilityEvent);
                                accessibilityEvent.setContentDescription(getTextView().getText());
                                setContentDescription(getTextView().getText());
                            }
                        };
                    } else if (i == 3 || i == 4) {
                        switchCell = new SwitchCell(context);
                    } else if (i == 5) {
                        switchCell = new TextCell(context, 23, false, true, null);
                    } else {
                        switchCell = null;
                    }
                    return new RecyclerListView.Holder(switchCell);
                }
                switchCell = new PowerSaverSlider(context);
            }
            switchCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(switchCell);
        }
    }

    public static class Item extends AdapterWithDiffUtils.Item {
        public int flags;
        public int iconResId;
        public CharSequence text;
        public int type;

        private Item(int i, CharSequence charSequence, int i2, int i3, int i4) {
            super(i, false);
            this.text = charSequence;
            this.iconResId = i2;
            this.flags = i3;
            this.type = i4;
        }

        public static Item asCheckbox(CharSequence charSequence, int i) {
            return new Item(4, charSequence, 0, i, 0);
        }

        public static Item asHeader(CharSequence charSequence) {
            return new Item(0, charSequence, 0, 0, 0);
        }

        public static Item asInfo(CharSequence charSequence) {
            return new Item(2, charSequence, 0, 0, 0);
        }

        public static Item asSlider() {
            return new Item(1, null, 0, 0, 0);
        }

        public static Item asSwitch(int i, CharSequence charSequence, int i2) {
            return new Item(3, charSequence, i, i2, 0);
        }

        public static Item asSwitch(CharSequence charSequence, int i) {
            return new Item(5, charSequence, 0, 0, i);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item item = (Item) obj;
            int i = item.viewType;
            int i2 = this.viewType;
            if (i != i2) {
                return false;
            }
            if (i2 == 3 && item.iconResId != this.iconResId) {
                return false;
            }
            if (i2 == 5 && item.type != this.type) {
                return false;
            }
            if ((i2 == 3 || i2 == 4) && item.flags != this.flags) {
                return false;
            }
            return !(i2 == 0 || i2 == 2 || i2 == 3 || i2 == 4 || i2 == 5) || TextUtils.equals(item.text, this.text);
        }

        public int getFlagsCount() {
            return Integer.bitCount(this.flags);
        }
    }

    public class PowerSaverSlider extends FrameLayout {
        BatteryDrawable batteryIcon;
        SpannableStringBuilder batteryText;
        LinearLayout headerLayout;
        AnimatedTextView headerOnView;
        private boolean headerOnVisible;
        TextView headerTextView;
        TextView leftTextView;
        AnimatedTextView middleTextView;
        private ValueAnimator offActiveAnimator;
        private float offActiveT;
        private ValueAnimator onActiveAnimator;
        private float onActiveT;
        TextView rightTextView;
        private SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
        SeekBarView seekBarView;
        FrameLayout valuesView;

        public PowerSaverSlider(Context context) {
            super(context);
            LinearLayout linearLayout = new LinearLayout(context);
            this.headerLayout = linearLayout;
            linearLayout.setGravity(LocaleController.isRTL ? 5 : 3);
            this.headerLayout.setImportantForAccessibility(4);
            TextView textView = new TextView(context);
            this.headerTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.headerTextView.setTypeface(AndroidUtilities.bold());
            TextView textView2 = this.headerTextView;
            int i = Theme.key_windowBackgroundWhiteBlueHeader;
            textView2.setTextColor(Theme.getColor(i));
            this.headerTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.headerTextView.setText(LocaleController.getString("LiteBatteryTitle"));
            this.headerLayout.addView(this.headerTextView, LayoutHelper.createLinear(-2, -2, 16));
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, false, false) {
                Drawable backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader), 0.15f));

                @Override
                public void onDraw(Canvas canvas) {
                    this.backgroundDrawable.setBounds(0, 0, (int) (getPaddingLeft() + getDrawable().getCurrentWidth() + getPaddingRight()), getMeasuredHeight());
                    this.backgroundDrawable.draw(canvas);
                    super.onDraw(canvas);
                }
            };
            this.headerOnView = animatedTextView;
            animatedTextView.setTypeface(AndroidUtilities.bold());
            this.headerOnView.setPadding(AndroidUtilities.dp(5.33f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(5.33f), AndroidUtilities.dp(2.0f));
            this.headerOnView.setTextSize(AndroidUtilities.dp(12.0f));
            this.headerOnView.setTextColor(Theme.getColor(i));
            this.headerLayout.addView(this.headerOnView, LayoutHelper.createLinear(-2, 17, 16, 6, 1, 0, 0));
            addView(this.headerLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 21.0f, 17.0f, 21.0f, 0.0f));
            SeekBarView seekBarView = new SeekBarView(context, true, null);
            this.seekBarView = seekBarView;
            seekBarView.setReportChanges(true);
            this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public CharSequence getContentDescription() {
                    return " ";
                }

                @Override
                public int getStepsCount() {
                    return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
                }

                @Override
                public void onSeekBarDrag(boolean z, float f) {
                    int round = Math.round(f * 100.0f);
                    if (round != LiteMode.getPowerSaverLevel()) {
                        LiteMode.setPowerSaverLevel(round);
                        LiteModeSettingsActivity.this.updateValues();
                        LiteModeSettingsActivity.this.updateInfo();
                        if (round <= 0 || round >= 100) {
                            try {
                                PowerSaverSlider.this.performHapticFeedback(3, 1);
                            } catch (Exception unused) {
                            }
                        }
                    }
                }

                @Override
                public void onSeekBarPressed(boolean z) {
                }
            });
            this.seekBarView.setProgress(LiteMode.getPowerSaverLevel() / 100.0f);
            this.seekBarView.setImportantForAccessibility(2);
            addView(this.seekBarView, LayoutHelper.createFrame(-1, 44.0f, 48, 6.0f, 68.0f, 6.0f, 0.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.valuesView = frameLayout;
            frameLayout.setImportantForAccessibility(4);
            TextView textView3 = new TextView(context);
            this.leftTextView = textView3;
            textView3.setTextSize(1, 13.0f);
            TextView textView4 = this.leftTextView;
            int i2 = Theme.key_windowBackgroundWhiteGrayText;
            textView4.setTextColor(Theme.getColor(i2));
            this.leftTextView.setGravity(3);
            this.leftTextView.setText(LocaleController.getString(R.string.LiteBatteryDisabled));
            this.valuesView.addView(this.leftTextView, LayoutHelper.createFrame(-2, -2, 19));
            AnimatedTextView animatedTextView2 = new AnimatedTextView(context, false, true, true) {
                @Override
                public void onMeasure(int i3, int i4) {
                    int size = View.MeasureSpec.getSize(i3);
                    if (size <= 0) {
                        size = AndroidUtilities.displaySize.x - AndroidUtilities.dp(20.0f);
                    }
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) ((size - PowerSaverSlider.this.leftTextView.getPaint().measureText(PowerSaverSlider.this.leftTextView.getText().toString())) - PowerSaverSlider.this.rightTextView.getPaint().measureText(PowerSaverSlider.this.rightTextView.getText().toString())), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), 1073741824));
                }
            };
            this.middleTextView = animatedTextView2;
            animatedTextView2.setAnimationProperties(0.45f, 0L, 240L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.middleTextView.setGravity(1);
            this.middleTextView.setTextSize(AndroidUtilities.dp(13.0f));
            this.middleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            this.valuesView.addView(this.middleTextView, LayoutHelper.createFrame(-2, -2, 17));
            this.batteryText = new SpannableStringBuilder("b");
            BatteryDrawable batteryDrawable = new BatteryDrawable();
            this.batteryIcon = batteryDrawable;
            batteryDrawable.colorFromPaint(this.middleTextView.getPaint());
            this.batteryIcon.setTranslationY(AndroidUtilities.dp(1.5f));
            this.batteryIcon.setBounds(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(-20.0f), AndroidUtilities.dp(23.0f), 0);
            this.batteryText.setSpan(new ImageSpan(this.batteryIcon, 0), 0, this.batteryText.length(), 33);
            TextView textView5 = new TextView(context);
            this.rightTextView = textView5;
            textView5.setTextSize(1, 13.0f);
            this.rightTextView.setTextColor(Theme.getColor(i2));
            this.rightTextView.setGravity(5);
            this.rightTextView.setText(LocaleController.getString(R.string.LiteBatteryEnabled));
            this.valuesView.addView(this.rightTextView, LayoutHelper.createFrame(-2, -2, 21));
            addView(this.valuesView, LayoutHelper.createFrame(-1, -2.0f, 55, 21.0f, 52.0f, 21.0f, 0.0f));
            this.seekBarAccessibilityDelegate = new IntSeekBarAccessibilityDelegate() {
                @Override
                protected int getDelta() {
                    return 5;
                }

                @Override
                protected int getMaxValue() {
                    return 100;
                }

                @Override
                protected int getProgress() {
                    return LiteMode.getPowerSaverLevel();
                }

                @Override
                public void onInitializeAccessibilityNodeInfoInternal(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfoInternal(view, accessibilityNodeInfo);
                    accessibilityNodeInfo.setEnabled(true);
                }

                @Override
                public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
                    String formatString;
                    int i3;
                    super.onPopulateAccessibilityEvent(view, accessibilityEvent);
                    StringBuilder sb = new StringBuilder(LocaleController.getString(R.string.LiteBatteryTitle));
                    sb.append(", ");
                    int powerSaverLevel = LiteMode.getPowerSaverLevel();
                    if (powerSaverLevel <= 0) {
                        i3 = R.string.LiteBatteryAlwaysDisabled;
                    } else {
                        if (powerSaverLevel < 100) {
                            formatString = LocaleController.formatString(R.string.AccDescrLiteBatteryWhenBelow, Integer.valueOf(Math.round(powerSaverLevel)));
                            sb.append(formatString);
                            accessibilityEvent.setContentDescription(sb);
                            PowerSaverSlider.this.setContentDescription(sb);
                        }
                        i3 = R.string.LiteBatteryAlwaysEnabled;
                    }
                    formatString = LocaleController.getString(i3);
                    sb.append(formatString);
                    accessibilityEvent.setContentDescription(sb);
                    PowerSaverSlider.this.setContentDescription(sb);
                }

                @Override
                protected void setProgress(int i3) {
                    float f = i3 / 100.0f;
                    PowerSaverSlider.this.seekBarView.delegate.onSeekBarDrag(true, f);
                    PowerSaverSlider.this.seekBarView.setProgress(f);
                }
            };
            update();
        }

        public void lambda$updateOffActive$1(ValueAnimator valueAnimator) {
            TextView textView = this.leftTextView;
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
            int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.offActiveT = floatValue;
            textView.setTextColor(ColorUtils.blendARGB(color, color2, floatValue));
        }

        public void lambda$updateOnActive$0(ValueAnimator valueAnimator) {
            TextView textView = this.rightTextView;
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
            int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.onActiveT = floatValue;
            textView.setTextColor(ColorUtils.blendARGB(color, color2, floatValue));
        }

        private void updateHeaderOnVisibility(boolean z) {
            if (z != this.headerOnVisible) {
                this.headerOnVisible = z;
                this.headerOnView.clearAnimation();
                this.headerOnView.animate().alpha(z ? 1.0f : 0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(220L).start();
            }
        }

        private void updateOffActive(boolean z) {
            final float f = z ? 1.0f : 0.0f;
            if (this.offActiveT != f) {
                this.offActiveT = f;
                ValueAnimator valueAnimator = this.offActiveAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.offActiveAnimator = null;
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.offActiveT, f);
                this.offActiveAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        LiteModeSettingsActivity.PowerSaverSlider.this.lambda$updateOffActive$1(valueAnimator2);
                    }
                });
                this.offActiveAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        PowerSaverSlider.this.leftTextView.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), PowerSaverSlider.this.offActiveT = f));
                    }
                });
                this.offActiveAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.offActiveAnimator.setDuration(320L);
                this.offActiveAnimator.start();
            }
        }

        private void updateOnActive(boolean z) {
            final float f = z ? 1.0f : 0.0f;
            if (this.onActiveT != f) {
                this.onActiveT = f;
                ValueAnimator valueAnimator = this.onActiveAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.onActiveAnimator = null;
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.onActiveT, f);
                this.onActiveAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        LiteModeSettingsActivity.PowerSaverSlider.this.lambda$updateOnActive$0(valueAnimator2);
                    }
                });
                this.onActiveAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        PowerSaverSlider.this.rightTextView.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), PowerSaverSlider.this.onActiveT = f));
                    }
                });
                this.onActiveAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.onActiveAnimator.setDuration(320L);
                this.onActiveAnimator.start();
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.seekBarAccessibilityDelegate.onInitializeAccessibilityNodeInfo(this, accessibilityNodeInfo);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(112.0f), 1073741824));
        }

        @Override
        public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(accessibilityEvent);
            this.seekBarAccessibilityDelegate.onPopulateAccessibilityEvent(this, accessibilityEvent);
        }

        @Override
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return this.seekBarAccessibilityDelegate.performAccessibilityAction(this, i, bundle);
        }

        public void update() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LiteModeSettingsActivity.PowerSaverSlider.update():void");
        }
    }

    public class SwitchCell extends FrameLayout {
        private int all;
        private ImageView arrowView;
        private CheckBox2 checkBoxView;
        private boolean containing;
        private AnimatedTextView countTextView;
        private boolean disabled;
        private int enabled;
        private ImageView imageView;
        private boolean needDivider;
        private boolean needLine;
        private Switch switchView;
        private TextView textView;
        private LinearLayout textViewLayout;

        public SwitchCell(Context context) {
            super(context);
            setImportantForAccessibility(1);
            int i = Theme.key_windowBackgroundWhite;
            setBackgroundColor(Theme.getColor(i));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
            this.imageView.setVisibility(8);
            addView(this.imageView, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 16, 20.0f, 0.0f, 20.0f, 0.0f));
            TextView textView = new TextView(context) {
                @Override
                protected void onMeasure(int i2, int i3) {
                    if (View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE) {
                        i2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(52.0f), Integer.MIN_VALUE);
                    }
                    super.onMeasure(i2, i3);
                }
            };
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextSize(1, 16.0f);
            TextView textView2 = this.textView;
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            textView2.setTextColor(Theme.getColor(i2));
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setImportantForAccessibility(2);
            AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, true);
            this.countTextView = animatedTextView;
            animatedTextView.setAnimationProperties(0.35f, 0L, 200L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.countTextView.setTypeface(AndroidUtilities.bold());
            this.countTextView.setTextSize(AndroidUtilities.dp(14.0f));
            this.countTextView.setTextColor(Theme.getColor(i2));
            this.countTextView.setImportantForAccessibility(2);
            ImageView imageView2 = new ImageView(context);
            this.arrowView = imageView2;
            imageView2.setVisibility(8);
            this.arrowView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), mode));
            this.arrowView.setImageResource(R.drawable.arrow_more);
            LinearLayout linearLayout = new LinearLayout(context);
            this.textViewLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.textViewLayout.setGravity(LocaleController.isRTL ? 5 : 3);
            if (LocaleController.isRTL) {
                this.textViewLayout.addView(this.arrowView, LayoutHelper.createLinear(16, 16, 0.0f, 16, 0, 0, 6, 0));
                this.textViewLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 0, 0, 6, 0));
                this.textViewLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
            } else {
                this.textViewLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
                this.textViewLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 6, 0, 0, 0));
                this.textViewLayout.addView(this.arrowView, LayoutHelper.createLinear(16, 16, 0.0f, 16, 2, 0, 0, 0));
            }
            addView(this.textViewLayout, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, 64.0f, 0.0f, 8.0f, 0.0f));
            Switch r5 = new Switch(context);
            this.switchView = r5;
            r5.setVisibility(8);
            this.switchView.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, i, i);
            this.switchView.setImportantForAccessibility(2);
            addView(this.switchView, LayoutHelper.createFrame(37, 50.0f, (LocaleController.isRTL ? 3 : 5) | 16, 19.0f, 0.0f, 19.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBoxView = checkBox2;
            checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
            this.checkBoxView.setDrawUnchecked(true);
            this.checkBoxView.setChecked(true, false);
            this.checkBoxView.setDrawBackgroundAsArc(10);
            this.checkBoxView.setVisibility(8);
            this.checkBoxView.setImportantForAccessibility(2);
            CheckBox2 checkBox22 = this.checkBoxView;
            boolean z = LocaleController.isRTL;
            addView(checkBox22, LayoutHelper.createFrame(21, 21.0f, (z ? 5 : 3) | 16, z ? 0.0f : 64.0f, 0.0f, z ? 64.0f : 0.0f, 0.0f));
            setFocusable(true);
        }

        private int preprocessFlagsCount(int r4) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LiteModeSettingsActivity.SwitchCell.preprocessFlagsCount(int):int");
        }

        private void updateCount(Item item, boolean z) {
            this.enabled = preprocessFlagsCount(LiteMode.getValue(true) & item.flags);
            this.all = preprocessFlagsCount(item.flags);
            this.countTextView.setText(String.format("%d/%d", Integer.valueOf(this.enabled), Integer.valueOf(this.all)), z && !LocaleController.isRTL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (LocaleController.isRTL) {
                if (this.needLine) {
                    float dp = AndroidUtilities.dp(75.0f);
                    canvas.drawRect(dp - AndroidUtilities.dp(0.66f), (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2.0f, dp, (getMeasuredHeight() + AndroidUtilities.dp(20.0f)) / 2.0f, Theme.dividerPaint);
                }
                if (this.needDivider) {
                    canvas.drawLine((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) + (this.textView.getTranslationX() < 0.0f ? AndroidUtilities.dp(-32.0f) : 0), getMeasuredHeight() - 1, 0.0f, getMeasuredHeight() - 1, Theme.dividerPaint);
                    return;
                }
                return;
            }
            if (this.needLine) {
                float measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(75.0f);
                canvas.drawRect(measuredWidth - AndroidUtilities.dp(0.66f), (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2.0f, measuredWidth, (getMeasuredHeight() + AndroidUtilities.dp(20.0f)) / 2.0f, Theme.dividerPaint);
            }
            if (this.needDivider) {
                canvas.drawLine(AndroidUtilities.dp(64.0f) + this.textView.getTranslationX(), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(this.checkBoxView.getVisibility() == 0 ? "android.widget.CheckBox" : "android.widget.Switch");
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
            accessibilityNodeInfo.setChecked(this.checkBoxView.getVisibility() == 0 ? this.checkBoxView.isChecked() : this.switchView.isChecked());
            StringBuilder sb = new StringBuilder();
            sb.append(this.textView.getText());
            if (this.containing) {
                sb.append('\n');
                sb.append(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.enabled), Integer.valueOf(this.all)));
            }
            accessibilityNodeInfo.setContentDescription(sb);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }

        public void set(Item item, boolean z) {
            float f;
            if (item.viewType == 3) {
                this.checkBoxView.setVisibility(8);
                this.imageView.setVisibility(0);
                this.imageView.setImageResource(item.iconResId);
                this.textView.setText(item.text);
                boolean z2 = item.getFlagsCount() > 1;
                this.containing = z2;
                if (z2) {
                    updateCount(item, false);
                    this.countTextView.setVisibility(0);
                    this.arrowView.setVisibility(0);
                } else {
                    this.countTextView.setVisibility(8);
                    this.arrowView.setVisibility(8);
                }
                this.textView.setTranslationX(0.0f);
                this.switchView.setVisibility(0);
                this.switchView.setChecked(LiteMode.isEnabled(item.flags), false);
                this.needLine = item.getFlagsCount() > 1;
            } else {
                this.checkBoxView.setVisibility(0);
                this.checkBoxView.setChecked(LiteMode.isEnabled(item.flags), false);
                this.imageView.setVisibility(8);
                this.switchView.setVisibility(8);
                this.countTextView.setVisibility(8);
                this.arrowView.setVisibility(8);
                this.textView.setText(item.text);
                this.textView.setTranslationX(AndroidUtilities.dp(41.0f) * (LocaleController.isRTL ? -2.2f : 1.0f));
                this.containing = false;
                this.needLine = false;
            }
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.textViewLayout.getLayoutParams();
            if (item.viewType == 3) {
                f = (LocaleController.isRTL ? 64 : 75) + 4;
            } else {
                f = 8.0f;
            }
            marginLayoutParams.rightMargin = AndroidUtilities.dp(f);
            this.needDivider = z;
            setWillNotDraw((z || this.needLine) ? false : true);
            setDisabled(LiteMode.isPowerSaverApplied(), false);
        }

        public void setDisabled(boolean z, boolean z2) {
            if (this.disabled != z) {
                this.disabled = z;
                if (z2) {
                    this.imageView.animate().alpha(z ? 0.5f : 1.0f).setDuration(220L).start();
                    this.textViewLayout.animate().alpha(z ? 0.5f : 1.0f).setDuration(220L).start();
                    this.switchView.animate().alpha(z ? 0.5f : 1.0f).setDuration(220L).start();
                    this.checkBoxView.animate().alpha(z ? 0.5f : 1.0f).setDuration(220L).start();
                } else {
                    this.imageView.setAlpha(z ? 0.5f : 1.0f);
                    this.textViewLayout.setAlpha(z ? 0.5f : 1.0f);
                    this.switchView.setAlpha(z ? 0.5f : 1.0f);
                    this.checkBoxView.setAlpha(z ? 0.5f : 1.0f);
                }
                setEnabled(!z);
            }
        }

        public void update(Item item) {
            if (item.viewType == 3) {
                boolean z = item.getFlagsCount() > 1;
                this.containing = z;
                if (z) {
                    updateCount(item, true);
                    int expandedIndex = LiteModeSettingsActivity.this.getExpandedIndex(item.flags);
                    this.arrowView.clearAnimation();
                    this.arrowView.animate().rotation((expandedIndex < 0 || !LiteModeSettingsActivity.this.expanded[expandedIndex]) ? 0.0f : 180.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(240L).start();
                }
                this.switchView.setChecked(LiteMode.isEnabled(item.flags), true);
            } else {
                this.checkBoxView.setChecked(LiteMode.isEnabled(item.flags), true);
            }
            setDisabled(LiteMode.isPowerSaverApplied(), true);
        }
    }

    public int getExpandedIndex(int i) {
        if (i == 3) {
            return 0;
        }
        if (i == 28700) {
            return 1;
        }
        return i == this.FLAGS_CHAT ? 2 : -1;
    }

    private void highlightRow(final int i) {
        this.listView.highlightRow(new RecyclerListView.IntReturnCallback() {
            @Override
            public final int run() {
                int lambda$highlightRow$2;
                lambda$highlightRow$2 = LiteModeSettingsActivity.this.lambda$highlightRow$2(i);
                return lambda$highlightRow$2;
            }
        });
    }

    public void lambda$createView$0(View view, int i, float f, float f2) {
        int expandedIndex;
        if (view == null || i < 0 || i >= this.items.size()) {
            return;
        }
        Item item = (Item) this.items.get(i);
        int i2 = item.viewType;
        if (i2 != 3 && i2 != 4) {
            if (i2 == 5 && item.type == 1) {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                boolean z = globalMainSettings.getBoolean("view_animations", true);
                SharedPreferences.Editor edit = globalMainSettings.edit();
                boolean z2 = !z;
                edit.putBoolean("view_animations", z2);
                SharedConfig.setAnimationsEnabled(z2);
                edit.commit();
                ((TextCell) view).setChecked(z2);
                return;
            }
            return;
        }
        if (LiteMode.isPowerSaverApplied()) {
            this.restrictBulletin = BulletinFactory.of(this).createSimpleBulletin(new BatteryDrawable(0.1f, -1, Theme.getColor(Theme.key_dialogSwipeRemove), 1.3f), LocaleController.getString(R.string.LiteBatteryRestricted)).show();
            return;
        }
        if (item.viewType != 3 || item.getFlagsCount() <= 1 || (!LocaleController.isRTL ? f < view.getMeasuredWidth() - AndroidUtilities.dp(75.0f) : f > AndroidUtilities.dp(75.0f)) || (expandedIndex = getExpandedIndex(item.flags)) == -1) {
            LiteMode.toggleFlag(item.flags, !LiteMode.isEnabledSetting(item.flags));
            updateValues();
        } else {
            this.expanded[expandedIndex] = !r5[expandedIndex];
            updateValues();
            updateItems();
        }
    }

    public int lambda$highlightRow$2(int i) {
        this.layoutManager.scrollToPositionWithOffset(i, AndroidUtilities.dp(60.0f));
        return i;
    }

    public void lambda$new$1(Boolean bool) {
        updateValues();
    }

    public void updateInfo() {
        String formatString;
        int i;
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (this.items.isEmpty()) {
            updateItems();
            return;
        }
        if (this.items.size() >= 2) {
            ArrayList arrayList = this.items;
            if (LiteMode.getPowerSaverLevel() <= 0) {
                i = R.string.LiteBatteryInfoDisabled;
            } else {
                if (LiteMode.getPowerSaverLevel() < 100) {
                    formatString = LocaleController.formatString(R.string.LiteBatteryInfoBelow, String.format("%d%%", Integer.valueOf(LiteMode.getPowerSaverLevel())));
                    arrayList.set(1, Item.asInfo(formatString));
                    this.adapter.notifyItemChanged(1);
                }
                i = R.string.LiteBatteryInfoEnabled;
            }
            formatString = LocaleController.getString(i);
            arrayList.set(1, Item.asInfo(formatString));
            this.adapter.notifyItemChanged(1);
        }
    }

    private void updateItems() {
        String formatString;
        int i;
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        if (Build.VERSION.SDK_INT >= 21) {
            this.items.add(Item.asSlider());
            ArrayList arrayList = this.items;
            if (LiteMode.getPowerSaverLevel() <= 0) {
                i = R.string.LiteBatteryInfoDisabled;
            } else if (LiteMode.getPowerSaverLevel() >= 100) {
                i = R.string.LiteBatteryInfoEnabled;
            } else {
                formatString = LocaleController.formatString(R.string.LiteBatteryInfoBelow, String.format("%d%%", Integer.valueOf(LiteMode.getPowerSaverLevel())));
                arrayList.add(Item.asInfo(formatString));
            }
            formatString = LocaleController.getString(i);
            arrayList.add(Item.asInfo(formatString));
        }
        this.items.add(Item.asHeader(LocaleController.getString("LiteOptionsTitle")));
        this.items.add(Item.asSwitch(R.drawable.msg2_sticker, LocaleController.getString(R.string.LiteOptionsStickers), 3));
        if (this.expanded[0]) {
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsAutoplayKeyboard"), 1));
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsAutoplayChat"), 2));
        }
        this.items.add(Item.asSwitch(R.drawable.msg2_smile_status, LocaleController.getString(R.string.LiteOptionsEmoji), 28700));
        if (this.expanded[1]) {
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsAutoplayKeyboard"), 16388));
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsAutoplayReactions"), 8200));
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsAutoplayChat"), 4112));
        }
        this.items.add(Item.asSwitch(R.drawable.msg2_ask_question, LocaleController.getString("LiteOptionsChat"), this.FLAGS_CHAT));
        if (this.expanded[2]) {
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsBackground"), 32));
            if (!AndroidUtilities.isTablet()) {
                this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsTopics"), 64));
            }
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsSpoiler"), 128));
            if (SharedConfig.getDevicePerformanceClass() >= 1 || BuildVars.DEBUG_PRIVATE_VERSION) {
                this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsBlur"), 256));
            }
            this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsScale"), 32768));
            if (ThanosEffect.supports()) {
                this.items.add(Item.asCheckbox(LocaleController.getString("LiteOptionsThanos"), 65536));
            }
        }
        this.items.add(Item.asSwitch(R.drawable.msg2_call_earpiece, LocaleController.getString("LiteOptionsCalls"), 512));
        this.items.add(Item.asSwitch(R.drawable.msg2_videocall, LocaleController.getString("LiteOptionsAutoplayVideo"), 1024));
        this.items.add(Item.asSwitch(R.drawable.msg2_gif, LocaleController.getString("LiteOptionsAutoplayGifs"), 2048));
        this.items.add(Item.asInfo(""));
        this.items.add(Item.asSwitch(LocaleController.getString("LiteSmoothTransitions"), 1));
        this.items.add(Item.asInfo(LocaleController.getString("LiteSmoothTransitionsInfo")));
        this.adapter.setItems(this.oldItems, this.items);
    }

    public void updateValues() {
        int childAdapterPosition;
        if (this.listView == null) {
            return;
        }
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt != null && (childAdapterPosition = this.listView.getChildAdapterPosition(childAt)) >= 0 && childAdapterPosition < this.items.size()) {
                Item item = (Item) this.items.get(childAdapterPosition);
                int i2 = item.viewType;
                if (i2 == 3 || i2 == 4) {
                    ((SwitchCell) childAt).update(item);
                } else if (i2 == 1) {
                    ((PowerSaverSlider) childAt).update();
                }
            }
        }
        if (this.restrictBulletin == null || LiteMode.isPowerSaverApplied()) {
            return;
        }
        this.restrictBulletin.hide();
        this.restrictBulletin = null;
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.PowerUsage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    LiteModeSettingsActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.contentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView2 = this.listView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView2.setAdapter(adapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                LiteModeSettingsActivity.this.lambda$createView$0(view, i, f, f2);
            }
        });
        this.fragmentView = this.contentView;
        this.FLAGS_CHAT = AndroidUtilities.isTablet() ? 98720 : 98784;
        updateItems();
        return this.fragmentView;
    }

    @Override
    public void onBecomeFullyHidden() {
        super.onBecomeFullyHidden();
        LiteMode.removeOnPowerSaverAppliedListener(this.onPowerAppliedChange);
    }

    @Override
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        LiteMode.addOnPowerSaverAppliedListener(this.onPowerAppliedChange);
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        LiteMode.savePreference();
        AnimatedEmojiDrawable.updateAll();
        Theme.reloadWallpaper(true);
    }

    public void scrollToFlags(int i) {
        for (int i2 = 0; i2 < this.items.size(); i2++) {
            if (((Item) this.items.get(i2)).flags == i) {
                highlightRow(i2);
                return;
            }
        }
    }

    public void scrollToType(int i) {
        for (int i2 = 0; i2 < this.items.size(); i2++) {
            if (((Item) this.items.get(i2)).type == i) {
                highlightRow(i2);
                return;
            }
        }
    }

    public void setExpanded(int i, boolean z) {
        int expandedIndex = getExpandedIndex(i);
        if (expandedIndex == -1) {
            return;
        }
        this.expanded[expandedIndex] = z;
        updateValues();
        updateItems();
    }
}
