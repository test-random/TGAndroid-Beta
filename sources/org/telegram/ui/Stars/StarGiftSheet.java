package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonSpan;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CompatDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TableView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TwoStepVerificationActivity;
import org.telegram.ui.TwoStepVerificationSetupActivity;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.BotWebViewSheet;

public class StarGiftSheet extends BottomSheet {
    private final LinkSpanDrawable.LinksTextView afterTableTextView;
    private final LinkSpanDrawable.LinksTextView beforeTableTextView;
    private final FrameLayout bottomBulletinContainer;
    private final ButtonWithCounterView button;
    private final CheckBox2 checkbox;
    private final LinearLayout checkboxLayout;
    private final View checkboxSeparator;
    private final TextView checkboxTextView;
    private ContainerView container;
    private HintView2 currentHintView;
    private View currentHintViewTextView;
    private PageTransition currentPage;
    private final long dialogId;
    private FireworksOverlay fireworksOverlay;
    private boolean firstSet;
    private final LinearLayout infoLayout;
    private boolean isLearnMore;
    private ColoredImageSpan lockSpan;
    private MessageObject messageObject;
    private boolean messageObjectRepolled;
    private boolean messageObjectRepolling;
    private boolean myProfile;
    private boolean onlyWearInfo;
    private View ownerTextView;
    private ArrayList sample_attributes;
    private TL_stars.SavedStarGift savedStarGift;
    private ShareAlert shareAlert;
    private boolean shownWearInfo;
    private String slug;
    private TL_stars.TL_starGiftUnique slugStarGift;
    private ValueAnimator switchingPagesAnimator;
    private final TableView tableView;
    private final TopView topView;
    private Boolean unsavedFromSavedStarGift;
    private final AffiliateProgramFragment.FeatureCell[] upgradeFeatureCells;
    private ColoredImageSpan upgradeIconSpan;
    private final LinearLayout upgradeLayout;
    private TLRPC.PaymentForm upgrade_form;
    private boolean userStarGiftRepolled;
    private boolean userStarGiftRepolling;
    private final AffiliateProgramFragment.FeatureCell[] wearFeatureCells;
    private final LinearLayout wearLayout;
    private final TextView wearSubtitle;
    private final TextView wearTitle;

    public class AnonymousClass1 extends BaseFragment {
        AnonymousClass1() {
        }

        @Override
        public Context getContext() {
            return StarGiftSheet.this.getContext();
        }

        @Override
        public int getCurrentAccount() {
            return this.currentAccount;
        }

        @Override
        public Activity getParentActivity() {
            for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
            }
            return null;
        }

