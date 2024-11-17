package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContactsEmptyView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Stories.DialogStoriesCell;
import org.telegram.ui.Stories.StoriesController;

public abstract class ContactsAdapter extends RecyclerListView.SectionsAdapter {
    DialogStoriesCell dialogStoriesCell;
    private boolean disableSections;
    BaseFragment fragment;
    public boolean hasStories;
    private LongSparseArray ignoreUsers;
    private boolean isAdmin;
    private boolean isChannel;
    public boolean isEmpty;
    private Context mContext;
    private boolean needPhonebook;
    private ArrayList onlineContacts;
    private int onlyUsers;
    private LongSparseArray selectedContacts;
    private int sortType;
    private int currentAccount = UserConfig.selectedAccount;
    public ArrayList userStories = new ArrayList();

    public ContactsAdapter(Context context, BaseFragment baseFragment, int i, boolean z, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, int i2, boolean z2) {
        this.mContext = context;
        this.onlyUsers = i;
        this.needPhonebook = z;
        this.ignoreUsers = longSparseArray;
        this.selectedContacts = longSparseArray2;
        this.isAdmin = i2 != 0;
        this.isChannel = i2 == 2;
        this.fragment = baseFragment;
    }

    private int getCountForSectionInternal(int i) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        boolean z = this.hasStories;
        if (z && i == 1) {
            return this.userStories.size() + 1;
        }
        if (z && i > 1) {
            i--;
        }
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                return (this.isAdmin || this.needPhonebook) ? 2 : 4;
            }
            if (this.isEmpty) {
                return 1;
            }
            if (this.sortType != 2) {
                int i2 = i - 1;
                if (i2 < arrayList.size()) {
                    int size = hashMap.get(arrayList.get(i2)).size();
                    return (i2 != arrayList.size() - 1 || this.needPhonebook) ? size + 1 : size;
                }
            } else if (i == 1) {
                if (this.onlineContacts.isEmpty()) {
                    return 0;
                }
                return this.onlineContacts.size() + 1;
            }
        } else {
            if (this.isEmpty) {
                return 1;
            }
            if (i < arrayList.size()) {
                int size2 = hashMap.get(arrayList.get(i)).size();
                return (i != arrayList.size() - 1 || this.needPhonebook) ? size2 + 1 : size2;
            }
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        }
        return 0;
    }

    public static int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC.TL_contact r4, org.telegram.tgnet.TLRPC.TL_contact r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.ContactsAdapter.lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLRPC$TL_contact, org.telegram.tgnet.TLRPC$TL_contact):int");
    }

    @Override
    public int getCountForSection(int i) {
        return getCountForSectionInternal(i);
    }

    @Override
    public int getHash(int i, int i2) {
        boolean z = this.hasStories;
        if (z && i == 1) {
            return i2 == this.userStories.size() ? Objects.hash(Integer.valueOf((-49612) * i), getItem(i, i2)) : Objects.hash(Integer.valueOf((-54323) * i), getItem(i, i2));
        }
        return Objects.hash(Integer.valueOf(((!z || i <= 1) ? i : i - 1) * (-49612)), getItem(i, i2));
    }

    @Override
    public Object getItem(int i, int i2) {
        if (getItemViewType(i, i2) == 2) {
            return this.hasStories ? "Stories" : "Header";
        }
        boolean z = this.hasStories;
        if (z && i == 1) {
            return i2 == this.userStories.size() ? "Header" : Long.valueOf(DialogObject.getPeerDialogId(((TL_stories.PeerStories) this.userStories.get(i2)).peer));
        }
        if (z && i > 1) {
            i--;
        }
        HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (i < arrayList.size()) {
                ArrayList<TLRPC.TL_contact> arrayList2 = hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList2.get(i2).user_id));
                }
            }
            return null;
        }
        if (i == 0) {
            return null;
        }
        if (this.sortType != 2) {
            int i3 = i - 1;
            if (i3 < arrayList.size()) {
                ArrayList<TLRPC.TL_contact> arrayList3 = hashMap.get(arrayList.get(i3));
                if (i2 < arrayList3.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList3.get(i2).user_id));
                }
                return null;
            }
        } else if (i == 1) {
            if (i2 < this.onlineContacts.size()) {
                return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC.TL_contact) this.onlineContacts.get(i2)).user_id));
            }
            return null;
        }
        if (!this.needPhonebook || i2 < 0 || i2 >= ContactsController.getInstance(this.currentAccount).phoneBookContacts.size()) {
            return null;
        }
        return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
    }

    @Override
    public int getItemViewType(int i, int i2) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        boolean z = this.hasStories;
        if (z && i == 1) {
            return i2 == this.userStories.size() ? 2 : 0;
        }
        if (z && i > 1) {
            i--;
        }
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (this.isEmpty) {
                return 4;
            }
            return i2 < hashMap.get(arrayList.get(i)).size() ? 0 : 3;
        }
        if (i == 0) {
            if (this.isAdmin) {
                if (i2 == 1) {
                    return 2;
                }
            } else if (this.needPhonebook) {
                if (i2 == 1) {
                    return this.isEmpty ? 5 : 2;
                }
            } else if (i2 == 3) {
                return this.isEmpty ? 5 : 2;
            }
        } else {
            if (this.isEmpty) {
                return 4;
            }
            if (this.sortType != 2) {
                int i3 = i - 1;
                if (i3 < arrayList.size()) {
                    return i2 < hashMap.get(arrayList.get(i3)).size() ? 0 : 3;
                }
            } else if (i == 1) {
                return i2 < this.onlineContacts.size() ? 0 : 3;
            }
        }
        return 1;
    }

    @Override
    public java.lang.String getLetter(int r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.ContactsAdapter.getLetter(int):java.lang.String");
    }

    @Override
    public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
        iArr[0] = (int) (getItemCount() * f);
        iArr[1] = 0;
    }

    @Override
    public int getSectionCount() {
        this.isEmpty = false;
        int i = 1;
        if (this.sortType == 2) {
            this.isEmpty = this.onlineContacts.isEmpty();
        } else {
            int size = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
            if (size == 0) {
                this.isEmpty = true;
            } else {
                i = size;
            }
        }
        if (this.onlyUsers == 0) {
            i++;
        }
        if (this.isAdmin) {
            i++;
        }
        return this.hasStories ? i + 1 : i;
    }

    @Override
    public View getSectionHeaderView(int i, View view) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap2 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell letterSectionCell = (LetterSectionCell) view;
        boolean z = this.hasStories;
        if (z && i == 1) {
            letterSectionCell.setLetter("");
            return letterSectionCell;
        }
        if (z && i > 1) {
            i--;
        }
        if (this.sortType == 2 || this.disableSections || this.isEmpty || (this.onlyUsers == 0 || this.isAdmin ? !(i != 0 && (i = i - 1) < arrayList.size()) : i >= arrayList.size())) {
            letterSectionCell.setLetter("");
        } else {
            letterSectionCell.setLetter(arrayList.get(i));
        }
        return view;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
        boolean z = this.hasStories;
        if (z && i == 1) {
            return i2 != this.userStories.size();
        }
        if (z && i > 1) {
            i--;
        }
        HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            return !this.isEmpty && i2 < hashMap.get(arrayList.get(i)).size();
        }
        if (i == 0) {
            return this.isAdmin ? i2 != 1 : this.needPhonebook ? i2 != 1 : i2 != 3;
        }
        if (this.isEmpty) {
            return false;
        }
        if (this.sortType == 2) {
            return i != 1 || i2 < this.onlineContacts.size();
        }
        int i3 = i - 1;
        return i3 >= arrayList.size() || i2 < hashMap.get(arrayList.get(i3)).size();
        return true;
    }

    @Override
    public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
        ArrayList<TLRPC.TL_contact> arrayList;
        String str;
        String string;
        int i3;
        int i4;
        String formatName;
        String formatPluralString;
        boolean z = this.hasStories;
        int i5 = 6;
        if (z && i == 1) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType != 2) {
                    return;
                }
                GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                int i6 = this.sortType;
                graySectionCell.setText(LocaleController.getString(i6 == 0 ? R.string.Contacts : i6 == 1 ? R.string.SortedByName : R.string.SortedByLastSeen));
                return;
            }
            UserCell userCell = (UserCell) viewHolder.itemView;
            userCell.setAvatarPadding(6);
            userCell.storyParams.drawSegments = true;
            StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(DialogObject.getPeerDialogId(((TL_stories.PeerStories) this.userStories.get(i2)).peer)));
            if (storiesController.hasUnreadStories(user.id)) {
                int unreadStoriesCount = storiesController.getUnreadStoriesCount(user.id);
                formatName = ContactsController.formatName(user);
                formatPluralString = LocaleController.formatPluralString("NewStories", unreadStoriesCount, Integer.valueOf(unreadStoriesCount));
            } else {
                int size = ((TL_stories.PeerStories) this.userStories.get(i2)).stories.size();
                formatName = ContactsController.formatName(user);
                formatPluralString = LocaleController.formatPluralString("Stories", size, Integer.valueOf(size));
            }
            userCell.setData(user, formatName, formatPluralString.toLowerCase(), 0);
            return;
        }
        if (z && i > 1) {
            i--;
        }
        int itemViewType2 = viewHolder.getItemViewType();
        if (itemViewType2 == 0) {
            UserCell userCell2 = (UserCell) viewHolder.itemView;
            userCell2.storyParams.drawSegments = false;
            if (this.sortType != 2 && !this.disableSections) {
                i5 = 58;
            }
            userCell2.setAvatarPadding(i5);
            if (this.sortType == 2) {
                arrayList = this.onlineContacts;
            } else {
                arrayList = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict).get((this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).get(i - ((this.onlyUsers == 0 || this.isAdmin) ? 1 : 0)));
            }
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList.get(i2).user_id));
            userCell2.setData(user2, null, null, 0);
            userCell2.setChecked(this.selectedContacts.indexOfKey(user2.id) >= 0, false);
            LongSparseArray longSparseArray = this.ignoreUsers;
            if (longSparseArray != null) {
                userCell2.setAlpha(longSparseArray.indexOfKey(user2.id) >= 0 ? 0.5f : 1.0f);
                return;
            }
            return;
        }
        if (itemViewType2 != 1) {
            if (itemViewType2 != 2) {
                return;
            }
            GraySectionCell graySectionCell2 = (GraySectionCell) viewHolder.itemView;
            if (this.hasStories) {
                i4 = R.string.HiddenStories;
            } else {
                int i7 = this.sortType;
                i4 = i7 == 0 ? R.string.Contacts : i7 == 1 ? R.string.SortedByName : R.string.SortedByLastSeen;
            }
            graySectionCell2.setText(LocaleController.getString(i4));
            return;
        }
        TextCell textCell = (TextCell) viewHolder.itemView;
        if (i != 0) {
            ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
            String str2 = contact.first_name;
            if (str2 != null && contact.last_name != null) {
                str = contact.first_name + " " + contact.last_name;
            } else {
                if (str2 != null && contact.last_name == null) {
                    textCell.setText(str2, false);
                    return;
                }
                str = contact.last_name;
            }
            textCell.setText(str, false);
            return;
        }
        if (this.needPhonebook) {
            if (i2 != 0) {
                return;
            }
            string = LocaleController.getString(R.string.InviteFriends);
            i3 = R.drawable.msg_invite;
        } else if (this.isAdmin) {
            string = LocaleController.getString(this.isChannel ? R.string.ChannelInviteViaLink : R.string.InviteToGroupByLink);
            i3 = R.drawable.msg_link2;
        } else if (i2 == 0) {
            string = LocaleController.getString(R.string.NewGroup);
            i3 = R.drawable.msg_groups;
        } else if (i2 == 1) {
            string = LocaleController.getString(R.string.NewContact);
            i3 = R.drawable.msg_addcontact;
        } else {
            if (i2 != 2) {
                return;
            }
            string = LocaleController.getString(R.string.NewChannel);
            i3 = R.drawable.msg_channel;
        }
        textCell.setTextAndIcon((CharSequence) string, i3, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view;
        if (i == 0) {
            view = new UserCell(this.mContext, 58, 1, false);
        } else if (i == 1) {
            view = new TextCell(this.mContext);
        } else if (i == 2) {
            view = new GraySectionCell(this.mContext);
        } else if (i == 3) {
            View dividerCell = new DividerCell(this.mContext);
            dividerCell.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(LocaleController.isRTL ? 72.0f : 28.0f), AndroidUtilities.dp(8.0f));
            view = dividerCell;
        } else if (i == 4) {
            FrameLayout frameLayout = new FrameLayout(this.mContext) {
                @Override
                protected void onMeasure(int i2, int i3) {
                    int size = View.MeasureSpec.getSize(i3);
                    if (size == 0) {
                        size = viewGroup.getMeasuredHeight();
                    }
                    if (size == 0) {
                        size = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    }
                    int dp = AndroidUtilities.dp(50.0f);
                    int dp2 = ContactsAdapter.this.onlyUsers != 0 ? 0 : AndroidUtilities.dp(30.0f) + dp;
                    if (!ContactsAdapter.this.isAdmin && !ContactsAdapter.this.needPhonebook) {
                        dp2 += dp;
                    }
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec(dp2 < size ? size - dp2 : 0, 1073741824));
                }
            };
            frameLayout.addView(new ContactsEmptyView(this.mContext), LayoutHelper.createFrame(-2, -2, 17));
            view = frameLayout;
        } else if (i != 6) {
            View shadowSectionCell = new ShadowSectionCell(this.mContext);
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            combinedDrawable.setFullsize(true);
            shadowSectionCell.setBackgroundDrawable(combinedDrawable);
            view = shadowSectionCell;
        } else {
            DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
            if (dialogStoriesCell == null) {
                DialogStoriesCell dialogStoriesCell2 = new DialogStoriesCell(this.mContext, this.fragment, this.currentAccount, 1) {
                    @Override
                    public void onUserLongPressed(View view2, long j) {
                        ContactsAdapter.this.onStoryLongPressed(view2, j);
                    }
                };
                this.dialogStoriesCell = dialogStoriesCell2;
                dialogStoriesCell2.setProgressToCollapse(0.0f, false);
            } else {
                AndroidUtilities.removeFromParent(dialogStoriesCell);
            }
            FrameLayout frameLayout2 = new FrameLayout(this.mContext);
            frameLayout2.addView(this.dialogStoriesCell, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 8.0f, 0.0f, 0.0f));
            view = frameLayout2;
        }
        return new RecyclerListView.Holder(view);
    }

    public void onStoryLongPressed(View view, long j) {
    }

    public void setDisableSections(boolean z) {
        this.disableSections = z;
    }

    public void setSortType(int i, boolean z) {
        this.sortType = i;
        if (i != 2) {
            notifyDataSetChanged();
            return;
        }
        if (this.onlineContacts == null || z) {
            this.onlineContacts = new ArrayList(ContactsController.getInstance(this.currentAccount).contacts);
            long j = UserConfig.getInstance(this.currentAccount).clientUserId;
            int size = this.onlineContacts.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                }
                if (((TLRPC.TL_contact) this.onlineContacts.get(i2)).user_id == j) {
                    this.onlineContacts.remove(i2);
                    break;
                }
                i2++;
            }
        }
        sortOnlineContacts();
    }

    public void setStories(ArrayList arrayList, boolean z) {
    }

    public void sortOnlineContacts() {
        if (this.onlineContacts == null) {
            return;
        }
        try {
            final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            Collections.sort(this.onlineContacts, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$sortOnlineContacts$0;
                    lambda$sortOnlineContacts$0 = ContactsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, currentTime, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
                    return lambda$sortOnlineContacts$0;
                }
            });
            notifyDataSetChanged();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
