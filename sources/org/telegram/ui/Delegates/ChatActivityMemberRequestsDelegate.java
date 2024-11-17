package org.telegram.ui.Delegates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.BlurredFrameLayout;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MemberRequestsBottomSheet;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ChatActivityMemberRequestsDelegate {
    private AvatarsImageView avatarsView;
    private MemberRequestsBottomSheet bottomSheet;
    private final Callback callback;
    private TLRPC.ChatFull chatInfo;
    private int closePendingRequestsCount = -1;
    private ImageView closeView;
    private final int currentAccount;
    private final TLRPC.Chat currentChat;
    private final BaseFragment fragment;
    private ValueAnimator pendingRequestsAnimator;
    private int pendingRequestsCount;
    private float pendingRequestsEnterOffset;
    private TextView requestsCountTextView;
    private FrameLayout root;
    private final SizeNotifierFrameLayout sizeNotifierFrameLayout;

    public interface Callback {
        void onEnterOffsetChanged();
    }

    public ChatActivityMemberRequestsDelegate(BaseFragment baseFragment, SizeNotifierFrameLayout sizeNotifierFrameLayout, TLRPC.Chat chat, Callback callback) {
        this.fragment = baseFragment;
        this.sizeNotifierFrameLayout = sizeNotifierFrameLayout;
        this.currentChat = chat;
        this.currentAccount = baseFragment.getCurrentAccount();
        this.callback = callback;
    }

    private void animatePendingRequests(final boolean z, boolean z2) {
        if (z == (this.root.getVisibility() == 0)) {
            return;
        }
        if (z) {
            if (this.closePendingRequestsCount == -1 && this.currentChat != null) {
                this.closePendingRequestsCount = this.fragment.getMessagesController().getChatPendingRequestsOnClosed(this.currentChat.id);
            }
            int i = this.pendingRequestsCount;
            int i2 = this.closePendingRequestsCount;
            if (i == i2) {
                return;
            }
            if (i2 != 0 && this.currentChat != null) {
                this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
            }
        }
        ValueAnimator valueAnimator = this.pendingRequestsAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f);
            this.pendingRequestsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatActivityMemberRequestsDelegate.this.lambda$animatePendingRequests$2(valueAnimator2);
                }
            });
            this.pendingRequestsAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (!z) {
                        ChatActivityMemberRequestsDelegate.this.root.setVisibility(8);
                    }
                    if (ChatActivityMemberRequestsDelegate.this.callback != null) {
                        ChatActivityMemberRequestsDelegate.this.callback.onEnterOffsetChanged();
                    }
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    if (z) {
                        ChatActivityMemberRequestsDelegate.this.root.setVisibility(0);
                    }
                }
            });
            this.pendingRequestsAnimator.setDuration(200L);
            this.pendingRequestsAnimator.start();
            return;
        }
        this.root.setVisibility(z ? 0 : 8);
        this.pendingRequestsEnterOffset = z ? 0.0f : -getViewHeight();
        Callback callback = this.callback;
        if (callback != null) {
            callback.onEnterOffsetChanged();
        }
    }

    public void lambda$animatePendingRequests$2(ValueAnimator valueAnimator) {
        this.pendingRequestsEnterOffset = (-getViewHeight()) * (1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue());
        Callback callback = this.callback;
        if (callback != null) {
            callback.onEnterOffsetChanged();
        }
    }

    public void lambda$getView$0(View view) {
        showBottomSheet();
    }

    public void lambda$getView$1(View view) {
        this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, this.pendingRequestsCount);
        this.closePendingRequestsCount = this.pendingRequestsCount;
        animatePendingRequests(false, true);
    }

    private void setPendingRequests(int i, List list, boolean z) {
        if (this.root == null) {
            return;
        }
        if (i <= 0) {
            if (this.currentChat != null) {
                this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
                this.closePendingRequestsCount = 0;
            }
            animatePendingRequests(false, z);
            this.pendingRequestsCount = 0;
            return;
        }
        if (this.pendingRequestsCount != i) {
            this.pendingRequestsCount = i;
            this.requestsCountTextView.setText(LocaleController.formatPluralString("JoinUsersRequests", i, new Object[0]));
            animatePendingRequests(true, z);
            if (list == null || list.isEmpty()) {
                return;
            }
            int min = Math.min(3, list.size());
            for (int i2 = 0; i2 < min; i2++) {
                TLRPC.User user = this.fragment.getMessagesController().getUser((Long) list.get(i2));
                if (user != null) {
                    this.avatarsView.setObject(i2, this.currentAccount, user);
                }
            }
            this.avatarsView.setCount(min);
            this.avatarsView.commitTransition(true);
        }
    }

    private void showBottomSheet() {
        if (this.bottomSheet == null) {
            this.bottomSheet = new MemberRequestsBottomSheet(this.fragment, this.currentChat.id) {
                @Override
                public void dismiss() {
                    if (ChatActivityMemberRequestsDelegate.this.bottomSheet != null && !ChatActivityMemberRequestsDelegate.this.bottomSheet.isNeedRestoreDialog()) {
                        ChatActivityMemberRequestsDelegate.this.bottomSheet = null;
                    }
                    super.dismiss();
                }
            };
        }
        this.fragment.showDialog(this.bottomSheet);
    }

    public void fillThemeDescriptions(List list) {
        list.add(new ThemeDescription(this.requestsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
        list.add(new ThemeDescription(this.closeView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chat_topPanelClose));
    }

    public View getView() {
        if (this.root == null) {
            BlurredFrameLayout blurredFrameLayout = new BlurredFrameLayout(this.fragment.getParentActivity(), this.sizeNotifierFrameLayout);
            this.root = blurredFrameLayout;
            blurredFrameLayout.setBackgroundColor(this.fragment.getThemedColor(Theme.key_chat_topPanelBackground));
            this.root.setVisibility(8);
            this.pendingRequestsEnterOffset = -getViewHeight();
            View view = new View(this.fragment.getParentActivity());
            view.setBackground(Theme.getSelectorDrawable(false));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ChatActivityMemberRequestsDelegate.this.lambda$getView$0(view2);
                }
            });
            this.root.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 2.0f));
            LinearLayout linearLayout = new LinearLayout(this.fragment.getParentActivity());
            linearLayout.setOrientation(0);
            this.root.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 36.0f, 0.0f));
            AvatarsImageView avatarsImageView = new AvatarsImageView(this.fragment.getParentActivity(), false) {
                @Override
                public void onMeasure(int i, int i2) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.avatarsDrawable.count == 0 ? 0 : ((r2 - 1) * 20) + 24), 1073741824), i2);
                }
            };
            this.avatarsView = avatarsImageView;
            avatarsImageView.setAvatarsTextSize(AndroidUtilities.dp(18.0f));
            this.avatarsView.reset();
            linearLayout.addView(this.avatarsView, LayoutHelper.createFrame(-2, -1.0f, 48, 8.0f, 0.0f, 10.0f, 0.0f));
            TextView textView = new TextView(this.fragment.getParentActivity());
            this.requestsCountTextView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.END);
            this.requestsCountTextView.setGravity(16);
            this.requestsCountTextView.setSingleLine();
            this.requestsCountTextView.setText((CharSequence) null);
            this.requestsCountTextView.setTextColor(this.fragment.getThemedColor(Theme.key_chat_topPanelTitle));
            this.requestsCountTextView.setTypeface(AndroidUtilities.bold());
            linearLayout.addView(this.requestsCountTextView, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(this.fragment.getParentActivity());
            this.closeView = imageView;
            if (Build.VERSION.SDK_INT >= 21) {
                imageView.setBackground(Theme.createSelectorDrawable(this.fragment.getThemedColor(Theme.key_inappPlayerClose) & 436207615, 1, AndroidUtilities.dp(14.0f)));
            }
            this.closeView.setColorFilter(new PorterDuffColorFilter(this.fragment.getThemedColor(Theme.key_chat_topPanelClose), PorterDuff.Mode.MULTIPLY));
            this.closeView.setContentDescription(LocaleController.getString(R.string.Close));
            this.closeView.setImageResource(R.drawable.miniplayer_close);
            this.closeView.setScaleType(ImageView.ScaleType.CENTER);
            this.closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ChatActivityMemberRequestsDelegate.this.lambda$getView$1(view2);
                }
            });
            this.root.addView(this.closeView, LayoutHelper.createFrame(36, -1.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null) {
                setPendingRequests(chatFull.requests_pending, chatFull.recent_requesters, false);
            }
        }
        return this.root;
    }

    public float getViewEnterOffset() {
        return this.pendingRequestsEnterOffset;
    }

    public int getViewHeight() {
        return AndroidUtilities.dp(40.0f);
    }

    public void onBackToScreen() {
        MemberRequestsBottomSheet memberRequestsBottomSheet = this.bottomSheet;
        if (memberRequestsBottomSheet == null || !memberRequestsBottomSheet.isNeedRestoreDialog()) {
            return;
        }
        showBottomSheet();
    }

    public void setChatInfo(TLRPC.ChatFull chatFull, boolean z) {
        this.chatInfo = chatFull;
        if (chatFull != null) {
            setPendingRequests(chatFull.requests_pending, chatFull.recent_requesters, z);
        }
    }
}
