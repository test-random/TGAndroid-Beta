package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ReplacementSpan;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.FilledTabsView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.PeerColorActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.Stars.StarGiftPatterns;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.StoriesUtilities;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class PeerColorActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final float[] particles = {-18.0f, -24.66f, 24.0f, 0.4f, 5.33f, -53.0f, 28.0f, 0.38f, -4.0f, -86.0f, 19.0f, 0.18f, 31.0f, -30.0f, 21.0f, 0.35f, 12.0f, -3.0f, 24.0f, 0.18f, 30.0f, -73.0f, 19.0f, 0.3f, 43.0f, -101.0f, 16.0f, 0.1f, -50.0f, 1.33f, 20.0f, 0.22f, -58.0f, -33.0f, 24.0f, 0.22f, -35.0f, -62.0f, 25.0f, 0.22f, -59.0f, -88.0f, 19.0f, 0.18f, -86.0f, -61.0f, 19.0f, 0.1f, -90.0f, -14.33f, 19.66f, 0.18f};
    private FrameLayout actionBarContainer;
    private boolean applying;
    private boolean applyingName;
    private boolean applyingProfile;
    private ImageView backButton;
    private BaseFragment bulletinFragment;
    private View changeDayNightView;
    private ValueAnimator changeDayNightViewAnimator;
    private float changeDayNightViewProgress;
    private ColoredActionBar colorBar;
    private FrameLayout contentView;
    private final SparseIntArray currentColors = new SparseIntArray();
    private ImageView dayNightItem;
    private final long dialogId;
    private boolean forceDark;
    private final StarsController.GiftsList gifts;
    private final boolean isChannel;
    private boolean isDark;
    public boolean loading;
    private final Theme.MessageDrawable msgInDrawable;
    private final Theme.MessageDrawable msgInDrawableSelected;
    public Page namePage;
    private Theme.ResourcesProvider parentResourcesProvider;
    public Page profilePage;
    private boolean startAtProfile;
    private RLottieDrawable sunDrawable;
    private FilledTabsView tabsView;
    private SimpleTextView titleView;
    private ViewPagerFixed viewPager;

    public static class ChangeNameColorCell extends View {
        private final Text buttonText;
        private PeerColorDrawable color1Drawable;
        private PeerColorDrawable color2Drawable;
        private final int currentAccount;
        private final Drawable drawable;
        private final boolean isChannelOrGroup;
        private final boolean isGroup;
        private LevelLock lock;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private Text userText;
        private final Paint userTextBackgroundPaint;
        private int userTextColorKey;

        public ChangeNameColorCell(int i, long j, Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            int i2;
            int i3;
            this.userTextBackgroundPaint = new Paint(1);
            this.userTextColorKey = -1;
            MessagesController messagesController = MessagesController.getInstance(i);
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
            this.currentAccount = i;
            boolean z = j < 0;
            this.isChannelOrGroup = z;
            boolean z2 = z && !ChatObject.isChannelAndNotMegaGroup(chat);
            this.isGroup = z2;
            this.resourcesProvider = resourcesProvider;
            Drawable mutate = context.getResources().getDrawable(R.drawable.menu_edit_appearance).mutate();
            this.drawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), PorterDuff.Mode.SRC_IN));
            CharSequence string = LocaleController.getString(z ? z2 ? R.string.ChangeGroupAppearance : R.string.ChangeChannelNameColor2 : R.string.ChangeUserNameColor);
            if (z && !z2 && MessagesController.getInstance(i).getMainSettings().getInt("boostingappearance", 0) < 3) {
                MessagesController.PeerColors peerColors = messagesController.peerColors;
                if (peerColors != null) {
                    int min = Math.min(Integer.MAX_VALUE, peerColors.maxLevel());
                    int max = Math.max(0, messagesController.peerColors.maxLevel());
                    i2 = Math.min(min, messagesController.peerColors.minLevel());
                    i3 = Math.max(max, messagesController.peerColors.minLevel());
                } else {
                    i2 = Integer.MAX_VALUE;
                    i3 = 0;
                }
                int min2 = Math.min(i2, messagesController.channelBgIconLevelMin);
                int min3 = Math.min(i3, messagesController.channelBgIconLevelMin);
                MessagesController.PeerColors peerColors2 = messagesController.profilePeerColors;
                if (peerColors2 != null) {
                    int min4 = Math.min(min2, peerColors2.maxLevel());
                    int max2 = Math.max(min3, messagesController.profilePeerColors.maxLevel());
                    min2 = Math.min(min4, messagesController.profilePeerColors.minLevel());
                    min3 = Math.max(max2, messagesController.profilePeerColors.minLevel());
                }
                int min5 = Math.min(min2, messagesController.channelProfileIconLevelMin);
                int max3 = Math.max(min3, messagesController.channelProfileIconLevelMin);
                int min6 = Math.min(min5, messagesController.channelEmojiStatusLevelMin);
                int max4 = Math.max(max3, messagesController.channelEmojiStatusLevelMin);
                int min7 = Math.min(min6, messagesController.channelWallpaperLevelMin);
                int max5 = Math.max(max4, messagesController.channelWallpaperLevelMin);
                int min8 = Math.min(min7, messagesController.channelCustomWallpaperLevelMin);
                int max6 = Math.max(max5, messagesController.channelCustomWallpaperLevelMin);
                int i4 = chat != null ? chat.level : 0;
                if (i4 < max6) {
                    this.lock = new LevelLock(context, true, Math.max(i4, min8), resourcesProvider);
                }
            }
            if (z && this.lock == null) {
                string = TextCell.applyNewSpan(string);
            }
            this.buttonText = new Text(string, 16.0f);
            updateColors();
        }

        private int rtl(int i) {
            return LocaleController.isRTL ? getMeasuredWidth() - i : i;
        }

        @Override
        protected void dispatchDraw(android.graphics.Canvas r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.ChangeNameColorCell.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void set(org.telegram.tgnet.TLRPC.Chat r5, boolean r6) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.ChangeNameColorCell.set(org.telegram.tgnet.TLRPC$Chat, boolean):void");
        }

        public void set(org.telegram.tgnet.TLRPC.User r6) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.ChangeNameColorCell.set(org.telegram.tgnet.TLRPC$User):void");
        }

        public void updateColors() {
            int i;
            this.drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.isChannelOrGroup ? Theme.key_windowBackgroundWhiteGrayIcon : Theme.key_windowBackgroundWhiteBlueText4, this.resourcesProvider), PorterDuff.Mode.SRC_IN));
            this.buttonText.setColor(Theme.getColor(this.isChannelOrGroup ? Theme.key_windowBackgroundWhiteBlackText : Theme.key_windowBackgroundWhiteBlueText4, this.resourcesProvider));
            if (this.userText == null || this.userTextBackgroundPaint == null || (i = this.userTextColorKey) == -1) {
                return;
            }
            int color = Theme.getColor(i, this.resourcesProvider);
            this.userText.setColor(color);
            this.userTextBackgroundPaint.setColor(Theme.multAlpha(color, 0.1f));
        }
    }

    public static class ColoredActionBar extends View {
        private LinearGradient backgroundGradient;
        private int backgroundGradientColor1;
        private int backgroundGradientColor2;
        private int backgroundGradientHeight;
        private final Paint backgroundPaint;
        public int color1;
        private final AnimatedColor color1Animated;
        public int color2;
        private final AnimatedColor color2Animated;
        private int defaultColor;
        protected boolean ignoreMeasure;
        public boolean isDefault;
        private float progressToGradient;
        private final Theme.ResourcesProvider resourcesProvider;

        public ColoredActionBar(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.progressToGradient = 0.0f;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.color1Animated = new AnimatedColor(this, 350L, cubicBezierInterpolator);
            this.color2Animated = new AnimatedColor(this, 350L, cubicBezierInterpolator);
            this.backgroundPaint = new Paint(1);
            this.resourcesProvider = resourcesProvider;
            this.defaultColor = Theme.getColor(Theme.key_actionBarDefault, resourcesProvider);
            setColor(-1, -1, false);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            int i = this.color1Animated.set(this.color1);
            int i2 = this.color2Animated.set(this.color2);
            if (this.backgroundGradient == null || this.backgroundGradientColor1 != i || this.backgroundGradientColor2 != i2 || this.backgroundGradientHeight != getHeight()) {
                int height = getHeight();
                this.backgroundGradientHeight = height;
                this.backgroundGradientColor2 = i2;
                this.backgroundGradientColor1 = i;
                LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, height, new int[]{i2, i}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                this.backgroundGradient = linearGradient;
                this.backgroundPaint.setShader(linearGradient);
                onUpdateColor();
            }
            if (this.progressToGradient < 1.0f) {
                canvas.drawColor(this.defaultColor);
            }
            float f = this.progressToGradient;
            if (f > 0.0f) {
                this.backgroundPaint.setAlpha((int) (f * 255.0f));
                canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.backgroundPaint);
            }
        }

        public int getActionBarButtonColor() {
            int i = Theme.key_actionBarDefaultIcon;
            return ColorUtils.blendARGB(Theme.getColor(i, this.resourcesProvider), this.isDefault ? Theme.getColor(i, this.resourcesProvider) : -1, this.progressToGradient);
        }

        public int getColor() {
            return ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider), ColorUtils.blendARGB(this.color1Animated.get(), this.color2Animated.get(), 0.75f), this.progressToGradient);
        }

        public int getTabsViewBackgroundColor() {
            int i = Theme.key_actionBarDefault;
            return ColorUtils.blendARGB(AndroidUtilities.computePerceivedBrightness(Theme.getColor(i, this.resourcesProvider)) > 0.721f ? Theme.getColor(Theme.key_actionBarDefaultIcon, this.resourcesProvider) : Theme.adaptHSV(Theme.getColor(i, this.resourcesProvider), 0.08f, -0.08f), AndroidUtilities.computePerceivedBrightness(ColorUtils.blendARGB(this.color1Animated.get(), this.color2Animated.get(), 0.75f)) > 0.721f ? Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon, this.resourcesProvider) : Theme.adaptHSV(ColorUtils.blendARGB(this.color1Animated.get(), this.color2Animated.get(), 0.75f), 0.08f, -0.08f), this.progressToGradient);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            if (!this.ignoreMeasure) {
                i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(144.0f), 1073741824);
            }
            super.onMeasure(i, i2);
        }

        protected void onUpdateColor() {
        }

        public void setColor(int i, int i2, boolean z) {
            MessagesController.PeerColors peerColors;
            MessagesController.PeerColor peerColor = null;
            if (i2 >= 0 && i >= 0 && (peerColors = MessagesController.getInstance(i).profilePeerColors) != null) {
                peerColor = peerColors.getColor(i2);
            }
            setColor(peerColor, z);
        }

        public void setColor(MessagesController.PeerColor peerColor, boolean z) {
            this.isDefault = false;
            if (peerColor == null) {
                this.isDefault = true;
                int color = Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider);
                this.color2 = color;
                this.color1 = color;
            } else {
                Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
                boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
                this.color1 = peerColor.getBgColor1(isDark);
                this.color2 = peerColor.getBgColor2(isDark);
            }
            if (!z) {
                this.color1Animated.set(this.color1, true);
                this.color2Animated.set(this.color2, true);
            }
            invalidate();
        }

        public void setProgressToGradient(float f) {
            if (Math.abs(this.progressToGradient - f) > 0.001f) {
                this.progressToGradient = f;
                onUpdateColor();
                invalidate();
            }
        }

        public void updateColors() {
            this.defaultColor = Theme.getColor(Theme.key_actionBarDefault, this.resourcesProvider);
            onUpdateColor();
            invalidate();
        }
    }

    public static class GiftCell extends FrameLayout {
        public TL_stars.starGiftAttributeBackdrop backdrop;
        public final FrameLayout card;
        public final GiftSheet.CardBackground cardBackground;
        public long id;
        public final BackupImageView imageView;
        private TLRPC.Document lastDocument;
        private long lastDocumentId;
        public TL_stars.starGiftAttributePattern pattern;

        public GiftCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            FrameLayout frameLayout = new FrameLayout(context);
            this.card = frameLayout;
            GiftSheet.CardBackground cardBackground = new GiftSheet.CardBackground(frameLayout, resourcesProvider, false);
            this.cardBackground = cardBackground;
            frameLayout.setBackground(cardBackground);
            addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
            ScaleStateListAnimator.apply(frameLayout, 0.025f, 1.25f);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(80, 80.0f, 17, 0.0f, 12.0f, 0.0f, 12.0f));
        }

        private void setSticker(TLRPC.Document document, Object obj) {
            if (document == null) {
                this.imageView.clearImage();
                this.lastDocument = null;
                this.lastDocumentId = 0L;
            } else {
                if (this.lastDocument == document) {
                    return;
                }
                this.lastDocument = document;
                this.lastDocumentId = document.id;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, AndroidUtilities.dp(100.0f));
                this.imageView.setImage(ImageLocation.getForDocument(document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "100_100", DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 0.3f), obj);
            }
        }

        public long getGiftId() {
            return this.id;
        }

        public void set(int i, TL_stars.TL_starGiftUnique tL_starGiftUnique) {
            this.id = tL_starGiftUnique.id;
            boolean z = i % 3 == 1;
            setPadding(z ? AndroidUtilities.dp(4.0f) : 0, 0, z ? AndroidUtilities.dp(4.0f) : 0, 0);
            setSticker(tL_starGiftUnique.getDocument(), tL_starGiftUnique);
            this.backdrop = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributeBackdrop.class);
            this.pattern = (TL_stars.starGiftAttributePattern) StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributePattern.class);
            this.cardBackground.setBackdrop(this.backdrop);
            this.cardBackground.setPattern(this.pattern);
        }

        public void setSelected(boolean z, boolean z2) {
            this.cardBackground.setSelected(z, z2);
            float f = z ? 0.9f : 1.0f;
            if (z2) {
                this.imageView.animate().scaleX(f).scaleY(f).start();
                return;
            }
            this.imageView.animate().cancel();
            this.imageView.setScaleX(f);
            this.imageView.setScaleY(f);
        }
    }

    public static class LevelLock extends Drawable {
        private final PremiumGradient.PremiumGradientTools gradientTools;
        private final Drawable lock;
        private final float lockScale;
        private final Theme.ResourcesProvider resourcesProvider;
        private final Text text;

        public LevelLock(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            this(context, false, i, resourcesProvider);
        }

        public LevelLock(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
            this.lockScale = 0.875f;
            this.resourcesProvider = resourcesProvider;
            this.text = new Text(LocaleController.formatPluralString(z ? "BoostLevelPlus" : "BoostLevel", i, new Object[0]), 12.0f, AndroidUtilities.bold());
            Drawable mutate = context.getResources().getDrawable(R.drawable.mini_switch_lock).mutate();
            this.lock = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            this.gradientTools = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, -1, -1, -1, resourcesProvider);
        }

        @Override
        public void draw(Canvas canvas) {
            int i = getBounds().left;
            int centerY = getBounds().centerY();
            RectF rectF = AndroidUtilities.rectTmp;
            float f = centerY;
            rectF.set(i, f - (getIntrinsicHeight() / 2.0f), getIntrinsicWidth() + i, (getIntrinsicHeight() / 2.0f) + f);
            this.gradientTools.gradientMatrix(rectF);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.gradientTools.paint);
            this.lock.setBounds(AndroidUtilities.dp(3.33f) + i, (int) (f - ((this.lock.getIntrinsicHeight() * 0.875f) / 2.0f)), (int) (AndroidUtilities.dp(3.33f) + i + (this.lock.getIntrinsicWidth() * 0.875f)), (int) (((this.lock.getIntrinsicHeight() * 0.875f) / 2.0f) + f));
            this.lock.draw(canvas);
            this.text.draw(canvas, i + AndroidUtilities.dp(3.66f) + (this.lock.getIntrinsicWidth() * 0.875f), f, -1, 1.0f);
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.33f);
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) (AndroidUtilities.dp(9.66f) + (this.lock.getIntrinsicWidth() * 0.875f) + this.text.getWidth());
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public class Page extends FrameLayout {
        private int actionBarHeight;
        private ButtonWithCounterView button;
        private FrameLayout buttonContainer;
        private CharSequence buttonLocked;
        int buttonRow;
        private CharSequence buttonUnlocked;
        int clearRow;
        int colorPickerRow;
        int giftsCount;
        int giftsEndRow;
        int giftsHeaderRow;
        int giftsInfoRow;
        int giftsLoadingEndRow;
        int giftsLoadingStartRow;
        int giftsStartRow;
        int iconRow;
        int info2Row;
        int infoRow;
        private GridLayoutManager layoutManager;
        private RecyclerView.Adapter listAdapter;
        private RecyclerListView listView;
        private ThemePreviewMessagesCell messagesCellPreview;
        private PeerColorGrid peerColorPicker;
        int previewRow;
        private ProfilePreview profilePreview;
        int rowCount;
        private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
        private int selectedColor;
        private long selectedEmoji;
        private TLRPC.TL_emojiStatusCollectible selectedEmojiCollectible;
        private SetReplyIconCell setReplyIconCell;
        int shadowRow;
        private final int type;
        final ArrayList uniqueGifts;

        public class AnonymousClass4 extends RecyclerListView.SelectionAdapter {
            final Context val$context;
            final PeerColorActivity val$this$0;
            final int val$type;

            AnonymousClass4(PeerColorActivity peerColorActivity, Context context, int i) {
                this.val$this$0 = peerColorActivity;
                this.val$context = context;
                this.val$type = i;
            }

            public void lambda$onCreateViewHolder$0(Integer num) {
                Page.this.selectedColor = num.intValue();
                Page.this.selectedEmojiCollectible = null;
                Page.this.updateProfilePreview(true);
                Page.this.updateMessages();
                if (Page.this.setReplyIconCell != null) {
                    Page.this.setReplyIconCell.invalidate();
                }
                Page page = PeerColorActivity.this.profilePage;
                if (page == null || page.profilePreview == null) {
                    return;
                }
                PeerColorActivity peerColorActivity = PeerColorActivity.this;
                if (peerColorActivity.namePage != null) {
                    peerColorActivity.profilePage.profilePreview.overrideAvatarColor(PeerColorActivity.this.namePage.selectedColor);
                }
            }

            @Override
            public int getItemCount() {
                return Page.this.rowCount;
            }

            @Override
            public int getItemViewType(int i) {
                Page page = Page.this;
                if (i == page.previewRow) {
                    return 0;
                }
                if (i != page.infoRow && i != page.giftsInfoRow && i != page.info2Row && i != page.shadowRow) {
                    if (i == page.colorPickerRow) {
                        return 1;
                    }
                    if (i == page.iconRow) {
                        return 3;
                    }
                    if (i == page.buttonRow) {
                        return 5;
                    }
                    if (i == page.clearRow) {
                        return 6;
                    }
                    if (i == page.giftsHeaderRow) {
                        return 7;
                    }
                    if (i >= page.giftsStartRow && i < page.giftsEndRow) {
                        return 8;
                    }
                    if (i >= page.giftsLoadingStartRow && i < page.giftsLoadingEndRow) {
                        return 9;
                    }
                    if (i == getItemCount() - 1) {
                        return 4;
                    }
                }
                return 2;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 6 || viewHolder.getItemViewType() == 8;
            }

            @Override
            public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r6, int r7) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.Page.AnonymousClass4.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                View view2;
                View view3;
                switch (i) {
                    case 0:
                        ThemePreviewMessagesCell themePreviewMessagesCell = Page.this.messagesCellPreview = new ThemePreviewMessagesCell(Page.this.getContext(), ((BaseFragment) PeerColorActivity.this).parentLayout, 3, PeerColorActivity.this.dialogId, ((BaseFragment) PeerColorActivity.this).resourceProvider);
                        themePreviewMessagesCell.setImportantForAccessibility(4);
                        themePreviewMessagesCell.fragment = PeerColorActivity.this;
                        view2 = themePreviewMessagesCell;
                        break;
                    case 1:
                        PeerColorGrid peerColorGrid = Page.this.peerColorPicker = new PeerColorGrid(Page.this.getContext(), this.val$type, ((BaseFragment) PeerColorActivity.this).currentAccount, ((BaseFragment) PeerColorActivity.this).resourceProvider);
                        peerColorGrid.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        peerColorGrid.setSelected(Page.this.selectedColor, false);
                        peerColorGrid.setOnColorClick(new Utilities.Callback() {
                            @Override
                            public final void run(Object obj) {
                                PeerColorActivity.Page.AnonymousClass4.this.lambda$onCreateViewHolder$0((Integer) obj);
                            }
                        });
                        view = peerColorGrid;
                        view2 = view;
                        break;
                    case 2:
                    default:
                        view2 = new TextInfoPrivacyCell(Page.this.getContext(), PeerColorActivity.this.getResourceProvider());
                        break;
                    case 3:
                        Page page = Page.this;
                        Page page2 = Page.this;
                        SetReplyIconCell setReplyIconCell = page.setReplyIconCell = new SetReplyIconCell(page2.getContext());
                        setReplyIconCell.update(false);
                        view = setReplyIconCell;
                        view2 = view;
                        break;
                    case 4:
                        View view4 = new View(Page.this.getContext()) {
                            @Override
                            protected void onMeasure(int i2, int i3) {
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(16.0f), 1073741824));
                            }
                        };
                        view4.setBackground(Theme.getThemedDrawableByKey(Page.this.getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        view2 = view4;
                        break;
                    case 5:
                        view2 = new View(Page.this.getContext()) {
                            @Override
                            protected void onMeasure(int i2, int i3) {
                                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(76.0f), 1073741824));
                            }
                        };
                        break;
                    case 6:
                        view3 = new TextCell(Page.this.getContext(), PeerColorActivity.this.getResourceProvider());
                        view3.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        view2 = view3;
                        break;
                    case 7:
                        view3 = new HeaderCell(Page.this.getContext(), ((BaseFragment) PeerColorActivity.this).resourceProvider);
                        view3.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                        view2 = view3;
                        break;
                    case 8:
                        view2 = new GiftCell(Page.this.getContext(), ((BaseFragment) PeerColorActivity.this).resourceProvider);
                        break;
                    case 9:
                        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.val$context, ((BaseFragment) PeerColorActivity.this).resourceProvider);
                        flickerLoadingView.setIsSingleCell(true);
                        flickerLoadingView.setViewType(35);
                        view2 = flickerLoadingView;
                        break;
                }
                return new RecyclerListView.Holder(view2);
            }

            @Override
            public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
                super.onViewAttachedToWindow(viewHolder);
                if (viewHolder.getItemViewType() == 8) {
                    GiftCell giftCell = (GiftCell) viewHolder.itemView;
                    int adapterPosition = viewHolder.getAdapterPosition();
                    Page page = Page.this;
                    int i = adapterPosition - page.giftsStartRow;
                    if (i < 0 || i >= page.uniqueGifts.size()) {
                        return;
                    }
                    TL_stars.TL_starGiftUnique tL_starGiftUnique = (TL_stars.TL_starGiftUnique) Page.this.uniqueGifts.get(i);
                    giftCell.set(i, tL_starGiftUnique);
                    giftCell.setSelected(Page.this.selectedEmojiCollectible != null && Page.this.selectedEmojiCollectible.collectible_id == tL_starGiftUnique.id, false);
                }
            }
        }

        public class SetReplyIconCell extends FrameLayout {
            private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable imageDrawable;
            private Text offText;
            private TextView textView;

            public SetReplyIconCell(Context context) {
                super(context);
                TextView textView;
                int i;
                setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                TextView textView2 = new TextView(context);
                this.textView = textView2;
                textView2.setTextSize(1, 16.0f);
                this.textView.setTextColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
                if (Page.this.type == 0) {
                    textView = this.textView;
                    i = PeerColorActivity.this.isChannel ? R.string.ChannelReplyIcon : R.string.UserReplyIcon;
                } else {
                    textView = this.textView;
                    i = PeerColorActivity.this.isChannel ? R.string.ChannelProfileIcon : R.string.UserProfileIcon;
                }
                textView.setText(LocaleController.getString(i));
                addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 23, 20.0f, 0.0f, 20.0f, 0.0f));
                this.imageDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, AndroidUtilities.dp(24.0f), 13);
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                updateImageBounds();
                this.imageDrawable.setColor(Integer.valueOf(getColor()));
                Text text = this.offText;
                if (text != null) {
                    text.draw(canvas, (getMeasuredWidth() - this.offText.getWidth()) - AndroidUtilities.dp(19.0f), getMeasuredHeight() / 2.0f, PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4), 1.0f);
                } else {
                    this.imageDrawable.draw(canvas);
                }
            }

            public int getColor() {
                MessagesController.PeerColor color;
                if (Page.this.selectedColor < 0) {
                    PeerColorActivity peerColorActivity = PeerColorActivity.this;
                    int i = Theme.key_actionBarDefault;
                    return AndroidUtilities.computePerceivedBrightness(peerColorActivity.getThemedColor(i)) > 0.8f ? Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, ((BaseFragment) PeerColorActivity.this).resourceProvider) : AndroidUtilities.computePerceivedBrightness(PeerColorActivity.this.getThemedColor(i)) < 0.2f ? Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultTitle, ((BaseFragment) PeerColorActivity.this).resourceProvider), 0.5f) : Theme.blendOver(Theme.getColor(Theme.key_windowBackgroundWhite, ((BaseFragment) PeerColorActivity.this).resourceProvider), Theme.multAlpha(PeerColorActivity.adaptProfileEmojiColor(Theme.getColor(i, ((BaseFragment) PeerColorActivity.this).resourceProvider)), 0.7f));
                }
                if (Page.this.selectedColor < 7) {
                    Page page = Page.this;
                    return PeerColorActivity.this.getThemedColor(Theme.keys_avatar_nameInMessage[page.selectedColor]);
                }
                MessagesController.PeerColors peerColors = Page.this.type == 0 ? MessagesController.getInstance(((BaseFragment) PeerColorActivity.this).currentAccount).peerColors : MessagesController.getInstance(((BaseFragment) PeerColorActivity.this).currentAccount).profilePeerColors;
                return (peerColors == null || (color = peerColors.getColor(Page.this.selectedColor)) == null) ? PeerColorActivity.this.getThemedColor(Theme.keys_avatar_nameInMessage[0]) : color.getColor1();
            }

            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.imageDrawable.attach();
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.imageDrawable.detach();
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
            }

            public void update(boolean z) {
                long j = Page.this.selectedEmoji;
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.imageDrawable;
                if (j != 0) {
                    swapAnimatedEmojiDrawable.set(Page.this.selectedEmoji, z);
                    this.offText = null;
                } else {
                    swapAnimatedEmojiDrawable.set((Drawable) null, z);
                    if (this.offText == null) {
                        this.offText = new Text(LocaleController.getString(PeerColorActivity.this.isChannel ? R.string.ChannelReplyIconOff : R.string.UserReplyIconOff), 16.0f);
                    }
                }
            }

            public void updateColors() {
                this.textView.setTextColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            }

            public void updateImageBounds() {
                this.imageDrawable.setBounds(LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (getWidth() - this.imageDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(21.0f), (getHeight() - this.imageDrawable.getIntrinsicHeight()) / 2, LocaleController.isRTL ? AndroidUtilities.dp(21.0f) + this.imageDrawable.getIntrinsicWidth() : getWidth() - AndroidUtilities.dp(21.0f), (getHeight() + this.imageDrawable.getIntrinsicHeight()) / 2);
            }
        }

        public Page(android.content.Context r14, final int r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.Page.<init>(org.telegram.ui.PeerColorActivity, android.content.Context, int):void");
        }

        public void lambda$new$0(int i, View view, int i2) {
            int i3;
            ProfilePreview profilePreview;
            Page page;
            if (view instanceof SetReplyIconCell) {
                showSelectStatusDialog((SetReplyIconCell) view);
                return;
            }
            if (i2 != this.clearRow) {
                int i4 = this.giftsStartRow;
                if (i2 < i4 || i2 >= this.giftsEndRow || (i3 = i2 - i4) < 0 || i3 >= this.uniqueGifts.size()) {
                    return;
                }
                TL_stars.TL_starGiftUnique tL_starGiftUnique = (TL_stars.TL_starGiftUnique) this.uniqueGifts.get(i3);
                this.selectedEmoji = 0L;
                this.selectedColor = -1;
                this.selectedEmojiCollectible = MessagesController.emojiStatusCollectibleFromGift(tL_starGiftUnique);
                updateProfilePreview(true);
                SetReplyIconCell setReplyIconCell = this.setReplyIconCell;
                if (setReplyIconCell != null) {
                    setReplyIconCell.update(true);
                    return;
                }
                return;
            }
            this.selectedColor = -1;
            this.selectedEmoji = 0L;
            this.selectedEmojiCollectible = null;
            updateMessages();
            if (i == 1) {
                PeerColorActivity.this.namePage.updateMessages();
            }
            SetReplyIconCell setReplyIconCell2 = this.setReplyIconCell;
            if (setReplyIconCell2 != null) {
                setReplyIconCell2.update(true);
            }
            updateProfilePreview(true);
            PeerColorActivity peerColorActivity = PeerColorActivity.this;
            Page page2 = peerColorActivity.profilePage;
            if (page2 == null || (profilePreview = page2.profilePreview) == null || (page = peerColorActivity.namePage) == null) {
                return;
            }
            profilePreview.overrideAvatarColor(page.selectedColor);
        }

        public void lambda$new$1(View view) {
            PeerColorActivity.this.buttonClick();
        }

        public void lambda$updateColors$2(View view) {
            if (view instanceof PeerColorGrid) {
                view.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                ((PeerColorGrid) view).updateColors();
                return;
            }
            if (view instanceof TextCell) {
                view.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                ((TextCell) view).updateColors();
            } else if (view instanceof SetReplyIconCell) {
                view.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                ((SetReplyIconCell) view).updateColors();
            } else if (view instanceof HeaderCell) {
                view.setBackgroundColor(PeerColorActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
            } else if (view instanceof GiftCell) {
                ((GiftCell) view).card.invalidate();
            }
        }

        public void updateButtonY() {
            if (this.buttonContainer == null) {
                return;
            }
            int itemCount = this.listAdapter.getItemCount() - 1;
            boolean z = false;
            int i = 0;
            for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                View childAt = this.listView.getChildAt(i2);
                int childAdapterPosition = this.listView.getChildAdapterPosition(childAt);
                if (childAdapterPosition != -1 && childAdapterPosition <= itemCount) {
                    i = Math.max(i, childAt.getTop());
                    if (childAdapterPosition == itemCount) {
                        z = true;
                    }
                }
            }
            if (!z) {
                i = this.listView.getMeasuredHeight();
            }
            this.buttonContainer.setTranslationY(Math.max(0, i - (this.listView.getMeasuredHeight() - AndroidUtilities.dp(76.0f))));
        }

        public void updateMessages() {
            MessageObject messageObject;
            ThemePreviewMessagesCell themePreviewMessagesCell = this.messagesCellPreview;
            if (themePreviewMessagesCell != null) {
                ChatMessageCell[] cells = themePreviewMessagesCell.getCells();
                for (int i = 0; i < cells.length; i++) {
                    ChatMessageCell chatMessageCell = cells[i];
                    if (chatMessageCell != null && (messageObject = chatMessageCell.getMessageObject()) != null) {
                        PeerColorGrid peerColorGrid = this.peerColorPicker;
                        if (peerColorGrid != null) {
                            messageObject.overrideLinkColor = peerColorGrid.getColorId();
                        }
                        messageObject.overrideLinkEmoji = this.selectedEmoji;
                        cells[i].setAvatar(messageObject);
                        cells[i].invalidate();
                    }
                }
            }
        }

        private void updateRows() {
            this.clearRow = -1;
            this.shadowRow = -1;
            this.giftsHeaderRow = -1;
            this.giftsStartRow = -1;
            this.giftsLoadingStartRow = -1;
            this.giftsLoadingEndRow = -1;
            this.giftsEndRow = -1;
            this.giftsInfoRow = -1;
            this.giftsCount = 0;
            this.uniqueGifts.clear();
            this.rowCount = 0;
            int i = this.type;
            if (i == 0) {
                this.rowCount = 1;
                this.previewRow = 0;
            }
            int i2 = this.rowCount;
            this.colorPickerRow = i2;
            this.iconRow = i2 + 1;
            int i3 = i2 + 3;
            this.rowCount = i3;
            this.infoRow = i2 + 2;
            if (i == 1 && (this.selectedColor >= 0 || this.selectedEmojiCollectible != null)) {
                this.clearRow = i3;
                this.rowCount = i2 + 5;
                this.shadowRow = i2 + 4;
            }
            if (i == 1 && PeerColorActivity.this.gifts != null) {
                int i4 = 3;
                for (int i5 = 0; i5 < PeerColorActivity.this.gifts.gifts.size(); i5++) {
                    TL_stars.StarGift starGift = ((TL_stars.SavedStarGift) PeerColorActivity.this.gifts.gifts.get(i5)).gift;
                    if (starGift instanceof TL_stars.TL_starGiftUnique) {
                        this.uniqueGifts.add((TL_stars.TL_starGiftUnique) starGift);
                        i4--;
                        if (i4 == 0) {
                            i4 = 3;
                        }
                    }
                }
                if (!this.uniqueGifts.isEmpty()) {
                    int i6 = this.rowCount;
                    int i7 = i6 + 1;
                    this.rowCount = i7;
                    this.giftsHeaderRow = i6;
                    this.giftsStartRow = i7;
                    this.rowCount = i7 + this.uniqueGifts.size();
                    this.giftsCount += this.uniqueGifts.size();
                    this.giftsEndRow = this.rowCount;
                    if (PeerColorActivity.this.gifts.loading || !PeerColorActivity.this.gifts.endReached) {
                        int i8 = this.rowCount;
                        this.giftsLoadingStartRow = i8;
                        int i9 = i4 > 0 ? i4 : 3;
                        int i10 = i8 + i9;
                        this.rowCount = i10;
                        this.giftsCount += i9;
                        this.giftsLoadingEndRow = i10;
                    }
                    int i11 = this.rowCount;
                    this.rowCount = i11 + 1;
                    this.giftsInfoRow = i11;
                }
            }
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.buttonRow = i12;
            if (PeerColorActivity.this.gifts == null || !seesLoading()) {
                return;
            }
            PeerColorActivity.this.gifts.load();
        }

        public void checkResetColorButton() {
            int i;
            if (this.type != 1) {
                return;
            }
            int i2 = this.clearRow;
            updateRows();
            if (i2 >= 0 && this.clearRow < 0) {
                this.listAdapter.notifyItemRangeRemoved(i2, 2);
            } else {
                if (i2 >= 0 || (i = this.clearRow) < 0) {
                    return;
                }
                this.listAdapter.notifyItemRangeInserted(i, 2);
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (PeerColorActivity.this.getParentLayout() != null) {
                PeerColorActivity.this.getParentLayout().drawHeaderShadow(canvas, this.actionBarHeight);
            }
        }

        public boolean hasUnsavedChanged() {
            if (PeerColorActivity.this.isChannel) {
                TLRPC.Chat chat = PeerColorActivity.this.getMessagesController().getChat(Long.valueOf(-PeerColorActivity.this.dialogId));
                if (chat == null) {
                    return false;
                }
                return this.type == 0 ? (this.selectedColor == ChatObject.getColorId(chat) && this.selectedEmoji == ChatObject.getEmojiId(chat)) ? false : true : (this.selectedColor == ChatObject.getProfileColorId(chat) && this.selectedEmoji == ChatObject.getProfileEmojiId(chat) && PeerColorActivity.eq(chat.emoji_status, this.selectedEmojiCollectible)) ? false : true;
            }
            TLRPC.User currentUser = PeerColorActivity.this.getUserConfig().getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            return this.type == 0 ? (this.selectedColor == UserObject.getColorId(currentUser) && this.selectedEmoji == UserObject.getEmojiId(currentUser)) ? false : true : (this.selectedColor == UserObject.getProfileColorId(currentUser) && this.selectedEmoji == UserObject.getProfileEmojiId(currentUser) && PeerColorActivity.eq(currentUser.emoji_status, this.selectedEmojiCollectible)) ? false : true;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            if (this.type == 0) {
                this.actionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
                ((ViewGroup.MarginLayoutParams) this.listView.getLayoutParams()).topMargin = this.actionBarHeight;
            } else {
                this.actionBarHeight = AndroidUtilities.dp(144.0f) + AndroidUtilities.statusBarHeight;
                ((ViewGroup.MarginLayoutParams) this.listView.getLayoutParams()).topMargin = this.actionBarHeight;
                ((ViewGroup.MarginLayoutParams) this.profilePreview.getLayoutParams()).height = this.actionBarHeight;
            }
            super.onMeasure(i, i2);
        }

        public void premiumChanged() {
            if (this.button == null || PeerColorActivity.this.isChannel) {
                return;
            }
            this.button.setText(!PeerColorActivity.this.getUserConfig().isPremium() ? this.buttonLocked : this.buttonUnlocked, true);
        }

        public boolean seesLoading() {
            if (this.listView == null) {
                return false;
            }
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                    return true;
                }
            }
            return false;
        }

        public void showSelectStatusDialog(final SetReplyIconCell setReplyIconCell) {
            int i;
            int i2;
            if (this.selectAnimatedEmojiDialog != null || setReplyIconCell == null) {
                return;
            }
            final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
            int min = (int) Math.min(AndroidUtilities.dp(330.0f), AndroidUtilities.displaySize.y * 0.75f);
            int min2 = (int) Math.min(AndroidUtilities.dp(324.0f), AndroidUtilities.displaySize.x * 0.95f);
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = setReplyIconCell.imageDrawable;
            if (setReplyIconCell.imageDrawable != null) {
                setReplyIconCell.imageDrawable.play();
                setReplyIconCell.updateImageBounds();
                Rect rect = AndroidUtilities.rectTmp2;
                rect.set(setReplyIconCell.imageDrawable.getBounds());
                int dp = this.type == 0 ? ((-rect.centerY()) + AndroidUtilities.dp(12.0f)) - min : (-(setReplyIconCell.getHeight() - rect.centerY())) - AndroidUtilities.dp(16.0f);
                i = rect.centerX() - (AndroidUtilities.displaySize.x - min2);
                i2 = dp;
            } else {
                i = 0;
                i2 = 0;
            }
            int i3 = i2;
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog(PeerColorActivity.this, getContext(), true, Integer.valueOf(i), this.type == 0 ? 5 : 7, true, PeerColorActivity.this.getResourceProvider(), this.type == 0 ? 24 : 16, setReplyIconCell.getColor()) {
                @Override
                protected float getScrimDrawableTranslationY() {
                    return 0.0f;
                }

                @Override
                protected void onEmojiSelected(View view, Long l, TLRPC.Document document, TL_stars.TL_starGiftUnique tL_starGiftUnique, Integer num) {
                    if (tL_starGiftUnique != null) {
                        Page.this.selectedEmojiCollectible = MessagesController.emojiStatusCollectibleFromGift(tL_starGiftUnique);
                        Page.this.selectedColor = -1;
                    } else {
                        Page.this.selectedEmoji = l == null ? 0L : l.longValue();
                        Page.this.selectedEmojiCollectible = null;
                    }
                    SetReplyIconCell setReplyIconCell2 = setReplyIconCell;
                    if (setReplyIconCell2 != null) {
                        setReplyIconCell2.update(true);
                    }
                    Page.this.updateProfilePreview(true);
                    Page.this.updateMessages();
                    if (selectAnimatedEmojiDialogWindowArr[0] != null) {
                        Page.this.selectAnimatedEmojiDialog = null;
                        selectAnimatedEmojiDialogWindowArr[0].dismiss();
                    }
                }
            };
            selectAnimatedEmojiDialog.useAccentForPlus = true;
            long j = this.selectedEmoji;
            selectAnimatedEmojiDialog.setSelected(j == 0 ? null : Long.valueOf(j));
            selectAnimatedEmojiDialog.setSaveState(3);
            selectAnimatedEmojiDialog.setScrimDrawable(swapAnimatedEmojiDrawable, setReplyIconCell);
            int i4 = -2;
            SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(selectAnimatedEmojiDialog, i4, i4) {
                @Override
                public void dismiss() {
                    super.dismiss();
                    Page.this.selectAnimatedEmojiDialog = null;
                }
            };
            this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialogWindow;
            selectAnimatedEmojiDialogWindowArr[0] = selectAnimatedEmojiDialogWindow;
            selectAnimatedEmojiDialogWindow.showAsDropDown(setReplyIconCell, 0, i3, (LocaleController.isRTL ? 3 : 5) | 48);
            selectAnimatedEmojiDialogWindowArr[0].dimBehind();
        }

        public void update() {
            updateRows();
            this.listAdapter.notifyDataSetChanged();
        }

        public void updateColors() {
            RecyclerListView recyclerListView = this.listView;
            PeerColorActivity peerColorActivity = PeerColorActivity.this;
            int i = Theme.key_windowBackgroundGray;
            recyclerListView.setBackgroundColor(peerColorActivity.getThemedColor(i));
            ButtonWithCounterView buttonWithCounterView = this.button;
            if (buttonWithCounterView != null) {
                buttonWithCounterView.updateColors();
            }
            ThemePreviewMessagesCell themePreviewMessagesCell = this.messagesCellPreview;
            if (themePreviewMessagesCell != null) {
                themePreviewMessagesCell.invalidate();
            }
            updateProfilePreview(true);
            this.buttonContainer.setBackgroundColor(PeerColorActivity.this.getThemedColor(i));
            AndroidUtilities.forEachViews((RecyclerView) this.listView, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    PeerColorActivity.Page.this.lambda$updateColors$2((View) obj);
                }
            });
        }

        public void updateProfilePreview(boolean z) {
            PeerColorGrid peerColorGrid = this.peerColorPicker;
            if (peerColorGrid != null) {
                peerColorGrid.setSelected(this.selectedColor, z);
            }
            ProfilePreview profilePreview = this.profilePreview;
            if (profilePreview != null) {
                TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible = this.selectedEmojiCollectible;
                if (tL_emojiStatusCollectible != null) {
                    profilePreview.setStatusEmoji(tL_emojiStatusCollectible.document_id, true, z);
                    this.profilePreview.setColor(MessagesController.PeerColor.fromCollectible(this.selectedEmojiCollectible), z);
                    this.profilePreview.setEmoji(this.selectedEmojiCollectible.pattern_document_id, true, z);
                } else {
                    if (DialogObject.isEmojiStatusCollectible(PeerColorActivity.this.dialogId)) {
                        this.profilePreview.setStatusEmoji(0L, false, z);
                    } else {
                        this.profilePreview.setStatusEmoji(DialogObject.getEmojiStatusDocumentId(PeerColorActivity.this.dialogId), DialogObject.isEmojiStatusCollectible(PeerColorActivity.this.dialogId), z);
                    }
                    this.profilePreview.setColor(this.selectedColor, z);
                    this.profilePreview.setEmoji(this.selectedEmoji, false, z);
                }
            }
            if (this.type == 1 && PeerColorActivity.this.colorBar != null) {
                if (this.selectedEmojiCollectible != null) {
                    PeerColorActivity.this.colorBar.setColor(MessagesController.PeerColor.fromCollectible(this.selectedEmojiCollectible), z);
                } else {
                    PeerColorActivity.this.colorBar.setColor(((BaseFragment) PeerColorActivity.this).currentAccount, this.selectedColor, z);
                }
            }
            checkResetColorButton();
            updateSelectedGift();
        }

        public void updateSelectedGift() {
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GiftCell) {
                    GiftCell giftCell = (GiftCell) childAt;
                    TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible = this.selectedEmojiCollectible;
                    giftCell.setSelected(tL_emojiStatusCollectible != null && tL_emojiStatusCollectible.collectible_id == giftCell.getGiftId(), true);
                }
            }
        }
    }

    public static class PeerColorDrawable extends Drawable {
        private final Path clipCirclePath;
        private final Paint color1Paint;
        private final Paint color2Paint;
        private final Path color2Path;
        private final Paint color3Paint;
        private final boolean hasColor3;
        private float radius = AndroidUtilities.dpf2(10.6665f);
        private Paint strokePaint;

        public PeerColorDrawable(int i, int i2, int i3) {
            Paint paint = new Paint(1);
            this.color1Paint = paint;
            Paint paint2 = new Paint(1);
            this.color2Paint = paint2;
            Paint paint3 = new Paint(1);
            this.color3Paint = paint3;
            this.color2Path = new Path();
            this.clipCirclePath = new Path();
            this.hasColor3 = i3 != i;
            paint.setColor(i);
            paint2.setColor(i2);
            paint3.setColor(i3);
            initPath();
        }

        public static PeerColorDrawable from(int i, int i2) {
            if (i2 < 7) {
                return new PeerColorDrawable(Theme.getColor(Theme.keys_avatar_nameInMessage[i2]), Theme.getColor(Theme.keys_avatar_nameInMessage[i2]), Theme.getColor(Theme.keys_avatar_nameInMessage[i2]));
            }
            MessagesController.PeerColors peerColors = MessagesController.getInstance(i).peerColors;
            return from(peerColors == null ? null : peerColors.getColor(i2), false);
        }

        public static PeerColorDrawable from(MessagesController.PeerColor peerColor, boolean z) {
            if (peerColor == null) {
                return new PeerColorDrawable(0, 0, 0);
            }
            return new PeerColorDrawable(peerColor.getColor1(), (!z || peerColor.hasColor6(Theme.isCurrentThemeDark())) ? peerColor.getColor2() : peerColor.getColor1(), z ? peerColor.getColor1() : peerColor.getColor3());
        }

        public static PeerColorDrawable fromProfile(int i, int i2) {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(i).profilePeerColors;
            return from(peerColors == null ? null : peerColors.getColor(i2), true);
        }

        private void initPath() {
            this.clipCirclePath.rewind();
            Path path = this.clipCirclePath;
            float f = this.radius;
            path.addCircle(f, f, f, Path.Direction.CW);
            this.color2Path.rewind();
            this.color2Path.moveTo(this.radius * 2.0f, 0.0f);
            Path path2 = this.color2Path;
            float f2 = this.radius * 2.0f;
            path2.lineTo(f2, f2);
            this.color2Path.lineTo(0.0f, this.radius * 2.0f);
            this.color2Path.close();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(getBounds().centerX() - this.radius, getBounds().centerY() - this.radius);
            Paint paint = this.strokePaint;
            if (paint != null) {
                float f = this.radius;
                canvas.drawCircle(f, f, f, paint);
            }
            canvas.clipPath(this.clipCirclePath);
            canvas.drawPaint(this.color1Paint);
            canvas.drawPath(this.color2Path, this.color2Paint);
            if (this.hasColor3) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(this.radius - AndroidUtilities.dp(3.66f), this.radius - AndroidUtilities.dp(3.66f), this.radius + AndroidUtilities.dp(3.66f), this.radius + AndroidUtilities.dp(3.66f));
                float f2 = this.radius;
                canvas.rotate(45.0f, f2, f2);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(2.33f), AndroidUtilities.dp(2.33f), this.color3Paint);
            }
            canvas.restore();
        }

        @Override
        public int getIntrinsicHeight() {
            return (int) (this.radius * 2.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) (this.radius * 2.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public PeerColorDrawable setRadius(float f) {
            this.radius = f;
            initPath();
            return this;
        }

        public PeerColorDrawable stroke(float f, int i) {
            if (this.strokePaint == null) {
                Paint paint = new Paint(1);
                this.strokePaint = paint;
                paint.setStyle(Paint.Style.STROKE);
            }
            this.strokePaint.setStrokeWidth(f);
            this.strokePaint.setColor(i);
            return this;
        }
    }

    public static class PeerColorGrid extends View {
        private final Paint backgroundPaint;
        private ColorButton[] buttons;
        private final int currentAccount;
        private final Paint dividerPaint;
        private boolean lock;
        private boolean needDivider;
        private Utilities.Callback onColorClick;
        final int[] order;
        private ColorButton pressedButton;
        private final Theme.ResourcesProvider resourcesProvider;
        private int selectedColorId;
        private final int type;

        public class ColorButton {
            private final ButtonBounce bounce;
            private Paint closePaint;
            private Path closePath;
            private boolean hasClose;
            private boolean hasColor2;
            private boolean hasColor3;
            public int id;
            private Drawable lockDrawable;
            private boolean pressed;
            private boolean selected;
            private final AnimatedFloat selectedT;
            private final Paint paint1 = new Paint(1);
            private final Paint paint2 = new Paint(1);
            private final Paint paint3 = new Paint(1);
            private final Path circlePath = new Path();
            private final Path color2Path = new Path();
            private final RectF bounds = new RectF();
            public final RectF clickBounds = new RectF();

            public ColorButton() {
                this.bounce = new ButtonBounce(PeerColorGrid.this);
                this.selectedT = new AnimatedFloat(PeerColorGrid.this, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            }

            protected void draw(Canvas canvas) {
                canvas.save();
                float scale = this.bounce.getScale(0.05f);
                canvas.scale(scale, scale, this.bounds.centerX(), this.bounds.centerY());
                canvas.save();
                this.circlePath.rewind();
                this.circlePath.addCircle(this.bounds.centerX(), this.bounds.centerY(), Math.min(this.bounds.height() / 2.0f, this.bounds.width() / 2.0f), Path.Direction.CW);
                canvas.clipPath(this.circlePath);
                canvas.drawPaint(this.paint1);
                if (this.hasColor2) {
                    this.color2Path.rewind();
                    Path path = this.color2Path;
                    RectF rectF = this.bounds;
                    path.moveTo(rectF.right, rectF.top);
                    Path path2 = this.color2Path;
                    RectF rectF2 = this.bounds;
                    path2.lineTo(rectF2.right, rectF2.bottom);
                    Path path3 = this.color2Path;
                    RectF rectF3 = this.bounds;
                    path3.lineTo(rectF3.left, rectF3.bottom);
                    this.color2Path.close();
                    canvas.drawPath(this.color2Path, this.paint2);
                }
                canvas.restore();
                if (this.hasColor3) {
                    canvas.save();
                    float width = this.bounds.width() * 0.315f;
                    RectF rectF4 = AndroidUtilities.rectTmp;
                    float f = width / 2.0f;
                    rectF4.set(this.bounds.centerX() - f, this.bounds.centerY() - f, this.bounds.centerX() + f, this.bounds.centerY() + f);
                    canvas.rotate(45.0f, this.bounds.centerX(), this.bounds.centerY());
                    canvas.drawRoundRect(rectF4, AndroidUtilities.dp(2.33f), AndroidUtilities.dp(2.33f), this.paint3);
                    canvas.restore();
                }
                float f2 = this.selectedT.set(this.selected);
                if (f2 > 0.0f) {
                    PeerColorGrid.this.backgroundPaint.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
                    PeerColorGrid.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, PeerColorGrid.this.resourcesProvider));
                    canvas.drawCircle(this.bounds.centerX(), this.bounds.centerY(), Math.min(this.bounds.height() / 2.0f, this.bounds.width() / 2.0f) + (PeerColorGrid.this.backgroundPaint.getStrokeWidth() * AndroidUtilities.lerp(0.5f, -2.0f, f2)), PeerColorGrid.this.backgroundPaint);
                }
                if (this.hasClose) {
                    if (PeerColorGrid.this.lock) {
                        if (this.lockDrawable == null) {
                            Drawable drawable = PeerColorGrid.this.getContext().getResources().getDrawable(R.drawable.msg_mini_lock3);
                            this.lockDrawable = drawable;
                            drawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                        }
                        this.lockDrawable.setBounds((int) (this.bounds.centerX() - ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 1.2f)), (int) (this.bounds.centerY() - ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 1.2f)), (int) (this.bounds.centerX() + ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 1.2f)), (int) (this.bounds.centerY() + ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 1.2f)));
                        this.lockDrawable.draw(canvas);
                    } else {
                        if (this.closePath == null) {
                            this.closePath = new Path();
                        }
                        if (this.closePaint == null) {
                            Paint paint = new Paint(1);
                            this.closePaint = paint;
                            paint.setColor(-1);
                            this.closePaint.setStyle(Paint.Style.STROKE);
                            this.closePaint.setStrokeCap(Paint.Cap.ROUND);
                        }
                        this.closePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                        this.closePath.rewind();
                        float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(4.0f), f2);
                        this.closePath.moveTo(this.bounds.centerX() - lerp, this.bounds.centerY() - lerp);
                        this.closePath.lineTo(this.bounds.centerX() + lerp, this.bounds.centerY() + lerp);
                        this.closePath.moveTo(this.bounds.centerX() + lerp, this.bounds.centerY() - lerp);
                        this.closePath.lineTo(this.bounds.centerX() - lerp, this.bounds.centerY() + lerp);
                        canvas.drawPath(this.closePath, this.closePaint);
                    }
                }
                canvas.restore();
            }

            public void layout(RectF rectF) {
                this.bounds.set(rectF);
            }

            public void layoutClickBounds(RectF rectF) {
                this.clickBounds.set(rectF);
            }

            public void set(int i) {
                this.hasColor3 = false;
                this.hasColor2 = false;
                this.paint1.setColor(i);
            }

            public void set(MessagesController.PeerColor peerColor) {
                Paint paint;
                int color;
                if (peerColor == null) {
                    return;
                }
                boolean isCurrentThemeDark = PeerColorGrid.this.resourcesProvider == null ? Theme.isCurrentThemeDark() : PeerColorGrid.this.resourcesProvider.isDark();
                if (PeerColorGrid.this.type != 0) {
                    this.paint1.setColor(peerColor.getColor(0, PeerColorGrid.this.resourcesProvider));
                    this.paint2.setColor(peerColor.hasColor6(isCurrentThemeDark) ? peerColor.getColor(1, PeerColorGrid.this.resourcesProvider) : peerColor.getColor(0, PeerColorGrid.this.resourcesProvider));
                    this.hasColor2 = peerColor.hasColor6(isCurrentThemeDark);
                    this.hasColor3 = false;
                    return;
                }
                if (isCurrentThemeDark && peerColor.hasColor2() && !peerColor.hasColor3()) {
                    this.paint1.setColor(peerColor.getColor(1, PeerColorGrid.this.resourcesProvider));
                    paint = this.paint2;
                    color = peerColor.getColor(0, PeerColorGrid.this.resourcesProvider);
                } else {
                    this.paint1.setColor(peerColor.getColor(0, PeerColorGrid.this.resourcesProvider));
                    paint = this.paint2;
                    color = peerColor.getColor(1, PeerColorGrid.this.resourcesProvider);
                }
                paint.setColor(color);
                this.paint3.setColor(peerColor.getColor(2, PeerColorGrid.this.resourcesProvider));
                this.hasColor2 = peerColor.hasColor2(isCurrentThemeDark);
                this.hasColor3 = peerColor.hasColor3(isCurrentThemeDark);
            }

            public void setClose(boolean z) {
                this.hasClose = z;
            }

            public void setPressed(boolean z) {
                ButtonBounce buttonBounce = this.bounce;
                this.pressed = z;
                buttonBounce.setPressed(z);
            }

            public void setSelected(boolean z, boolean z2) {
                this.selected = z;
                if (!z2) {
                    this.selectedT.set(z, true);
                }
                PeerColorGrid.this.invalidate();
            }
        }

        public PeerColorGrid(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.order = new int[]{5, 3, 1, 0, 2, 4, 6, -1};
            this.dividerPaint = new Paint(1);
            this.needDivider = true;
            this.selectedColorId = 0;
            this.type = i;
            this.currentAccount = i2;
            this.resourcesProvider = resourcesProvider;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.buttons != null) {
                int i = 0;
                while (true) {
                    ColorButton[] colorButtonArr = this.buttons;
                    if (i >= colorButtonArr.length) {
                        break;
                    }
                    colorButtonArr[i].draw(canvas);
                    i++;
                }
            }
            if (this.needDivider) {
                this.dividerPaint.setColor(Theme.getColor(Theme.key_divider, this.resourcesProvider));
                canvas.drawRect(AndroidUtilities.dp(21.0f), getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(21.0f), getMeasuredHeight(), this.dividerPaint);
            }
        }

        @Override
        public boolean dispatchTouchEvent(android.view.MotionEvent r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.PeerColorGrid.dispatchTouchEvent(android.view.MotionEvent):boolean");
        }

        public int getColorId() {
            return this.selectedColorId;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            MessagesController.PeerColors peerColors = this.type == 0 ? messagesController.peerColors : messagesController.profilePeerColors;
            int size2 = peerColors == null ? 0 : peerColors.colors.size();
            int i4 = this.type;
            int i5 = 8;
            int i6 = 2;
            if (i4 == 2) {
                size2 = 8;
            }
            if (i4 != 2 && i4 == 0) {
                i5 = 7;
            }
            float f = size;
            float f2 = i5;
            float f3 = i5 + 1;
            float min = Math.min(AndroidUtilities.dp(54.0f), f / ((f3 * 0.28947f) + f2));
            float min2 = Math.min(0.28947f * min, AndroidUtilities.dp(8.0f));
            float min3 = Math.min(0.31578946f * min, AndroidUtilities.dp(11.33f));
            setMeasuredDimension(size, (int) (((size2 / i5) * min) + ((r13 + 1) * min3)));
            ColorButton[] colorButtonArr = this.buttons;
            if (colorButtonArr == null || colorButtonArr.length != size2) {
                this.buttons = new ColorButton[size2];
                int i7 = 0;
                while (i7 < size2) {
                    this.buttons[i7] = new ColorButton();
                    if (this.type == i6) {
                        ColorButton colorButton = this.buttons[i7];
                        int i8 = this.order[i7];
                        colorButton.id = i8;
                        colorButton.setClose(i8 < 0);
                        ColorButton colorButton2 = this.buttons[i7];
                        int i9 = this.order[i7];
                        if (i9 < 0) {
                            i3 = Theme.key_avatar_backgroundGray;
                        } else {
                            int[] iArr = Theme.keys_avatar_nameInMessage;
                            i3 = iArr[i9 % iArr.length];
                        }
                        colorButton2.set(Theme.getColor(i3, this.resourcesProvider));
                    } else if (peerColors != null && i7 >= 0 && i7 < peerColors.colors.size()) {
                        this.buttons[i7].id = peerColors.colors.get(i7).id;
                        this.buttons[i7].set(peerColors.colors.get(i7));
                    }
                    i7++;
                    i6 = 2;
                }
            }
            float f4 = ((f - ((f2 * min) + (f3 * min2))) / 2.0f) + min2;
            if (this.buttons != null) {
                float f5 = f4;
                float f6 = min3;
                for (int i10 = 0; i10 < this.buttons.length; i10++) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(f5, f6, f5 + min, f6 + min);
                    this.buttons[i10].layout(rectF);
                    rectF.inset((-min2) / 2.0f, (-min3) / 2.0f);
                    this.buttons[i10].layoutClickBounds(rectF);
                    ColorButton colorButton3 = this.buttons[i10];
                    colorButton3.setSelected(colorButton3.id == this.selectedColorId, false);
                    if (i10 % i5 == i5 - 1) {
                        f6 += min + min3;
                        f5 = f4;
                    } else {
                        f5 += min + min2;
                    }
                }
            }
        }

        public void setCloseAsLock(boolean z) {
            this.lock = z;
        }

        public void setDivider(boolean z) {
            this.needDivider = z;
            invalidate();
        }

        public void setOnColorClick(Utilities.Callback<Integer> callback) {
            this.onColorClick = callback;
        }

        public void setSelected(int i, boolean z) {
            this.selectedColorId = i;
            if (this.buttons == null) {
                return;
            }
            int i2 = 0;
            while (true) {
                ColorButton[] colorButtonArr = this.buttons;
                if (i2 >= colorButtonArr.length) {
                    return;
                }
                ColorButton colorButton = colorButtonArr[i2];
                colorButton.setSelected(colorButton.id == i, z);
                i2++;
            }
        }

        public void updateColors() {
            ColorButton colorButton;
            int i;
            if (this.buttons == null) {
                return;
            }
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            MessagesController.PeerColors peerColors = this.type == 0 ? messagesController.peerColors : messagesController.profilePeerColors;
            int i2 = 0;
            while (true) {
                ColorButton[] colorButtonArr = this.buttons;
                if (i2 >= colorButtonArr.length) {
                    invalidate();
                    return;
                }
                int i3 = this.type;
                if (i3 == 2) {
                    ColorButton colorButton2 = colorButtonArr[i2];
                    int i4 = this.order[i2];
                    colorButton2.id = i4;
                    colorButton2.setClose(i4 < 0);
                    colorButton = this.buttons[i2];
                    int i5 = this.order[i2];
                    if (i5 < 0) {
                        i = Theme.key_avatar_backgroundGray;
                    } else {
                        int[] iArr = Theme.keys_avatar_nameInMessage;
                        i = iArr[i5 % iArr.length];
                    }
                } else if (i2 >= 7 || i3 != 0) {
                    if (peerColors != null && i2 >= 0 && i2 < peerColors.colors.size()) {
                        this.buttons[i2].id = peerColors.colors.get(i2).id;
                        this.buttons[i2].set(peerColors.colors.get(i2));
                    }
                    i2++;
                } else {
                    colorButton = colorButtonArr[i2];
                    int i6 = this.order[i2];
                    colorButton.id = i6;
                    i = Theme.keys_avatar_nameInMessage[i6];
                }
                colorButton.set(Theme.getColor(i, this.resourcesProvider));
                i2++;
            }
        }
    }

    public static class PeerColorSpan extends ReplacementSpan {
        public PeerColorDrawable drawable;
        private int size = AndroidUtilities.dp(21.0f);

        public PeerColorSpan(boolean z, int i, int i2) {
            this.drawable = z ? PeerColorDrawable.fromProfile(i, i2) : PeerColorDrawable.from(i, i2);
        }

        @Override
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            PeerColorDrawable peerColorDrawable = this.drawable;
            if (peerColorDrawable != null) {
                int i6 = (i3 + i5) / 2;
                int i7 = this.size;
                peerColorDrawable.setBounds((int) (AndroidUtilities.dp(3.0f) + f), i6 - this.size, (int) (f + AndroidUtilities.dp(5.0f) + i7), i6 + i7);
                this.drawable.draw(canvas);
            }
        }

        @Override
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return AndroidUtilities.dp(3.0f) + this.size + AndroidUtilities.dp(3.0f);
        }

        public PeerColorSpan setSize(int i) {
            PeerColorDrawable peerColorDrawable = this.drawable;
            if (peerColorDrawable != null) {
                peerColorDrawable.setRadius(i / 2.0f);
                this.size = i;
            }
            return this;
        }
    }

    public static class ProfilePreview extends FrameLayout {
        protected final AvatarDrawable avatarDrawable;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable botVerificationEmoji;
        private final int currentAccount;
        private final long dialogId;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji;
        private AnimatedFloat emojiCollectible;
        protected final ImageReceiver imageReceiver;
        private final boolean isChannel;
        private boolean isEmojiCollectible;
        private boolean isForum;
        private MessagesController.PeerColor peerColor;
        private final RectF rectF;
        private final Theme.ResourcesProvider resourcesProvider;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusEmoji;
        private final StoriesUtilities.StoryGradientTools storyGradient;
        protected final SimpleTextView subtitleView;
        protected final SimpleTextView titleView;

        public ProfilePreview(Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            CharSequence userName;
            long botVerificationIcon;
            TLRPC.EmojiStatus emojiStatus;
            long j2;
            SimpleTextView simpleTextView;
            String string;
            int i2;
            SimpleTextView simpleTextView2;
            String formatPluralStringComma;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageReceiver = imageReceiver;
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            this.avatarDrawable = avatarDrawable;
            this.emoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, false, AndroidUtilities.dp(20.0f), 13);
            this.storyGradient = new StoriesUtilities.StoryGradientTools((View) this, false);
            this.emojiCollectible = new AnimatedFloat(this, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.rectF = new RectF();
            this.currentAccount = i;
            this.dialogId = j;
            this.resourcesProvider = resourcesProvider;
            boolean z = j < 0;
            this.isChannel = z;
            SimpleTextView simpleTextView3 = new SimpleTextView(context) {
                @Override
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    ProfilePreview.this.statusEmoji.attach();
                }

                @Override
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    ProfilePreview.this.statusEmoji.detach();
                }
            };
            this.titleView = simpleTextView3;
            this.botVerificationEmoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(simpleTextView3, AndroidUtilities.dp(17.0f));
            this.statusEmoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(simpleTextView3, AndroidUtilities.dp(24.0f));
            simpleTextView3.setLeftDrawableOutside(true);
            simpleTextView3.setRightDrawableOutside(true);
            simpleTextView3.setTextColor(-1);
            simpleTextView3.setTextSize(20);
            simpleTextView3.setTypeface(AndroidUtilities.bold());
            simpleTextView3.setScrollNonFitText(true);
            addView(simpleTextView3, LayoutHelper.createFrame(-1, -2.0f, 83, 97.0f, 0.0f, 16.0f, 50.33f));
            SimpleTextView simpleTextView4 = new SimpleTextView(context);
            this.subtitleView = simpleTextView4;
            simpleTextView4.setTextSize(14);
            simpleTextView4.setTextColor(-2130706433);
            simpleTextView4.setScrollNonFitText(true);
            addView(simpleTextView4, LayoutHelper.createFrame(-1, -2.0f, 83, 97.0f, 0.0f, 16.0f, 30.66f));
            imageReceiver.setRoundRadius(AndroidUtilities.dp(54.0f));
            if (z) {
                TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
                userName = chat == null ? "" : chat.title;
                avatarDrawable.setInfo(i, chat);
                imageReceiver.setForUserOrChat(chat, avatarDrawable);
                botVerificationIcon = DialogObject.getBotVerificationIcon(chat);
                if (chat != null) {
                    emojiStatus = chat.emoji_status;
                    j2 = DialogObject.getEmojiStatusDocumentId(emojiStatus);
                }
                j2 = 0;
            } else {
                TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
                userName = UserObject.getUserName(currentUser);
                avatarDrawable.setInfo(i, currentUser);
                imageReceiver.setForUserOrChat(currentUser, avatarDrawable);
                botVerificationIcon = DialogObject.getBotVerificationIcon(currentUser);
                if (currentUser != null) {
                    emojiStatus = currentUser.emoji_status;
                    j2 = DialogObject.getEmojiStatusDocumentId(emojiStatus);
                }
                j2 = 0;
            }
            try {
                userName = Emoji.replaceEmoji(userName, null, false);
            } catch (Exception unused) {
            }
            this.titleView.setText(userName);
            if (botVerificationIcon != 0) {
                this.botVerificationEmoji.set(botVerificationIcon, false);
                this.titleView.setLeftDrawable(this.botVerificationEmoji);
            }
            if (j2 != 0) {
                this.statusEmoji.set(j2, false);
                this.titleView.setRightDrawable(this.statusEmoji);
            }
            if (this.isChannel) {
                long j3 = -j;
                TLRPC.Chat chat2 = MessagesController.getInstance(i).getChat(Long.valueOf(j3));
                TLRPC.ChatFull chatFull = MessagesController.getInstance(i).getChatFull(j3);
                if (chatFull == null || chatFull.participants_count <= 0) {
                    if (chat2 != null && chat2.participants_count > 0) {
                        if (ChatObject.isChannelAndNotMegaGroup(chat2)) {
                            simpleTextView2 = this.subtitleView;
                            formatPluralStringComma = LocaleController.formatPluralStringComma("Subscribers", chat2.participants_count);
                        } else {
                            simpleTextView2 = this.subtitleView;
                            formatPluralStringComma = LocaleController.formatPluralStringComma("Members", chat2.participants_count);
                        }
                        simpleTextView2.setText(formatPluralStringComma);
                        setWillNotDraw(false);
                    }
                    boolean isPublic = ChatObject.isPublic(chat2);
                    if (ChatObject.isChannelAndNotMegaGroup(chat2)) {
                        simpleTextView = this.subtitleView;
                        i2 = isPublic ? R.string.ChannelPublic : R.string.ChannelPrivate;
                    } else {
                        simpleTextView = this.subtitleView;
                        i2 = isPublic ? R.string.MegaPublic : R.string.MegaPrivate;
                    }
                    string = LocaleController.getString(i2).toLowerCase();
                } else if (ChatObject.isChannelAndNotMegaGroup(chat2)) {
                    simpleTextView = this.subtitleView;
                    string = LocaleController.formatPluralStringComma("Subscribers", chatFull.participants_count);
                } else {
                    simpleTextView = this.subtitleView;
                    string = LocaleController.formatPluralStringComma("Members", chatFull.participants_count);
                }
            } else {
                simpleTextView = this.subtitleView;
                string = LocaleController.getString(R.string.Online);
            }
            simpleTextView.setText(string);
            setWillNotDraw(false);
        }

        private int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.rectF.set(AndroidUtilities.dp(20.33f), getHeight() - AndroidUtilities.dp(78.66f), AndroidUtilities.dp(20.33f) + AndroidUtilities.dp(53.33f), getHeight() - AndroidUtilities.dp(25.33f));
            this.imageReceiver.setRoundRadius(AndroidUtilities.dp(this.isForum ? 18.0f : 54.0f));
            this.imageReceiver.setImageCoords(this.rectF);
            this.imageReceiver.draw(canvas);
            float width = (this.rectF.width() / 2.0f) + AndroidUtilities.dp(4.0f);
            float dp = AndroidUtilities.dp(this.isForum ? 22.0f : 58.0f);
            canvas.drawRoundRect(this.rectF.centerX() - width, this.rectF.centerY() - width, this.rectF.centerX() + width, this.rectF.centerY() + width, dp, dp, this.storyGradient.getPaint(this.rectF));
            StarGiftPatterns.drawProfilePattern(canvas, this.emoji, getWidth(), getHeight(), 1.0f, this.emojiCollectible.set(this.isEmojiCollectible));
            super.dispatchDraw(canvas);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.emoji.attach();
            this.imageReceiver.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.emoji.detach();
            this.imageReceiver.onDetachedFromWindow();
        }

        public void overrideAvatarColor(int i) {
            int themedColor;
            int i2;
            if (i >= 14) {
                MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                MessagesController.PeerColors peerColors = messagesController != null ? messagesController.peerColors : null;
                MessagesController.PeerColor color = peerColors != null ? peerColors.getColor(i) : null;
                if (color != null) {
                    int color1 = color.getColor1();
                    themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getPeerColorIndex(color1)]);
                    i2 = Theme.keys_avatar_background2[AvatarDrawable.getPeerColorIndex(color1)];
                } else {
                    long j = i;
                    themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j)]);
                    i2 = Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j)];
                }
            } else {
                long j2 = i;
                themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j2)]);
                i2 = Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j2)];
            }
            this.avatarDrawable.setColor(themedColor, getThemedColor(i2));
            invalidate();
        }

        public void setColor(int i, boolean z) {
            MessagesController.PeerColors peerColors = MessagesController.getInstance(this.currentAccount).profilePeerColors;
            setColor(peerColors == null ? null : peerColors.getColor(i), z);
        }

        public void setColor(MessagesController.PeerColor peerColor, boolean z) {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
            int multAlpha;
            this.peerColor = peerColor;
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
            if (peerColor != null) {
                int i = peerColor.patternColor;
                if (i != 0) {
                    this.emoji.setColor(Integer.valueOf(i));
                } else {
                    this.emoji.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(peerColor.getBgColor1(isDark))));
                }
                this.statusEmoji.setColor(Integer.valueOf(ColorUtils.blendARGB(peerColor.getStoryColor1(Theme.isCurrentThemeDark()), -1, 0.25f)));
                this.botVerificationEmoji.setColor(Integer.valueOf(ColorUtils.blendARGB(peerColor.getStoryColor1(Theme.isCurrentThemeDark()), -1, 0.25f)));
                int blendARGB = ColorUtils.blendARGB(peerColor.getStoryColor1(isDark), peerColor.getStoryColor2(isDark), 0.5f);
                int i2 = Theme.key_actionBarDefault;
                if (Theme.hasHue(getThemedColor(i2))) {
                    this.subtitleView.setTextColor(Theme.changeColorAccent(getThemedColor(i2), blendARGB, getThemedColor(Theme.key_avatar_subtitleInProfileBlue), isDark, blendARGB));
                } else {
                    this.subtitleView.setTextColor(blendARGB);
                }
                this.titleView.setTextColor(-1);
            } else {
                int i3 = Theme.key_actionBarDefault;
                if (AndroidUtilities.computePerceivedBrightness(getThemedColor(i3)) > 0.8f) {
                    swapAnimatedEmojiDrawable = this.emoji;
                    multAlpha = getThemedColor(Theme.key_windowBackgroundWhiteBlueText);
                } else if (AndroidUtilities.computePerceivedBrightness(getThemedColor(i3)) < 0.2f) {
                    swapAnimatedEmojiDrawable = this.emoji;
                    multAlpha = Theme.multAlpha(getThemedColor(Theme.key_actionBarDefaultTitle), 0.5f);
                } else {
                    this.emoji.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(getThemedColor(i3))));
                    AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = this.statusEmoji;
                    int i4 = Theme.key_profile_verifiedBackground;
                    swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(Theme.getColor(i4, this.resourcesProvider)));
                    this.botVerificationEmoji.setColor(Integer.valueOf(Theme.getColor(i4, this.resourcesProvider)));
                    this.subtitleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
                    this.titleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
                }
                swapAnimatedEmojiDrawable.setColor(Integer.valueOf(multAlpha));
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable22 = this.statusEmoji;
                int i42 = Theme.key_profile_verifiedBackground;
                swapAnimatedEmojiDrawable22.setColor(Integer.valueOf(Theme.getColor(i42, this.resourcesProvider)));
                this.botVerificationEmoji.setColor(Integer.valueOf(Theme.getColor(i42, this.resourcesProvider)));
                this.subtitleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
                this.titleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
            }
            this.storyGradient.setColor(peerColor, z);
            invalidate();
        }

        public void setEmoji(long j, boolean z, boolean z2) {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
            int color;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2;
            int multAlpha;
            MessagesController.PeerColor peerColor;
            Theme.ResourcesProvider resourcesProvider;
            int i;
            if (j == 0) {
                this.emoji.set((Drawable) null, z2);
            } else {
                this.emoji.set(j, z2);
            }
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            boolean isDark = resourcesProvider2 != null ? resourcesProvider2.isDark() : Theme.isCurrentThemeDark();
            MessagesController.PeerColor peerColor2 = this.peerColor;
            if (peerColor2 != null) {
                multAlpha = peerColor2.patternColor;
                if (multAlpha != 0) {
                    swapAnimatedEmojiDrawable2 = this.emoji;
                    swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(multAlpha));
                } else {
                    swapAnimatedEmojiDrawable = this.emoji;
                    color = peerColor2.getBgColor1(isDark);
                    swapAnimatedEmojiDrawable.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(color)));
                }
            } else {
                int i2 = Theme.key_actionBarDefault;
                if (AndroidUtilities.computePerceivedBrightness(getThemedColor(i2)) > 0.8f) {
                    swapAnimatedEmojiDrawable2 = this.emoji;
                    multAlpha = getThemedColor(Theme.key_windowBackgroundWhiteBlueText);
                } else if (AndroidUtilities.computePerceivedBrightness(getThemedColor(i2)) < 0.2f) {
                    swapAnimatedEmojiDrawable2 = this.emoji;
                    multAlpha = Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultTitle), 0.5f);
                } else {
                    swapAnimatedEmojiDrawable = this.emoji;
                    color = Theme.getColor(i2);
                    swapAnimatedEmojiDrawable.setColor(Integer.valueOf(PeerColorActivity.adaptProfileEmojiColor(color)));
                }
                swapAnimatedEmojiDrawable2.setColor(Integer.valueOf(multAlpha));
            }
            MessagesController.PeerColor peerColor3 = this.peerColor;
            if (peerColor3 != null) {
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable3 = this.statusEmoji;
                int color2 = peerColor3.getColor(1, this.resourcesProvider);
                if (this.peerColor.hasColor6(isDark)) {
                    peerColor = this.peerColor;
                    resourcesProvider = this.resourcesProvider;
                    i = 4;
                } else {
                    peerColor = this.peerColor;
                    resourcesProvider = this.resourcesProvider;
                    i = 2;
                }
                swapAnimatedEmojiDrawable3.setColor(Integer.valueOf(ColorUtils.blendARGB(color2, peerColor.getColor(i, resourcesProvider), 0.5f)));
            } else {
                this.statusEmoji.setColor(Integer.valueOf(Theme.getColor(Theme.key_profile_verifiedBackground, this.resourcesProvider)));
            }
            this.isEmojiCollectible = z;
            if (!z2) {
                this.emojiCollectible.force(z);
            }
            invalidate();
        }

        public void setForum(boolean z) {
            if (this.isForum != z) {
                invalidate();
            }
            this.isForum = z;
        }

        public void setStatusEmoji(long j, boolean z, boolean z2) {
            this.statusEmoji.set(j, z2);
            this.statusEmoji.setParticles(z, z2);
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
            MessagesController.PeerColor peerColor = this.peerColor;
            if (peerColor != null) {
                this.statusEmoji.setColor(Integer.valueOf(ColorUtils.blendARGB(peerColor.getColor2(isDark), this.peerColor.hasColor6(isDark) ? this.peerColor.getColor5(isDark) : this.peerColor.getColor3(isDark), 0.5f)));
            } else {
                this.statusEmoji.setColor(Integer.valueOf(Theme.getColor(Theme.key_profile_verifiedBackground, this.resourcesProvider)));
            }
        }
    }

    public PeerColorActivity(long j) {
        boolean isCurrentThemeDark = Theme.isCurrentThemeDark();
        this.isDark = isCurrentThemeDark;
        this.forceDark = isCurrentThemeDark;
        this.dialogId = j;
        this.isChannel = j != 0;
        if (j >= 0) {
            StarsController.GiftsList giftsList = new StarsController.GiftsList(this.currentAccount, j, false);
            this.gifts = giftsList;
            giftsList.include_limited = false;
            giftsList.include_unlimited = false;
            giftsList.include_unique = true;
            giftsList.load();
        } else {
            this.gifts = null;
        }
        this.resourceProvider = new Theme.ResourcesProvider() {
            @Override
            public void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
                Theme.applyServiceShaderMatrix(i, i2, f, f2);
            }

            @Override
            public ColorFilter getAnimatedEmojiColorFilter() {
                ColorFilter colorFilter;
                colorFilter = Theme.chat_animatedEmojiTextColorFilter;
                return colorFilter;
            }

            @Override
            public int getColor(int i) {
                int indexOfKey = PeerColorActivity.this.currentColors.indexOfKey(i);
                return indexOfKey >= 0 ? PeerColorActivity.this.currentColors.valueAt(indexOfKey) : PeerColorActivity.this.parentResourcesProvider != null ? PeerColorActivity.this.parentResourcesProvider.getColor(i) : Theme.getColor(i);
            }

            @Override
            public int getColorOrDefault(int i) {
                int color;
                color = getColor(i);
                return color;
            }

            @Override
            public int getCurrentColor(int i) {
                int color;
                color = getColor(i);
                return color;
            }

            @Override
            public Drawable getDrawable(String str) {
                return str.equals("drawableMsgIn") ? PeerColorActivity.this.msgInDrawable : str.equals("drawableMsgInSelected") ? PeerColorActivity.this.msgInDrawableSelected : PeerColorActivity.this.parentResourcesProvider != null ? PeerColorActivity.this.parentResourcesProvider.getDrawable(str) : Theme.getThemeDrawable(str);
            }

            @Override
            public Paint getPaint(String str) {
                Paint themePaint;
                themePaint = Theme.getThemePaint(str);
                return themePaint;
            }

            @Override
            public boolean hasGradientService() {
                return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
            }

            @Override
            public boolean isDark() {
                return PeerColorActivity.this.isDark;
            }

            @Override
            public void setAnimatedColor(int i, int i2) {
                Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i, i2);
            }
        };
        this.msgInDrawable = new Theme.MessageDrawable(0, false, false, this.resourceProvider);
        this.msgInDrawableSelected = new Theme.MessageDrawable(0, false, true, this.resourceProvider);
    }

    public static int adaptProfileEmojiColor(int i) {
        return Theme.adaptHSV(i, 0.5f, (AndroidUtilities.computePerceivedBrightness(i) > 0.2f ? 1 : (AndroidUtilities.computePerceivedBrightness(i) == 0.2f ? 0 : -1)) < 0 ? 0.28f : -0.28f);
    }

    private void apply() {
        if (this.applying) {
            return;
        }
        if (this.isChannel || getUserConfig().isPremium()) {
            if (this.isChannel) {
                lambda$onBackPressed$323();
            } else {
                TLRPC.User currentUser = getUserConfig().getCurrentUser();
                if (currentUser.color == null) {
                    TLRPC.TL_peerColor tL_peerColor = new TLRPC.TL_peerColor();
                    currentUser.color = tL_peerColor;
                    tL_peerColor.color = (int) (currentUser.id % 7);
                }
                TL_stars.TL_starGiftUnique tL_starGiftUnique = null;
                if (this.namePage.selectedColor != UserObject.getColorId(currentUser) || this.namePage.selectedEmoji != UserObject.getEmojiId(currentUser)) {
                    this.applyingName = true;
                    TL_account.updateColor updatecolor = new TL_account.updateColor();
                    currentUser.flags2 |= 256;
                    TLRPC.TL_peerColor tL_peerColor2 = currentUser.color;
                    tL_peerColor2.flags |= 1;
                    updatecolor.flags |= 4;
                    int i = this.namePage.selectedColor;
                    tL_peerColor2.color = i;
                    updatecolor.color = i;
                    if (this.namePage.selectedEmoji != 0) {
                        updatecolor.flags |= 1;
                        TLRPC.TL_peerColor tL_peerColor3 = currentUser.color;
                        tL_peerColor3.flags |= 2;
                        long j = this.namePage.selectedEmoji;
                        tL_peerColor3.background_emoji_id = j;
                        updatecolor.background_emoji_id = j;
                    } else {
                        TLRPC.TL_peerColor tL_peerColor4 = currentUser.color;
                        tL_peerColor4.flags &= -3;
                        tL_peerColor4.background_emoji_id = 0L;
                    }
                    getConnectionsManager().sendRequest(updatecolor, null);
                }
                if (this.profilePage.selectedColor != UserObject.getProfileColorId(currentUser) || this.profilePage.selectedEmoji != UserObject.getProfileEmojiId(currentUser)) {
                    this.applyingProfile = true;
                    if (currentUser.profile_color == null) {
                        currentUser.profile_color = new TLRPC.TL_peerColor();
                    }
                    TL_account.updateColor updatecolor2 = new TL_account.updateColor();
                    updatecolor2.for_profile = true;
                    currentUser.flags2 |= 512;
                    if (this.profilePage.selectedColor < 0) {
                        currentUser.profile_color.flags &= -2;
                    } else {
                        TLRPC.TL_peerColor tL_peerColor5 = currentUser.profile_color;
                        tL_peerColor5.flags |= 1;
                        updatecolor2.flags |= 4;
                        int i2 = this.profilePage.selectedColor;
                        tL_peerColor5.color = i2;
                        updatecolor2.color = i2;
                    }
                    if (this.profilePage.selectedEmoji != 0) {
                        updatecolor2.flags |= 1;
                        TLRPC.TL_peerColor tL_peerColor6 = currentUser.profile_color;
                        tL_peerColor6.flags |= 2;
                        long j2 = this.profilePage.selectedEmoji;
                        tL_peerColor6.background_emoji_id = j2;
                        updatecolor2.background_emoji_id = j2;
                    } else {
                        TLRPC.TL_peerColor tL_peerColor7 = currentUser.profile_color;
                        tL_peerColor7.flags &= -3;
                        tL_peerColor7.background_emoji_id = 0L;
                    }
                    getConnectionsManager().sendRequest(updatecolor2, null);
                }
                if (!eq(currentUser.emoji_status, this.profilePage.selectedEmojiCollectible) && (this.profilePage.selectedEmojiCollectible != null || DialogObject.isEmojiStatusCollectible(currentUser.emoji_status))) {
                    ?? tL_emojiStatusEmpty = new TLRPC.TL_emojiStatusEmpty();
                    if (this.profilePage.selectedEmojiCollectible != null) {
                        long j3 = this.profilePage.selectedEmojiCollectible.collectible_id;
                        int i3 = 0;
                        while (true) {
                            if (i3 >= this.profilePage.uniqueGifts.size()) {
                                break;
                            }
                            TL_stars.TL_starGiftUnique tL_starGiftUnique2 = (TL_stars.TL_starGiftUnique) this.profilePage.uniqueGifts.get(i3);
                            if (tL_starGiftUnique2.id == j3) {
                                tL_starGiftUnique = tL_starGiftUnique2;
                                break;
                            }
                            i3++;
                        }
                    }
                    if (tL_starGiftUnique != null) {
                        tL_emojiStatusEmpty = new TLRPC.TL_inputEmojiStatusCollectible();
                        tL_emojiStatusEmpty.collectible_id = tL_starGiftUnique.id;
                    }
                    getMessagesController().updateEmojiStatus(0L, tL_emojiStatusEmpty, tL_starGiftUnique);
                }
                getMessagesController().putUser(currentUser, false);
                getUserConfig().saveConfig(true);
                lambda$onBackPressed$323();
                showBulletin();
            }
            this.applying = true;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_EMOJI_STATUS));
        }
    }

    public void buttonClick() {
        if (this.loading) {
            return;
        }
        if (this.isChannel) {
            lambda$onBackPressed$323();
        } else if (!getUserConfig().isPremium()) {
            showDialog(new PremiumFeatureBottomSheet(this, 23, true));
            return;
        }
        apply();
        lambda$onBackPressed$323();
        showBulletin();
    }

    public static boolean eq(TLRPC.EmojiStatus emojiStatus, TLRPC.TL_emojiStatusCollectible tL_emojiStatusCollectible) {
        boolean z = emojiStatus instanceof TLRPC.TL_emojiStatusCollectible;
        return (tL_emojiStatusCollectible != null) == z && tL_emojiStatusCollectible != null && z && ((TLRPC.TL_emojiStatusCollectible) emojiStatus).collectible_id == tL_emojiStatusCollectible.collectible_id;
    }

    public void lambda$createView$0(Integer num) {
        ViewPagerFixed viewPagerFixed = this.viewPager;
        if (viewPagerFixed != null) {
            viewPagerFixed.scrollToPosition(num.intValue());
        }
    }

    public void lambda$createView$1(View view) {
        if (onBackPressed()) {
            lambda$onBackPressed$323();
        }
    }

    public void lambda$createView$2(View view) {
        toggleTheme();
    }

    public void lambda$showUnsavedAlert$3(AlertDialog alertDialog, int i) {
        lambda$onBackPressed$323();
    }

    public void lambda$showUnsavedAlert$4(AlertDialog alertDialog, int i) {
        buttonClick();
    }

    public static boolean lambda$toggleTheme$5(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$toggleTheme$6() {
        this.isDark = !this.isDark;
        updateThemeColors();
        setForceDark(this.isDark, true);
        updateColors();
    }

    private void showBulletin() {
        BulletinFactory of;
        PeerColorDrawable fromProfile;
        int i;
        Bulletin createSimpleBulletin;
        if (this.bulletinFragment != null) {
            if (!this.applyingName || (this.applyingProfile && getCurrentPage() != this.namePage)) {
                if (this.applyingProfile && (!this.applyingName || getCurrentPage() == this.profilePage)) {
                    if (this.profilePage.selectedColor < 0) {
                        long j = this.profilePage.selectedEmoji;
                        BulletinFactory of2 = BulletinFactory.of(this.bulletinFragment);
                        if (j != 0) {
                            createSimpleBulletin = of2.createStaticEmojiBulletin(AnimatedEmojiDrawable.findDocument(this.currentAccount, this.profilePage.selectedEmoji), LocaleController.getString(this.isChannel ? R.string.ChannelProfileColorEmojiApplied : R.string.UserProfileColorEmojiApplied));
                        } else {
                            createSimpleBulletin = of2.createSimpleBulletin(R.raw.contact_check, LocaleController.getString(this.isChannel ? R.string.ChannelProfileColorResetApplied : R.string.UserProfileColorResetApplied));
                        }
                        createSimpleBulletin.show();
                    } else {
                        of = BulletinFactory.of(this.bulletinFragment);
                        fromProfile = PeerColorDrawable.fromProfile(this.currentAccount, this.profilePage.selectedColor);
                        i = this.isChannel ? R.string.ChannelProfileColorApplied : R.string.UserProfileColorApplied;
                    }
                }
                this.bulletinFragment = null;
            }
            of = BulletinFactory.of(this.bulletinFragment);
            fromProfile = PeerColorDrawable.from(this.currentAccount, this.namePage.selectedColor);
            i = this.isChannel ? R.string.ChannelColorApplied : R.string.UserColorApplied;
            createSimpleBulletin = of.createSimpleBulletin(fromProfile, LocaleController.getString(i));
            createSimpleBulletin.show();
            this.bulletinFragment = null;
        }
    }

    private void showUnsavedAlert() {
        if (getVisibleDialog() != null) {
            return;
        }
        AlertDialog create = new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString(this.isChannel ? R.string.ChannelColorUnsaved : R.string.UserColorUnsaved)).setMessage(LocaleController.getString(this.isChannel ? R.string.ChannelColorUnsavedMessage : R.string.UserColorUnsavedMessage)).setNegativeButton(LocaleController.getString(R.string.Dismiss), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                PeerColorActivity.this.lambda$showUnsavedAlert$3(alertDialog, i);
            }
        }).setPositiveButton(LocaleController.getString(R.string.ApplyTheme), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                PeerColorActivity.this.lambda$showUnsavedAlert$4(alertDialog, i);
            }
        }).create();
        showDialog(create);
        ((TextView) create.getButton(-2)).setTextColor(getThemedColor(Theme.key_text_RedBold));
    }

    public void updateColors() {
        this.contentView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundGray));
        SimpleTextView simpleTextView = this.titleView;
        if (simpleTextView != null) {
            simpleTextView.setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
        }
        this.namePage.updateColors();
        this.profilePage.updateColors();
        ColoredActionBar coloredActionBar = this.colorBar;
        if (coloredActionBar != null) {
            coloredActionBar.updateColors();
        }
        setNavigationBarColor(getNavigationBarColor());
    }

    public static CharSequence withLevelLock(CharSequence charSequence, int i) {
        if (i <= 0) {
            return charSequence;
        }
        Context context = ApplicationLoader.applicationContext;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        spannableStringBuilder.append((CharSequence) "  L");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(new LevelLock(context, i, null));
        coloredImageSpan.setTranslateY(AndroidUtilities.dp(1.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    @Override
    public View createView(Context context) {
        FrameLayout frameLayout;
        View view;
        FrameLayout.LayoutParams createFrame;
        this.namePage = new Page(context, 0);
        this.profilePage = new Page(context, 1);
        this.actionBar.setCastShadows(false);
        this.actionBar.setVisibility(8);
        this.actionBar.setAllowOverlayTitle(false);
        FrameLayout frameLayout2 = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                if (PeerColorActivity.this.actionBarContainer != null) {
                    ((ViewGroup.MarginLayoutParams) PeerColorActivity.this.actionBarContainer.getLayoutParams()).height = ActionBar.getCurrentActionBarHeight();
                    ((ViewGroup.MarginLayoutParams) PeerColorActivity.this.actionBarContainer.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(i, i2);
            }
        };
        frameLayout2.setFitsSystemWindows(true);
        this.colorBar = new ColoredActionBar(context, this.resourceProvider) {
            private int lastBtnColor = 0;

            @Override
            protected void onUpdateColor() {
                PeerColorActivity.this.updateLightStatusBar();
                updateActionBarButtonsColor();
                if (PeerColorActivity.this.tabsView != null) {
                    PeerColorActivity.this.tabsView.setBackgroundColor(getTabsViewBackgroundColor());
                }
            }

            public void updateActionBarButtonsColor() {
                int actionBarButtonColor = getActionBarButtonColor();
                if (this.lastBtnColor != actionBarButtonColor) {
                    if (PeerColorActivity.this.backButton != null) {
                        this.lastBtnColor = actionBarButtonColor;
                        PeerColorActivity.this.backButton.setColorFilter(new PorterDuffColorFilter(actionBarButtonColor, PorterDuff.Mode.SRC_IN));
                    }
                    if (PeerColorActivity.this.dayNightItem != null) {
                        this.lastBtnColor = actionBarButtonColor;
                        PeerColorActivity.this.dayNightItem.setColorFilter(new PorterDuffColorFilter(actionBarButtonColor, PorterDuff.Mode.SRC_IN));
                    }
                }
            }
        };
        this.profilePage.updateProfilePreview(false);
        frameLayout2.addView(this.colorBar, LayoutHelper.createFrame(-1, -2, 55));
        ViewPagerFixed viewPagerFixed = new ViewPagerFixed(context) {
            @Override
            protected void onTabAnimationUpdate(boolean z) {
                PeerColorActivity.this.tabsView.setSelected(PeerColorActivity.this.viewPager.getPositionAnimated());
                PeerColorActivity.this.colorBar.setProgressToGradient(PeerColorActivity.this.viewPager.getPositionAnimated());
            }
        };
        this.viewPager = viewPagerFixed;
        viewPagerFixed.setAdapter(new ViewPagerFixed.Adapter() {
            @Override
            public void bindView(View view2, int i, int i2) {
            }

            @Override
            public View createView(int i) {
                if (i == 0) {
                    return PeerColorActivity.this.namePage;
                }
                if (i == 1) {
                    return PeerColorActivity.this.profilePage;
                }
                return null;
            }

            @Override
            public int getItemCount() {
                return 2;
            }

            @Override
            public int getItemViewType(int i) {
                return i;
            }
        });
        frameLayout2.addView(this.viewPager, LayoutHelper.createFrame(-1, -1, 119));
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.actionBarContainer = frameLayout3;
        frameLayout2.addView(frameLayout3, LayoutHelper.createFrame(-1, -2, 55));
        if (this.isChannel) {
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleView = simpleTextView;
            simpleTextView.setText(LocaleController.getString(R.string.ChannelColorTitle2));
            this.titleView.setEllipsizeByGradient(true);
            this.titleView.setTextSize(20);
            this.titleView.setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
            this.titleView.setTypeface(AndroidUtilities.bold());
            frameLayout = this.actionBarContainer;
            view = this.titleView;
            createFrame = LayoutHelper.createFrame(-2, -2.0f, 19, 72.0f, 0.0f, 72.0f, 0.0f);
        } else {
            FilledTabsView filledTabsView = new FilledTabsView(context);
            this.tabsView = filledTabsView;
            filledTabsView.setTabs(LocaleController.getString(this.isChannel ? R.string.ChannelColorTabName : R.string.UserColorTabName), LocaleController.getString(this.isChannel ? R.string.ChannelColorTabProfile : R.string.UserColorTabProfile));
            this.tabsView.onTabSelected(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    PeerColorActivity.this.lambda$createView$0((Integer) obj);
                }
            });
            frameLayout = this.actionBarContainer;
            view = this.tabsView;
            createFrame = LayoutHelper.createFrame(-1, 40, 17);
        }
        frameLayout.addView(view, createFrame);
        if (this.startAtProfile) {
            this.viewPager.setPosition(1);
            FilledTabsView filledTabsView2 = this.tabsView;
            if (filledTabsView2 != null) {
                filledTabsView2.setSelected(1.0f);
            }
            ColoredActionBar coloredActionBar = this.colorBar;
            if (coloredActionBar != null) {
                coloredActionBar.setProgressToGradient(1.0f);
                updateLightStatusBar();
            }
        }
        ImageView imageView = new ImageView(context);
        this.backButton = imageView;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        ImageView imageView2 = this.backButton;
        int i = Theme.key_actionBarWhiteSelector;
        imageView2.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 1));
        this.backButton.setImageResource(R.drawable.ic_ab_back);
        ImageView imageView3 = this.backButton;
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        imageView3.setColorFilter(new PorterDuffColorFilter(-1, mode));
        this.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PeerColorActivity.this.lambda$createView$1(view2);
            }
        });
        this.actionBarContainer.addView(this.backButton, LayoutHelper.createFrame(54, 54, 19));
        int i2 = R.raw.sun;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
        this.sunDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        if (this.isDark) {
            this.sunDrawable.setCurrentFrame(35);
            this.sunDrawable.setCustomEndFrame(36);
        } else {
            this.sunDrawable.setCustomEndFrame(0);
            this.sunDrawable.setCurrentFrame(0);
        }
        this.sunDrawable.beginApplyLayerColors();
        int color = Theme.getColor(Theme.key_chats_menuName);
        this.sunDrawable.setLayerColor("Sunny.**", color);
        this.sunDrawable.setLayerColor("Path 6.**", color);
        this.sunDrawable.setLayerColor("Path.**", color);
        this.sunDrawable.setLayerColor("Path 5.**", color);
        this.sunDrawable.commitApplyLayerColors();
        ImageView imageView4 = new ImageView(context);
        this.dayNightItem = imageView4;
        imageView4.setScaleType(scaleType);
        this.dayNightItem.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 1));
        this.dayNightItem.setColorFilter(new PorterDuffColorFilter(-1, mode));
        this.dayNightItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PeerColorActivity.this.lambda$createView$2(view2);
            }
        });
        this.actionBarContainer.addView(this.dayNightItem, LayoutHelper.createFrame(54, 54, 21));
        this.dayNightItem.setImageDrawable(this.sunDrawable);
        this.colorBar.updateColors();
        this.contentView = frameLayout2;
        this.fragmentView = frameLayout2;
        return frameLayout2;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 != this.currentAccount) {
            return;
        }
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            this.namePage.premiumChanged();
            this.profilePage.premiumChanged();
        } else if (i == NotificationCenter.starUserGiftsLoaded) {
            this.profilePage.update();
        }
    }

    public Page getCurrentPage() {
        return this.viewPager.getCurrentPosition() == 0 ? this.namePage : this.profilePage;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                PeerColorActivity.this.updateColors();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText2, Theme.key_listSelector, Theme.key_windowBackgroundGray, Theme.key_windowBackgroundWhiteGrayText4, Theme.key_text_RedRegular, Theme.key_windowBackgroundChecked, Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
    }

    public boolean hasUnsavedChanged() {
        return this.namePage.hasUnsavedChanged() || this.profilePage.hasUnsavedChanged();
    }

    @Override
    public boolean isLightStatusBar() {
        ColoredActionBar coloredActionBar = this.colorBar;
        return coloredActionBar == null ? super.isLightStatusBar() : ColorUtils.calculateLuminance(coloredActionBar.getColor()) > 0.699999988079071d;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        if (!this.isChannel && hasUnsavedChanged() && getUserConfig().isPremium()) {
            return false;
        }
        return super.isSwipeBackEnabled(motionEvent);
    }

    @Override
    public boolean onBackPressed() {
        if (this.isChannel || !hasUnsavedChanged() || !getUserConfig().isPremium()) {
            return super.onBackPressed();
        }
        showUnsavedAlert();
        return false;
    }

    @Override
    public void onFragmentClosed() {
        super.onFragmentClosed();
        Bulletin.removeDelegate(this);
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.starUserGiftsLoaded);
        Bulletin.addDelegate(this, new Bulletin.Delegate() {
            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
            }

            @Override
            public boolean clipWithGradient(int i) {
                return true;
            }

            @Override
            public int getBottomOffset(int i) {
                return AndroidUtilities.dp(62.0f);
            }

            @Override
            public int getTopOffset(int i) {
                return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
            }

            @Override
            public void onBottomOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
            }

            @Override
            public void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override
            public void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }
        });
        getMediaDataController().loadReplyIcons();
        if (MessagesController.getInstance(this.currentAccount).peerColors == null && BuildVars.DEBUG_PRIVATE_VERSION) {
            MessagesController.getInstance(this.currentAccount).loadAppConfig(true);
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.starUserGiftsLoaded);
    }

    public void setForceDark(boolean z, boolean z2) {
        if (this.forceDark == z) {
            return;
        }
        this.forceDark = z;
        if (z2) {
            RLottieDrawable rLottieDrawable = this.sunDrawable;
            rLottieDrawable.setCustomEndFrame(z ? rLottieDrawable.getFramesCount() : 0);
            RLottieDrawable rLottieDrawable2 = this.sunDrawable;
            if (rLottieDrawable2 != null) {
                rLottieDrawable2.start();
                return;
            }
            return;
        }
        int framesCount = z ? this.sunDrawable.getFramesCount() - 1 : 0;
        this.sunDrawable.setCurrentFrame(framesCount, false, true);
        this.sunDrawable.setCustomEndFrame(framesCount);
        ImageView imageView = this.dayNightItem;
        if (imageView != null) {
            imageView.invalidate();
        }
    }

    public PeerColorActivity setOnApplied(BaseFragment baseFragment) {
        this.bulletinFragment = baseFragment;
        return this;
    }

    @Override
    public void setResourceProvider(Theme.ResourcesProvider resourcesProvider) {
        this.parentResourcesProvider = resourcesProvider;
    }

    public PeerColorActivity startOnProfile() {
        this.startAtProfile = true;
        return this;
    }

    public void toggleTheme() {
        FrameLayout frameLayout = (FrameLayout) getParentActivity().getWindow().getDecorView();
        final Bitmap createBitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(createBitmap);
        this.dayNightItem.setAlpha(0.0f);
        frameLayout.draw(canvas);
        this.dayNightItem.setAlpha(1.0f);
        final Paint paint = new Paint(1);
        paint.setColor(-16777216);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        final Paint paint2 = new Paint(1);
        paint2.setFilterBitmap(true);
        int[] iArr = new int[2];
        this.dayNightItem.getLocationInWindow(iArr);
        final float f = iArr[0];
        final float f2 = iArr[1];
        final float measuredWidth = f + (this.dayNightItem.getMeasuredWidth() / 2.0f);
        final float measuredHeight = f2 + (this.dayNightItem.getMeasuredHeight() / 2.0f);
        final float max = Math.max(createBitmap.getHeight(), createBitmap.getWidth()) + AndroidUtilities.navigationBarHeight;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        paint2.setShader(new BitmapShader(createBitmap, tileMode, tileMode));
        View view = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas2) {
                super.onDraw(canvas2);
                if (PeerColorActivity.this.isDark) {
                    if (PeerColorActivity.this.changeDayNightViewProgress > 0.0f) {
                        canvas.drawCircle(measuredWidth, measuredHeight, max * PeerColorActivity.this.changeDayNightViewProgress, paint);
                    }
                    canvas2.drawBitmap(createBitmap, 0.0f, 0.0f, paint2);
                } else {
                    canvas2.drawCircle(measuredWidth, measuredHeight, max * (1.0f - PeerColorActivity.this.changeDayNightViewProgress), paint2);
                }
                canvas2.save();
                canvas2.translate(f, f2);
                PeerColorActivity.this.dayNightItem.draw(canvas2);
                canvas2.restore();
            }
        };
        this.changeDayNightView = view;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$toggleTheme$5;
                lambda$toggleTheme$5 = PeerColorActivity.lambda$toggleTheme$5(view2, motionEvent);
                return lambda$toggleTheme$5;
            }
        });
        this.changeDayNightViewProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.changeDayNightViewAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean changedNavigationBarColor = false;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PeerColorActivity.this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                PeerColorActivity.this.changeDayNightView.invalidate();
                if (this.changedNavigationBarColor || PeerColorActivity.this.changeDayNightViewProgress <= 0.5f) {
                    return;
                }
                this.changedNavigationBarColor = true;
            }
        });
        this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (PeerColorActivity.this.changeDayNightView != null) {
                    if (PeerColorActivity.this.changeDayNightView.getParent() != null) {
                        ((ViewGroup) PeerColorActivity.this.changeDayNightView.getParent()).removeView(PeerColorActivity.this.changeDayNightView);
                    }
                    PeerColorActivity.this.changeDayNightView = null;
                }
                PeerColorActivity.this.changeDayNightViewAnimator = null;
                super.onAnimationEnd(animator);
            }
        });
        this.changeDayNightViewAnimator.setDuration(400L);
        this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        this.changeDayNightViewAnimator.start();
        frameLayout.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PeerColorActivity.this.lambda$toggleTheme$6();
            }
        });
    }

    public void updateLightStatusBar() {
        if (getParentActivity() == null) {
            return;
        }
        AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), isLightStatusBar());
    }

    public void updateThemeColors() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeerColorActivity.updateThemeColors():void");
    }
}
