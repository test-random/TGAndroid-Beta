package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StickersActivity;

public class StickersArchiveAlert extends AlertDialog.Builder {
    private int currentType;
    private BaseFragment parentFragment;
    private ArrayList stickerSets;

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return StickersArchiveAlert.this.stickerSets.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((ArchivedStickerSetCell) viewHolder.itemView).setStickersSet((TLRPC.StickerSetCovered) StickersArchiveAlert.this.stickerSets.get(i), i != StickersArchiveAlert.this.stickerSets.size() - 1);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ArchivedStickerSetCell archivedStickerSetCell = new ArchivedStickerSetCell(this.context, false);
            archivedStickerSetCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(82.0f)));
            return new RecyclerListView.Holder(archivedStickerSetCell);
        }
    }

    public StickersArchiveAlert(Context context, BaseFragment baseFragment, ArrayList arrayList) {
        super(context);
        int i;
        TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) arrayList.get(0);
        if (stickerSetCovered.set.masks) {
            this.currentType = 1;
            i = R.string.ArchivedMasksAlertTitle;
        } else {
            this.currentType = 0;
            i = R.string.ArchivedStickersAlertTitle;
        }
        setTitle(LocaleController.getString(i));
        this.stickerSets = new ArrayList(arrayList);
        this.parentFragment = baseFragment;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setView(linearLayout);
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(23.0f), 0);
        textView.setText(LocaleController.getString(stickerSetCovered.set.masks ? R.string.ArchivedMasksAlertInfo : R.string.ArchivedStickersAlertInfo));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        recyclerListView.setAdapter(new ListAdapter(context));
        recyclerListView.setVerticalScrollBarEnabled(false);
        recyclerListView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        recyclerListView.setGlowColor(-657673);
        linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
        setNegativeButton(LocaleController.getString(R.string.Close), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                dialogInterface.dismiss();
            }
        });
        if (this.parentFragment != null) {
            setPositiveButton(LocaleController.getString(R.string.Settings), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    StickersArchiveAlert.this.lambda$new$1(dialogInterface, i2);
                }
            });
        }
    }

    public void lambda$new$1(DialogInterface dialogInterface, int i) {
        this.parentFragment.presentFragment(new StickersActivity(this.currentType, null));
        dialogInterface.dismiss();
    }
}
