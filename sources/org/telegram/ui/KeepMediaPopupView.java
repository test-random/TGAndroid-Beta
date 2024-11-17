package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CacheByChatsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.DialogsActivity;

public class KeepMediaPopupView extends ActionBarPopupWindow.ActionBarPopupWindowLayout {
    private final CacheByChatsController cacheByChatsController;
    Callback callback;
    ArrayList checkItems;
    int currentType;
    ActionBarMenuSubItem delete;
    private final TextView description;
    private ArrayList exceptions;
    ExceptionsView exceptionsView;
    ActionBarMenuSubItem forever;
    FrameLayout gap;
    ActionBarMenuSubItem oneDay;
    ActionBarMenuSubItem oneMonth;
    ActionBarMenuSubItem oneWeek;
    BaseFragment parentFragment;
    ActionBarMenuSubItem twoDay;

    public interface Callback {
        void onKeepMediaChange(int i, int i2);
    }

    private static class CheckItem {
        final ActionBarMenuSubItem item;
        final int type;

        private CheckItem(ActionBarMenuSubItem actionBarMenuSubItem, int i) {
            this.item = actionBarMenuSubItem;
            this.type = i;
        }
    }

    public class ExceptionsView extends FrameLayout {
        AvatarsImageView avatarsImageView;
        boolean ignoreLayout;
        SimpleTextView titleView;

