package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;

public class PhonebookShareAlert extends BottomSheet {
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private Paint backgroundPaint;
    private TextView buttonTextView;
    private TLRPC.User currentUser;
    private ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate delegate;
    private boolean inLayout;
    private boolean isImport;
    private LinearLayout linearLayout;
    private ListAdapter listAdapter;
    private ArrayList other;
    private BaseFragment parentFragment;
    private int phoneEndRow;
    private int phoneStartRow;
    private ArrayList phones;
    private int rowCount;
    private int scrollOffsetY;
    private NestedScrollView scrollView;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private int userRow;
    private int vcardEndRow;
    private int vcardStartRow;

    private class ListAdapter {
        private ListAdapter() {
        }

        public View createView(Context context, int i) {
            int itemViewType = getItemViewType(i);
            View textCheckBoxCell = itemViewType != 0 ? new TextCheckBoxCell(context) : new UserCell(context);
            onBindViewHolder(textCheckBoxCell, i, itemViewType);
            return textCheckBoxCell;
        }

        public int getItemCount() {
            return PhonebookShareAlert.this.rowCount;
        }

        public int getItemViewType(int i) {
            return i == PhonebookShareAlert.this.userRow ? 0 : 1;
        }

        public void onBindViewHolder(View view, int i, int i2) {
            AndroidUtilities.VcardItem vcardItem;
            int i3;
            if (i2 == 1) {
                TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) view;
                if (i < PhonebookShareAlert.this.phoneStartRow || i >= PhonebookShareAlert.this.phoneEndRow) {
                    vcardItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.other.get(i - PhonebookShareAlert.this.vcardStartRow);
                    int i4 = vcardItem.type;
                    if (i4 == 1) {
                        i3 = R.drawable.msg_mention;
                    } else if (i4 == 2) {
                        i3 = R.drawable.msg_location;
                    } else if (i4 == 3) {
                        i3 = R.drawable.msg_link;
                    } else {
                        if (i4 != 4) {
                            if (i4 == 5) {
                                i3 = R.drawable.msg_calendar2;
                            } else if (i4 == 6) {
                                i3 = "ORG".equalsIgnoreCase(vcardItem.getRawType(true)) ? R.drawable.msg_work : R.drawable.msg_jobtitle;
                            }
                        }
                        i3 = R.drawable.msg_info;
                    }
                } else {
                    vcardItem = (AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(i - PhonebookShareAlert.this.phoneStartRow);
                    i3 = R.drawable.msg_calls;
                }
                textCheckBoxCell.setVCardItem(vcardItem, i3, i != getItemCount() - 1);
            }
        }
    }

    public class TextCheckBoxCell extends FrameLayout {
        private Switch checkBox;
        private ImageView imageView;
        private boolean needDivider;
        private TextView textView;
        private TextView valueTextView;

