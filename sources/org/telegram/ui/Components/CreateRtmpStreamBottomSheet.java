package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Components.CreateRtmpStreamBottomSheet;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UItem;

public class CreateRtmpStreamBottomSheet extends BottomSheetWithRecyclerListView {
    private UniversalAdapter adapter;
    private final boolean hasFewPeers;
    private final JoinCallAlert.JoinCallAlertDelegate joinCallDelegate;
    private String rtmpKey;
    private String rtmpUrl;
    private TLRPC.InputPeer selectAfterDismiss;

    public static class TextDetailCellFactory extends UItem.UItemFactory {
        static {
            UItem.UItemFactory.setup(new TextDetailCellFactory());
        }

        private void copyRtmpValue(Context context, String str) {
            AndroidUtilities.addToClipboard(str);
            if (AndroidUtilities.shouldShowClipboardToast()) {
                Toast.makeText(context, LocaleController.getString(R.string.TextCopied), 0).show();
            }
        }

        public void lambda$createView$0(Context context, TextDetailCell textDetailCell, View view) {
            copyRtmpValue(context, textDetailCell.textView.getText().toString());
        }

        public static UItem of(String str, String str2, boolean z) {
            UItem ofFactory = UItem.ofFactory(TextDetailCellFactory.class);
            ofFactory.text = str;
            ofFactory.textValue = str2;
            ofFactory.hideDivider = !z;
            ofFactory.enabled = false;
            return ofFactory;
        }

        @Override
        public void bindView(View view, UItem uItem, boolean z) {
            ((TextDetailCell) view).setTextAndValue(uItem.text, uItem.textValue, !uItem.hideDivider);
        }

        @Override
        public TextDetailCell createView(final Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            final TextDetailCell textDetailCell = new TextDetailCell(context);
            textDetailCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
            Drawable mutate = ContextCompat.getDrawable(context, R.drawable.msg_copy).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader), PorterDuff.Mode.MULTIPLY));
            textDetailCell.setImage(mutate);
            textDetailCell.setImageClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    CreateRtmpStreamBottomSheet.TextDetailCellFactory.this.lambda$createView$0(context, textDetailCell, view);
                }
            });
            return textDetailCell;
        }
    }

    public static class TopCell extends LinearLayout {
        public TopCell(Context context) {
            super(context);
            setOrientation(1);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            rLottieImageView.setAutoRepeat(true);
            rLottieImageView.setAnimation(R.raw.utyan_streaming, 112, 112);
            rLottieImageView.playAnimation();
            addView(rLottieImageView, LayoutHelper.createLinear(112, 112, 49, 0, 24, 0, 0));
            TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setText(LocaleController.formatString(R.string.Streaming, new Object[0]));
            textView.setTextSize(1, 20.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 14, 0, 7));
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 14.0f);
            textView2.setGravity(1);
            textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            textView2.setText(LocaleController.formatString(R.string.VoipStreamStart, new Object[0]));
            textView2.setLineSpacing(textView2.getLineSpacingExtra(), textView2.getLineSpacingMultiplier() * 1.1f);
            addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 28, 0, 28, 17));
        }
    }

    public CreateRtmpStreamBottomSheet(BaseFragment baseFragment, final TLRPC.Peer peer, long j, boolean z, JoinCallAlert.JoinCallAlertDelegate joinCallAlertDelegate) {
        super(baseFragment, false, false);
        this.topPadding = 0.26f;
        this.joinCallDelegate = joinCallAlertDelegate;
        this.hasFewPeers = z;
        TextView textView = new TextView(this.containerView.getContext());
        textView.setGravity(17);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setSingleLine(true);
        textView.setTextSize(1, 14.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.VoipChannelStartStreaming));
        textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(Theme.key_featuredStickers_addButton), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 120)));
        this.containerView.addView(textView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 12.0f));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                CreateRtmpStreamBottomSheet.this.lambda$new$0(peer, view);
            }
        });
        RecyclerListView recyclerListView = this.recyclerListView;
        int i = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i, 0, i, AndroidUtilities.dp(72.0f));
        fixNavigationBar();
        updateTitle();
        TL_phone.getGroupCallStreamRtmpUrl getgroupcallstreamrtmpurl = new TL_phone.getGroupCallStreamRtmpUrl();
        getgroupcallstreamrtmpurl.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        getgroupcallstreamrtmpurl.revoke = false;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getgroupcallstreamrtmpurl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CreateRtmpStreamBottomSheet.this.lambda$new$2(tLObject, tL_error);
            }
        });
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asCustom(new TopCell(getContext())));
        arrayList.add(UItem.asShadow(null));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.VoipChatStreamSettings)));
        arrayList.add(TextDetailCellFactory.of(this.rtmpUrl, LocaleController.getString(R.string.VoipChatStreamServerUrl), true));
        arrayList.add(TextDetailCellFactory.of(this.rtmpKey, LocaleController.getString(R.string.VoipChatStreamKey), false));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.VoipChatStreamWithAnotherAppDescription)));
    }

    public void lambda$new$0(TLRPC.Peer peer, View view) {
        this.selectAfterDismiss = MessagesController.getInstance(this.currentAccount).getInputPeer(MessageObject.getPeerId(peer));
        lambda$new$0();
    }

    public void lambda$new$1(TLObject tLObject) {
        if (tLObject == null || !(tLObject instanceof TL_phone.groupCallStreamRtmpUrl)) {
            return;
        }
        TL_phone.groupCallStreamRtmpUrl groupcallstreamrtmpurl = (TL_phone.groupCallStreamRtmpUrl) tLObject;
        this.rtmpUrl = groupcallstreamrtmpurl.url;
        this.rtmpKey = groupcallstreamrtmpurl.key;
        this.adapter.update(false);
    }

    public void lambda$new$2(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                CreateRtmpStreamBottomSheet.this.lambda$new$1(tLObject);
            }
        });
    }

    public static void show(TLRPC.Peer peer, BaseFragment baseFragment, long j, boolean z, JoinCallAlert.JoinCallAlertDelegate joinCallAlertDelegate) {
        CreateRtmpStreamBottomSheet createRtmpStreamBottomSheet = new CreateRtmpStreamBottomSheet(baseFragment, peer, j, z, joinCallAlertDelegate);
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            createRtmpStreamBottomSheet.show();
        } else {
            baseFragment.showDialog(createRtmpStreamBottomSheet);
        }
    }

    @Override
    public RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        UniversalAdapter universalAdapter = new UniversalAdapter(recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                CreateRtmpStreamBottomSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, this.resourcesProvider);
        this.adapter = universalAdapter;
        return universalAdapter;
    }

    @Override
    public void dismissInternal() {
        super.dismissInternal();
        TLRPC.InputPeer inputPeer = this.selectAfterDismiss;
        if (inputPeer != null) {
            this.joinCallDelegate.didSelectChat(inputPeer, this.hasFewPeers, false, true);
        }
    }

    @Override
    protected CharSequence getTitle() {
        return LocaleController.getString(R.string.Streaming);
    }
}