        public ExceptionsView(Context context) {
            super(context);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.titleView.setEllipsizeByGradient(true);
            this.titleView.setRightPadding(AndroidUtilities.dp(68.0f));
            this.titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            addView(this.titleView, LayoutHelper.createFrame(0, -2.0f, 19, 19.0f, 0.0f, 19.0f, 0.0f));
            AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
            this.avatarsImageView = avatarsImageView;
            avatarsImageView.avatarsDrawable.setShowSavedMessages(true);
            this.avatarsImageView.setStyle(11);
            this.avatarsImageView.setAvatarsTextSize(AndroidUtilities.dp(22.0f));
            addView(this.avatarsImageView, LayoutHelper.createFrame(56, -1.0f, 21, 0.0f, 0.0f, 4.0f, 0.0f));
            setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector), 0, 4));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            View view = (View) getParent();
            if (view != null && view.getWidth() > 0) {
                i = View.MeasureSpec.makeMeasureSpec(view.getWidth(), 1073741824);
            }
            this.ignoreLayout = true;
            this.titleView.setVisibility(8);
            super.onMeasure(i, i2);
            this.titleView.setVisibility(0);
            this.titleView.getLayoutParams().width = getMeasuredWidth();
            this.ignoreLayout = false;
            KeepMediaPopupView.this.updateAvatarsPosition();
            super.onMeasure(i, i2);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public KeepMediaPopupView(final BaseFragment baseFragment, Context context) {
        super(context, null);
        this.checkItems = new ArrayList();
        this.parentFragment = baseFragment;
        this.cacheByChatsController = baseFragment.getMessagesController().getCacheByChatsController();
        setFitItems(true);
        this.oneDay = ActionBarMenuItem.addItem(this, R.drawable.msg_autodelete_1d, LocaleController.formatPluralString("Days", 1, new Object[0]), false, null);
        this.twoDay = ActionBarMenuItem.addItem(this, R.drawable.msg_autodelete_2d, LocaleController.formatPluralString("Days", 2, new Object[0]), false, null);
        this.oneWeek = ActionBarMenuItem.addItem(this, R.drawable.msg_autodelete_1w, LocaleController.formatPluralString("Weeks", 1, new Object[0]), false, null);
        this.oneMonth = ActionBarMenuItem.addItem(this, R.drawable.msg_autodelete_1m, LocaleController.formatPluralString("Months", 1, new Object[0]), false, null);
        this.forever = ActionBarMenuItem.addItem(this, R.drawable.msg_cancel, LocaleController.getString(R.string.AutoDeleteMediaNever), false, null);
        ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(this, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteException), false, null);
        this.delete = addItem;
        int i = Theme.key_text_RedRegular;
        addItem.setColors(Theme.getColor(i), Theme.getColor(i));
        this.checkItems.add(new CheckItem(this.oneDay, CacheByChatsController.KEEP_MEDIA_ONE_DAY));
        this.checkItems.add(new CheckItem(this.twoDay, CacheByChatsController.KEEP_MEDIA_TWO_DAY));
        this.checkItems.add(new CheckItem(this.oneWeek, CacheByChatsController.KEEP_MEDIA_ONE_WEEK));
        this.checkItems.add(new CheckItem(this.oneMonth, CacheByChatsController.KEEP_MEDIA_ONE_MONTH));
        this.checkItems.add(new CheckItem(this.forever, CacheByChatsController.KEEP_MEDIA_FOREVER));
        this.checkItems.add(new CheckItem(this.delete, CacheByChatsController.KEEP_MEDIA_DELETE));
        FrameLayout frameLayout = new FrameLayout(context);
        this.gap = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator));
        View view = new View(context);
        view.setBackground(Theme.getThemedDrawableByKey(context, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow, null));
        this.gap.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        this.gap.setTag(R.id.fit_width_tag, 1);
        addView(this.gap, LayoutHelper.createLinear(-1, 8));
        ExceptionsView exceptionsView = new ExceptionsView(context);
        this.exceptionsView = exceptionsView;
        addView((View) exceptionsView, LayoutHelper.createLinear(-1, 48));
        this.exceptionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                KeepMediaPopupView.this.lambda$new$2(baseFragment, view2);
            }
        });
        for (int i2 = 0; i2 < this.checkItems.size(); i2++) {
            final int i3 = ((CheckItem) this.checkItems.get(i2)).type;
            ((CheckItem) this.checkItems.get(i2)).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    KeepMediaPopupView.this.lambda$new$3(i3, view2);
                }
            });
        }
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        this.description = linksTextView;
        linksTextView.setTag(R.id.fit_width_tag, 1);
        linksTextView.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        linksTextView.setTextSize(1, 13.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        linksTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        linksTextView.setText(LocaleController.getString(R.string.KeepMediaPopupDescription));
        addView((View) linksTextView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 8, 0, 0));
    }

    public boolean lambda$new$1(final DialogsActivity dialogsActivity, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        final CacheByChatsController.KeepMediaException keepMediaException = null;
        int i2 = 0;
        while (i2 < arrayList.size()) {
            ArrayList arrayList2 = this.exceptions;
            CacheByChatsController.KeepMediaException keepMediaException2 = new CacheByChatsController.KeepMediaException(((MessagesStorage.TopicKey) arrayList.get(i2)).dialogId, CacheByChatsController.KEEP_MEDIA_ONE_DAY);
            arrayList2.add(keepMediaException2);
            i2++;
            keepMediaException = keepMediaException2;
        }
        this.cacheByChatsController.saveKeepMediaExceptions(this.currentType, this.exceptions);
        Bundle bundle = new Bundle();
        bundle.putInt("type", this.currentType);
        final CacheChatsExceptionsFragment cacheChatsExceptionsFragment = new CacheChatsExceptionsFragment(bundle) {
            @Override
            public void onTransitionAnimationEnd(boolean z3, boolean z4) {
                super.onTransitionAnimationEnd(z3, z4);
                if (!z3 || z4) {
                    return;
                }
                dialogsActivity.removeSelfFromStack();
            }
        };
        cacheChatsExceptionsFragment.setExceptions(this.exceptions);
        this.parentFragment.presentFragment(cacheChatsExceptionsFragment);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                CacheChatsExceptionsFragment.this.showPopupFor(keepMediaException);
            }
        }, 150L);
        return true;
    }

    public void lambda$new$2(BaseFragment baseFragment, View view) {
        CacheChatsExceptionsFragment cacheChatsExceptionsFragment;
        this.window.dismiss();
        if (this.exceptions.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("checkCanWrite", false);
            int i = this.currentType;
            bundle.putInt("dialogsType", i == 1 ? 6 : i == 2 ? 5 : 4);
            bundle.putBoolean("allowGlobalSearch", false);
            final DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                @Override
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
                    boolean lambda$new$1;
                    lambda$new$1 = KeepMediaPopupView.this.lambda$new$1(dialogsActivity, dialogsActivity2, arrayList, charSequence, z, z2, i2, topicsFragment);
                    return lambda$new$1;
                }
            });
            cacheChatsExceptionsFragment = dialogsActivity;
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("type", this.currentType);
            CacheChatsExceptionsFragment cacheChatsExceptionsFragment2 = new CacheChatsExceptionsFragment(bundle2);
            cacheChatsExceptionsFragment2.setExceptions(this.exceptions);
            cacheChatsExceptionsFragment = cacheChatsExceptionsFragment2;
        }
        baseFragment.presentFragment(cacheChatsExceptionsFragment);
    }

    public void lambda$new$3(int i, View view) {
        this.window.dismiss();
        int i2 = this.currentType;
        if (i2 < 0) {
            Callback callback = this.callback;
            if (callback != null) {
                callback.onKeepMediaChange(i2, i);
                return;
            }
            return;
        }
        this.cacheByChatsController.setKeepMedia(i2, i);
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onKeepMediaChange(this.currentType, i);
        }
    }

    public void updateAvatarsPosition() {
        if (this.exceptions != null) {
            this.exceptionsView.avatarsImageView.setTranslationX(AndroidUtilities.dp(12.0f) * (3 - Math.min(3, this.exceptions.size())));
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void update(int i) {
        this.currentType = i;
        ActionBarMenuSubItem actionBarMenuSubItem = this.twoDay;
        if (i == 3) {
            actionBarMenuSubItem.setVisibility(0);
            this.oneMonth.setVisibility(8);
            this.gap.setVisibility(8);
            this.exceptionsView.setVisibility(8);
            this.description.setVisibility(8);
        } else {
            actionBarMenuSubItem.setVisibility(8);
            this.oneMonth.setVisibility(0);
            this.gap.setVisibility(0);
            this.exceptionsView.setVisibility(0);
            this.description.setVisibility(0);
        }
        ArrayList<CacheByChatsController.KeepMediaException> keepMediaExceptions = this.cacheByChatsController.getKeepMediaExceptions(i);
        this.exceptions = keepMediaExceptions;
        if (keepMediaExceptions.isEmpty()) {
            this.exceptionsView.titleView.setText(LocaleController.getString(R.string.AddAnException));
            this.exceptionsView.titleView.setRightPadding(AndroidUtilities.dp(8.0f));
            this.exceptionsView.avatarsImageView.setObject(0, this.parentFragment.getCurrentAccount(), null);
            this.exceptionsView.avatarsImageView.setObject(1, this.parentFragment.getCurrentAccount(), null);
            this.exceptionsView.avatarsImageView.setObject(2, this.parentFragment.getCurrentAccount(), null);
        } else {
            int min = Math.min(3, this.exceptions.size());
            this.exceptionsView.titleView.setRightPadding(AndroidUtilities.dp((Math.max(0, min - 1) * 12) + 64));
            this.exceptionsView.titleView.setText(LocaleController.formatPluralString("ExceptionShort", this.exceptions.size(), Integer.valueOf(this.exceptions.size())));
            for (int i2 = 0; i2 < min; i2++) {
                this.exceptionsView.avatarsImageView.setObject(i2, this.parentFragment.getCurrentAccount(), this.parentFragment.getMessagesController().getUserOrChat(((CacheByChatsController.KeepMediaException) this.exceptions.get(i2)).dialogId));
            }
        }
        this.exceptionsView.avatarsImageView.commitTransition(false);
        this.delete.setVisibility(8);
        this.description.setVisibility(8);
        updateAvatarsPosition();
    }

    public void updateForDialog(boolean z) {
        this.currentType = -1;
        this.gap.setVisibility(0);
        this.delete.setVisibility(z ? 8 : 0);
        this.description.setVisibility(0);
        this.exceptionsView.setVisibility(8);
    }
}
