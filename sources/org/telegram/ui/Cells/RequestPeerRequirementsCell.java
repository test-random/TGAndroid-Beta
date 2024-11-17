package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class RequestPeerRequirementsCell extends LinearLayout {
    private TLRPC.RequestPeerType requestPeerType;
    private ArrayList requirements;

    class RequirementCell extends LinearLayout {
        private ImageView imageView;
        private TextView textView;

        public RequirementCell(Context context, Requirement requirement) {
            super(context);
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            setOrientation(0);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setImageResource(requirement.padding <= 0 ? R.drawable.list_check : R.drawable.list_circle);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader), PorterDuff.Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createLinear(20, 20, 0.0f, 51, (requirement.padding * 16) + 17, -1, 0, 0));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 14.0f);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.textView.setSingleLine(false);
            this.textView.setText(requirement.text);
            addView(this.textView, LayoutHelper.createLinear(-1, -2, 1, 6, 4, 24, 4));
        }
    }

    public RequestPeerRequirementsCell(Context context) {
        super(context);
        this.requirements = new ArrayList();
        setOrientation(1);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
    }

    private void checkAdminRights(TLRPC.TL_chatAdminRights tL_chatAdminRights, boolean z, int i, int i2) {
        checkAdminRights(tL_chatAdminRights, z, AndroidUtilities.replaceTags(LocaleController.getString(i)), AndroidUtilities.replaceTags(LocaleController.getString(i2)));
    }

    private void checkAdminRights(TLRPC.TL_chatAdminRights tL_chatAdminRights, boolean z, CharSequence charSequence, CharSequence charSequence2) {
        if (tL_chatAdminRights == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        if (tL_chatAdminRights.change_info) {
            arrayList.add(Requirement.make(1, LocaleController.getString(z ? R.string.EditAdminChangeChannelInfo : R.string.EditAdminChangeGroupInfo)));
        }
        if (tL_chatAdminRights.post_messages && z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminPostMessages)));
        }
        if (tL_chatAdminRights.edit_messages && z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminEditMessages)));
        }
        if (tL_chatAdminRights.delete_messages) {
            arrayList.add(Requirement.make(1, LocaleController.getString(z ? R.string.EditAdminDeleteMessages : R.string.EditAdminGroupDeleteMessages)));
        }
        if (tL_chatAdminRights.ban_users && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminBanUsers)));
        }
        if (tL_chatAdminRights.invite_users) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminAddUsers)));
        }
        if (tL_chatAdminRights.pin_messages && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminPinMessages)));
        }
        if (tL_chatAdminRights.add_admins) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminAddAdmins)));
        }
        if (tL_chatAdminRights.anonymous && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminSendAnonymously)));
        }
        if (tL_chatAdminRights.manage_call) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.StartVoipChatPermission)));
        }
        if (tL_chatAdminRights.manage_topics && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.ManageTopicsPermission)));
        }
        if (arrayList.size() == 1) {
            this.requirements.add(Requirement.make(TextUtils.concat(charSequence2, " ", ((Requirement) arrayList.get(0)).text)));
            return;
        }
        if (arrayList.isEmpty()) {
            return;
        }
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
        valueOf.append((CharSequence) " ");
        for (int i = 0; i < arrayList.size(); i++) {
            if (i > 0) {
                valueOf.append((CharSequence) ", ");
            }
            valueOf.append((CharSequence) ((Requirement) arrayList.get(i)).text.toString().toLowerCase());
        }
        valueOf.append((CharSequence) ".");
        this.requirements.add(Requirement.make(valueOf));
    }

    private void checkRequirement(Boolean bool, int i, int i2) {
        ArrayList arrayList;
        String string;
        if (bool != null) {
            if (bool.booleanValue()) {
                arrayList = this.requirements;
                string = LocaleController.getString(i);
            } else {
                arrayList = this.requirements;
                string = LocaleController.getString(i2);
            }
            arrayList.add(Requirement.make(AndroidUtilities.replaceTags(string)));
        }
    }

    private View emptyView(int i, int i2) {
        return emptyView(i, new ColorDrawable(i2));
    }

    private View emptyView(final int i, Drawable drawable) {
        View view = new View(getContext()) {
            @Override
            protected void onMeasure(int i2, int i3) {
                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(i), 1073741824));
            }
        };
        view.setBackground(drawable);
        return view;
    }

    public static CharSequence rightsToString(TLRPC.TL_chatAdminRights tL_chatAdminRights, boolean z) {
        ArrayList arrayList = new ArrayList();
        if (tL_chatAdminRights.change_info) {
            arrayList.add(Requirement.make(1, LocaleController.getString(z ? R.string.EditAdminChangeChannelInfo : R.string.EditAdminChangeGroupInfo)));
        }
        if (tL_chatAdminRights.post_messages && z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminPostMessages)));
        }
        if (tL_chatAdminRights.edit_messages && z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminEditMessages)));
        }
        if (tL_chatAdminRights.delete_messages) {
            arrayList.add(Requirement.make(1, LocaleController.getString(z ? R.string.EditAdminDeleteMessages : R.string.EditAdminGroupDeleteMessages)));
        }
        if (tL_chatAdminRights.ban_users && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminBanUsers)));
        }
        if (tL_chatAdminRights.invite_users) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminAddUsers)));
        }
        if (tL_chatAdminRights.pin_messages && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminPinMessages)));
        }
        if (tL_chatAdminRights.add_admins) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminAddAdmins)));
        }
        if (tL_chatAdminRights.anonymous && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.EditAdminSendAnonymously)));
        }
        if (tL_chatAdminRights.manage_call) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.StartVoipChatPermission)));
        }
        if (tL_chatAdminRights.manage_topics && !z) {
            arrayList.add(Requirement.make(1, LocaleController.getString(R.string.ManageTopicsPermission)));
        }
        if (arrayList.size() == 1) {
            return ((Requirement) arrayList.get(0)).text.toString().toLowerCase();
        }
        if (arrayList.isEmpty()) {
            return "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            if (i > 0) {
                spannableStringBuilder.append((CharSequence) ", ");
            }
            spannableStringBuilder.append((CharSequence) ((Requirement) arrayList.get(i)).text.toString().toLowerCase());
        }
        return spannableStringBuilder;
    }

    public void set(TLRPC.RequestPeerType requestPeerType) {
        ArrayList arrayList;
        int i;
        Boolean bool;
        if (this.requestPeerType != requestPeerType) {
            this.requestPeerType = requestPeerType;
            removeAllViews();
            this.requirements.clear();
            if (requestPeerType instanceof TLRPC.TL_requestPeerTypeUser) {
                checkRequirement(((TLRPC.TL_requestPeerTypeUser) requestPeerType).premium, R.string.PeerRequirementPremiumTrue, R.string.PeerRequirementPremiumFalse);
            } else {
                boolean z = requestPeerType instanceof TLRPC.TL_requestPeerTypeBroadcast;
                Boolean bool2 = requestPeerType.has_username;
                if (z) {
                    checkRequirement(bool2, R.string.PeerRequirementChannelPublicTrue, R.string.PeerRequirementChannelPublicFalse);
                    Boolean bool3 = requestPeerType.bot_participant;
                    if (bool3 != null && bool3.booleanValue()) {
                        this.requirements.add(Requirement.make(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PeerRequirementChannelBotParticipant))));
                    }
                    Boolean bool4 = requestPeerType.creator;
                    if (bool4 != null && bool4.booleanValue()) {
                        arrayList = this.requirements;
                        i = R.string.PeerRequirementChannelCreatorTrue;
                        arrayList.add(Requirement.make(AndroidUtilities.replaceTags(LocaleController.getString(i))));
                    }
                    bool = requestPeerType.creator;
                    if (bool != null || !bool.booleanValue()) {
                        checkAdminRights(requestPeerType.user_admin_rights, z, R.string.PeerRequirementUserRights, R.string.PeerRequirementUserRight);
                    }
                } else {
                    checkRequirement(bool2, R.string.PeerRequirementGroupPublicTrue, R.string.PeerRequirementGroupPublicFalse);
                    checkRequirement(requestPeerType.forum, R.string.PeerRequirementForumTrue, R.string.PeerRequirementForumFalse);
                    Boolean bool5 = requestPeerType.bot_participant;
                    if (bool5 != null && bool5.booleanValue()) {
                        this.requirements.add(Requirement.make(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PeerRequirementGroupBotParticipant))));
                    }
                    Boolean bool6 = requestPeerType.creator;
                    if (bool6 != null && bool6.booleanValue()) {
                        arrayList = this.requirements;
                        i = R.string.PeerRequirementGroupCreatorTrue;
                        arrayList.add(Requirement.make(AndroidUtilities.replaceTags(LocaleController.getString(i))));
                    }
                    bool = requestPeerType.creator;
                    if (bool != null) {
                    }
                    checkAdminRights(requestPeerType.user_admin_rights, z, R.string.PeerRequirementUserRights, R.string.PeerRequirementUserRight);
                }
            }
            if (this.requirements.isEmpty()) {
                return;
            }
            HeaderCell headerCell = new HeaderCell(getContext(), 20);
            headerCell.setText(LocaleController.getString(R.string.PeerRequirements));
            int i2 = Theme.key_windowBackgroundWhite;
            headerCell.setBackgroundColor(Theme.getColor(i2));
            addView(headerCell, LayoutHelper.createLinear(-1, -2));
            addView(emptyView(9, Theme.getColor(i2)), LayoutHelper.createLinear(-1, -2));
            Iterator it = this.requirements.iterator();
            while (it.hasNext()) {
                addView(new RequirementCell(getContext(), (Requirement) it.next()), LayoutHelper.createLinear(-1, -2));
            }
            addView(emptyView(12, Theme.getColor(Theme.key_windowBackgroundWhite)), LayoutHelper.createLinear(-1, -2));
            addView(emptyView(12, Theme.getThemedDrawableByKey(getContext(), R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow)), LayoutHelper.createLinear(-1, -2));
        }
    }
}
