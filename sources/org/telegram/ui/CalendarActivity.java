package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CalendarActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoryViewer;

public class CalendarActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    TextPaint activeTextPaint;
    CalendarAdapter adapter;
    BackDrawable backDrawable;
    Paint blackoutPaint;
    private View blurredView;
    private FrameLayout bottomBar;
    private int calendarType;
    Callback callback;
    private boolean canClearHistory;
    ChatActivity chatActivity;
    private boolean checkEnterItems;
    FrameLayout contentView;
    private int dateSelectedEnd;
    private int dateSelectedStart;
    private long dialogId;
    boolean endReached;
    private boolean inSelectionMode;
    private boolean isOpened;
    int lastDaysSelected;
    int lastId;
    boolean lastInSelectionMode;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    private boolean loading;
    private SpoilerEffect mediaSpoilerEffect;
    SparseArray messagesByYearMounth;
    private int minDate;
    int minMontYear;
    int monthCount;
    private Path path;
    private int photosVideosTypeFilter;
    TextView removeDaysButton;
    TextView selectDaysButton;
    HintView selectDaysHint;
    private Paint selectOutlinePaint;
    private Paint selectPaint;
    int selectedMonth;
    int selectedYear;
    private ValueAnimator selectionAnimator;
    int startFromMonth;
    int startFromYear;
    int startOffset;
    private StoriesController.StoriesList storiesList;
    private int storiesPlaceDay;
    private StoryViewer.HolderDrawAbove storiesPlaceDrawAbove;
    private StoryViewer.PlaceProvider storiesPlaceProvider;
    TextPaint textPaint;
    TextPaint textPaint2;
    private long topicId;

    public class AnonymousClass1 implements StoryViewer.PlaceProvider {
        AnonymousClass1() {
        }

        public void lambda$findView$0(Canvas canvas, RectF rectF, float f, boolean z) {
            CalendarActivity.this.blackoutPaint.setAlpha((int) (80.0f * f));
            float lerp = AndroidUtilities.lerp(0.0f, Math.min(rectF.width(), rectF.height()) / 2.0f, f);
            canvas.drawRoundRect(rectF, lerp, lerp, CalendarActivity.this.blackoutPaint);
            float clamp = Utilities.clamp((f - 0.5f) / 0.5f, 1.0f, 0.0f);
            if (clamp > 0.0f) {
                int alpha = CalendarActivity.this.activeTextPaint.getAlpha();
                CalendarActivity.this.activeTextPaint.setAlpha((int) (alpha * clamp));
                canvas.save();
                float min = Math.min(2.0f, Math.min(rectF.height(), rectF.width()) / AndroidUtilities.dp(44.0f));
                canvas.scale(min, min, rectF.centerX(), rectF.centerY());
                canvas.drawText(Integer.toString(CalendarActivity.this.storiesPlaceDay + 1), rectF.centerX(), rectF.centerY() + AndroidUtilities.dp(5.0f), CalendarActivity.this.activeTextPaint);
                canvas.restore();
                CalendarActivity.this.activeTextPaint.setAlpha(alpha);
            }
        }

        @Override
        public boolean findView(long j, int i, int i2, int i3, StoryViewer.TransitionViewHolder transitionViewHolder) {
            if (CalendarActivity.this.listView == null) {
                return false;
            }
            for (int i4 = 0; i4 < CalendarActivity.this.listView.getChildCount(); i4++) {
                View childAt = CalendarActivity.this.listView.getChildAt(i4);
                if (childAt instanceof MonthView) {
                    MonthView monthView = (MonthView) childAt;
                    if (monthView.messagesByDays == null) {
                        continue;
                    } else {
                        for (int i5 = 0; i5 < monthView.messagesByDays.size(); i5++) {
                            ArrayList arrayList = ((PeriodDay) monthView.messagesByDays.valueAt(i5)).storyItems;
                            if (arrayList != null && arrayList.contains(Integer.valueOf(i2))) {
                                ImageReceiver imageReceiver = (ImageReceiver) monthView.imagesByDays.get(CalendarActivity.this.storiesPlaceDay = monthView.messagesByDays.keyAt(i5));
                                if (imageReceiver == null) {
                                    return false;
                                }
                                transitionViewHolder.storyImage = imageReceiver;
                                if (CalendarActivity.this.storiesPlaceDrawAbove == null) {
                                    CalendarActivity.this.storiesPlaceDrawAbove = new StoryViewer.HolderDrawAbove() {
                                        @Override
                                        public final void draw(Canvas canvas, RectF rectF, float f, boolean z) {
                                            CalendarActivity.AnonymousClass1.this.lambda$findView$0(canvas, rectF, f, z);
                                        }
                                    };
                                }
                                transitionViewHolder.drawAbove = CalendarActivity.this.storiesPlaceDrawAbove;
                                transitionViewHolder.view = monthView;
                                transitionViewHolder.clipParent = CalendarActivity.this.fragmentView;
                                transitionViewHolder.clipTop = AndroidUtilities.dp(36.0f);
                                transitionViewHolder.clipBottom = CalendarActivity.this.fragmentView.getBottom();
                                transitionViewHolder.avatarImage = null;
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void loadNext(boolean z) {
            StoryViewer.PlaceProvider.CC.$default$loadNext(this, z);
        }

        @Override
        public void preLayout(long j, int i, Runnable runnable) {
            if (CalendarActivity.this.listView == null) {
                runnable.run();
            }
            CalendarActivity.this.listView.post(runnable);
        }
    }

    public class CalendarAdapter extends RecyclerView.Adapter {
        private CalendarAdapter() {
        }

        CalendarAdapter(CalendarActivity calendarActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override
        public int getItemCount() {
            return CalendarActivity.this.monthCount;
        }

        @Override
        public long getItemId(int i) {
            CalendarActivity calendarActivity = CalendarActivity.this;
            return ((calendarActivity.startFromYear - (i / 12)) * 100) + (calendarActivity.startFromMonth - (i % 12));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MonthView monthView = (MonthView) viewHolder.itemView;
            CalendarActivity calendarActivity = CalendarActivity.this;
            int i2 = calendarActivity.startFromYear - (i / 12);
            int i3 = calendarActivity.startFromMonth - (i % 12);
            if (i3 < 0) {
                i3 += 12;
                i2--;
            }
            monthView.setDate(i2, i3, (SparseArray) calendarActivity.messagesByYearMounth.get((i2 * 100) + i3), monthView.currentYear == i2 && monthView.currentMonthInYear == i3);
            monthView.startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
            monthView.setSelectionValue(1.0f);
            CalendarActivity.this.updateRowSelections(monthView, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new MonthView(viewGroup.getContext()));
        }
    }

    public interface Callback {
        void onDateSelected(int i, int i2);
    }

    public class MonthView extends FrameLayout {
        boolean attached;
        int cellCount;
        int currentMonthInYear;
        int currentYear;
        int daysInMonth;
        GestureDetectorCompat gestureDetector;
        SparseArray imagesByDays;
        SparseArray messagesByDays;
        private SparseArray rowAnimators;
        private SparseArray rowSelectionPos;
        int startDayOfWeek;
        int startMonthTime;
        SimpleTextView titleView;

        public class AnonymousClass2 extends GestureDetector.SimpleOnGestureListener {
            final Context val$context;
            final CalendarActivity val$this$0;

            AnonymousClass2(CalendarActivity calendarActivity, Context context) {
                this.val$this$0 = calendarActivity;
                this.val$context = context;
            }

            private PeriodDay getDayAtCoord(float f, float f2) {
                PeriodDay periodDay;
                MonthView monthView = MonthView.this;
                if (monthView.messagesByDays == null) {
                    return null;
                }
                int i = monthView.startDayOfWeek;
                float measuredWidth = monthView.getMeasuredWidth() / 7.0f;
                float dp = AndroidUtilities.dp(52.0f);
                int dp2 = AndroidUtilities.dp(44.0f) / 2;
                int i2 = 0;
                for (int i3 = 0; i3 < MonthView.this.daysInMonth; i3++) {
                    float f3 = (i * measuredWidth) + (measuredWidth / 2.0f);
                    float dp3 = (i2 * dp) + (dp / 2.0f) + AndroidUtilities.dp(44.0f);
                    float f4 = dp2;
                    if (f >= f3 - f4 && f <= f3 + f4 && f2 >= dp3 - f4 && f2 <= dp3 + f4 && (periodDay = (PeriodDay) MonthView.this.messagesByDays.get(i3, null)) != null) {
                        return periodDay;
                    }
                    i++;
                    if (i >= 7) {
                        i2++;
                        i = 0;
                    }
                }
                return null;
            }

            public void lambda$onLongPress$0(BaseFragment baseFragment, PeriodDay periodDay) {
                CalendarActivity.this.lambda$onBackPressed$319();
                ((ChatActivity) baseFragment).jumpToDate(periodDay.date);
            }

            public void lambda$onLongPress$1(final PeriodDay periodDay, View view) {
                if (((BaseFragment) CalendarActivity.this).parentLayout != null && ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 3) {
                    final BaseFragment baseFragment = (BaseFragment) ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 3);
                    if (baseFragment instanceof ChatActivity) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$0(baseFragment, periodDay);
                            }
                        }, 300L);
                    }
                }
                CalendarActivity.this.finishPreviewFragment();
            }

            public void lambda$onLongPress$2(PeriodDay periodDay, View view) {
                CalendarActivity calendarActivity = CalendarActivity.this;
                calendarActivity.dateSelectedStart = calendarActivity.dateSelectedEnd = periodDay.date;
                CalendarActivity.this.inSelectionMode = true;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
                CalendarActivity.this.finishPreviewFragment();
            }

            public void lambda$onLongPress$3(View view) {
                if (((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 3) {
                    final BaseFragment baseFragment = (BaseFragment) ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 3);
                    if (baseFragment instanceof ChatActivity) {
                        CalendarActivity calendarActivity = CalendarActivity.this;
                        AlertsCreator.createClearDaysDialogAlert(calendarActivity, 1, calendarActivity.getMessagesController().getUser(Long.valueOf(CalendarActivity.this.dialogId)), null, false, new MessagesStorage.BooleanCallback() {
                            @Override
                            public void run(boolean z) {
                                CalendarActivity.this.lambda$onBackPressed$319();
                                ((ChatActivity) baseFragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                            }
                        }, null);
                    }
                }
                CalendarActivity.this.finishPreviewFragment();
            }

            public void lambda$onLongPress$4(View view) {
                CalendarActivity.this.finishPreviewFragment();
            }

            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                final PeriodDay dayAtCoord;
                String str;
                super.onLongPress(motionEvent);
                if (CalendarActivity.this.calendarType != 0 || AndroidUtilities.isTablet() || (dayAtCoord = getDayAtCoord(motionEvent.getX(), motionEvent.getY())) == null) {
                    return;
                }
                MonthView.this.performHapticFeedback(0);
                Bundle bundle = new Bundle();
                long j = CalendarActivity.this.dialogId;
                long j2 = CalendarActivity.this.dialogId;
                if (j > 0) {
                    str = "user_id";
                } else {
                    j2 = -j2;
                    str = "chat_id";
                }
                bundle.putLong(str, j2);
                bundle.putInt("start_from_date", dayAtCoord.date);
                bundle.putBoolean("need_remove_previous_same_chat_activity", false);
                ChatActivity chatActivity = new ChatActivity(bundle);
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(CalendarActivity.this.getParentActivity(), R.drawable.popup_fixed_alert, CalendarActivity.this.getResourceProvider());
                actionBarPopupWindowLayout.setBackgroundColor(CalendarActivity.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), true, false);
                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.JumpToDate), R.drawable.msg_message);
                actionBarMenuSubItem.setMinimumWidth(160);
                actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$1(dayAtCoord, view);
                    }
                });
                actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                if (CalendarActivity.this.canClearHistory) {
                    ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, false);
                    actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.SelectThisDay), R.drawable.msg_select);
                    actionBarMenuSubItem2.setMinimumWidth(160);
                    actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$2(dayAtCoord, view);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
                    ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, true);
                    actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString(R.string.ClearHistory), R.drawable.msg_delete);
                    actionBarMenuSubItem3.setMinimumWidth(160);
                    actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$3(view);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem3);
                }
                actionBarPopupWindowLayout.setFitItems(true);
                CalendarActivity.this.blurredView = new View(this.val$context) {
                    @Override
                    public void setAlpha(float f) {
                        super.setAlpha(f);
                        View view = CalendarActivity.this.fragmentView;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                };
                CalendarActivity.this.blurredView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$4(view);
                    }
                });
                CalendarActivity.this.blurredView.setVisibility(8);
                CalendarActivity.this.blurredView.setFitsSystemWindows(true);
                ((BaseFragment) CalendarActivity.this).parentLayout.getOverlayContainerView().addView(CalendarActivity.this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
                CalendarActivity.this.prepareBlurBitmap();
                CalendarActivity.this.presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayout);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                ChatActivity chatActivity;
                CalendarActivity calendarActivity;
                int i;
                CalendarActivity calendarActivity2;
                int i2;
                PeriodDay dayAtCoord;
                if (((BaseFragment) CalendarActivity.this).parentLayout == null) {
                    return false;
                }
                if (((CalendarActivity.this.calendarType == 1 && MonthView.this.messagesByDays != null) || CalendarActivity.this.storiesList != null) && (dayAtCoord = getDayAtCoord(motionEvent.getX(), motionEvent.getY())) != null && dayAtCoord.messageObject != null) {
                    CalendarActivity calendarActivity3 = CalendarActivity.this;
                    if (calendarActivity3.callback != null) {
                        if (calendarActivity3.storiesList != null) {
                            StoryViewer orCreateStoryViewer = CalendarActivity.this.getOrCreateStoryViewer();
                            Context context = MonthView.this.getContext();
                            MessageObject messageObject = dayAtCoord.messageObject;
                            orCreateStoryViewer.open(context, messageObject.storyItem, messageObject.getId(), CalendarActivity.this.storiesList, true, CalendarActivity.this.storiesPlaceProvider);
                        } else {
                            CalendarActivity.this.callback.onDateSelected(dayAtCoord.messageObject.getId(), dayAtCoord.startOffset);
                            CalendarActivity.this.lambda$onBackPressed$319();
                        }
                    }
                }
                MonthView monthView = MonthView.this;
                if (monthView.messagesByDays != null) {
                    if (CalendarActivity.this.inSelectionMode) {
                        PeriodDay dayAtCoord2 = getDayAtCoord(motionEvent.getX(), motionEvent.getY());
                        if (dayAtCoord2 != null) {
                            if (CalendarActivity.this.selectionAnimator != null) {
                                CalendarActivity.this.selectionAnimator.cancel();
                                CalendarActivity.this.selectionAnimator = null;
                            }
                            if (CalendarActivity.this.dateSelectedStart != 0 || CalendarActivity.this.dateSelectedEnd != 0) {
                                if (CalendarActivity.this.dateSelectedStart == dayAtCoord2.date && CalendarActivity.this.dateSelectedEnd == dayAtCoord2.date) {
                                    calendarActivity2 = CalendarActivity.this;
                                    i2 = calendarActivity2.dateSelectedEnd = 0;
                                } else if (CalendarActivity.this.dateSelectedStart == dayAtCoord2.date) {
                                    calendarActivity2 = CalendarActivity.this;
                                    i2 = calendarActivity2.dateSelectedEnd;
                                } else {
                                    if (CalendarActivity.this.dateSelectedEnd == dayAtCoord2.date) {
                                        CalendarActivity calendarActivity4 = CalendarActivity.this;
                                        calendarActivity4.dateSelectedEnd = calendarActivity4.dateSelectedStart;
                                    } else if (CalendarActivity.this.dateSelectedStart == CalendarActivity.this.dateSelectedEnd) {
                                        if (dayAtCoord2.date > CalendarActivity.this.dateSelectedEnd) {
                                            CalendarActivity.this.dateSelectedEnd = dayAtCoord2.date;
                                        } else {
                                            calendarActivity = CalendarActivity.this;
                                            i = dayAtCoord2.date;
                                            calendarActivity.dateSelectedStart = i;
                                        }
                                    }
                                    CalendarActivity.this.updateTitle();
                                    CalendarActivity.this.animateSelection();
                                }
                                calendarActivity2.dateSelectedStart = i2;
                                CalendarActivity.this.updateTitle();
                                CalendarActivity.this.animateSelection();
                            }
                            calendarActivity = CalendarActivity.this;
                            i = calendarActivity.dateSelectedEnd = dayAtCoord2.date;
                            calendarActivity.dateSelectedStart = i;
                            CalendarActivity.this.updateTitle();
                            CalendarActivity.this.animateSelection();
                        }
                    } else {
                        PeriodDay dayAtCoord3 = getDayAtCoord(motionEvent.getX(), motionEvent.getY());
                        if (dayAtCoord3 != null && ((BaseFragment) CalendarActivity.this).parentLayout != null && ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 2) {
                            BaseFragment baseFragment = (BaseFragment) ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 2);
                            if (baseFragment instanceof ChatActivity) {
                                CalendarActivity.this.lambda$onBackPressed$319();
                                chatActivity = (ChatActivity) baseFragment;
                                chatActivity.jumpToDate(dayAtCoord3.date);
                            }
                        } else if (dayAtCoord3 != null) {
                            CalendarActivity calendarActivity5 = CalendarActivity.this;
                            if (calendarActivity5.chatActivity != null) {
                                calendarActivity5.lambda$onBackPressed$319();
                                chatActivity = CalendarActivity.this.chatActivity;
                                chatActivity.jumpToDate(dayAtCoord3.date);
                            }
                        }
                    }
                }
                return false;
            }
        }

        public MonthView(Context context) {
            super(context);
            this.messagesByDays = new SparseArray();
            this.imagesByDays = new SparseArray();
            this.rowAnimators = new SparseArray();
            this.rowSelectionPos = new SparseArray();
            setWillNotDraw(false);
            this.titleView = new SimpleTextView(context);
            if (CalendarActivity.this.calendarType == 0 && CalendarActivity.this.canClearHistory) {
                this.titleView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public final boolean onLongClick(View view) {
                        boolean lambda$new$0;
                        lambda$new$0 = CalendarActivity.MonthView.this.lambda$new$0(view);
                        return lambda$new$0;
                    }
                });
                this.titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MonthView monthView;
                        MonthView monthView2 = MonthView.this;
                        if (monthView2.messagesByDays != null && CalendarActivity.this.inSelectionMode) {
                            int i = 0;
                            int i2 = -1;
                            int i3 = -1;
                            while (true) {
                                monthView = MonthView.this;
                                if (i >= monthView.daysInMonth) {
                                    break;
                                }
                                PeriodDay periodDay = (PeriodDay) monthView.messagesByDays.get(i, null);
                                if (periodDay != null) {
                                    if (i2 == -1) {
                                        i2 = periodDay.date;
                                    }
                                    i3 = periodDay.date;
                                }
                                i++;
                            }
                            if (i2 < 0 || i3 < 0) {
                                return;
                            }
                            CalendarActivity.this.dateSelectedStart = i2;
                            CalendarActivity.this.dateSelectedEnd = i3;
                            CalendarActivity.this.updateTitle();
                            CalendarActivity.this.animateSelection();
                        }
                    }
                });
            }
            this.titleView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            this.titleView.setTextSize(15);
            this.titleView.setTypeface(AndroidUtilities.bold());
            this.titleView.setGravity(17);
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(this.titleView, LayoutHelper.createFrame(-1, 28.0f, 0, 0.0f, 12.0f, 0.0f, 4.0f));
            GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new AnonymousClass2(CalendarActivity.this, context));
            this.gestureDetector = gestureDetectorCompat;
            gestureDetectorCompat.setIsLongpressEnabled(CalendarActivity.this.calendarType == 0);
        }

        public void lambda$animateRow$1(RowAnimationValue rowAnimationValue, float f, float f2, float f3, float f4, float f5, float f6, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            rowAnimationValue.startX = f + ((f2 - f) * floatValue);
            rowAnimationValue.endX = f3 + ((f4 - f3) * floatValue);
            rowAnimationValue.alpha = f5 + ((f6 - f5) * floatValue);
            invalidate();
        }

        public boolean lambda$new$0(View view) {
            if (this.messagesByDays == null) {
                return false;
            }
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                PeriodDay periodDay = (PeriodDay) this.messagesByDays.get(i3, null);
                if (periodDay != null) {
                    if (i == -1) {
                        i = periodDay.date;
                    }
                    i2 = periodDay.date;
                }
            }
            if (i >= 0 && i2 >= 0) {
                CalendarActivity.this.inSelectionMode = true;
                CalendarActivity.this.dateSelectedStart = i;
                CalendarActivity.this.dateSelectedEnd = i2;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
            }
            return false;
        }

        public void setSelectionValue(float f) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay periodDay = (PeriodDay) this.messagesByDays.get(i, null);
                    if (periodDay != null) {
                        float f2 = periodDay.fromSelProgress;
                        periodDay.selectProgress = f2 + ((periodDay.toSelProgress - f2) * f);
                        float f3 = periodDay.fromSelSEProgress;
                        periodDay.selectStartEndProgress = f3 + ((periodDay.toSelSEProgress - f3) * f);
                    }
                }
            }
            invalidate();
        }

        public void startSelectionAnimation(int i, int i2) {
            if (this.messagesByDays != null) {
                for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                    PeriodDay periodDay = (PeriodDay) this.messagesByDays.get(i3, null);
                    if (periodDay != null) {
                        periodDay.fromSelProgress = periodDay.selectProgress;
                        int i4 = periodDay.date;
                        periodDay.toSelProgress = (i4 < i || i4 > i2) ? 0.0f : 1.0f;
                        periodDay.fromSelSEProgress = periodDay.selectStartEndProgress;
                        if (i4 == i || i4 == i2) {
                            periodDay.toSelSEProgress = 1.0f;
                        } else {
                            periodDay.toSelSEProgress = 0.0f;
                        }
                    }
                }
            }
        }

        public void animateRow(final int i, int i2, int i3, final boolean z, boolean z2) {
            float f;
            float f2;
            final float f3;
            ValueAnimator valueAnimator = (ValueAnimator) this.rowAnimators.get(i);
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float measuredWidth = getMeasuredWidth() / 7.0f;
            RowAnimationValue rowAnimationValue = (RowAnimationValue) this.rowSelectionPos.get(i);
            if (rowAnimationValue != null) {
                f = rowAnimationValue.startX;
                f2 = rowAnimationValue.endX;
                f3 = rowAnimationValue.alpha;
            } else {
                f = (i2 * measuredWidth) + (measuredWidth / 2.0f);
                f2 = f;
                f3 = 0.0f;
            }
            float f4 = z ? (i2 * measuredWidth) + (measuredWidth / 2.0f) : f;
            float f5 = z ? (i3 * measuredWidth) + (measuredWidth / 2.0f) : f2;
            float f6 = z ? 1.0f : 0.0f;
            final RowAnimationValue rowAnimationValue2 = new RowAnimationValue(f, f2);
            this.rowSelectionPos.put(i, rowAnimationValue2);
            if (!z2) {
                rowAnimationValue2.startX = f4;
                rowAnimationValue2.endX = f5;
                rowAnimationValue2.alpha = f6;
                invalidate();
                return;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
            duration.setInterpolator(Easings.easeInOutQuad);
            final float f7 = f;
            final float f8 = f4;
            final float f9 = f2;
            final float f10 = f5;
            final float f11 = f6;
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    CalendarActivity.MonthView.this.lambda$animateRow$1(rowAnimationValue2, f7, f8, f9, f10, f3, f11, valueAnimator2);
                }
            });
            final float f12 = f4;
            final float f13 = f5;
            final float f14 = f6;
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animator) {
                    RowAnimationValue rowAnimationValue3 = rowAnimationValue2;
                    rowAnimationValue3.startX = f12;
                    rowAnimationValue3.endX = f13;
                    rowAnimationValue3.alpha = f14;
                    MonthView.this.invalidate();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    MonthView.this.rowAnimators.remove(i);
                    if (z) {
                        return;
                    }
                    MonthView.this.rowSelectionPos.remove(i);
                }
            });
            duration.start();
            this.rowAnimators.put(i, duration);
        }

        public void dismissRowAnimations(boolean z) {
            for (int i = 0; i < this.rowSelectionPos.size(); i++) {
                animateRow(this.rowSelectionPos.keyAt(i), 0, 0, false, z);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    ((ImageReceiver) this.imagesByDays.valueAt(i)).onAttachedToWindow();
                }
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    ((ImageReceiver) this.imagesByDays.valueAt(i)).onDetachedFromWindow();
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float f;
            int i;
            float f2;
            float f3;
            float f4;
            String num;
            float dp;
            TextPaint textPaint;
            float f5;
            float f6;
            PeriodDay periodDay;
            float f7;
            super.onDraw(canvas);
            int i2 = this.startDayOfWeek;
            float f8 = 7.0f;
            float measuredWidth = getMeasuredWidth() / 7.0f;
            float dp2 = AndroidUtilities.dp(52.0f);
            float f9 = 44.0f;
            int dp3 = AndroidUtilities.dp(44.0f);
            int i3 = 0;
            while (true) {
                f = 2.0f;
                if (i3 >= Math.ceil((this.startDayOfWeek + this.daysInMonth) / 7.0f)) {
                    break;
                }
                float dp4 = (i3 * dp2) + (dp2 / 2.0f) + AndroidUtilities.dp(44.0f);
                RowAnimationValue rowAnimationValue = (RowAnimationValue) this.rowSelectionPos.get(i3);
                if (rowAnimationValue != null) {
                    CalendarActivity.this.selectPaint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
                    CalendarActivity.this.selectPaint.setAlpha((int) (rowAnimationValue.alpha * 40.8f));
                    RectF rectF = AndroidUtilities.rectTmp;
                    float f10 = dp3 / 2.0f;
                    rectF.set(rowAnimationValue.startX - f10, dp4 - f10, rowAnimationValue.endX + f10, dp4 + f10);
                    float dp5 = AndroidUtilities.dp(32.0f);
                    canvas.drawRoundRect(rectF, dp5, dp5, CalendarActivity.this.selectPaint);
                }
                i3++;
            }
            int i4 = i2;
            int i5 = 0;
            int i6 = 0;
            while (i6 < this.daysInMonth) {
                float f11 = (i4 * measuredWidth) + (measuredWidth / f);
                float dp6 = (i5 * dp2) + (dp2 / f) + AndroidUtilities.dp(f9);
                int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
                SparseArray sparseArray = this.messagesByDays;
                PeriodDay periodDay2 = sparseArray != null ? (PeriodDay) sparseArray.get(i6, null) : null;
                int i7 = i6 + 1;
                if (currentTimeMillis < this.startMonthTime + (i7 * 86400) || (CalendarActivity.this.minDate > 0 && CalendarActivity.this.minDate > this.startMonthTime + ((i6 + 2) * 86400))) {
                    i = i5;
                    f2 = measuredWidth;
                    int alpha = CalendarActivity.this.textPaint.getAlpha();
                    CalendarActivity.this.textPaint.setAlpha((int) (alpha * 0.3f));
                    canvas.drawText(Integer.toString(i7), f11, AndroidUtilities.dp(5.0f) + dp6, CalendarActivity.this.textPaint);
                    CalendarActivity.this.textPaint.setAlpha(alpha);
                } else {
                    if (periodDay2 == null || !periodDay2.hasImage) {
                        f3 = dp6;
                        i = i5;
                        f2 = measuredWidth;
                        PeriodDay periodDay3 = periodDay2;
                        if (periodDay3 == null || periodDay3.selectStartEndProgress < 0.01f) {
                            f4 = f11;
                            num = Integer.toString(i7);
                            dp = AndroidUtilities.dp(5.0f) + f3;
                            textPaint = CalendarActivity.this.textPaint;
                        } else {
                            CalendarActivity.this.selectPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            CalendarActivity.this.selectPaint.setAlpha((int) (periodDay3.selectStartEndProgress * 255.0f));
                            canvas.drawCircle(f11, f3, AndroidUtilities.dp(44.0f) / 2.0f, CalendarActivity.this.selectPaint);
                            Paint paint = CalendarActivity.this.selectOutlinePaint;
                            int i8 = Theme.key_chat_messagePanelVoiceBackground;
                            paint.setColor(Theme.getColor(i8));
                            RectF rectF2 = AndroidUtilities.rectTmp;
                            rectF2.set(f11 - (AndroidUtilities.dp(44.0f) / 2.0f), f3 - (AndroidUtilities.dp(44.0f) / 2.0f), (AndroidUtilities.dp(44.0f) / 2.0f) + f11, f3 + (AndroidUtilities.dp(44.0f) / 2.0f));
                            f4 = f11;
                            canvas.drawArc(rectF2, -90.0f, periodDay3.selectStartEndProgress * 360.0f, false, CalendarActivity.this.selectOutlinePaint);
                            int dp7 = (int) (AndroidUtilities.dp(7.0f) * periodDay3.selectStartEndProgress);
                            CalendarActivity.this.selectPaint.setColor(Theme.getColor(i8));
                            CalendarActivity.this.selectPaint.setAlpha((int) (periodDay3.selectStartEndProgress * 255.0f));
                            canvas.drawCircle(f4, f3, (AndroidUtilities.dp(44.0f) - dp7) / 2.0f, CalendarActivity.this.selectPaint);
                            float f12 = periodDay3.selectStartEndProgress;
                            if (f12 != 1.0f) {
                                int alpha2 = CalendarActivity.this.textPaint.getAlpha();
                                CalendarActivity.this.textPaint.setAlpha((int) (alpha2 * (1.0f - f12)));
                                canvas.drawText(Integer.toString(i7), f4, AndroidUtilities.dp(5.0f) + f3, CalendarActivity.this.textPaint);
                                CalendarActivity.this.textPaint.setAlpha(alpha2);
                                int alpha3 = CalendarActivity.this.textPaint.getAlpha();
                                CalendarActivity.this.activeTextPaint.setAlpha((int) (alpha3 * f12));
                                canvas.drawText(Integer.toString(i7), f4, AndroidUtilities.dp(5.0f) + f3, CalendarActivity.this.activeTextPaint);
                                CalendarActivity.this.activeTextPaint.setAlpha(alpha3);
                            }
                            num = Integer.toString(i7);
                            dp = AndroidUtilities.dp(5.0f) + f3;
                            textPaint = CalendarActivity.this.activeTextPaint;
                        }
                    } else {
                        if (this.imagesByDays.get(i6) != null) {
                            if (CalendarActivity.this.checkEnterItems && !periodDay2.wasDrawn) {
                                periodDay2.enterAlpha = 0.0f;
                                periodDay2.startEnterDelay = Math.max(0.0f, ((getY() + dp6) / CalendarActivity.this.listView.getMeasuredHeight()) * 150.0f);
                            }
                            float f13 = periodDay2.startEnterDelay;
                            if (f13 > 0.0f) {
                                float f14 = f13 - 16.0f;
                                periodDay2.startEnterDelay = f14;
                                if (f14 < 0.0f) {
                                    periodDay2.startEnterDelay = 0.0f;
                                } else {
                                    invalidate();
                                }
                            }
                            if (periodDay2.startEnterDelay >= 0.0f) {
                                float f15 = periodDay2.enterAlpha;
                                if (f15 != 1.0f) {
                                    float f16 = f15 + 0.07272727f;
                                    periodDay2.enterAlpha = f16;
                                    if (f16 > 1.0f) {
                                        periodDay2.enterAlpha = 1.0f;
                                    } else {
                                        invalidate();
                                    }
                                }
                            }
                            f6 = periodDay2.enterAlpha;
                            if (f6 != 1.0f) {
                                canvas.save();
                                float f17 = (0.2f * f6) + 0.8f;
                                canvas.scale(f17, f17, f11, dp6);
                            }
                            int dp8 = (int) (AndroidUtilities.dp(f8) * periodDay2.selectProgress);
                            if (periodDay2.selectStartEndProgress >= 0.01f) {
                                CalendarActivity.this.selectPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                                CalendarActivity.this.selectPaint.setAlpha((int) (periodDay2.selectStartEndProgress * 255.0f));
                                canvas.drawCircle(f11, dp6, AndroidUtilities.dp(44.0f) / 2.0f, CalendarActivity.this.selectPaint);
                                CalendarActivity.this.selectOutlinePaint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
                                RectF rectF3 = AndroidUtilities.rectTmp;
                                rectF3.set(f11 - (AndroidUtilities.dp(44.0f) / 2.0f), dp6 - (AndroidUtilities.dp(44.0f) / 2.0f), (AndroidUtilities.dp(44.0f) / 2.0f) + f11, (AndroidUtilities.dp(44.0f) / 2.0f) + dp6);
                                periodDay = periodDay2;
                                f3 = dp6;
                                f7 = f11;
                                i = i5;
                                canvas.drawArc(rectF3, -90.0f, 360.0f * periodDay2.selectStartEndProgress, false, CalendarActivity.this.selectOutlinePaint);
                            } else {
                                periodDay = periodDay2;
                                f3 = dp6;
                                f7 = f11;
                                i = i5;
                            }
                            PeriodDay periodDay4 = periodDay;
                            ((ImageReceiver) this.imagesByDays.get(i6)).setAlpha(periodDay4.enterAlpha);
                            f11 = f7;
                            f2 = measuredWidth;
                            ((ImageReceiver) this.imagesByDays.get(i6)).setImageCoords(f11 - ((AndroidUtilities.dp(44.0f) - dp8) / 2.0f), f3 - ((AndroidUtilities.dp(44.0f) - dp8) / 2.0f), AndroidUtilities.dp(44.0f) - dp8, AndroidUtilities.dp(44.0f) - dp8);
                            ((ImageReceiver) this.imagesByDays.get(i6)).draw(canvas);
                            if (this.messagesByDays.get(i6) != null && ((PeriodDay) this.messagesByDays.get(i6)).messageObject != null && ((PeriodDay) this.messagesByDays.get(i6)).messageObject.hasMediaSpoilers()) {
                                float dp9 = (AndroidUtilities.dp(44.0f) - dp8) / 2.0f;
                                CalendarActivity.this.path.rewind();
                                CalendarActivity.this.path.addCircle(f11, f3, dp9, Path.Direction.CW);
                                canvas.save();
                                canvas.clipPath(CalendarActivity.this.path);
                                CalendarActivity.this.mediaSpoilerEffect.setColor(ColorUtils.setAlphaComponent(-1, (int) (Color.alpha(-1) * 0.325f * periodDay4.enterAlpha)));
                                CalendarActivity.this.mediaSpoilerEffect.setBounds((int) (f11 - dp9), (int) (f3 - dp9), (int) (f11 + dp9), (int) (dp9 + f3));
                                CalendarActivity.this.mediaSpoilerEffect.draw(canvas);
                                invalidate();
                                canvas.restore();
                            }
                            CalendarActivity.this.blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (periodDay4.enterAlpha * 80.0f)));
                            canvas.drawCircle(f11, f3, (AndroidUtilities.dp(44.0f) - dp8) / 2.0f, CalendarActivity.this.blackoutPaint);
                            periodDay4.wasDrawn = true;
                            f5 = 1.0f;
                            if (f6 != 1.0f) {
                                canvas.restore();
                            }
                        } else {
                            f3 = dp6;
                            i = i5;
                            f2 = measuredWidth;
                            f5 = 1.0f;
                            f6 = 1.0f;
                        }
                        if (f6 != f5) {
                            int alpha4 = CalendarActivity.this.textPaint.getAlpha();
                            CalendarActivity.this.textPaint.setAlpha((int) (alpha4 * (f5 - f6)));
                            canvas.drawText(Integer.toString(i7), f11, f3 + AndroidUtilities.dp(5.0f), CalendarActivity.this.textPaint);
                            CalendarActivity.this.textPaint.setAlpha(alpha4);
                            int alpha5 = CalendarActivity.this.textPaint.getAlpha();
                            CalendarActivity.this.activeTextPaint.setAlpha((int) (alpha5 * f6));
                            canvas.drawText(Integer.toString(i7), f11, f3 + AndroidUtilities.dp(5.0f), CalendarActivity.this.activeTextPaint);
                            CalendarActivity.this.activeTextPaint.setAlpha(alpha5);
                        } else {
                            f4 = f11;
                            num = Integer.toString(i7);
                            dp = AndroidUtilities.dp(5.0f) + f3;
                            textPaint = CalendarActivity.this.activeTextPaint;
                        }
                    }
                    canvas.drawText(num, f4, dp, textPaint);
                }
                i4++;
                if (i4 >= 7) {
                    i5 = i + 1;
                    i4 = 0;
                } else {
                    i5 = i;
                }
                i6 = i7;
                measuredWidth = f2;
                f8 = 7.0f;
                f9 = 44.0f;
                f = 2.0f;
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((this.cellCount * 52) + 44), 1073741824));
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return this.gestureDetector.onTouchEvent(motionEvent);
        }

        public void setDate(int r22, int r23, android.util.SparseArray r24, boolean r25) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CalendarActivity.MonthView.setDate(int, int, android.util.SparseArray, boolean):void");
        }
    }

    public class PeriodDay {
        int date;
        float enterAlpha;
        float fromSelProgress;
        float fromSelSEProgress;
        boolean hasImage;
        MessageObject messageObject;
        float selectProgress;
        float selectStartEndProgress;
        float startEnterDelay;
        int startOffset;
        ArrayList storyItems;
        float toSelProgress;
        float toSelSEProgress;
        boolean wasDrawn;

        private PeriodDay() {
            this.enterAlpha = 1.0f;
            this.startEnterDelay = 1.0f;
            this.hasImage = true;
        }

        PeriodDay(CalendarActivity calendarActivity, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public static final class RowAnimationValue {
        float alpha;
        float endX;
        float startX;

        RowAnimationValue(float f, float f2) {
            this.startX = f;
            this.endX = f2;
        }
    }

    public CalendarActivity(Bundle bundle, int i, int i2) {
        super(bundle);
        this.textPaint = new TextPaint(1);
        this.activeTextPaint = new TextPaint(1);
        this.textPaint2 = new TextPaint(1);
        this.selectOutlinePaint = new Paint(1);
        this.selectPaint = new Paint(1);
        this.blackoutPaint = new Paint(1);
        this.messagesByYearMounth = new SparseArray();
        this.startOffset = 0;
        this.path = new Path();
        this.mediaSpoilerEffect = new SpoilerEffect();
        this.photosVideosTypeFilter = i;
        if (i2 != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(i2 * 1000);
            this.selectedYear = calendar.get(1);
            this.selectedMonth = calendar.get(2);
        }
        this.selectOutlinePaint.setStyle(Paint.Style.STROKE);
        this.selectOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.selectOutlinePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    static void access$1300(CalendarActivity calendarActivity, MonthView monthView, boolean z) {
        calendarActivity.updateRowSelections(monthView, z);
    }

    static int access$3700(CalendarActivity calendarActivity) {
        return calendarActivity.currentAccount;
    }

    public void animateSelection() {
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CalendarActivity.this.lambda$animateSelection$4(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                for (int i = 0; i < CalendarActivity.this.listView.getChildCount(); i++) {
                    ((MonthView) CalendarActivity.this.listView.getChildAt(i)).startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
                }
            }
        });
        duration.start();
        this.selectionAnimator = duration;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            updateRowSelections((MonthView) this.listView.getChildAt(i), true);
        }
        for (int i2 = 0; i2 < this.listView.getCachedChildCount(); i2++) {
            MonthView monthView = (MonthView) this.listView.getCachedChildAt(i2);
            updateRowSelections(monthView, false);
            monthView.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            monthView.setSelectionValue(1.0f);
        }
        for (int i3 = 0; i3 < this.listView.getHiddenChildCount(); i3++) {
            MonthView monthView2 = (MonthView) this.listView.getHiddenChildAt(i3);
            updateRowSelections(monthView2, false);
            monthView2.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            monthView2.setSelectionValue(1.0f);
        }
        for (int i4 = 0; i4 < this.listView.getAttachedScrapChildCount(); i4++) {
            MonthView monthView3 = (MonthView) this.listView.getAttachedScrapChildAt(i4);
            updateRowSelections(monthView3, false);
            monthView3.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            monthView3.setSelectionValue(1.0f);
        }
    }

    public void checkLoadNext() {
        if (this.loading || this.endReached) {
            return;
        }
        int i = Integer.MAX_VALUE;
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof MonthView) {
                MonthView monthView = (MonthView) childAt;
                int i3 = (monthView.currentYear * 100) + monthView.currentMonthInYear;
                if (i3 < i) {
                    i = i3;
                }
            }
        }
        int i4 = this.minMontYear;
        if (((i4 / 100) * 12) + (i4 % 100) + 3 >= ((i / 100) * 12) + (i % 100)) {
            loadNext();
        }
    }

    public void lambda$animateSelection$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            ((MonthView) this.listView.getChildAt(i)).setSelectionValue(floatValue);
        }
    }

    public void lambda$createView$0(View view) {
        this.inSelectionMode = true;
        updateTitle();
    }

    public void lambda$createView$1(View view) {
        int i = this.lastDaysSelected;
        if (i != 0) {
            AlertsCreator.createClearDaysDialogAlert(this, i, getMessagesController().getUser(Long.valueOf(this.dialogId)), null, false, new MessagesStorage.BooleanCallback() {
                @Override
                public void run(boolean z) {
                    CalendarActivity.this.lambda$onBackPressed$319();
                    if (((BaseFragment) CalendarActivity.this).parentLayout != null && ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 2) {
                        BaseFragment baseFragment = (BaseFragment) ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 2);
                        if (baseFragment instanceof ChatActivity) {
                            ((ChatActivity) baseFragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                            return;
                        }
                        return;
                    }
                    CalendarActivity calendarActivity = CalendarActivity.this;
                    ChatActivity chatActivity = calendarActivity.chatActivity;
                    if (chatActivity != null) {
                        chatActivity.deleteHistory(calendarActivity.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                    }
                }
            }, null);
            return;
        }
        if (this.selectDaysHint == null) {
            HintView hintView = new HintView(this.contentView.getContext(), 8);
            this.selectDaysHint = hintView;
            hintView.setExtraTranslationY(AndroidUtilities.dp(24.0f));
            this.contentView.addView(this.selectDaysHint, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
            this.selectDaysHint.setText(LocaleController.getString(R.string.SelectDaysTooltip));
        }
        this.selectDaysHint.showForView(this.bottomBar, true);
    }

    public void lambda$loadNext$2(TLRPC.TL_error tL_error, TLObject tLObject, Calendar calendar) {
        int i;
        int i2;
        AnonymousClass1 anonymousClass1;
        if (tL_error == null) {
            TLRPC.TL_messages_searchResultsCalendar tL_messages_searchResultsCalendar = (TLRPC.TL_messages_searchResultsCalendar) tLObject;
            int i3 = 0;
            while (true) {
                i = 5;
                i2 = 2;
                anonymousClass1 = null;
                if (i3 >= tL_messages_searchResultsCalendar.periods.size()) {
                    break;
                }
                calendar.setTimeInMillis(tL_messages_searchResultsCalendar.periods.get(i3).date * 1000);
                int i4 = (calendar.get(1) * 100) + calendar.get(2);
                SparseArray sparseArray = (SparseArray) this.messagesByYearMounth.get(i4);
                if (sparseArray == null) {
                    sparseArray = new SparseArray();
                    this.messagesByYearMounth.put(i4, sparseArray);
                }
                PeriodDay periodDay = new PeriodDay(this, anonymousClass1);
                periodDay.messageObject = new MessageObject(this.currentAccount, tL_messages_searchResultsCalendar.messages.get(i3), false, false);
                periodDay.date = (int) (calendar.getTimeInMillis() / 1000);
                int i5 = this.startOffset + tL_messages_searchResultsCalendar.periods.get(i3).count;
                this.startOffset = i5;
                periodDay.startOffset = i5;
                int i6 = calendar.get(5) - 1;
                if (sparseArray.get(i6, null) == null || !((PeriodDay) sparseArray.get(i6, null)).hasImage) {
                    sparseArray.put(i6, periodDay);
                }
                int i7 = this.minMontYear;
                if (i4 < i7 || i7 == 0) {
                    this.minMontYear = i4;
                }
                i3++;
            }
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            int i8 = tL_messages_searchResultsCalendar.min_date;
            this.minDate = i8;
            while (true) {
                calendar.setTimeInMillis(i8 * 1000);
                calendar.set(11, 0);
                calendar.set(12, 0);
                calendar.set(13, 0);
                calendar.set(14, 0);
                if (calendar.getTimeInMillis() / 1000 > currentTimeMillis) {
                    break;
                }
                int i9 = (calendar.get(1) * 100) + calendar.get(i2);
                SparseArray sparseArray2 = (SparseArray) this.messagesByYearMounth.get(i9);
                if (sparseArray2 == null) {
                    sparseArray2 = new SparseArray();
                    this.messagesByYearMounth.put(i9, sparseArray2);
                }
                int i10 = calendar.get(i) - 1;
                if (sparseArray2.get(i10, null) == null) {
                    PeriodDay periodDay2 = new PeriodDay(this, anonymousClass1);
                    periodDay2.hasImage = false;
                    periodDay2.date = (int) (calendar.getTimeInMillis() / 1000);
                    sparseArray2.put(i10, periodDay2);
                }
                i8 += 86400;
                i = 5;
                i2 = 2;
            }
            this.loading = false;
            if (tL_messages_searchResultsCalendar.messages.isEmpty()) {
                this.endReached = true;
            } else {
                ArrayList<TLRPC.Message> arrayList = tL_messages_searchResultsCalendar.messages;
                this.lastId = arrayList.get(arrayList.size() - 1).id;
                this.endReached = false;
                checkLoadNext();
            }
            if (this.isOpened) {
                this.checkEnterItems = true;
            }
            this.listView.invalidate();
            int timeInMillis = ((int) (((calendar.getTimeInMillis() / 1000) - tL_messages_searchResultsCalendar.min_date) / 2629800)) + 1;
            this.adapter.notifyItemRangeChanged(0, this.monthCount);
            int i11 = this.monthCount;
            if (timeInMillis > i11) {
                this.adapter.notifyItemRangeInserted(i11 + 1, timeInMillis);
                this.monthCount = timeInMillis;
            }
            if (this.endReached) {
                resumeDelayedFragmentAnimation();
            }
        }
    }

    public void lambda$loadNext$3(final Calendar calendar, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                CalendarActivity.this.lambda$loadNext$2(tL_error, tLObject, calendar);
            }
        });
    }

    private void loadNext() {
        if (this.loading || this.endReached) {
            return;
        }
        if (this.storiesList != null) {
            updateFromStoriesList();
            this.storiesList.load(false, 100);
            this.loading = this.storiesList.isLoading();
            return;
        }
        this.loading = true;
        TLRPC.TL_messages_getSearchResultsCalendar tL_messages_getSearchResultsCalendar = new TLRPC.TL_messages_getSearchResultsCalendar();
        int i = this.photosVideosTypeFilter;
        tL_messages_getSearchResultsCalendar.filter = i == 1 ? new TLRPC.TL_inputMessagesFilterPhotos() : i == 2 ? new TLRPC.TL_inputMessagesFilterVideo() : new TLRPC.TL_inputMessagesFilterPhotoVideo();
        tL_messages_getSearchResultsCalendar.peer = getMessagesController().getInputPeer(this.dialogId);
        if (this.topicId != 0 && this.dialogId == getUserConfig().getClientUserId()) {
            tL_messages_getSearchResultsCalendar.flags |= 4;
            tL_messages_getSearchResultsCalendar.saved_peer_id = getMessagesController().getInputPeer(this.topicId);
        }
        tL_messages_getSearchResultsCalendar.offset_id = this.lastId;
        final Calendar calendar = Calendar.getInstance();
        this.listView.setItemAnimator(null);
        getConnectionsManager().sendRequest(tL_messages_getSearchResultsCalendar, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CalendarActivity.this.lambda$loadNext$3(calendar, tLObject, tL_error);
            }
        });
    }

    public void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int measuredWidth = (int) (this.parentLayout.getView().getMeasuredWidth() / 6.0f);
        int measuredHeight = (int) (this.parentLayout.getView().getMeasuredHeight() / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        this.parentLayout.getView().draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
        this.blurredView.setBackground(new BitmapDrawable(createBitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    public void updateColors() {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.activeTextPaint.setColor(-1);
        TextPaint textPaint = this.textPaint;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textPaint.setColor(Theme.getColor(i));
        this.textPaint2.setColor(Theme.getColor(i));
        this.actionBar.setTitleColor(Theme.getColor(i));
        this.backDrawable.setColor(Theme.getColor(i));
        this.actionBar.setItemsColor(Theme.getColor(i), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_listSelector), false);
    }

    private void updateFromStoriesList() {
        AnonymousClass1 anonymousClass1;
        this.loading = this.storiesList.isLoading();
        Calendar calendar = Calendar.getInstance();
        this.messagesByYearMounth.clear();
        this.minDate = Integer.MAX_VALUE;
        int i = 0;
        while (true) {
            anonymousClass1 = null;
            if (i >= this.storiesList.messageObjects.size()) {
                break;
            }
            MessageObject messageObject = (MessageObject) this.storiesList.messageObjects.get(i);
            this.minDate = Math.min(this.minDate, messageObject.messageOwner.date);
            calendar.setTimeInMillis(messageObject.messageOwner.date * 1000);
            int i2 = (calendar.get(1) * 100) + calendar.get(2);
            SparseArray sparseArray = (SparseArray) this.messagesByYearMounth.get(i2);
            if (sparseArray == null) {
                sparseArray = new SparseArray();
                this.messagesByYearMounth.put(i2, sparseArray);
            }
            int i3 = calendar.get(5) - 1;
            PeriodDay periodDay = (PeriodDay) sparseArray.get(i3);
            if (periodDay == null) {
                periodDay = new PeriodDay(this, anonymousClass1);
                periodDay.storyItems = new ArrayList();
            }
            periodDay.storyItems.add(Integer.valueOf(messageObject.getId()));
            periodDay.messageObject = messageObject;
            periodDay.date = (int) (calendar.getTimeInMillis() / 1000);
            sparseArray.put(i3, periodDay);
            int i4 = this.minMontYear;
            if (i2 < i4 || i4 == 0) {
                this.minMontYear = i2;
            }
            i++;
        }
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        for (int i5 = this.minDate; i5 < currentTimeMillis; i5 += 86400) {
            calendar.setTimeInMillis(i5 * 1000);
            calendar.set(11, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            int i6 = (calendar.get(1) * 100) + calendar.get(2);
            SparseArray sparseArray2 = (SparseArray) this.messagesByYearMounth.get(i6);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                this.messagesByYearMounth.put(i6, sparseArray2);
            }
            int i7 = calendar.get(5) - 1;
            if (sparseArray2.get(i7, null) == null) {
                PeriodDay periodDay2 = new PeriodDay(this, anonymousClass1);
                periodDay2.hasImage = false;
                periodDay2.date = (int) (calendar.getTimeInMillis() / 1000);
                sparseArray2.put(i7, periodDay2);
            }
        }
        this.endReached = this.storiesList.isFull();
        if (this.isOpened) {
            this.checkEnterItems = true;
        }
        this.listView.invalidate();
        int timeInMillis = ((int) (((calendar.getTimeInMillis() / 1000) - this.minDate) / 2629800)) + 1;
        this.adapter.notifyItemRangeChanged(0, this.monthCount);
        int i8 = this.monthCount;
        if (timeInMillis > i8) {
            this.adapter.notifyItemRangeInserted(i8 + 1, timeInMillis);
            this.monthCount = timeInMillis;
        }
        if (this.endReached) {
            resumeDelayedFragmentAnimation();
        }
    }

    public void updateRowSelections(MonthView monthView, boolean z) {
        int i;
        int i2;
        boolean z2;
        int i3;
        if (this.dateSelectedStart == 0 || this.dateSelectedEnd == 0) {
            monthView.dismissRowAnimations(z);
            return;
        }
        if (monthView.messagesByDays == null) {
            return;
        }
        if (!z) {
            monthView.dismissRowAnimations(false);
        }
        int i4 = monthView.startDayOfWeek;
        int i5 = 0;
        int i6 = -1;
        int i7 = -1;
        for (int i8 = 0; i8 < monthView.daysInMonth; i8++) {
            PeriodDay periodDay = (PeriodDay) monthView.messagesByDays.get(i8, null);
            if (periodDay == null || (i3 = periodDay.date) < this.dateSelectedStart || i3 > this.dateSelectedEnd) {
                i = i6;
                i2 = i7;
            } else {
                if (i6 == -1) {
                    i6 = i4;
                }
                i2 = i4;
                i = i6;
            }
            i4++;
            if (i4 >= 7) {
                if (i == -1 || i2 == -1) {
                    i2 = 0;
                    z2 = false;
                    i = 0;
                } else {
                    z2 = true;
                }
                monthView.animateRow(i5, i, i2, z2, z);
                i5++;
                i4 = 0;
                i6 = -1;
                i7 = -1;
            } else {
                i6 = i;
                i7 = i2;
            }
        }
        if (i6 == -1 || i7 == -1) {
            monthView.animateRow(i5, 0, 0, false, z);
        } else {
            monthView.animateRow(i5, i6, i7, true, z);
        }
    }

    public void updateTitle() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CalendarActivity.updateTitle():void");
    }

    @Override
    public View createView(Context context) {
        this.textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        TextPaint textPaint = this.textPaint;
        Paint.Align align = Paint.Align.CENTER;
        textPaint.setTextAlign(align);
        this.textPaint2.setTextSize(AndroidUtilities.dp(11.0f));
        this.textPaint2.setTextAlign(align);
        this.textPaint2.setTypeface(AndroidUtilities.bold());
        this.activeTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.activeTextPaint.setTypeface(AndroidUtilities.bold());
        this.activeTextPaint.setTextAlign(align);
        this.contentView = new FrameLayout(context) {
            int lastSize;

            @Override
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int measuredHeight = (getMeasuredHeight() + getMeasuredWidth()) << 16;
                if (this.lastSize != measuredHeight) {
                    this.lastSize = measuredHeight;
                    CalendarActivity.this.adapter.notifyDataSetChanged();
                }
            }
        };
        createActionBar(context);
        this.contentView.addView(this.actionBar);
        this.actionBar.setTitle(LocaleController.getString(R.string.Calendar));
        this.actionBar.setCastShadows(false);
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                CalendarActivity.this.checkEnterItems = false;
            }
        };
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setReverseLayout(true);
        RecyclerListView recyclerListView2 = this.listView;
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, null);
        this.adapter = calendarAdapter;
        recyclerListView2.setAdapter(calendarAdapter);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                CalendarActivity.this.checkLoadNext();
            }
        });
        boolean z = this.calendarType == 0 && this.canClearHistory;
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, z ? 48.0f : 0.0f));
        final String[] strArr = {LocaleController.getString(R.string.CalendarWeekNameShortMonday), LocaleController.getString(R.string.CalendarWeekNameShortTuesday), LocaleController.getString(R.string.CalendarWeekNameShortWednesday), LocaleController.getString(R.string.CalendarWeekNameShortThursday), LocaleController.getString(R.string.CalendarWeekNameShortFriday), LocaleController.getString(R.string.CalendarWeekNameShortSaturday), LocaleController.getString(R.string.CalendarWeekNameShortSunday)};
        final Drawable mutate = ContextCompat.getDrawable(context, R.drawable.header_shadow).mutate();
        this.contentView.addView(new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float measuredWidth = getMeasuredWidth() / 7.0f;
                for (int i = 0; i < 7; i++) {
                    canvas.drawText(strArr[i], (i * measuredWidth) + (measuredWidth / 2.0f), ((getMeasuredHeight() - AndroidUtilities.dp(2.0f)) / 2.0f) + AndroidUtilities.dp(5.0f), CalendarActivity.this.textPaint2);
                }
                mutate.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                mutate.draw(canvas);
            }
        }, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    if (CalendarActivity.this.dateSelectedStart == 0 && CalendarActivity.this.dateSelectedEnd == 0 && !CalendarActivity.this.inSelectionMode) {
                        CalendarActivity.this.lambda$onBackPressed$319();
                        return;
                    }
                    CalendarActivity.this.inSelectionMode = false;
                    CalendarActivity.this.dateSelectedStart = 0;
                    CalendarActivity.this.dateSelectedEnd = 0;
                    CalendarActivity.this.updateTitle();
                    CalendarActivity.this.animateSelection();
                }
            }
        });
        this.fragmentView = this.contentView;
        Calendar calendar = Calendar.getInstance();
        this.startFromYear = calendar.get(1);
        int i = calendar.get(2);
        this.startFromMonth = i;
        int i2 = this.selectedYear;
        if (i2 != 0) {
            int i3 = (((this.startFromYear - i2) * 12) + i) - this.selectedMonth;
            this.monthCount = i3 + 1;
            this.layoutManager.scrollToPositionWithOffset(i3, AndroidUtilities.dp(120.0f));
        }
        if (this.monthCount < 3) {
            this.monthCount = 3;
        }
        BackDrawable backDrawable = new BackDrawable(false);
        this.backDrawable = backDrawable;
        this.actionBar.setBackButtonDrawable(backDrawable);
        this.backDrawable.setRotation(0.0f, false);
        loadNext();
        updateColors();
        this.activeTextPaint.setColor(-1);
        if (z) {
            FrameLayout frameLayout = new FrameLayout(context) {
                @Override
                public void onDraw(Canvas canvas) {
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.getShadowHeight(), Theme.dividerPaint);
                }
            };
            this.bottomBar = frameLayout;
            frameLayout.setWillNotDraw(false);
            this.bottomBar.setPadding(0, AndroidUtilities.getShadowHeight(), 0, 0);
            this.bottomBar.setClipChildren(false);
            TextView textView = new TextView(context);
            this.selectDaysButton = textView;
            textView.setGravity(17);
            this.selectDaysButton.setTextSize(1, 15.0f);
            this.selectDaysButton.setTypeface(AndroidUtilities.bold());
            this.selectDaysButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    CalendarActivity.this.lambda$createView$0(view);
                }
            });
            this.selectDaysButton.setText(LocaleController.getString(R.string.SelectDays));
            this.selectDaysButton.setAllCaps(true);
            this.bottomBar.addView(this.selectDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.removeDaysButton = textView2;
            textView2.setGravity(17);
            this.removeDaysButton.setTextSize(1, 15.0f);
            this.removeDaysButton.setTypeface(AndroidUtilities.bold());
            this.removeDaysButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    CalendarActivity.this.lambda$createView$1(view);
                }
            });
            this.removeDaysButton.setAllCaps(true);
            this.removeDaysButton.setVisibility(8);
            this.bottomBar.addView(this.removeDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            this.contentView.addView(this.bottomBar, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
            TextView textView3 = this.selectDaysButton;
            int i4 = Theme.key_chat_fieldOverlayText;
            textView3.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(i4), 51), 2));
            TextView textView4 = this.removeDaysButton;
            int i5 = Theme.key_text_RedBold;
            textView4.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(i5), 51), 2));
            this.selectDaysButton.setTextColor(Theme.getColor(i4));
            this.removeDaysButton.setTextColor(Theme.getColor(i5));
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.storiesListUpdated && this.storiesList == ((StoriesController.StoriesList) objArr[0])) {
            updateFromStoriesList();
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public void didSetColor() {
                CalendarActivity.this.updateColors();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        new ArrayList();
        new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhite);
        new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlackText);
        new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_listSelector);
        return super.getThemeDescriptions();
    }

    @Override
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, null, true)) > 0.699999988079071d;
    }

    @Override
    public boolean needDelayOpenAnimation() {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (!this.inSelectionMode) {
            return super.onBackPressed();
        }
        this.inSelectionMode = false;
        this.dateSelectedEnd = 0;
        this.dateSelectedStart = 0;
        updateTitle();
        animateSelection();
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CalendarActivity.onFragmentCreate():boolean");
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.storiesList != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesListUpdated);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        View view;
        if (z && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground(null);
        }
    }

    @Override
    public void onTransitionAnimationProgress(boolean z, float f) {
        super.onTransitionAnimationProgress(z, f);
        View view = this.blurredView;
        if (view == null || view.getVisibility() != 0) {
            return;
        }
        if (z) {
            this.blurredView.setAlpha(1.0f - f);
        } else {
            this.blurredView.setAlpha(f);
        }
    }

    @Override
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        this.isOpened = true;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }
}
