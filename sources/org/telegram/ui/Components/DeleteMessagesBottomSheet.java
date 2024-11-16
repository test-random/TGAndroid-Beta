package org.telegram.ui.Components;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Collection$EL;
import j$.util.DesugarArrays;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CollapseTextCell;
import org.telegram.ui.ChatActivity$$ExternalSyntheticLambda295;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorBtnCell;
import org.telegram.ui.Components.RecyclerListView;

public class DeleteMessagesBottomSheet extends BottomSheetWithRecyclerListView {
    private TextView actionButton;
    private UniversalAdapter adapter;
    private boolean banChecked;
    private boolean[] banFilter;
    private Action banOrRestrict;
    private TLRPC.TL_chatBannedRights bannedRights;
    private SelectorBtnCell buttonContainer;
    private boolean canRestrict;
    private TLRPC.TL_chatBannedRights defaultBannedRights;
    private Action deleteAll;
    private TLRPC.Chat inChat;
    private boolean isForum;
    private long mergeDialogId;
    private ArrayList messages;
    private int mode;
    private Runnable onDelete;
    private int[] participantMessageCounts;
    private boolean participantMessageCountsLoaded;
    private boolean participantMessageCountsLoading;
    private ArrayList participantsBannedRights;
    private Action report;
    private boolean restrict;
    private boolean[] restrictFilter;
    private boolean sendMediaCollapsed;
    private float shiftDp;
    private int topicId;

    public class Action {
        boolean[] checks;
        boolean collapsed;
        boolean[] filter;
        int filteredCount;
        ArrayList options;
        int selectedCount;
        String title;
        int totalCount;
        int type;

        Action(int i, ArrayList arrayList) {
            this.type = i;
            int size = arrayList.size();
            this.totalCount = size;
            this.selectedCount = 0;
            if (size > 0) {
                this.options = arrayList;
                this.checks = new boolean[size];
                this.collapsed = true;
                updateTitle();
            }
        }

        void collapseOrExpand() {
            this.collapsed = !this.collapsed;
            DeleteMessagesBottomSheet.this.adapter.update(true);
        }

        TLObject first() {
            for (int i = 0; i < this.totalCount; i++) {
                boolean[] zArr = this.filter;
                if (zArr == null || zArr[i]) {
                    return (TLObject) this.options.get(i);
                }
            }
            return null;
        }

        void forEach(Utilities.IndexedConsumer indexedConsumer) {
            for (int i = 0; i < this.totalCount; i++) {
                boolean[] zArr = this.filter;
                if (zArr == null || zArr[i]) {
                    indexedConsumer.accept((TLObject) this.options.get(i), i);
                }
            }
        }

        void forEachSelected(Utilities.IndexedConsumer indexedConsumer) {
            boolean[] zArr;
            for (int i = 0; i < this.totalCount; i++) {
                if (this.checks[i] && ((zArr = this.filter) == null || zArr[i])) {
                    indexedConsumer.accept((TLObject) this.options.get(i), i);
                }
            }
        }

        int getCount() {
            return this.filter != null ? this.filteredCount : this.totalCount;
        }

        boolean isExpandable() {
            return getCount() > 1;
        }

        boolean isPresent() {
            return getCount() > 0;
        }

        void setFilter(boolean[] zArr) {
            if (this.totalCount == 0) {
                return;
            }
            this.filter = zArr;
            updateCounters();
            updateTitle();
        }

