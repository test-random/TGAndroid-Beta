package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CallLogActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity;

public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList activeGroupCalls;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    private ImageSpan iconIn;
    private ImageSpan iconMissed;
    private ImageSpan iconOut;
    private TLRPC.Chat lastCallChat;
    private TLRPC.User lastCallUser;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private boolean openTransitionStarted;
    private ActionBarMenuItem otherItem;
    private int prevPosition;
    private int prevTop;
    private Drawable redDrawable;
    private boolean scrollUpdated;
    private NumberTextView selectedDialogsCountTextView;
    private Long waitingForCallChatId;
    private ArrayList actionModeViews = new ArrayList();
    private ArrayList calls = new ArrayList();
    private ArrayList selectedIds = new ArrayList();
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    public class AnonymousClass2 extends RecyclerView.OnScrollListener {
        AnonymousClass2() {
        }

        public void lambda$onScrolled$0(CallLogRow callLogRow) {
            CallLogActivity.this.getCalls(((TLRPC.Message) callLogRow.calls.get(r3.size() - 1)).id, 100);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            boolean z;
            int findFirstVisibleItemPosition = CallLogActivity.this.layoutManager.findFirstVisibleItemPosition();
            boolean z2 = false;
            int abs = findFirstVisibleItemPosition == -1 ? 0 : Math.abs(CallLogActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            if (abs > 0) {
                int itemCount = CallLogActivity.this.listViewAdapter.getItemCount();
                if (!CallLogActivity.this.endReached && !CallLogActivity.this.loading && !CallLogActivity.this.calls.isEmpty() && abs + findFirstVisibleItemPosition >= itemCount - 5) {
                    final CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            CallLogActivity.AnonymousClass2.this.lambda$onScrolled$0(callLogRow);
                        }
                    });
                }
            }
            if (CallLogActivity.this.floatingButton.getVisibility() != 8) {
                View childAt = recyclerView.getChildAt(0);
                int top = childAt != null ? childAt.getTop() : 0;
                if (CallLogActivity.this.prevPosition == findFirstVisibleItemPosition) {
                    int i3 = CallLogActivity.this.prevTop - top;
                    z = top < CallLogActivity.this.prevTop;
                    if (Math.abs(i3) > 1) {
                        z2 = true;
                    }
                } else {
                    z2 = true;
                    z = findFirstVisibleItemPosition > CallLogActivity.this.prevPosition;
                }
                if (z2 && CallLogActivity.this.scrollUpdated) {
                    CallLogActivity.this.hideFloatingButton(z);
                }
                CallLogActivity.this.prevPosition = findFirstVisibleItemPosition;
                CallLogActivity.this.prevTop = top;
                CallLogActivity.this.scrollUpdated = true;
            }
        }
    }

    public class CallCell extends FrameLayout {
        private CheckBox2 checkBox;
        private ImageView imageView;
        private ProfileSearchCell profileSearchCell;

        public CallCell(Context context) {
            super(context);
            int i = Theme.key_windowBackgroundWhite;
            setBackgroundColor(Theme.getColor(i));
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell;
            profileSearchCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(32.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(32.0f), 0);
            this.profileSearchCell.setSublabelOffset(AndroidUtilities.dp(LocaleController.isRTL ? 2.0f : -2.0f), -AndroidUtilities.dp(4.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setAlpha(214);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
            this.imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    CallLogActivity.CallCell.this.lambda$new$0(view);
                }
            });
            this.imageView.setContentDescription(LocaleController.getString(R.string.Call));
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, i, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 42.0f, 32.0f, 42.0f, 0.0f));
        }

        public void lambda$new$0(View view) {
            CallLogRow callLogRow = (CallLogRow) view.getTag();
            TLRPC.UserFull userFull = CallLogActivity.this.getMessagesController().getUserFull(callLogRow.user.id);
            TLRPC.User user = CallLogActivity.this.lastCallUser = callLogRow.user;
            boolean z = callLogRow.video;
            VoIPHelper.startCall(user, z, z || (userFull != null && userFull.video_calls_available), CallLogActivity.this.getParentActivity(), null, CallLogActivity.this.getAccountInstance());
        }

        public void setChecked(boolean z, boolean z2) {
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 == null) {
                return;
            }
            checkBox2.setChecked(z, z2);
        }
    }

    public static class CallLogRow {
        public ArrayList calls;
        public int type;
        public TLRPC.User user;
        public boolean video;

        private CallLogRow() {
        }
    }

    public static class EmptyTextProgressView extends FrameLayout {
        private TextView emptyTextView1;
        private TextView emptyTextView2;
        private RLottieImageView imageView;
        private View progressView;

        public EmptyTextProgressView(Context context, View view) {
            super(context);
            addView(view, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView = view;
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.utyan_call, 120, 120);
            this.imageView.setAutoRepeat(false);
            addView(this.imageView, LayoutHelper.createFrame(140, 140.0f, 17, 52.0f, 4.0f, 52.0f, 60.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    CallLogActivity.EmptyTextProgressView.this.lambda$new$0(view2);
                }
            });
            TextView textView = new TextView(context);
            this.emptyTextView1 = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.emptyTextView1.setText(LocaleController.getString(R.string.NoRecentCalls));
            this.emptyTextView1.setTextSize(1, 20.0f);
            this.emptyTextView1.setTypeface(AndroidUtilities.bold());
            this.emptyTextView1.setGravity(17);
            addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 40.0f, 17.0f, 0.0f));
            this.emptyTextView2 = new TextView(context);
            String string = LocaleController.getString(R.string.NoRecentCallsInfo);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                string = string.replace('\n', ' ');
            }
            this.emptyTextView2.setText(string);
            this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
            this.emptyTextView2.setTextSize(1, 14.0f);
            this.emptyTextView2.setGravity(17);
            this.emptyTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.emptyTextView2, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 80.0f, 17.0f, 0.0f));
            view.setAlpha(0.0f);
            this.imageView.setAlpha(0.0f);
            this.emptyTextView1.setAlpha(0.0f);
            this.emptyTextView2.setAlpha(0.0f);
            setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$new$1;
                    lambda$new$1 = CallLogActivity.EmptyTextProgressView.lambda$new$1(view2, motionEvent);
                    return lambda$new$1;
                }
            });
        }

        public void lambda$new$0(View view) {
            if (this.imageView.isPlaying()) {
                return;
            }
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }

        public static boolean lambda$new$1(View view, MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        public void showProgress() {
            this.imageView.animate().alpha(0.0f).setDuration(150L).start();
            this.emptyTextView1.animate().alpha(0.0f).setDuration(150L).start();
            this.emptyTextView2.animate().alpha(0.0f).setDuration(150L).start();
            this.progressView.animate().alpha(1.0f).setDuration(150L).start();
        }

        public void showTextView() {
            this.imageView.animate().alpha(1.0f).setDuration(150L).start();
            this.emptyTextView1.animate().alpha(1.0f).setDuration(150L).start();
            this.emptyTextView2.animate().alpha(1.0f).setDuration(150L).start();
            this.progressView.animate().alpha(0.0f).setDuration(150L).start();
            this.imageView.playAnimation();
        }
    }

    public class GroupCallCell extends FrameLayout {
        private ProgressButton button;
        private TLRPC.Chat currentChat;
        private ProfileSearchCell profileSearchCell;

        public GroupCallCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            String string = LocaleController.getString(R.string.VoipChatJoin);
            this.button = new ProgressButton(context);
            int ceil = (int) Math.ceil(r0.getPaint().measureText(string));
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell;
            profileSearchCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(44.0f) + ceil : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(44.0f) + ceil, 0);
            this.profileSearchCell.setSublabelOffset(0, -AndroidUtilities.dp(1.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            this.button.setText(string);
            this.button.setTextSize(1, 14.0f);
            this.button.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.button.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
            this.button.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed), 16.0f);
            this.button.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
            addView(this.button, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
            this.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    CallLogActivity.GroupCallCell.this.lambda$new$0(view);
                }
            });
        }

        public void lambda$new$0(View view) {
            Long l = (Long) view.getTag();
            ChatObject.Call groupCall = CallLogActivity.this.getMessagesController().getGroupCall(l.longValue(), false);
            CallLogActivity callLogActivity = CallLogActivity.this;
            callLogActivity.lastCallChat = callLogActivity.getMessagesController().getChat(l);
            if (groupCall == null) {
                CallLogActivity.this.waitingForCallChatId = l;
                CallLogActivity.this.getMessagesController().loadFullChat(l.longValue(), 0, true);
            } else {
                TLRPC.Chat chat = CallLogActivity.this.lastCallChat;
                Activity parentActivity = CallLogActivity.this.getParentActivity();
                CallLogActivity callLogActivity2 = CallLogActivity.this;
                VoIPHelper.startCall(chat, null, null, false, parentActivity, callLogActivity2, callLogActivity2.getAccountInstance());
            }
        }

        public void setChat(TLRPC.Chat chat) {
            this.currentChat = chat;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int activeEndRow;
        private int activeHeaderRow;
        private int activeStartRow;
        private int callsEndRow;
        private int callsHeaderRow;
        private int callsStartRow;
        private int loadingCallsRow;
        private Context mContext;
        private int rowsCount;
        private int sectionRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        private void updateRows() {
            this.activeHeaderRow = -1;
            this.callsHeaderRow = -1;
            this.activeStartRow = -1;
            this.activeEndRow = -1;
            this.callsStartRow = -1;
            this.callsEndRow = -1;
            this.loadingCallsRow = -1;
            this.sectionRow = -1;
            this.rowsCount = 0;
            if (!CallLogActivity.this.activeGroupCalls.isEmpty()) {
                int i = this.rowsCount;
                int i2 = i + 1;
                this.rowsCount = i2;
                this.activeHeaderRow = i;
                this.activeStartRow = i2;
                int size = i2 + CallLogActivity.this.activeGroupCalls.size();
                this.rowsCount = size;
                this.activeEndRow = size;
            }
            if (CallLogActivity.this.calls.isEmpty()) {
                return;
            }
            if (this.activeHeaderRow != -1) {
                int i3 = this.rowsCount;
                this.sectionRow = i3;
                this.rowsCount = i3 + 2;
                this.callsHeaderRow = i3 + 1;
            }
            int i4 = this.rowsCount;
            this.callsStartRow = i4;
            int size2 = i4 + CallLogActivity.this.calls.size();
            this.rowsCount = size2;
            this.callsEndRow = size2;
            if (CallLogActivity.this.endReached) {
                return;
            }
            int i5 = this.rowsCount;
            this.rowsCount = i5 + 1;
            this.loadingCallsRow = i5;
        }

        @Override
        public int getItemCount() {
            return this.rowsCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == this.activeHeaderRow || i == this.callsHeaderRow) {
                return 3;
            }
            if (i >= this.callsStartRow && i < this.callsEndRow) {
                return 0;
            }
            if (i >= this.activeStartRow && i < this.activeEndRow) {
                return 4;
            }
            if (i == this.loadingCallsRow) {
                return 1;
            }
            return i == this.sectionRow ? 5 : 2;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 4;
        }

        @Override
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        @Override
        public void notifyItemInserted(int i) {
            updateRows();
            super.notifyItemInserted(i);
        }

        @Override
        public void notifyItemMoved(int i, int i2) {
            updateRows();
            super.notifyItemMoved(i, i2);
        }

        @Override
        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            super.notifyItemRangeChanged(i, i2);
        }

        @Override
        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
        }

        @Override
        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            super.notifyItemRangeInserted(i, i2);
        }

        @Override
        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            SpannableString spannableString;
            ImageSpan imageSpan;
            int i2;
            int i3;
            String string;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                int i4 = i - this.callsStartRow;
                CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i4);
                CallCell callCell = (CallCell) viewHolder.itemView;
                callCell.imageView.setImageResource(callLogRow.video ? R.drawable.profile_video : R.drawable.profile_phone);
                TLRPC.Message message = (TLRPC.Message) callLogRow.calls.get(0);
                String str = LocaleController.isRTL ? "\u202b" : "";
                if (callLogRow.calls.size() == 1) {
                    spannableString = new SpannableString(str + "  " + LocaleController.formatDateCallLog(message.date));
                } else {
                    spannableString = new SpannableString(String.format(str + "  (%d) %s", Integer.valueOf(callLogRow.calls.size()), LocaleController.formatDateCallLog(message.date)));
                }
                SpannableString spannableString2 = spannableString;
                int i5 = callLogRow.type;
                if (i5 == 0) {
                    imageSpan = CallLogActivity.this.iconOut;
                } else {
                    if (i5 != 1) {
                        if (i5 == 2) {
                            imageSpan = CallLogActivity.this.iconMissed;
                        }
                        callCell.profileSearchCell.setData(callLogRow.user, null, null, spannableString2, false, false);
                        callCell.profileSearchCell.useSeparator = i4 == CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached;
                        callCell.imageView.setTag(callLogRow);
                        return;
                    }
                    imageSpan = CallLogActivity.this.iconIn;
                }
                spannableString2.setSpan(imageSpan, str.length(), str.length() + 1, 0);
                callCell.profileSearchCell.setData(callLogRow.user, null, null, spannableString2, false, false);
                callCell.profileSearchCell.useSeparator = i4 == CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached;
                callCell.imageView.setTag(callLogRow);
                return;
            }
            if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == this.activeHeaderRow) {
                    i2 = R.string.VoipChatActiveChats;
                } else if (i != this.callsHeaderRow) {
                    return;
                } else {
                    i2 = R.string.VoipChatRecentCalls;
                }
                headerCell.setText(LocaleController.getString(i2));
                return;
            }
            if (itemViewType != 4) {
                return;
            }
            int i6 = i - this.activeStartRow;
            TLRPC.Chat chat = CallLogActivity.this.getMessagesController().getChat((Long) CallLogActivity.this.activeGroupCalls.get(i6));
            GroupCallCell groupCallCell = (GroupCallCell) viewHolder.itemView;
            groupCallCell.setChat(chat);
            groupCallCell.button.setTag(Long.valueOf(chat.id));
            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                i3 = !ChatObject.isPublic(chat) ? R.string.ChannelPrivate : R.string.ChannelPublic;
            } else {
                if (chat.has_geo) {
                    string = LocaleController.getString(R.string.MegaLocation);
                    String str2 = string;
                    ProfileSearchCell profileSearchCell = groupCallCell.profileSearchCell;
                    if (i6 != CallLogActivity.this.activeGroupCalls.size() - 1 && !CallLogActivity.this.endReached) {
                        r5 = true;
                    }
                    profileSearchCell.useSeparator = r5;
                    groupCallCell.profileSearchCell.setData(chat, null, null, str2, false, false);
                }
                i3 = !ChatObject.isPublic(chat) ? R.string.MegaPrivate : R.string.MegaPublic;
            }
            string = LocaleController.getString(i3).toLowerCase();
            String str22 = string;
            ProfileSearchCell profileSearchCell2 = groupCallCell.profileSearchCell;
            if (i6 != CallLogActivity.this.activeGroupCalls.size() - 1) {
                r5 = true;
            }
            profileSearchCell2.useSeparator = r5;
            groupCallCell.profileSearchCell.setData(chat, null, null, str22, false, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View callCell;
            if (i == 0) {
                callCell = new CallCell(this.mContext);
            } else if (i == 1) {
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                flickerLoadingView.setIsSingleCell(true);
                flickerLoadingView.setViewType(8);
                flickerLoadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                flickerLoadingView.showDate(false);
                callCell = flickerLoadingView;
            } else if (i == 2) {
                callCell = new TextInfoPrivacyCell(this.mContext);
                callCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else if (i != 3) {
                callCell = i != 4 ? new ShadowSectionCell(this.mContext) : new GroupCallCell(this.mContext);
            } else {
                callCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 2, false, CallLogActivity.this.getResourceProvider());
                callCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new RecyclerListView.Holder(callCell);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof CallCell) {
                ((CallCell) viewHolder.itemView).setChecked(CallLogActivity.this.isSelected(((CallLogRow) CallLogActivity.this.calls.get(viewHolder.getAdapterPosition() - this.callsStartRow)).calls), false);
            }
        }
    }

    private boolean addOrRemoveSelectedDialog(ArrayList arrayList, CallCell callCell) {
        if (arrayList.isEmpty()) {
            return false;
        }
        if (isSelected(arrayList)) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.selectedIds.remove(Integer.valueOf(((TLRPC.Message) arrayList.get(i)).id));
            }
            callCell.setChecked(false, true);
            showOrUpdateActionMode();
            return false;
        }
        int size2 = arrayList.size();
        for (int i2 = 0; i2 < size2; i2++) {
            Integer valueOf = Integer.valueOf(((TLRPC.Message) arrayList.get(i2)).id);
            if (!this.selectedIds.contains(valueOf)) {
                this.selectedIds.add(valueOf);
            }
        }
        callCell.setChecked(true, true);
        showOrUpdateActionMode();
        return true;
    }

    private void createActionMode() {
        if (this.actionBar.actionModeIsExist(null)) {
            return;
        }
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.bold());
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createActionMode$7;
                lambda$createActionMode$7 = CallLogActivity.lambda$createActionMode$7(view, motionEvent);
                return lambda$createActionMode$7;
            }
        });
        this.actionModeViews.add(createActionMode.addItemWithWidth(2, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString(R.string.Delete)));
    }

    private void deleteAllMessages(final boolean z) {
        TLRPC.TL_messages_deletePhoneCallHistory tL_messages_deletePhoneCallHistory = new TLRPC.TL_messages_deletePhoneCallHistory();
        tL_messages_deletePhoneCallHistory.revoke = z;
        getConnectionsManager().sendRequest(tL_messages_deletePhoneCallHistory, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CallLogActivity.this.lambda$deleteAllMessages$6(z, tLObject, tL_error);
            }
        });
    }

    public void getCalls(int i, int i2) {
        if (this.loading) {
            return;
        }
        this.loading = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null && !this.firstLoaded) {
            emptyTextProgressView.showProgress();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
        tL_messages_search.limit = i2;
        tL_messages_search.peer = new TLRPC.TL_inputPeerEmpty();
        tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
        tL_messages_search.q = "";
        tL_messages_search.offset_id = i;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CallLogActivity.this.lambda$getCalls$9(tLObject, tL_error);
            }
        }, 2), this.classGuid);
    }

    public void hideActionMode(boolean z) {
        this.actionBar.hideActionMode();
        this.selectedIds.clear();
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof CallCell) {
                ((CallCell) childAt).setChecked(false, z);
            }
        }
    }

    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden == z) {
            return;
        }
        this.floatingHidden = z;
        ObjectAnimator duration = ObjectAnimator.ofFloat(this.floatingButton, "translationY", z ? AndroidUtilities.dp(100.0f) : 0.0f).setDuration(300L);
        duration.setInterpolator(this.floatingInterpolator);
        this.floatingButton.setClickable(!z);
        duration.start();
    }

    public boolean isSelected(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (this.selectedIds.contains(Integer.valueOf(((TLRPC.Message) arrayList.get(i)).id))) {
                return true;
            }
        }
        return false;
    }

    public static boolean lambda$createActionMode$7(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$0(View view, int i) {
        if (!(view instanceof CallCell)) {
            if (view instanceof GroupCallCell) {
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", ((GroupCallCell) view).currentChat.id);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
                return;
            }
            return;
        }
        CallLogRow callLogRow = (CallLogRow) this.calls.get(i - this.listViewAdapter.callsStartRow);
        if (this.actionBar.isActionModeShowed()) {
            addOrRemoveSelectedDialog(callLogRow.calls, (CallCell) view);
            return;
        }
        Bundle bundle2 = new Bundle();
        bundle2.putLong("user_id", callLogRow.user.id);
        bundle2.putInt("message_id", ((TLRPC.Message) callLogRow.calls.get(0)).id);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
        presentFragment(new ChatActivity(bundle2), true);
    }

    public boolean lambda$createView$1(View view, int i) {
        if (!(view instanceof CallCell)) {
            return false;
        }
        addOrRemoveSelectedDialog(((CallLogRow) this.calls.get(i - this.listViewAdapter.callsStartRow)).calls, (CallCell) view);
        return true;
    }

    public void lambda$createView$2(TLRPC.User user, String str, ContactsActivity contactsActivity) {
        TLRPC.UserFull userFull = getMessagesController().getUserFull(user.id);
        this.lastCallUser = user;
        VoIPHelper.startCall(user, false, userFull != null && userFull.video_calls_available, getParentActivity(), null, getAccountInstance());
    }

    public void lambda$createView$3(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity = new ContactsActivity(bundle);
        contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            @Override
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity2) {
                CallLogActivity.this.lambda$createView$2(user, str, contactsActivity2);
            }
        });
        presentFragment(contactsActivity);
    }

    public void lambda$deleteAllMessages$6(boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_affectedFoundMessages tL_messages_affectedFoundMessages = (TLRPC.TL_messages_affectedFoundMessages) tLObject;
            TLRPC.TL_updateDeleteMessages tL_updateDeleteMessages = new TLRPC.TL_updateDeleteMessages();
            tL_updateDeleteMessages.messages = tL_messages_affectedFoundMessages.messages;
            tL_updateDeleteMessages.pts = tL_messages_affectedFoundMessages.pts;
            tL_updateDeleteMessages.pts_count = tL_messages_affectedFoundMessages.pts_count;
            TLRPC.TL_updates tL_updates = new TLRPC.TL_updates();
            tL_updates.updates.add(tL_updateDeleteMessages);
            getMessagesController().processUpdates(tL_updates, false);
            if (tL_messages_affectedFoundMessages.offset != 0) {
                deleteAllMessages(z);
            }
        }
    }

    public void lambda$getCalls$8(TLRPC.TL_error tL_error, TLObject tLObject) {
        CallLogRow callLogRow;
        int max = Math.max(this.listViewAdapter.callsStartRow, 0) + this.calls.size();
        if (tL_error == null) {
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            this.endReached = messages_messages.messages.isEmpty();
            for (int i = 0; i < messages_messages.users.size(); i++) {
                TLRPC.User user = messages_messages.users.get(i);
                longSparseArray.put(user.id, user);
            }
            if (this.calls.size() > 0) {
                ArrayList arrayList = this.calls;
                callLogRow = (CallLogRow) arrayList.get(arrayList.size() - 1);
            } else {
                callLogRow = null;
            }
            for (int i2 = 0; i2 < messages_messages.messages.size(); i2++) {
                TLRPC.Message message = messages_messages.messages.get(i2);
                TLRPC.MessageAction messageAction = message.action;
                if (messageAction != null && !(messageAction instanceof TLRPC.TL_messageActionHistoryClear)) {
                    int i3 = MessageObject.getFromChatId(message) == getUserConfig().getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason phoneCallDiscardReason = message.action.reason;
                    if (i3 == 1 && ((phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                        i3 = 2;
                    }
                    long fromChatId = MessageObject.getFromChatId(message);
                    if (fromChatId == getUserConfig().getClientUserId()) {
                        fromChatId = message.peer_id.user_id;
                    }
                    if (callLogRow == null || callLogRow.user.id != fromChatId || callLogRow.type != i3) {
                        if (callLogRow != null && !this.calls.contains(callLogRow)) {
                            this.calls.add(callLogRow);
                        }
                        callLogRow = new CallLogRow();
                        callLogRow.calls = new ArrayList();
                        callLogRow.user = (TLRPC.User) longSparseArray.get(fromChatId);
                        callLogRow.type = i3;
                        TLRPC.MessageAction messageAction2 = message.action;
                        callLogRow.video = messageAction2 != null && messageAction2.video;
                    }
                    callLogRow.calls.add(message);
                }
            }
            if (callLogRow != null && callLogRow.calls.size() > 0 && !this.calls.contains(callLogRow)) {
                this.calls.add(callLogRow);
            }
        } else {
            this.endReached = true;
        }
        this.loading = false;
        showItemsAnimated(max);
        if (!this.firstLoaded) {
            resumeDelayedFragmentAnimation();
        }
        this.firstLoaded = true;
        this.otherItem.setVisibility(this.calls.isEmpty() ? 8 : 0);
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void lambda$getCalls$9(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                CallLogActivity.this.lambda$getCalls$8(tL_error, tLObject);
            }
        });
    }

    public void lambda$getThemeDescriptions$10() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof CallCell) {
                    ((CallCell) childAt).profileSearchCell.update(0);
                }
            }
        }
    }

    public static void lambda$showDeleteAlert$4(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public void lambda$showDeleteAlert$5(boolean z, boolean[] zArr, AlertDialog alertDialog, int i) {
        if (z) {
            deleteAllMessages(zArr[0]);
            this.calls.clear();
            this.loading = false;
            this.endReached = true;
            this.otherItem.setVisibility(8);
            this.listViewAdapter.notifyDataSetChanged();
        } else {
            getMessagesController().deleteMessages(new ArrayList<>(this.selectedIds), null, null, 0L, 0, zArr[0], 0);
        }
        hideActionMode(false);
    }

    public void showDeleteAlert(final boolean z) {
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString(R.string.DeleteAllCalls));
            i = R.string.DeleteAllCallsText;
        } else {
            builder.setTitle(LocaleController.getString(R.string.DeleteCalls));
            i = R.string.DeleteSelectedCallsText;
        }
        builder.setMessage(LocaleController.getString(i));
        final boolean[] zArr = {false};
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        checkBoxCell.setText(LocaleController.getString(R.string.DeleteCallsForEveryone), "", false, false);
        checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(8.0f), 0);
        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
        checkBoxCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CallLogActivity.lambda$showDeleteAlert$4(zArr, view);
            }
        });
        builder.setView(frameLayout);
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                CallLogActivity.this.lambda$showDeleteAlert$5(z, zArr, alertDialog, i2);
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

    private void showItemsAnimated(final int i) {
        if (this.isPaused || !this.openTransitionStarted) {
            return;
        }
        final View view = null;
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof FlickerLoadingView) {
                view = childAt;
            }
        }
        if (view != null) {
            this.listView.removeView(view);
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                CallLogActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = CallLogActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = CallLogActivity.this.listView.getChildAt(i3);
                    RecyclerView.ViewHolder childViewHolder = CallLogActivity.this.listView.getChildViewHolder(childAt2);
                    if (childAt2 != view && CallLogActivity.this.listView.getChildAdapterPosition(childAt2) >= i && !(childAt2 instanceof GroupCallCell) && (!(childAt2 instanceof HeaderCell) || childViewHolder.getAdapterPosition() != CallLogActivity.this.listViewAdapter.activeHeaderRow)) {
                        childAt2.setAlpha(0.0f);
                        int min = (int) ((Math.min(CallLogActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt2.getTop())) / CallLogActivity.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt2, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay(min);
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                View view2 = view;
                if (view2 != null && view2.getParent() == null) {
                    CallLogActivity.this.listView.addView(view);
                    final RecyclerView.LayoutManager layoutManager = CallLogActivity.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(view);
                        View view3 = view;
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view3, (Property<View, Float>) View.ALPHA, view3.getAlpha(), 0.0f);
                        ofFloat2.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                view.setAlpha(1.0f);
                                layoutManager.stopIgnoringView(view);
                                CallLogActivity.this.listView.removeView(view);
                            }
                        });
                        ofFloat2.start();
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    private void showOrUpdateActionMode() {
        boolean z = true;
        if (!this.actionBar.isActionModeShowed()) {
            createActionMode();
            this.actionBar.showActionMode();
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.actionModeViews.size(); i++) {
                View view = (View) this.actionModeViews.get(i);
                view.setPivotY(ActionBar.getCurrentActionBarHeight() / 2);
                AndroidUtilities.clearDrawableAnimation(view);
                arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 0.1f, 1.0f));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200L);
            animatorSet.start();
            z = false;
        } else if (this.selectedIds.isEmpty()) {
            hideActionMode(true);
            return;
        }
        this.selectedDialogsCountTextView.setNumber(this.selectedIds.size(), z);
    }

    @Override
    public View createView(Context context) {
        Drawable mutate = getParentActivity().getResources().getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
        this.greenDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        Drawable drawable = this.greenDrawable;
        int i = Theme.key_calls_callReceivedGreenIcon;
        int color = Theme.getColor(i);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        drawable.setColorFilter(new PorterDuffColorFilter(color, mode));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        Resources resources = getParentActivity().getResources();
        int i2 = R.drawable.ic_call_received_green_18dp;
        Drawable mutate2 = resources.getDrawable(i2).mutate();
        this.greenDrawable2 = mutate2;
        mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        Drawable mutate3 = getParentActivity().getResources().getDrawable(i2).mutate();
        this.redDrawable = mutate3;
        mutate3.setBounds(0, 0, mutate3.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_fill_RedNormal), mode));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.Calls));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    if (((BaseFragment) CallLogActivity.this).actionBar.isActionModeShowed()) {
                        CallLogActivity.this.hideActionMode(true);
                        return;
                    } else {
                        CallLogActivity.this.lambda$onBackPressed$323();
                        return;
                    }
                }
                if (i3 == 1) {
                    CallLogActivity.this.showDeleteAlert(true);
                } else if (i3 == 2) {
                    CallLogActivity.this.showDeleteAlert(false);
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(10, R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        this.otherItem.addSubItem(1, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteAllCalls));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(8);
        this.flickerLoadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.flickerLoadingView.showDate(false);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, this.flickerLoadingView);
        this.emptyView = emptyTextProgressView;
        frameLayout2.addView(emptyTextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                CallLogActivity.this.lambda$createView$0(view, i3);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i3) {
                boolean lambda$createView$1;
                lambda$createView$1 = CallLogActivity.this.lambda$createView$1(view, i3);
                return lambda$createView$1;
            }
        });
        this.listView.setOnScrollListener(new AnonymousClass2());
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setVisibility(0);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        int i3 = Build.VERSION.SDK_INT;
        if (i3 < 21) {
            Drawable mutate4 = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            mutate4.setColorFilter(new PorterDuffColorFilter(-16777216, mode));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate4, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), mode));
        this.floatingButton.setImageResource(R.drawable.ic_call);
        this.floatingButton.setContentDescription(LocaleController.getString(R.string.Call));
        if (i3 >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        ImageView imageView2 = this.floatingButton;
        int i4 = i3 >= 21 ? 56 : 60;
        float f = i3 >= 21 ? 56.0f : 60.0f;
        boolean z = LocaleController.isRTL;
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(i4, f, (z ? 3 : 5) | 80, z ? 14.0f : 0.0f, 0.0f, z ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CallLogActivity.this.lambda$createView$3(view);
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Long l;
        ListAdapter listAdapter;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (this.firstLoaded && !((Boolean) objArr[2]).booleanValue()) {
                Iterator it = ((ArrayList) objArr[1]).iterator();
                while (it.hasNext()) {
                    MessageObject messageObject = (MessageObject) it.next();
                    if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                        long fromChatId = messageObject.getFromChatId();
                        long j = fromChatId == getUserConfig().getClientUserId() ? messageObject.messageOwner.peer_id.user_id : fromChatId;
                        int i3 = fromChatId == getUserConfig().getClientUserId() ? 0 : 1;
                        TLRPC.PhoneCallDiscardReason phoneCallDiscardReason = messageObject.messageOwner.action.reason;
                        if (i3 == 1 && ((phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                            i3 = 2;
                        }
                        if (this.calls.size() > 0) {
                            CallLogRow callLogRow = (CallLogRow) this.calls.get(0);
                            if (callLogRow.user.id == j && callLogRow.type == i3) {
                                callLogRow.calls.add(0, messageObject.messageOwner);
                                this.listViewAdapter.notifyItemChanged(0);
                            }
                        }
                        CallLogRow callLogRow2 = new CallLogRow();
                        ArrayList arrayList = new ArrayList();
                        callLogRow2.calls = arrayList;
                        arrayList.add(messageObject.messageOwner);
                        callLogRow2.user = getMessagesController().getUser(Long.valueOf(j));
                        callLogRow2.type = i3;
                        callLogRow2.video = messageObject.isVideoCall();
                        this.calls.add(0, callLogRow2);
                        this.listViewAdapter.notifyItemInserted(0);
                    }
                }
                ActionBarMenuItem actionBarMenuItem = this.otherItem;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setVisibility(this.calls.isEmpty() ? 8 : 0);
                    return;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagesDeleted) {
            if (!this.firstLoaded || ((Boolean) objArr[2]).booleanValue()) {
                return;
            }
            ArrayList arrayList2 = (ArrayList) objArr[0];
            Iterator it2 = this.calls.iterator();
            while (it2.hasNext()) {
                CallLogRow callLogRow3 = (CallLogRow) it2.next();
                Iterator it3 = callLogRow3.calls.iterator();
                while (it3.hasNext()) {
                    if (arrayList2.contains(Integer.valueOf(((TLRPC.Message) it3.next()).id))) {
                        it3.remove();
                        r3 = 1;
                    }
                }
                if (callLogRow3.calls.size() == 0) {
                    it2.remove();
                }
            }
            if (r3 == 0 || (listAdapter = this.listViewAdapter) == null) {
                return;
            }
        } else {
            if (i != NotificationCenter.activeGroupCallsUpdated) {
                if (i == NotificationCenter.chatInfoDidLoad) {
                    Long l2 = this.waitingForCallChatId;
                    if (l2 == null || ((TLRPC.ChatFull) objArr[0]).id != l2.longValue() || getMessagesController().getGroupCall(this.waitingForCallChatId.longValue(), true) == null) {
                        return;
                    }
                } else if (i != NotificationCenter.groupCallUpdated || (l = this.waitingForCallChatId) == null || !l.equals((Long) objArr[0])) {
                    return;
                }
                VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
                this.waitingForCallChatId = null;
                return;
            }
            this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
            listAdapter = this.listViewAdapter;
            if (listAdapter == null) {
                return;
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                CallLogActivity.this.lambda$getThemeDescriptions$10();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        int i = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CallCell.class, HeaderCell.class, GroupCallCell.class}, null, null, null, i));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i2 = ThemeDescription.FLAG_BACKGROUND;
        int i3 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i3));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        int i4 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3));
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, null, Theme.key_calls_callReceivedGreenIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, null, Theme.key_fill_RedNormal));
        arrayList.add(new ThemeDescription(this.flickerLoadingView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        return arrayList;
    }

    @Override
    public boolean needDelayOpenAnimation() {
        return true;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getCalls(0, 50);
        this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    @Override
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        boolean z;
        if (i == 101 || i == 102 || i == 103) {
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    z = true;
                    break;
                } else {
                    if (iArr[i2] != 0) {
                        z = false;
                        break;
                    }
                    i2++;
                }
            }
            if (iArr.length <= 0 || !z) {
                VoIPHelper.permissionDenied(getParentActivity(), null, i);
            } else if (i == 103) {
                VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
            } else {
                TLRPC.UserFull userFull = this.lastCallUser != null ? getMessagesController().getUserFull(this.lastCallUser.id) : null;
                VoIPHelper.startCall(this.lastCallUser, i == 102, i == 102 || (userFull != null && userFull.video_calls_available), getParentActivity(), null, getAccountInstance());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (z) {
            this.openTransitionStarted = true;
        }
    }
}
