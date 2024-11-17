package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachRestrictedLayout extends ChatAttachAlert.AttachAlertLayout {
    private final RecyclerView.Adapter adapter;
    private int gridExtraSpace;
    public final int id;
    private final RecyclerListView listView;
    private final EmptyTextProgressView progressView;

    public ChatAttachRestrictedLayout(int i, ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.id = i;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString(R.string.NoPhotos));
        emptyTextProgressView.setOnTouchListener(null);
        emptyTextProgressView.setTextSize(16);
        addView(emptyTextProgressView, LayoutHelper.createFrame(-1, -2.0f));
        emptyTextProgressView.setLottie(R.raw.media_forbidden, 150, 150);
        emptyTextProgressView.setText(ChatObject.getRestrictedErrorText(this.parentAlert.getChat(), i == 1 ? 7 : i == 3 ? 18 : i == 4 ? 19 : 22));
        emptyTextProgressView.showTextView();
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider);
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(2);
        recyclerListView.setVerticalScrollBarEnabled(false);
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        recyclerListView.setClipToPadding(false);
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
            @Override
            public int getItemCount() {
                return 1;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i2) {
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i2) {
                return new RecyclerListView.Holder(new View(ChatAttachRestrictedLayout.this.getContext()) {
                    @Override
                    protected void onMeasure(int i3, int i4) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824), View.MeasureSpec.makeMeasureSpec(ChatAttachRestrictedLayout.this.gridExtraSpace, 1073741824));
                    }
                });
            }
        };
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                ChatAttachRestrictedLayout chatAttachRestrictedLayout = ChatAttachRestrictedLayout.this;
                chatAttachRestrictedLayout.parentAlert.updateLayout(chatAttachRestrictedLayout, true, i3);
            }
        });
        addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
    }

    @Override
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = top;
        }
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            top = i;
        }
        this.progressView.setTranslationY(((((getMeasuredHeight() - top) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2) + top);
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

    @Override
    public void onPreMeasure(int r4, int r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachRestrictedLayout.onPreMeasure(int, int):void");
    }

    @Override
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }
}