        void toggleAllChecks() {
            boolean[] zArr;
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= this.totalCount) {
                    z = true;
                    break;
                } else if (this.checks[i] && ((zArr = this.filter) == null || zArr[i])) {
                    break;
                } else {
                    i++;
                }
            }
            Arrays.fill(this.checks, z);
            updateCounters();
            DeleteMessagesBottomSheet.this.adapter.update(true);
        }

        void toggleCheck(int i) {
            boolean[] zArr = this.filter;
            if (zArr == null || zArr[i]) {
                boolean[] zArr2 = this.checks;
                boolean z = !zArr2[i];
                zArr2[i] = z;
                int i2 = this.selectedCount;
                this.selectedCount = z ? i2 + 1 : i2 - 1;
                DeleteMessagesBottomSheet.this.adapter.update(true);
            }
        }

        void updateCounters() {
            int i;
            this.selectedCount = 0;
            this.filteredCount = 0;
            while (i < this.totalCount) {
                boolean[] zArr = this.filter;
                if (zArr == null) {
                    i = this.checks[i] ? 0 : i + 1;
                    this.selectedCount++;
                } else {
                    if (zArr[i]) {
                        this.filteredCount++;
                        if (!this.checks[i]) {
                        }
                        this.selectedCount++;
                    }
                }
            }
        }

        void updateTitle() {
            String formatString;
            int i;
            if (this.totalCount == 0) {
                return;
            }
            TLObject first = first();
            String forcedFirstName = first instanceof TLRPC.User ? UserObject.getForcedFirstName((TLRPC.User) first) : ContactsController.formatName(first);
            int i2 = this.type;
            if (i2 == 0) {
                i = R.string.DeleteReportSpam;
            } else if (i2 == 1) {
                if (!isExpandable()) {
                    formatString = LocaleController.formatString(R.string.DeleteAllFrom, forcedFirstName);
                    this.title = formatString;
                }
                i = R.string.DeleteAllFromUsers;
            } else {
                if (i2 != 2) {
                    return;
                }
                if (DeleteMessagesBottomSheet.this.restrict) {
                    if (!isExpandable()) {
                        formatString = LocaleController.formatString(R.string.DeleteRestrict, forcedFirstName);
                        this.title = formatString;
                    }
                    i = R.string.DeleteRestrictUsers;
                } else {
                    if (!isExpandable()) {
                        formatString = LocaleController.formatString(R.string.DeleteBan, forcedFirstName);
                        this.title = formatString;
                    }
                    i = R.string.DeleteBanUsers;
                }
            }
            formatString = LocaleController.getString(i);
            this.title = formatString;
        }
    }

    public DeleteMessagesBottomSheet(BaseFragment baseFragment, TLRPC.Chat chat, ArrayList arrayList, ArrayList arrayList2, TLRPC.ChannelParticipant[] channelParticipantArr, long j, int i, int i2, Runnable runnable) {
        super(baseFragment.getContext(), baseFragment, false, false, false, true, BottomSheetWithRecyclerListView.ActionBarType.SLIDING, baseFragment.getResourceProvider());
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.TL_chatBannedRights tL_chatBannedRights2;
        this.restrict = false;
        this.participantMessageCountsLoading = false;
        this.participantMessageCountsLoaded = false;
        this.sendMediaCollapsed = true;
        this.shiftDp = 10.0f;
        setShowHandle(true);
        fixNavigationBar();
        this.takeTranslationIntoAccount = true;
        RecyclerListView recyclerListView = this.recyclerListView;
        int i3 = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i3, this.headerTotalHeight, i3, AndroidUtilities.dp(68.0f));
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i4) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i4);
            }

            @Override
            public void onDoubleTap(View view, int i4, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i4, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i4, float f, float f2) {
                DeleteMessagesBottomSheet.this.lambda$new$0(view, i4, f, f2);
            }
        });
        this.takeTranslationIntoAccount = true;
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() {
            @Override
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                super.onMoveAnimationUpdate(viewHolder);
                ((BottomSheet) DeleteMessagesBottomSheet.this).containerView.invalidate();
            }
        };
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDurations(350L);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        SelectorBtnCell selectorBtnCell = new SelectorBtnCell(getContext(), this.resourcesProvider, null);
        this.buttonContainer = selectorBtnCell;
        selectorBtnCell.setClickable(true);
        this.buttonContainer.setOrientation(1);
        this.buttonContainer.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.buttonContainer.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
        TextView textView = new TextView(getContext());
        this.actionButton = textView;
        textView.setLines(1);
        this.actionButton.setSingleLine(true);
        this.actionButton.setGravity(1);
        this.actionButton.setEllipsize(TextUtils.TruncateAt.END);
        this.actionButton.setGravity(17);
        this.actionButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.actionButton.setTypeface(AndroidUtilities.bold());
        this.actionButton.setTextSize(1, 14.0f);
        this.actionButton.setText(LocaleController.getString(R.string.DeleteProceedBtn));
        this.actionButton.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 6.0f));
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                DeleteMessagesBottomSheet.this.lambda$new$1(view);
            }
        });
        this.buttonContainer.addView(this.actionButton, LayoutHelper.createLinear(-1, 48, 87));
        ViewGroup viewGroup = this.containerView;
        SelectorBtnCell selectorBtnCell2 = this.buttonContainer;
        int i4 = this.backgroundPaddingLeft;
        viewGroup.addView(selectorBtnCell2, LayoutHelper.createFrameMarginPx(-1, -2.0f, 87, i4, 0, i4, 0));
        this.inChat = chat;
        this.isForum = ChatObject.isForum(chat);
        this.messages = arrayList;
        this.mergeDialogId = j;
        this.topicId = i;
        this.mode = i2;
        this.onDelete = runnable;
        this.defaultBannedRights = chat.default_banned_rights;
        TLRPC.TL_chatBannedRights tL_chatBannedRights3 = new TLRPC.TL_chatBannedRights();
        this.bannedRights = tL_chatBannedRights3;
        TLRPC.TL_chatBannedRights tL_chatBannedRights4 = this.defaultBannedRights;
        if (tL_chatBannedRights4.view_messages) {
            tL_chatBannedRights3.view_messages = true;
        }
        if (tL_chatBannedRights4.send_messages) {
            tL_chatBannedRights3.send_messages = true;
        }
        if (tL_chatBannedRights4.send_media) {
            tL_chatBannedRights3.send_media = true;
        }
        if (tL_chatBannedRights4.send_stickers) {
            tL_chatBannedRights3.send_stickers = true;
        }
        if (tL_chatBannedRights4.send_gifs) {
            tL_chatBannedRights3.send_gifs = true;
        }
        if (tL_chatBannedRights4.send_games) {
            tL_chatBannedRights3.send_games = true;
        }
        if (tL_chatBannedRights4.send_inline) {
            tL_chatBannedRights3.send_inline = true;
        }
        if (tL_chatBannedRights4.embed_links) {
            tL_chatBannedRights3.embed_links = true;
        }
        if (tL_chatBannedRights4.send_polls) {
            tL_chatBannedRights3.send_polls = true;
        }
        if (tL_chatBannedRights4.invite_users) {
            tL_chatBannedRights3.invite_users = true;
        }
        if (tL_chatBannedRights4.change_info) {
            tL_chatBannedRights3.change_info = true;
        }
        if (tL_chatBannedRights4.pin_messages) {
            tL_chatBannedRights3.pin_messages = true;
        }
        if (tL_chatBannedRights4.manage_topics) {
            tL_chatBannedRights3.manage_topics = true;
        }
        if (tL_chatBannedRights4.send_photos) {
            tL_chatBannedRights3.send_photos = true;
        }
        if (tL_chatBannedRights4.send_videos) {
            tL_chatBannedRights3.send_videos = true;
        }
        if (tL_chatBannedRights4.send_audios) {
            tL_chatBannedRights3.send_audios = true;
        }
        if (tL_chatBannedRights4.send_docs) {
            tL_chatBannedRights3.send_docs = true;
        }
        if (tL_chatBannedRights4.send_voices) {
            tL_chatBannedRights3.send_voices = true;
        }
        if (tL_chatBannedRights4.send_roundvideos) {
            tL_chatBannedRights3.send_roundvideos = true;
        }
        if (tL_chatBannedRights4.send_plain) {
            tL_chatBannedRights3.send_plain = true;
        }
        this.report = new Action(0, arrayList2);
        this.deleteAll = new Action(1, arrayList2);
        if (ChatObject.canBlockUsers(chat)) {
            this.banFilter = new boolean[arrayList2.size()];
            int i5 = 0;
            while (i5 < arrayList2.size()) {
                TLRPC.ChannelParticipant channelParticipant = i5 < channelParticipantArr.length ? channelParticipantArr[i5] : null;
                if ((chat.creator || (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant instanceof TLRPC.TL_channelParticipantCreator))) && (!(channelParticipant instanceof TLRPC.TL_channelParticipantBanned) || (tL_chatBannedRights2 = channelParticipant.banned_rights) == null || !isBanned(tL_chatBannedRights2))) {
                    this.banFilter[i5] = true;
                }
                i5++;
            }
            this.restrictFilter = new boolean[arrayList2.size()];
            if (hasAnyDefaultRights()) {
                int i6 = 0;
                while (i6 < arrayList2.size()) {
                    TLRPC.ChannelParticipant channelParticipant2 = i6 < channelParticipantArr.length ? channelParticipantArr[i6] : null;
                    if (!(arrayList2.get(i6) instanceof TLRPC.Chat) && ((!(channelParticipant2 instanceof TLRPC.TL_channelParticipantBanned) || (tL_chatBannedRights = channelParticipant2.banned_rights) == null || canBeRestricted(tL_chatBannedRights)) && this.banFilter[i6])) {
                        this.restrictFilter[i6] = true;
                        this.canRestrict = true;
                    }
                    i6++;
                }
            }
            this.participantsBannedRights = (ArrayList) DesugarArrays.stream(channelParticipantArr).map(new Function() {
                @Override
                public Function andThen(Function function) {
                    return Function.CC.$default$andThen(this, function);
                }

                @Override
                public final Object apply(Object obj) {
                    TLRPC.TL_chatBannedRights lambda$new$2;
                    lambda$new$2 = DeleteMessagesBottomSheet.lambda$new$2((TLRPC.ChannelParticipant) obj);
                    return lambda$new$2;
                }

                @Override
                public Function compose(Function function) {
                    return Function.CC.$default$compose(this, function);
                }
            }).collect(Collectors.toCollection(new ChatActivity$$ExternalSyntheticLambda295()));
            Action action = new Action(2, arrayList2);
            this.banOrRestrict = action;
            action.setFilter(this.banFilter);
        } else {
            this.banOrRestrict = new Action(2, new ArrayList(0));
        }
        this.adapter.update(false);
        this.actionBar.setTitle(getTitle());
    }

    private boolean allDefaultMediaBanned() {
        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.defaultBannedRights;
        return tL_chatBannedRights.send_photos && tL_chatBannedRights.send_videos && tL_chatBannedRights.send_stickers && tL_chatBannedRights.send_audios && tL_chatBannedRights.send_docs && tL_chatBannedRights.send_voices && tL_chatBannedRights.send_roundvideos && tL_chatBannedRights.embed_links && tL_chatBannedRights.send_polls;
    }

    public static TLRPC.TL_chatBannedRights bannedRightsOr(TLRPC.TL_chatBannedRights tL_chatBannedRights, TLRPC.TL_chatBannedRights tL_chatBannedRights2) {
        if (tL_chatBannedRights == null) {
            return tL_chatBannedRights2;
        }
        if (tL_chatBannedRights2 == null) {
            return tL_chatBannedRights;
        }
        TLRPC.TL_chatBannedRights tL_chatBannedRights3 = new TLRPC.TL_chatBannedRights();
        boolean z = true;
        tL_chatBannedRights3.view_messages = tL_chatBannedRights.view_messages || tL_chatBannedRights2.view_messages;
        tL_chatBannedRights3.send_messages = tL_chatBannedRights.send_messages || tL_chatBannedRights2.send_messages;
        tL_chatBannedRights3.send_media = tL_chatBannedRights.send_media || tL_chatBannedRights2.send_media;
        tL_chatBannedRights3.send_stickers = tL_chatBannedRights.send_stickers || tL_chatBannedRights2.send_stickers;
        tL_chatBannedRights3.send_gifs = tL_chatBannedRights.send_gifs || tL_chatBannedRights2.send_gifs;
        tL_chatBannedRights3.send_games = tL_chatBannedRights.send_games || tL_chatBannedRights2.send_games;
        tL_chatBannedRights3.send_inline = tL_chatBannedRights.send_inline || tL_chatBannedRights2.send_inline;
        tL_chatBannedRights3.embed_links = tL_chatBannedRights.embed_links || tL_chatBannedRights2.embed_links;
        tL_chatBannedRights3.send_polls = tL_chatBannedRights.send_polls || tL_chatBannedRights2.send_polls;
        tL_chatBannedRights3.change_info = tL_chatBannedRights.change_info || tL_chatBannedRights2.change_info;
        tL_chatBannedRights3.invite_users = tL_chatBannedRights.invite_users || tL_chatBannedRights2.invite_users;
        tL_chatBannedRights3.pin_messages = tL_chatBannedRights.pin_messages || tL_chatBannedRights2.pin_messages;
        tL_chatBannedRights3.manage_topics = tL_chatBannedRights.manage_topics || tL_chatBannedRights2.manage_topics;
        tL_chatBannedRights3.send_photos = tL_chatBannedRights.send_photos || tL_chatBannedRights2.send_photos;
        tL_chatBannedRights3.send_videos = tL_chatBannedRights.send_videos || tL_chatBannedRights2.send_videos;
        tL_chatBannedRights3.send_roundvideos = tL_chatBannedRights.send_roundvideos || tL_chatBannedRights2.send_roundvideos;
        tL_chatBannedRights3.send_audios = tL_chatBannedRights.send_audios || tL_chatBannedRights2.send_audios;
        tL_chatBannedRights3.send_voices = tL_chatBannedRights.send_voices || tL_chatBannedRights2.send_voices;
        tL_chatBannedRights3.send_docs = tL_chatBannedRights.send_docs || tL_chatBannedRights2.send_docs;
        if (!tL_chatBannedRights.send_plain && !tL_chatBannedRights2.send_plain) {
            z = false;
        }
        tL_chatBannedRights3.send_plain = z;
        return tL_chatBannedRights3;
    }

    private boolean canBeRestricted(org.telegram.tgnet.TLRPC.TL_chatBannedRights r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.DeleteMessagesBottomSheet.canBeRestricted(org.telegram.tgnet.TLRPC$TL_chatBannedRights):boolean");
    }

    private void fillAction(final ArrayList arrayList, final Action action) {
        if (action.isPresent()) {
            if (!action.isExpandable()) {
                arrayList.add(UItem.asRoundCheckbox(action.type, action.title).setChecked(action.selectedCount > 0));
                return;
            }
            int i = action.type;
            String str = action.title;
            int i2 = action.selectedCount;
            if (i2 <= 0) {
                i2 = action.getCount();
            }
            arrayList.add(UItem.asUserGroupCheckbox(i, str, String.valueOf(i2)).setChecked(action.selectedCount > 0).setCollapsed(action.collapsed).setClickCallback(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    DeleteMessagesBottomSheet.this.lambda$fillAction$7(action, view);
                }
            }));
            if (action.collapsed) {
                return;
            }
            action.forEach(new Utilities.IndexedConsumer() {
                @Override
                public final void accept(Object obj, int i3) {
                    DeleteMessagesBottomSheet.lambda$fillAction$8(arrayList, action, (TLObject) obj, i3);
                }
            });
        }
    }

    public void fillItems(ArrayList arrayList, final UniversalAdapter universalAdapter) {
        boolean z;
        boolean z2 = false;
        if (this.messages == null) {
            return;
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.DeleteAdditionalActions)));
        fillAction(arrayList, this.report);
        fillAction(arrayList, this.deleteAll);
        fillAction(arrayList, this.banOrRestrict);
        if (this.banOrRestrict.isPresent()) {
            if (this.restrict) {
                arrayList.add(UItem.asShadow(null));
                arrayList.add(UItem.asAnimatedHeader(0, this.banOrRestrict.isExpandable() ? LocaleController.formatPluralString("UserRestrictionsCanDoUsers", this.banOrRestrict.selectedCount, new Object[0]) : LocaleController.getString(R.string.UserRestrictionsCanDo)));
                arrayList.add(UItem.asSwitch(0, LocaleController.getString(R.string.UserRestrictionsSend)).setChecked((this.bannedRights.send_plain || this.defaultBannedRights.send_plain) ? false : true).setLocked(this.defaultBannedRights.send_plain));
                final int sendMediaSelectedCount = getSendMediaSelectedCount();
                arrayList.add(UItem.asExpandableSwitch(1, LocaleController.getString(R.string.UserRestrictionsSendMedia), String.format(Locale.US, "%d/9", Integer.valueOf(sendMediaSelectedCount))).setChecked(sendMediaSelectedCount > 0).setLocked(allDefaultMediaBanned()).setCollapsed(this.sendMediaCollapsed).setClickCallback(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        DeleteMessagesBottomSheet.this.lambda$fillItems$9(sendMediaSelectedCount, universalAdapter, view);
                    }
                }));
                if (!this.sendMediaCollapsed) {
                    arrayList.add(UItem.asRoundCheckbox(6, LocaleController.getString(R.string.SendMediaPermissionPhotos)).setChecked((this.bannedRights.send_photos || this.defaultBannedRights.send_photos) ? false : true).setLocked(this.defaultBannedRights.send_photos).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(7, LocaleController.getString(R.string.SendMediaPermissionVideos)).setChecked((this.bannedRights.send_videos || this.defaultBannedRights.send_videos) ? false : true).setLocked(this.defaultBannedRights.send_videos).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(8, LocaleController.getString(R.string.SendMediaPermissionFiles)).setChecked((this.bannedRights.send_docs || this.defaultBannedRights.send_docs) ? false : true).setLocked(this.defaultBannedRights.send_docs).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(9, LocaleController.getString(R.string.SendMediaPermissionMusic)).setChecked((this.bannedRights.send_audios || this.defaultBannedRights.send_audios) ? false : true).setLocked(this.defaultBannedRights.send_audios).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(10, LocaleController.getString(R.string.SendMediaPermissionVoice)).setChecked((this.bannedRights.send_voices || this.defaultBannedRights.send_voices) ? false : true).setLocked(this.defaultBannedRights.send_voices).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(11, LocaleController.getString(R.string.SendMediaPermissionRound)).setChecked((this.bannedRights.send_roundvideos || this.defaultBannedRights.send_roundvideos) ? false : true).setLocked(this.defaultBannedRights.send_roundvideos).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(12, LocaleController.getString(R.string.SendMediaPermissionStickersGifs)).setChecked((this.bannedRights.send_stickers || this.defaultBannedRights.send_stickers) ? false : true).setLocked(this.defaultBannedRights.send_stickers).setPad(1));
                    arrayList.add(UItem.asRoundCheckbox(13, LocaleController.getString(R.string.SendMediaPolls)).setChecked((this.bannedRights.send_polls || this.defaultBannedRights.send_polls) ? false : true).setLocked(this.defaultBannedRights.send_polls).setPad(1));
                    UItem asRoundCheckbox = UItem.asRoundCheckbox(14, LocaleController.getString(R.string.UserRestrictionsEmbedLinks));
                    TLRPC.TL_chatBannedRights tL_chatBannedRights = this.bannedRights;
                    if (!tL_chatBannedRights.embed_links) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.defaultBannedRights;
                        if (!tL_chatBannedRights2.embed_links && !tL_chatBannedRights.send_plain && !tL_chatBannedRights2.send_plain) {
                            z = true;
                            arrayList.add(asRoundCheckbox.setChecked(z).setLocked(this.defaultBannedRights.embed_links).setPad(1));
                        }
                    }
                    z = false;
                    arrayList.add(asRoundCheckbox.setChecked(z).setLocked(this.defaultBannedRights.embed_links).setPad(1));
                }
                arrayList.add(UItem.asSwitch(2, LocaleController.getString(R.string.UserRestrictionsInviteUsers)).setChecked((this.bannedRights.invite_users || this.defaultBannedRights.invite_users) ? false : true).setLocked(this.defaultBannedRights.invite_users));
                arrayList.add(UItem.asSwitch(3, LocaleController.getString(R.string.UserRestrictionsPinMessages)).setChecked((this.bannedRights.pin_messages || this.defaultBannedRights.pin_messages) ? false : true).setLocked(this.defaultBannedRights.pin_messages));
                arrayList.add(UItem.asSwitch(4, LocaleController.getString(R.string.UserRestrictionsChangeInfo)).setChecked((this.bannedRights.change_info || this.defaultBannedRights.change_info) ? false : true).setLocked(this.defaultBannedRights.change_info));
                if (this.isForum) {
                    UItem asSwitch = UItem.asSwitch(5, LocaleController.getString(R.string.CreateTopicsPermission));
                    if (!this.bannedRights.manage_topics && !this.defaultBannedRights.manage_topics) {
                        z2 = true;
                    }
                    arrayList.add(asSwitch.setChecked(z2).setLocked(this.defaultBannedRights.manage_topics));
                }
            }
            if (this.canRestrict) {
                arrayList.add(UItem.asShadowCollapseButton(1, LocaleController.getString(getRestrictToggleTextKey())).setCollapsed(!this.restrict).accent());
            }
        }
    }

    private int getRestrictToggleTextKey() {
        return !this.banOrRestrict.isExpandable() ? this.restrict ? R.string.DeleteToggleBanUser : R.string.DeleteToggleRestrictUser : this.restrict ? R.string.DeleteToggleBanUsers : R.string.DeleteToggleRestrictUsers;
    }

    private int getSendMediaSelectedCount() {
        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.bannedRights;
        int i = (tL_chatBannedRights.send_photos || this.defaultBannedRights.send_photos) ? 0 : 1;
        if (!tL_chatBannedRights.send_videos && !this.defaultBannedRights.send_videos) {
            i++;
        }
        if (!tL_chatBannedRights.send_stickers && !this.defaultBannedRights.send_stickers) {
            i++;
        }
        if (!tL_chatBannedRights.send_audios && !this.defaultBannedRights.send_audios) {
            i++;
        }
        if (!tL_chatBannedRights.send_docs && !this.defaultBannedRights.send_docs) {
            i++;
        }
        if (!tL_chatBannedRights.send_voices && !this.defaultBannedRights.send_voices) {
            i++;
        }
        if (!tL_chatBannedRights.send_roundvideos && !this.defaultBannedRights.send_roundvideos) {
            i++;
        }
        if (!tL_chatBannedRights.embed_links) {
            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.defaultBannedRights;
            if (!tL_chatBannedRights2.embed_links && !tL_chatBannedRights.send_plain && !tL_chatBannedRights2.send_plain) {
                i++;
            }
        }
        return (tL_chatBannedRights.send_polls || this.defaultBannedRights.send_polls) ? i : i + 1;
    }

    private boolean hasAnyDefaultRights() {
        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.defaultBannedRights;
        return (tL_chatBannedRights.send_messages && tL_chatBannedRights.send_media && tL_chatBannedRights.send_stickers && tL_chatBannedRights.send_gifs && tL_chatBannedRights.send_games && tL_chatBannedRights.send_inline && tL_chatBannedRights.embed_links && tL_chatBannedRights.send_polls && tL_chatBannedRights.change_info && tL_chatBannedRights.invite_users && tL_chatBannedRights.pin_messages && (tL_chatBannedRights.manage_topics || !this.isForum) && tL_chatBannedRights.send_photos && tL_chatBannedRights.send_videos && tL_chatBannedRights.send_roundvideos && tL_chatBannedRights.send_audios && tL_chatBannedRights.send_voices && tL_chatBannedRights.send_docs && tL_chatBannedRights.send_plain) ? false : true;
    }

    private static boolean isBanned(TLRPC.TL_chatBannedRights tL_chatBannedRights) {
        return tL_chatBannedRights.view_messages;
    }

    public void lambda$fillAction$7(Action action, View view) {
        saveScrollPosition();
        action.collapseOrExpand();
        applyScrolledPosition(true);
    }

    public static void lambda$fillAction$8(ArrayList arrayList, Action action, TLObject tLObject, int i) {
        arrayList.add(UItem.asUserCheckbox((action.type << 24) | i, tLObject).setChecked(action.checks[i]).setPad(1));
    }

    public void lambda$fillItems$9(int i, UniversalAdapter universalAdapter, View view) {
        if (allDefaultMediaBanned()) {
            new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.UserRestrictionsCantModify)).setMessage(LocaleController.getString(R.string.UserRestrictionsCantModifyDisabled)).setPositiveButton(LocaleController.getString(R.string.OK), null).create().show();
            return;
        }
        boolean z = i <= 0;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.bannedRights;
        boolean z2 = !z;
        tL_chatBannedRights.send_media = z2;
        tL_chatBannedRights.send_photos = z2;
        tL_chatBannedRights.send_videos = z2;
        tL_chatBannedRights.send_stickers = z2;
        tL_chatBannedRights.send_gifs = z2;
        tL_chatBannedRights.send_inline = z2;
        tL_chatBannedRights.send_games = z2;
        tL_chatBannedRights.send_audios = z2;
        tL_chatBannedRights.send_docs = z2;
        tL_chatBannedRights.send_voices = z2;
        tL_chatBannedRights.send_roundvideos = z2;
        tL_chatBannedRights.embed_links = z2;
        tL_chatBannedRights.send_polls = z2;
        onRestrictionsChanged();
        universalAdapter.update(true);
    }

    public void lambda$getTitle$3(int[] iArr, TLObject tLObject, int i) {
        iArr[0] = iArr[0] + this.participantMessageCounts[i];
    }

    public void lambda$new$0(View view, int i, float f, float f2) {
        UItem item = this.adapter.getItem(i - 1);
        if (item == null) {
            return;
        }
        onClick(item, view, i, f, f2);
    }

    public void lambda$new$1(View view) {
        proceed();
    }

    public static TLRPC.TL_chatBannedRights lambda$new$2(TLRPC.ChannelParticipant channelParticipant) {
        if (channelParticipant == null) {
            return null;
        }
        return channelParticipant.banned_rights;
    }

    public boolean lambda$performDelete$10(MessageObject messageObject) {
        TLRPC.Peer peer = messageObject.messageOwner.peer_id;
        return !(peer == null || peer.chat_id == (-this.mergeDialogId)) || this.mergeDialogId == 0;
    }

    public boolean lambda$performDelete$11(MessageObject messageObject) {
        TLRPC.Peer peer = messageObject.messageOwner.peer_id;
        if (peer != null) {
            long j = peer.chat_id;
            long j2 = this.mergeDialogId;
            if (j == (-j2) && j2 != 0) {
                return true;
            }
        }
        return false;
    }

    public void lambda$performDelete$12(TLObject tLObject, int i) {
        MessagesController messagesController;
        long j;
        TLRPC.Chat chat;
        boolean z;
        boolean z2;
        TLRPC.User user;
        MessagesController messagesController2;
        long j2;
        TLRPC.Chat chat2;
        BaseFragment baseFragment;
        TLRPC.User user2;
        if (this.restrict) {
            TLRPC.TL_chatBannedRights bannedRightsOr = bannedRightsOr(this.bannedRights, (TLRPC.TL_chatBannedRights) this.participantsBannedRights.get(i));
            if (tLObject instanceof TLRPC.User) {
                messagesController2 = MessagesController.getInstance(this.currentAccount);
                j2 = this.inChat.id;
                user2 = (TLRPC.User) tLObject;
                baseFragment = getBaseFragment();
                chat2 = null;
            } else {
                if (!(tLObject instanceof TLRPC.Chat)) {
                    return;
                }
                messagesController2 = MessagesController.getInstance(this.currentAccount);
                j2 = this.inChat.id;
                chat2 = (TLRPC.Chat) tLObject;
                baseFragment = getBaseFragment();
                user2 = null;
            }
            messagesController2.setParticipantBannedRole(j2, user2, chat2, bannedRightsOr, false, baseFragment);
            return;
        }
        if (tLObject instanceof TLRPC.User) {
            messagesController = MessagesController.getInstance(this.currentAccount);
            j = this.inChat.id;
            user = (TLRPC.User) tLObject;
            z = false;
            z2 = false;
            chat = null;
        } else {
            if (!(tLObject instanceof TLRPC.Chat)) {
                return;
            }
            messagesController = MessagesController.getInstance(this.currentAccount);
            j = this.inChat.id;
            chat = (TLRPC.Chat) tLObject;
            z = false;
            z2 = false;
            user = null;
        }
        messagesController.deleteParticipantFromChat(j, user, chat, z, z2);
    }

    public boolean lambda$performDelete$13(MessageObject messageObject) {
        TLRPC.Peer peer = messageObject.messageOwner.peer_id;
        return (peer == null || peer.chat_id == (-this.mergeDialogId)) ? false : true;
    }

    public static boolean lambda$performDelete$14(TLObject tLObject, MessageObject messageObject) {
        return tLObject instanceof TLRPC.User ? messageObject.messageOwner.from_id.user_id == ((TLRPC.User) tLObject).id : (tLObject instanceof TLRPC.Chat) && messageObject.messageOwner.from_id.user_id == ((TLRPC.Chat) tLObject).id;
    }

    public void lambda$performDelete$15(final TLObject tLObject, int i) {
        TLRPC.InputPeer inputPeer;
        TLRPC.TL_channels_reportSpam tL_channels_reportSpam = new TLRPC.TL_channels_reportSpam();
        tL_channels_reportSpam.channel = MessagesController.getInputChannel(this.inChat);
        if (!(tLObject instanceof TLRPC.User)) {
            if (tLObject instanceof TLRPC.Chat) {
                inputPeer = MessagesController.getInputPeer((TLRPC.Chat) tLObject);
            }
            tL_channels_reportSpam.id = (ArrayList) Collection$EL.stream(this.messages).filter(new Predicate() {
                @Override
                public Predicate and(Predicate predicate) {
                    return Predicate.CC.$default$and(this, predicate);
                }

                @Override
                public Predicate negate() {
                    return Predicate.CC.$default$negate(this);
                }

                @Override
                public Predicate or(Predicate predicate) {
                    return Predicate.CC.$default$or(this, predicate);
                }

                @Override
                public final boolean test(Object obj) {
                    boolean lambda$performDelete$13;
                    lambda$performDelete$13 = DeleteMessagesBottomSheet.this.lambda$performDelete$13((MessageObject) obj);
                    return lambda$performDelete$13;
                }
            }).filter(new Predicate() {
                @Override
                public Predicate and(Predicate predicate) {
                    return Predicate.CC.$default$and(this, predicate);
                }

                @Override
                public Predicate negate() {
                    return Predicate.CC.$default$negate(this);
                }

                @Override
                public Predicate or(Predicate predicate) {
                    return Predicate.CC.$default$or(this, predicate);
                }

                @Override
                public final boolean test(Object obj) {
                    boolean lambda$performDelete$14;
                    lambda$performDelete$14 = DeleteMessagesBottomSheet.lambda$performDelete$14(TLObject.this, (MessageObject) obj);
                    return lambda$performDelete$14;
                }
            }).map(new DeleteMessagesBottomSheet$$ExternalSyntheticLambda8()).collect(Collectors.toCollection(new ChatActivity$$ExternalSyntheticLambda295()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_reportSpam, null);
        }
        inputPeer = MessagesController.getInputPeer((TLRPC.User) tLObject);
        tL_channels_reportSpam.participant = inputPeer;
        tL_channels_reportSpam.id = (ArrayList) Collection$EL.stream(this.messages).filter(new Predicate() {
            @Override
            public Predicate and(Predicate predicate) {
                return Predicate.CC.$default$and(this, predicate);
            }

            @Override
            public Predicate negate() {
                return Predicate.CC.$default$negate(this);
            }

            @Override
            public Predicate or(Predicate predicate) {
                return Predicate.CC.$default$or(this, predicate);
            }

            @Override
            public final boolean test(Object obj) {
                boolean lambda$performDelete$13;
                lambda$performDelete$13 = DeleteMessagesBottomSheet.this.lambda$performDelete$13((MessageObject) obj);
                return lambda$performDelete$13;
            }
        }).filter(new Predicate() {
            @Override
            public Predicate and(Predicate predicate) {
                return Predicate.CC.$default$and(this, predicate);
            }

            @Override
            public Predicate negate() {
                return Predicate.CC.$default$negate(this);
            }

            @Override
            public Predicate or(Predicate predicate) {
                return Predicate.CC.$default$or(this, predicate);
            }

            @Override
            public final boolean test(Object obj) {
                boolean lambda$performDelete$14;
                lambda$performDelete$14 = DeleteMessagesBottomSheet.lambda$performDelete$14(TLObject.this, (MessageObject) obj);
                return lambda$performDelete$14;
            }
        }).map(new DeleteMessagesBottomSheet$$ExternalSyntheticLambda8()).collect(Collectors.toCollection(new ChatActivity$$ExternalSyntheticLambda295()));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_reportSpam, null);
    }

    public void lambda$performDelete$16(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC.User) {
            MessagesController.getInstance(this.currentAccount).deleteUserChannelHistory(this.inChat, (TLRPC.User) tLObject, null, 0);
        } else if (tLObject instanceof TLRPC.Chat) {
            MessagesController.getInstance(this.currentAccount).deleteUserChannelHistory(this.inChat, null, (TLRPC.Chat) tLObject, 0);
        }
    }

    public static boolean lambda$updateParticipantMessageCounts$4(TLRPC.InputPeer inputPeer, MessageObject messageObject) {
        return MessageObject.peersEqual(inputPeer, messageObject.messageOwner.from_id);
    }

    public void lambda$updateParticipantMessageCounts$5(TLObject tLObject, final TLRPC.InputPeer inputPeer, int i, int[] iArr) {
        if (tLObject instanceof TLRPC.TL_messages_channelMessages) {
            this.participantMessageCounts[i] = ((TLRPC.TL_messages_channelMessages) tLObject).count - ((int) Collection$EL.stream(this.messages).filter(new Predicate() {
                @Override
                public Predicate and(Predicate predicate) {
                    return Predicate.CC.$default$and(this, predicate);
                }

                @Override
                public Predicate negate() {
                    return Predicate.CC.$default$negate(this);
                }

                @Override
                public Predicate or(Predicate predicate) {
                    return Predicate.CC.$default$or(this, predicate);
                }

                @Override
                public final boolean test(Object obj) {
                    boolean lambda$updateParticipantMessageCounts$4;
                    lambda$updateParticipantMessageCounts$4 = DeleteMessagesBottomSheet.lambda$updateParticipantMessageCounts$4(TLRPC.InputPeer.this, (MessageObject) obj);
                    return lambda$updateParticipantMessageCounts$4;
                }
            }).count());
        }
        int i2 = iArr[0] - 1;
        iArr[0] = i2;
        if (i2 == 0) {
            this.participantMessageCountsLoading = false;
            this.participantMessageCountsLoaded = true;
            updateTitleAnimated();
        }
    }

    public void lambda$updateParticipantMessageCounts$6(final TLRPC.InputPeer inputPeer, final int i, final int[] iArr, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                DeleteMessagesBottomSheet.this.lambda$updateParticipantMessageCounts$5(tLObject, inputPeer, i, iArr);
            }
        });
    }

    private void onClick(UItem uItem, View view, int i, float f, float f2) {
        Action action;
        Action action2;
        int i2 = uItem.viewType;
        if (i2 == 37) {
            int i3 = uItem.id;
            int i4 = i3 >>> 24;
            int i5 = i3 & 16777215;
            if (i4 == 0) {
                action2 = this.report;
            } else if (i4 == 1) {
                this.deleteAll.toggleCheck(i5);
            } else if (i4 != 2) {
                return;
            } else {
                action2 = this.banOrRestrict;
            }
            action2.toggleCheck(i5);
            return;
        }
        if (i2 != 36 && i2 != 35) {
            if (i2 != 39) {
                if (i2 == 40) {
                    this.sendMediaCollapsed = !this.sendMediaCollapsed;
                    saveScrollPosition();
                    this.adapter.update(true);
                    applyScrolledPosition(true);
                    return;
                }
                if (i2 == 38) {
                    boolean z = !this.restrict;
                    this.restrict = z;
                    this.banOrRestrict.setFilter(z ? this.restrictFilter : this.banFilter);
                    this.adapter.update(true);
                    onRestrictionsChanged();
                    return;
                }
                return;
            }
            if (uItem.locked) {
                new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.UserRestrictionsCantModify)).setMessage(LocaleController.getString(R.string.UserRestrictionsCantModifyDisabled)).setPositiveButton(LocaleController.getString(R.string.OK), null).create().show();
                return;
            }
            int i6 = uItem.id;
            if (i6 == 2) {
                this.bannedRights.invite_users = !r4.invite_users;
            } else if (i6 == 3) {
                this.bannedRights.pin_messages = !r4.pin_messages;
            } else if (i6 == 4) {
                this.bannedRights.change_info = !r4.change_info;
            } else {
                if (i6 != 5) {
                    if (i6 == 0) {
                        this.bannedRights.send_plain = !r4.send_plain;
                    }
                    this.adapter.update(true);
                    return;
                }
                this.bannedRights.manage_topics = !r4.manage_topics;
            }
            onRestrictionsChanged();
            this.adapter.update(true);
            return;
        }
        int i7 = uItem.id;
        if (i7 == 0) {
            action = this.report;
        } else if (i7 == 1) {
            this.deleteAll.toggleAllChecks();
        } else {
            if (i7 != 2) {
                if (i2 == 35) {
                    if (uItem.locked) {
                        new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.UserRestrictionsCantModify)).setMessage(LocaleController.getString(R.string.UserRestrictionsCantModifyDisabled)).setPositiveButton(LocaleController.getString(R.string.OK), null).create().show();
                        return;
                    }
                    if (i7 == 6) {
                        this.bannedRights.send_photos = !r4.send_photos;
                    } else if (i7 == 7) {
                        this.bannedRights.send_videos = !r4.send_videos;
                    } else if (i7 == 9) {
                        this.bannedRights.send_audios = !r4.send_audios;
                    } else if (i7 == 8) {
                        this.bannedRights.send_docs = !r4.send_docs;
                    } else if (i7 == 11) {
                        this.bannedRights.send_roundvideos = !r4.send_roundvideos;
                    } else if (i7 == 10) {
                        this.bannedRights.send_voices = !r4.send_voices;
                    } else if (i7 == 12) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.bannedRights;
                        boolean z2 = !tL_chatBannedRights.send_stickers;
                        tL_chatBannedRights.send_inline = z2;
                        tL_chatBannedRights.send_gifs = z2;
                        tL_chatBannedRights.send_games = z2;
                        tL_chatBannedRights.send_stickers = z2;
                    } else {
                        if (i7 != 14) {
                            if (i7 == 13) {
                                this.bannedRights.send_polls = !r4.send_polls;
                            }
                            this.adapter.update(true);
                            return;
                        }
                        TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.bannedRights;
                        if (tL_chatBannedRights2.send_plain || this.defaultBannedRights.send_plain) {
                            int i8 = 0;
                            while (true) {
                                if (i8 >= this.adapter.getItemCount()) {
                                    break;
                                }
                                UItem item = this.adapter.getItem(i8);
                                if (item.viewType == 39 && item.id == 0) {
                                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.recyclerListView.findViewHolderForAdapterPosition(i8 + 1);
                                    if (findViewHolderForAdapterPosition != null) {
                                        View view2 = findViewHolderForAdapterPosition.itemView;
                                        float f3 = -this.shiftDp;
                                        this.shiftDp = f3;
                                        AndroidUtilities.shakeViewSpring(view2, f3);
                                    }
                                } else {
                                    i8++;
                                }
                            }
                            BotWebViewVibrationEffect.APP_ERROR.vibrate();
                            return;
                        }
                        tL_chatBannedRights2.embed_links = !tL_chatBannedRights2.embed_links;
                    }
                    onRestrictionsChanged();
                    this.adapter.update(true);
                    return;
                }
                return;
            }
            action = this.banOrRestrict;
        }
        action.toggleAllChecks();
        return;
        onDeleteAllChanged();
    }

    private void onDeleteAllChanged() {
        if (this.participantMessageCountsLoaded) {
            updateTitleAnimated();
        } else {
            updateParticipantMessageCounts();
        }
    }

    private void onRestrictionsChanged() {
        if (this.restrict && this.banOrRestrict.isPresent()) {
            this.banChecked = this.banOrRestrict.selectedCount > 0;
        }
        if (this.restrict && this.banOrRestrict.isPresent()) {
            Action action = this.banOrRestrict;
            if (action.selectedCount == 0) {
                action.toggleAllChecks();
                if (this.restrict && this.banOrRestrict.isPresent()) {
                    this.banChecked = this.banOrRestrict.selectedCount > 0;
                    return;
                }
            }
        }
        if (!this.restrict && this.banOrRestrict.isPresent()) {
            boolean z = this.banChecked;
            Action action2 = this.banOrRestrict;
            if (z != (action2.selectedCount > 0)) {
                action2.toggleAllChecks();
            }
        }
        if (this.restrict) {
        }
    }

    private void performDelete() {
        ArrayList<Integer> arrayList = (ArrayList) Collection$EL.stream(this.messages).filter(new Predicate() {
            @Override
            public Predicate and(Predicate predicate) {
                return Predicate.CC.$default$and(this, predicate);
            }

            @Override
            public Predicate negate() {
                return Predicate.CC.$default$negate(this);
            }

            @Override
            public Predicate or(Predicate predicate) {
                return Predicate.CC.$default$or(this, predicate);
            }

            @Override
            public final boolean test(Object obj) {
                boolean lambda$performDelete$10;
                lambda$performDelete$10 = DeleteMessagesBottomSheet.this.lambda$performDelete$10((MessageObject) obj);
                return lambda$performDelete$10;
            }
        }).map(new DeleteMessagesBottomSheet$$ExternalSyntheticLambda8()).collect(Collectors.toCollection(new ChatActivity$$ExternalSyntheticLambda295()));
        ArrayList<Integer> arrayList2 = (ArrayList) Collection$EL.stream(this.messages).filter(new Predicate() {
            @Override
            public Predicate and(Predicate predicate) {
                return Predicate.CC.$default$and(this, predicate);
            }

            @Override
            public Predicate negate() {
                return Predicate.CC.$default$negate(this);
            }

            @Override
            public Predicate or(Predicate predicate) {
                return Predicate.CC.$default$or(this, predicate);
            }

            @Override
            public final boolean test(Object obj) {
                boolean lambda$performDelete$11;
                lambda$performDelete$11 = DeleteMessagesBottomSheet.this.lambda$performDelete$11((MessageObject) obj);
                return lambda$performDelete$11;
            }
        }).map(new DeleteMessagesBottomSheet$$ExternalSyntheticLambda8()).collect(Collectors.toCollection(new ChatActivity$$ExternalSyntheticLambda295()));
        if (!arrayList.isEmpty()) {
            MessagesController.getInstance(this.currentAccount).deleteMessages(arrayList, null, null, -this.inChat.id, this.topicId, false, this.mode);
        }
        if (!arrayList2.isEmpty()) {
            MessagesController.getInstance(this.currentAccount).deleteMessages(arrayList2, null, null, this.mergeDialogId, this.topicId, true, this.mode);
        }
        this.banOrRestrict.forEachSelected(new Utilities.IndexedConsumer() {
            @Override
            public final void accept(Object obj, int i) {
                DeleteMessagesBottomSheet.this.lambda$performDelete$12((TLObject) obj, i);
            }
        });
        this.report.forEachSelected(new Utilities.IndexedConsumer() {
            @Override
            public final void accept(Object obj, int i) {
                DeleteMessagesBottomSheet.this.lambda$performDelete$15((TLObject) obj, i);
            }
        });
        this.deleteAll.forEachSelected(new Utilities.IndexedConsumer() {
            @Override
            public final void accept(Object obj, int i) {
                DeleteMessagesBottomSheet.this.lambda$performDelete$16((TLObject) obj, i);
            }
        });
    }

    private void proceed() {
        StringBuilder sb;
        String formatPluralString;
        dismiss();
        Runnable runnable = this.onDelete;
        if (runnable != null) {
            runnable.run();
        }
        String str = "";
        if (this.report.selectedCount > 0) {
            str = "" + LocaleController.formatPluralString("UsersReported", this.report.selectedCount, new Object[0]);
        }
        if (this.banOrRestrict.selectedCount > 0) {
            if (!TextUtils.isEmpty(str)) {
                str = str + "\n";
            }
            if (this.restrict) {
                sb = new StringBuilder();
                sb.append(str);
                formatPluralString = LocaleController.formatPluralString("UsersRestricted", this.banOrRestrict.selectedCount, new Object[0]);
            } else {
                sb = new StringBuilder();
                sb.append(str);
                formatPluralString = LocaleController.formatPluralString("UsersBanned", this.banOrRestrict.selectedCount, new Object[0]);
            }
            sb.append(formatPluralString);
            str = sb.toString();
        }
        int i = this.banOrRestrict.selectedCount > 0 ? R.raw.ic_admin : R.raw.contact_check;
        (TextUtils.isEmpty(str) ? BulletinFactory.of(getBaseFragment()).createSimpleBulletin(i, LocaleController.getString(R.string.MessagesDeleted)) : BulletinFactory.of(getBaseFragment()).createSimpleBulletin(i, LocaleController.getString(R.string.MessagesDeleted), str)).show();
        performDelete();
    }

    private void updateParticipantMessageCounts() {
        if (this.participantMessageCountsLoading) {
            return;
        }
        this.participantMessageCountsLoading = true;
        int i = this.deleteAll.totalCount;
        this.participantMessageCounts = new int[i];
        final int[] iArr = {i};
        for (final int i2 = 0; i2 < this.deleteAll.totalCount; i2++) {
            TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.peer = MessagesController.getInputPeer(this.inChat);
            tL_messages_search.q = "";
            final TLRPC.InputPeer inputPeer = MessagesController.getInputPeer((TLObject) this.deleteAll.options.get(i2));
            tL_messages_search.from_id = inputPeer;
            tL_messages_search.flags |= 1;
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            tL_messages_search.limit = 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_search, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    DeleteMessagesBottomSheet.this.lambda$updateParticipantMessageCounts$6(inputPeer, i2, iArr, tLObject, tL_error);
                }
            });
        }
    }

    @Override
    protected boolean canHighlightChildAt(View view, float f, float f2) {
        return !(view instanceof CollapseTextCell);
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        UniversalAdapter universalAdapter = new UniversalAdapter(recyclerListView, getContext(), this.currentAccount, getBaseFragment().getClassGuid(), true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                DeleteMessagesBottomSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, this.resourcesProvider);
        this.adapter = universalAdapter;
        return universalAdapter;
    }

    @Override
    protected CharSequence getTitle() {
        ArrayList arrayList = this.messages;
        final int[] iArr = {arrayList != null ? arrayList.size() : 0};
        if (this.participantMessageCounts != null && this.participantMessageCountsLoaded) {
            this.deleteAll.forEachSelected(new Utilities.IndexedConsumer() {
                @Override
                public final void accept(Object obj, int i) {
                    DeleteMessagesBottomSheet.this.lambda$getTitle$3(iArr, (TLObject) obj, i);
                }
            });
        }
        return LocaleController.formatPluralString("DeleteOptionsTitle", iArr[0], new Object[0]);
    }

    @Override
    public void show() {
        super.show();
        Bulletin.hideVisible();
    }
}
