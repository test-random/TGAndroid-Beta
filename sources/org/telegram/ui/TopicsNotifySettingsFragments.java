package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TopicExceptionCell;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.TopicsNotifySettingsFragments;

public class TopicsNotifySettingsFragments extends BaseFragment {
    private final int VIEW_TYPE_ADD_EXCEPTION;
    private final int VIEW_TYPE_DELETE_ALL;
    private final int VIEW_TYPE_DIVIDER;
    private final int VIEW_TYPE_TOPIC;
    Adapter adapter;
    long dialogId;
    HashSet exceptionsTopics;
    ArrayList items;
    RecyclerListView recyclerListView;

    public class AnonymousClass2 implements RecyclerListView.OnItemClickListener {

        public class AnonymousClass1 implements ProfileNotificationsActivity.ProfileNotificationsActivityDelegate {
            final TLRPC.TL_forumTopic val$topic;

            AnonymousClass1(TLRPC.TL_forumTopic tL_forumTopic) {
                this.val$topic = tL_forumTopic;
            }

            public void lambda$didRemoveException$0(TLRPC.TL_forumTopic tL_forumTopic) {
                TopicsNotifySettingsFragments.this.exceptionsTopics.remove(Integer.valueOf(tL_forumTopic.id));
                TopicsNotifySettingsFragments.this.updateRows();
            }

            @Override
            public void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
            }

