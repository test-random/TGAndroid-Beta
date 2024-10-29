package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.StatsController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CacheChart;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.DataUsage2Activity;

public class DataUsage2Activity extends BaseFragment {
    private boolean changeStatusBar;
    private ViewPagerFixed.Adapter pageAdapter;
    private ViewPagerFixed pager;
    private Theme.ResourcesProvider resourcesProvider;
    private ViewPagerFixed.TabsView tabsView;
    private static int[] colors = {Theme.key_statisticChartLine_blue, Theme.key_statisticChartLine_green, Theme.key_statisticChartLine_lightblue, Theme.key_statisticChartLine_golden, Theme.key_statisticChartLine_red, Theme.key_statisticChartLine_purple, Theme.key_statisticChartLine_cyan};
    private static int[] particles = {R.drawable.msg_filled_data_videos, R.drawable.msg_filled_data_files, R.drawable.msg_filled_data_photos, R.drawable.msg_filled_data_messages, R.drawable.msg_filled_data_music, R.drawable.msg_filled_data_voice, R.drawable.msg_filled_data_calls};
    private static int[] titles = {R.string.LocalVideoCache, R.string.LocalDocumentCache, R.string.LocalPhotoCache, R.string.MessagesSettings, R.string.LocalMusicCache, R.string.LocalAudioCache, R.string.CallsDataUsage};
    private static int[] stats = {2, 5, 4, 1, 7, 3, 0};

    public class Cell extends FrameLayout {
        ImageView arrowView;
        boolean divider;
        ImageView imageView;
        LinearLayout linearLayout;
        LinearLayout linearLayout2;
        TextView textView;
        TextView valueTextView;

