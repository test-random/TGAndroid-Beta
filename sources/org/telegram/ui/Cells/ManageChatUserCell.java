package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Stories.StoriesUtilities;

public class ManageChatUserCell extends FrameLayout {
    private final AvatarDrawable avatarDrawable;
    private final BackupImageView avatarImageView;
    private final int currentAccount;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currentStatus;
    private ImageView customImageView;
    private ManageChatUserCellDelegate delegate;
    private int dividerColor;
    private boolean isAdmin;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private final int namePadding;
    private final SimpleTextView nameTextView;
    private boolean needDivider;
    private ImageView optionsButton;
    private final Theme.ResourcesProvider resourcesProvider;
    private int statusColor;
    private int statusOnlineColor;
    private final SimpleTextView statusTextView;
    private final StoriesUtilities.AvatarStoryParams storyAvatarParams;
    private TL_stories.StoryItem storyItem;

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public ManageChatUserCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, null);
    }

    public ManageChatUserCell(Context context, int i, int i2, boolean z, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.dividerColor = -1;
        this.currentAccount = UserConfig.selectedAccount;
        this.storyAvatarParams = new StoriesUtilities.AvatarStoryParams(false);
        this.resourcesProvider = resourcesProvider;
        this.statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider);
        this.statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider);
        this.namePadding = i2;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context) {
            @Override
            public void onDraw(Canvas canvas) {
                if (ManageChatUserCell.this.storyItem == null) {
                    super.onDraw(canvas);
                    return;
                }
                float dp = AndroidUtilities.dp(1.0f);
                ManageChatUserCell.this.storyAvatarParams.originalAvatarRect.set(dp, dp, getMeasuredWidth() - r0, getMeasuredHeight() - r0);
                ManageChatUserCell.this.storyAvatarParams.drawSegments = false;
                ManageChatUserCell.this.storyAvatarParams.animate = false;
                ManageChatUserCell.this.storyAvatarParams.drawInside = true;
                ManageChatUserCell.this.storyAvatarParams.isArchive = false;
                ManageChatUserCell.this.storyAvatarParams.resourcesProvider = resourcesProvider;
                ManageChatUserCell.this.storyAvatarParams.storyItem = ManageChatUserCell.this.storyItem;
                StoriesUtilities.drawAvatarWithStory(ManageChatUserCell.this.storyItem.dialogId, canvas, this.imageReceiver, ManageChatUserCell.this.storyAvatarParams);
            }
        };
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        boolean z2 = LocaleController.isRTL;
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : i + 7, 8.0f, z2 ? i + 7 : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        simpleTextView.setTextSize(17);
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 46.0f : i2 + 68, 11.5f, z3 ? i2 + 68 : 46.0f, 0.0f));
        NotificationCenter.listenEmojiLoading(simpleTextView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        simpleTextView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        boolean z4 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z4 ? 5 : 3) | 48, z4 ? 28.0f : i2 + 68, 34.5f, z4 ? i2 + 68 : 28.0f, 0.0f));
        if (z) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector, resourcesProvider)));
            this.optionsButton.setImageResource(R.drawable.ic_ab_other);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu, resourcesProvider), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(60, 64, (LocaleController.isRTL ? 3 : 5) | 48));
            this.optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ManageChatUserCell.this.lambda$new$0(view);
                }
            });
            this.optionsButton.setContentDescription(LocaleController.getString(R.string.AccDescrUserOptions));
        }
    }

    public void lambda$new$0(View view) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public StoriesUtilities.AvatarStoryParams getStoryAvatarParams() {
        return this.storyAvatarParams;
    }

    public TL_stories.StoryItem getStoryItem() {
        return this.storyItem;
    }

    public long getUserId() {
        Object obj = this.currentObject;
        if (obj instanceof TLRPC.User) {
            return ((TLRPC.User) obj).id;
        }
        return 0L;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            int i = this.dividerColor;
            if (i >= 0) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(i, this.resourcesProvider));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0), getMeasuredHeight() - 1, this.dividerColor >= 0 ? Theme.dividerExtraPaint : Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setCustomImageVisible(boolean z) {
        ImageView imageView = this.customImageView;
        if (imageView == null) {
            return;
        }
        imageView.setVisibility(z ? 0 : 8);
    }

    public void setCustomRightImage(int i) {
        ImageView imageView = new ImageView(getContext());
        this.customImageView = imageView;
        imageView.setImageResource(i);
        this.customImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.customImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_voipgroup_mutedIconUnscrolled, this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
        addView(this.customImageView, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : 5) | 48));
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        SimpleTextView simpleTextView;
        int i;
        float f;
        float f2;
        if (obj == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.currentObject = obj;
        int i2 = 28;
        if (this.optionsButton == null) {
            ImageView imageView = this.customImageView;
            if (imageView != null) {
                boolean z2 = imageView.getVisibility() == 0;
                SimpleTextView simpleTextView2 = this.nameTextView;
                boolean z3 = LocaleController.isRTL;
                simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? z2 ? 54 : 28 : this.namePadding + 68, (charSequence2 == null || charSequence2.length() > 0) ? 11.5f : 20.5f, LocaleController.isRTL ? this.namePadding + 68 : z2 ? 54 : 28, 0.0f));
                simpleTextView = this.statusTextView;
                boolean z4 = LocaleController.isRTL;
                i = (z4 ? 5 : 3) | 48;
                f = z4 ? z2 ? 54 : 28 : this.namePadding + 68;
                if (!z4) {
                    if (z2) {
                        i2 = 54;
                    }
                    f2 = i2;
                }
                f2 = this.namePadding + 68;
            }
            this.needDivider = z;
            setWillNotDraw(!z);
            update(0);
        }
        boolean onOptionsButtonCheck = this.delegate.onOptionsButtonCheck(this, false);
        this.optionsButton.setVisibility(onOptionsButtonCheck ? 0 : 4);
        SimpleTextView simpleTextView3 = this.nameTextView;
        boolean z5 = LocaleController.isRTL;
        simpleTextView3.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, (z5 ? 5 : 3) | 48, z5 ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68, (charSequence2 == null || charSequence2.length() > 0) ? 11.5f : 20.5f, LocaleController.isRTL ? this.namePadding + 68 : onOptionsButtonCheck ? 46 : 28, 0.0f));
        simpleTextView = this.statusTextView;
        boolean z6 = LocaleController.isRTL;
        i = (z6 ? 5 : 3) | 48;
        f = z6 ? onOptionsButtonCheck ? 46 : 28 : this.namePadding + 68;
        if (!z6) {
            if (onOptionsButtonCheck) {
                i2 = 46;
            }
            f2 = i2;
        }
        f2 = this.namePadding + 68;
        simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i, f, 34.5f, f2, 0.0f));
        this.needDivider = z;
        setWillNotDraw(!z);
        update(0);
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public void setDividerColor(int i) {
        this.dividerColor = i;
    }

    public void setIsAdmin(boolean z) {
        this.isAdmin = z;
    }

    public void setNameColor(int i) {
        this.nameTextView.setTextColor(i);
    }

    public void setStatusColors(int i, int i2) {
        this.statusColor = i;
        this.statusOnlineColor = i2;
    }

    public void setStoryItem(TL_stories.StoryItem storyItem, View.OnClickListener onClickListener) {
        this.storyItem = storyItem;
        this.avatarImageView.setOnClickListener(onClickListener);
    }

    public void update(int r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ManageChatUserCell.update(int):void");
    }
}
