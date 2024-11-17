package org.telegram.ui.Stories;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GradientTools;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.Shaker$$ExternalSyntheticLambda0;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesUtilities;

public abstract class StoriesUtilities {
    public static GradientTools closeFriendsGradientTools;
    public static GradientTools errorGradientTools;
    public static Drawable expiredStoryDrawable;
    public static int grayLastColor;
    public static Paint grayPaint;
    public static int storyCellGrayLastColor;
    public static GradientTools[] storiesGradientTools = new GradientTools[2];
    public static Paint[] storyCellGreyPaint = new Paint[2];
    private static final RectF rectTmp = new RectF();
    static boolean scheduled = false;
    static int debugState = 0;
    static Runnable debugRunnable = new Runnable() {
        @Override
        public void run() {
            int abs = Math.abs(Utilities.random.nextInt() % 3);
            StoriesUtilities.debugState = abs;
            if (abs == 2) {
                StoriesUtilities.debugState = 1;
            } else {
                StoriesUtilities.debugState = 2;
            }
            NotificationCenter.getInstance(UserConfig.selectedAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
            AndroidUtilities.runOnUIThread(StoriesUtilities.debugRunnable, 1000L);
            LaunchActivity.getLastFragment().getFragmentView();
        }
    };
    private static final RectF forumRect = new RectF();
    private static final Path forumRoundRectPath = new Path();
    private static final Matrix forumRoundRectMatrix = new Matrix();
    private static final PathMeasure forumRoundRectPathMeasure = new PathMeasure();
    private static final Path forumSegmentPath = new Path();

    public static class AvatarStoryParams {
        public boolean allowLongress;
        public float alpha;
        public boolean animate;
        public int animateFromUnreadState;
        ButtonBounce buttonBounce;
        public View child;
        public long crossfadeToDialog;
        public float crossfadeToDialogProgress;
        public int currentState;
        private long dialogId;
        public boolean drawHiddenStoriesAsSegments;
        public boolean drawInside;
        public boolean drawSegments;
        public boolean forceAnimateProgressToSegments;
        public int forceState;
        float globalAngle;
        public int globalState;
        boolean inc;
        public boolean isArchive;
        public boolean isDialogStoriesCell;
        public boolean isFirst;
        public boolean isLast;
        private final boolean isStoryCell;
        Runnable longPressRunnable;
        UserStoriesLoadOperation operation;
        public RectF originalAvatarRect;
        boolean pressed;
        public int prevState;
        public int prevUnreadState;
        public float progressToArc;
        public float progressToProgressSegments;
        public float progressToSate;
        public float progressToSegments;
        public Theme.ResourcesProvider resourcesProvider;
        public boolean showProgress;
        float startX;
        float startY;
        public int storyId;
        public TL_stories.StoryItem storyItem;
        float sweepAngle;
        public int unreadState;

        public AvatarStoryParams(boolean z) {
            this(z, null);
        }

        public AvatarStoryParams(boolean z, Theme.ResourcesProvider resourcesProvider) {
            this.drawSegments = true;
            this.animate = true;
            this.progressToSegments = 1.0f;
            this.progressToArc = 0.0f;
            this.alpha = 1.0f;
            this.progressToSate = 1.0f;
            this.showProgress = false;
            this.originalAvatarRect = new RectF();
            this.allowLongress = false;
            this.isStoryCell = z;
            this.resourcesProvider = resourcesProvider;
        }

        public void lambda$checkOnTouchEvent$0(View view) {
            view.performHapticFeedback(0);
            ButtonBounce buttonBounce = this.buttonBounce;
            if (buttonBounce != null) {
                buttonBounce.setPressed(false);
            }
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).requestDisallowInterceptTouchEvent(false);
            }
            this.pressed = false;
            onLongPress();
        }

