package org.telegram.ui.Stars;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StarAppsSheet;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class ExplainStarsSheet extends BottomSheetWithRecyclerListView {
    private UniversalAdapter adapter;
    private FrameLayout buttonContainer;
    private LinearLayout headerView;

    public static class FeatureCell extends LinearLayout {
        private final ImageView imageView;
        private final LinkSpanDrawable.LinksTextView subtitleView;
        private final LinearLayout textLayout;
        private final TextView titleView;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem of(int i, CharSequence charSequence, CharSequence charSequence2) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.selectable = false;
                ofFactory.intValue = i;
                ofFactory.text = charSequence;
                ofFactory.subtext = charSequence2;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((FeatureCell) view).set(uItem.intValue, uItem.text, uItem.subtext);
            }

            @Override
            public FeatureCell createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new FeatureCell(context);
            }
        }

        public FeatureCell(Context context) {
            super(context);
            setOrientation(0);
            setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(12.0f));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            int i = Theme.key_windowBackgroundWhiteBlackText;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), PorterDuff.Mode.SRC_IN));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(imageView, LayoutHelper.createLinear(24, 24, 51, 0, 6, 16, 0));
            LinearLayout linearLayout = new LinearLayout(context);
            this.textLayout = linearLayout;
            linearLayout.setOrientation(1);
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(Theme.getColor(i));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 7, 0, 0, 0, 3));
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.subtitleView = linksTextView;
            linksTextView.setTextSize(1, 14.0f);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn));
            linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 7));
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 55, 0, 0, 0, 0));
        }

        public void set(int i, CharSequence charSequence, CharSequence charSequence2) {
            this.imageView.setImageResource(i);
            this.titleView.setText(charSequence);
            this.subtitleView.setText(charSequence2);
        }
    }

    public ExplainStarsSheet(Context context) {
        super(context, null, false, false, false, null);
        this.topPadding = 0.1f;
        fixNavigationBar();
        RecyclerListView recyclerListView = this.recyclerListView;
        int i = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i, 0, i, 0);
        LinearLayout linearLayout = new LinearLayout(context);
        this.headerView = linearLayout;
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        StarParticlesView makeParticlesView = StarsIntroActivity.makeParticlesView(context, 70, 0);
        frameLayout.addView(makeParticlesView, LayoutHelper.createFrame(-1, -1.0f));
        GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1, 2);
        GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
        gLIconRenderer.colorKey1 = Theme.key_starsGradient1;
        gLIconRenderer.colorKey2 = Theme.key_starsGradient2;
        gLIconRenderer.updateColors();
        gLIconTextureView.setStarParticlesView(makeParticlesView);
        frameLayout.addView(gLIconTextureView, LayoutHelper.createFrame(170, 170.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
        gLIconTextureView.setPaused(false);
        this.headerView.addView(frameLayout, LayoutHelper.createFrame(-1, 150.0f));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i2 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i2, this.resourcesProvider));
        textView.setGravity(17);
        textView.setText(LocaleController.getString(R.string.ExplainStarsTitle));
        this.headerView.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 2, 0, 0));
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(i2, this.resourcesProvider));
        textView2.setGravity(17);
        textView2.setText(LocaleController.getString(R.string.ExplainStarsTitle2));
        this.headerView.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 16, 9, 16, 18));
        this.buttonContainer = new FrameLayout(context);
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, this.resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString(R.string.ExplainStarsButton), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ExplainStarsSheet.this.lambda$new$0(view);
            }
        });
        this.buttonContainer.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 119, 10.0f, 10.0f, 10.0f, 10.0f));
        FrameLayout frameLayout2 = this.buttonContainer;
        int i3 = this.backgroundPaddingLeft;
        frameLayout2.setPadding(i3, 0, i3, 0);
        this.buttonContainer.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerView.addView(this.buttonContainer, LayoutHelper.createFrame(-1, -2, 87));
        this.adapter.update(false);
    }

    public void lambda$fillItems$1() {
        new StarAppsSheet(getContext()).show();
    }

    public void lambda$new$0(View view) {
        lambda$new$0();
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        UniversalAdapter universalAdapter = new UniversalAdapter(recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ExplainStarsSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, this.resourcesProvider) {
            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return false;
            }
        };
        this.adapter = universalAdapter;
        return universalAdapter;
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asCustom(this.headerView));
        arrayList.add(FeatureCell.Factory.of(R.drawable.msg_gift_premium, LocaleController.getString(R.string.ExplainStarsFeature1Title), LocaleController.getString(R.string.ExplainStarsFeature1Text)));
        arrayList.add(FeatureCell.Factory.of(R.drawable.msg_bot, LocaleController.getString(R.string.ExplainStarsFeature2Title), AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.ExplainStarsFeature2Text), new Runnable() {
            @Override
            public final void run() {
                ExplainStarsSheet.this.lambda$fillItems$1();
            }
        }), true)));
        arrayList.add(FeatureCell.Factory.of(R.drawable.menu_unlock, LocaleController.getString(R.string.ExplainStarsFeature3Title), LocaleController.getString(R.string.ExplainStarsFeature3Text)));
        arrayList.add(FeatureCell.Factory.of(R.drawable.menu_feature_paid, LocaleController.getString(R.string.ExplainStarsFeature4Title), LocaleController.getString(R.string.ExplainStarsFeature4Text)));
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(68.0f)));
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.ExplainStarsTitle);
    }
}
