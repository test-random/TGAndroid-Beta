package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.RecyclerListView;

public class JoinCallAlert extends BottomSheet {
    private static ArrayList cachedChats;
    private static long lastCacheDid;
    private static long lastCacheTime;
    private static int lastCachedAccount;
    private boolean animationInProgress;
    private ArrayList chats;
    private TLRPC.Peer currentPeer;
    private int currentType;
    private JoinCallAlertDelegate delegate;
    private BottomSheetCell doneButton;
    private boolean ignoreLayout;
    private RecyclerListView listView;
    private int[] location;
    private TextView messageTextView;
    private boolean schedule;
    private int scrollOffsetY;
    private TLRPC.InputPeer selectAfterDismiss;
    private TLRPC.Peer selectedPeer;
    private Drawable shadowDrawable;
    private TextView textView;

    public class BottomSheetCell extends FrameLayout {
        private View background;
        private boolean hasBackground;
        private CharSequence text;
        private TextView[] textView;

        public BottomSheetCell(Context context, boolean z) {
            super(context);
            this.textView = new TextView[2];
            this.hasBackground = !z;
            setBackground(null);
            View view = new View(context);
            this.background = view;
            if (this.hasBackground) {
                view.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
            }
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, z ? 0.0f : 16.0f, 16.0f, 16.0f));
            for (int i = 0; i < 2; i++) {
                this.textView[i] = new TextView(context);
                this.textView[i].setFocusable(false);
                this.textView[i].setLines(1);
                this.textView[i].setSingleLine(true);
                this.textView[i].setGravity(1);
                this.textView[i].setEllipsize(TextUtils.TruncateAt.END);
                this.textView[i].setGravity(17);
                if (this.hasBackground) {
                    this.textView[i].setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
                    this.textView[i].setTypeface(AndroidUtilities.bold());
                } else {
                    this.textView[i].setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                }
                this.textView[i].setImportantForAccessibility(2);
                this.textView[i].setTextSize(1, 14.0f);
                this.textView[i].setPadding(0, 0, 0, this.hasBackground ? 0 : AndroidUtilities.dp(13.0f));
                addView(this.textView[i], LayoutHelper.createFrame(-2, -2.0f, 17, 24.0f, 0.0f, 24.0f, 0.0f));
                if (i == 1) {
                    this.textView[i].setAlpha(0.0f);
                }
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.Button");
            accessibilityNodeInfo.setClickable(true);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), 1073741824));
        }

        public void setText(CharSequence charSequence, boolean z) {
            this.text = charSequence;
            if (!z) {
                this.textView[0].setText(charSequence);
                return;
            }
            this.textView[1].setText(charSequence);
            JoinCallAlert.this.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180L);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            TextView textView = this.textView[0];
            Property property = View.ALPHA;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property, 1.0f, 0.0f);
            TextView textView2 = this.textView[0];
            Property property2 = View.TRANSLATION_Y;
            animatorSet.playTogether(ofFloat, ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property2, 0.0f, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.textView[1], (Property<TextView, Float>) property, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.textView[1], (Property<TextView, Float>) property2, AndroidUtilities.dp(10.0f), 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    JoinCallAlert.this.animationInProgress = false;
                    TextView textView3 = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = textView3;
                }
            });
            animatorSet.start();
        }
    }

    public interface JoinCallAlertDelegate {
        void didSelectChat(TLRPC.InputPeer inputPeer, boolean z, boolean z2, boolean z3);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return JoinCallAlert.this.chats.size();
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLObject chat;
            String str;
            long peerId = MessageObject.getPeerId((TLRPC.Peer) JoinCallAlert.this.chats.get(i));
            JoinCallAlert joinCallAlert = JoinCallAlert.this;
            if (peerId > 0) {
                chat = MessagesController.getInstance(((BottomSheet) joinCallAlert).currentAccount).getUser(Long.valueOf(peerId));
                str = LocaleController.getString(R.string.VoipGroupPersonalAccount);
            } else {
                chat = MessagesController.getInstance(((BottomSheet) joinCallAlert).currentAccount).getChat(Long.valueOf(-peerId));
                str = null;
            }
            int i2 = JoinCallAlert.this.currentType;
            View view = viewHolder.itemView;
            if (i2 == 0) {
                ((ShareDialogCell) view).setDialog(peerId, peerId == MessageObject.getPeerId(JoinCallAlert.this.selectedPeer), null);
            } else {
                ((GroupCreateUserCell) view).setObject(chat, null, str, i != getItemCount() - 1);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View groupCreateUserCell;
            if (JoinCallAlert.this.currentType == 0) {
                groupCreateUserCell = new ShareDialogCell(this.context, 2, null);
                groupCreateUserCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0f), AndroidUtilities.dp(100.0f)));
            } else {
                groupCreateUserCell = new GroupCreateUserCell(this.context, 2, 0, false, JoinCallAlert.this.currentType == 2, null);
            }
            return new RecyclerListView.Holder(groupCreateUserCell);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            viewHolder.getAdapterPosition();
            long peerId = MessageObject.getPeerId(JoinCallAlert.this.selectedPeer);
            View view = viewHolder.itemView;
            if (!(view instanceof GroupCreateUserCell)) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) view;
                shareDialogCell.setChecked(peerId == shareDialogCell.getCurrentDialog(), false);
            } else {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
                Object object = groupCreateUserCell.getObject();
                groupCreateUserCell.setChecked(peerId == (object != null ? object instanceof TLRPC.Chat ? -((TLRPC.Chat) object).id : ((TLRPC.User) object).id : 0L), false);
            }
        }
    }

    private JoinCallAlert(Context context, long j, ArrayList arrayList, int i, TLRPC.Peer peer, final JoinCallAlertDelegate joinCallAlertDelegate) {
        super(context, false);
        int color;
        ViewGroup viewGroup;
        TextView textView;
        int i2;
        TextView textView2;
        ViewGroup.LayoutParams createFrame;
        boolean z;
        TextView textView3;
        int i3;
        TextView textView4;
        ViewGroup.LayoutParams createFrame2;
        TextView textView5;
        int i4;
        TLRPC.Peer peer2;
        this.location = new int[2];
        setApplyBottomPadding(false);
        this.chats = new ArrayList(arrayList);
        this.delegate = joinCallAlertDelegate;
        this.currentType = i;
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        if (i != 2) {
            color = Theme.getColor(Theme.key_dialogBackground);
            mutate.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            this.selectedPeer = (TLRPC.Peer) this.chats.get(0);
        } else if (VoIPService.getSharedInstance() != null) {
            long selfId = VoIPService.getSharedInstance().getSelfId();
            int size = this.chats.size();
            for (int i5 = 0; i5 < size; i5++) {
                peer2 = (TLRPC.Peer) this.chats.get(i5);
                if (MessageObject.getPeerId(peer2) == selfId) {
                    this.currentPeer = peer2;
                    this.selectedPeer = peer2;
                    break;
                }
            }
            Drawable drawable = this.shadowDrawable;
            color = Theme.getColor(Theme.key_voipgroup_inviteMembersBackground);
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        } else {
            if (peer != null) {
                long peerId = MessageObject.getPeerId(peer);
                int size2 = this.chats.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    peer2 = (TLRPC.Peer) this.chats.get(i6);
                    if (MessageObject.getPeerId(peer2) == peerId) {
                        this.currentPeer = peer2;
                        this.selectedPeer = peer2;
                        break;
                    }
                }
            } else {
                this.selectedPeer = (TLRPC.Peer) this.chats.get(0);
            }
            Drawable drawable2 = this.shadowDrawable;
            color = Theme.getColor(Theme.key_voipgroup_inviteMembersBackground);
            drawable2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        fixNavigationBar(color);
        if (this.currentType == 0) {
            LinearLayout linearLayout = new LinearLayout(context) {
                boolean sorted;

                @Override
                protected void onMeasure(int i7, int i8) {
                    if (JoinCallAlert.this.currentType == 0) {
                        int size3 = View.MeasureSpec.getSize(i7);
                        int size4 = JoinCallAlert.this.chats.size() * AndroidUtilities.dp(95.0f);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) JoinCallAlert.this.listView.getLayoutParams();
                        if (size4 > size3) {
                            layoutParams.width = -1;
                            layoutParams.gravity = 51;
                            if (!this.sorted) {
                                if (JoinCallAlert.this.selectedPeer != null) {
                                    JoinCallAlert.this.chats.remove(JoinCallAlert.this.selectedPeer);
                                    JoinCallAlert.this.chats.add(0, JoinCallAlert.this.selectedPeer);
                                }
                                this.sorted = true;
                            }
                        } else {
                            layoutParams.width = -2;
                            layoutParams.gravity = 49;
                            if (!this.sorted) {
                                if (JoinCallAlert.this.selectedPeer != null) {
                                    int max = JoinCallAlert.this.chats.size() % 2 == 0 ? Math.max(0, (JoinCallAlert.this.chats.size() / 2) - 1) : JoinCallAlert.this.chats.size() / 2;
                                    JoinCallAlert.this.chats.remove(JoinCallAlert.this.selectedPeer);
                                    JoinCallAlert.this.chats.add(max, JoinCallAlert.this.selectedPeer);
                                }
                                this.sorted = true;
                            }
                        }
                    }
                    super.onMeasure(i7, i8);
                }
            };
            linearLayout.setOrientation(1);
            NestedScrollView nestedScrollView = new NestedScrollView(context);
            nestedScrollView.addView(linearLayout);
            setCustomView(nestedScrollView);
            viewGroup = linearLayout;
        } else {
            FrameLayout frameLayout = new FrameLayout(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    JoinCallAlert.this.shadowDrawable.setBounds(0, JoinCallAlert.this.scrollOffsetY - ((BottomSheet) JoinCallAlert.this).backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    JoinCallAlert.this.shadowDrawable.draw(canvas);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() != 0 || JoinCallAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= JoinCallAlert.this.scrollOffsetY) {
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                    JoinCallAlert.this.dismiss();
                    return true;
                }

                @Override
                protected void onLayout(boolean z2, int i7, int i8, int i9, int i10) {
                    super.onLayout(z2, i7, i8, i9, i10);
                    JoinCallAlert.this.updateLayout();
                }

                @Override
                protected void onMeasure(int i7, int i8) {
                    int size3 = View.MeasureSpec.getSize(i8);
                    if (Build.VERSION.SDK_INT >= 21) {
                        size3 -= AndroidUtilities.statusBarHeight;
                    }
                    measureChildWithMargins(JoinCallAlert.this.messageTextView, i7, 0, i8, 0);
                    int measuredHeight = JoinCallAlert.this.messageTextView.getMeasuredHeight();
                    ((FrameLayout.LayoutParams) JoinCallAlert.this.listView.getLayoutParams()).topMargin = AndroidUtilities.dp(65.0f) + measuredHeight;
                    getMeasuredWidth();
                    int dp = AndroidUtilities.dp(80.0f) + (JoinCallAlert.this.chats.size() * AndroidUtilities.dp(58.0f)) + ((BottomSheet) JoinCallAlert.this).backgroundPaddingTop + AndroidUtilities.dp(55.0f) + measuredHeight;
                    int i9 = size3 / 5;
                    int i10 = dp < i9 * 3 ? size3 - dp : i9 * 2;
                    if (JoinCallAlert.this.listView.getPaddingTop() != i10) {
                        JoinCallAlert.this.ignoreLayout = true;
                        JoinCallAlert.this.listView.setPadding(0, i10, 0, 0);
                        JoinCallAlert.this.ignoreLayout = false;
                    }
                    super.onMeasure(i7, View.MeasureSpec.makeMeasureSpec(size3, 1073741824));
                }

                @Override
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return !JoinCallAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
                }

                @Override
                public void requestLayout() {
                    if (JoinCallAlert.this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.containerView = frameLayout;
            frameLayout.setWillNotDraw(false);
            ViewGroup viewGroup2 = this.containerView;
            int i7 = this.backgroundPaddingLeft;
            viewGroup2.setPadding(i7, 0, i7, 0);
            viewGroup = frameLayout;
        }
        final TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public void requestLayout() {
                if (JoinCallAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), this.currentType == 0 ? 0 : 1, false));
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setSelectorDrawableColor(0);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i8, int i9) {
                JoinCallAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i8) {
                JoinCallAlert.this.lambda$new$6(chat, view, i8);
            }
        });
        RecyclerListView recyclerListView2 = this.listView;
        if (i != 0) {
            viewGroup.addView(recyclerListView2, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 100.0f, 0.0f, 80.0f));
        } else {
            recyclerListView2.setSelectorDrawableColor(0);
            this.listView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        }
        if (i == 0) {
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            rLottieImageView.setAutoRepeat(true);
            rLottieImageView.setAnimation(R.raw.utyan_schedule, 120, 120);
            rLottieImageView.playAnimation();
            viewGroup.addView(rLottieImageView, LayoutHelper.createLinear(160, 160, 49, 17, 8, 17, 0));
        }
        TextView textView6 = new TextView(context);
        this.textView = textView6;
        textView6.setTypeface(AndroidUtilities.bold());
        this.textView.setTextSize(1, 20.0f);
        this.textView.setTextColor(Theme.getColor(i == 2 ? Theme.key_voipgroup_nameText : Theme.key_dialogTextBlack));
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (i == 0) {
            if (ChatObject.isChannelOrGiga(chat)) {
                textView5 = this.textView;
                i4 = R.string.StartVoipChannelTitle;
            } else {
                textView5 = this.textView;
                i4 = R.string.StartVoipChatTitle;
            }
            textView5.setText(LocaleController.getString(i4));
            textView2 = this.textView;
            createFrame = LayoutHelper.createLinear(-2, -2, 49, 23, 16, 23, 0);
        } else {
            if (i == 2) {
                textView = this.textView;
                i2 = R.string.VoipGroupDisplayAs;
            } else if (ChatObject.isChannelOrGiga(chat)) {
                textView = this.textView;
                i2 = R.string.VoipChannelJoinAs;
            } else {
                textView = this.textView;
                i2 = R.string.VoipGroupJoinAs;
            }
            textView.setText(LocaleController.getString(i2));
            textView2 = this.textView;
            createFrame = LayoutHelper.createFrame(-2, -2.0f, 51, 23.0f, 8.0f, 23.0f, 0.0f);
        }
        viewGroup.addView(textView2, createFrame);
        TextView textView7 = new TextView(getContext());
        this.messageTextView = textView7;
        textView7.setTextColor(Theme.getColor(i == 2 ? Theme.key_voipgroup_lastSeenText : Theme.key_dialogTextGray3));
        this.messageTextView.setTextSize(1, 14.0f);
        int size3 = this.chats.size();
        for (int i8 = 0; i8 < size3; i8++) {
            long peerId2 = MessageObject.getPeerId((TLRPC.Peer) this.chats.get(i8));
            if (peerId2 < 0) {
                TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId2));
                if (!ChatObject.isChannel(chat2) || chat2.megagroup) {
                    z = true;
                    break;
                }
            }
        }
        z = false;
        this.messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        if (i == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(LocaleController.getString((!ChatObject.isChannel(chat) || chat.megagroup) ? R.string.VoipGroupStart2 : R.string.VoipChannelStart2));
            if (this.chats.size() > 1) {
                sb.append("\n\n");
                sb.append(LocaleController.getString(R.string.VoipChatDisplayedAs));
            } else {
                this.listView.setVisibility(8);
            }
            this.messageTextView.setText(sb);
            this.messageTextView.setGravity(49);
            textView4 = this.messageTextView;
            createFrame2 = LayoutHelper.createLinear(-2, -2, 49, 23, 0, 23, 5);
        } else {
            if (z) {
                textView3 = this.messageTextView;
                i3 = R.string.VoipGroupStartAsInfoGroup;
            } else {
                textView3 = this.messageTextView;
                i3 = R.string.VoipGroupStartAsInfo;
            }
            textView3.setText(LocaleController.getString(i3));
            this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            textView4 = this.messageTextView;
            createFrame2 = LayoutHelper.createFrame(-2, -2.0f, 51, 23.0f, 0.0f, 23.0f, 5.0f);
        }
        viewGroup.addView(textView4, createFrame2);
        if (i == 0) {
            viewGroup.addView(this.listView, LayoutHelper.createLinear(this.chats.size() < 5 ? -2 : -1, 95, 49, 0, 6, 0, 0));
        }
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context, false);
        this.doneButton = bottomSheetCell;
        bottomSheetCell.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                JoinCallAlert.this.lambda$new$7(joinCallAlertDelegate, view);
            }
        });
        if (this.currentType == 0) {
            viewGroup.addView(this.doneButton, LayoutHelper.createLinear(-1, 50, 51, 0, 0, 0, 0));
            BottomSheetCell bottomSheetCell2 = new BottomSheetCell(context, true);
            bottomSheetCell2.setText(LocaleController.getString(ChatObject.isChannelOrGiga(chat) ? R.string.VoipChannelScheduleVoiceChat : R.string.VoipGroupScheduleVoiceChat), false);
            bottomSheetCell2.background.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    JoinCallAlert.this.lambda$new$8(view);
                }
            });
            viewGroup.addView(bottomSheetCell2, LayoutHelper.createLinear(-1, 50, 51, 0, 0, 0, 0));
        } else {
            viewGroup.addView(this.doneButton, LayoutHelper.createFrame(-1, 50.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        updateDoneButton(false, chat);
    }

    public static void checkFewUsers(Context context, final long j, final AccountInstance accountInstance, final MessagesStorage.BooleanCallback booleanCallback) {
        if (lastCachedAccount == accountInstance.getCurrentAccount() && lastCacheDid == j && cachedChats != null && SystemClock.elapsedRealtime() - lastCacheTime < 240000) {
            booleanCallback.run(cachedChats.size() == 1);
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(context, 3);
        TL_phone.getGroupCallJoinAs getgroupcalljoinas = new TL_phone.getGroupCallJoinAs();
        getgroupcalljoinas.peer = accountInstance.getMessagesController().getInputPeer(j);
        final int sendRequest = accountInstance.getConnectionsManager().sendRequest(getgroupcalljoinas, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinCallAlert.lambda$checkFewUsers$1(AlertDialog.this, j, accountInstance, booleanCallback, tLObject, tL_error);
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                JoinCallAlert.lambda$checkFewUsers$2(AccountInstance.this, sendRequest, dialogInterface);
            }
        });
        try {
            alertDialog.showDelayed(500L);
        } catch (Exception unused) {
        }
    }

    public static void lambda$checkFewUsers$0(AlertDialog alertDialog, TLObject tLObject, long j, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            TL_phone.joinAsPeers joinaspeers = (TL_phone.joinAsPeers) tLObject;
            cachedChats = joinaspeers.peers;
            lastCacheDid = j;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(joinaspeers.chats, false);
            accountInstance.getMessagesController().putUsers(joinaspeers.users, false);
            booleanCallback.run(joinaspeers.peers.size() == 1);
        }
    }

    public static void lambda$checkFewUsers$1(final AlertDialog alertDialog, final long j, final AccountInstance accountInstance, final MessagesStorage.BooleanCallback booleanCallback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                JoinCallAlert.lambda$checkFewUsers$0(AlertDialog.this, tLObject, j, accountInstance, booleanCallback);
            }
        });
    }

    public static void lambda$checkFewUsers$2(AccountInstance accountInstance, int i, DialogInterface dialogInterface) {
        accountInstance.getConnectionsManager().cancelRequest(i, true);
    }

    public void lambda$new$6(TLRPC.Chat chat, View view, int i) {
        if (this.animationInProgress || this.chats.get(i) == this.selectedPeer) {
            return;
        }
        this.selectedPeer = (TLRPC.Peer) this.chats.get(i);
        boolean z = view instanceof GroupCreateUserCell;
        if (z) {
            ((GroupCreateUserCell) view).setChecked(true, true);
        } else if (view instanceof ShareDialogCell) {
            ((ShareDialogCell) view).setChecked(true, true);
            view.invalidate();
        }
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt != view) {
                if (z) {
                    ((GroupCreateUserCell) childAt).setChecked(false, true);
                } else if (view instanceof ShareDialogCell) {
                    ((ShareDialogCell) childAt).setChecked(false, true);
                }
            }
        }
        if (this.currentType != 0) {
            updateDoneButton(true, chat);
        }
    }

    public void lambda$new$7(JoinCallAlertDelegate joinCallAlertDelegate, View view) {
        TLRPC.InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        if (this.currentType != 2) {
            this.selectAfterDismiss = inputPeer;
        } else if (this.selectedPeer != this.currentPeer) {
            joinCallAlertDelegate.didSelectChat(inputPeer, this.chats.size() > 1, false, false);
        }
        dismiss();
    }

    public void lambda$new$8(View view) {
        this.selectAfterDismiss = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(this.selectedPeer));
        this.schedule = true;
        dismiss();
    }

    public static void lambda$open$3(AlertDialog alertDialog, TLObject tLObject, AccountInstance accountInstance, JoinCallAlertDelegate joinCallAlertDelegate, long j, Context context, BaseFragment baseFragment, int i, TLRPC.Peer peer) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject != null) {
            TL_phone.joinAsPeers joinaspeers = (TL_phone.joinAsPeers) tLObject;
            if (joinaspeers.peers.size() == 1) {
                joinCallAlertDelegate.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(joinaspeers.peers.get(0))), false, false, false);
                return;
            }
            cachedChats = joinaspeers.peers;
            lastCacheDid = j;
            lastCacheTime = SystemClock.elapsedRealtime();
            lastCachedAccount = accountInstance.getCurrentAccount();
            accountInstance.getMessagesController().putChats(joinaspeers.chats, false);
            accountInstance.getMessagesController().putUsers(joinaspeers.users, false);
            showAlert(context, j, joinaspeers.peers, baseFragment, i, peer, joinCallAlertDelegate);
        }
    }

    public static void lambda$open$4(final AlertDialog alertDialog, final AccountInstance accountInstance, final JoinCallAlertDelegate joinCallAlertDelegate, final long j, final Context context, final BaseFragment baseFragment, final int i, final TLRPC.Peer peer, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                JoinCallAlert.lambda$open$3(AlertDialog.this, tLObject, accountInstance, joinCallAlertDelegate, j, context, baseFragment, i, peer);
            }
        });
    }

    public static void lambda$open$5(AccountInstance accountInstance, int i, DialogInterface dialogInterface) {
        accountInstance.getConnectionsManager().cancelRequest(i, true);
    }

    public static void open(final Context context, final long j, final AccountInstance accountInstance, final BaseFragment baseFragment, final int i, final TLRPC.Peer peer, final JoinCallAlertDelegate joinCallAlertDelegate) {
        if (context == null || joinCallAlertDelegate == null) {
            return;
        }
        if (lastCachedAccount == accountInstance.getCurrentAccount() && lastCacheDid == j && cachedChats != null && SystemClock.elapsedRealtime() - lastCacheTime < 300000) {
            if (cachedChats.size() != 1 || i == 0) {
                showAlert(context, j, cachedChats, baseFragment, i, peer, joinCallAlertDelegate);
                return;
            } else {
                joinCallAlertDelegate.didSelectChat(accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId((TLRPC.Peer) cachedChats.get(0))), false, false, false);
                return;
            }
        }
        final AlertDialog alertDialog = new AlertDialog(context, 3);
        TL_phone.getGroupCallJoinAs getgroupcalljoinas = new TL_phone.getGroupCallJoinAs();
        getgroupcalljoinas.peer = accountInstance.getMessagesController().getInputPeer(j);
        final int sendRequest = accountInstance.getConnectionsManager().sendRequest(getgroupcalljoinas, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinCallAlert.lambda$open$4(AlertDialog.this, accountInstance, joinCallAlertDelegate, j, context, baseFragment, i, peer, tLObject, tL_error);
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                JoinCallAlert.lambda$open$5(AccountInstance.this, sendRequest, dialogInterface);
            }
        });
        try {
            alertDialog.showDelayed(500L);
        } catch (Exception unused) {
        }
    }

    public static void processDeletedChat(int i, long j) {
        ArrayList arrayList;
        if (lastCachedAccount != i || (arrayList = cachedChats) == null || j > 0) {
            return;
        }
        int size = arrayList.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                break;
            }
            if (MessageObject.getPeerId((TLRPC.Peer) cachedChats.get(i2)) == j) {
                cachedChats.remove(i2);
                break;
            }
            i2++;
        }
        if (cachedChats.isEmpty()) {
            cachedChats = null;
        }
    }

    public static void resetCache() {
        cachedChats = null;
    }

    private static void showAlert(Context context, long j, ArrayList arrayList, BaseFragment baseFragment, int i, TLRPC.Peer peer, JoinCallAlertDelegate joinCallAlertDelegate) {
        if (i == 0) {
            CreateGroupCallBottomSheet.show(arrayList, baseFragment, j, joinCallAlertDelegate);
            return;
        }
        JoinCallAlert joinCallAlert = new JoinCallAlert(context, j, arrayList, i, peer, joinCallAlertDelegate);
        if (baseFragment == null) {
            joinCallAlert.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(joinCallAlert);
        }
    }

    private void updateDoneButton(boolean z, TLRPC.Chat chat) {
        BottomSheetCell bottomSheetCell;
        String formatString;
        BottomSheetCell bottomSheetCell2;
        String formatString2;
        if (this.currentType == 0) {
            if (ChatObject.isChannelOrGiga(chat)) {
                bottomSheetCell2 = this.doneButton;
                formatString2 = LocaleController.formatString("VoipChannelStartVoiceChat", R.string.VoipChannelStartVoiceChat, new Object[0]);
            } else {
                bottomSheetCell2 = this.doneButton;
                formatString2 = LocaleController.formatString("VoipGroupStartVoiceChat", R.string.VoipGroupStartVoiceChat, new Object[0]);
            }
            bottomSheetCell2.setText(formatString2, z);
            return;
        }
        long peerId = MessageObject.getPeerId(this.selectedPeer);
        if (DialogObject.isUserDialog(peerId)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
            bottomSheetCell = this.doneButton;
            formatString = LocaleController.formatString("VoipGroupContinueAs", R.string.VoipGroupContinueAs, UserObject.getFirstName(user));
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId));
            bottomSheetCell = this.doneButton;
            formatString = LocaleController.formatString("VoipGroupContinueAs", R.string.VoipGroupContinueAs, chat2 != null ? chat2.title : "");
        }
        bottomSheetCell.setText(formatString, z);
    }

    public void updateLayout() {
        if (this.currentType == 0) {
            return;
        }
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(9.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = top;
        }
        if (this.scrollOffsetY != i) {
            this.textView.setTranslationY(AndroidUtilities.dp(19.0f) + top);
            this.messageTextView.setTranslationY(top + AndroidUtilities.dp(56.0f));
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            this.containerView.invalidate();
        }
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    public void dismissInternal() {
        super.dismissInternal();
        TLRPC.InputPeer inputPeer = this.selectAfterDismiss;
        if (inputPeer != null) {
            this.delegate.didSelectChat(inputPeer, this.chats.size() > 1, this.schedule, false);
        }
    }
}