        @Override
        public Dialog showDialog(Dialog dialog) {
            dialog.show();
            return dialog;
        }
    }

    public class AnonymousClass2 extends ShareAlert {
        AnonymousClass2(Context context, ChatActivity chatActivity, ArrayList arrayList, String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3, boolean z4, Integer num, Theme.ResourcesProvider resourcesProvider) {
            super(context, chatActivity, arrayList, str, str2, z, str3, str4, z2, z3, z4, num, resourcesProvider);
            this.includeStoryFromMessage = true;
        }

        @Override
        public void onSend(LongSparseArray longSparseArray, int i, TLRPC.TL_forumTopic tL_forumTopic) {
            Bulletin createSimpleBulletin;
            super.onSend(longSparseArray, i, tL_forumTopic);
            BulletinFactory bulletinFactory = StarGiftSheet.this.getBulletinFactory();
            if (bulletinFactory != null) {
                if (longSparseArray.size() == 1) {
                    long keyAt = longSparseArray.keyAt(0);
                    if (keyAt == UserConfig.getInstance(this.currentAccount).clientUserId) {
                        createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.saved_messages, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LinkSharedToSavedMessages, new Object[0])), 5000);
                    } else if (keyAt < 0) {
                        createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LinkSharedTo, tL_forumTopic != null ? tL_forumTopic.title : MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-keyAt)).title)), 5000);
                    } else {
                        createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LinkSharedTo, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(keyAt)).first_name)), 5000);
                    }
                } else {
                    createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(LocaleController.formatPluralString("LinkSharedToManyChats", longSparseArray.size(), Integer.valueOf(longSparseArray.size()))));
                }
                createSimpleBulletin.hideAfterBottomSheet(false).show();
                try {
                    this.container.performHapticFeedback(3);
                } catch (Exception unused) {
                }
            }
        }

        @Override
        protected void onShareStory(View view) {
            StarGiftSheet.this.repostStory(view);
        }
    }

    public class AnonymousClass3 implements ShareAlert.ShareAlertDelegate {
        AnonymousClass3() {
        }

        @Override
        public boolean didCopy() {
            StarGiftSheet.this.getBulletinFactory().createCopyLinkBulletin(false).show();
            return true;
        }

        @Override
        public void didShare() {
            ShareAlert.ShareAlertDelegate.CC.$default$didShare(this);
        }
    }

    public class AnonymousClass4 extends AnimatorListenerAdapter {
        final Runnable val$done;
        final int val$page;

        AnonymousClass4(int i, Runnable runnable) {
            r2 = i;
            r3 = runnable;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            StarGiftSheet.this.onSwitchedPage();
            StarGiftSheet.this.infoLayout.setVisibility(r2 == 0 ? 0 : 8);
            StarGiftSheet.this.upgradeLayout.setVisibility(r2 == 1 ? 0 : 8);
            StarGiftSheet.this.wearLayout.setVisibility(r2 == 2 ? 0 : 8);
            StarGiftSheet.this.switchingPagesAnimator = null;
            Runnable runnable = r3;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public class AnonymousClass5 extends ClickableSpan {
        final long val$sender_id;

        AnonymousClass5(long j) {
            r2 = j;
        }

        @Override
        public void onClick(View view) {
            StarGiftSheet.this.lambda$set$28(r2);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setColor(textPaint.linkColor);
        }
    }

    public class AnonymousClass6 extends ClickableSpan {
        final long val$recipient_id;

        AnonymousClass6(long j) {
            r2 = j;
        }

        @Override
        public void onClick(View view) {
            StarGiftSheet.this.lambda$set$28(r2);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setColor(textPaint.linkColor);
        }
    }

    public class AnonymousClass7 extends BaseFragment {
        final UserSelectorBottomSheet[] val$sheet;

        AnonymousClass7(UserSelectorBottomSheet[] userSelectorBottomSheetArr) {
            r2 = userSelectorBottomSheetArr;
        }

        @Override
        public Context getContext() {
            return StarGiftSheet.this.getContext();
        }

        @Override
        public Activity getParentActivity() {
            LaunchActivity launchActivity = LaunchActivity.instance;
            return launchActivity == null ? AndroidUtilities.findActivity(StarGiftSheet.this.getContext()) : launchActivity;
        }

        @Override
        public Theme.ResourcesProvider getResourceProvider() {
            return ((BottomSheet) StarGiftSheet.this).resourcesProvider;
        }

        @Override
        public boolean presentFragment(BaseFragment baseFragment) {
            r2[0].lambda$new$0();
            StarGiftSheet.this.lambda$new$0();
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                return safeLastFragment.presentFragment(safeLastFragment);
            }
            return false;
        }
    }

    public class ContainerView extends FrameLayout {
        private final Paint backgroundPaint;
        private final Path path;
        private final RectF rect;

        public ContainerView(Context context) {
            super(context);
            this.rect = new RectF();
            this.backgroundPaint = new Paint(1);
            this.path = new Path();
            setWillNotDraw(false);
            setClipChildren(false);
            setClipToPadding(false);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            float pVar = top();
            float dp = AndroidUtilities.dp(12.0f);
            this.rect.set(((BottomSheet) StarGiftSheet.this).backgroundPaddingLeft, pVar, getWidth() - ((BottomSheet) StarGiftSheet.this).backgroundPaddingLeft, getHeight() + dp);
            this.backgroundPaint.setColor(StarGiftSheet.this.getThemedColor(Theme.key_dialogBackground));
            this.path.rewind();
            this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
            canvas.drawPath(this.path, this.backgroundPaint);
            canvas.clipPath(this.path);
            super.dispatchDraw(canvas);
            updateTopViewTranslation();
            canvas.restore();
        }

        public float height() {
            return StarGiftSheet.this.topView.getRealHeight() + 0.0f + (StarGiftSheet.this.currentPage.at(0) * StarGiftSheet.this.infoLayout.getMeasuredHeight()) + (StarGiftSheet.this.currentPage.at(1) * StarGiftSheet.this.upgradeLayout.getMeasuredHeight()) + (StarGiftSheet.this.currentPage.at(2) * StarGiftSheet.this.wearLayout.getMeasuredHeight());
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            StarGiftSheet.this.onSwitchedPage();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt = getChildAt(i3);
                if (childAt == StarGiftSheet.this.button) {
                    StarGiftSheet.this.button.measure(View.MeasureSpec.makeMeasureSpec((size - (((BottomSheet) StarGiftSheet.this).backgroundPaddingLeft * 2)) - (AndroidUtilities.dp(14.0f) * 2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
                } else if (childAt instanceof HintView2) {
                    childAt.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824));
                } else {
                    childAt.measure(i, i2);
                }
            }
            setMeasuredDimension(size, Math.max(StarGiftSheet.this.infoLayout.getMeasuredHeight(), StarGiftSheet.this.upgradeLayout.getMeasuredHeight()) + StarGiftSheet.this.topView.getMeasuredHeight());
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            FrameLayout frameLayout = StarGiftSheet.this.topBulletinContainer;
            if (frameLayout != null) {
                frameLayout.setTranslationY((getTranslationY() - height()) - AndroidUtilities.navigationBarHeight);
            }
        }

        public float top() {
            return Math.max(0.0f, getHeight() - height());
        }

        public void updateTopViewTranslation() {
            StarGiftSheet.this.topView.setTranslationY(StarGiftSheet.this.topView.getMeasuredHeight() - height());
            FrameLayout frameLayout = StarGiftSheet.this.topBulletinContainer;
            if (frameLayout != null) {
                frameLayout.setTranslationY((getTranslationY() - height()) - AndroidUtilities.navigationBarHeight);
            }
        }
    }

    public static class GiftTransferTopView extends View {
        private final Paint arrowPaint;
        private final Path arrowPath;
        private final StarGiftDrawableIcon giftDrawable;
        private final ImageReceiver userImageReceiver;

        public GiftTransferTopView(Context context, TL_stars.StarGift starGift) {
            super(context);
            Path path = new Path();
            this.arrowPath = path;
            Paint paint = new Paint(1);
            this.arrowPaint = paint;
            StarGiftDrawableIcon starGiftDrawableIcon = new StarGiftDrawableIcon(this, starGift, 60, 0.27f);
            this.giftDrawable = starGiftDrawableIcon;
            starGiftDrawableIcon.setPatternsType(3);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.userImageReceiver = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(30.0f));
            imageReceiver.setImageBitmap(SessionCell.createDrawable(60, "fragment"));
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(AndroidUtilities.dp(2.33f));
            path.rewind();
            path.moveTo(0.0f, -AndroidUtilities.dp(8.0f));
            path.lineTo(AndroidUtilities.dp(6.166f), 0.0f);
            path.lineTo(0.0f, AndroidUtilities.dp(8.0f));
        }

        public GiftTransferTopView(Context context, TL_stars.StarGift starGift, TLObject tLObject) {
            super(context);
            Path path = new Path();
            this.arrowPath = path;
            Paint paint = new Paint(1);
            this.arrowPaint = paint;
            StarGiftDrawableIcon starGiftDrawableIcon = new StarGiftDrawableIcon(this, starGift, 60, 0.27f);
            this.giftDrawable = starGiftDrawableIcon;
            starGiftDrawableIcon.setPatternsType(3);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(tLObject);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.userImageReceiver = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(30.0f));
            imageReceiver.setForUserOrChat(tLObject, avatarDrawable);
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            path.rewind();
            path.moveTo(0.0f, -AndroidUtilities.dp(8.0f));
            path.lineTo(AndroidUtilities.dp(6.166f), 0.0f);
            path.lineTo(0.0f, AndroidUtilities.dp(8.0f));
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.userImageReceiver.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.userImageReceiver.onDetachedFromWindow();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int width = (getWidth() / 2) - (AndroidUtilities.dp(156.0f) / 2);
            int height = (getHeight() / 2) - AndroidUtilities.dp(30.0f);
            this.giftDrawable.setBounds(width, height, AndroidUtilities.dp(60.0f) + width, AndroidUtilities.dp(60.0f) + height);
            this.giftDrawable.draw(canvas);
            canvas.save();
            canvas.translate((getWidth() / 2.0f) - (AndroidUtilities.dp(6.166f) / 2.0f), getHeight() / 2.0f);
            canvas.drawPath(this.arrowPath, this.arrowPaint);
            canvas.restore();
            this.userImageReceiver.setImageCoords(width + AndroidUtilities.dp(96.0f), height, AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f));
            this.userImageReceiver.draw(canvas);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824));
        }
    }

    public static class PageTransition {
        public int from;
        public float progress;
        public int to;

        public PageTransition(int i, int i2, float f) {
            this.from = i;
            this.to = i2;
        }

        public float at(int i) {
            int i2 = this.to;
            if (i2 == i && this.from == i) {
                return 1.0f;
            }
            if (i2 == i) {
                return this.progress;
            }
            if (this.from == i) {
                return 1.0f - this.progress;
            }
            return 0.0f;
        }

        public float at(int i, int i2) {
            if (contains(i) && contains(i2)) {
                return 1.0f;
            }
            return Math.max(at(i), at(i2));
        }

        public boolean contains(int i) {
            return this.from == i || this.to == i;
        }

        public boolean is(int i) {
            return this.to == i;
        }

        public void setProgress(float f) {
            this.progress = f;
        }
    }

    public static class StarGiftDrawableIcon extends CompatDrawable {
        private RadialGradient gradient;
        private final ImageReceiver imageReceiver;
        private final Matrix matrix;
        private final Path path;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable pattern;
        private float patternsScale;
        private int patternsType;
        private final RectF rect;
        private int rounding;
        private final int sizeDp;

        public StarGiftDrawableIcon(View view, TL_stars.StarGift starGift, int i, float f) {
            super(view);
            this.path = new Path();
            this.rect = new RectF();
            this.matrix = new Matrix();
            this.rounding = AndroidUtilities.dp(16.0f);
            this.patternsType = 0;
            this.patternsScale = f;
            ImageReceiver imageReceiver = new ImageReceiver(view);
            this.imageReceiver = imageReceiver;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(view, false, AndroidUtilities.dp(i > 180 ? 24.0f : 18.0f));
            this.pattern = swapAnimatedEmojiDrawable;
            this.sizeDp = i;
            if (starGift != null) {
                TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeBackdrop.class);
                TL_stars.starGiftAttributePattern stargiftattributepattern = (TL_stars.starGiftAttributePattern) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributePattern.class);
                TL_stars.starGiftAttributeModel stargiftattributemodel = (TL_stars.starGiftAttributeModel) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeModel.class);
                if (stargiftattributepattern != null) {
                    swapAnimatedEmojiDrawable.set(stargiftattributepattern.document, false);
                }
                if (stargiftattributebackdrop != null) {
                    this.gradient = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dpf2(i) / 2.0f, new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                    swapAnimatedEmojiDrawable.setColor(Integer.valueOf(stargiftattributebackdrop.pattern_color | (-16777216)));
                }
                if (stargiftattributemodel != null) {
                    StarsIntroActivity.setGiftImage(imageReceiver, stargiftattributemodel.document, (int) (i * 0.75f));
                }
            }
            this.paint.setShader(this.gradient);
            if (view.isAttachedToWindow()) {
                onAttachedToWindow();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            this.rect.set(getBounds());
            canvas.save();
            this.path.rewind();
            Path path = this.path;
            RectF rectF = this.rect;
            float f = this.rounding;
            path.addRoundRect(rectF, f, f, Path.Direction.CW);
            canvas.clipPath(this.path);
            if (this.gradient != null) {
                this.matrix.reset();
                this.matrix.postTranslate(this.rect.centerX(), this.rect.centerY());
                this.gradient.setLocalMatrix(this.matrix);
                this.paint.setShader(this.gradient);
            }
            canvas.drawPaint(this.paint);
            canvas.save();
            canvas.translate(this.rect.centerX(), this.rect.centerY());
            StarGiftPatterns.drawPattern(canvas, this.patternsType, this.pattern, this.rect.width(), this.rect.height(), 1.0f, this.patternsScale);
            canvas.restore();
            float min = Math.min(this.rect.width(), this.rect.height()) * 0.75f;
            float f2 = min / 2.0f;
            this.imageReceiver.setImageCoords(this.rect.centerX() - f2, this.rect.centerY() - f2, min, min);
            this.imageReceiver.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(this.sizeDp);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(this.sizeDp);
        }

        @Override
        public void onAttachedToWindow() {
            this.pattern.attach();
            this.imageReceiver.onAttachedToWindow();
        }

        @Override
        public void onDetachedToWindow() {
            this.pattern.detach();
            this.imageReceiver.onDetachedFromWindow();
        }

        public StarGiftDrawableIcon setPatternsType(int i) {
            this.patternsType = i;
            return this;
        }

        public StarGiftDrawableIcon setRounding(int i) {
            this.rounding = i;
            return this;
        }
    }

    public static class TopView extends FrameLayout {
        private boolean attached;
        private BackupImageView avatarView;
        private final TL_stars.starGiftAttributeBackdrop[] backdrop;
        private BagRandomizer backdrops;
        private final RadialGradient[] backgroundGradient;
        private final Matrix[] backgroundMatrix;
        private final Paint[] backgroundPaint;
        public final Button[] buttons;
        private int buttonsBackgroundColor;
        private final LinearLayout buttonsLayout;
        private final Runnable checkToRotateRunnable;
        private final ImageView closeView;
        private int currentImageIndex;
        private PageTransition currentPage;
        private boolean hasLink;
        private final FrameLayout imageLayout;
        private final BackupImageView[] imageView;
        private final LinearLayout[] layout;
        private final FrameLayout.LayoutParams[] layoutLayoutParams;
        private BagRandomizer models;
        private final ImageView optionsView;
        private StarsReactionsSheet.Particles particles;
        private final RectF particlesBounds;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] pattern;
        private BagRandomizer patterns;
        private LinkSpanDrawable.LinksTextView priceView;
        private LinearGradient profileBackgroundGradient;
        private final Matrix profileBackgroundMatrix;
        private Paint profileBackgroundPaint;
        private final Theme.ResourcesProvider resourcesProvider;
        private ValueAnimator rotationAnimator;
        private ArrayList sampleAttributes;
        private final LinkSpanDrawable.LinksTextView[] subtitleView;
        private final LinearLayout.LayoutParams[] subtitleViewLayoutParams;
        private ValueAnimator switchAnimator;
        private float switchScale;
        private final LinkSpanDrawable.LinksTextView[] titleView;
        private float toggleBackdrop;
        private int toggled;
        private FrameLayout userLayout;
        private float wearImageScale;
        private float wearImageTx;
        private float wearImageTy;
        private TLObject wearPreviewObject;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            AnonymousClass1() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                TopView.this.toggleBackdrop = r3.toggled;
                TopView topView = TopView.this;
                topView.onSwitchPage(topView.currentPage);
                StarsIntroActivity.setGiftImage(TopView.this.imageView[2 - TopView.this.toggled].getImageReceiver(), ((TL_stars.starGiftAttributeModel) TopView.this.models.getNext()).document, 160);
                AndroidUtilities.cancelRunOnUIThread(TopView.this.checkToRotateRunnable);
                AndroidUtilities.runOnUIThread(TopView.this.checkToRotateRunnable, 2500L);
            }
        }

        public class AnonymousClass2 extends AnimatorListenerAdapter {
            AnonymousClass2() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                TopView.this.switchScale = 1.0f;
                TopView.this.imageLayout.setScaleX(TopView.this.switchScale);
                TopView.this.imageLayout.setScaleY(TopView.this.switchScale);
                TopView.this.invalidate();
            }
        }

        public static class Button extends FrameLayout {
            public ImageView imageView;
            public TextView textView;

            public Button(Context context) {
                super(context);
                ImageView imageView = new ImageView(context);
                this.imageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                addView(this.imageView, LayoutHelper.createFrame(24, 24.0f, 49, 0.0f, 8.0f, 0.0f, 0.0f));
                TextView textView = new TextView(context);
                this.textView = textView;
                textView.setTypeface(AndroidUtilities.bold());
                this.textView.setTextSize(1, 12.0f);
                this.textView.setTextColor(-1);
                this.textView.setGravity(17);
                addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 49, 4.0f, 35.0f, 4.0f, 0.0f));
            }

            public void set(int i, CharSequence charSequence, boolean z) {
                if (z) {
                    AndroidUtilities.updateImageViewImageAnimated(this.imageView, i);
                } else {
                    this.imageView.setImageResource(i);
                }
                this.textView.setText(charSequence);
            }
        }

        public TopView(Context context, Theme.ResourcesProvider resourcesProvider, final Runnable runnable, View.OnClickListener onClickListener, View.OnClickListener onClickListener2, View.OnClickListener onClickListener3, View.OnClickListener onClickListener4) {
            super(context);
            this.imageView = new BackupImageView[3];
            boolean z = false;
            this.currentImageIndex = 0;
            this.layout = new LinearLayout[3];
            this.layoutLayoutParams = new FrameLayout.LayoutParams[3];
            this.titleView = new LinkSpanDrawable.LinksTextView[3];
            this.subtitleView = new LinkSpanDrawable.LinksTextView[3];
            this.subtitleViewLayoutParams = new LinearLayout.LayoutParams[3];
            this.currentPage = new PageTransition(0, 0, 1.0f);
            this.backdrop = new TL_stars.starGiftAttributeBackdrop[3];
            this.checkToRotateRunnable = new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.TopView.this.lambda$new$1();
                }
            };
            this.backgroundPaint = new Paint[3];
            this.backgroundGradient = new RadialGradient[3];
            this.backgroundMatrix = new Matrix[3];
            this.profileBackgroundMatrix = new Matrix();
            this.profileBackgroundPaint = new Paint(1);
            this.pattern = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[2];
            int i = 0;
            while (true) {
                Paint[] paintArr = this.backgroundPaint;
                if (i >= paintArr.length) {
                    break;
                }
                paintArr[i] = new Paint(1);
                i++;
            }
            int i2 = 0;
            while (true) {
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.pattern;
                if (i2 >= swapAnimatedEmojiDrawableArr.length) {
                    break;
                }
                swapAnimatedEmojiDrawableArr[i2] = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(28.0f));
                i2++;
            }
            this.switchScale = 1.0f;
            this.particlesBounds = new RectF();
            this.resourcesProvider = resourcesProvider;
            setWillNotDraw(false);
            this.imageLayout = new FrameLayout(context);
            int i3 = 0;
            while (i3 < 3) {
                this.imageView[i3] = new BackupImageView(context);
                this.imageView[i3].setLayerNum(6660);
                if (i3 > 0) {
                    this.imageView[i3].getImageReceiver().setCrossfadeDuration(1);
                }
                this.imageLayout.addView(this.imageView[i3], LayoutHelper.createFrame(-1, -1, 119));
                this.imageView[i3].setAlpha(i3 == this.currentImageIndex ? 1.0f : 0.0f);
                i3++;
            }
            LinearLayout linearLayout = new LinearLayout(context);
            this.buttonsLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.buttons = new Button[3];
            int i4 = 0;
            while (true) {
                Button[] buttonArr = this.buttons;
                if (i4 >= buttonArr.length) {
                    break;
                }
                buttonArr[i4] = new Button(context);
                if (i4 == 0) {
                    this.buttons[i4].set(R.drawable.filled_gift_transfer, LocaleController.getString(R.string.Gift2ActionTransfer), z);
                    this.buttons[i4].setOnClickListener(onClickListener2);
                } else if (i4 == 1) {
                    this.buttons[i4].set(R.drawable.filled_crown_on, LocaleController.getString(R.string.Gift2ActionWear), z);
                    this.buttons[i4].setOnClickListener(onClickListener3);
                } else if (i4 == 2) {
                    this.buttons[i4].set(R.drawable.filled_share, LocaleController.getString(R.string.Gift2ActionShare), z);
                    this.buttons[i4].setOnClickListener(onClickListener4);
                }
                ScaleStateListAnimator.apply(this.buttons[i4], 0.075f, 1.5f);
                this.buttonsLayout.addView(this.buttons[i4], LayoutHelper.createLinear(0, 56, 1.0f, 119, i4 == 0 ? 0 : 11, 0, 0, 0));
                i4++;
                z = false;
            }
            int i5 = 0;
            for (int i6 = 3; i5 < i6; i6 = 3) {
                this.layout[i5] = new LinearLayout(context);
                this.layout[i5].setOrientation(1);
                View view = this.layout[i5];
                FrameLayout.LayoutParams[] layoutParamsArr = this.layoutLayoutParams;
                ViewGroup.LayoutParams createFrame = LayoutHelper.createFrame(-1, -2.0f, 119, 16.0f, i5 == 2 ? 64.0f : 170.0f, 16.0f, 0.0f);
                layoutParamsArr[i5] = createFrame;
                addView(view, createFrame);
                if (i5 == 2) {
                    FrameLayout frameLayout = new FrameLayout(context);
                    this.userLayout = frameLayout;
                    this.layout[i5].addView(frameLayout, LayoutHelper.createLinear(-1, 104, 119));
                    BackupImageView backupImageView = new BackupImageView(context);
                    this.avatarView = backupImageView;
                    backupImageView.setRoundRadius(AndroidUtilities.dp(30.0f));
                    this.userLayout.addView(this.avatarView, LayoutHelper.createFrame(60, 60.0f, 19, 1.0f, 0.0f, 0.0f, 0.0f));
                    this.titleView[i5] = new LinkSpanDrawable.LinksTextView(context);
                    this.titleView[i5].setTextColor(-1);
                    this.titleView[i5].setTextSize(1, 20.0f);
                    this.titleView[i5].setTypeface(AndroidUtilities.bold());
                    this.titleView[i5].setSingleLine();
                    LinkSpanDrawable.LinksTextView linksTextView = this.titleView[i5];
                    TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
                    linksTextView.setEllipsize(truncateAt);
                    this.userLayout.addView(this.titleView[i5], LayoutHelper.createFrame(-1, -2.0f, 55, 81.0f, 30.33f, 40.0f, 0.0f));
                    this.subtitleView[i5] = new LinkSpanDrawable.LinksTextView(context);
                    this.subtitleView[i5].setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
                    this.subtitleView[i5].setTextSize(1, 14.0f);
                    this.subtitleView[i5].setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                    this.subtitleView[i5].setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                    this.subtitleView[i5].setDisablePaddingsOffsetY(true);
                    this.subtitleView[i5].setSingleLine();
                    this.titleView[i5].setEllipsize(truncateAt);
                    this.userLayout.addView(this.subtitleView[i5], LayoutHelper.createFrame(-1, -2.0f, 55, 81.0f, 57.0f, 4.0f, 0.0f));
                } else {
                    this.titleView[i5] = new LinkSpanDrawable.LinksTextView(context);
                    LinkSpanDrawable.LinksTextView linksTextView2 = this.titleView[i5];
                    int i7 = Theme.key_dialogTextBlack;
                    linksTextView2.setTextColor(Theme.getColor(i7, resourcesProvider));
                    this.titleView[i5].setTextSize(1, 20.0f);
                    this.titleView[i5].setTypeface(AndroidUtilities.bold());
                    this.titleView[i5].setGravity(17);
                    this.layout[i5].addView(this.titleView[i5], LayoutHelper.createLinear(-1, -2, 17, 24, 0, 24, 0));
                    if (i5 == 0) {
                        LinkSpanDrawable.LinksTextView linksTextView3 = new LinkSpanDrawable.LinksTextView(context);
                        this.priceView = linksTextView3;
                        linksTextView3.setTextSize(1, 18.0f);
                        this.priceView.setTypeface(AndroidUtilities.bold());
                        this.priceView.setGravity(17);
                        this.priceView.setTextColor(Theme.getColor(Theme.key_color_green, resourcesProvider));
                        this.layout[i5].addView(this.priceView, LayoutHelper.createLinear(-1, -2, 17, 24, 0, 24, 4));
                    }
                    this.subtitleView[i5] = new LinkSpanDrawable.LinksTextView(context);
                    this.subtitleView[i5].setTextColor(Theme.getColor(i7, resourcesProvider));
                    this.subtitleView[i5].setTextSize(1, 14.0f);
                    this.subtitleView[i5].setGravity(17);
                    this.subtitleView[i5].setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                    this.subtitleView[i5].setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                    this.subtitleView[i5].setDisablePaddingsOffsetY(true);
                    LinearLayout linearLayout2 = this.layout[i5];
                    LinkSpanDrawable.LinksTextView linksTextView4 = this.subtitleView[i5];
                    LinearLayout.LayoutParams[] layoutParamsArr2 = this.subtitleViewLayoutParams;
                    LinearLayout.LayoutParams createLinear = LayoutHelper.createLinear(-1, -2, 17, 24, 0, 24, 0);
                    layoutParamsArr2[i5] = createLinear;
                    linearLayout2.addView(linksTextView4, createLinear);
                    this.subtitleViewLayoutParams[i5].topMargin = AndroidUtilities.dp(i5 == 1 ? 7.33f : this.backdrop[0] == null ? 9.0f : 5.66f);
                }
                if (i5 == 0) {
                    this.layout[i5].addView(this.buttonsLayout, LayoutHelper.createLinear(-1, -2, 7, 0, 15, 0, 0));
                }
                i5++;
            }
            addView(this.imageLayout, LayoutHelper.createFrame(160, 160.0f, 49, 0.0f, 8.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.closeView = imageView;
            imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(28.0f), 620756991));
            imageView.setImageResource(R.drawable.msg_close);
            ScaleStateListAnimator.apply(imageView);
            addView(imageView, LayoutHelper.createFrame(28, 28.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    runnable.run();
                }
            });
            imageView.setVisibility(8);
            ImageView imageView2 = new ImageView(context);
            this.optionsView = imageView2;
            imageView2.setImageResource(R.drawable.media_more);
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            imageView2.setBackground(Theme.createSelectorDrawable(553648127, 1));
            ScaleStateListAnimator.apply(imageView2);
            addView(imageView2, LayoutHelper.createFrame(42, 42.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
            imageView2.setOnClickListener(onClickListener);
            imageView2.setVisibility(8);
        }

        private void animateSwitch() {
            ValueAnimator valueAnimator = this.switchAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.switchAnimator = null;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.switchAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarGiftSheet.TopView.this.lambda$animateSwitch$3(valueAnimator2);
                }
            });
            this.switchAnimator.addListener(new AnimatorListenerAdapter() {
                AnonymousClass2() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    TopView.this.switchScale = 1.0f;
                    TopView.this.imageLayout.setScaleX(TopView.this.switchScale);
                    TopView.this.imageLayout.setScaleY(TopView.this.switchScale);
                    TopView.this.invalidate();
                }
            });
            this.switchAnimator.setDuration(320L);
            this.switchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.switchAnimator.start();
        }

        public void lambda$animateSwitch$3(ValueAnimator valueAnimator) {
            float pow = (((float) Math.pow((r5 * 2.0f) - 2.0f, 2.0d)) * 0.075f * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f;
            this.switchScale = pow;
            this.imageLayout.setScaleX(pow);
            this.imageLayout.setScaleY(this.switchScale);
            invalidate();
        }

        public void lambda$new$1() {
            if (this.imageView[2 - this.toggled].getImageReceiver().hasImageLoaded()) {
                rotateAttributes();
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
                AndroidUtilities.runOnUIThread(this.checkToRotateRunnable, 150L);
            }
        }

        public void lambda$rotateAttributes$2(ValueAnimator valueAnimator) {
            this.toggleBackdrop = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            onSwitchPage(this.currentPage);
        }

        private void rotateAttributes() {
            PageTransition pageTransition = this.currentPage;
            if (pageTransition != null && pageTransition.to == 1 && isAttachedToWindow()) {
                AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
                ValueAnimator valueAnimator = this.rotationAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.rotationAnimator = null;
                }
                int i = 1 - this.toggled;
                this.toggled = i;
                RLottieDrawable lottieAnimation = this.imageView[2 - i].getImageReceiver().getLottieAnimation();
                RLottieDrawable lottieAnimation2 = this.imageView[this.toggled + 1].getImageReceiver().getLottieAnimation();
                if (lottieAnimation2 != null && lottieAnimation != null) {
                    lottieAnimation2.setProgress(lottieAnimation.getProgress(), false);
                }
                this.models.next();
                int i2 = this.toggled + 1;
                TL_stars.starGiftAttributeBackdrop[] stargiftattributebackdropArr = this.backdrop;
                TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = (TL_stars.starGiftAttributeBackdrop) this.backdrops.next();
                stargiftattributebackdropArr[i2] = stargiftattributebackdrop;
                setBackdropPaint(i2, stargiftattributebackdrop);
                setPattern(1, (TL_stars.starGiftAttributePattern) this.patterns.next());
                animateSwitch();
                float f = this.toggled;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f - f, f);
                this.rotationAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        StarGiftSheet.TopView.this.lambda$rotateAttributes$2(valueAnimator2);
                    }
                });
                this.rotationAnimator.addListener(new AnimatorListenerAdapter() {
                    AnonymousClass1() {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        TopView.this.toggleBackdrop = r3.toggled;
                        TopView topView = TopView.this;
                        topView.onSwitchPage(topView.currentPage);
                        StarsIntroActivity.setGiftImage(TopView.this.imageView[2 - TopView.this.toggled].getImageReceiver(), ((TL_stars.starGiftAttributeModel) TopView.this.models.getNext()).document, 160);
                        AndroidUtilities.cancelRunOnUIThread(TopView.this.checkToRotateRunnable);
                        AndroidUtilities.runOnUIThread(TopView.this.checkToRotateRunnable, 2500L);
                    }
                });
                this.rotationAnimator.setDuration(320L);
                this.rotationAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.rotationAnimator.start();
            }
        }

        private void setBackdropPaint(int i, TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop) {
            if (stargiftattributebackdrop == null) {
                return;
            }
            RadialGradient[] radialGradientArr = this.backgroundGradient;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            radialGradientArr[i] = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dp(200.0f), new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, tileMode);
            if (i == 0) {
                LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(168.0f), new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, tileMode);
                this.profileBackgroundGradient = linearGradient;
                this.profileBackgroundPaint.setShader(linearGradient);
            }
            Matrix[] matrixArr = this.backgroundMatrix;
            if (matrixArr[i] == null) {
                matrixArr[i] = new Matrix();
            }
            this.backgroundPaint[i].setShader(this.backgroundGradient[i]);
        }

        private void setPattern(int i, TL_stars.starGiftAttributePattern stargiftattributepattern) {
            if (stargiftattributepattern == null) {
                return;
            }
            this.pattern[i].set(stargiftattributepattern.document, true);
        }

        private void updateWearImageTranslation() {
            this.wearImageScale = AndroidUtilities.dpf2(33.33f) / AndroidUtilities.dpf2(160.0f);
            this.wearImageTx = (((-this.imageLayout.getLeft()) + AndroidUtilities.dp(97.0f)) + Math.min(this.titleView[2].getPaint().measureText(this.titleView[2].getText().toString()) + AndroidUtilities.dp(12.0f), this.titleView[2].getWidth())) - (AndroidUtilities.dp(126.67f) / 2.0f);
            this.wearImageTy = ((-this.imageLayout.getTop()) + AndroidUtilities.dp(88.66f)) - (AndroidUtilities.dp(126.67f) / 2.0f);
        }

        @Override
        protected void dispatchDraw(android.graphics.Canvas r22) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.TopView.dispatchDraw(android.graphics.Canvas):void");
        }

        public float getRealHeight() {
            return ((AndroidUtilities.dp(this.backdrop[0] != null ? 24.0f : 10.0f) + AndroidUtilities.dp(160.0f) + this.layout[0].getMeasuredHeight()) * this.currentPage.at(0)) + 0.0f + ((AndroidUtilities.dp(this.backdrop[1] != null ? 24.0f : 10.0f) + AndroidUtilities.dp(160.0f) + this.layout[1].getMeasuredHeight()) * this.currentPage.at(1)) + ((AndroidUtilities.dp(64.0f) + this.layout[2].getMeasuredHeight()) * this.currentPage.at(2));
        }

        @Override
        protected void onAttachedToWindow() {
            this.attached = true;
            super.onAttachedToWindow();
            this.pattern[0].attach();
            this.pattern[1].attach();
        }

        @Override
        protected void onDetachedFromWindow() {
            this.attached = false;
            super.onDetachedFromWindow();
            this.pattern[0].detach();
            this.pattern[1].detach();
            AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (this.currentPage.contains(2)) {
                updateWearImageTranslation();
                onSwitchPage(this.currentPage);
            }
        }

        public void onSwitchPage(org.telegram.ui.Stars.StarGiftSheet.PageTransition r11) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.TopView.onSwitchPage(org.telegram.ui.Stars.StarGiftSheet$PageTransition):void");
        }

        public void prepareSwitchPage(PageTransition pageTransition) {
            int i = pageTransition.from;
            if (i != pageTransition.to) {
                RLottieDrawable lottieAnimation = this.imageView[i].getImageReceiver().getLottieAnimation();
                RLottieDrawable lottieAnimation2 = this.imageView[pageTransition.to].getImageReceiver().getLottieAnimation();
                if (lottieAnimation2 == null || lottieAnimation == null) {
                    return;
                }
                lottieAnimation2.setProgress(lottieAnimation.getProgress(), false);
            }
        }

        public void setGift(TL_stars.StarGift starGift, boolean z, boolean z2, boolean z3) {
            if (starGift instanceof TL_stars.TL_starGiftUnique) {
                this.backdrop[0] = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeBackdrop.class);
                setPattern(0, (TL_stars.starGiftAttributePattern) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributePattern.class));
                this.subtitleView[0].setTextSize(1, 13.0f);
                this.buttonsLayout.setVisibility(z ? 0 : 8);
                if (z) {
                    this.buttons[1].set(z2 ? R.drawable.filled_crown_off : R.drawable.filled_crown_on, LocaleController.getString(z2 ? R.string.Gift2ActionWearOff : R.string.Gift2ActionWear), false);
                }
            } else {
                this.backdrop[0] = null;
                setPattern(0, null);
                this.subtitleView[0].setTextSize(1, 14.0f);
                this.buttonsLayout.setVisibility(8);
            }
            this.hasLink = z3;
            setBackdropPaint(0, this.backdrop[0]);
            StarsIntroActivity.setGiftImage(this.imageView[0].getImageReceiver(), starGift, 160);
            onSwitchPage(this.currentPage);
        }

        public void setPreviewingAttributes(ArrayList<TL_stars.StarGiftAttribute> arrayList) {
            this.sampleAttributes = arrayList;
            this.models = new BagRandomizer(StarsController.findAttributes(arrayList, TL_stars.starGiftAttributeModel.class));
            this.patterns = new BagRandomizer(StarsController.findAttributes(arrayList, TL_stars.starGiftAttributePattern.class));
            this.backdrops = new BagRandomizer(StarsController.findAttributes(arrayList, TL_stars.starGiftAttributeBackdrop.class));
            this.subtitleView[1].setTextSize(1, 14.0f);
            this.buttonsLayout.setVisibility(8);
            this.toggleBackdrop = 0.0f;
            this.toggled = 0;
            setPattern(1, (TL_stars.starGiftAttributePattern) this.patterns.next());
            StarsIntroActivity.setGiftImage(this.imageView[1].getImageReceiver(), ((TL_stars.starGiftAttributeModel) this.models.next()).document, 160);
            TL_stars.starGiftAttributeBackdrop[] stargiftattributebackdropArr = this.backdrop;
            TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = (TL_stars.starGiftAttributeBackdrop) this.backdrops.next();
            stargiftattributebackdropArr[1] = stargiftattributebackdrop;
            setBackdropPaint(1, stargiftattributebackdrop);
            StarsIntroActivity.setGiftImage(this.imageView[2].getImageReceiver(), ((TL_stars.starGiftAttributeModel) this.models.getNext()).document, 160);
            AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
            AndroidUtilities.runOnUIThread(this.checkToRotateRunnable, 2500L);
            invalidate();
        }

        public void setText(int i, CharSequence charSequence, long j, CharSequence charSequence2) {
            this.titleView[i].setText(charSequence);
            if (i == 0) {
                this.priceView.setTextColor(Theme.getColor(Theme.key_color_green, this.resourcesProvider));
                this.priceView.setText(StarsIntroActivity.replaceStarsWithPlain(LocaleController.formatNumber((int) j, ' ') + " ⭐️", 0.8f));
                this.priceView.setVisibility(j != 0 ? 0 : 8);
            }
            this.subtitleView[i].setText(charSequence2);
            this.subtitleView[i].setVisibility(TextUtils.isEmpty(charSequence2) ? 8 : 0);
        }

        public void setWearPreview(TLObject tLObject) {
            String lowerCase;
            String str;
            String str2;
            String str3;
            this.wearPreviewObject = tLObject;
            if (tLObject instanceof TLRPC.User) {
                str3 = UserObject.getUserName((TLRPC.User) tLObject);
                str2 = LocaleController.getString(R.string.Online);
            } else {
                if (!(tLObject instanceof TLRPC.Chat)) {
                    return;
                }
                TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                String str4 = chat == null ? "" : chat.title;
                boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
                int i = chat.participants_count;
                if (isChannelAndNotMegaGroup) {
                    if (i > 1) {
                        str = "Subscribers";
                        lowerCase = LocaleController.formatPluralStringComma(str, i);
                        String str5 = str4;
                        str2 = lowerCase;
                        str3 = str5;
                    } else {
                        lowerCase = LocaleController.getString(R.string.DiscussChannel);
                        String str52 = str4;
                        str2 = lowerCase;
                        str3 = str52;
                    }
                } else if (i > 1) {
                    str = "Members";
                    lowerCase = LocaleController.formatPluralStringComma(str, i);
                    String str522 = str4;
                    str2 = lowerCase;
                    str3 = str522;
                } else {
                    lowerCase = LocaleController.getString(R.string.AccDescrGroup).toLowerCase();
                    String str5222 = str4;
                    str2 = lowerCase;
                    str3 = str5222;
                }
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(tLObject);
            this.avatarView.setForUserOrChat(tLObject, avatarDrawable);
            this.titleView[2].setText(str3);
            this.subtitleView[2].setText(str2);
            updateWearImageTranslation();
            onSwitchPage(this.currentPage);
        }
    }

    public static class UpgradeIcon extends CompatDrawable {
        private final Path arrow;
        private final long start;
        private final Paint strokePaint;
        private final View view;

        public UpgradeIcon(View view, int i) {
            super(view);
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            Path path = new Path();
            this.arrow = path;
            this.start = System.currentTimeMillis();
            this.view = view;
            this.paint.setColor(-1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(i);
            path.rewind();
            path.moveTo(-AndroidUtilities.dpf2(2.91f), AndroidUtilities.dpf2(1.08f));
            path.lineTo(0.0f, -AndroidUtilities.dpf2(1.08f));
            path.lineTo(AndroidUtilities.dpf2(2.91f), AndroidUtilities.dpf2(1.08f));
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), getBounds().width() / 2.0f, this.paint);
            float currentTimeMillis = ((float) ((System.currentTimeMillis() - this.start) % 400)) / 400.0f;
            this.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.33f));
            canvas.save();
            canvas.translate(getBounds().centerX(), getBounds().centerY() - (((AndroidUtilities.dpf2(2.16f) * 3.0f) + (AndroidUtilities.dpf2(1.166f) * 2.0f)) / 2.0f));
            int i = 0;
            while (i < 4) {
                float f = i == 0 ? 1.0f - currentTimeMillis : i == 3 ? currentTimeMillis : 1.0f;
                this.strokePaint.setAlpha((int) (255.0f * f));
                canvas.save();
                float lerp = AndroidUtilities.lerp(0.5f, 1.0f, f);
                canvas.scale(lerp, lerp);
                canvas.drawPath(this.arrow, this.strokePaint);
                canvas.restore();
                canvas.translate(0.0f, AndroidUtilities.dpf2(3.3260002f) * f);
                i++;
            }
            canvas.restore();
            View view = this.view;
            if (view != null) {
                view.invalidate();
            }
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(18.0f);
        }
    }

    public StarGiftSheet(Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.currentPage = new PageTransition(0, 0, 1.0f);
        this.firstSet = true;
        this.currentAccount = i;
        this.dialogId = j;
        ContainerView containerView = new ContainerView(context);
        this.container = containerView;
        this.containerView = containerView;
        fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
        LinearLayout linearLayout = new LinearLayout(context);
        this.infoLayout = linearLayout;
        linearLayout.setOrientation(1);
        linearLayout.setPadding(this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f), this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(56.0f));
        this.containerView.addView(linearLayout, LayoutHelper.createFrame(-1, -1, 87));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.beforeTableTextView = linksTextView;
        int i2 = Theme.key_dialogTextGray2;
        linksTextView.setTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setGravity(17);
        linksTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        int i3 = Theme.key_chat_messageLinkIn;
        linksTextView.setLinkTextColor(Theme.getColor(i3, resourcesProvider));
        linksTextView.setDisablePaddingsOffsetY(true);
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-2, -2, 1, 5, -4, 5, 10));
        linksTextView.setVisibility(8);
        TableView tableView = new TableView(context, resourcesProvider);
        this.tableView = tableView;
        linearLayout.addView(tableView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
        LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.afterTableTextView = linksTextView2;
        linksTextView2.setTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView2.setTextSize(1, 14.0f);
        linksTextView2.setGravity(17);
        linksTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linksTextView2.setLinkTextColor(Theme.getColor(i3, resourcesProvider));
        linksTextView2.setDisablePaddingsOffsetY(true);
        linksTextView2.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
        linearLayout.addView(linksTextView2, LayoutHelper.createLinear(-2, -2, 1, 5, 6, 5, 16));
        linksTextView2.setVisibility(8);
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.button = buttonWithCounterView;
        buttonWithCounterView.setText(LocaleController.getString(R.string.OK), false);
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(-1, 48.0f, 87, 0.0f, 0.0f, 0.0f, 4.0f);
        createFrame.leftMargin = this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f);
        createFrame.rightMargin = this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f);
        this.containerView.addView(buttonWithCounterView, createFrame);
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.upgradeLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        linearLayout2.setPadding(AndroidUtilities.dp(4.0f) + this.backgroundPaddingLeft, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(4.0f) + this.backgroundPaddingLeft, AndroidUtilities.dp(56.0f));
        this.containerView.addView(linearLayout2, LayoutHelper.createFrame(-1, -1, 87));
        this.upgradeFeatureCells = r13;
        AffiliateProgramFragment.FeatureCell featureCell = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        int i4 = R.drawable.menu_feature_unique;
        featureCell.set(i4, LocaleController.getString(R.string.Gift2UpgradeFeature1Title), LocaleController.getString(R.string.Gift2UpgradeFeature1Text));
        linearLayout2.addView(r13[0], LayoutHelper.createLinear(-1, -2));
        AffiliateProgramFragment.FeatureCell featureCell2 = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        featureCell2.set(R.drawable.menu_feature_transfer, LocaleController.getString(R.string.Gift2UpgradeFeature2Title), LocaleController.getString(R.string.Gift2UpgradeFeature2Text));
        linearLayout2.addView(r13[1], LayoutHelper.createLinear(-1, -2));
        AffiliateProgramFragment.FeatureCell featureCell3 = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        AffiliateProgramFragment.FeatureCell[] featureCellArr = {featureCell, featureCell2, featureCell3};
        featureCell3.set(R.drawable.menu_feature_tradable, LocaleController.getString(R.string.Gift2UpgradeFeature3Title), LocaleController.getString(R.string.Gift2UpgradeFeature3Text));
        linearLayout2.addView(featureCellArr[2], LayoutHelper.createLinear(-1, -2));
        View view = new View(context);
        this.checkboxSeparator = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
        linearLayout2.addView(view, LayoutHelper.createLinear(-2, 1.0f / AndroidUtilities.density, 7, 17, -4, 17, 6));
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.checkboxLayout = linearLayout3;
        linearLayout3.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f));
        linearLayout3.setOrientation(0);
        linearLayout3.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 6, 6));
        CheckBox2 checkBox2 = new CheckBox2(context, 24, resourcesProvider);
        this.checkbox = checkBox2;
        checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(false, false);
        checkBox2.setDrawBackgroundAsArc(10);
        linearLayout3.addView(checkBox2, LayoutHelper.createLinear(26, 26, 16, 0, 0, 0, 0));
        TextView textView = new TextView(context);
        this.checkboxTextView = textView;
        int i5 = Theme.key_dialogTextBlack;
        textView.setTextColor(getThemedColor(i5));
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString(R.string.Gift2AddSenderName));
        linearLayout3.addView(textView, LayoutHelper.createLinear(-2, -2, 16, 9, 0, 0, 0));
        linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 4));
        ScaleStateListAnimator.apply(linearLayout3, 0.025f, 1.5f);
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.lambda$new$0(view2);
            }
        });
        LinearLayout linearLayout4 = new LinearLayout(context);
        this.wearLayout = linearLayout4;
        linearLayout4.setOrientation(1);
        linearLayout4.setPadding(AndroidUtilities.dp(4.0f) + this.backgroundPaddingLeft, AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f) + this.backgroundPaddingLeft, AndroidUtilities.dp(56.0f));
        this.containerView.addView(linearLayout4, LayoutHelper.createFrame(-1, -1, 87));
        TextView textView2 = new TextView(context);
        this.wearTitle = textView2;
        textView2.setTextColor(Theme.getColor(i5, resourcesProvider));
        textView2.setTextSize(1, 20.0f);
        textView2.setGravity(17);
        textView2.setTypeface(AndroidUtilities.bold());
        linearLayout4.addView(textView2, LayoutHelper.createLinear(-1, -2, 7, 20, 0, 20, 0));
        TextView textView3 = new TextView(context);
        this.wearSubtitle = textView3;
        textView3.setTextColor(Theme.getColor(i5, resourcesProvider));
        textView3.setTextSize(1, 14.0f);
        textView3.setGravity(17);
        textView3.setText(LocaleController.getString(R.string.Gift2WearSubtitle));
        linearLayout4.addView(textView3, LayoutHelper.createLinear(-1, -2, 7, 20, 6, 20, 24));
        this.wearFeatureCells = r2;
        AffiliateProgramFragment.FeatureCell featureCell4 = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        featureCell4.set(i4, LocaleController.getString(R.string.Gift2WearFeature1Title), LocaleController.getString(R.string.Gift2WearFeature1Text));
        linearLayout4.addView(r2[0], LayoutHelper.createLinear(-1, -2));
        AffiliateProgramFragment.FeatureCell featureCell5 = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        featureCell5.set(R.drawable.menu_feature_cover, LocaleController.getString(R.string.Gift2WearFeature2Title), LocaleController.getString(R.string.Gift2WearFeature2Text));
        linearLayout4.addView(r2[1], LayoutHelper.createLinear(-1, -2));
        AffiliateProgramFragment.FeatureCell featureCell6 = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        AffiliateProgramFragment.FeatureCell[] featureCellArr2 = {featureCell4, featureCell5, featureCell6};
        featureCell6.set(R.drawable.menu_verification, LocaleController.getString(R.string.Gift2WearFeature3Title), LocaleController.getString(R.string.Gift2WearFeature3Text));
        linearLayout4.addView(featureCellArr2[2], LayoutHelper.createLinear(-1, -2));
        linearLayout.setAlpha(1.0f);
        linearLayout2.setAlpha(0.0f);
        linearLayout4.setAlpha(0.0f);
        TopView topView = new TopView(context, resourcesProvider, new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.onBackPressed();
            }
        }, new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.onMenuPressed(view2);
            }
        }, new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.lambda$new$1(view2);
            }
        }, new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.onWearPressed(view2);
            }
        }, new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.onSharePressed(view2);
            }
        });
        this.topView = topView;
        int i6 = this.backgroundPaddingLeft;
        topView.setPadding(i6, 0, i6, 0);
        this.containerView.addView(topView, LayoutHelper.createFrame(-1, -2, 87));
        FireworksOverlay fireworksOverlay = new FireworksOverlay(context);
        this.fireworksOverlay = fireworksOverlay;
        this.container.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.bottomBulletinContainer = frameLayout;
        frameLayout.setPadding(this.backgroundPaddingLeft + AndroidUtilities.dp(6.0f), 0, this.backgroundPaddingLeft + AndroidUtilities.dp(6.0f), 0);
        this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, 200.0f, 87, 0.0f, 0.0f, 0.0f, 52.0f));
    }

    private void addAttributeRow(final TL_stars.StarGiftAttribute starGiftAttribute) {
        int i;
        if (starGiftAttribute instanceof TL_stars.starGiftAttributeModel) {
            i = R.string.Gift2AttributeModel;
        } else if (starGiftAttribute instanceof TL_stars.starGiftAttributeBackdrop) {
            i = R.string.Gift2AttributeBackdrop;
        } else if (!(starGiftAttribute instanceof TL_stars.starGiftAttributePattern)) {
            return;
        } else {
            i = R.string.Gift2AttributeSymbol;
        }
        final ButtonSpan.TextViewButtons[] textViewButtonsArr = {(ButtonSpan.TextViewButtons) ((TableView.TableRowContent) this.tableView.addRow(LocaleController.getString(i), starGiftAttribute.name, AffiliateProgramFragment.percents(starGiftAttribute.rarity_permille), new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$addAttributeRow$15(starGiftAttribute, textViewButtonsArr);
            }
        }).getChildAt(1)).getChildAt(0)};
    }

    private boolean applyNewGiftFromUpdates(TLRPC.Updates updates) {
        TLRPC.TL_updateNewMessage tL_updateNewMessage;
        if (updates == null) {
            return false;
        }
        TLRPC.Update update = updates.update;
        if (update instanceof TLRPC.TL_updateNewMessage) {
            tL_updateNewMessage = (TLRPC.TL_updateNewMessage) update;
        } else {
            if (updates.updates != null) {
                for (int i = 0; i < updates.updates.size(); i++) {
                    TLRPC.Update update2 = updates.updates.get(i);
                    if (update2 instanceof TLRPC.TL_updateNewMessage) {
                        tL_updateNewMessage = (TLRPC.TL_updateNewMessage) update2;
                        break;
                    }
                }
            }
            tL_updateNewMessage = null;
        }
        if (tL_updateNewMessage == null) {
            return false;
        }
        this.savedStarGift = null;
        this.myProfile = false;
        MessageObject messageObject = new MessageObject(this.currentAccount, tL_updateNewMessage.message, false, false);
        messageObject.setType();
        set(messageObject);
        return true;
    }

    private boolean canConvert() {
        TLRPC.Peer peer;
        if (getInputStarGift() == null) {
            return false;
        }
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                return false;
            }
            TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
            return (!messageObject.isOutOwner() || ((this.messageObject.getDialogId() > UserConfig.getInstance(this.currentAccount).getClientUserId() ? 1 : (this.messageObject.getDialogId() == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : -1)) == 0) || ((peer = tL_messageActionStarGift.peer) != null && isMineWithActions(this.currentAccount, DialogObject.getPeerDialogId(peer)))) && !tL_messageActionStarGift.converted && tL_messageActionStarGift.convert_stars > 0 && MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.messageObject.messageOwner.date) > 0;
        }
        TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
        if (savedStarGift == null) {
            return false;
        }
        int currentTime = MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - savedStarGift.date);
        if (!isMineWithActions(this.currentAccount, this.dialogId)) {
            return false;
        }
        int i = this.savedStarGift.flags;
        return ((((this.dialogId > 0L ? 1 : (this.dialogId == 0L ? 0 : -1)) < 0 ? 2048 : 8) & i) == 0 || (i & 16) == 0 || (i & 2) == 0 || currentTime <= 0) ? false : true;
    }

    public void convert() {
        int i;
        long peerDialogId;
        long j;
        long j2;
        final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        final TL_stars.InputSavedStarGift inputStarGift = getInputStarGift();
        if (inputStarGift == null) {
            return;
        }
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            i = messageObject.messageOwner.date;
            boolean isOutOwner = messageObject.isOutOwner();
            MessageObject messageObject2 = this.messageObject;
            TLRPC.Message message = messageObject2.messageOwner;
            if (message == null) {
                return;
            }
            TLRPC.MessageAction messageAction = message.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                return;
            }
            TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
            TLRPC.Peer peer = tL_messageActionStarGift.peer;
            j2 = peer != null ? DialogObject.getPeerDialogId(peer) : isOutOwner ? messageObject2.getDialogId() : clientUserId;
            TLRPC.Peer peer2 = tL_messageActionStarGift.from_id;
            peerDialogId = peer2 != null ? DialogObject.getPeerDialogId(peer2) : isOutOwner ? clientUserId : this.messageObject.getDialogId();
            j = tL_messageActionStarGift.convert_stars;
        } else {
            TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
            if (savedStarGift == null) {
                return;
            }
            i = savedStarGift.date;
            peerDialogId = ((savedStarGift.flags & 2) == 0 || savedStarGift.name_hidden) ? 2666000L : DialogObject.getPeerDialogId(savedStarGift.from_id);
            j = this.savedStarGift.convert_stars;
            j2 = this.dialogId;
        }
        final long j3 = j;
        final long j4 = j2;
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ConvertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("Gift2ConvertText2", Math.max(1, (MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - i)) / 86400), UserObject.isService(peerDialogId) ? LocaleController.getString(R.string.StarsTransactionHidden) : DialogObject.getShortName(peerDialogId), LocaleController.formatPluralStringComma("Gift2ConvertStars", (int) j3)))).setPositiveButton(LocaleController.getString(R.string.Gift2ConvertButton), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                StarGiftSheet.this.lambda$convert$42(inputStarGift, j4, clientUserId, j3, alertDialog, i2);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    private void doTransfer(final long j, final Utilities.Callback callback) {
        TLRPC.Message message;
        final long peerDialogId;
        long j2;
        TL_stars.InputSavedStarGift inputStarGift = getInputStarGift();
        if (inputStarGift == null) {
            return;
        }
        TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
        if (savedStarGift == null || !(savedStarGift.gift instanceof TL_stars.TL_starGiftUnique)) {
            MessageObject messageObject = this.messageObject;
            if (messageObject == null || (message = messageObject.messageOwner) == null) {
                return;
            }
            TLRPC.MessageAction messageAction = message.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                return;
            }
            TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = (TLRPC.TL_messageActionStarGiftUnique) messageAction;
            peerDialogId = DialogObject.getPeerDialogId(tL_messageActionStarGiftUnique.gift.owner_id);
            j2 = tL_messageActionStarGiftUnique.transfer_stars;
        } else {
            peerDialogId = this.dialogId;
            j2 = savedStarGift.transfer_stars;
        }
        if (j2 <= 0) {
            TL_stars.transferStarGift transferstargift = new TL_stars.transferStarGift();
            transferstargift.stargift = inputStarGift;
            transferstargift.to_id = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(transferstargift, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$doTransfer$83(callback, j, peerDialogId, tLObject, tL_error);
                }
            });
            return;
        }
        final StarsController starsController = StarsController.getInstance(this.currentAccount);
        if (!starsController.balanceAvailable()) {
            starsController.getBalance(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$doTransfer$84(starsController, j, callback);
                }
            });
            return;
        }
        final TLRPC.TL_inputInvoiceStarGiftTransfer tL_inputInvoiceStarGiftTransfer = new TLRPC.TL_inputInvoiceStarGiftTransfer();
        tL_inputInvoiceStarGiftTransfer.stargift = inputStarGift;
        tL_inputInvoiceStarGiftTransfer.to_id = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
        tL_payments_getPaymentForm.invoice = tL_inputInvoiceStarGiftTransfer;
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(this.resourcesProvider);
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_payments_getPaymentForm.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_payments_getPaymentForm.flags |= 1;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$doTransfer$93(tL_inputInvoiceStarGiftTransfer, j, peerDialogId, callback, tLObject, tL_error);
            }
        });
    }

    private void doUpgrade() {
        TL_stars.InputSavedStarGift inputStarGift;
        final long j;
        if (this.button.isLoading() || (inputStarGift = getInputStarGift()) == null) {
            return;
        }
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                return;
            } else {
                j = ((TLRPC.TL_messageActionStarGift) messageAction).upgrade_stars;
            }
        } else {
            TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
            if (savedStarGift == null) {
                return;
            } else {
                j = savedStarGift.upgrade_stars;
            }
        }
        if (j > 0 || this.upgrade_form != null) {
            this.button.setLoading(true);
            if (j > 0) {
                TL_stars.upgradeStarGift upgradestargift = new TL_stars.upgradeStarGift();
                upgradestargift.keep_original_details = this.checkbox.isChecked();
                upgradestargift.stargift = inputStarGift;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(upgradestargift, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StarGiftSheet.this.lambda$doUpgrade$58(tLObject, tL_error);
                    }
                });
                return;
            }
            final StarsController starsController = StarsController.getInstance(this.currentAccount);
            if (!starsController.balanceAvailable()) {
                starsController.getBalance(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doUpgrade$59(starsController);
                    }
                });
                return;
            }
            TLRPC.TL_inputInvoiceStarGiftUpgrade tL_inputInvoiceStarGiftUpgrade = new TLRPC.TL_inputInvoiceStarGiftUpgrade();
            tL_inputInvoiceStarGiftUpgrade.keep_original_details = this.checkbox.isChecked();
            tL_inputInvoiceStarGiftUpgrade.stargift = inputStarGift;
            TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
            tL_payments_sendStarsForm.form_id = this.upgrade_form.form_id;
            tL_payments_sendStarsForm.invoice = tL_inputInvoiceStarGiftUpgrade;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$doUpgrade$65(j, tLObject, tL_error);
                }
            });
        }
    }

    private long getDialogId() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.getDialogId():long");
    }

    private BaseFragment getDummyFragment() {
        return new BaseFragment() {
            AnonymousClass1() {
            }

            @Override
            public Context getContext() {
                return StarGiftSheet.this.getContext();
            }

            @Override
            public int getCurrentAccount() {
                return this.currentAccount;
            }

            @Override
            public Activity getParentActivity() {
                for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                    if (context instanceof Activity) {
                        return (Activity) context;
                    }
                }
                return null;
            }

            @Override
            public Dialog showDialog(Dialog dialog) {
                dialog.show();
                return dialog;
            }
        };
    }

    private TL_stars.InputSavedStarGift getInputStarGift() {
        int i;
        TLRPC.Message message;
        TLRPC.Message message2;
        long j;
        TLRPC.Message message3;
        if (this.dialogId < 0) {
            TL_stars.TL_inputSavedStarGiftChat tL_inputSavedStarGiftChat = new TL_stars.TL_inputSavedStarGiftChat();
            tL_inputSavedStarGiftChat.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            MessageObject messageObject = this.messageObject;
            if (messageObject == null || (message3 = messageObject.messageOwner) == null) {
                TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
                if (savedStarGift == null || (savedStarGift.flags & 2048) == 0) {
                    return null;
                }
                j = savedStarGift.saved_id;
            } else {
                TLRPC.MessageAction messageAction = message3.action;
                if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                    TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                    if ((tL_messageActionStarGift.flags & 4096) == 0) {
                        return null;
                    }
                    j = tL_messageActionStarGift.saved_id;
                } else {
                    if (!(messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                        return null;
                    }
                    TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = (TLRPC.TL_messageActionStarGiftUnique) messageAction;
                    if ((tL_messageActionStarGiftUnique.flags & 128) == 0) {
                        return null;
                    }
                    j = tL_messageActionStarGiftUnique.saved_id;
                }
            }
            tL_inputSavedStarGiftChat.saved_id = j;
            return tL_inputSavedStarGiftChat;
        }
        MessageObject messageObject2 = this.messageObject;
        if (messageObject2 != null && (message2 = messageObject2.messageOwner) != null) {
            TLRPC.MessageAction messageAction2 = message2.action;
            if ((messageAction2 instanceof TLRPC.TL_messageActionStarGift) && (messageAction2.flags & 4096) != 0) {
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift2 = (TLRPC.TL_messageActionStarGift) messageAction2;
                TL_stars.TL_inputSavedStarGiftChat tL_inputSavedStarGiftChat2 = new TL_stars.TL_inputSavedStarGiftChat();
                tL_inputSavedStarGiftChat2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(tL_messageActionStarGift2.peer);
                tL_inputSavedStarGiftChat2.saved_id = tL_messageActionStarGift2.saved_id;
                return tL_inputSavedStarGiftChat2;
            }
        }
        if (messageObject2 != null && (message = messageObject2.messageOwner) != null) {
            TLRPC.MessageAction messageAction3 = message.action;
            if ((messageAction3 instanceof TLRPC.TL_messageActionStarGiftUnique) && (messageAction3.flags & 128) != 0) {
                TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique2 = (TLRPC.TL_messageActionStarGiftUnique) messageAction3;
                TL_stars.TL_inputSavedStarGiftChat tL_inputSavedStarGiftChat3 = new TL_stars.TL_inputSavedStarGiftChat();
                tL_inputSavedStarGiftChat3.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(tL_messageActionStarGiftUnique2.peer);
                tL_inputSavedStarGiftChat3.saved_id = tL_messageActionStarGiftUnique2.saved_id;
                return tL_inputSavedStarGiftChat3;
            }
        }
        TL_stars.TL_inputSavedStarGiftUser tL_inputSavedStarGiftUser = new TL_stars.TL_inputSavedStarGiftUser();
        MessageObject messageObject3 = this.messageObject;
        if (messageObject3 != null) {
            i = messageObject3.getId();
        } else {
            TL_stars.SavedStarGift savedStarGift2 = this.savedStarGift;
            if (savedStarGift2 == null) {
                return null;
            }
            i = savedStarGift2.msg_id;
        }
        tL_inputSavedStarGiftUser.msg_id = i;
        return tL_inputSavedStarGiftUser;
    }

    private String getLink() {
        TL_stars.StarGift gift = getGift();
        if (!(gift instanceof TL_stars.TL_starGiftUnique) || gift.slug == null) {
            return null;
        }
        return MessagesController.getInstance(this.currentAccount).linkPrefix + "/nft/" + gift.slug;
    }

    public void lambda$openTransfer$66(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, final TwoStepVerificationActivity twoStepVerificationActivity) {
        TL_stars.getStarGiftWithdrawalUrl getstargiftwithdrawalurl = new TL_stars.getStarGiftWithdrawalUrl();
        TL_stars.InputSavedStarGift inputStarGift = getInputStarGift();
        getstargiftwithdrawalurl.stargift = inputStarGift;
        if (inputStarGift == null) {
            return;
        }
        getstargiftwithdrawalurl.password = inputCheckPasswordSRP;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getstargiftwithdrawalurl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$initTONTransfer$80(twoStepVerificationActivity, tLObject, tL_error);
            }
        });
    }

    public static boolean isMine(int i, long j) {
        return j >= 0 ? UserConfig.getInstance(i).getClientUserId() == j : ChatObject.canUserDoAction(MessagesController.getInstance(i).getChat(Long.valueOf(-j)), 5);
    }

    public static boolean isMineWithActions(int i, long j) {
        if (j >= 0) {
            return UserConfig.getInstance(i).getClientUserId() == j;
        }
        TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
        return chat != null && chat.creator;
    }

    public void lambda$addAttributeRow$15(TL_stars.StarGiftAttribute starGiftAttribute, ButtonSpan.TextViewButtons[] textViewButtonsArr) {
        showHint(LocaleController.formatString(R.string.Gift2RarityHint, AffiliateProgramFragment.percents(starGiftAttribute.rarity_permille)), textViewButtonsArr[0], false);
    }

    public static void lambda$convert$38(StarsIntroActivity starsIntroActivity, long j) {
        BulletinFactory.of(starsIntroActivity).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) j)).show(true);
    }

    public static void lambda$convert$39(StatisticActivity statisticActivity, long j) {
        BulletinFactory.of(statisticActivity).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2ConvertedChannel", (int) j)).show(true);
    }

    public void lambda$convert$40(AlertDialog alertDialog, TLObject tLObject, long j, long j2, final long j3, TLRPC.TL_error tL_error) {
        alertDialog.dismissUnless(400L);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            getBulletinFactory().createErrorBulletin(tL_error != null ? LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text) : LocaleController.getString(R.string.UnknownError)).show(false);
            return;
        }
        lambda$new$0();
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j);
        if (j < 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", -j);
            bundle.putBoolean("start_from_monetization", true);
            final StatisticActivity statisticActivity = new StatisticActivity(bundle);
            BotStarsController.getInstance(this.currentAccount).invalidateStarsBalance(j);
            BotStarsController.getInstance(this.currentAccount).invalidateTransactions(j, true);
            statisticActivity.whenFullyVisible(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.lambda$convert$39(StatisticActivity.this, j3);
                }
            });
            safeLastFragment.presentFragment(statisticActivity);
            return;
        }
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(j2);
        if (userFull != null) {
            int max = Math.max(0, userFull.stargifts_count - 1);
            userFull.stargifts_count = max;
            if (max <= 0) {
                userFull.flags2 &= -257;
            }
        }
        StarsController.getInstance(this.currentAccount).invalidateBalance();
        StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
        if (safeLastFragment instanceof StarsIntroActivity) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) j3)).show(true);
            return;
        }
        final StarsIntroActivity starsIntroActivity = new StarsIntroActivity();
        starsIntroActivity.whenFullyVisible(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.lambda$convert$38(StarsIntroActivity.this, j3);
            }
        });
        safeLastFragment.presentFragment(starsIntroActivity);
    }

    public void lambda$convert$41(final AlertDialog alertDialog, final long j, final long j2, final long j3, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$convert$40(alertDialog, tLObject, j, j2, j3, tL_error);
            }
        });
    }

    public void lambda$convert$42(TL_stars.InputSavedStarGift inputSavedStarGift, final long j, final long j2, final long j3, AlertDialog alertDialog, int i) {
        final AlertDialog alertDialog2 = new AlertDialog(ApplicationLoader.applicationContext, 3);
        alertDialog2.showDelayed(500L);
        TL_stars.convertStarGift convertstargift = new TL_stars.convertStarGift();
        convertstargift.stargift = inputSavedStarGift;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(convertstargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$convert$41(alertDialog2, j, j2, j3, tLObject, tL_error);
            }
        });
    }

    public void lambda$doTransfer$81(ChatActivity chatActivity, long j) {
        BulletinFactory.of(chatActivity).createSimpleBulletin(R.raw.forward, LocaleController.getString(R.string.Gift2TransferredTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2TransferredText, getGiftName(), DialogObject.getShortName(j)))).show();
    }

    public void lambda$doTransfer$82(Utilities.Callback callback, TLRPC.TL_error tL_error, TLObject tLObject, final long j, long j2) {
        if (callback != null) {
            callback.run(tL_error);
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            if (!(tLObject instanceof TLRPC.Updates)) {
                BulletinFactory.of(safeLastFragment).showForError(tL_error);
            } else if (j < 0 || j2 < 0) {
                BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.forward, LocaleController.getString(R.string.Gift2TransferredTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2TransferredText, getGiftName(), DialogObject.getShortName(j)))).show();
            } else {
                final ChatActivity of = ChatActivity.of(j);
                of.whenFullyVisible(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doTransfer$81(of, j);
                    }
                });
                safeLastFragment.presentFragment(of);
            }
        }
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j2);
    }

    public void lambda$doTransfer$83(final Utilities.Callback callback, final long j, final long j2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.Updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$82(callback, tL_error, tLObject, j, j2);
            }
        });
    }

    public void lambda$doTransfer$84(StarsController starsController, long j, Utilities.Callback callback) {
        if (starsController.balanceAvailable()) {
            doTransfer(j, callback);
        } else {
            getBulletinFactory().createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, "NO_BALANCE")).show();
        }
    }

    public void lambda$doTransfer$85(ChatActivity chatActivity, long j) {
        BulletinFactory.of(chatActivity).createSimpleBulletin(R.raw.forward, LocaleController.getString(R.string.Gift2TransferredTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2TransferredText, getGiftName(), DialogObject.getShortName(j)))).show();
    }

    public void lambda$doTransfer$86(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public void lambda$doTransfer$87(boolean[] zArr, long j, Utilities.Callback callback) {
        zArr[0] = true;
        this.button.setLoading(false);
        doTransfer(j, callback);
    }

    public void lambda$doTransfer$88(DialogInterface dialogInterface) {
        this.button.setLoading(false);
    }

    public void lambda$doTransfer$89(long j, final long j2, final Utilities.Callback callback) {
        final boolean[] zArr = {false};
        StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(getContext(), this.resourcesProvider, j, 11, null, new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$87(zArr, j2, callback);
            }
        });
        starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                StarGiftSheet.this.lambda$doTransfer$88(dialogInterface);
            }
        });
        starsNeededSheet.show();
    }

    public void lambda$doTransfer$90(TLObject tLObject, final long j, long j2, final Utilities.Callback callback, TLRPC.TL_error tL_error, final long j3) {
        if (!(tLObject instanceof TLRPC.TL_payments_paymentResult)) {
            if (tL_error == null || !"BALANCE_TOO_LOW".equals(tL_error.text)) {
                if (callback != null) {
                    callback.run(tL_error);
                }
                getBulletinFactory().showForError(tL_error);
                return;
            } else if (MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                StarsController.getInstance(this.currentAccount).invalidateBalance(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doTransfer$89(j3, j, callback);
                    }
                });
                return;
            } else {
                this.button.setLoading(false);
                StarsController.showNoSupportDialog(getContext(), this.resourcesProvider);
                return;
            }
        }
        final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_paymentResult.updates.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_payments_paymentResult.updates.chats, false);
        StarsController.getInstance(this.currentAccount).invalidateTransactions(false);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j2);
        StarsController.getInstance(this.currentAccount).invalidateBalance();
        if (callback != null) {
            callback.run(null);
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            if (j < 0 || j2 < 0) {
                BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.forward, LocaleController.getString(R.string.Gift2TransferredTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2TransferredText, getGiftName(), DialogObject.getShortName(j)))).show();
            } else {
                final ChatActivity of = ChatActivity.of(j);
                of.whenFullyVisible(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doTransfer$85(of, j);
                    }
                });
                safeLastFragment.presentFragment(of);
            }
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$86(tL_payments_paymentResult);
            }
        });
    }

    public void lambda$doTransfer$91(final long j, final long j2, final Utilities.Callback callback, final long j3, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$90(tLObject, j, j2, callback, tL_error, j3);
            }
        });
    }

    public void lambda$doTransfer$92(TLObject tLObject, TLRPC.TL_inputInvoiceStarGiftTransfer tL_inputInvoiceStarGiftTransfer, final long j, final long j2, final Utilities.Callback callback, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.PaymentForm)) {
            if (callback != null) {
                callback.run(tL_error);
            }
            getBulletinFactory().makeForError(tL_error).show();
            return;
        }
        TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
        TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
        tL_payments_sendStarsForm.form_id = paymentForm.form_id;
        tL_payments_sendStarsForm.invoice = tL_inputInvoiceStarGiftTransfer;
        Iterator<TLRPC.TL_labeledPrice> it = paymentForm.invoice.prices.iterator();
        final long j3 = 0;
        while (it.hasNext()) {
            j3 += it.next().amount;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                StarGiftSheet.this.lambda$doTransfer$91(j, j2, callback, j3, tLObject2, tL_error2);
            }
        });
    }

    public void lambda$doTransfer$93(final TLRPC.TL_inputInvoiceStarGiftTransfer tL_inputInvoiceStarGiftTransfer, final long j, final long j2, final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$92(tLObject, tL_inputInvoiceStarGiftTransfer, j, j2, callback, tL_error);
            }
        });
    }

    public void lambda$doUpgrade$56(TLObject tLObject) {
        MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
    }

    public void lambda$doUpgrade$57(TLRPC.TL_error tL_error, final TLObject tLObject) {
        String str;
        if (tL_error != null || !(tLObject instanceof TLRPC.Updates)) {
            getBulletinFactory().showForError(tL_error);
            return;
        }
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(getDialogId());
        if (!applyNewGiftFromUpdates((TLRPC.Updates) tLObject)) {
            lambda$new$0();
            return;
        }
        this.button.setLoading(false);
        this.fireworksOverlay.start(true);
        switchPage(0, true);
        if (getGift() != null) {
            str = getGift().title + " #" + LocaleController.formatNumber(getGift().num, ',');
        } else {
            str = "";
        }
        getBulletinFactory().createSimpleBulletin(R.raw.gift_upgrade, LocaleController.getString(R.string.Gift2UpgradedTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2UpgradedText, str))).setDuration(5000).show();
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$56(tLObject);
            }
        });
    }

    public void lambda$doUpgrade$58(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.Updates) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$57(tL_error, tLObject);
            }
        });
    }

    public void lambda$doUpgrade$59(StarsController starsController) {
        if (!starsController.balanceAvailable()) {
            getBulletinFactory().createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, "NO_BALANCE")).show();
        } else {
            this.button.setLoading(false);
            doUpgrade();
        }
    }

    public void lambda$doUpgrade$60(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public void lambda$doUpgrade$61(boolean[] zArr) {
        zArr[0] = true;
        this.button.setLoading(false);
        doUpgrade();
    }

    public void lambda$doUpgrade$62(DialogInterface dialogInterface) {
        this.button.setLoading(false);
    }

    public void lambda$doUpgrade$63(long j) {
        final boolean[] zArr = {false};
        StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(getContext(), this.resourcesProvider, j, 10, null, new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$61(zArr);
            }
        });
        starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                StarGiftSheet.this.lambda$doUpgrade$62(dialogInterface);
            }
        });
        starsNeededSheet.show();
    }

    public void lambda$doUpgrade$64(TLObject tLObject, TLRPC.TL_error tL_error, final long j) {
        String str;
        if (!(tLObject instanceof TLRPC.TL_payments_paymentResult)) {
            if (tL_error == null || !"BALANCE_TOO_LOW".equals(tL_error.text)) {
                getBulletinFactory().showForError(tL_error);
                return;
            } else if (MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                StarsController.getInstance(this.currentAccount).invalidateBalance(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doUpgrade$63(j);
                    }
                });
                return;
            } else {
                this.button.setLoading(false);
                StarsController.showNoSupportDialog(getContext(), this.resourcesProvider);
                return;
            }
        }
        final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_paymentResult.updates.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_payments_paymentResult.updates.chats, false);
        StarsController.getInstance(this.currentAccount).invalidateTransactions(false);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(getDialogId());
        StarsController.getInstance(this.currentAccount).invalidateBalance();
        if (!applyNewGiftFromUpdates(tL_payments_paymentResult.updates)) {
            lambda$new$0();
            return;
        }
        this.button.setLoading(false);
        this.fireworksOverlay.start(true);
        switchPage(0, true);
        if (getGift() != null) {
            str = getGift().title + " #" + LocaleController.formatNumber(getGift().num, ',');
        } else {
            str = "";
        }
        getBulletinFactory().createSimpleBulletin(R.raw.gift_upgrade, LocaleController.getString(R.string.Gift2UpgradedTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2UpgradedText, str))).setDuration(5000).show();
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$60(tL_payments_paymentResult);
            }
        });
    }

    public void lambda$doUpgrade$65(final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$64(tLObject, tL_error, j);
            }
        });
    }

    public void lambda$initTONTransfer$76(AlertDialog alertDialog, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, null));
    }

    public void lambda$initTONTransfer$77(TLRPC.TL_error tL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity) {
        if (tL_error == null) {
            TL_account.Password password = (TL_account.Password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo(null, password);
            TwoStepVerificationActivity.initPasswordNewAlgo(password);
            lambda$openTransfer$66(twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity);
        }
    }

    public void lambda$initTONTransfer$78(final TwoStepVerificationActivity twoStepVerificationActivity, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$initTONTransfer$77(tL_error, tLObject, twoStepVerificationActivity);
            }
        });
    }

    public void lambda$initTONTransfer$79(TLRPC.TL_error tL_error, final TwoStepVerificationActivity twoStepVerificationActivity, TLObject tLObject) {
        int i;
        int i2;
        if (getContext() == null) {
            return;
        }
        if (tL_error == null) {
            twoStepVerificationActivity.needHideProgress();
            twoStepVerificationActivity.lambda$onBackPressed$323();
            if (tLObject instanceof TL_stars.starGiftWithdrawalUrl) {
                Browser.openUrlInSystemBrowser(getContext(), ((TL_stars.starGiftWithdrawalUrl) tLObject).url);
                return;
            }
            return;
        }
        if (!"PASSWORD_MISSING".equals(tL_error.text) && !tL_error.text.startsWith("PASSWORD_TOO_FRESH_") && !tL_error.text.startsWith("SESSION_TOO_FRESH_")) {
            if ("SRP_ID_INVALID".equals(tL_error.text)) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account.getPassword(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                        StarGiftSheet.this.lambda$initTONTransfer$78(twoStepVerificationActivity, tLObject2, tL_error2);
                    }
                }, 8);
                return;
            }
            if (twoStepVerificationActivity != null) {
                twoStepVerificationActivity.needHideProgress();
                twoStepVerificationActivity.lambda$onBackPressed$323();
            }
            BulletinFactory.showError(tL_error);
            return;
        }
        if (twoStepVerificationActivity != null) {
            twoStepVerificationActivity.needHideProgress();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString(R.string.Gift2TransferToTONAlertTitle));
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView textView = new TextView(getContext());
        int i3 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i3));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.Gift2TransferToTONAlertText)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout2 = new LinearLayout(getContext());
        linearLayout2.setOrientation(0);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(getContext());
        int i4 = R.drawable.list_circle;
        imageView.setImageResource(i4);
        imageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
        int color = Theme.getColor(i3);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
        TextView textView2 = new TextView(getContext());
        textView2.setTextColor(Theme.getColor(i3));
        textView2.setTextSize(1, 16.0f);
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.Gift2TransferToTONAlertText1)));
        if (LocaleController.isRTL) {
            linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
            linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 5));
        } else {
            linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2));
            linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
        }
        LinearLayout linearLayout3 = new LinearLayout(getContext());
        linearLayout3.setOrientation(0);
        linearLayout.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
        ImageView imageView2 = new ImageView(getContext());
        imageView2.setImageResource(i4);
        imageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
        imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i3), mode));
        TextView textView3 = new TextView(getContext());
        textView3.setTextColor(Theme.getColor(i3));
        textView3.setTextSize(1, 16.0f);
        textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.Gift2TransferToTONAlertText2)));
        if (LocaleController.isRTL) {
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
            i = 5;
            linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2, 5));
        } else {
            i = 5;
            linearLayout3.addView(imageView2, LayoutHelper.createLinear(-2, -2));
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2));
        }
        if ("PASSWORD_MISSING".equals(tL_error.text)) {
            builder.setPositiveButton(LocaleController.getString(R.string.Gift2TransferToTONSetPassword), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i5) {
                    StarGiftSheet.this.lambda$initTONTransfer$76(alertDialog, i5);
                }
            });
            i2 = R.string.Cancel;
        } else {
            TextView textView4 = new TextView(getContext());
            textView4.setTextColor(Theme.getColor(i3));
            textView4.setTextSize(1, 16.0f);
            if (!LocaleController.isRTL) {
                i = 3;
            }
            textView4.setGravity(i | 48);
            textView4.setText(LocaleController.getString(R.string.Gift2TransferToTONAlertText3));
            linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
            i2 = R.string.OK;
        }
        builder.setNegativeButton(LocaleController.getString(i2), null);
        if (twoStepVerificationActivity != null) {
            twoStepVerificationActivity.showDialog(builder.create());
        } else {
            builder.show();
        }
    }

    public void lambda$initTONTransfer$80(final TwoStepVerificationActivity twoStepVerificationActivity, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$initTONTransfer$79(tL_error, twoStepVerificationActivity, tLObject);
            }
        });
    }

    public void lambda$new$0(View view) {
        if (this.button.isLoading()) {
            return;
        }
        this.checkbox.setChecked(!r3.isChecked(), true);
    }

    public void lambda$new$1(View view) {
        openTransfer();
    }

    public void lambda$onMenuPressed$2(String str) {
        AndroidUtilities.addToClipboard(str);
        getBulletinFactory().createCopyLinkBulletin(false).show();
    }

    public void lambda$onMenuPressed$3() {
        onSharePressed(null);
    }

    public void lambda$onWearPressed$4(View view) {
        this.shownWearInfo = true;
        toggleWear();
    }

    public void lambda$openAsLearnMore$36(View view) {
        lambda$new$0();
    }

    public void lambda$openAsLearnMore$37(String str, TL_stars.starGiftUpgradePreview stargiftupgradepreview) {
        if (stargiftupgradepreview == null) {
            return;
        }
        this.topView.setPreviewingAttributes(stargiftupgradepreview.sample_attributes);
        switchPage(1, false);
        this.topView.setText(1, LocaleController.getString(R.string.Gift2LearnMoreTitle), 0L, LocaleController.formatString(R.string.Gift2LearnMoreText, str));
        this.upgradeFeatureCells[0].setText(LocaleController.getString(R.string.Gift2UpgradeFeature1TextLearn));
        this.upgradeFeatureCells[1].setText(LocaleController.getString(R.string.Gift2UpgradeFeature2TextLearn));
        this.upgradeFeatureCells[2].setText(LocaleController.getString(R.string.Gift2UpgradeFeature3TextLearn));
        this.checkboxLayout.setVisibility(8);
        this.checkboxSeparator.setVisibility(8);
        this.button.setText(LocaleController.getString(R.string.OK), false);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarGiftSheet.this.lambda$openAsLearnMore$36(view);
            }
        });
        show();
    }

    public void lambda$openTransfer$67(UserSelectorBottomSheet[] userSelectorBottomSheetArr, Browser.Progress progress, TwoStepVerificationActivity twoStepVerificationActivity) {
        userSelectorBottomSheetArr[0].lambda$new$0();
        progress.end();
        presentFragment(twoStepVerificationActivity);
    }

    public void lambda$openTransfer$68(final UserSelectorBottomSheet[] userSelectorBottomSheetArr, AlertDialog alertDialog, int i) {
        final Browser.Progress makeButtonLoading = alertDialog.makeButtonLoading(i);
        final TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(2, new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() {
            @Override
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                StarGiftSheet.this.lambda$openTransfer$66(twoStepVerificationActivity, inputCheckPasswordSRP);
            }
        });
        twoStepVerificationActivity.setDelegateString(getGiftName());
        makeButtonLoading.init();
        twoStepVerificationActivity.preload(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$openTransfer$67(userSelectorBottomSheetArr, makeButtonLoading, twoStepVerificationActivity);
            }
        });
    }

    public void lambda$openTransfer$69(TLRPC.TL_error tL_error) {
        getBulletinFactory().showForError(tL_error);
    }

    public void lambda$openTransfer$70(Browser.Progress progress, UserSelectorBottomSheet[] userSelectorBottomSheetArr, final TLRPC.TL_error tL_error) {
        progress.end();
        userSelectorBottomSheetArr[0].lambda$new$0();
        if (tL_error != null) {
            this.bottomBulletinContainer.post(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$openTransfer$69(tL_error);
                }
            });
        } else {
            lambda$new$0();
        }
    }

    public void lambda$openTransfer$71(Long l, final UserSelectorBottomSheet[] userSelectorBottomSheetArr, AlertDialog alertDialog, int i) {
        final Browser.Progress makeButtonLoading = alertDialog.makeButtonLoading(i);
        makeButtonLoading.init();
        doTransfer(l.longValue(), new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarGiftSheet.this.lambda$openTransfer$70(makeButtonLoading, userSelectorBottomSheetArr, (TLRPC.TL_error) obj);
            }
        });
    }

    public void lambda$openTransfer$72(final Long l, TL_stars.TL_starGiftUnique tL_starGiftUnique, long j, final UserSelectorBottomSheet[] userSelectorBottomSheetArr) {
        String str;
        TLRPC.Chat chat;
        long longValue = l.longValue();
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        if (longValue >= 0) {
            TLRPC.User user = messagesController.getUser(l);
            str = UserObject.getForcedFirstName(user);
            chat = user;
        } else {
            TLRPC.Chat chat2 = messagesController.getChat(Long.valueOf(-l.longValue()));
            if (chat2 == null) {
                str = "";
                chat = chat2;
            } else {
                str = chat2.title;
                chat = chat2;
            }
        }
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        linearLayout.addView(new GiftTransferTopView(getContext(), tL_starGiftUnique, chat), LayoutHelper.createLinear(-1, -2, 48, 0, -4, 0, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
        textView.setTextSize(1, 16.0f);
        textView.setText(AndroidUtilities.replaceTags(j > 0 ? LocaleController.formatPluralString("Gift2TransferPriceText", (int) j, getGiftName(), DialogObject.getShortName(l.longValue())) : LocaleController.formatString(R.string.Gift2TransferText, getGiftName(), str)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 48, 24, 4, 24, 4));
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setView(linearLayout).setPositiveButton(j > 0 ? StarsIntroActivity.replaceStars(LocaleController.formatString(R.string.Gift2TransferDoPrice, Integer.valueOf((int) j))) : LocaleController.getString(R.string.Gift2TransferDo), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                StarGiftSheet.this.lambda$openTransfer$71(l, userSelectorBottomSheetArr, alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    public void lambda$openTransfer$73(TLObject tLObject, Runnable runnable, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_messages_chatFull)) {
            getBulletinFactory().makeForError(tL_error).show();
            return;
        }
        TLRPC.TL_messages_chatFull tL_messages_chatFull = (TLRPC.TL_messages_chatFull) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_messages_chatFull.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_messages_chatFull.chats, false);
        MessagesController.getInstance(this.currentAccount).putChatFull(tL_messages_chatFull.full_chat);
        if (tL_messages_chatFull.full_chat.stargifts_available) {
            runnable.run();
        } else {
            new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ChannelDoesntSupportGiftsTitle)).setMessage(LocaleController.getString(R.string.Gift2ChannelDoesntSupportGiftsText)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
        }
    }

    public void lambda$openTransfer$74(final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$openTransfer$73(tLObject, runnable, tL_error);
            }
        });
    }

    public void lambda$openTransfer$75(int i, int i2, int i3, final TL_stars.TL_starGiftUnique tL_starGiftUnique, final UserSelectorBottomSheet[] userSelectorBottomSheetArr, final long j, final Long l) {
        AlertDialog.Builder negativeButton;
        AlertDialog.Builder message;
        if (l.longValue() != -99) {
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$openTransfer$72(l, tL_starGiftUnique, j, userSelectorBottomSheetArr);
                }
            };
            if (l.longValue() < 0) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-l.longValue());
                if (chatFull == null) {
                    TLRPC.TL_channels_getFullChannel tL_channels_getFullChannel = new TLRPC.TL_channels_getFullChannel();
                    tL_channels_getFullChannel.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(-l.longValue());
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getFullChannel, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            StarGiftSheet.this.lambda$openTransfer$74(runnable, tLObject, tL_error);
                        }
                    });
                    return;
                } else if (!chatFull.stargifts_available) {
                    new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ChannelDoesntSupportGiftsTitle)).setMessage(LocaleController.getString(R.string.Gift2ChannelDoesntSupportGiftsText)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                    return;
                }
            }
            runnable.run();
            return;
        }
        if (i < i2) {
            message = new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ExportTONUnlocksAlertTitle)).setMessage(LocaleController.formatPluralString("Gift2ExportTONUnlocksAlertText", Math.max(1, i3), new Object[0]));
        } else {
            if (BuildVars.DEBUG_PRIVATE_VERSION || !BuildVars.DEBUG_VERSION) {
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(1);
                linearLayout.addView(new GiftTransferTopView(getContext(), tL_starGiftUnique), LayoutHelper.createLinear(-1, -2, 48, 0, -4, 0, 0));
                TextView textView = new TextView(getContext());
                int i4 = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i4, this.resourcesProvider));
                textView.setTextSize(1, 20.0f);
                textView.setTypeface(AndroidUtilities.bold());
                textView.setText(LocaleController.getString(R.string.Gift2ExportTONFragmentTitle));
                linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 48, 24, 4, 24, 14));
                TextView textView2 = new TextView(getContext());
                textView2.setTextColor(Theme.getColor(i4, this.resourcesProvider));
                textView2.setTextSize(1, 16.0f);
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2ExportTONFragmentText, getGiftName())));
                linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 48, 24, 0, 24, 4));
                negativeButton = new AlertDialog.Builder(getContext(), this.resourcesProvider).setView(linearLayout).setPositiveButton(LocaleController.getString(R.string.Gift2ExportTONFragmentOpen), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i5) {
                        StarGiftSheet.this.lambda$openTransfer$68(userSelectorBottomSheetArr, alertDialog, i5);
                    }
                }).setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                negativeButton.show();
            }
            message = new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ExportTONUpdateRequiredTitle)).setMessage(LocaleController.getString(R.string.Gift2ExportTONUpdateRequiredText));
        }
        negativeButton = message.setPositiveButton(LocaleController.getString(R.string.OK), null);
        negativeButton.show();
    }

    public void lambda$openUpgrade$52(TL_stars.starGiftUpgradePreview stargiftupgradepreview) {
        if (stargiftupgradepreview == null) {
            return;
        }
        this.sample_attributes = stargiftupgradepreview.sample_attributes;
        openUpgradeAfter();
    }

    public void lambda$openUpgrade$53(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.PaymentForm)) {
            getBulletinFactory().makeForError(tL_error).show();
            return;
        }
        TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
        this.upgrade_form = paymentForm;
        openUpgradeAfter();
    }

    public void lambda$openUpgrade$54(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$openUpgrade$53(tLObject, tL_error);
            }
        });
    }

    public void lambda$openUpgradeAfter$55(View view) {
        doUpgrade();
    }

    public void lambda$repollMessage$33(MessageObject messageObject) {
        TLRPC.Message message;
        this.messageObjectRepolled = true;
        Boolean bool = this.unsavedFromSavedStarGift;
        if (bool != null && messageObject != null && (message = messageObject.messageOwner) != null) {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                ((TLRPC.TL_messageActionStarGift) messageAction).saved = true ^ bool.booleanValue();
            } else if (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique) {
                ((TLRPC.TL_messageActionStarGiftUnique) messageAction).saved = true ^ bool.booleanValue();
            }
        }
        set(messageObject);
    }

    public void lambda$repollMessage$34(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        final MessageObject messageObject;
        if (tLObject instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            for (int i2 = 0; i2 < messages_messages.messages.size(); i2++) {
                TLRPC.Message message = messages_messages.messages.get(i2);
                if (message != null && message.id == i) {
                    TLRPC.MessageAction messageAction = message.action;
                    if ((messageAction instanceof TLRPC.TL_messageActionStarGift) || (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                        messageObject = new MessageObject(this.currentAccount, message, false, false);
                        messageObject.setType();
                        break;
                    }
                }
            }
        }
        messageObject = null;
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$repollMessage$33(messageObject);
                }
            });
        }
    }

    public void lambda$repollSavedStarGift$35(TL_stars.SavedStarGift savedStarGift) {
        TLRPC.Message message;
        this.userStarGiftRepolling = false;
        this.userStarGiftRepolled = true;
        if (savedStarGift != null) {
            this.unsavedFromSavedStarGift = Boolean.valueOf(savedStarGift.unsaved);
            MessageObject messageObject = this.messageObject;
            if (messageObject == null || (message = messageObject.messageOwner) == null) {
                return;
            }
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique) {
                TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = (TLRPC.TL_messageActionStarGiftUnique) messageAction;
                boolean z = tL_messageActionStarGiftUnique.saved;
                boolean z2 = !savedStarGift.unsaved;
                if (z == z2) {
                    return;
                } else {
                    tL_messageActionStarGiftUnique.saved = z2;
                }
            } else if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                boolean z3 = tL_messageActionStarGift.saved;
                boolean z4 = !savedStarGift.unsaved;
                if (z3 == z4) {
                    return;
                } else {
                    tL_messageActionStarGift.saved = z4;
                }
            }
            set(messageObject);
        }
    }

    public void lambda$repostStory$12(Long l) {
        TLRPC.Chat chat;
        String str = (l.longValue() >= 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-l.longValue()))) == null) ? "" : chat.title;
        getBulletinFactory().createSimpleBulletin(R.raw.contact_check, AndroidUtilities.replaceTags(TextUtils.isEmpty(str) ? LocaleController.getString(R.string.GiftRepostedToProfile) : LocaleController.formatString(R.string.GiftRepostedToChannelProfile, str))).show();
    }

    public void lambda$repostStory$13(StoryRecorder storyRecorder, View view, Long l, Runnable runnable, Boolean bool, final Long l2) {
        boolean booleanValue = bool.booleanValue();
        StoryRecorder.SourceView sourceView = null;
        if (booleanValue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$repostStory$12(l2);
                }
            });
            storyRecorder.replaceSourceView(null);
            ShareAlert shareAlert = this.shareAlert;
            if (shareAlert != null) {
                shareAlert.lambda$new$0();
                this.shareAlert = null;
            }
        } else {
            if ((view instanceof ShareDialogCell) && view.isAttachedToWindow()) {
                sourceView = StoryRecorder.SourceView.fromShareCell((ShareDialogCell) view);
            }
            storyRecorder.replaceSourceView(sourceView);
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$set$16() {
        getBulletinFactory().createSimpleBulletin(R.raw.copy, LocaleController.getString(R.string.WalletAddressCopied)).show(false);
    }

    public void lambda$set$18(View view) {
        onBackPressed();
    }

    public void lambda$set$19() {
        new ExplainStarsSheet(getContext()).show();
    }

    public void lambda$set$21(long j) {
        new GiftSheet(getContext(), this.currentAccount, j, new StarGiftSheet$$ExternalSyntheticLambda52(this)).show();
    }

    public void lambda$set$22(View view) {
        openUpgrade();
    }

    public void lambda$set$23(View view) {
        onBackPressed();
    }

    public void lambda$set$24(String str) {
        Browser.openUrlInSystemBrowser(getContext(), MessagesController.getInstance(this.currentAccount).tonBlockchainExplorerUrl + str);
    }

    public void lambda$set$25() {
        new ExplainStarsSheet(getContext()).show();
    }

    public void lambda$set$27(long j) {
        new GiftSheet(getContext(), this.currentAccount, j, new StarGiftSheet$$ExternalSyntheticLambda52(this)).show();
    }

    public void lambda$set$29(long j) {
        new GiftSheet(getContext(), this.currentAccount, j, new StarGiftSheet$$ExternalSyntheticLambda52(this)).show();
    }

    public void lambda$set$30(View view) {
        openUpgrade();
    }

    public void lambda$set$31(View view) {
        onBackPressed();
    }

    public void lambda$set$32(String str) {
        Browser.openUrlInSystemBrowser(getContext(), MessagesController.getInstance(this.currentAccount).tonBlockchainExplorerUrl + str);
    }

    public void lambda$setupWearPage$5(View view) {
        this.shownWearInfo = true;
        toggleWear();
    }

    public void lambda$show$46(TL_stars.TL_payments_uniqueStarGift tL_payments_uniqueStarGift) {
        TL_stars.TL_starGiftUnique tL_starGiftUnique = (TL_stars.TL_starGiftUnique) tL_payments_uniqueStarGift.gift;
        this.slugStarGift = tL_starGiftUnique;
        set(tL_starGiftUnique, true, false);
        super.show();
    }

    public static void lambda$show$47(AlertDialog alertDialog) {
        alertDialog.dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.UniqueGiftNotFound)).show();
        }
    }

    public void lambda$show$48(final AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_stars.TL_payments_uniqueStarGift) {
            final TL_stars.TL_payments_uniqueStarGift tL_payments_uniqueStarGift = (TL_stars.TL_payments_uniqueStarGift) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_uniqueStarGift.users, false);
            if (tL_payments_uniqueStarGift.gift instanceof TL_stars.TL_starGiftUnique) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$show$46(tL_payments_uniqueStarGift);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.lambda$show$47(AlertDialog.this);
            }
        });
    }

    public void lambda$show$49(AlertDialog alertDialog, MessageObject messageObject) {
        alertDialog.dismiss();
        this.messageObjectRepolled = true;
        set(messageObject);
        super.show();
    }

    public static void lambda$show$50(AlertDialog alertDialog) {
        alertDialog.dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.MessageNotFound)).show();
        }
    }

    public void lambda$show$51(TLRPC.TL_messageActionStarGift tL_messageActionStarGift, final AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        final MessageObject messageObject;
        if (tLObject instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            for (int i = 0; i < messages_messages.messages.size(); i++) {
                TLRPC.Message message = messages_messages.messages.get(i);
                if (message != null && !(message instanceof TLRPC.TL_messageEmpty) && message.id == tL_messageActionStarGift.upgrade_msg_id) {
                    messageObject = new MessageObject(this.currentAccount, message, false, false);
                    messageObject.setType();
                    break;
                }
            }
        }
        messageObject = null;
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$show$49(alertDialog, messageObject);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.lambda$show$50(AlertDialog.this);
                }
            });
        }
    }

    public void lambda$switchPage$14(ValueAnimator valueAnimator) {
        this.currentPage.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        onSwitchedPage();
    }

    public static void lambda$toggleShow$43(long j, BaseFragment baseFragment) {
        String str;
        Bundle bundle = new Bundle();
        if (j >= 0) {
            str = "user_id";
        } else {
            j = -j;
            str = "chat_id";
        }
        bundle.putLong(str, j);
        bundle.putBoolean("my_profile", true);
        bundle.putBoolean("open_gifts", true);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    public void lambda$toggleShow$44(TLObject tLObject, TLRPC.Document document, boolean z, TLRPC.TL_error tL_error) {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            if (tL_error != null) {
                getBulletinFactory().createErrorBulletin(LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text)).show(false);
                return;
            }
            return;
        }
        lambda$new$0();
        final long dialogId = getDialogId();
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(dialogId);
        if (dialogId >= 0) {
            BulletinFactory.of(safeLastFragment).createEmojiBulletin(document, LocaleController.getString(z ? R.string.Gift2MadePrivateTitle : R.string.Gift2MadePublicTitle), AndroidUtilities.replaceSingleTag(LocaleController.getString(z ? R.string.Gift2MadePrivate : R.string.Gift2MadePublic), safeLastFragment instanceof ProfileActivity ? null : new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.lambda$toggleShow$43(dialogId, safeLastFragment);
                }
            })).show(true);
        } else {
            BulletinFactory.of(safeLastFragment).createEmojiBulletin(document, LocaleController.getString(z ? R.string.Gift2ChannelMadePrivateTitle : R.string.Gift2ChannelMadePublicTitle), LocaleController.getString(z ? R.string.Gift2ChannelMadePrivate : R.string.Gift2ChannelMadePublic)).show();
        }
    }

    public void lambda$toggleShow$45(final TLRPC.Document document, final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$toggleShow$44(tLObject, document, z, tL_error);
            }
        });
    }

    public void lambda$toggleWear$10(boolean z) {
        showHint(AndroidUtilities.replaceTags(LocaleController.formatString(z ? R.string.Gift2ActionWearDone : R.string.Gift2ActionWearOffDone, getGiftName())), this.ownerTextView, true);
    }

    public void lambda$toggleWear$11(View view) {
        onBackPressed();
    }

    public void lambda$toggleWear$6() {
        new PremiumFeatureBottomSheet(getDummyFragment(), 12, false).show();
    }

    public void lambda$toggleWear$7(TLRPC.Chat chat) {
        presentFragment(StatisticActivity.create(chat));
    }

    public void lambda$toggleWear$8(TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, long j, MessagesController messagesController, ChannelBoostsController.CanApplyBoost canApplyBoost) {
        this.button.setLoading(false);
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(getDummyFragment(), getContext(), 26, this.currentAccount, this.resourcesProvider);
        limitReachedBottomSheet.setCanApplyBoost(canApplyBoost);
        limitReachedBottomSheet.setBoostsStats(tL_premium_boostsStatus, true);
        limitReachedBottomSheet.setDialogId(j);
        final TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
        if (chat != null) {
            limitReachedBottomSheet.showStatisticButtonInLink(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$toggleWear$7(chat);
                }
            });
        }
        limitReachedBottomSheet.show();
    }

    public void lambda$toggleWear$9(final MessagesController messagesController, final long j, final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        if (tL_premium_boostsStatus != null && tL_premium_boostsStatus.level < messagesController.channelEmojiStatusLevelMin) {
            messagesController.getBoostsController().userCanBoostChannel(j, tL_premium_boostsStatus, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    StarGiftSheet.this.lambda$toggleWear$8(tL_premium_boostsStatus, j, messagesController, (ChannelBoostsController.CanApplyBoost) obj);
                }
            });
        } else {
            this.button.setLoading(false);
            toggleWear(true);
        }
    }

    public void onMenuPressed(View view) {
        final String link = getLink();
        ItemOptions.makeOptions(this.container, this.resourcesProvider, view).addIf(link != null, R.drawable.msg_link, LocaleController.getString(R.string.CopyLink), new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$onMenuPressed$2(link);
            }
        }).addIf(link != null, R.drawable.msg_share, LocaleController.getString(R.string.ShareFile), new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$onMenuPressed$3();
            }
        }).addIf(canTransfer(), R.drawable.menu_feature_transfer, LocaleController.getString(R.string.Gift2TransferOption), new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.openTransfer();
            }
        }).addIf(this.savedStarGift == null && getDialogId() != 0, R.drawable.msg_view_file, LocaleController.getString(R.string.Gift2ViewInProfile), new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.openInProfile();
            }
        }).setDrawScrim(false).setOnTopOfScrim().setDimAlpha(0).translate(0.0f, -AndroidUtilities.dp(2.0f)).show();
    }

    public void onSwitchedPage() {
        this.infoLayout.setAlpha(this.currentPage.at(0));
        this.upgradeLayout.setAlpha(this.currentPage.at(1));
        this.wearLayout.setAlpha(this.currentPage.at(2));
        this.topView.onSwitchPage(this.currentPage);
        this.container.updateTopViewTranslation();
        this.container.invalidate();
    }

    public void onWearPressed(View view) {
        if (UserConfig.getInstance(this.currentAccount).isPremium() && (isWorn() || this.shownWearInfo)) {
            toggleWear();
            return;
        }
        TL_stars.TL_starGiftUnique uniqueGift = getUniqueGift();
        if (uniqueGift == null) {
            return;
        }
        long peerDialogId = DialogObject.getPeerDialogId(uniqueGift.owner_id);
        this.wearTitle.setText(LocaleController.formatString(R.string.Gift2WearTitle, uniqueGift.title + " #" + LocaleController.formatNumber(uniqueGift.num, ',')));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.Gift2WearStart));
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            spannableStringBuilder.append((CharSequence) " l");
            if (this.lockSpan == null) {
                this.lockSpan = new ColoredImageSpan(R.drawable.msg_mini_lock3);
            }
            spannableStringBuilder.setSpan(this.lockSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        }
        this.button.setText(spannableStringBuilder, true);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.lambda$onWearPressed$4(view2);
            }
        });
        this.topView.setWearPreview(MessagesController.getInstance(this.currentAccount).getUserOrChat(peerDialogId));
        switchPage(2, true);
    }

    public void openInProfile() {
        long dialogId = getDialogId();
        if (dialogId == 0) {
            return;
        }
        lambda$set$28(dialogId);
    }

    public void lambda$set$28(long j) {
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
        lambda$new$0();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || UserObject.isService(j)) {
            return;
        }
        Bundle bundle = new Bundle();
        if (j > 0) {
            bundle.putLong("user_id", j);
            if (j == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                bundle.putBoolean("my_profile", true);
            }
        } else {
            bundle.putLong("chat_id", -j);
        }
        bundle.putBoolean("open_gifts", true);
        safeLastFragment.presentFragment(new ProfileActivity(bundle));
    }

    public void openUpgrade() {
        TL_stars.InputSavedStarGift inputStarGift;
        boolean z;
        boolean z2;
        boolean z3;
        long j;
        long j2;
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
        if (this.switchingPagesAnimator == null && (inputStarGift = getInputStarGift()) != null) {
            MessageObject messageObject = this.messageObject;
            if (messageObject != null) {
                TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
                if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                    return;
                }
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                j = tL_messageActionStarGift.gift.id;
                j2 = tL_messageActionStarGift.upgrade_stars;
                z3 = tL_messageActionStarGift.name_hidden;
                TLRPC.TL_textWithEntities tL_textWithEntities = tL_messageActionStarGift.message;
                z = (tL_textWithEntities == null || TextUtils.isEmpty(tL_textWithEntities.text)) ? false : true;
                z2 = tL_messageActionStarGift.peer != null;
            } else {
                TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
                if (savedStarGift == null) {
                    return;
                }
                TL_stars.StarGift starGift = savedStarGift.gift;
                long j3 = starGift.id;
                long j4 = savedStarGift.upgrade_stars;
                boolean z4 = (starGift instanceof TL_stars.TL_starGift) && savedStarGift.name_hidden;
                TLRPC.TL_textWithEntities tL_textWithEntities2 = savedStarGift.message;
                z = (tL_textWithEntities2 == null || TextUtils.isEmpty(tL_textWithEntities2.text)) ? false : true;
                z2 = this.dialogId < 0;
                z3 = z4;
                j = j3;
                j2 = j4;
            }
            if (z3) {
                this.checkboxTextView.setText(LocaleController.getString(z2 ? R.string.Gift2AddMyNameNameChannel : R.string.Gift2AddMyNameName));
            } else {
                this.checkboxTextView.setText(LocaleController.getString(z ? R.string.Gift2AddSenderNameComment : R.string.Gift2AddSenderName));
            }
            this.checkbox.setChecked(!z3 && j2 > 0, false);
            ArrayList arrayList = this.sample_attributes;
            if (arrayList != null && (j2 > 0 || this.upgrade_form != null)) {
                openUpgradeAfter();
                return;
            }
            if (arrayList == null) {
                StarsController.getInstance(this.currentAccount).getStarGiftPreview(j, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        StarGiftSheet.this.lambda$openUpgrade$52((TL_stars.starGiftUpgradePreview) obj);
                    }
                });
            }
            if (j2 > 0 || this.upgrade_form != null) {
                return;
            }
            TLRPC.TL_inputInvoiceStarGiftUpgrade tL_inputInvoiceStarGiftUpgrade = new TLRPC.TL_inputInvoiceStarGiftUpgrade();
            tL_inputInvoiceStarGiftUpgrade.keep_original_details = this.checkbox.isChecked();
            tL_inputInvoiceStarGiftUpgrade.stargift = inputStarGift;
            TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm = new TLRPC.TL_payments_getPaymentForm();
            tL_payments_getPaymentForm.invoice = tL_inputInvoiceStarGiftUpgrade;
            JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(this.resourcesProvider);
            if (makeThemeParams != null) {
                TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                tL_payments_getPaymentForm.theme_params = tL_dataJSON;
                tL_dataJSON.data = makeThemeParams.toString();
                tL_payments_getPaymentForm.flags |= 1;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$openUpgrade$54(tLObject, tL_error);
                }
            });
        }
    }

    private void openUpgradeAfter() {
        long j;
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                return;
            } else {
                j = ((TLRPC.TL_messageActionStarGift) messageAction).upgrade_stars;
            }
        } else {
            TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
            if (savedStarGift == null) {
                return;
            } else {
                j = savedStarGift.upgrade_stars;
            }
        }
        if (this.sample_attributes != null) {
            if (j > 0 || this.upgrade_form != null) {
                long j2 = 0;
                if (this.upgrade_form != null) {
                    for (int i = 0; i < this.upgrade_form.invoice.prices.size(); i++) {
                        j2 += this.upgrade_form.invoice.prices.get(i).amount;
                    }
                }
                this.topView.setPreviewingAttributes(this.sample_attributes);
                this.topView.setText(1, LocaleController.getString(R.string.Gift2UpgradeTitle), 0L, LocaleController.getString(R.string.Gift2UpgradeText));
                if (j2 > 0) {
                    this.button.setText(StarsIntroActivity.replaceStars(LocaleController.formatString(R.string.Gift2UpgradeButton, Long.valueOf(j2))), true);
                } else {
                    this.button.setText(LocaleController.getString(R.string.Confirm), true);
                }
                this.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        StarGiftSheet.this.lambda$openUpgradeAfter$55(view);
                    }
                });
                switchPage(1, true);
            }
        }
    }

    private void presentFragment(BaseFragment baseFragment) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        safeLastFragment.showAsSheet(baseFragment, bottomSheetParams);
    }

    private void repollMessage() {
        MessageObject messageObject;
        if (this.messageObjectRepolling || this.messageObjectRepolled || (messageObject = this.messageObject) == null) {
            return;
        }
        this.messageObjectRepolling = true;
        final int id = messageObject.getId();
        TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
        tL_messages_getMessages.id.add(Integer.valueOf(id));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$repollMessage$34(id, tLObject, tL_error);
            }
        });
    }

    private void repollSavedStarGift() {
        TL_stars.InputSavedStarGift inputStarGift;
        if (this.userStarGiftRepolling || this.userStarGiftRepolled || this.messageObject == null || (inputStarGift = getInputStarGift()) == null) {
            return;
        }
        this.userStarGiftRepolling = true;
        StarsController.getInstance(this.currentAccount).getUserStarGift(inputStarGift, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarGiftSheet.this.lambda$repollSavedStarGift$35((TL_stars.SavedStarGift) obj);
            }
        });
    }

    public void repostStory(final View view) {
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity == null) {
            return;
        }
        StoryRecorder.SourceView fromShareCell = view instanceof ShareDialogCell ? StoryRecorder.SourceView.fromShareCell((ShareDialogCell) view) : null;
        ArrayList arrayList = new ArrayList();
        MessageObject messageObject = this.messageObject;
        if (messageObject == null) {
            if (!(getGift() instanceof TL_stars.TL_starGiftUnique)) {
                return;
            }
            long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            TL_stars.TL_starGiftUnique tL_starGiftUnique = (TL_stars.TL_starGiftUnique) getGift();
            TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
            tL_messageService.peer_id = MessagesController.getInstance(this.currentAccount).getPeer(clientUserId);
            tL_messageService.from_id = MessagesController.getInstance(this.currentAccount).getPeer(clientUserId);
            tL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = new TLRPC.TL_messageActionStarGiftUnique();
            tL_messageActionStarGiftUnique.gift = tL_starGiftUnique;
            tL_messageActionStarGiftUnique.upgrade = true;
            tL_messageService.action = tL_messageActionStarGiftUnique;
            messageObject = new MessageObject(this.currentAccount, tL_messageService, false, false);
            messageObject.setType();
        }
        arrayList.add(messageObject);
        final StoryRecorder storyRecorder = StoryRecorder.getInstance(launchActivity, this.currentAccount);
        storyRecorder.setOnPrepareCloseListener(new Utilities.Callback4() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4) {
                StarGiftSheet.this.lambda$repostStory$13(storyRecorder, view, (Long) obj, (Runnable) obj2, (Boolean) obj3, (Long) obj4);
            }
        });
        storyRecorder.openRepost(fromShareCell, StoryEntry.repostMessage(arrayList));
    }

    public void toggleShow() {
        final boolean z;
        TL_stars.StarGift starGift;
        TLRPC.Message message;
        if (this.button.isLoading()) {
            return;
        }
        TL_stars.InputSavedStarGift inputStarGift = getInputStarGift();
        MessageObject messageObject = this.messageObject;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
            if (savedStarGift == null) {
                return;
            }
            z = !savedStarGift.unsaved;
            starGift = savedStarGift.gift;
        } else {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                z = tL_messageActionStarGift.saved;
                starGift = tL_messageActionStarGift.gift;
            } else {
                if (!(messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                    return;
                }
                TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = (TLRPC.TL_messageActionStarGiftUnique) messageAction;
                z = tL_messageActionStarGiftUnique.saved;
                starGift = tL_messageActionStarGiftUnique.gift;
            }
        }
        final TLRPC.Document document = starGift.getDocument();
        this.button.setLoading(true);
        TL_stars.saveStarGift savestargift = new TL_stars.saveStarGift();
        savestargift.unsave = z;
        savestargift.stargift = inputStarGift;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(savestargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$toggleShow$45(document, z, tLObject, tL_error);
            }
        });
    }

    public boolean canTransfer() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.canTransfer():boolean");
    }

    protected BulletinFactory getBulletinFactory() {
        return BulletinFactory.of(this.bottomBulletinContainer, this.resourcesProvider);
    }

    public TL_stars.StarGift getGift() {
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            if (message == null) {
                return null;
            }
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                return ((TLRPC.TL_messageActionStarGift) messageAction).gift;
            }
            if (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique) {
                return ((TLRPC.TL_messageActionStarGiftUnique) messageAction).gift;
            }
        } else {
            TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
            if (savedStarGift != null) {
                return savedStarGift.gift;
            }
            TL_stars.TL_starGiftUnique tL_starGiftUnique = this.slugStarGift;
            if (tL_starGiftUnique != null) {
                return tL_starGiftUnique;
            }
        }
        return null;
    }

    public String getGiftName() {
        TL_stars.StarGift gift = getGift();
        if (!(gift instanceof TL_stars.TL_starGiftUnique)) {
            return "";
        }
        return ((TL_stars.TL_starGiftUnique) gift).title + " #" + LocaleController.formatNumber(r0.num, ',');
    }

    public TL_stars.TL_starGiftUnique getUniqueGift() {
        TL_stars.StarGift gift = getGift();
        if (gift instanceof TL_stars.TL_starGiftUnique) {
            return (TL_stars.TL_starGiftUnique) gift;
        }
        return null;
    }

    public boolean isWorn() {
        TL_stars.TL_starGiftUnique uniqueGift = getUniqueGift();
        if (uniqueGift == null) {
            return false;
        }
        long peerDialogId = DialogObject.getPeerDialogId(uniqueGift.owner_id);
        if (peerDialogId == 0) {
            return false;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        if (peerDialogId > 0) {
            TLRPC.EmojiStatus emojiStatus = messagesController.getUser(Long.valueOf(peerDialogId)).emoji_status;
            return (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) && ((TLRPC.TL_emojiStatusCollectible) emojiStatus).collectible_id == uniqueGift.id;
        }
        TLRPC.EmojiStatus emojiStatus2 = messagesController.getChat(Long.valueOf(-peerDialogId)).emoji_status;
        return (emojiStatus2 instanceof TLRPC.TL_emojiStatusCollectible) && ((TLRPC.TL_emojiStatusCollectible) emojiStatus2).collectible_id == uniqueGift.id;
    }

    @Override
    public void onBackPressed() {
        if (this.onlyWearInfo || this.currentPage.to <= 0 || this.button.isLoading() || this.isLearnMore) {
            super.onBackPressed();
            return;
        }
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            set(messageObject);
        } else {
            TL_stars.SavedStarGift savedStarGift = this.savedStarGift;
            if (savedStarGift != null) {
                set(savedStarGift);
            }
        }
        switchPage(0, true);
    }

    public void onSharePressed(View view) {
        ShareAlert shareAlert = this.shareAlert;
        if (shareAlert != null && shareAlert.isShown()) {
            this.shareAlert.lambda$new$0();
        }
        String link = getLink();
        AnonymousClass2 anonymousClass2 = new ShareAlert(getContext(), null, null, link, null, false, link, null, false, false, true, null, this.resourcesProvider) {
            AnonymousClass2(Context context, ChatActivity chatActivity, ArrayList arrayList, String link2, String str2, boolean z, String link22, String str4, boolean z2, boolean z3, boolean z4, Integer num, Theme.ResourcesProvider resourcesProvider) {
                super(context, chatActivity, arrayList, link22, str2, z, link22, str4, z2, z3, z4, num, resourcesProvider);
                this.includeStoryFromMessage = true;
            }

            @Override
            public void onSend(LongSparseArray longSparseArray, int i, TLRPC.TL_forumTopic tL_forumTopic) {
                Bulletin createSimpleBulletin;
                super.onSend(longSparseArray, i, tL_forumTopic);
                BulletinFactory bulletinFactory = StarGiftSheet.this.getBulletinFactory();
                if (bulletinFactory != null) {
                    if (longSparseArray.size() == 1) {
                        long keyAt = longSparseArray.keyAt(0);
                        if (keyAt == UserConfig.getInstance(this.currentAccount).clientUserId) {
                            createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.saved_messages, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LinkSharedToSavedMessages, new Object[0])), 5000);
                        } else if (keyAt < 0) {
                            createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LinkSharedTo, tL_forumTopic != null ? tL_forumTopic.title : MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-keyAt)).title)), 5000);
                        } else {
                            createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LinkSharedTo, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(keyAt)).first_name)), 5000);
                        }
                    } else {
                        createSimpleBulletin = bulletinFactory.createSimpleBulletin(R.raw.forward, AndroidUtilities.replaceTags(LocaleController.formatPluralString("LinkSharedToManyChats", longSparseArray.size(), Integer.valueOf(longSparseArray.size()))));
                    }
                    createSimpleBulletin.hideAfterBottomSheet(false).show();
                    try {
                        this.container.performHapticFeedback(3);
                    } catch (Exception unused) {
                    }
                }
            }

            @Override
            protected void onShareStory(View view2) {
                StarGiftSheet.this.repostStory(view2);
            }
        };
        this.shareAlert = anonymousClass2;
        anonymousClass2.setDelegate(new ShareAlert.ShareAlertDelegate() {
            AnonymousClass3() {
            }

            @Override
            public boolean didCopy() {
                StarGiftSheet.this.getBulletinFactory().createCopyLinkBulletin(false).show();
                return true;
            }

            @Override
            public void didShare() {
                ShareAlert.ShareAlertDelegate.CC.$default$didShare(this);
            }
        });
        this.shareAlert.show();
    }

    @Override
    protected void onSwipeStarts() {
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
    }

    public void openAsLearnMore(long j, final String str) {
        this.isLearnMore = true;
        StarsController.getInstance(this.currentAccount).getStarGiftPreview(j, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarGiftSheet.this.lambda$openAsLearnMore$37(str, (TL_stars.starGiftUpgradePreview) obj);
            }
        });
    }

    public void openTransfer() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.openTransfer():void");
    }

    public StarGiftSheet set(String str, TL_stars.TL_starGiftUnique tL_starGiftUnique) {
        this.slug = str;
        this.slugStarGift = tL_starGiftUnique;
        set(tL_starGiftUnique, true, false);
        this.beforeTableTextView.setVisibility(8);
        this.afterTableTextView.setVisibility(8);
        if (this.firstSet) {
            switchPage(0, false);
            this.firstSet = false;
        }
        return this;
    }

    public org.telegram.ui.Stars.StarGiftSheet set(org.telegram.messenger.MessageObject r43) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.set(org.telegram.messenger.MessageObject):org.telegram.ui.Stars.StarGiftSheet");
    }

    public org.telegram.ui.Stars.StarGiftSheet set(org.telegram.tgnet.tl.TL_stars.SavedStarGift r38) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.set(org.telegram.tgnet.tl.TL_stars$SavedStarGift):org.telegram.ui.Stars.StarGiftSheet");
    }

    public void set(TL_stars.TL_starGiftUnique tL_starGiftUnique, boolean z, boolean z2) {
        SpannableString spannableString;
        TableView tableView;
        CharSequence formatSpannable;
        TableView tableView2;
        String string;
        StringBuilder sb;
        final long peerDialogId = DialogObject.getPeerDialogId(tL_starGiftUnique.owner_id);
        this.topView.setGift(tL_starGiftUnique, isMineWithActions(this.currentAccount, peerDialogId), isWorn(), getLink() != null);
        this.topView.setText(0, tL_starGiftUnique.title, 0L, LocaleController.formatPluralStringComma("Gift2CollectionNumber", tL_starGiftUnique.num));
        Spannable spannable = null;
        this.ownerTextView = null;
        this.tableView.clear();
        if (!z2) {
            if (!TextUtils.isEmpty(tL_starGiftUnique.owner_address)) {
                this.tableView.addWalletAddressRow(LocaleController.getString(R.string.Gift2Owner), tL_starGiftUnique.owner_address, new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$set$16();
                    }
                });
            } else if (peerDialogId == 0 && tL_starGiftUnique.owner_name != null) {
                this.tableView.addRow(LocaleController.getString(R.string.Gift2Owner), tL_starGiftUnique.owner_name);
            } else if (peerDialogId != 0) {
                this.ownerTextView = ((TableView.TableRowContent) this.tableView.addRowUserWithEmojiStatus(LocaleController.getString(R.string.Gift2Owner), this.currentAccount, peerDialogId, new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$set$17(peerDialogId);
                    }
                }).getChildAt(1)).getChildAt(0);
            }
        }
        addAttributeRow(StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributeModel.class));
        addAttributeRow(StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributeBackdrop.class));
        addAttributeRow(StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributePattern.class));
        if (!z2) {
            if (this.messageObject == null) {
                tableView2 = this.tableView;
                string = LocaleController.getString(R.string.Gift2Quantity);
                sb = new StringBuilder();
            } else if (this.messageObjectRepolled) {
                tableView2 = this.tableView;
                string = LocaleController.getString(R.string.Gift2Quantity);
                sb = new StringBuilder();
            } else {
                TextView textView = (TextView) ((TableView.TableRowContent) this.tableView.addRow(LocaleController.getString(R.string.Gift2Quantity), "").getChildAt(1)).getChildAt(0);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("x ");
                LoadingSpan loadingSpan = new LoadingSpan(textView, AndroidUtilities.dp(90.0f), 0, this.resourcesProvider);
                int i = Theme.key_windowBackgroundWhiteBlackText;
                loadingSpan.setColors(Theme.multAlpha(Theme.getColor(i, this.resourcesProvider), 0.21f), Theme.multAlpha(Theme.getColor(i, this.resourcesProvider), 0.08f));
                spannableStringBuilder.setSpan(loadingSpan, 0, 1, 33);
                textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
                repollMessage();
            }
            sb.append(LocaleController.formatPluralStringComma("Gift2QuantityIssued1", tL_starGiftUnique.availability_issued));
            sb.append(LocaleController.formatPluralStringComma("Gift2QuantityIssued2", tL_starGiftUnique.availability_total));
            tableView2.addRow(string, sb.toString());
        }
        TL_stars.starGiftAttributeOriginalDetails stargiftattributeoriginaldetails = (TL_stars.starGiftAttributeOriginalDetails) StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributeOriginalDetails.class);
        if (stargiftattributeoriginaldetails != null) {
            if ((stargiftattributeoriginaldetails.flags & 1) != 0) {
                long peerDialogId2 = DialogObject.getPeerDialogId(stargiftattributeoriginaldetails.sender_id);
                spannableString = new SpannableString(DialogObject.getName(peerDialogId2));
                spannableString.setSpan(new ClickableSpan() {
                    final long val$sender_id;

                    AnonymousClass5(long peerDialogId22) {
                        r2 = peerDialogId22;
                    }

                    @Override
                    public void onClick(View view) {
                        StarGiftSheet.this.lambda$set$28(r2);
                    }

                    @Override
                    public void updateDrawState(TextPaint textPaint) {
                        textPaint.setColor(textPaint.linkColor);
                    }
                }, 0, spannableString.length(), 33);
            } else {
                spannableString = null;
            }
            long peerDialogId3 = DialogObject.getPeerDialogId(stargiftattributeoriginaldetails.recipient_id);
            SpannableString spannableString2 = new SpannableString(DialogObject.getName(peerDialogId3));
            spannableString2.setSpan(new ClickableSpan() {
                final long val$recipient_id;

                AnonymousClass6(long peerDialogId32) {
                    r2 = peerDialogId32;
                }

                @Override
                public void onClick(View view) {
                    StarGiftSheet.this.lambda$set$28(r2);
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setColor(textPaint.linkColor);
                }
            }, 0, spannableString2.length(), 33);
            if (stargiftattributeoriginaldetails.message != null) {
                TextPaint textPaint = new TextPaint(1);
                textPaint.setTextSize(AndroidUtilities.dp(14.0f));
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(stargiftattributeoriginaldetails.message.text);
                MessageObject.addEntitiesToText(spannableStringBuilder2, stargiftattributeoriginaldetails.message.entities, false, false, false, false);
                spannable = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji(spannableStringBuilder2, textPaint.getFontMetricsInt(), false), stargiftattributeoriginaldetails.message.entities, textPaint.getFontMetricsInt());
            }
            String replaceAll = LocaleController.getInstance().getFormatterYear().format(stargiftattributeoriginaldetails.date * 1000).replaceAll("\\.", "/");
            if (stargiftattributeoriginaldetails.sender_id == stargiftattributeoriginaldetails.recipient_id) {
                tableView = this.tableView;
                formatSpannable = spannable == null ? LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsSelf, spannableString, replaceAll) : LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsSelfComment, spannableString, replaceAll, spannable);
            } else {
                tableView = this.tableView;
                formatSpannable = spannableString != null ? spannable == null ? LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetails, spannableString, spannableString2, replaceAll) : LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsComment, spannableString, spannableString2, replaceAll, spannable) : spannable == null ? LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsNoSender, spannableString2, replaceAll) : LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsNoSenderComment, spannableString2, replaceAll, spannable);
            }
            TableView.TableRowFullContent addFullRow = tableView.addFullRow(formatSpannable);
            addFullRow.setFilled(true);
            SpoilersTextView spoilersTextView = (SpoilersTextView) addFullRow.getChildAt(0);
            spoilersTextView.setTextSize(1, 12.0f);
            spoilersTextView.setGravity(17);
        }
        this.button.setText(LocaleController.getString(R.string.OK), true);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarGiftSheet.this.lambda$set$18(view);
            }
        });
    }

    public StarGiftSheet setupWearPage() {
        TL_stars.TL_starGiftUnique uniqueGift = getUniqueGift();
        if (uniqueGift == null) {
            return this;
        }
        long peerDialogId = DialogObject.getPeerDialogId(uniqueGift.owner_id);
        this.wearTitle.setText(LocaleController.formatString(R.string.Gift2WearTitle, uniqueGift.title + " #" + LocaleController.formatNumber(uniqueGift.num, ',')));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.Gift2WearStart));
        if (peerDialogId == UserConfig.getInstance(this.currentAccount).getClientUserId() && !UserConfig.getInstance(this.currentAccount).isPremium()) {
            spannableStringBuilder.append((CharSequence) " l");
            if (this.lockSpan == null) {
                this.lockSpan = new ColoredImageSpan(R.drawable.msg_mini_lock3);
            }
            spannableStringBuilder.setSpan(this.lockSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        }
        this.button.setText(spannableStringBuilder, true);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarGiftSheet.this.lambda$setupWearPage$5(view);
            }
        });
        this.topView.setWearPreview(MessagesController.getInstance(this.currentAccount).getUserOrChat(peerDialogId));
        switchPage(2, false);
        this.onlyWearInfo = true;
        return this;
    }

    @Override
    public void show() {
        MessageObject messageObject;
        TLRPC.Message message;
        if (this.slug != null && this.slugStarGift == null) {
            final AlertDialog alertDialog = new AlertDialog(getContext(), 3);
            alertDialog.showDelayed(500L);
            TL_stars.getUniqueStarGift getuniquestargift = new TL_stars.getUniqueStarGift();
            getuniquestargift.slug = this.slug;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getuniquestargift, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$show$48(alertDialog, tLObject, tL_error);
                }
            });
        } else if (this.savedStarGift == null && (messageObject = this.messageObject) != null && (message = messageObject.messageOwner) != null) {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                final TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                if (tL_messageActionStarGift.upgraded) {
                    final AlertDialog alertDialog2 = new AlertDialog(getContext(), 3);
                    alertDialog2.showDelayed(500L);
                    TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                    tL_messages_getMessages.id.add(Integer.valueOf(tL_messageActionStarGift.upgrade_msg_id));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            StarGiftSheet.this.lambda$show$51(tL_messageActionStarGift, alertDialog2, tLObject, tL_error);
                        }
                    });
                    return;
                }
            }
        }
        super.show();
    }

    public void showHint(CharSequence charSequence, View view, boolean z) {
        Layout layout;
        float paddingLeft;
        HintView2 hintView2 = this.currentHintView;
        if ((hintView2 != null && hintView2.shown() && this.currentHintViewTextView == view) || view == null) {
            return;
        }
        if (!z) {
            if (view instanceof TextView) {
                layout = ((TextView) view).getLayout();
            } else if (!(view instanceof SimpleTextView)) {
                return;
            } else {
                layout = ((SimpleTextView) view).getLayout();
            }
            if (layout == null) {
                return;
            }
            CharSequence text = layout.getText();
            if (!(text instanceof Spanned)) {
                return;
            }
            Spanned spanned = (Spanned) text;
            ButtonSpan[] buttonSpanArr = (ButtonSpan[]) spanned.getSpans(0, spanned.length(), ButtonSpan.class);
            if (buttonSpanArr == null || buttonSpanArr.length <= 0) {
                return;
            }
            paddingLeft = view.getPaddingLeft() + layout.getPrimaryHorizontal(spanned.getSpanStart(buttonSpanArr[buttonSpanArr.length - 1])) + (r5.getSize() / 2.0f);
        } else {
            if (!(view instanceof SimpleTextView)) {
                return;
            }
            SimpleTextView simpleTextView = (SimpleTextView) view;
            paddingLeft = simpleTextView.getRightDrawableX() + (simpleTextView.getRightDrawableWidth() / 2.0f);
        }
        int[] iArr = new int[2];
        view.getLocationOnScreen(r5);
        this.container.getLocationOnScreen(iArr);
        int[] iArr2 = {iArr2[0] - iArr[0], iArr2[1] - iArr[1]};
        HintView2 hintView22 = this.currentHintView;
        if (hintView22 != null) {
            hintView22.hide();
            this.currentHintView = null;
        }
        final HintView2 hintView23 = new HintView2(getContext(), 3);
        hintView23.setMultilineText(!z);
        hintView23.setText(charSequence);
        hintView23.setJointPx(0.0f, (iArr2[0] + paddingLeft) - (AndroidUtilities.dp(16.0f) + this.backgroundPaddingLeft));
        hintView23.setTranslationY(((iArr2[1] - AndroidUtilities.dp(100.0f)) - (view.getHeight() / 2.0f)) + AndroidUtilities.dp((z ? 18 : 0) + 4.33f));
        hintView23.setDuration(3000L);
        hintView23.setPadding(AndroidUtilities.dp(16.0f) + this.backgroundPaddingLeft, 0, AndroidUtilities.dp(16.0f) + this.backgroundPaddingLeft, 0);
        hintView23.setOnHiddenListener(new Runnable() {
            @Override
            public final void run() {
                AndroidUtilities.removeFromParent(HintView2.this);
            }
        });
        hintView23.show();
        this.container.addView(hintView23, LayoutHelper.createFrame(-1, 100.0f));
        this.currentHintView = hintView23;
        this.currentHintViewTextView = view;
    }

    public void switchPage(int i, boolean z) {
        switchPage(i, z, null);
    }

    public void switchPage(int i, boolean z, Runnable runnable) {
        ValueAnimator valueAnimator = this.switchingPagesAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.switchingPagesAnimator = null;
        }
        if (i != 1) {
            AndroidUtilities.cancelRunOnUIThread(this.topView.checkToRotateRunnable);
        }
        PageTransition pageTransition = this.currentPage;
        PageTransition pageTransition2 = new PageTransition(pageTransition == null ? 0 : pageTransition.to, i, 0.0f);
        this.currentPage = pageTransition2;
        if (z) {
            this.infoLayout.setVisibility(pageTransition2.contains(0) ? 0 : 8);
            this.upgradeLayout.setVisibility(this.currentPage.contains(1) ? 0 : 8);
            this.wearLayout.setVisibility(this.currentPage.contains(2) ? 0 : 8);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.switchingPagesAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarGiftSheet.this.lambda$switchPage$14(valueAnimator2);
                }
            });
            this.switchingPagesAnimator.addListener(new AnimatorListenerAdapter() {
                final Runnable val$done;
                final int val$page;

                AnonymousClass4(int i2, Runnable runnable2) {
                    r2 = i2;
                    r3 = runnable2;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    StarGiftSheet.this.onSwitchedPage();
                    StarGiftSheet.this.infoLayout.setVisibility(r2 == 0 ? 0 : 8);
                    StarGiftSheet.this.upgradeLayout.setVisibility(r2 == 1 ? 0 : 8);
                    StarGiftSheet.this.wearLayout.setVisibility(r2 == 2 ? 0 : 8);
                    StarGiftSheet.this.switchingPagesAnimator = null;
                    Runnable runnable2 = r3;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            });
            this.switchingPagesAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.switchingPagesAnimator.setDuration(320L);
            this.switchingPagesAnimator.start();
            this.topView.prepareSwitchPage(this.currentPage);
        } else {
            pageTransition2.setProgress(1.0f);
            onSwitchedPage();
            this.infoLayout.setVisibility(i2 == 0 ? 0 : 8);
            this.upgradeLayout.setVisibility(i2 == 1 ? 0 : 8);
            this.wearLayout.setVisibility(i2 != 2 ? 8 : 0);
            if (runnable2 != null) {
                runnable2.run();
            }
        }
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
    }

    public void toggleWear() {
        toggleWear(false);
    }

    public void toggleWear(boolean z) {
        TL_stars.TL_starGiftUnique uniqueGift = getUniqueGift();
        if (uniqueGift == null) {
            return;
        }
        MessagesController.getGlobalMainSettings().edit().putInt("statusgiftpage", 3).apply();
        final boolean z2 = !isWorn();
        if (isWorn()) {
            MessagesController.getInstance(this.currentAccount).updateEmojiStatus(getDialogId(), new TLRPC.TL_emojiStatusEmpty(), null);
        } else {
            final long dialogId = getDialogId();
            if (dialogId >= 0) {
                if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
                    BulletinFactory.of(this.bottomBulletinContainer, this.resourcesProvider).createSimpleBulletinDetail(R.raw.star_premium_2, AndroidUtilities.premiumText(LocaleController.getString(R.string.Gift2ActionWearNeededPremium), new Runnable() {
                        @Override
                        public final void run() {
                            StarGiftSheet.this.lambda$toggleWear$6();
                        }
                    })).show();
                    return;
                }
            } else if (!z) {
                final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                this.button.setLoading(true);
                MessagesController.getInstance(this.currentAccount).getBoostsController().getBoostsStats(dialogId, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        StarGiftSheet.this.lambda$toggleWear$9(messagesController, dialogId, (TL_stories.TL_premium_boostsStatus) obj);
                    }
                });
                return;
            }
            TLRPC.TL_inputEmojiStatusCollectible tL_inputEmojiStatusCollectible = new TLRPC.TL_inputEmojiStatusCollectible();
            tL_inputEmojiStatusCollectible.collectible_id = uniqueGift.id;
            MessagesController.getInstance(this.currentAccount).updateEmojiStatus(getDialogId(), tL_inputEmojiStatusCollectible, uniqueGift);
        }
        this.topView.buttons[1].set(z2 ? R.drawable.filled_crown_off : R.drawable.filled_crown_on, LocaleController.getString(z2 ? R.string.Gift2ActionWearOff : R.string.Gift2ActionWear), true);
        if (this.onlyWearInfo) {
            lambda$new$0();
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$toggleWear$10(z2);
            }
        };
        if (this.currentPage.is(0)) {
            runnable.run();
        } else {
            switchPage(0, true, runnable);
        }
        this.button.setText(LocaleController.getString(R.string.OK), true);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarGiftSheet.this.lambda$toggleWear$11(view);
            }
        });
    }
}