        public Cell(Context context) {
            super(context);
            LinearLayout linearLayout;
            View view;
            LinearLayout.LayoutParams createLinear;
            LinearLayout linearLayout2;
            View view2;
            LinearLayout.LayoutParams createLinear2;
            setBackgroundColor(DataUsage2Activity.this.getThemedColor(Theme.key_windowBackgroundWhite));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(28, 28.0f, (LocaleController.isRTL ? 5 : 3) | 16, 18.0f, 0.0f, 18.0f, 0.0f));
            LinearLayout linearLayout3 = new LinearLayout(context);
            this.linearLayout = linearLayout3;
            linearLayout3.setOrientation(0);
            this.linearLayout.setWeightSum(2.0f);
            addView(this.linearLayout, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, 64.0f, 0.0f, 20.0f, 0.0f));
            LinearLayout linearLayout4 = new LinearLayout(context);
            this.linearLayout2 = linearLayout4;
            linearLayout4.setOrientation(0);
            if (LocaleController.isRTL) {
                this.linearLayout2.setGravity(5);
            }
            this.linearLayout2.setWeightSum(2.0f);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 16.0f);
            TextView textView2 = this.textView;
            int i = Theme.key_windowBackgroundWhiteBlackText;
            textView2.setTextColor(DataUsage2Activity.this.getThemedColor(i));
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setSingleLine();
            this.textView.setLines(1);
            ImageView imageView2 = new ImageView(context);
            this.arrowView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.arrowView.setImageResource(R.drawable.arrow_more);
            this.arrowView.setColorFilter(new PorterDuffColorFilter(DataUsage2Activity.this.getThemedColor(i), PorterDuff.Mode.MULTIPLY));
            this.arrowView.setTranslationY(AndroidUtilities.dp(1.0f));
            this.arrowView.setVisibility(8);
            if (LocaleController.isRTL) {
                this.linearLayout2.addView(this.arrowView, LayoutHelper.createLinear(16, 16, 21, 3, 0, 0, 0));
                linearLayout = this.linearLayout2;
                view = this.textView;
                createLinear = LayoutHelper.createLinear(-2, -2, 21);
            } else {
                this.linearLayout2.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
                linearLayout = this.linearLayout2;
                view = this.arrowView;
                createLinear = LayoutHelper.createLinear(16, 16, 16, 3, 0, 0, 0);
            }
            linearLayout.addView(view, createLinear);
            TextView textView3 = new TextView(context);
            this.valueTextView = textView3;
            textView3.setTextSize(1, 16.0f);
            this.valueTextView.setTextColor(DataUsage2Activity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText2));
            this.valueTextView.setGravity(LocaleController.isRTL ? 3 : 5);
            if (LocaleController.isRTL) {
                this.linearLayout.addView(this.valueTextView, LayoutHelper.createLinear(-2, -2, 19));
                linearLayout2 = this.linearLayout;
                view2 = this.linearLayout2;
                createLinear2 = LayoutHelper.createLinear(0, -2, 2.0f, 21);
            } else {
                this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(0, -2, 2.0f, 16));
                linearLayout2 = this.linearLayout;
                view2 = this.valueTextView;
                createLinear2 = LayoutHelper.createLinear(-2, -2, 21);
            }
            linearLayout2.addView(view2, createLinear2);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.divider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void set(int i, int i2, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            if (i2 == 0) {
                this.imageView.setVisibility(8);
            } else {
                this.imageView.setVisibility(0);
                this.imageView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.0f), i));
                this.imageView.setImageResource(i2);
            }
            this.textView.setText(charSequence);
            this.valueTextView.setText(charSequence2);
            this.divider = z;
            setWillNotDraw(!z);
        }

        public void setArrow(Boolean bool) {
            if (bool == null) {
                this.arrowView.setVisibility(8);
            } else {
                this.arrowView.setVisibility(0);
                this.arrowView.animate().rotation(bool.booleanValue() ? 0.0f : 180.0f).setDuration(360L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
        }
    }

    public class CustomCharacterSpan extends MetricAffectingSpan {
        double ratio;

        public CustomCharacterSpan(double d) {
            this.ratio = d;
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            int i = textPaint.baselineShift;
            double ascent = textPaint.ascent();
            double d = this.ratio;
            Double.isNaN(ascent);
            textPaint.baselineShift = i + ((int) (ascent * d));
        }

        @Override
        public void updateMeasureState(TextPaint textPaint) {
            int i = textPaint.baselineShift;
            double ascent = textPaint.ascent();
            double d = this.ratio;
            Double.isNaN(ascent);
            textPaint.baselineShift = i + ((int) (ascent * d));
        }
    }

    public static class ItemInner extends AdapterWithDiffUtils.Item {
        public int imageColor;
        public int imageResId;
        public int index;
        public int key;
        public boolean pad;
        public CharSequence text;
        public CharSequence valueText;

        public ItemInner(int i) {
            super(i, false);
        }

        private ItemInner(int i, int i2, int i3, int i4, CharSequence charSequence, CharSequence charSequence2) {
            super(i, false);
            this.index = i2;
            this.imageResId = i3;
            this.imageColor = i4;
            this.text = charSequence;
            this.valueText = charSequence2;
        }

        private ItemInner(int i, CharSequence charSequence) {
            super(i, false);
            this.text = charSequence;
        }

        public static ItemInner asCell(int i, int i2, int i3, CharSequence charSequence, CharSequence charSequence2) {
            return new ItemInner(2, i, i2, i3, charSequence, charSequence2);
        }

        public static ItemInner asHeader(String str) {
            return new ItemInner(4, str);
        }

        public static ItemInner asSeparator() {
            return new ItemInner(3);
        }

        public static ItemInner asSeparator(String str) {
            return new ItemInner(3, str);
        }

        public static ItemInner asSubtitle(String str) {
            return new ItemInner(1, str);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ItemInner)) {
                return false;
            }
            ItemInner itemInner = (ItemInner) obj;
            int i = itemInner.viewType;
            int i2 = this.viewType;
            if (i != i2) {
                return false;
            }
            return (i2 == 1 || i2 == 4 || i2 == 3 || i2 == 5) ? TextUtils.equals(this.text, itemInner.text) : i2 == 2 ? itemInner.index == this.index && TextUtils.equals(this.text, itemInner.text) && itemInner.imageColor == this.imageColor && itemInner.imageResId == this.imageResId : itemInner.key == this.key;
        }
    }

    public class ListView extends RecyclerListView {
        Adapter adapter;
        private boolean animateChart;
        private CacheChart chart;
        private Size[] chartSegments;
        private boolean[] collapsed;
        int currentType;
        private boolean empty;
        private ArrayList itemInners;
        LinearLayoutManager layoutManager;
        private ArrayList oldItems;
        private ArrayList removedSegments;
        private Size[] segments;
        private int[] tempPercents;
        private float[] tempSizes;
        private long totalSize;
        private long totalSizeIn;
        private long totalSizeOut;

        public class Adapter extends AdapterWithDiffUtils {

            class AnonymousClass1 extends CacheChart {
                AnonymousClass1(Context context, int i, int[] iArr, int i2, int[] iArr2) {
                    super(context, i, iArr, i2, iArr2);
                }

                public static int lambda$onSectionDown$0(int i) {
                    return i;
                }

                @Override
                protected int heightDp() {
                    return 216;
                }

                @Override
                protected void onSectionDown(int i, boolean z) {
                    final int i2;
                    if (!z) {
                        ListView.this.removeHighlightRow();
                        return;
                    }
                    if (i < 0 || i >= ListView.this.segments.length) {
                        return;
                    }
                    int i3 = 0;
                    while (true) {
                        i2 = -1;
                        if (i3 >= ListView.this.segments.length) {
                            i3 = -1;
                            break;
                        } else if (ListView.this.segments[i3].index == i) {
                            break;
                        } else {
                            i3++;
                        }
                    }
                    int i4 = 0;
                    while (true) {
                        if (i4 < ListView.this.itemInners.size()) {
                            ItemInner itemInner = (ItemInner) ListView.this.itemInners.get(i4);
                            if (itemInner != null && itemInner.viewType == 2 && itemInner.index == i3) {
                                i2 = i4;
                                break;
                            }
                            i4++;
                        } else {
                            break;
                        }
                    }
                    ListView listView = ListView.this;
                    if (i2 >= 0) {
                        listView.highlightRow(new RecyclerListView.IntReturnCallback() {
                            @Override
                            public final int run() {
                                int lambda$onSectionDown$0;
                                lambda$onSectionDown$0 = DataUsage2Activity.ListView.Adapter.AnonymousClass1.lambda$onSectionDown$0(i2);
                                return lambda$onSectionDown$0;
                            }
                        }, 0);
                    } else {
                        listView.removeHighlightRow();
                    }
                }

                @Override
                protected int padInsideDp() {
                    return 10;
                }
            }

            private Adapter() {
            }

            @Override
            public int getItemCount() {
                return ListView.this.itemInners.size();
            }

            @Override
            public int getItemViewType(int i) {
                return ((ItemInner) ListView.this.itemInners.get(i)).viewType;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                ItemInner itemInner = (ItemInner) ListView.this.itemInners.get(viewHolder.getAdapterPosition());
                int i = itemInner.viewType;
                return i == 5 || (i == 2 && itemInner.index != -1);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                Context context;
                int i2;
                int i3;
                int i4;
                ItemInner itemInner = (ItemInner) ListView.this.itemInners.get(viewHolder.getAdapterPosition());
                int itemViewType = viewHolder.getItemViewType();
                boolean z = false;
                if (itemViewType == 0) {
                    CacheChart cacheChart = (CacheChart) viewHolder.itemView;
                    if (ListView.this.segments != null) {
                        cacheChart.setSegments(ListView.this.totalSize, ListView.this.animateChart, ListView.this.chartSegments);
                    }
                    ListView.this.animateChart = false;
                    return;
                }
                Boolean bool = null;
                if (itemViewType == 1) {
                    SubtitleCell subtitleCell = (SubtitleCell) viewHolder.itemView;
                    subtitleCell.setText(itemInner.text);
                    int i5 = i + 1;
                    if (i5 >= ListView.this.itemInners.size() || (i4 = ((ItemInner) ListView.this.itemInners.get(i5)).viewType) == itemInner.viewType || i4 == 3 || i4 == 6) {
                        subtitleCell.setBackground(null);
                        return;
                    } else {
                        subtitleCell.setBackground(Theme.getThemedDrawableByKey(ListView.this.getContext(), R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                }
                if (itemViewType == 2) {
                    Cell cell = (Cell) viewHolder.itemView;
                    int i6 = i + 1;
                    cell.set(itemInner.imageColor, itemInner.imageResId, itemInner.text, itemInner.valueText, i6 < getItemCount() && ((ItemInner) ListView.this.itemInners.get(i6)).viewType == itemViewType);
                    if (!itemInner.pad && (i3 = itemInner.index) >= 0 && (i3 >= ListView.this.segments.length || ListView.this.segments[itemInner.index].size > 0)) {
                        bool = Boolean.valueOf(ListView.this.collapsed[itemInner.index]);
                    }
                    cell.setArrow(bool);
                    return;
                }
                if (itemViewType != 3) {
                    if (itemViewType == 4) {
                        ((HeaderCell) viewHolder.itemView).setText(itemInner.text);
                        return;
                    } else if (itemViewType == 5) {
                        ((TextCell) viewHolder.itemView).setText(itemInner.text.toString(), false);
                        return;
                    } else {
                        if (itemViewType == 6) {
                            ((RoundingCell) viewHolder.itemView).setTop(true);
                            return;
                        }
                        return;
                    }
                }
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                boolean z2 = i > 0 && itemInner.viewType != ((ItemInner) ListView.this.itemInners.get(i + (-1))).viewType;
                int i7 = i + 1;
                if (i7 < ListView.this.itemInners.size() && ((ItemInner) ListView.this.itemInners.get(i7)).viewType != itemInner.viewType) {
                    z = true;
                }
                if (z2 && z) {
                    context = ListView.this.getContext();
                    i2 = R.drawable.greydivider;
                } else if (z2) {
                    context = ListView.this.getContext();
                    i2 = R.drawable.greydivider_bottom;
                } else if (!z) {
                    textInfoPrivacyCell.setBackground(null);
                    textInfoPrivacyCell.setText(itemInner.text);
                } else {
                    context = ListView.this.getContext();
                    i2 = R.drawable.greydivider_top;
                }
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(context, i2, Theme.key_windowBackgroundGrayShadow));
                textInfoPrivacyCell.setText(itemInner.text);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                int themedColor;
                View view2;
                if (i == 0) {
                    ListView.this.chart = new AnonymousClass1(ListView.this.getContext(), DataUsage2Activity.colors.length, DataUsage2Activity.colors, 1, DataUsage2Activity.particles);
                    ListView.this.chart.setInterceptTouch(false);
                    view = ListView.this.chart;
                } else if (i == 1) {
                    ListView listView = ListView.this;
                    view = new SubtitleCell(listView.getContext());
                } else if (i != 3) {
                    if (i == 4) {
                        View headerCell = new HeaderCell(ListView.this.getContext());
                        themedColor = ListView.this.getThemedColor(Theme.key_windowBackgroundWhite);
                        view2 = headerCell;
                    } else if (i == 5) {
                        TextCell textCell = new TextCell(ListView.this.getContext());
                        textCell.setTextColor(ListView.this.getThemedColor(Theme.key_text_RedRegular));
                        themedColor = ListView.this.getThemedColor(Theme.key_windowBackgroundWhite);
                        view2 = textCell;
                    } else if (i == 6) {
                        view = new RoundingCell(ListView.this.getContext());
                    } else if (i != 7) {
                        ListView listView2 = ListView.this;
                        view = new Cell(listView2.getContext());
                    } else {
                        view = new View(ListView.this.getContext()) {
                            {
                                setBackgroundColor(ListView.this.getThemedColor(Theme.key_windowBackgroundWhite));
                            }

                            @Override
                            protected void onMeasure(int i2, int i3) {
                                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), 1073741824));
                            }
                        };
                    }
                    view2.setBackgroundColor(themedColor);
                    view = view2;
                } else {
                    view = new TextInfoPrivacyCell(ListView.this.getContext());
                }
                return new RecyclerListView.Holder(view);
            }
        }

        public class Size extends CacheChart.SegmentSize {
            int inCount;
            long inSize;
            int index;
            int outCount;
            long outSize;

            public Size(int i, long j, long j2, long j3, int i2, int i3) {
                this.index = i;
                this.size = j;
                this.selected = true;
                this.inSize = j2;
                this.inCount = i2;
                this.outSize = j3;
                this.outCount = i3;
            }
        }

        public ListView(Context context) {
            super(context);
            this.animateChart = false;
            this.currentType = 0;
            this.oldItems = new ArrayList();
            this.itemInners = new ArrayList();
            this.tempSizes = new float[7];
            this.tempPercents = new int[7];
            this.removedSegments = new ArrayList();
            this.collapsed = new boolean[7];
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            this.layoutManager = linearLayoutManager;
            setLayoutManager(linearLayoutManager);
            Adapter adapter = new Adapter();
            this.adapter = adapter;
            setAdapter(adapter);
            setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i) {
                    DataUsage2Activity.ListView.this.lambda$new$1(view, i);
                }
            });
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setDurations(220L);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            setItemAnimator(defaultItemAnimator);
        }

        private String formatPercent(int i) {
            return i <= 0 ? String.format("<%d%%", 1) : String.format("%d%%", Integer.valueOf(i));
        }

        private long getBytesCount(int i) {
            return getSentBytesCount(i) + getReceivedBytesCount(i);
        }

        private long getReceivedBytesCount(int i) {
            int i2 = this.currentType;
            return (i2 == 1 || i2 == 2 || i2 == 3) ? StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getReceivedBytesCount(this.currentType - 1, i) : StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getReceivedBytesCount(0, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getReceivedBytesCount(1, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getReceivedBytesCount(2, i);
        }

        private int getReceivedItemsCount(int i) {
            int i2 = this.currentType;
            return (i2 == 1 || i2 == 2 || i2 == 3) ? StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getRecivedItemsCount(this.currentType - 1, i) : StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getRecivedItemsCount(0, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getRecivedItemsCount(1, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getRecivedItemsCount(2, i);
        }

        private long getResetStatsDate() {
            int i = this.currentType;
            return (i == 1 || i == 2 || i == 3) ? StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getResetStatsDate(this.currentType - 1) : min(StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getResetStatsDate(0), StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getResetStatsDate(1), StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getResetStatsDate(2));
        }

        private long getSentBytesCount(int i) {
            int i2 = this.currentType;
            return (i2 == 1 || i2 == 2 || i2 == 3) ? StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentBytesCount(this.currentType - 1, i) : StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentBytesCount(0, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentBytesCount(1, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentBytesCount(2, i);
        }

        private int getSentItemsCount(int i) {
            int i2 = this.currentType;
            return (i2 == 1 || i2 == 2 || i2 == 3) ? StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentItemsCount(this.currentType - 1, i) : StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentItemsCount(0, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentItemsCount(1, i) + StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).getSentItemsCount(2, i);
        }

        public void lambda$new$0(DialogInterface dialogInterface, int i) {
            this.removedSegments.clear();
            int i2 = 0;
            while (true) {
                Size[] sizeArr = this.segments;
                if (i2 >= sizeArr.length) {
                    StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).resetStats(0);
                    StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).resetStats(1);
                    StatsController.getInstance(((BaseFragment) DataUsage2Activity.this).currentAccount).resetStats(2);
                    this.animateChart = true;
                    setup();
                    updateRows(true);
                    return;
                }
                Size size = sizeArr[i2];
                if (size.size > 0) {
                    this.removedSegments.add(Integer.valueOf(size.index));
                }
                i2++;
            }
        }

        public void lambda$new$1(View view, int i) {
            if ((view instanceof Cell) && i >= 0 && i < this.itemInners.size()) {
                ItemInner itemInner = (ItemInner) this.itemInners.get(i);
                if (itemInner != null) {
                    int i2 = itemInner.index;
                    if (i2 >= 0) {
                        this.collapsed[i2] = !r0[i2];
                        updateRows(true);
                        return;
                    } else {
                        if (i2 == -2) {
                            DataUsage2Activity.this.presentFragment(new DataAutoDownloadActivity(this.currentType - 1));
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            if (view instanceof TextCell) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataUsage2Activity.this.getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.ResetStatisticsAlertTitle));
                builder.setMessage(LocaleController.getString(R.string.ResetStatisticsAlert));
                builder.setPositiveButton(LocaleController.getString(R.string.Reset), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        DataUsage2Activity.ListView.this.lambda$new$0(dialogInterface, i3);
                    }
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                AlertDialog create = builder.create();
                DataUsage2Activity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                }
            }
        }

        public static int lambda$setup$2(Size size, Size size2) {
            return Long.compare(size2.size, size.size);
        }

        private long min(long... jArr) {
            long j = Long.MAX_VALUE;
            for (long j2 : jArr) {
                if (j > j2) {
                    j = j2;
                }
            }
            return j;
        }

        private void setup() {
            this.totalSize = getBytesCount(6);
            this.totalSizeIn = getReceivedBytesCount(6);
            this.totalSizeOut = getSentBytesCount(6);
            if (this.segments == null) {
                this.segments = new Size[7];
            }
            if (this.chartSegments == null) {
                this.chartSegments = new Size[7];
            }
            for (int i = 0; i < DataUsage2Activity.stats.length; i++) {
                long bytesCount = getBytesCount(DataUsage2Activity.stats[i]);
                Size[] sizeArr = this.chartSegments;
                Size[] sizeArr2 = this.segments;
                Size size = new Size(i, bytesCount, getReceivedBytesCount(DataUsage2Activity.stats[i]), getSentBytesCount(DataUsage2Activity.stats[i]), getReceivedItemsCount(DataUsage2Activity.stats[i]), getSentItemsCount(DataUsage2Activity.stats[i]));
                sizeArr2[i] = size;
                sizeArr[i] = size;
                this.tempSizes[i] = ((float) bytesCount) / ((float) this.totalSize);
            }
            Arrays.sort(this.segments, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$setup$2;
                    lambda$setup$2 = DataUsage2Activity.ListView.lambda$setup$2((DataUsage2Activity.ListView.Size) obj, (DataUsage2Activity.ListView.Size) obj2);
                    return lambda$setup$2;
                }
            });
            AndroidUtilities.roundPercents(this.tempSizes, this.tempPercents);
            Arrays.fill(this.collapsed, true);
        }

        private void updateRows(boolean z) {
            int i;
            CharSequence concat;
            String str;
            int i2;
            CharSequence concat2;
            this.oldItems.clear();
            this.oldItems.addAll(this.itemInners);
            this.itemInners.clear();
            this.itemInners.add(new ItemInner(0));
            String formatString = this.totalSize > 0 ? LocaleController.formatString("YourNetworkUsageSince", R.string.YourNetworkUsageSince, LocaleController.getInstance().getFormatterStats().format(getResetStatsDate())) : LocaleController.formatString("NoNetworkUsageSince", R.string.NoNetworkUsageSince, LocaleController.getInstance().getFormatterStats().format(getResetStatsDate()));
            this.itemInners.add(ItemInner.asSubtitle(formatString));
            ArrayList arrayList = new ArrayList();
            int i3 = 0;
            while (true) {
                Size[] sizeArr = this.segments;
                if (i3 >= sizeArr.length) {
                    break;
                }
                Size size = sizeArr[i3];
                long j = size.size;
                int i4 = size.index;
                boolean z2 = this.empty || this.removedSegments.contains(Integer.valueOf(i4));
                if (j > 0 || z2) {
                    SpannableString spannableString = new SpannableString(formatPercent(this.tempPercents[i4]));
                    spannableString.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableString.length(), 33);
                    spannableString.setSpan(new RelativeSizeSpan(0.8f), 0, spannableString.length(), 33);
                    str = formatString;
                    spannableString.setSpan(new CustomCharacterSpan(0.1d), 0, spannableString.length(), 33);
                    int i5 = DataUsage2Activity.particles[i4];
                    int themedColor = getThemedColor(DataUsage2Activity.colors[i4]);
                    if (j == 0) {
                        concat2 = LocaleController.getString(DataUsage2Activity.titles[i4]);
                        i2 = 1;
                    } else {
                        i2 = 1;
                        concat2 = TextUtils.concat(LocaleController.getString(DataUsage2Activity.titles[i4]), "  ", spannableString);
                    }
                    arrayList.add(ItemInner.asCell(i3, i5, themedColor, concat2, AndroidUtilities.formatFileSize(j)));
                } else {
                    str = formatString;
                    i2 = 1;
                }
                i3 += i2;
                formatString = str;
            }
            String str2 = formatString;
            if (!arrayList.isEmpty()) {
                SpannableString spannableString2 = new SpannableString("^");
                Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_mini_upload).mutate();
                int i6 = Theme.key_windowBackgroundWhiteBlackText;
                int themedColor2 = getThemedColor(i6);
                PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                mutate.setColorFilter(new PorterDuffColorFilter(themedColor2, mode));
                mutate.setBounds(0, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(18.0f));
                spannableString2.setSpan(new ImageSpan(mutate, 2), 0, 1, 33);
                SpannableString spannableString3 = new SpannableString("v");
                Drawable mutate2 = getContext().getResources().getDrawable(R.drawable.msg_mini_download).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor(i6), mode));
                mutate2.setBounds(0, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(18.0f));
                spannableString3.setSpan(new ImageSpan(mutate2, 2), 0, 1, 33);
                int i7 = 0;
                while (i7 < arrayList.size()) {
                    int i8 = ((ItemInner) arrayList.get(i7)).index;
                    if (i8 >= 0 && !this.collapsed[i8]) {
                        Size size2 = this.segments[i8];
                        if (DataUsage2Activity.stats[size2.index] == 0) {
                            if (size2.outSize > 0 || size2.outCount > 0) {
                                i7++;
                                arrayList.add(i7, ItemInner.asCell(-1, 0, 0, LocaleController.formatPluralStringComma("OutgoingCallsCount", size2.outCount), AndroidUtilities.formatFileSize(size2.outSize)));
                            }
                            if (size2.inSize > 0 || size2.inCount > 0) {
                                i7++;
                                concat = LocaleController.formatPluralStringComma("IncomingCallsCount", size2.inCount);
                                arrayList.add(i7, ItemInner.asCell(-1, 0, 0, concat, AndroidUtilities.formatFileSize(size2.inSize)));
                            }
                        } else if (DataUsage2Activity.stats[size2.index] != 1) {
                            if (size2.outSize > 0 || size2.outCount > 0) {
                                i7++;
                                arrayList.add(i7, ItemInner.asCell(-1, 0, 0, TextUtils.concat(spannableString2, " ", AndroidUtilities.replaceTags(LocaleController.formatPluralStringComma("FilesSentCount", size2.outCount))), AndroidUtilities.formatFileSize(size2.outSize)));
                            }
                            if (size2.inSize > 0 || size2.inCount > 0) {
                                i7++;
                                concat = TextUtils.concat(spannableString3, " ", AndroidUtilities.replaceTags(LocaleController.formatPluralStringComma("FilesReceivedCount", size2.inCount)));
                                arrayList.add(i7, ItemInner.asCell(-1, 0, 0, concat, AndroidUtilities.formatFileSize(size2.inSize)));
                            }
                        } else {
                            if (size2.outSize > 0 || size2.outCount > 0) {
                                i7++;
                                arrayList.add(i7, ItemInner.asCell(-1, 0, 0, TextUtils.concat(spannableString2, " ", LocaleController.getString(R.string.BytesSent)), AndroidUtilities.formatFileSize(size2.outSize)));
                            }
                            if (size2.inSize > 0 || size2.inCount > 0) {
                                i = 1;
                                i7++;
                                arrayList.add(i7, ItemInner.asCell(-1, 0, 0, TextUtils.concat(spannableString3, " ", LocaleController.getString(R.string.BytesReceived)), AndroidUtilities.formatFileSize(size2.inSize)));
                            }
                        }
                        i7 += i;
                    }
                    i = 1;
                    i7 += i;
                }
                this.itemInners.addAll(arrayList);
                if (!this.empty) {
                    this.itemInners.add(ItemInner.asSeparator(LocaleController.getString(R.string.DataUsageSectionsInfo)));
                }
            }
            if (!this.empty) {
                this.itemInners.add(ItemInner.asHeader(LocaleController.getString(R.string.TotalNetworkUsage)));
                this.itemInners.add(ItemInner.asCell(-1, R.drawable.msg_filled_data_sent, getThemedColor(Theme.key_statisticChartLine_lightblue), LocaleController.getString(R.string.BytesSent), AndroidUtilities.formatFileSize(this.totalSizeOut)));
                this.itemInners.add(ItemInner.asCell(-1, R.drawable.msg_filled_data_received, getThemedColor(Theme.key_statisticChartLine_green), LocaleController.getString(R.string.BytesReceived), AndroidUtilities.formatFileSize(this.totalSizeIn)));
            }
            if (!arrayList.isEmpty()) {
                this.itemInners.add(ItemInner.asSeparator(str2));
            }
            if (this.currentType != 0) {
                if (arrayList.isEmpty()) {
                    this.itemInners.add(ItemInner.asSeparator());
                }
                this.itemInners.add(ItemInner.asCell(-2, R.drawable.msg_download_settings, getThemedColor(Theme.key_statisticChartLine_lightblue), LocaleController.getString(R.string.AutomaticDownloadSettings), null));
                int i9 = this.currentType;
                this.itemInners.add(ItemInner.asSeparator(LocaleController.getString(i9 != 1 ? i9 != 3 ? R.string.AutomaticDownloadSettingsInfoWiFi : R.string.AutomaticDownloadSettingsInfoRoaming : R.string.AutomaticDownloadSettingsInfoMobile)));
            }
            if (!arrayList.isEmpty()) {
                this.itemInners.add(new ItemInner(5, LocaleController.getString(R.string.ResetStatistics)));
            }
            this.itemInners.add(ItemInner.asSeparator());
            Adapter adapter = this.adapter;
            if (adapter != null) {
                if (z) {
                    adapter.setItems(this.oldItems, this.itemInners);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
        }

        public void setType(int i) {
            this.currentType = i;
            this.removedSegments.clear();
            this.empty = getBytesCount(6) <= 0;
            setup();
            updateRows(false);
        }
    }

    private class PageAdapter extends ViewPagerFixed.Adapter {
        private PageAdapter() {
        }

        @Override
        public void bindView(View view, int i, int i2) {
            ListView listView = (ListView) view;
            listView.setType(i);
            listView.scrollToPosition(0);
        }

        @Override
        public View createView(int i) {
            DataUsage2Activity dataUsage2Activity = DataUsage2Activity.this;
            return new ListView(dataUsage2Activity.getContext());
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        @Override
        public String getItemTitle(int i) {
            int i2;
            if (i == 0) {
                i2 = R.string.NetworkUsageAllTab;
            } else if (i == 1) {
                i2 = R.string.NetworkUsageMobileTab;
            } else if (i == 2) {
                i2 = R.string.NetworkUsageWiFiTab;
            } else {
                if (i != 3) {
                    return "";
                }
                i2 = R.string.NetworkUsageRoamingTab;
            }
            return LocaleController.getString(i2);
        }
    }

    public static class RoundingCell extends View {
        Paint paint;
        Path path;
        private boolean top;

        public RoundingCell(Context context) {
            super(context);
            this.path = new Path();
            Paint paint = new Paint(1);
            this.paint = paint;
            this.top = true;
            paint.setShadowLayer(AndroidUtilities.dp(1.0f), 0.0f, AndroidUtilities.dp(-0.66f), 251658240);
            this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }

        @Override
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            requestLayout();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(this.path, this.paint);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(13.0f), 1073741824));
            setTop(this.top);
        }

        public void setTop(boolean z) {
            float dp;
            RectF rectF;
            float dp2;
            float measuredWidth;
            float measuredHeight;
            this.path.rewind();
            this.top = z;
            if (z) {
                dp = AndroidUtilities.dp(14.0f);
                rectF = AndroidUtilities.rectTmp;
                dp2 = AndroidUtilities.dp(4.0f);
                measuredWidth = getMeasuredWidth();
                measuredHeight = AndroidUtilities.dp(4.0f) + (getMeasuredHeight() * 2);
            } else {
                dp = AndroidUtilities.dp(8.0f);
                rectF = AndroidUtilities.rectTmp;
                dp2 = ((-getMeasuredHeight()) * 2) - AndroidUtilities.dp(4.0f);
                measuredWidth = getMeasuredWidth();
                measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(4.0f);
            }
            rectF.set(0.0f, dp2, measuredWidth, measuredHeight);
            this.path.addRoundRect(rectF, dp, dp, Path.Direction.CW);
        }
    }

    class SubtitleCell extends FrameLayout {
        TextView textView;

        public SubtitleCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setGravity(17);
            this.textView.setTextSize(1, 13.0f);
            this.textView.setTextColor(DataUsage2Activity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 119, 24.0f, 0.0f, 24.0f, 14.0f));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    public DataUsage2Activity() {
        this(null);
    }

    public DataUsage2Activity(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString(R.string.NetworkUsage));
        ActionBar actionBar = this.actionBar;
        int i = Theme.key_actionBarActionModeDefault;
        actionBar.setBackgroundColor(getThemedColor(i));
        ActionBar actionBar2 = this.actionBar;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        actionBar2.setTitleColor(getThemedColor(i2));
        this.actionBar.setItemsColor(getThemedColor(i2), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_listSelector), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    DataUsage2Activity.this.lambda$onBackPressed$319();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DataUsage2Activity.this.getParentLayout() == null || DataUsage2Activity.this.tabsView == null) {
                    return;
                }
                float measuredHeight = DataUsage2Activity.this.tabsView.getMeasuredHeight();
                canvas.drawLine(0.0f, measuredHeight, getWidth(), measuredHeight, Theme.dividerPaint);
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i4), 1073741824));
            }
        };
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundGray));
        ViewPagerFixed viewPagerFixed = new ViewPagerFixed(context);
        this.pager = viewPagerFixed;
        PageAdapter pageAdapter = new PageAdapter();
        this.pageAdapter = pageAdapter;
        viewPagerFixed.setAdapter(pageAdapter);
        ViewPagerFixed.TabsView createTabsView = this.pager.createTabsView(true, 8);
        this.tabsView = createTabsView;
        createTabsView.setBackgroundColor(getThemedColor(i));
        frameLayout.addView(this.tabsView, LayoutHelper.createFrame(-1, 48, 55));
        frameLayout.addView(this.pager, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 48.0f, 0.0f, 0.0f));
        this.fragmentView = frameLayout;
        return frameLayout;
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override
    public boolean isLightStatusBar() {
        return !this.changeStatusBar ? super.isLightStatusBar() : AndroidUtilities.computePerceivedBrightness(Theme.getColor(Theme.key_actionBarActionModeDefault)) > 0.721f;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return (motionEvent != null && motionEvent.getY() <= ((float) (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(48.0f)))) || this.pager.getCurrentPosition() == 0;
    }

    @Override
    public void onTransitionAnimationProgress(boolean z, float f) {
        if (f > 0.5f && !this.changeStatusBar) {
            this.changeStatusBar = true;
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        }
        super.onTransitionAnimationProgress(z, f);
    }
}