        public TextCheckBoxCell(Context context) {
            super(context);
            float f;
            float f2;
            float f3;
            float f4;
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(PhonebookShareAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setSingleLine(false);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            TextView textView2 = this.textView;
            boolean z = LocaleController.isRTL;
            int i = (z ? 5 : 3) | 48;
            if (z) {
                f = PhonebookShareAlert.this.isImport ? 17 : 64;
            } else {
                f = 72.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 72.0f;
            } else {
                f2 = PhonebookShareAlert.this.isImport ? 17 : 64;
            }
            addView(textView2, LayoutHelper.createFrame(-1, -1.0f, i, f, 10.0f, f2, 0.0f));
            TextView textView3 = new TextView(context);
            this.valueTextView = textView3;
            textView3.setTextColor(PhonebookShareAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            TextView textView4 = this.valueTextView;
            boolean z2 = LocaleController.isRTL;
            int i2 = z2 ? 5 : 3;
            if (z2) {
                f3 = PhonebookShareAlert.this.isImport ? 17 : 64;
            } else {
                f3 = 72.0f;
            }
            if (LocaleController.isRTL) {
                f4 = 72.0f;
            } else {
                f4 = PhonebookShareAlert.this.isImport ? 17 : 64;
            }
            addView(textView4, LayoutHelper.createFrame(-2, -2.0f, i2, f3, 35.0f, f4, 0.0f));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(PhonebookShareAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
            ImageView imageView2 = this.imageView;
            boolean z3 = LocaleController.isRTL;
            addView(imageView2, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 5 : 3) | 48, z3 ? 0.0f : 20.0f, 20.0f, z3 ? 20.0f : 0.0f, 0.0f));
            if (PhonebookShareAlert.this.isImport) {
                return;
            }
            Switch r1 = new Switch(context);
            this.checkBox = r1;
            int i3 = Theme.key_switchTrack;
            int i4 = Theme.key_switchTrackChecked;
            int i5 = Theme.key_windowBackgroundWhite;
            r1.setColors(i3, i4, i5, i5);
            addView(this.checkBox, LayoutHelper.createFrame(37, 40.0f, (LocaleController.isRTL ? 3 : 5) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            Switch r0 = this.checkBox;
            if (r0 != null) {
                r0.invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(70.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int measuredHeight = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView = this.valueTextView;
            textView.layout(textView.getLeft(), measuredHeight, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + measuredHeight);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            measureChildWithMargins(this.textView, i, 0, i2, 0);
            measureChildWithMargins(this.valueTextView, i, 0, i2, 0);
            measureChildWithMargins(this.imageView, i, 0, i2, 0);
            Switch r7 = this.checkBox;
            if (r7 != null) {
                measureChildWithMargins(r7, i, 0, i2, 0);
            }
            setMeasuredDimension(View.MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(64.0f), this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0f)) + (this.needDivider ? 1 : 0));
        }

        public void setChecked(boolean z) {
            Switch r0 = this.checkBox;
            if (r0 != null) {
                r0.setChecked(z, true);
            }
        }

        public void setVCardItem(AndroidUtilities.VcardItem vcardItem, int i, boolean z) {
            this.textView.setText(vcardItem.getValue(true));
            this.valueTextView.setText(vcardItem.getType());
            Switch r0 = this.checkBox;
            if (r0 != null) {
                r0.setChecked(vcardItem.checked, false);
            }
            ImageView imageView = this.imageView;
            if (i != 0) {
                imageView.setImageResource(i);
            } else {
                imageView.setImageDrawable(null);
            }
            this.needDivider = z;
            setWillNotDraw(!z);
        }
    }

    public class UserCell extends LinearLayout {
        public UserCell(Context context) {
            super(context);
            String formatUserStatus;
            boolean z;
            setOrientation(1);
            if (PhonebookShareAlert.this.phones.size() == 1 && PhonebookShareAlert.this.other.size() == 0) {
                formatUserStatus = ((AndroidUtilities.VcardItem) PhonebookShareAlert.this.phones.get(0)).getValue(true);
                z = false;
            } else {
                formatUserStatus = (PhonebookShareAlert.this.currentUser.status == null || PhonebookShareAlert.this.currentUser.status.expires == 0) ? null : LocaleController.formatUserStatus(((BottomSheet) PhonebookShareAlert.this).currentAccount, PhonebookShareAlert.this.currentUser);
                z = true;
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(30.0f));
            avatarDrawable.setInfo(((BottomSheet) PhonebookShareAlert.this).currentAccount, PhonebookShareAlert.this.currentUser);
            BackupImageView backupImageView = new BackupImageView(context);
            backupImageView.setRoundRadius(AndroidUtilities.dp(40.0f));
            backupImageView.setForUserOrChat(PhonebookShareAlert.this.currentUser, avatarDrawable);
            addView(backupImageView, LayoutHelper.createLinear(80, 80, 49, 0, 32, 0, 0));
            TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 17.0f);
            textView.setTextColor(PhonebookShareAlert.this.getThemedColor(Theme.key_dialogTextBlack));
            textView.setSingleLine(true);
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            textView.setEllipsize(truncateAt);
            textView.setText(ContactsController.formatName(PhonebookShareAlert.this.currentUser.first_name, PhonebookShareAlert.this.currentUser.last_name));
            addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, formatUserStatus != null ? 0 : 27));
            if (formatUserStatus != null) {
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setTextColor(PhonebookShareAlert.this.getThemedColor(Theme.key_dialogTextGray3));
                textView2.setSingleLine(true);
                textView2.setEllipsize(truncateAt);
                textView2.setText(formatUserStatus);
                addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 10, 3, 10, z ? 27 : 11));
            }
        }
    }

    public PhonebookShareAlert(BaseFragment baseFragment, ContactsController.Contact contact, TLRPC.User user, Uri uri, File file, String str, String str2) {
        this(baseFragment, contact, user, uri, file, (String) null, str, str2);
    }

    public PhonebookShareAlert(BaseFragment baseFragment, ContactsController.Contact contact, TLRPC.User user, Uri uri, File file, String str, String str2, String str3) {
        this(baseFragment, contact, user, uri, file, str, str2, str3, null);
    }

    public PhonebookShareAlert(org.telegram.ui.ActionBar.BaseFragment r16, org.telegram.messenger.ContactsController.Contact r17, org.telegram.tgnet.TLRPC.User r18, android.net.Uri r19, java.io.File r20, java.lang.String r21, java.lang.String r22, java.lang.String r23, final org.telegram.ui.ActionBar.Theme.ResourcesProvider r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.<init>(org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.ContactsController$Contact, org.telegram.tgnet.TLRPC$User, android.net.Uri, java.io.File, java.lang.String, java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public PhonebookShareAlert(BaseFragment baseFragment, ContactsController.Contact contact, TLRPC.User user, Uri uri, File file, String str, String str2, Theme.ResourcesProvider resourcesProvider) {
        this(baseFragment, contact, user, uri, file, null, str, str2, resourcesProvider);
    }

    public void lambda$new$0(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        updateLayout(!this.inLayout);
    }

    public void lambda$new$1(AndroidUtilities.VcardItem vcardItem, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
                if (AndroidUtilities.shouldShowClipboardToast()) {
                    Toast.makeText(this.parentFragment.getParentActivity(), LocaleController.getString(R.string.TextCopied), 0).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$new$2(int r4, android.view.View r5, android.view.View r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.lambda$new$2(int, android.view.View, android.view.View):void");
    }

    public boolean lambda$new$3(int r3, org.telegram.ui.ActionBar.Theme.ResourcesProvider r4, android.content.Context r5, android.view.View r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.lambda$new$3(int, org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.content.Context, android.view.View):boolean");
    }

    public void lambda$new$4(boolean z, int i) {
        this.delegate.didSelectContact(this.currentUser, z, i, 0L, false);
        dismiss();
    }

    public void lambda$new$5(Theme.ResourcesProvider resourcesProvider, View view) {
        StringBuilder sb;
        if (this.isImport) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(LocaleController.getString(R.string.AddContactTitle));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setItems(new CharSequence[]{LocaleController.getString(R.string.CreateNewContact), LocaleController.getString(R.string.AddToExistingContact)}, new DialogInterface.OnClickListener() {
                private void fillRowWithType(String str, ContentValues contentValues) {
                    int i = 2;
                    if (!str.startsWith("X-")) {
                        if ("PREF".equalsIgnoreCase(str)) {
                            i = 12;
                        } else if ("HOME".equalsIgnoreCase(str)) {
                            i = 1;
                        } else if (!"MOBILE".equalsIgnoreCase(str) && !"CELL".equalsIgnoreCase(str)) {
                            if ("OTHER".equalsIgnoreCase(str)) {
                                i = 7;
                            } else if ("WORK".equalsIgnoreCase(str)) {
                                i = 3;
                            } else if ("RADIO".equalsIgnoreCase(str) || "VOICE".equalsIgnoreCase(str)) {
                                i = 14;
                            } else if ("PAGER".equalsIgnoreCase(str)) {
                                i = 6;
                            } else if ("CALLBACK".equalsIgnoreCase(str)) {
                                i = 8;
                            } else if ("CAR".equalsIgnoreCase(str)) {
                                i = 9;
                            } else if ("ASSISTANT".equalsIgnoreCase(str)) {
                                i = 19;
                            } else if ("MMS".equalsIgnoreCase(str)) {
                                i = 20;
                            } else if (str.startsWith("FAX")) {
                                i = 4;
                            } else {
                                contentValues.put("data2", (Integer) 0);
                            }
                        }
                        contentValues.put("data2", Integer.valueOf(i));
                        return;
                    }
                    contentValues.put("data2", (Integer) 0);
                    str = str.substring(2);
                    contentValues.put("data3", str);
                }

                private void fillUrlRowWithType(String str, ContentValues contentValues) {
                    int i;
                    int i2;
                    if (!str.startsWith("X-")) {
                        if ("HOMEPAGE".equalsIgnoreCase(str)) {
                            i = 1;
                        } else if ("BLOG".equalsIgnoreCase(str)) {
                            i2 = 2;
                            contentValues.put("data2", i2);
                            return;
                        } else if ("PROFILE".equalsIgnoreCase(str)) {
                            i = 3;
                        } else if ("HOME".equalsIgnoreCase(str)) {
                            i = 4;
                        } else if ("WORK".equalsIgnoreCase(str)) {
                            i = 5;
                        } else if ("FTP".equalsIgnoreCase(str)) {
                            i = 6;
                        } else if ("OTHER".equalsIgnoreCase(str)) {
                            i = 7;
                        } else {
                            contentValues.put("data2", (Integer) 0);
                        }
                        i2 = Integer.valueOf(i);
                        contentValues.put("data2", i2);
                        return;
                    }
                    contentValues.put("data2", (Integer) 0);
                    str = str.substring(2);
                    contentValues.put("data3", str);
                }

                @Override
                public void onClick(android.content.DialogInterface r20, int r21) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookShareAlert.AnonymousClass5.onClick(android.content.DialogInterface, int):void");
                }
            });
            builder.show();
            return;
        }
        if (this.currentUser.restriction_reason.isEmpty()) {
            Locale locale = Locale.US;
            TLRPC.User user = this.currentUser;
            sb = new StringBuilder(String.format(locale, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", ContactsController.formatName(user.first_name, user.last_name)));
        } else {
            sb = new StringBuilder(this.currentUser.restriction_reason.get(0).text);
        }
        int lastIndexOf = sb.lastIndexOf("END:VCARD");
        if (lastIndexOf >= 0) {
            this.currentUser.phone = null;
            for (int size = this.phones.size() - 1; size >= 0; size--) {
                AndroidUtilities.VcardItem vcardItem = (AndroidUtilities.VcardItem) this.phones.get(size);
                if (vcardItem.checked) {
                    TLRPC.User user2 = this.currentUser;
                    if (user2.phone == null) {
                        user2.phone = vcardItem.getValue(false);
                    }
                    for (int i = 0; i < vcardItem.vcardData.size(); i++) {
                        sb.insert(lastIndexOf, vcardItem.vcardData.get(i) + "\n");
                    }
                }
            }
            for (int size2 = this.other.size() - 1; size2 >= 0; size2--) {
                AndroidUtilities.VcardItem vcardItem2 = (AndroidUtilities.VcardItem) this.other.get(size2);
                if (vcardItem2.checked) {
                    for (int size3 = vcardItem2.vcardData.size() - 1; size3 >= 0; size3 += -1) {
                        sb.insert(lastIndexOf, vcardItem2.vcardData.get(size3) + "\n");
                    }
                }
            }
            this.currentUser.restriction_reason.clear();
            TLRPC.RestrictionReason restrictionReason = new TLRPC.RestrictionReason();
            restrictionReason.text = sb.toString();
            restrictionReason.reason = "";
            restrictionReason.platform = "";
            this.currentUser.restriction_reason.add(restrictionReason);
        }
        BaseFragment baseFragment = this.parentFragment;
        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                @Override
                public final void didSelectDate(boolean z, int i2) {
                    PhonebookShareAlert.this.lambda$new$4(z, i2);
                }
            }, resourcesProvider);
        } else {
            this.delegate.didSelectContact(this.currentUser, true, 0, 0L, false);
            dismiss();
        }
    }

    public void updateLayout(boolean z) {
        View childAt = this.scrollView.getChildAt(0);
        int top = childAt.getTop() - this.scrollView.getScrollY();
        if (top < 0) {
            top = 0;
        }
        boolean z2 = top <= 0;
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (z) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimation = animatorSet2;
                animatorSet2.setDuration(180L);
                AnimatorSet animatorSet3 = this.actionBarAnimation;
                ActionBar actionBar = this.actionBar;
                Property property = View.ALPHA;
                animatorSet3.playTogether(ObjectAnimator.ofFloat(actionBar, (Property<ActionBar, Float>) property, z2 ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.actionBarShadow, (Property<View, Float>) property, z2 ? 1.0f : 0.0f));
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        PhonebookShareAlert.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(z2 ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(z2 ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != top) {
            this.scrollOffsetY = top;
            this.containerView.invalidate();
        }
        childAt.getBottom();
        this.scrollView.getMeasuredHeight();
        boolean z3 = childAt.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight();
        if (!(z3 && this.shadow.getTag() == null) && (z3 || this.shadow.getTag() == null)) {
            return;
        }
        this.shadow.setTag(z3 ? 1 : null);
        AnimatorSet animatorSet4 = this.shadowAnimation;
        if (animatorSet4 != null) {
            animatorSet4.cancel();
            this.shadowAnimation = null;
        }
        if (!z) {
            this.shadow.setAlpha(z3 ? 1.0f : 0.0f);
            return;
        }
        AnimatorSet animatorSet5 = new AnimatorSet();
        this.shadowAnimation = animatorSet5;
        animatorSet5.setDuration(180L);
        this.shadowAnimation.playTogether(ObjectAnimator.ofFloat(this.shadow, (Property<View, Float>) View.ALPHA, z3 ? 1.0f : 0.0f));
        this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PhonebookShareAlert.this.shadowAnimation = null;
            }
        });
        this.shadowAnimation.start();
    }

    private void updateRows() {
        this.rowCount = 1;
        this.userRow = 0;
        if (this.phones.size() > 1 || !this.other.isEmpty()) {
            if (this.phones.isEmpty()) {
                this.phoneStartRow = -1;
                this.phoneEndRow = -1;
            } else {
                int i = this.rowCount;
                this.phoneStartRow = i;
                int size = i + this.phones.size();
                this.rowCount = size;
                this.phoneEndRow = size;
            }
            if (!this.other.isEmpty()) {
                int i2 = this.rowCount;
                this.vcardStartRow = i2;
                int size2 = i2 + this.other.size();
                this.rowCount = size2;
                this.vcardEndRow = size2;
                return;
            }
        } else {
            this.phoneStartRow = -1;
            this.phoneEndRow = -1;
        }
        this.vcardStartRow = -1;
        this.vcardEndRow = -1;
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bulletin.addDelegate((FrameLayout) this.containerView, new Bulletin.Delegate() {
            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
            }

            @Override
            public boolean clipWithGradient(int i) {
                return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
            }

            @Override
            public int getBottomOffset(int i) {
                return AndroidUtilities.dp(74.0f);
            }

            @Override
            public int getTopOffset(int i) {
                return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
            }

            @Override
            public void onBottomOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
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
    protected void onStop() {
        super.onStop();
        Bulletin.removeDelegate((FrameLayout) this.containerView);
    }

    public void setDelegate(ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }
}
