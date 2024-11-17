package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class InviteUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private ContactsController.Contact currentContact;
    private CharSequence currentName;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public InviteUserCell(Context context, boolean z) {
        super(context);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z2 = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(50, 50.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 11.0f, 11.0f, z2 ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 28.0f : 72.0f, 14.0f, z3 ? 72.0f : 28.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(16);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z4 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z4 ? 5 : 3) | 48, z4 ? 28.0f : 72.0f, 39.0f, z4 ? 72.0f : 28.0f, 0.0f));
        if (z) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox22 = this.checkBox;
            boolean z5 = LocaleController.isRTL;
            addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z5 ? 5 : 3) | 48, z5 ? 0.0f : 40.0f, 40.0f, z5 ? 39.0f : 0.0f, 0.0f));
        }
    }

    public ContactsController.Contact getContact() {
        return this.currentContact;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f), 1073741824));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setUser(ContactsController.Contact contact, CharSequence charSequence) {
        this.currentContact = contact;
        this.currentName = charSequence;
        update(0);
    }

    public void update(int i) {
        ContactsController.Contact contact = this.currentContact;
        if (contact == null) {
            return;
        }
        this.avatarDrawable.setInfo(contact.contact_id, contact.first_name, contact.last_name, null, null, null, true);
        CharSequence charSequence = this.currentName;
        if (charSequence != null) {
            this.nameTextView.setText(charSequence, true);
        } else {
            SimpleTextView simpleTextView = this.nameTextView;
            ContactsController.Contact contact2 = this.currentContact;
            simpleTextView.setText(ContactsController.formatName(contact2.first_name, contact2.last_name));
        }
        SimpleTextView simpleTextView2 = this.statusTextView;
        int i2 = Theme.key_windowBackgroundWhiteGrayText;
        simpleTextView2.setTag(Integer.valueOf(i2));
        this.statusTextView.setTextColor(Theme.getColor(i2));
        ContactsController.Contact contact3 = this.currentContact;
        int i3 = contact3.imported;
        if (i3 > 0) {
            this.statusTextView.setText(LocaleController.formatPluralString("TelegramContacts", i3, new Object[0]));
        } else {
            this.statusTextView.setText(contact3.phones.get(0));
        }
        this.avatarImageView.setImageDrawable(this.avatarDrawable);
    }
}