        private void processOpenStory(View view) {
            MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
            StoriesController storiesController = messagesController.getStoriesController();
            if (this.drawHiddenStoriesAsSegments) {
                openStory(0L, null);
                return;
            }
            if (this.dialogId != UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
                if (storiesController.hasStories(this.dialogId)) {
                    openStory(this.dialogId, null);
                    return;
                }
                long j = this.dialogId;
                if (j > 0) {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(j));
                    if (user == null || user.stories_unavailable || user.stories_max_id <= 0) {
                        return;
                    }
                    new UserStoriesLoadOperation().load(this.dialogId, view, this);
                    return;
                }
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
                if (chat == null || chat.stories_unavailable || chat.stories_max_id <= 0) {
                    return;
                }
                new UserStoriesLoadOperation().load(this.dialogId, view, this);
            }
        }

        public void updateProgressParams() {
            boolean z;
            if (this.inc) {
                float f = this.sweepAngle + 0.016f;
                this.sweepAngle = f;
                if (f >= 1.0f) {
                    this.sweepAngle = 1.0f;
                    z = false;
                    this.inc = z;
                }
            } else {
                float f2 = this.sweepAngle - 0.016f;
                this.sweepAngle = f2;
                if (f2 < 0.0f) {
                    this.sweepAngle = 0.0f;
                    z = true;
                    this.inc = z;
                }
            }
            this.globalAngle += 1.152f;
        }

        public boolean checkOnTouchEvent(MotionEvent motionEvent, final View view) {
            TLRPC.User user;
            this.child = view;
            StoriesController storiesController = MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController();
            boolean z = false;
            if (motionEvent.getAction() == 0 && this.originalAvatarRect.contains(motionEvent.getX(), motionEvent.getY())) {
                long j = this.dialogId;
                TLRPC.Chat chat = null;
                MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                if (j > 0) {
                    user = messagesController.getUser(Long.valueOf(this.dialogId));
                } else {
                    TLRPC.Chat chat2 = messagesController.getChat(Long.valueOf(-this.dialogId));
                    user = null;
                    chat = chat2;
                }
                if (this.drawHiddenStoriesAsSegments) {
                    z = storiesController.hasHiddenStories();
                } else if (this.dialogId <= 0 ? MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(this.dialogId) || (chat != null && !chat.stories_unavailable && chat.stories_max_id > 0) : MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(this.dialogId) || (user != null && !user.stories_unavailable && user.stories_max_id > 0)) {
                    z = true;
                }
                if (this.dialogId != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId && z) {
                    ButtonBounce buttonBounce = this.buttonBounce;
                    if (buttonBounce == null) {
                        this.buttonBounce = new ButtonBounce(view, 1.5f, 5.0f);
                    } else {
                        buttonBounce.setView(view);
                    }
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    this.buttonBounce.setPressed(true);
                    this.pressed = true;
                    this.startX = motionEvent.getX();
                    this.startY = motionEvent.getY();
                    if (this.allowLongress) {
                        Runnable runnable = this.longPressRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                        }
                        Runnable runnable2 = new Runnable() {
                            @Override
                            public final void run() {
                                StoriesUtilities.AvatarStoryParams.this.lambda$checkOnTouchEvent$0(view);
                            }
                        };
                        this.longPressRunnable = runnable2;
                        AndroidUtilities.runOnUIThread(runnable2, ViewConfiguration.getLongPressTimeout());
                    }
                }
            } else if (motionEvent.getAction() == 2 && this.pressed) {
                if (Math.abs(this.startX - motionEvent.getX()) > AndroidUtilities.touchSlop || Math.abs(this.startY - motionEvent.getY()) > AndroidUtilities.touchSlop) {
                    ButtonBounce buttonBounce2 = this.buttonBounce;
                    if (buttonBounce2 != null) {
                        buttonBounce2.setView(view);
                        this.buttonBounce.setPressed(false);
                    }
                    Runnable runnable3 = this.longPressRunnable;
                    if (runnable3 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable3);
                    }
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    this.pressed = false;
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                ButtonBounce buttonBounce3 = this.buttonBounce;
                if (buttonBounce3 != null) {
                    buttonBounce3.setView(view);
                    this.buttonBounce.setPressed(false);
                }
                if (this.pressed && motionEvent.getAction() == 1) {
                    processOpenStory(view);
                }
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).requestDisallowInterceptTouchEvent(false);
                }
                this.pressed = false;
                Runnable runnable4 = this.longPressRunnable;
                if (runnable4 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable4);
                }
            }
            return this.pressed;
        }

        public float getScale() {
            ButtonBounce buttonBounce = this.buttonBounce;
            if (buttonBounce == null) {
                return 1.0f;
            }
            return buttonBounce.getScale(0.08f);
        }

        public void onDetachFromWindow() {
            reset();
        }

        public void onLongPress() {
        }

        public void openStory(long j, Runnable runnable) {
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment == null || this.child == null) {
                return;
            }
            lastFragment.getOrCreateStoryViewer().doOnAnimationReady(runnable);
            ViewParent parent = this.child.getParent();
            lastFragment.getOrCreateStoryViewer().open(lastFragment.getContext(), j, parent instanceof RecyclerView ? StoriesListPlaceProvider.of((RecyclerListView) parent) : null);
        }

        public void reset() {
            UserStoriesLoadOperation userStoriesLoadOperation = this.operation;
            if (userStoriesLoadOperation != null) {
                userStoriesLoadOperation.cancel();
                this.operation = null;
            }
            this.buttonBounce = null;
            this.pressed = false;
        }
    }

    public static class EnsureStoryFileLoadedObject {
        private boolean cancelled;
        long dialogId;
        ImageReceiver imageReceiver;
        public Runnable runnable;
        StoriesController storiesController;

        private EnsureStoryFileLoadedObject(StoriesController storiesController, long j) {
            this.cancelled = false;
            this.dialogId = j;
            this.storiesController = storiesController;
        }

        public void cancel() {
            this.cancelled = true;
            this.storiesController.setLoading(this.dialogId, false);
        }
    }

    public static class StoryGradientTools {
        private final AnimatedColor animatedColor1;
        private final AnimatedColor animatedColor2;
        private int color1;
        private int color2;
        public final int currentAccount;
        private final Runnable invalidate;
        private final boolean isDialogCell;
        private final GradientTools tools;

        public StoryGradientTools(View view, boolean z) {
            this(new Shaker$$ExternalSyntheticLambda0(view), z);
            Objects.requireNonNull(view);
        }

        public StoryGradientTools(Runnable runnable, boolean z) {
            this.currentAccount = UserConfig.selectedAccount;
            this.invalidate = runnable;
            this.isDialogCell = z;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.animatedColor1 = new AnimatedColor(runnable, 350L, cubicBezierInterpolator);
            this.animatedColor2 = new AnimatedColor(runnable, 350L, cubicBezierInterpolator);
            GradientTools gradientTools = new GradientTools();
            this.tools = gradientTools;
            gradientTools.isDiagonal = true;
            gradientTools.isRotate = true;
            resetColors(false);
            gradientTools.paint.setStrokeWidth(AndroidUtilities.dpf2(2.3f));
            gradientTools.paint.setStyle(Paint.Style.STROKE);
            gradientTools.paint.setStrokeCap(Paint.Cap.ROUND);
        }

        private void resetColors(boolean z) {
            int color;
            int i;
            if (this.isDialogCell) {
                color = Theme.getColor(Theme.key_stories_circle_dialog1);
                i = Theme.key_stories_circle_dialog2;
            } else {
                color = Theme.getColor(Theme.key_stories_circle1);
                i = Theme.key_stories_circle2;
            }
            setColors(color, Theme.getColor(i), z);
        }

        private void setColors(int i, int i2, boolean z) {
            this.color1 = i;
            this.color2 = i2;
            if (!z) {
                this.animatedColor1.set(i, true);
                this.animatedColor2.set(i2, true);
            }
            Runnable runnable = this.invalidate;
            if (runnable != null) {
                runnable.run();
            }
        }

        public Paint getPaint(RectF rectF) {
            this.tools.setColors(this.animatedColor1.set(this.color1), this.animatedColor2.set(this.color2));
            this.tools.setBounds(rectF.left, rectF.top, rectF.right, rectF.bottom);
            return this.tools.paint;
        }

        public void setChat(TLRPC.Chat chat, boolean z) {
            setColorId(-1, z);
        }

        public void setColorId(int i, boolean z) {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(this.currentAccount).profilePeerColors;
            MessagesController.PeerColor color = peerColors == null ? null : peerColors.getColor(i);
            if (color != null) {
                setColors(color.getStoryColor1(Theme.isCurrentThemeDark()), color.getStoryColor2(Theme.isCurrentThemeDark()), z);
            } else {
                resetColors(z);
            }
        }

        public void setUser(TLRPC.User user, boolean z) {
            TLRPC.TL_peerColor tL_peerColor;
            setColorId((user == null || (tL_peerColor = user.profile_color) == null) ? -1 : tL_peerColor.color, z);
        }
    }

    public static class UserStoriesLoadOperation {
        boolean canceled;
        private int currentAccount;
        long dialogId;
        int guid = ConnectionsManager.generateClassGuid();
        AvatarStoryParams params;
        int reqId;
        View view;

        public void lambda$load$0(View view, long j) {
            view.invalidate();
            MessagesController.getInstance(this.currentAccount).getStoriesController().setLoading(j, false);
        }

        public void lambda$load$1(final View view, final long j, AvatarStoryParams avatarStoryParams) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StoriesUtilities.UserStoriesLoadOperation.this.lambda$load$0(view, j);
                }
            }, 500L);
            avatarStoryParams.openStory(j, null);
        }

        public void lambda$load$2(org.telegram.tgnet.TLObject r10, final long r11, final android.view.View r13, final org.telegram.ui.Stories.StoriesUtilities.AvatarStoryParams r14, org.telegram.messenger.MessagesController r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.StoriesUtilities.UserStoriesLoadOperation.lambda$load$2(org.telegram.tgnet.TLObject, long, android.view.View, org.telegram.ui.Stories.StoriesUtilities$AvatarStoryParams, org.telegram.messenger.MessagesController):void");
        }

        public void lambda$load$3(final long j, final View view, final AvatarStoryParams avatarStoryParams, final MessagesController messagesController, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StoriesUtilities.UserStoriesLoadOperation.this.lambda$load$2(tLObject, j, view, avatarStoryParams, messagesController);
                }
            });
        }

        void cancel() {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, false);
            this.canceled = true;
            this.params = null;
        }

        void load(final long j, final View view, final AvatarStoryParams avatarStoryParams) {
            int i = UserConfig.selectedAccount;
            this.currentAccount = i;
            this.dialogId = j;
            this.params = avatarStoryParams;
            this.view = view;
            final MessagesController messagesController = MessagesController.getInstance(i);
            messagesController.getStoriesController().setLoading(j, true);
            view.invalidate();
            TL_stories.TL_stories_getPeerStories tL_stories_getPeerStories = new TL_stories.TL_stories_getPeerStories();
            tL_stories_getPeerStories.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getPeerStories, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StoriesUtilities.UserStoriesLoadOperation.this.lambda$load$3(j, view, avatarStoryParams, messagesController, tLObject, tL_error);
                }
            });
        }
    }

    public static void applyUploadingStr(SimpleTextView simpleTextView, boolean z, boolean z2) {
        String string = LocaleController.getString(z2 ? R.string.StoryEditing : R.string.UploadingStory);
        int indexOf = string.indexOf("…");
        String str = string;
        if (indexOf > 0) {
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(string);
            UploadingDotsSpannable uploadingDotsSpannable = new UploadingDotsSpannable();
            valueOf.setSpan(uploadingDotsSpannable, valueOf.length() - 1, valueOf.length(), 0);
            uploadingDotsSpannable.setParent(simpleTextView, z);
            str = valueOf;
        }
        simpleTextView.setText(str);
    }

    public static void applyViewedUser(TL_stories.StoryItem storyItem, TLRPC.User user) {
        if (user == null || storyItem.dialogId != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId || hasExpiredViews(storyItem)) {
            return;
        }
        if (storyItem.views == null) {
            storyItem.views = new TL_stories.TL_storyViews();
        }
        TL_stories.StoryViews storyViews = storyItem.views;
        if (storyViews.views_count == 0) {
            storyViews.views_count = 1;
            storyViews.recent_viewers.add(Long.valueOf(user.id));
        }
    }

    private static void checkGrayPaint(Theme.ResourcesProvider resourcesProvider) {
        Paint paint;
        int blendARGB;
        if (grayPaint == null) {
            Paint paint2 = new Paint(1);
            grayPaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
            grayPaint.setStrokeWidth(AndroidUtilities.dpf2(1.3f));
            grayPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider);
        if (grayLastColor != color) {
            grayLastColor = color;
            float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
            if (computePerceivedBrightness >= 0.721f) {
                paint = grayPaint;
                blendARGB = ColorUtils.blendARGB(color, -16777216, 0.2f);
            } else if (computePerceivedBrightness < 0.25f) {
                paint = grayPaint;
                blendARGB = ColorUtils.blendARGB(color, -1, 0.2f);
            } else {
                paint = grayPaint;
                blendARGB = ColorUtils.blendARGB(color, -1, 0.44f);
            }
            paint.setColor(blendARGB);
        }
    }

    private static void checkStoriesGradientTools(boolean z) {
        int color;
        int i;
        GradientTools[] gradientToolsArr = storiesGradientTools;
        if (gradientToolsArr[z ? 1 : 0] == null) {
            gradientToolsArr[z ? 1 : 0] = new GradientTools();
            GradientTools gradientTools = storiesGradientTools[z ? 1 : 0];
            gradientTools.isDiagonal = true;
            gradientTools.isRotate = true;
            if (z) {
                color = Theme.getColor(Theme.key_stories_circle_dialog1);
                i = Theme.key_stories_circle_dialog2;
            } else {
                color = Theme.getColor(Theme.key_stories_circle1);
                i = Theme.key_stories_circle2;
            }
            gradientTools.setColors(color, Theme.getColor(i));
            storiesGradientTools[z ? 1 : 0].paint.setStrokeWidth(AndroidUtilities.dpf2(2.3f));
            storiesGradientTools[z ? 1 : 0].paint.setStyle(Paint.Style.STROKE);
            storiesGradientTools[z ? 1 : 0].paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    private static void checkStoryCellGrayPaint(boolean z, Theme.ResourcesProvider resourcesProvider) {
        Paint paint;
        int blendARGB;
        Paint[] paintArr = storyCellGreyPaint;
        if (paintArr[z ? 1 : 0] == null) {
            paintArr[z ? 1 : 0] = new Paint(1);
            storyCellGreyPaint[z ? 1 : 0].setStyle(Paint.Style.STROKE);
            storyCellGreyPaint[z ? 1 : 0].setStrokeWidth(AndroidUtilities.dpf2(1.3f));
            storyCellGreyPaint[z ? 1 : 0].setStrokeCap(Paint.Cap.ROUND);
        }
        int color = Theme.getColor(!z ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived, resourcesProvider);
        if (storyCellGrayLastColor != color) {
            storyCellGrayLastColor = color;
            float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(color);
            if (computePerceivedBrightness >= 0.721f) {
                paint = storyCellGreyPaint[z ? 1 : 0];
                blendARGB = ColorUtils.blendARGB(color, -16777216, 0.2f);
            } else if (computePerceivedBrightness < 0.25f) {
                paint = storyCellGreyPaint[z ? 1 : 0];
                blendARGB = ColorUtils.blendARGB(color, -1, 0.2f);
            } else {
                paint = storyCellGreyPaint[z ? 1 : 0];
                blendARGB = ColorUtils.blendARGB(color, -1, 0.44f);
            }
            paint.setColor(blendARGB);
        }
    }

    public static CharSequence createExpiredStoryString() {
        return createExpiredStoryString(false, "ExpiredStory", R.string.ExpiredStory, new Object[0]);
    }

    public static CharSequence createExpiredStoryString(boolean z, String str, int i, Object... objArr) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "d ").append((CharSequence) LocaleController.formatString(str, i, objArr));
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_mini_bomb);
        if (z) {
            coloredImageSpan.setScale(0.8f, 0.8f);
        } else {
            coloredImageSpan.setTopOffset(-1);
        }
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        return spannableStringBuilder;
    }

    public static CharSequence createReplyStoryString() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) "d ").append((CharSequence) LocaleController.getString(R.string.Story));
        spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.msg_mini_replystory2), 0, 1, 0);
        return spannableStringBuilder;
    }

    public static void drawArcExcludeArc(Canvas canvas, RectF rectF, Paint paint, float f, float f2, float f3, float f4) {
        boolean z;
        float f5 = f2 - f;
        if (f >= f3 || f2 >= f3 + f5) {
            z = false;
        } else {
            canvas.drawArc(rectF, f, Math.min(f2, f3) - f, false, paint);
            z = true;
        }
        float max = Math.max(f, f4);
        float min = Math.min(f2, 360.0f + f3);
        if (min >= max) {
            canvas.drawArc(rectF, max, min - max, false, paint);
        } else {
            if (z) {
                return;
            }
            if (f <= f3 || f2 >= f4) {
                canvas.drawArc(rectF, f, f5, false, paint);
            }
        }
    }

    public static void drawAvatarWithStory(long j, Canvas canvas, ImageReceiver imageReceiver, AvatarStoryParams avatarStoryParams) {
        drawAvatarWithStory(j, canvas, imageReceiver, UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId() != j && MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController().hasStories(j), avatarStoryParams);
    }

    public static void drawAvatarWithStory(long j, Canvas canvas, ImageReceiver imageReceiver, boolean z, AvatarStoryParams avatarStoryParams) {
        int predictiveUnreadState;
        int i;
        int i2;
        int i3;
        float f;
        float f2;
        Paint paint;
        Paint paint2;
        Paint paint3;
        float dpf2;
        float f3;
        float dp;
        Paint paint4;
        Paint paint5;
        Paint paint6;
        float dpf22;
        float f4;
        float dp2;
        GradientTools gradientTools;
        float dp3;
        StoriesController storiesController = MessagesController.getInstance(UserConfig.selectedAccount).getStoriesController();
        boolean z2 = avatarStoryParams.animate;
        boolean z3 = false;
        if (avatarStoryParams.dialogId != j) {
            avatarStoryParams.dialogId = j;
            avatarStoryParams.reset();
            z2 = false;
        }
        boolean isLoading = storiesController.isLoading(j);
        boolean z4 = ChatObject.isForum(UserConfig.selectedAccount, j) && !avatarStoryParams.isDialogStoriesCell;
        boolean hasHiddenStories = avatarStoryParams.drawHiddenStoriesAsSegments ? storiesController.hasHiddenStories() : z;
        if (avatarStoryParams.storyItem != null) {
            storiesController.getUnreadState(j, avatarStoryParams.storyId);
            isLoading = false;
        }
        if (isLoading) {
            if (storiesController.hasStories(j)) {
                predictiveUnreadState = 3;
                i = 2;
            } else {
                i = getPredictiveUnreadState(storiesController, j);
                predictiveUnreadState = 3;
            }
            z2 = false;
        } else if (!hasHiddenStories) {
            predictiveUnreadState = getPredictiveUnreadState(storiesController, j);
            i = predictiveUnreadState;
        } else if (avatarStoryParams.drawSegments) {
            predictiveUnreadState = 2;
            i = 2;
        } else {
            int unreadState = storiesController.getUnreadState(j, avatarStoryParams.storyId);
            i = unreadState;
            predictiveUnreadState = unreadState == 0 ? 2 : 1;
        }
        int i4 = avatarStoryParams.forceState;
        if (i4 != 0) {
            predictiveUnreadState = i4;
            i = predictiveUnreadState;
        }
        int i5 = avatarStoryParams.currentState;
        if (i5 != predictiveUnreadState) {
            if (i5 == 3) {
                z2 = true;
            }
            if (predictiveUnreadState == 3) {
                avatarStoryParams.animateFromUnreadState = i;
                avatarStoryParams.progressToProgressSegments = 0.0f;
            }
            if (z2) {
                avatarStoryParams.prevState = i5;
                avatarStoryParams.prevUnreadState = avatarStoryParams.unreadState;
                avatarStoryParams.currentState = predictiveUnreadState;
                avatarStoryParams.progressToSate = 0.0f;
            } else {
                avatarStoryParams.currentState = predictiveUnreadState;
                avatarStoryParams.progressToSate = 1.0f;
            }
        }
        avatarStoryParams.unreadState = i;
        ButtonBounce buttonBounce = avatarStoryParams.buttonBounce;
        float scale = buttonBounce != null ? buttonBounce.getScale(0.08f) : 1.0f;
        if (avatarStoryParams.showProgress != isLoading && isLoading) {
            avatarStoryParams.sweepAngle = 1.0f;
            avatarStoryParams.inc = false;
        }
        avatarStoryParams.showProgress = isLoading;
        if (avatarStoryParams.currentState == 0 && avatarStoryParams.progressToSate == 1.0f) {
            imageReceiver.setImageCoords(avatarStoryParams.originalAvatarRect);
            imageReceiver.draw(canvas);
            return;
        }
        if (scale != 1.0f) {
            int save = canvas.save();
            canvas.scale(scale, scale, avatarStoryParams.originalAvatarRect.centerX(), avatarStoryParams.originalAvatarRect.centerY());
            i2 = save;
        } else {
            i2 = 0;
        }
        float f5 = avatarStoryParams.progressToSate;
        if (f5 != 1.0f) {
            f5 = CubicBezierInterpolator.DEFAULT.getInterpolation(f5);
        }
        float f6 = f5;
        float lerp = (!avatarStoryParams.isStoryCell || avatarStoryParams.drawInside) ? AndroidUtilities.lerp(getInset(avatarStoryParams.prevState, avatarStoryParams.animateFromUnreadState), getInset(avatarStoryParams.currentState, avatarStoryParams.animateFromUnreadState), avatarStoryParams.progressToSate) : 0.0f;
        if (lerp == 0.0f) {
            imageReceiver.setImageCoords(avatarStoryParams.originalAvatarRect);
        } else {
            RectF rectF = rectTmp;
            rectF.set(avatarStoryParams.originalAvatarRect);
            rectF.inset(lerp, lerp);
            imageReceiver.setImageCoords(rectF);
        }
        if ((avatarStoryParams.prevState == 1 && avatarStoryParams.progressToSate != 1.0f) || avatarStoryParams.currentState == 1) {
            if (i == 2) {
                getCloseFriendsPaint(imageReceiver);
                gradientTools = closeFriendsGradientTools;
            } else {
                getUnreadCirclePaint(imageReceiver, avatarStoryParams.isStoryCell);
                gradientTools = storiesGradientTools[avatarStoryParams.isStoryCell ? 1 : 0];
            }
            boolean z5 = avatarStoryParams.prevState == 1 && avatarStoryParams.progressToSate != 1.0f;
            float f7 = (!avatarStoryParams.isStoryCell || avatarStoryParams.drawInside) ? 0.0f : -AndroidUtilities.dp(4.0f);
            if (z5) {
                dp3 = f7 + (AndroidUtilities.dp(5.0f) * f6);
                gradientTools.paint.setAlpha((int) (avatarStoryParams.alpha * 255.0f * (1.0f - f6)));
            } else {
                gradientTools.paint.setAlpha((int) (avatarStoryParams.alpha * 255.0f * f6));
                dp3 = f7 + (AndroidUtilities.dp(5.0f) * (1.0f - f6));
            }
            RectF rectF2 = rectTmp;
            rectF2.set(avatarStoryParams.originalAvatarRect);
            rectF2.inset(dp3, dp3);
            drawCircleInternal(canvas, imageReceiver.getParentView(), avatarStoryParams, gradientTools.paint, z4);
        }
        int i6 = avatarStoryParams.prevState;
        if ((i6 != 2 || avatarStoryParams.progressToSate == 1.0f) && avatarStoryParams.currentState != 2) {
            i3 = i2;
            f = 1.0f;
            f2 = 0.08f;
        } else {
            if (i6 == 2 && avatarStoryParams.progressToSate != 1.0f) {
                z3 = true;
            }
            if (avatarStoryParams.isStoryCell) {
                checkStoryCellGrayPaint(avatarStoryParams.isArchive, avatarStoryParams.resourcesProvider);
                paint4 = storyCellGreyPaint[avatarStoryParams.isArchive ? 1 : 0];
            } else {
                checkGrayPaint(avatarStoryParams.resourcesProvider);
                paint4 = grayPaint;
            }
            Paint paint7 = paint4;
            if (avatarStoryParams.drawSegments) {
                Paint unreadCirclePaint = getUnreadCirclePaint(imageReceiver, avatarStoryParams.isStoryCell);
                unreadCirclePaint.setAlpha((int) (avatarStoryParams.alpha * 255.0f));
                Paint closeFriendsPaint = getCloseFriendsPaint(imageReceiver);
                closeFriendsPaint.setAlpha((int) (avatarStoryParams.alpha * 255.0f));
                checkGrayPaint(avatarStoryParams.resourcesProvider);
                paint5 = unreadCirclePaint;
                paint6 = closeFriendsPaint;
            } else {
                paint5 = null;
                paint6 = null;
            }
            if (avatarStoryParams.drawSegments) {
                if (avatarStoryParams.isStoryCell && !avatarStoryParams.drawInside) {
                    dpf22 = AndroidUtilities.dpf2(3.5f);
                    f4 = -dpf22;
                }
                f4 = 0.0f;
            } else {
                if (avatarStoryParams.isStoryCell && !avatarStoryParams.drawInside) {
                    dpf22 = AndroidUtilities.dpf2(2.7f);
                    f4 = -dpf22;
                }
                f4 = 0.0f;
            }
            if (z3) {
                dp2 = f4 + (AndroidUtilities.dp(5.0f) * f6);
                paint7.setAlpha((int) (avatarStoryParams.alpha * 255.0f * (1.0f - f6)));
            } else {
                paint7.setAlpha((int) (avatarStoryParams.alpha * 255.0f * f6));
                dp2 = f4 + (AndroidUtilities.dp(5.0f) * (1.0f - f6));
            }
            RectF rectF3 = rectTmp;
            rectF3.set(avatarStoryParams.originalAvatarRect);
            rectF3.inset(dp2, dp2);
            if (avatarStoryParams.drawSegments) {
                i3 = i2;
                f2 = 0.08f;
                f = 1.0f;
                drawSegmentsInternal(canvas, storiesController, imageReceiver, avatarStoryParams, paint7, paint5, paint6, z4);
            } else {
                i3 = i2;
                f = 1.0f;
                f2 = 0.08f;
                drawCircleInternal(canvas, imageReceiver.getParentView(), avatarStoryParams, paint7, z4);
            }
        }
        if ((avatarStoryParams.prevState == 3 && avatarStoryParams.progressToSate != f) || avatarStoryParams.currentState == 3) {
            if (avatarStoryParams.animateFromUnreadState == 1) {
                getUnreadCirclePaint(imageReceiver, avatarStoryParams.isStoryCell);
                paint = storiesGradientTools[avatarStoryParams.isStoryCell ? 1 : 0].paint;
            } else if (avatarStoryParams.isStoryCell) {
                checkStoryCellGrayPaint(avatarStoryParams.isArchive, avatarStoryParams.resourcesProvider);
                paint = storyCellGreyPaint[avatarStoryParams.isArchive ? 1 : 0];
            } else {
                checkGrayPaint(avatarStoryParams.resourcesProvider);
                paint = grayPaint;
            }
            Paint paint8 = paint;
            paint8.setAlpha((int) (f6 * 255.0f));
            if (avatarStoryParams.drawSegments) {
                Paint unreadCirclePaint2 = getUnreadCirclePaint(imageReceiver, avatarStoryParams.isStoryCell);
                unreadCirclePaint2.setAlpha((int) (avatarStoryParams.alpha * 255.0f));
                Paint closeFriendsPaint2 = getCloseFriendsPaint(imageReceiver);
                closeFriendsPaint2.setAlpha((int) (avatarStoryParams.alpha * 255.0f));
                checkGrayPaint(avatarStoryParams.resourcesProvider);
                paint2 = unreadCirclePaint2;
                paint3 = closeFriendsPaint2;
            } else {
                paint2 = null;
                paint3 = null;
            }
            if (avatarStoryParams.drawSegments) {
                if (avatarStoryParams.isStoryCell && !avatarStoryParams.drawInside) {
                    dpf2 = AndroidUtilities.dpf2(3.5f);
                    f3 = -dpf2;
                }
                f3 = 0.0f;
            } else {
                if (avatarStoryParams.isStoryCell && !avatarStoryParams.drawInside) {
                    dpf2 = AndroidUtilities.dpf2(2.7f);
                    f3 = -dpf2;
                }
                f3 = 0.0f;
            }
            if (avatarStoryParams.prevState != 3 || avatarStoryParams.progressToSate == f) {
                paint8.setAlpha((int) (avatarStoryParams.alpha * 255.0f * f6));
                dp = f3 + (AndroidUtilities.dp(5.0f) * (f - f6));
            } else {
                dp = f3 + (AndroidUtilities.dp(7.0f) * f6);
                paint8.setAlpha((int) (avatarStoryParams.alpha * 255.0f * (f - f6)));
            }
            RectF rectF4 = rectTmp;
            rectF4.set(avatarStoryParams.originalAvatarRect);
            rectF4.inset(dp, dp);
            boolean z6 = avatarStoryParams.drawSegments;
            if (z6 && avatarStoryParams.currentState == 3) {
                float f8 = avatarStoryParams.progressToProgressSegments;
                if (f8 != f) {
                    float f9 = f8 + f2;
                    avatarStoryParams.progressToProgressSegments = f9;
                    if (f9 > f) {
                        avatarStoryParams.progressToProgressSegments = f;
                    }
                    float f10 = avatarStoryParams.progressToSegments;
                    avatarStoryParams.progressToSegments = f - avatarStoryParams.progressToProgressSegments;
                    drawSegmentsInternal(canvas, storiesController, imageReceiver, avatarStoryParams, paint8, paint2, paint3, z4);
                    avatarStoryParams.progressToSegments = f10;
                    if (imageReceiver.getParentView() != null) {
                        imageReceiver.invalidate();
                        imageReceiver.getParentView().invalidate();
                    }
                }
            }
            if (z6) {
                int unreadState2 = storiesController.getUnreadState(avatarStoryParams.dialogId);
                if (unreadState2 == 2) {
                    paint8 = paint3;
                } else if (unreadState2 == 1) {
                    paint8 = paint2;
                }
            }
            drawProgress(canvas, avatarStoryParams, imageReceiver.getParentView(), paint8);
        }
        imageReceiver.draw(canvas);
        float f11 = avatarStoryParams.progressToSate;
        if (f11 != f) {
            float f12 = f11 + (AndroidUtilities.screenRefreshTime / 250.0f);
            avatarStoryParams.progressToSate = f12;
            if (f12 > f) {
                avatarStoryParams.progressToSate = f;
            }
            if (imageReceiver.getParentView() != null) {
                imageReceiver.invalidate();
                imageReceiver.getParentView().invalidate();
            }
        }
        int i7 = i3;
        if (i7 != 0) {
            canvas.restoreToCount(i7);
        }
    }

    private static void drawCircleInternal(Canvas canvas, View view, AvatarStoryParams avatarStoryParams, Paint paint, boolean z) {
        if (z) {
            RectF rectF = forumRect;
            rectF.set(rectTmp);
            rectF.inset(AndroidUtilities.dp(0.5f), AndroidUtilities.dp(0.5f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), paint);
            return;
        }
        float f = avatarStoryParams.progressToArc;
        if (f != 0.0f) {
            canvas.drawArc(rectTmp, (f / 2.0f) + 360.0f, 360.0f - f, false, paint);
        } else {
            RectF rectF2 = rectTmp;
            canvas.drawCircle(rectF2.centerX(), rectF2.centerY(), rectF2.width() / 2.0f, paint);
        }
    }

    private static void drawProgress(Canvas canvas, AvatarStoryParams avatarStoryParams, View view, Paint paint) {
        avatarStoryParams.updateProgressParams();
        view.invalidate();
        if (avatarStoryParams.inc) {
            canvas.drawArc(rectTmp, avatarStoryParams.globalAngle, avatarStoryParams.sweepAngle * 360.0f, false, paint);
        } else {
            canvas.drawArc(rectTmp, avatarStoryParams.globalAngle + 360.0f, avatarStoryParams.sweepAngle * (-360.0f), false, paint);
        }
        for (int i = 0; i < 16; i++) {
            float f = (i * 22.5f) + 10.0f;
            canvas.drawArc(rectTmp, avatarStoryParams.globalAngle + f, ((22.5f + f) - 10.0f) - f, false, paint);
        }
    }

    private static void drawSegment(android.graphics.Canvas r9, android.graphics.RectF r10, android.graphics.Paint r11, float r12, float r13, org.telegram.ui.Stories.StoriesUtilities.AvatarStoryParams r14, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.StoriesUtilities.drawSegment(android.graphics.Canvas, android.graphics.RectF, android.graphics.Paint, float, float, org.telegram.ui.Stories.StoriesUtilities$AvatarStoryParams, boolean):void");
    }

    private static void drawSegmentsInternal(android.graphics.Canvas r24, org.telegram.ui.Stories.StoriesController r25, org.telegram.messenger.ImageReceiver r26, org.telegram.ui.Stories.StoriesUtilities.AvatarStoryParams r27, android.graphics.Paint r28, android.graphics.Paint r29, android.graphics.Paint r30, boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.StoriesUtilities.drawSegmentsInternal(android.graphics.Canvas, org.telegram.ui.Stories.StoriesController, org.telegram.messenger.ImageReceiver, org.telegram.ui.Stories.StoriesUtilities$AvatarStoryParams, android.graphics.Paint, android.graphics.Paint, android.graphics.Paint, boolean):void");
    }

    public static EnsureStoryFileLoadedObject ensureStoryFileLoaded(TL_stories.PeerStories peerStories, final Runnable runnable) {
        TL_stories.StoryItem storyItem;
        ArrayList<TLRPC.PhotoSize> arrayList;
        ArrayList<TLRPC.PhotoSize> arrayList2;
        TLRPC.Document document;
        if (peerStories == null || peerStories.stories.isEmpty() || DialogObject.getPeerDialogId(peerStories.peer) == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
            runnable.run();
            return null;
        }
        StoriesController storiesController = MessagesController.getInstance(UserConfig.selectedAccount).storiesController;
        int i = storiesController.dialogIdToMaxReadId.get(DialogObject.getPeerDialogId(peerStories.peer));
        int i2 = 0;
        while (true) {
            if (i2 >= peerStories.stories.size()) {
                storyItem = null;
                break;
            }
            if (peerStories.stories.get(i2).id > i) {
                storyItem = peerStories.stories.get(i2);
                break;
            }
            i2++;
        }
        if (storyItem == null) {
            storyItem = peerStories.stories.get(0);
        }
        TLRPC.MessageMedia messageMedia = storyItem.media;
        if (messageMedia == null || messageMedia.document == null) {
            TLRPC.Photo photo = messageMedia != null ? messageMedia.photo : null;
            if (photo == null || (arrayList = photo.sizes) == null) {
                runnable.run();
                return null;
            }
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(arrayList, Integer.MAX_VALUE), "", false);
            if (pathToAttach != null && pathToAttach.exists()) {
                runnable.run();
                return null;
            }
        } else {
            File pathToAttach2 = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(storyItem.media.document, "", false);
            if (pathToAttach2 != null && pathToAttach2.exists()) {
                runnable.run();
                return null;
            }
            File pathToAttach3 = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(storyItem.media.document, "", true);
            if (pathToAttach3 != null) {
                try {
                    int lastIndexOf = pathToAttach3.getName().lastIndexOf(".");
                    if (lastIndexOf > 0) {
                        File file = new File(pathToAttach3.getParentFile(), pathToAttach3.getName().substring(0, lastIndexOf) + ".temp");
                        if (file.exists() && file.length() > 0) {
                            runnable.run();
                            return null;
                        }
                    }
                } catch (Exception unused) {
                }
            }
        }
        final EnsureStoryFileLoadedObject ensureStoryFileLoadedObject = new EnsureStoryFileLoadedObject(storiesController, DialogObject.getPeerDialogId(peerStories.peer));
        ensureStoryFileLoadedObject.runnable = new Runnable() {
            @Override
            public final void run() {
                StoriesUtilities.lambda$ensureStoryFileLoaded$0(StoriesUtilities.EnsureStoryFileLoadedObject.this, runnable);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public final void run() {
                StoriesUtilities.lambda$ensureStoryFileLoaded$1(r1, ensureStoryFileLoadedObject);
            }
        };
        final Runnable[] runnableArr = {runnable2};
        AndroidUtilities.runOnUIThread(runnable2, 3000L);
        ImageReceiver imageReceiver = new ImageReceiver() {
            @Override
            public boolean setImageBitmapByKey(Drawable drawable, String str, int i3, boolean z, int i4) {
                boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i3, z, i4);
                Runnable runnable3 = runnableArr[0];
                if (runnable3 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable3);
                    ensureStoryFileLoadedObject.runnable.run();
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        onDetachedFromWindow();
                    }
                });
                return imageBitmapByKey;
            }
        };
        ensureStoryFileLoadedObject.imageReceiver = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
        ensureStoryFileLoadedObject.imageReceiver.onAttachedToWindow();
        String storyImageFilter = getStoryImageFilter();
        TLRPC.MessageMedia messageMedia2 = storyItem.media;
        if (messageMedia2 == null || (document = messageMedia2.document) == null) {
            TLRPC.Photo photo2 = messageMedia2 != null ? messageMedia2.photo : null;
            if (photo2 == null || (arrayList2 = photo2.sizes) == null) {
                ensureStoryFileLoadedObject.runnable.run();
                return null;
            }
            ensureStoryFileLoadedObject.imageReceiver.setImage(null, null, ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(arrayList2, Integer.MAX_VALUE), photo2), storyImageFilter, null, null, null, 0L, null, storyItem, 0);
            return ensureStoryFileLoadedObject;
        }
        ensureStoryFileLoadedObject.imageReceiver.setImage(ImageLocation.getForDocument(document), storyImageFilter + "_pframe", null, null, null, 0L, null, storyItem, 0);
        return ensureStoryFileLoadedObject;
    }

    public static Paint getCloseFriendsPaint(ImageReceiver imageReceiver) {
        if (closeFriendsGradientTools == null) {
            GradientTools gradientTools = new GradientTools();
            closeFriendsGradientTools = gradientTools;
            gradientTools.isDiagonal = true;
            gradientTools.isRotate = true;
            gradientTools.setColors(Theme.getColor(Theme.key_stories_circle_closeFriends1), Theme.getColor(Theme.key_stories_circle_closeFriends2));
            closeFriendsGradientTools.paint.setStrokeWidth(AndroidUtilities.dpf2(2.3f));
            closeFriendsGradientTools.paint.setStyle(Paint.Style.STROKE);
            closeFriendsGradientTools.paint.setStrokeCap(Paint.Cap.ROUND);
        }
        closeFriendsGradientTools.setBounds(imageReceiver.getImageX(), imageReceiver.getImageY(), imageReceiver.getImageX2(), imageReceiver.getImageY2());
        return closeFriendsGradientTools.paint;
    }

    public static Paint getErrorPaint(RectF rectF) {
        if (errorGradientTools == null) {
            GradientTools gradientTools = new GradientTools();
            errorGradientTools = gradientTools;
            gradientTools.isDiagonal = true;
            gradientTools.isRotate = true;
            int color = Theme.getColor(Theme.key_color_orange);
            int color2 = Theme.getColor(Theme.key_text_RedBold);
            errorGradientTools.setColors(ColorUtils.blendARGB(color, color2, 0.25f), color2);
            errorGradientTools.paint.setStrokeWidth(AndroidUtilities.dpf2(2.3f));
            errorGradientTools.paint.setStyle(Paint.Style.STROKE);
            errorGradientTools.paint.setStrokeCap(Paint.Cap.ROUND);
        }
        errorGradientTools.setBounds(rectF.left, rectF.top, rectF.right, rectF.bottom);
        return errorGradientTools.paint;
    }

    public static Paint getErrorPaint(ImageReceiver imageReceiver) {
        if (errorGradientTools == null) {
            GradientTools gradientTools = new GradientTools();
            errorGradientTools = gradientTools;
            gradientTools.isDiagonal = true;
            gradientTools.isRotate = true;
            int color = Theme.getColor(Theme.key_color_orange);
            int color2 = Theme.getColor(Theme.key_text_RedBold);
            errorGradientTools.setColors(ColorUtils.blendARGB(color, color2, 0.25f), color2);
            errorGradientTools.paint.setStrokeWidth(AndroidUtilities.dpf2(2.3f));
            errorGradientTools.paint.setStyle(Paint.Style.STROKE);
            errorGradientTools.paint.setStrokeCap(Paint.Cap.ROUND);
        }
        errorGradientTools.setBounds(imageReceiver.getImageX(), imageReceiver.getImageY(), imageReceiver.getImageX2(), imageReceiver.getImageY2());
        return errorGradientTools.paint;
    }

    public static Drawable getExpiredStoryDrawable() {
        if (expiredStoryDrawable == null) {
            Bitmap createBitmap = Bitmap.createBitmap(360, 180, Bitmap.Config.ARGB_8888);
            createBitmap.eraseColor(-7829368);
            Canvas canvas = new Canvas(createBitmap);
            TextPaint textPaint = new TextPaint(1);
            textPaint.setTextSize(15.0f);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 100));
            canvas.drawText("expired", 180.0f, 86.0f, textPaint);
            canvas.drawText("story", 180.0f, 106.0f, textPaint);
            expiredStoryDrawable = new BitmapDrawable(createBitmap);
        }
        return expiredStoryDrawable;
    }

    private static int getInset(int i, int i2) {
        float f;
        if (i == 3) {
            i = i2;
        }
        if (i == 2) {
            f = 3.0f;
        } else {
            if (i != 1) {
                return 0;
            }
            f = 4.0f;
        }
        return AndroidUtilities.dp(f);
    }

    public static int getPredictiveUnreadState(StoriesController storiesController, long j) {
        if (j == 0) {
            return 0;
        }
        MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
        if (j <= 0) {
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
            if (chat == null || chat.stories_max_id <= 0 || chat.stories_unavailable) {
                return 0;
            }
            return chat.stories_max_id > storiesController.dialogIdToMaxReadId.get(j, 0) ? 1 : 2;
        }
        TLRPC.User user = messagesController.getUser(Long.valueOf(j));
        if (j == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId || user == null || user.stories_max_id <= 0 || user.stories_unavailable) {
            return 0;
        }
        return user.stories_max_id > storiesController.dialogIdToMaxReadId.get(j, 0) ? 1 : 2;
    }

    public static String getStoryImageFilter() {
        int max = (int) (Math.max(AndroidUtilities.getRealScreenSize().x, AndroidUtilities.getRealScreenSize().y) / AndroidUtilities.density);
        return max + "_" + max;
    }

    public static Paint getUnreadCirclePaint(ImageReceiver imageReceiver, boolean z) {
        checkStoriesGradientTools(z);
        storiesGradientTools[z ? 1 : 0].setBounds(imageReceiver.getImageX(), imageReceiver.getImageY(), imageReceiver.getImageX2(), imageReceiver.getImageY2());
        return storiesGradientTools[z ? 1 : 0].paint;
    }

    public static CharSequence getUploadingStr(TextView textView, boolean z, boolean z2) {
        String string = LocaleController.getString(z2 ? R.string.StoryEditing : R.string.UploadingStory);
        if (string.indexOf("…") <= 0) {
            return string;
        }
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(string);
        UploadingDotsSpannable uploadingDotsSpannable = new UploadingDotsSpannable();
        valueOf.setSpan(uploadingDotsSpannable, valueOf.length() - 1, valueOf.length(), 0);
        uploadingDotsSpannable.setParent(textView, z);
        return valueOf;
    }

    public static boolean hasExpiredViews(TL_stories.StoryItem storyItem) {
        return storyItem != null && ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime() > storyItem.expire_date + 86400;
    }

    public static boolean isExpired(int i, TL_stories.StoryItem storyItem) {
        return ConnectionsManager.getInstance(i).getCurrentTime() > storyItem.expire_date;
    }

    public static void lambda$ensureStoryFileLoaded$0(EnsureStoryFileLoadedObject ensureStoryFileLoadedObject, Runnable runnable) {
        if (ensureStoryFileLoadedObject.cancelled) {
            return;
        }
        runnable.run();
    }

    public static void lambda$ensureStoryFileLoaded$1(Runnable[] runnableArr, EnsureStoryFileLoadedObject ensureStoryFileLoadedObject) {
        runnableArr[0] = null;
        ensureStoryFileLoadedObject.runnable.run();
        ImageReceiver imageReceiver = ensureStoryFileLoadedObject.imageReceiver;
        if (imageReceiver != null) {
            imageReceiver.onDetachedFromWindow();
        }
    }

    public static void setImage(ImageReceiver imageReceiver, TL_stories.StoryItem storyItem) {
        setImage(imageReceiver, storyItem, "320_320");
    }

    public static void setImage(ImageReceiver imageReceiver, TL_stories.StoryItem storyItem, String str) {
        ArrayList<TLRPC.PhotoSize> arrayList;
        StoryWidgetsImageDecorator storyWidgetsImageDecorator;
        TLRPC.Document document;
        if (storyItem == null) {
            return;
        }
        TLRPC.MessageMedia messageMedia = storyItem.media;
        if (messageMedia == null || (document = messageMedia.document) == null) {
            TLRPC.Photo photo = messageMedia != null ? messageMedia.photo : null;
            if (messageMedia instanceof TLRPC.TL_messageMediaUnsupported) {
                Bitmap createBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                createBitmap.eraseColor(ColorUtils.blendARGB(-16777216, -1, 0.2f));
                imageReceiver.setImageBitmap(createBitmap);
                storyWidgetsImageDecorator = new StoryWidgetsImageDecorator(storyItem);
            } else if (photo == null || (arrayList = photo.sizes) == null) {
                imageReceiver.clearImage();
                return;
            } else {
                imageReceiver.setImage(null, null, ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(arrayList, Integer.MAX_VALUE), photo), str, null, null, ImageLoader.createStripedBitmap(photo.sizes), 0L, null, storyItem, 0);
                storyWidgetsImageDecorator = new StoryWidgetsImageDecorator(storyItem);
            }
        } else {
            imageReceiver.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, Integer.MAX_VALUE), storyItem.media.document), str, null, null, ImageLoader.createStripedBitmap(storyItem.media.document.thumbs), 0L, null, storyItem, 0);
            storyWidgetsImageDecorator = new StoryWidgetsImageDecorator(storyItem);
        }
        imageReceiver.addDecorator(storyWidgetsImageDecorator);
    }

    public static void setImage(ImageReceiver imageReceiver, StoriesController.UploadingStory uploadingStory) {
        imageReceiver.setImage(ImageLocation.getForPath(uploadingStory.entry.isVideo ? uploadingStory.firstFramePath : uploadingStory.path), "320_180", null, null, null, 0L, null, null, 0);
    }

    public static void setStoryMiniImage(ImageReceiver imageReceiver, TL_stories.StoryItem storyItem) {
        ArrayList<TLRPC.PhotoSize> arrayList;
        if (storyItem == null) {
            return;
        }
        TLRPC.MessageMedia messageMedia = storyItem.media;
        TLRPC.Document document = messageMedia.document;
        if (document != null) {
            imageReceiver.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 1000), storyItem.media.document), "100_100", null, null, ImageLoader.createStripedBitmap(storyItem.media.document.thumbs), 0L, null, storyItem, 0);
            return;
        }
        TLRPC.Photo photo = messageMedia.photo;
        if (photo == null || (arrayList = photo.sizes) == null) {
            imageReceiver.clearImage();
        } else {
            imageReceiver.setImage(null, null, ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(arrayList, 1000), photo), "100_100", null, null, ImageLoader.createStripedBitmap(photo.sizes), 0L, null, storyItem, 0);
        }
    }

    public static void setThumbImage(ImageReceiver imageReceiver, TL_stories.StoryItem storyItem, int i, int i2) {
        ArrayList<TLRPC.PhotoSize> arrayList;
        TLRPC.Document document;
        TLRPC.MessageMedia messageMedia = storyItem.media;
        if (messageMedia != null && (document = messageMedia.document) != null) {
            imageReceiver.setImage(ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, AndroidUtilities.dp(Math.max(i, i2)), false, null, true), storyItem.media.document), i + "_" + i2, null, null, ImageLoader.createStripedBitmap(storyItem.media.document.thumbs), 0L, null, storyItem, 0);
            return;
        }
        TLRPC.Photo photo = messageMedia != null ? messageMedia.photo : null;
        if (photo == null || (arrayList = photo.sizes) == null) {
            imageReceiver.clearImage();
            return;
        }
        imageReceiver.setImage(null, null, ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.dp(Math.max(i, i2)), false, null, true), photo), i + "_" + i2, null, null, ImageLoader.createStripedBitmap(photo.sizes), 0L, null, storyItem, 0);
    }

    public static void updateColors() {
        GradientTools gradientTools = closeFriendsGradientTools;
        if (gradientTools != null) {
            gradientTools.setColors(Theme.getColor(Theme.key_stories_circle_closeFriends1), Theme.getColor(Theme.key_stories_circle_closeFriends2));
        }
        GradientTools gradientTools2 = storiesGradientTools[0];
        if (gradientTools2 != null) {
            gradientTools2.setColors(Theme.getColor(Theme.key_stories_circle_dialog1), Theme.getColor(Theme.key_stories_circle_dialog2));
        }
        GradientTools gradientTools3 = storiesGradientTools[1];
        if (gradientTools3 != null) {
            gradientTools3.setColors(Theme.getColor(Theme.key_stories_circle1), Theme.getColor(Theme.key_stories_circle2));
        }
        if (errorGradientTools != null) {
            int color = Theme.getColor(Theme.key_color_orange);
            int color2 = Theme.getColor(Theme.key_text_RedBold);
            errorGradientTools.setColors(ColorUtils.blendARGB(color, color2, 0.25f), color2);
        }
    }
}
