package org.telegram.ui.Stories;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Layout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.LaunchActivity;

public class StealthModeAlert extends BottomSheet {
    private final PremiumButtonView button;
    private Listener listener;
    private boolean stealthModeIsActive;
    private int type;
    Runnable updateButtonRunnuble;

    private class ItemCell extends FrameLayout {
        TextView description;
        ImageView imageView;
        TextView textView;

        public ItemCell(Context context) {
            super(context);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(28, 28.0f, 0, 25.0f, 12.0f, 16.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTypeface(AndroidUtilities.bold());
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, ((BottomSheet) StealthModeAlert.this).resourcesProvider));
            this.textView.setTextSize(1, 14.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 0, 68.0f, 8.0f, 16.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.description = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, ((BottomSheet) StealthModeAlert.this).resourcesProvider));
            this.description.setTextSize(1, 14.0f);
            addView(this.description, LayoutHelper.createFrame(-1, -2.0f, 0, 68.0f, 28.0f, 16.0f, 8.0f));
        }
    }

    public interface Listener {
        void onButtonClicked(boolean z);
    }

    public StealthModeAlert(Context context, final float f, final int i, final Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.updateButtonRunnuble = new Runnable() {
            @Override
            public final void run() {
                StealthModeAlert.this.lambda$new$4();
            }
        };
        this.type = i;
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                Bulletin.addDelegate(StealthModeAlert.this.container, new Bulletin.Delegate() {
                    @Override
                    public boolean allowLayoutChanges() {
                        return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
                    }

                    @Override
                    public boolean bottomOffsetAnimated() {
                        return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
                    }

                    @Override
                    public boolean clipWithGradient(int i2) {
                        return Bulletin.Delegate.CC.$default$clipWithGradient(this, i2);
                    }

                    @Override
                    public int getBottomOffset(int i2) {
                        return Bulletin.Delegate.CC.$default$getBottomOffset(this, i2);
                    }

                    @Override
                    public int getTopOffset(int i2) {
                        return (int) (f + AndroidUtilities.dp(58.0f));
                    }

                    @Override
                    public void onBottomOffsetChange(float f2) {
                        Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f2);
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
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                Bulletin.removeDelegate(StealthModeAlert.this.container);
            }
        };
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
        imageView.setImageResource(R.drawable.large_stealth);
        frameLayout.addView(imageView, LayoutHelper.createFrame(80, 80.0f, 1, 0.0f, 18.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 116.0f, 0.0f, 0.0f));
        TextView textView = new TextView(getContext());
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        textView.setText(LocaleController.getString(R.string.StealthModeTitle));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1));
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        simpleTextView.setTextSize(14);
        simpleTextView.setAlignment(Layout.Alignment.ALIGN_CENTER);
        simpleTextView.setMaxLines(100);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        simpleTextView.setText(LocaleController.getString(UserConfig.getInstance(this.currentAccount).isPremium() ? R.string.StealthModeHint : R.string.StealthModePremiumHint));
        linearLayout.addView(simpleTextView, LayoutHelper.createLinear(-2, -2, 1, 36, 10, 36, 0));
        ItemCell itemCell = new ItemCell(getContext());
        itemCell.imageView.setImageResource(R.drawable.msg_stealth_5min);
        itemCell.textView.setText(LocaleController.getString(R.string.HideRecentViews));
        itemCell.description.setText(LocaleController.getString(R.string.HideRecentViewsDescription));
        linearLayout.addView(itemCell, LayoutHelper.createLinear(-1, -2, 0, 0, 20, 0, 0));
        ItemCell itemCell2 = new ItemCell(getContext());
        itemCell2.imageView.setImageResource(R.drawable.msg_stealth_25min);
        itemCell2.textView.setText(LocaleController.getString(R.string.HideNextViews));
        itemCell2.description.setText(LocaleController.getString(R.string.HideNextViewsDescription));
        linearLayout.addView(itemCell2, LayoutHelper.createLinear(-1, -2, 0, 0, 10, 0, 0));
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(8.0f), true, resourcesProvider);
        this.button = premiumButtonView;
        premiumButtonView.drawGradient = false;
        premiumButtonView.overlayTextView.getDrawable().setSplitByWords(false);
        int i2 = R.raw.unlock_icon;
        premiumButtonView.setIcon(i2);
        ScaleStateListAnimator.apply(premiumButtonView);
        final TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (currentUser.premium) {
            updateButton(false);
        } else {
            premiumButtonView.setIcon(i2);
            premiumButtonView.setButton(LocaleController.getString(R.string.UnlockStealthMode), new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    StealthModeAlert.this.lambda$new$0(view);
                }
            });
        }
        linearLayout.addView(premiumButtonView, LayoutHelper.createLinear(-1, 48, 80, 14, 24, 14, 16));
        setCustomView(frameLayout);
        premiumButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StealthModeAlert.this.lambda$new$3(currentUser, i, resourcesProvider, view);
            }
        });
    }

    public void lambda$new$0(View view) {
        dismiss();
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            lastFragment.showDialog(new PremiumFeatureBottomSheet(lastFragment, 14, false));
        }
    }

    public static void lambda$new$1() {
    }

    public static void lambda$new$2(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StealthModeAlert.lambda$new$1();
            }
        });
    }

    public void lambda$new$3(TLRPC.User user, int i, Theme.ResourcesProvider resourcesProvider, View view) {
        if (!user.premium) {
            dismiss();
            BaseFragment lastFragment = LaunchActivity.getLastFragment();
            if (lastFragment != null) {
                lastFragment.showDialog(new PremiumFeatureBottomSheet(lastFragment, 14, false));
                return;
            }
            return;
        }
        if (this.stealthModeIsActive) {
            dismiss();
            Listener listener = this.listener;
            if (listener != null) {
                listener.onButtonClicked(false);
                return;
            }
            return;
        }
        StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
        TL_stories.TL_storiesStealthMode stealthMode = storiesController.getStealthMode();
        if (stealthMode != null && ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() <= stealthMode.cooldown_until_date) {
            if (!this.stealthModeIsActive) {
                BulletinFactory of = BulletinFactory.of(this.container, resourcesProvider);
                if (of != null) {
                    of.createErrorBulletin(AndroidUtilities.replaceTags(LocaleController.getString(R.string.StealthModeCooldownHint))).show(true);
                    return;
                }
                return;
            }
            dismiss();
            Listener listener2 = this.listener;
            if (listener2 != null) {
                listener2.onButtonClicked(false);
                return;
            }
            return;
        }
        TL_stories.TL_stories_activateStealthMode tL_stories_activateStealthMode = new TL_stories.TL_stories_activateStealthMode();
        tL_stories_activateStealthMode.future = true;
        tL_stories_activateStealthMode.past = true;
        TL_stories.TL_storiesStealthMode tL_storiesStealthMode = new TL_stories.TL_storiesStealthMode();
        tL_storiesStealthMode.flags |= 3;
        tL_storiesStealthMode.cooldown_until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + MessagesController.getInstance(this.currentAccount).stealthModeCooldown;
        tL_storiesStealthMode.active_until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + MessagesController.getInstance(this.currentAccount).stealthModeFuture;
        storiesController.setStealthMode(tL_storiesStealthMode);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_activateStealthMode, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StealthModeAlert.lambda$new$2(tLObject, tL_error);
            }
        });
        try {
            this.containerView.performHapticFeedback(3);
        } catch (Exception unused) {
        }
        dismiss();
        if (i == 0) {
            showStealthModeEnabledBulletin();
        }
        Listener listener3 = this.listener;
        if (listener3 != null) {
            listener3.onButtonClicked(true);
        }
    }

    public void lambda$new$4() {
        if (isShowing()) {
            updateButton(true);
        }
    }

    public static void showStealthModeEnabledBulletin() {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        BulletinFactory of = lastFragment.getLastStoryViewer() != null ? BulletinFactory.of(lastFragment.getLastStoryViewer().windowView, lastFragment.getLastStoryViewer().getResourceProvider()) : BulletinFactory.global();
        if (of != null) {
            of.createSimpleLargeBulletin(R.drawable.msg_stories_stealth2, LocaleController.getString(R.string.StealthModeOn), LocaleController.getString(R.string.StealthModeOnHint)).show();
        }
    }

    private void updateButton(boolean z) {
        PremiumButtonView premiumButtonView;
        int i;
        TL_stories.TL_storiesStealthMode stealthMode = MessagesController.getInstance(this.currentAccount).getStoriesController().getStealthMode();
        if (stealthMode == null || ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() >= stealthMode.active_until_date) {
            if (stealthMode != null) {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                int i2 = stealthMode.cooldown_until_date;
                if (currentTime <= i2) {
                    long currentTime2 = i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                    int i3 = (int) (currentTime2 % 60);
                    long j = currentTime2 / 60;
                    int i4 = (int) (j % 60);
                    int i5 = (int) (j / 60);
                    StringBuilder sb = new StringBuilder();
                    Locale locale = Locale.ENGLISH;
                    sb.append(String.format(locale, "%02d", Integer.valueOf(i5)));
                    sb.append(String.format(locale, ":%02d", Integer.valueOf(i4)));
                    sb.append(String.format(locale, ":%02d", Integer.valueOf(i3)));
                    this.button.setOverlayText(LocaleController.formatString("AvailableIn", R.string.AvailableIn, sb.toString()), true, z);
                    this.button.overlayTextView.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_featuredStickers_buttonText), 125));
                    AndroidUtilities.cancelRunOnUIThread(this.updateButtonRunnuble);
                    AndroidUtilities.runOnUIThread(this.updateButtonRunnuble, 1000L);
                    return;
                }
            }
            int i6 = this.type;
            if (i6 != 0) {
                if (i6 == 1) {
                    premiumButtonView = this.button;
                    i = R.string.EnableStealthModeAndOpenStory;
                }
                this.button.overlayTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            }
            premiumButtonView = this.button;
            i = R.string.EnableStealthMode;
        } else {
            this.stealthModeIsActive = true;
            premiumButtonView = this.button;
            i = R.string.StealthModeIsActive;
        }
        premiumButtonView.setOverlayText(LocaleController.getString(i), true, z);
        this.button.overlayTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
