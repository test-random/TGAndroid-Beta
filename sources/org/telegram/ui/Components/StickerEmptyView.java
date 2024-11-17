package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class StickerEmptyView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private boolean animateLayoutChange;
    public final ButtonWithCounterView button;
    int colorKey1;
    int currentAccount;
    int keyboardSize;
    private int lastH;
    public LinearLayout linearLayout;
    boolean preventMoving;
    private RadialProgressView progressBar;
    private boolean progressShowing;
    public final View progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    Runnable showProgressRunnable;
    private int stickerType;
    public BackupImageView stickerView;
    public final LinkSpanDrawable.LinksTextView subtitle;
    public final SpoilersTextView title;

    public StickerEmptyView(Context context, View view, int i) {
        this(context, view, i, null);
    }

    public StickerEmptyView(Context context, View view, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.showProgressRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPropertyAnimator scaleX;
                StickerEmptyView stickerEmptyView = StickerEmptyView.this;
                View view2 = stickerEmptyView.progressView;
                if (view2 != null) {
                    if (view2.getVisibility() != 0) {
                        StickerEmptyView.this.progressView.setVisibility(0);
                        StickerEmptyView.this.progressView.setAlpha(0.0f);
                    }
                    StickerEmptyView.this.progressView.animate().setListener(null).cancel();
                    scaleX = StickerEmptyView.this.progressView.animate().alpha(1.0f);
                } else {
                    scaleX = stickerEmptyView.progressBar.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f);
                }
                scaleX.setDuration(150L).start();
            }
        };
        this.colorKey1 = Theme.key_emptyListPlaceholder;
        this.resourcesProvider = resourcesProvider;
        this.progressView = view;
        this.stickerType = i;
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            public void setVisibility(int i2) {
                if (getVisibility() == 8 && i2 == 0) {
                    StickerEmptyView.this.setSticker();
                    if (LiteMode.isEnabled(3)) {
                        StickerEmptyView.this.stickerView.getImageReceiver().startAnimation();
                    }
                } else if (i2 == 8) {
                    StickerEmptyView.this.stickerView.getImageReceiver().clearImage();
                }
                super.setVisibility(i2);
            }
        };
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context);
        this.stickerView = backupImageView;
        backupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StickerEmptyView.this.lambda$new$0(view2);
            }
        });
        SpoilersTextView spoilersTextView = new SpoilersTextView(context);
        this.title = spoilersTextView;
        spoilersTextView.setTypeface(AndroidUtilities.bold());
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        spoilersTextView.setTag(Integer.valueOf(i2));
        spoilersTextView.setTextColor(getThemedColor(i2));
        spoilersTextView.setTextSize(1, 20.0f);
        spoilersTextView.setGravity(17);
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        this.subtitle = linksTextView;
        int i3 = Theme.key_windowBackgroundWhiteGrayText;
        linksTextView.setTag(Integer.valueOf(i3));
        linksTextView.setTextColor(getThemedColor(i3));
        linksTextView.setLinkTextColor(getThemedColor(Theme.key_windowBackgroundWhiteLinkText));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setGravity(17);
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.button = buttonWithCounterView;
        buttonWithCounterView.setVisibility(8);
        this.linearLayout.addView(this.stickerView, LayoutHelper.createLinear(117, 117, 1));
        this.linearLayout.addView(spoilersTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 12, 0, 0));
        this.linearLayout.addView(linksTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
        this.linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 1, 28, 16, 28, 0));
        addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 46.0f, 0.0f, 46.0f, 30.0f));
        if (view == null) {
            RadialProgressView radialProgressView = new RadialProgressView(context, resourcesProvider);
            this.progressBar = radialProgressView;
            radialProgressView.setAlpha(0.0f);
            this.progressBar.setScaleY(0.5f);
            this.progressBar.setScaleX(0.5f);
            addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private int getWhitespaceCount(CharSequence charSequence) {
        int i = 0;
        for (int i2 = 0; i2 < charSequence.length(); i2++) {
            if (Character.isWhitespace(charSequence.charAt(i2))) {
                i++;
            }
        }
        return i;
    }

    public void lambda$new$0(View view) {
        this.stickerView.getImageReceiver().startAnimation();
    }

    public void setSticker() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        TLRPC.Document document;
        int i;
        ImageReceiver imageReceiver;
        int i2 = this.stickerType;
        if (i2 != 0) {
            int i3 = 1;
            if (i2 != 1) {
                TLRPC.Document document2 = null;
                String str = null;
                document2 = null;
                document2 = null;
                if (i2 == 16) {
                    document = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker("👍");
                    tL_messages_stickerSet = null;
                } else {
                    TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
                    if (stickerSetByName == null) {
                        stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
                    }
                    if (stickerSetByName != null && (i = this.stickerType) >= 0 && i < stickerSetByName.documents.size()) {
                        document2 = stickerSetByName.documents.get(this.stickerType);
                    }
                    tL_messages_stickerSet = stickerSetByName;
                    document = document2;
                    str = "130_130";
                }
                if (!LiteMode.isEnabled(3)) {
                    str = str + "_firstframe";
                }
                String str2 = str;
                if (document == null) {
                    MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, tL_messages_stickerSet == null);
                    this.stickerView.getImageReceiver().clearImage();
                    return;
                }
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document.thumbs, this.colorKey1, 0.2f);
                if (svgThumb != null) {
                    svgThumb.overrideWidthAndHeight(512, 512);
                }
                this.stickerView.setImage(ImageLocation.getForDocument(document), str2, "tgs", svgThumb, tL_messages_stickerSet);
                int i4 = this.stickerType;
                if (i4 == 9 || i4 == 0) {
                    imageReceiver = this.stickerView.getImageReceiver();
                } else {
                    imageReceiver = this.stickerView.getImageReceiver();
                    i3 = 2;
                }
                imageReceiver.setAutoRepeat(i3);
                return;
            }
        }
        this.stickerView.setImageDrawable(new RLottieDrawable(R.raw.utyan_empty, "utyan_empty", AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f)));
    }

    public void createButtonLayout(CharSequence charSequence, final Runnable runnable) {
        ((LinearLayout.LayoutParams) this.subtitle.getLayoutParams()).topMargin = AndroidUtilities.dp(12.0f);
        TextView textView = new TextView(getContext());
        textView.setText(charSequence);
        int i = Theme.key_featuredStickers_buttonText;
        textView.setTextColor(Theme.getColor(i, this.resourcesProvider));
        textView.setPadding(AndroidUtilities.dp(45.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(45.0f), AndroidUtilities.dp(12.0f));
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 15.0f);
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            }
        };
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AndroidUtilities.runOnUIThread(runnable, 100L);
            }
        });
        frameLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), ColorUtils.setAlphaComponent(Theme.getColor(i, this.resourcesProvider), 30)));
        ScaleStateListAnimator.apply(frameLayout, 0.05f, 1.5f);
        frameLayout.addView(textView);
        this.linearLayout.setClipChildren(false);
        this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 1, 0, 28, 0, 4));
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals((String) objArr[0]) && getVisibility() == 0) {
            setSticker();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == 0) {
            setSticker();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        super.onLayout(z, i, i2, i3, i4);
        if ((this.animateLayoutChange || this.preventMoving) && (i5 = this.lastH) > 0 && i5 != getMeasuredHeight()) {
            float measuredHeight = (this.lastH - getMeasuredHeight()) / 2.0f;
            LinearLayout linearLayout = this.linearLayout;
            linearLayout.setTranslationY(linearLayout.getTranslationY() + measuredHeight);
            if (!this.preventMoving) {
                this.linearLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250L);
            }
            RadialProgressView radialProgressView = this.progressBar;
            if (radialProgressView != null) {
                radialProgressView.setTranslationY(radialProgressView.getTranslationY() + measuredHeight);
                if (!this.preventMoving) {
                    this.progressBar.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250L);
                }
            }
        }
        this.lastH = getMeasuredHeight();
    }

    public void setAnimateLayoutChange(boolean z) {
        this.animateLayoutChange = z;
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.title.setTag(Integer.valueOf(i));
        this.title.setTextColor(getThemedColor(i));
        this.subtitle.setTag(Integer.valueOf(i2));
        this.subtitle.setTextColor(getThemedColor(i2));
        this.colorKey1 = i3;
    }

    public void setKeyboardHeight(int i, boolean z) {
        if (this.keyboardSize != i) {
            if (getVisibility() != 0) {
                z = false;
            }
            this.keyboardSize = i;
            float dp = (-(i >> 1)) + (i > 0 ? AndroidUtilities.dp(20.0f) : 0);
            if (!z) {
                this.linearLayout.setTranslationY(dp);
                RadialProgressView radialProgressView = this.progressBar;
                if (radialProgressView != null) {
                    radialProgressView.setTranslationY(dp);
                    return;
                }
                return;
            }
            ViewPropertyAnimator translationY = this.linearLayout.animate().translationY(dp);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            translationY.setInterpolator(cubicBezierInterpolator).setDuration(250L);
            RadialProgressView radialProgressView2 = this.progressBar;
            if (radialProgressView2 != null) {
                radialProgressView2.animate().translationY(dp).setInterpolator(cubicBezierInterpolator).setDuration(250L);
            }
        }
    }

    public void setPreventMoving(boolean z) {
        this.preventMoving = z;
        if (z) {
            return;
        }
        this.linearLayout.setTranslationY(0.0f);
        RadialProgressView radialProgressView = this.progressBar;
        if (radialProgressView != null) {
            radialProgressView.setTranslationY(0.0f);
        }
    }

    public void setStickerType(int i) {
        if (this.stickerType != i) {
            this.stickerType = i;
            setSticker();
        }
    }

    public void setSubtitle(CharSequence charSequence) {
        if (getWhitespaceCount(charSequence) > 4 && charSequence.length() > 20) {
            int length = charSequence.length() >> 1;
            int i = -1;
            int i2 = 0;
            for (int i3 = 0; i3 < charSequence.length(); i3++) {
                if (Character.isWhitespace(charSequence.charAt(i3))) {
                    int abs = Math.abs(length - i3);
                    if (i == -1 || abs < i2) {
                        i = i3;
                        i2 = abs;
                    }
                }
            }
            if (i > 0) {
                charSequence = ((Object) charSequence.subSequence(0, i)) + "\n" + ((Object) charSequence.subSequence(i + 1, charSequence.length()));
            }
        }
        this.subtitle.setText(charSequence);
    }

    @Override
    public void setVisibility(int i) {
        ViewPropertyAnimator scaleX;
        if (getVisibility() != i && i == 0) {
            if (this.progressShowing) {
                this.linearLayout.animate().alpha(0.0f).scaleY(0.8f).scaleX(0.8f).setDuration(150L).start();
                this.progressView.animate().setListener(null).cancel();
                this.progressView.setVisibility(0);
                this.progressView.setAlpha(1.0f);
            } else {
                this.linearLayout.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150L).start();
                View view = this.progressView;
                if (view != null) {
                    view.animate().setListener(null).cancel();
                    scaleX = this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            StickerEmptyView.this.progressView.setVisibility(8);
                        }
                    }).alpha(0.0f);
                } else {
                    scaleX = this.progressBar.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f);
                }
                scaleX.setDuration(150L).start();
                this.stickerView.getImageReceiver().startAnimation();
            }
        }
        super.setVisibility(i);
        if (getVisibility() == 0) {
            setSticker();
            return;
        }
        this.lastH = 0;
        this.linearLayout.setAlpha(0.0f);
        this.linearLayout.setScaleX(0.8f);
        this.linearLayout.setScaleY(0.8f);
        View view2 = this.progressView;
        if (view2 != null) {
            view2.animate().setListener(null).cancel();
            this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    StickerEmptyView.this.progressView.setVisibility(8);
                }
            }).alpha(0.0f).setDuration(150L).start();
        } else {
            this.progressBar.setAlpha(0.0f);
            this.progressBar.setScaleX(0.5f);
            this.progressBar.setScaleY(0.5f);
        }
        this.stickerView.getImageReceiver().stopAnimation();
        this.stickerView.getImageReceiver().clearImage();
    }

    public void showProgress(boolean z) {
        showProgress(z, true);
    }

    public void showProgress(boolean z, boolean z2) {
        View view;
        int i;
        ViewPropertyAnimator scaleX;
        if (this.progressShowing != z) {
            this.progressShowing = z;
            if (getVisibility() != 0) {
                return;
            }
            if (z2) {
                if (z) {
                    this.linearLayout.animate().alpha(0.0f).scaleY(0.8f).scaleX(0.8f).setDuration(150L).start();
                    this.showProgressRunnable.run();
                    return;
                }
                this.linearLayout.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150L).start();
                View view2 = this.progressView;
                if (view2 != null) {
                    view2.animate().setListener(null).cancel();
                    scaleX = this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            StickerEmptyView.this.progressView.setVisibility(8);
                        }
                    }).alpha(0.0f);
                } else {
                    scaleX = this.progressBar.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f);
                }
                scaleX.setDuration(150L).start();
                this.stickerView.getImageReceiver().startAnimation();
                return;
            }
            if (z) {
                this.linearLayout.animate().cancel();
                this.linearLayout.setAlpha(0.0f);
                this.linearLayout.setScaleX(0.8f);
                this.linearLayout.setScaleY(0.8f);
                View view3 = this.progressView;
                if (view3 == null) {
                    this.progressBar.setAlpha(1.0f);
                    this.progressBar.setScaleX(1.0f);
                    this.progressBar.setScaleY(1.0f);
                    return;
                } else {
                    view3.animate().setListener(null).cancel();
                    this.progressView.setAlpha(1.0f);
                    view = this.progressView;
                    i = 0;
                }
            } else {
                this.linearLayout.animate().cancel();
                this.linearLayout.setAlpha(1.0f);
                this.linearLayout.setScaleX(1.0f);
                this.linearLayout.setScaleY(1.0f);
                View view4 = this.progressView;
                if (view4 == null) {
                    this.progressBar.setAlpha(0.0f);
                    this.progressBar.setScaleX(0.5f);
                    this.progressBar.setScaleY(0.5f);
                    return;
                } else {
                    view4.animate().setListener(null).cancel();
                    view = this.progressView;
                    i = 8;
                }
            }
            view.setVisibility(i);
        }
    }
}