            @Override
            public void didRemoveException(long j) {
                TopicsNotifySettingsFragments.this.removeException(this.val$topic.id);
                final TLRPC.TL_forumTopic tL_forumTopic = this.val$topic;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        TopicsNotifySettingsFragments.AnonymousClass2.AnonymousClass1.this.lambda$didRemoveException$0(tL_forumTopic);
                    }
                }, 300L);
            }
        }

        AnonymousClass2() {
        }

        public void lambda$onItemClick$0(TLRPC.TL_forumTopic tL_forumTopic, NotificationsSettingsActivity.NotificationException notificationException) {
            TopicsNotifySettingsFragments.this.exceptionsTopics.add(Integer.valueOf(tL_forumTopic.id));
            TopicsNotifySettingsFragments.this.updateRows();
        }

        public void lambda$onItemClick$1(final TLRPC.TL_forumTopic tL_forumTopic) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", TopicsNotifySettingsFragments.this.dialogId);
            bundle.putLong("topic_id", tL_forumTopic.id);
            bundle.putBoolean("exception", true);
            ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle);
            profileNotificationsActivity.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() {
                @Override
                public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
                    TopicsNotifySettingsFragments.AnonymousClass2.this.lambda$onItemClick$0(tL_forumTopic, notificationException);
                }

                @Override
                public void didRemoveException(long j) {
                    ProfileNotificationsActivity.ProfileNotificationsActivityDelegate.CC.$default$didRemoveException(this, j);
                }
            });
            TopicsNotifySettingsFragments.this.presentFragment(profileNotificationsActivity);
        }

        public void lambda$onItemClick$2(AlertDialog alertDialog, int i) {
            Iterator it = TopicsNotifySettingsFragments.this.exceptionsTopics.iterator();
            while (it.hasNext()) {
                TopicsNotifySettingsFragments.this.removeException(((Integer) it.next()).intValue());
            }
            TopicsNotifySettingsFragments.this.exceptionsTopics.clear();
            TopicsNotifySettingsFragments.this.updateRows();
        }

        @Override
        public void onItemClick(View view, int i) {
            if (((Item) TopicsNotifySettingsFragments.this.items.get(i)).viewType == 1) {
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", -TopicsNotifySettingsFragments.this.dialogId);
                bundle.putBoolean("for_select", true);
                TopicsFragment topicsFragment = new TopicsFragment(bundle);
                topicsFragment.setExcludeTopics(TopicsNotifySettingsFragments.this.exceptionsTopics);
                topicsFragment.setOnTopicSelectedListener(new TopicsFragment.OnTopicSelectedListener() {
                    @Override
                    public final void onTopicSelected(TLRPC.TL_forumTopic tL_forumTopic) {
                        TopicsNotifySettingsFragments.AnonymousClass2.this.lambda$onItemClick$1(tL_forumTopic);
                    }
                });
                TopicsNotifySettingsFragments.this.presentFragment(topicsFragment);
            }
            if (((Item) TopicsNotifySettingsFragments.this.items.get(i)).viewType == 2) {
                TLRPC.TL_forumTopic tL_forumTopic = ((Item) TopicsNotifySettingsFragments.this.items.get(i)).topic;
                Bundle bundle2 = new Bundle();
                bundle2.putLong("dialog_id", TopicsNotifySettingsFragments.this.dialogId);
                bundle2.putLong("topic_id", tL_forumTopic.id);
                bundle2.putBoolean("exception", false);
                ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(bundle2);
                profileNotificationsActivity.setDelegate(new AnonymousClass1(tL_forumTopic));
                TopicsNotifySettingsFragments.this.presentFragment(profileNotificationsActivity);
            }
            if (((Item) TopicsNotifySettingsFragments.this.items.get(i)).viewType == 4) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TopicsNotifySettingsFragments.this.getParentActivity());
                builder.setTitle(LocaleController.getString(R.string.NotificationsDeleteAllExceptionTitle));
                builder.setMessage(LocaleController.getString(R.string.NotificationsDeleteAllExceptionAlert));
                builder.setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() {
                    @Override
                    public final void onClick(AlertDialog alertDialog, int i2) {
                        TopicsNotifySettingsFragments.AnonymousClass2.this.lambda$onItemClick$2(alertDialog, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                AlertDialog create = builder.create();
                TopicsNotifySettingsFragments.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                }
            }
        }
    }

    public class Adapter extends AdapterWithDiffUtils {
        private Adapter() {
        }

        @Override
        public int getItemCount() {
            return TopicsNotifySettingsFragments.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            return ((Item) TopicsNotifySettingsFragments.this.items.get(i)).viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 2 || viewHolder.getItemViewType() == 4;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (((Item) TopicsNotifySettingsFragments.this.items.get(i)).viewType == 2) {
                TopicExceptionCell topicExceptionCell = (TopicExceptionCell) viewHolder.itemView;
                TopicsNotifySettingsFragments topicsNotifySettingsFragments = TopicsNotifySettingsFragments.this;
                topicExceptionCell.setTopic(topicsNotifySettingsFragments.dialogId, ((Item) topicsNotifySettingsFragments.items.get(i)).topic);
                boolean z = true;
                if (i != TopicsNotifySettingsFragments.this.items.size() - 1 && ((Item) TopicsNotifySettingsFragments.this.items.get(i + 1)).viewType != 2) {
                    z = false;
                }
                topicExceptionCell.drawDivider = z;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            View view3;
            if (i == 1) {
                TextCell textCell = new TextCell(viewGroup.getContext());
                textCell.setTextAndIcon((CharSequence) LocaleController.getString(R.string.NotificationsAddAnException), R.drawable.msg_contact_add, true);
                textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                view3 = textCell;
            } else if (i == 2) {
                view3 = new TopicExceptionCell(viewGroup.getContext());
            } else {
                if (i == 3) {
                    view = new ShadowSectionCell(viewGroup.getContext());
                    view2 = view;
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view2);
                }
                if (i != 4) {
                    view2 = null;
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view2);
                }
                TextCell textCell2 = new TextCell(viewGroup.getContext());
                textCell2.setText(LocaleController.getString(R.string.NotificationsDeleteAllException), false);
                textCell2.setColors(-1, Theme.key_text_RedRegular);
                view3 = textCell2;
            }
            view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            view = view3;
            view2 = view;
            view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view2);
        }
    }

    public class Item extends AdapterWithDiffUtils.Item {
        final TLRPC.TL_forumTopic topic;

        private Item(int i, TLRPC.TL_forumTopic tL_forumTopic) {
            super(i, false);
            this.topic = tL_forumTopic;
        }

        public boolean equals(Object obj) {
            TLRPC.TL_forumTopic tL_forumTopic;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            if (this.viewType != item.viewType) {
                return false;
            }
            TLRPC.TL_forumTopic tL_forumTopic2 = this.topic;
            return tL_forumTopic2 == null || (tL_forumTopic = item.topic) == null || tL_forumTopic2.id == tL_forumTopic.id;
        }
    }

    public TopicsNotifySettingsFragments(Bundle bundle) {
        super(bundle);
        this.VIEW_TYPE_ADD_EXCEPTION = 1;
        this.VIEW_TYPE_TOPIC = 2;
        this.VIEW_TYPE_DIVIDER = 3;
        this.VIEW_TYPE_DELETE_ALL = 4;
        this.items = new ArrayList();
        this.exceptionsTopics = new HashSet();
    }

    public static void lambda$removeException$0(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void removeException(int i) {
        getNotificationsController().getNotificationsSettingsFacade().clearPreference(this.dialogId, i);
        TL_account.updateNotifySettings updatenotifysettings = new TL_account.updateNotifySettings();
        updatenotifysettings.settings = new TLRPC.TL_inputPeerNotifySettings();
        TLRPC.TL_inputNotifyForumTopic tL_inputNotifyForumTopic = new TLRPC.TL_inputNotifyForumTopic();
        tL_inputNotifyForumTopic.peer = getMessagesController().getInputPeer(this.dialogId);
        tL_inputNotifyForumTopic.top_msg_id = i;
        updatenotifysettings.peer = tL_inputNotifyForumTopic;
        getConnectionsManager().sendRequest(updatenotifysettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TopicsNotifySettingsFragments.lambda$removeException$0(tLObject, tL_error);
            }
        });
    }

    public void updateRows() {
        ArrayList arrayList;
        int i = 0;
        int i2 = 1;
        TLRPC.TL_forumTopic tL_forumTopic = null;
        Object[] objArr = 0;
        Object[] objArr2 = 0;
        Object[] objArr3 = 0;
        Object[] objArr4 = 0;
        Object[] objArr5 = 0;
        Object[] objArr6 = 0;
        Object[] objArr7 = 0;
        Object[] objArr8 = 0;
        if ((this.isPaused || this.adapter == null) ? false : true) {
            arrayList = new ArrayList();
            arrayList.addAll(this.items);
        } else {
            arrayList = null;
        }
        this.items.clear();
        this.items.add(new Item(i2, tL_forumTopic));
        ArrayList<TLRPC.TL_forumTopic> topics = getMessagesController().getTopicsController().getTopics(-this.dialogId);
        if (topics != null) {
            int i3 = 0;
            while (i < topics.size()) {
                if (this.exceptionsTopics.contains(Integer.valueOf(topics.get(i).id))) {
                    this.items.add(new Item(2, topics.get(i)));
                    i3 = 1;
                }
                i++;
            }
            i = i3;
        }
        int i4 = 3;
        if (i != 0) {
            this.items.add(new Item(i4, objArr6 == true ? 1 : 0));
            this.items.add(new Item(4, objArr4 == true ? 1 : 0));
        }
        this.items.add(new Item(i4, objArr2 == true ? 1 : 0));
        Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.setItems(arrayList, this.items);
        }
    }

    @Override
    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    TopicsNotifySettingsFragments.this.lambda$onBackPressed$323();
                }
            }
        });
        this.actionBar.setTitle(LocaleController.getString(R.string.NotificationsExceptions));
        this.recyclerListView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView = this.recyclerListView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.recyclerListView.setOnItemClickListener(new AnonymousClass2());
        frameLayout.addView(this.recyclerListView);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        return this.fragmentView;
    }

    @Override
    public boolean onFragmentCreate() {
        this.dialogId = this.arguments.getLong("dialog_id");
        updateRows();
        return super.onFragmentCreate();
    }

    public void setExceptions(HashSet hashSet) {
        this.exceptionsTopics = hashSet;
    }
}
