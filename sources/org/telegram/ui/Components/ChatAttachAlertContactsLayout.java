package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertContactsLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private PhonebookShareAlertDelegate delegate;
    private EmptyTextProgressView emptyView;
    private FrameLayout frameLayout;
    private boolean ignoreLayout;
    private FillLastLinearLayoutManager layoutManager;
    private ShareAdapter listAdapter;
    private RecyclerListView listView;
    private boolean multipleSelectionAllowed;
    private ShareSearchAdapter searchAdapter;
    private SearchField searchField;
    private HashMap selectedContacts;
    private ArrayList selectedContactsOrder;
    private boolean sendPressed;
    private View shadow;
    private AnimatorSet shadowAnimation;

    public static class ListItemID {
        private final long id;
        private final Type type;

        public enum Type {
            USER,
            CONTACT
        }

        public ListItemID(Type type, long j) {
            this.type = type;
            this.id = j;
        }

        public static ListItemID of(Object obj) {
            if (obj instanceof ContactsController.Contact) {
                return new ListItemID(Type.CONTACT, ((ContactsController.Contact) obj).contact_id);
            }
            if (obj instanceof TLRPC.User) {
                return new ListItemID(Type.USER, ((TLRPC.User) obj).id);
            }
            return null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ListItemID listItemID = (ListItemID) obj;
            return this.id == listItemID.id && this.type == listItemID.type;
        }

        public int hashCode() {
            return Objects.hash(this.type, Long.valueOf(this.id));
        }
    }

    public interface PhonebookShareAlertDelegate {

        public abstract class CC {
            public static void $default$didSelectContacts(PhonebookShareAlertDelegate phonebookShareAlertDelegate, ArrayList arrayList, String str, boolean z, int i, long j, boolean z2) {
            }
        }

        void didSelectContact(TLRPC.User user, boolean z, int i, long j, boolean z2);

        void didSelectContacts(ArrayList arrayList, String str, boolean z, int i, long j, boolean z2);
    }

    public class ShareAdapter extends RecyclerListView.SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public ShareAdapter(Context context) {
            this.mContext = context;
        }

        public static CharSequence lambda$onBindViewHolder$0(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        public static CharSequence lambda$onBindViewHolder$1(TLRPC.User user) {
            return PhoneFormat.getInstance().format("+" + user.phone);
        }

        @Override
        public int getCountForSection(int i) {
            if (i == 0 || i == getSectionCount() - 1) {
                return 1;
            }
            int i2 = i - 1;
            HashMap<String, ArrayList<Object>> hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (i2 < arrayList.size()) {
                return hashMap.get(arrayList.get(i2)).size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i, int i2) {
            if (i == 0) {
                return null;
            }
            int i3 = i - 1;
            HashMap<String, ArrayList<Object>> hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList<String> arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (i3 < arrayList.size()) {
                ArrayList<Object> arrayList2 = hashMap.get(arrayList.get(i3));
                if (i2 < arrayList2.size()) {
                    return arrayList2.get(i2);
                }
            }
            return null;
        }

        @Override
        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            return i == getSectionCount() - 1 ? 2 : 0;
        }

        @Override
        public String getLetter(int i) {
            return null;
        }

        @Override
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }

        @Override
        public int getSectionCount() {
            return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size() + 2;
        }

        @Override
        public View getSectionHeaderView(int i, View view) {
            return null;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            if (i == 0 || i == getSectionCount() - 1) {
                return false;
            }
            return i2 < ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(i + (-1))).size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }

        @Override
        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            final TLRPC.User user;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                Object item = getItem(i, i2);
                boolean z = true;
                if (i == getSectionCount() - 2 && i2 == getCountForSection(i) - 1) {
                    z = false;
                }
                if (item instanceof ContactsController.Contact) {
                    final ContactsController.Contact contact = (ContactsController.Contact) item;
                    user = contact.user;
                    if (user == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, ContactsController.formatName(contact.first_name, contact.last_name), new UserCell.CharSequenceCallback() {
                            @Override
                            public final CharSequence run() {
                                CharSequence lambda$onBindViewHolder$0;
                                lambda$onBindViewHolder$0 = ChatAttachAlertContactsLayout.ShareAdapter.lambda$onBindViewHolder$0(ContactsController.Contact.this);
                                return lambda$onBindViewHolder$0;
                            }
                        }, z);
                        user = null;
                    }
                } else {
                    user = (TLRPC.User) item;
                }
                if (user != null) {
                    userCell.setData(user, (CharSequence) null, new UserCell.CharSequenceCallback() {
                        @Override
                        public final CharSequence run() {
                            CharSequence lambda$onBindViewHolder$1;
                            lambda$onBindViewHolder$1 = ChatAttachAlertContactsLayout.ShareAdapter.lambda$onBindViewHolder$1(TLRPC.User.this);
                            return lambda$onBindViewHolder$1;
                        }
                    }, z);
                }
                userCell.setChecked(ChatAttachAlertContactsLayout.this.selectedContacts.containsKey(ListItemID.of(item)), false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View userCell;
            if (i == 0) {
                userCell = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
            } else if (i != 1) {
                userCell = new View(this.mContext);
            } else {
                userCell = new View(this.mContext);
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(userCell);
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private ArrayList searchResult = new ArrayList();
        private ArrayList searchResultNames = new ArrayList();
        private Runnable searchRunnable;

        public ShareSearchAdapter(Context context) {
            this.mContext = context;
        }

        public static CharSequence lambda$onBindViewHolder$4(ContactsController.Contact contact) {
            return contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format(contact.phones.get(0));
        }

        public static CharSequence lambda$onBindViewHolder$5(TLRPC.User user) {
            return PhoneFormat.getInstance().format("+" + user.phone);
        }

        public void lambda$processSearch$1(java.lang.String r19, java.util.ArrayList r20, java.util.ArrayList r21, int r22, int r23) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.ShareSearchAdapter.lambda$processSearch$1(java.lang.String, java.util.ArrayList, java.util.ArrayList, int, int):void");
        }

        public void lambda$processSearch$2(final String str, final int i) {
            final int i2 = UserConfig.selectedAccount;
            final ArrayList arrayList = new ArrayList(ContactsController.getInstance(i2).contactsBook.values());
            final ArrayList arrayList2 = new ArrayList(ContactsController.getInstance(i2).contacts);
            Utilities.searchQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.lambda$processSearch$1(str, arrayList, arrayList2, i2, i);
                }
            });
        }

        public void lambda$updateSearchResults$3(int i, ArrayList arrayList, ArrayList arrayList2) {
            if (i != this.lastSearchId) {
                return;
            }
            if (i != -1 && ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.searchAdapter) {
                ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.searchAdapter);
            }
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            notifyDataSetChanged();
        }

        public void lambda$search$0(final String str, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.lambda$processSearch$2(str, i);
                }
            });
        }

        private void updateSearchResults(String str, final ArrayList arrayList, final ArrayList arrayList2, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertContactsLayout.ShareSearchAdapter.this.lambda$updateSearchResults$3(i, arrayList, arrayList2);
                }
            });
        }

        public Object getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i2);
        }

        @Override
        public int getItemCount() {
            return this.searchResult.size() + 2;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            return i == getItemCount() - 1 ? 2 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertContactsLayout.this.updateEmptyView();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            final TLRPC.User user;
            if (viewHolder.getItemViewType() == 0) {
                UserCell userCell = (UserCell) viewHolder.itemView;
                boolean z = i != getItemCount() + (-2);
                Object item = getItem(i);
                if (item instanceof ContactsController.Contact) {
                    final ContactsController.Contact contact = (ContactsController.Contact) item;
                    user = contact.user;
                    if (user == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData((TLRPC.User) null, (CharSequence) this.searchResultNames.get(i - 1), new UserCell.CharSequenceCallback() {
                            @Override
                            public final CharSequence run() {
                                CharSequence lambda$onBindViewHolder$4;
                                lambda$onBindViewHolder$4 = ChatAttachAlertContactsLayout.ShareSearchAdapter.lambda$onBindViewHolder$4(ContactsController.Contact.this);
                                return lambda$onBindViewHolder$4;
                            }
                        }, z);
                        user = null;
                    }
                } else {
                    user = (TLRPC.User) item;
                }
                if (user != null) {
                    userCell.setData(user, (CharSequence) this.searchResultNames.get(i - 1), new UserCell.CharSequenceCallback() {
                        @Override
                        public final CharSequence run() {
                            CharSequence lambda$onBindViewHolder$5;
                            lambda$onBindViewHolder$5 = ChatAttachAlertContactsLayout.ShareSearchAdapter.lambda$onBindViewHolder$5(TLRPC.User.this);
                            return lambda$onBindViewHolder$5;
                        }
                    }, z);
                }
                userCell.setChecked(ChatAttachAlertContactsLayout.this.selectedContacts.containsKey(ListItemID.of(item)), false);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View userCell;
            if (i == 0) {
                userCell = new UserCell(this.mContext, ChatAttachAlertContactsLayout.this.resourcesProvider);
            } else if (i != 1) {
                userCell = new View(this.mContext);
            } else {
                userCell = new View(this.mContext);
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            }
            return new RecyclerListView.Holder(userCell);
        }

        public void search(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
            } else {
                final int i = this.lastSearchId + 1;
                this.lastSearchId = i;
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ChatAttachAlertContactsLayout.ShareSearchAdapter.this.lambda$search$0(str, i);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }
    }

    public static class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable;
        private BackupImageView avatarImageView;
        private CheckBox2 checkBox;
        private int currentAccount;
        private int currentId;
        private CharSequence currentName;
        private CharSequence currentStatus;
        private TLRPC.User currentUser;
        private CharSequence formattedPhoneNumber;
        private TLRPC.User formattedPhoneNumberUser;
        private TLRPC.FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private SimpleTextView statusTextView;

        public interface CharSequenceCallback {
            CharSequence run();
        }

        public UserCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.currentAccount = UserConfig.selectedAccount;
            this.resourcesProvider = resourcesProvider;
            this.avatarDrawable = new AvatarDrawable(resourcesProvider);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
            BackupImageView backupImageView2 = this.avatarImageView;
            boolean z = LocaleController.isRTL;
            addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 14.0f, 9.0f, z ? 14.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context) {
                @Override
                public boolean setText(CharSequence charSequence, boolean z2) {
                    return super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), z2);
                }
            };
            this.nameTextView = simpleTextView;
            NotificationCenter.listenEmojiLoading(simpleTextView);
            this.nameTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.nameTextView.setTypeface(AndroidUtilities.bold());
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView2 = this.nameTextView;
            boolean z2 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 28.0f : 72.0f, 12.0f, z2 ? 72.0f : 28.0f, 0.0f));
            SimpleTextView simpleTextView3 = new SimpleTextView(context);
            this.statusTextView = simpleTextView3;
            simpleTextView3.setTextSize(13);
            this.statusTextView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView4 = this.statusTextView;
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 28.0f : 72.0f, 36.0f, z3 ? 72.0f : 28.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox22 = this.checkBox;
            boolean z4 = LocaleController.isRTL;
            addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z4 ? 5 : 3) | 48, z4 ? 0.0f : 44.0f, 37.0f, z4 ? 44.0f : 0.0f, 0.0f));
        }

        public void lambda$setData$1(CharSequenceCallback charSequenceCallback) {
            final CharSequence run = charSequenceCallback.run();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertContactsLayout.UserCell.this.lambda$setData$0(run);
                }
            });
        }

        public void lambda$setStatus$2() {
            this.statusTextView.setText(this.formattedPhoneNumber);
        }

        public void lambda$setStatus$3() {
            if (this.currentUser != null) {
                this.formattedPhoneNumber = PhoneFormat.getInstance().format("+" + this.currentUser.phone);
                this.formattedPhoneNumberUser = this.currentUser;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ChatAttachAlertContactsLayout.UserCell.this.lambda$setStatus$2();
                    }
                });
            }
        }

        protected int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(70.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void setChecked(boolean z, boolean z2) {
            if (this.checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
        }

        public void setCurrentId(int i) {
            this.currentId = i;
        }

        public void setData(TLRPC.User user, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            if (user == null && charSequence == null && charSequence2 == null) {
                this.currentStatus = null;
                this.currentName = null;
                this.nameTextView.setText("");
                this.statusTextView.setText("");
                this.avatarImageView.setImageDrawable(null);
                return;
            }
            this.currentStatus = charSequence2;
            this.currentName = charSequence;
            this.currentUser = user;
            this.needDivider = z;
            setWillNotDraw(!z);
            update(0);
        }

        public void setData(TLRPC.User user, CharSequence charSequence, final CharSequenceCallback charSequenceCallback, boolean z) {
            setData(user, charSequence, (CharSequence) null, z);
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertContactsLayout.UserCell.this.lambda$setData$1(charSequenceCallback);
                }
            });
        }

        public void lambda$setData$0(CharSequence charSequence) {
            this.currentStatus = charSequence;
            if (charSequence == null) {
                TLRPC.User user = this.currentUser;
                if (user == null) {
                    return;
                }
                if (TextUtils.isEmpty(user.phone)) {
                    this.statusTextView.setText(LocaleController.getString(R.string.NumberUnknown));
                    return;
                } else if (this.formattedPhoneNumberUser == this.currentUser || (charSequence = this.formattedPhoneNumber) == null) {
                    this.statusTextView.setText("");
                    Utilities.globalQueue.postRunnable(new Runnable() {
                        @Override
                        public final void run() {
                            ChatAttachAlertContactsLayout.UserCell.this.lambda$setStatus$3();
                        }
                    });
                    return;
                }
            }
            this.statusTextView.setText(charSequence);
        }

        public void update(int r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.UserCell.update(int):void");
        }
    }

    public ChatAttachAlertContactsLayout(ChatAttachAlert chatAttachAlert, Context context, final Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.selectedContacts = new HashMap();
        this.selectedContactsOrder = new ArrayList();
        this.sendPressed = false;
        this.searchAdapter = new ShareSearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.frameLayout = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        SearchField searchField = new SearchField(context, false, resourcesProvider) {
            @Override
            protected void onFieldTouchUp(EditTextBoldCursor editTextBoldCursor) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(editTextBoldCursor, true);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                ChatAttachAlertContactsLayout.this.parentAlert.makeFocusable(getSearchEditText(), true);
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override
            public void onTextChange(String str) {
                if (str.length() != 0) {
                    if (ChatAttachAlertContactsLayout.this.emptyView != null) {
                        ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString(R.string.NoResult));
                    }
                } else if (ChatAttachAlertContactsLayout.this.listView.getAdapter() != ChatAttachAlertContactsLayout.this.listAdapter) {
                    int currentTop = ChatAttachAlertContactsLayout.this.getCurrentTop();
                    ChatAttachAlertContactsLayout.this.emptyView.setText(LocaleController.getString(R.string.NoContacts));
                    ChatAttachAlertContactsLayout.this.emptyView.showTextView();
                    ChatAttachAlertContactsLayout.this.listView.setAdapter(ChatAttachAlertContactsLayout.this.listAdapter);
                    ChatAttachAlertContactsLayout.this.listAdapter.notifyDataSetChanged();
                    if (currentTop > 0) {
                        ChatAttachAlertContactsLayout.this.layoutManager.scrollToPositionWithOffset(0, -currentTop);
                    }
                }
                if (ChatAttachAlertContactsLayout.this.searchAdapter != null) {
                    ChatAttachAlertContactsLayout.this.searchAdapter.search(str);
                }
            }

            @Override
            public void processTouchEvent(MotionEvent motionEvent) {
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setLocation(obtain.getRawX(), (obtain.getRawY() - ChatAttachAlertContactsLayout.this.parentAlert.getSheetContainer().getTranslationY()) - AndroidUtilities.dp(58.0f));
                ChatAttachAlertContactsLayout.this.listView.dispatchTouchEvent(obtain);
                obtain.recycle();
            }
        };
        this.searchField = searchField;
        searchField.setHint(LocaleController.getString(R.string.SearchFriends));
        this.frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1, 51));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setText(LocaleController.getString(R.string.NoContacts));
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) {
            @Override
            protected boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((ChatAttachAlertContactsLayout.this.parentAlert.scrollOffsetY[0] + AndroidUtilities.dp(30.0f)) + ((Build.VERSION.SDK_INT < 21 || ChatAttachAlertContactsLayout.this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)));
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.listView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(9.0f), this.listView) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public int calculateDyToMakeVisible(View view, int i2) {
                        return super.calculateDyToMakeVisible(view, i2) - (ChatAttachAlertContactsLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(8.0f));
                    }

                    @Override
                    public int calculateTimeForDeceleration(int i2) {
                        return super.calculateTimeForDeceleration(i2) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(i);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView2.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setBind(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ShareAdapter shareAdapter = new ShareAdapter(context);
        this.listAdapter = shareAdapter;
        recyclerListView3.setAdapter(shareAdapter);
        this.listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                ChatAttachAlertContactsLayout.this.lambda$new$1(resourcesProvider, view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertContactsLayout chatAttachAlertContactsLayout = ChatAttachAlertContactsLayout.this;
                chatAttachAlertContactsLayout.parentAlert.updateLayout(chatAttachAlertContactsLayout, true, i2);
                ChatAttachAlertContactsLayout.this.updateEmptyViewPosition();
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i) {
                boolean lambda$new$2;
                lambda$new$2 = ChatAttachAlertContactsLayout.this.lambda$new$2(view, i);
                return lambda$new$2;
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        addView(this.shadow, layoutParams);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter.getInstance(this.parentAlert.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        updateEmptyView();
    }

    public int getCurrentTop() {
        if (this.listView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
    }

    public void lambda$getThemeDescriptions$3() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }

    public void lambda$new$0(TLRPC.User user, boolean z, int i, long j, boolean z2) {
        this.parentAlert.dismiss(true);
        this.delegate.didSelectContact(user, z, i, j, z2);
    }

    public void lambda$new$1(Theme.ResourcesProvider resourcesProvider, View view, int i) {
        Object item;
        ContactsController.Contact contact;
        String str;
        String str2;
        String str3;
        String str4;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (adapter == shareSearchAdapter) {
            item = shareSearchAdapter.getItem(i);
        } else {
            int sectionForPosition = this.listAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = this.listAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition < 0 || sectionForPosition < 0) {
                return;
            } else {
                item = this.listAdapter.getItem(sectionForPosition, positionInSectionForPosition);
            }
        }
        if (item != null) {
            if (!this.selectedContacts.isEmpty()) {
                addOrRemoveSelectedContact((UserCell) view, item);
                return;
            }
            if (item instanceof ContactsController.Contact) {
                ContactsController.Contact contact2 = (ContactsController.Contact) item;
                TLRPC.User user = contact2.user;
                if (user != null) {
                    str3 = user.first_name;
                    str4 = user.last_name;
                } else {
                    str3 = contact2.first_name;
                    str4 = contact2.last_name;
                }
                contact = contact2;
                str2 = str4;
                str = str3;
            } else {
                TLRPC.User user2 = (TLRPC.User) item;
                ContactsController.Contact contact3 = new ContactsController.Contact();
                String str5 = user2.first_name;
                contact3.first_name = str5;
                String str6 = user2.last_name;
                contact3.last_name = str6;
                contact3.phones.add(user2.phone);
                contact3.user = user2;
                contact = contact3;
                str = str5;
                str2 = str6;
            }
            PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(this.parentAlert.baseFragment, contact, (TLRPC.User) null, (Uri) null, (File) null, str, str2, resourcesProvider);
            phonebookShareAlert.setDelegate(new PhonebookShareAlertDelegate() {
                @Override
                public final void didSelectContact(TLRPC.User user3, boolean z, int i2, long j, boolean z2) {
                    ChatAttachAlertContactsLayout.this.lambda$new$0(user3, z, i2, j, z2);
                }

                @Override
                public void didSelectContacts(ArrayList arrayList, String str7, boolean z, int i2, long j, boolean z2) {
                    ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate.CC.$default$didSelectContacts(this, arrayList, str7, z, i2, j, z2);
                }
            });
            phonebookShareAlert.show();
        }
    }

    public boolean lambda$new$2(View view, int i) {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        Object item = adapter == shareSearchAdapter ? shareSearchAdapter.getItem(i) : this.listAdapter.getItem(i);
        if (item == null) {
            return false;
        }
        addOrRemoveSelectedContact((UserCell) view, item);
        return true;
    }

    private org.telegram.tgnet.TLRPC.User prepareContact(java.lang.Object r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertContactsLayout.prepareContact(java.lang.Object):org.telegram.tgnet.TLRPC$User");
    }

    private void runShadowAnimation(final boolean z) {
        if ((!z || this.shadow.getTag() == null) && (z || this.shadow.getTag() != null)) {
            return;
        }
        this.shadow.setTag(z ? null : 1);
        if (z) {
            this.shadow.setVisibility(0);
        }
        AnimatorSet animatorSet = this.shadowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.shadowAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.shadow, (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.shadowAnimation.setDuration(150L);
        this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (ChatAttachAlertContactsLayout.this.shadowAnimation == null || !ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animator)) {
                    return;
                }
                ChatAttachAlertContactsLayout.this.shadowAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ChatAttachAlertContactsLayout.this.shadowAnimation == null || !ChatAttachAlertContactsLayout.this.shadowAnimation.equals(animator)) {
                    return;
                }
                if (!z) {
                    ChatAttachAlertContactsLayout.this.shadow.setVisibility(4);
                }
                ChatAttachAlertContactsLayout.this.shadowAnimation = null;
            }
        });
        this.shadowAnimation.start();
    }

    private void showErrorBox(String str) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.AppName)).setMessage(str).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
    }

    public void updateEmptyView() {
        this.emptyView.setVisibility(this.listView.getAdapter().getItemCount() == 2 ? 0 : 8);
        updateEmptyViewPosition();
    }

    public void updateEmptyViewPosition() {
        View childAt;
        if (this.emptyView.getVisibility() == 0 && (childAt = this.listView.getChildAt(0)) != null) {
            this.emptyView.setTranslationY(((r1.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2);
        }
    }

    public void addOrRemoveSelectedContact(UserCell userCell, Object obj) {
        boolean z = false;
        if (this.selectedContacts.isEmpty() && !this.multipleSelectionAllowed) {
            showErrorBox(LocaleController.formatString("AttachContactsSlowMode", R.string.AttachContactsSlowMode, new Object[0]));
            return;
        }
        ListItemID of = ListItemID.of(obj);
        if (this.selectedContacts.containsKey(of)) {
            this.selectedContacts.remove(of);
            this.selectedContactsOrder.remove(of);
        } else {
            this.selectedContacts.put(of, obj);
            this.selectedContactsOrder.add(of);
            z = true;
        }
        userCell.setChecked(z, true);
        this.parentAlert.updateCountButton(z ? 1 : 2);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ShareAdapter shareAdapter;
        if (i != NotificationCenter.contactsDidLoad || (shareAdapter = this.listAdapter) == null) {
            return;
        }
        shareAdapter.notifyDataSetChanged();
    }

    @Override
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            runShadowAnimation(true);
            top = i;
        } else {
            runShadowAnimation(false);
        }
        this.frameLayout.setTranslationY(top);
        return top + AndroidUtilities.dp(12.0f);
    }

    @Override
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(4.0f);
    }

    @Override
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    public ArrayList<TLRPC.User> getSelected() {
        ArrayList<TLRPC.User> arrayList = new ArrayList<>(this.selectedContacts.size());
        Iterator it = this.selectedContactsOrder.iterator();
        while (it.hasNext()) {
            arrayList.add(prepareContact(this.selectedContacts.get((ListItemID) it.next())));
        }
        return arrayList;
    }

    @Override
    public int getSelectedItemsCount() {
        return this.selectedContacts.size();
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ChatAttachAlertContactsLayout.this.lambda$getThemeDescriptions$3();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.frameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this.shadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        arrayList.add(new ThemeDescription(this.searchField.getSearchBackground(), ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogSearchBackground));
        int i = Theme.key_dialogSearchIcon;
        arrayList.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"searchIconImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.searchField, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SearchField.class}, new String[]{"clearSearchImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogSearchText));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_dialogSearchHint));
        arrayList.add(new ThemeDescription(this.searchField.getSearchEditText(), ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_featuredStickers_addedIcon));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        int i2 = Theme.key_dialogTextGray2;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, i2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    @Override
    public void onDestroy() {
        NotificationCenter.getInstance(this.parentAlert.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateEmptyViewPosition();
    }

    @Override
    public void onPreMeasure(int i, int i2) {
        int i3;
        if (this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            i3 = AndroidUtilities.dp(8.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (!AndroidUtilities.isTablet()) {
                android.graphics.Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    i3 = (int) (i2 / 3.5f);
                    this.parentAlert.setAllowNestedScroll(true);
                }
            }
            i3 = (i2 / 5) * 2;
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != i3) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, i3, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
    }

    @Override
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    @Override
    public void sendSelectedItems(boolean z, int i, long j, boolean z2) {
        if ((this.selectedContacts.size() == 0 && this.delegate == null) || this.sendPressed) {
            return;
        }
        this.sendPressed = true;
        ArrayList arrayList = new ArrayList(this.selectedContacts.size());
        Iterator it = this.selectedContactsOrder.iterator();
        while (it.hasNext()) {
            arrayList.add(prepareContact(this.selectedContacts.get((ListItemID) it.next())));
        }
        this.delegate.didSelectContacts(arrayList, this.parentAlert.commentTextView.getText().toString(), z, i, j, z2);
    }

    public void setDelegate(PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    public void setMultipleSelectionAllowed(boolean z) {
        this.multipleSelectionAllowed = z;
    }

    @Override
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }
}
