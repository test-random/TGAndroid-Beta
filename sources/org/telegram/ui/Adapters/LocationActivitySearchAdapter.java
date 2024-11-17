package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.RecyclerListView;

public abstract class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private FlickerLoadingView globalGradientView;
    private Context mContext;
    private boolean myLocationDenied;
    private Theme.ResourcesProvider resourcesProvider;

    public LocationActivitySearchAdapter(Context context, Theme.ResourcesProvider resourcesProvider, boolean z, boolean z2) {
        super(z, z2);
        this.myLocationDenied = false;
        this.mContext = context;
        this.resourcesProvider = resourcesProvider;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
    }

    public TLRPC.TL_messageMediaVenue getItem(int i) {
        ArrayList arrayList;
        if (!this.locations.isEmpty()) {
            i--;
        }
        if (i >= 0 && i < this.locations.size()) {
            arrayList = this.locations;
        } else {
            if (isSearching()) {
                return null;
            }
            i -= this.locations.size();
            if (!this.locations.isEmpty()) {
                i--;
            }
            if (i < 0 || i >= this.places.size()) {
                return null;
            }
            arrayList = this.places;
        }
        return (TLRPC.TL_messageMediaVenue) arrayList.get(i);
    }

    @Override
    public int getItemCount() {
        int size = !this.locations.isEmpty() ? this.locations.size() + 1 : 0;
        if (this.myLocationDenied) {
            return size;
        }
        if (isSearching()) {
            return size + 3;
        }
        if (!this.locations.isEmpty() && !this.places.isEmpty()) {
            size++;
        }
        return size + this.places.size();
    }

    @Override
    public int getItemViewType(int i) {
        return ((i == 0 || i == this.locations.size() + 1) && !this.locations.isEmpty()) ? 1 : 0;
    }

    public boolean isEmpty() {
        return this.places.size() == 0 && this.locations.size() == 0;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        GraySectionCell graySectionCell;
        int i2;
        TLRPC.TL_messageMediaVenue tL_messageMediaVenue;
        int i3;
        boolean z = true;
        if (viewHolder.getItemViewType() != 0) {
            if (viewHolder.getItemViewType() == 1) {
                if (i != 0 || this.locations.isEmpty()) {
                    graySectionCell = (GraySectionCell) viewHolder.itemView;
                    i2 = R.string.NearbyVenue;
                } else {
                    graySectionCell = (GraySectionCell) viewHolder.itemView;
                    i2 = R.string.LocationOnMap;
                }
                graySectionCell.setText(LocaleController.getString(i2));
                return;
            }
            return;
        }
        int i4 = !this.locations.isEmpty() ? i - 1 : i;
        if (i4 < 0 || i4 >= this.locations.size()) {
            if (!isSearching()) {
                int size = i4 - this.locations.size();
                if (!this.searchingLocations && !this.locations.isEmpty()) {
                    size--;
                }
                i3 = size;
                if (i3 >= 0 && i3 < this.places.size()) {
                    tL_messageMediaVenue = (TLRPC.TL_messageMediaVenue) this.places.get(i3);
                }
            }
            tL_messageMediaVenue = null;
            i3 = i;
        } else {
            tL_messageMediaVenue = (TLRPC.TL_messageMediaVenue) this.locations.get(i4);
            i3 = 2;
        }
        LocationCell locationCell = (LocationCell) viewHolder.itemView;
        if (i == getItemCount() - 1 || (!this.searchingLocations && !this.locations.isEmpty() && i == this.locations.size())) {
            z = false;
        }
        locationCell.setLocation(tL_messageMediaVenue, i3, z);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecyclerListView.Holder(i == 0 ? new LocationCell(this.mContext, false, this.resourcesProvider) : new GraySectionCell(this.mContext, this.resourcesProvider));
    }

    public void setMyLocationDenied(boolean z) {
        if (this.myLocationDenied == z) {
            return;
        }
        this.myLocationDenied = z;
    }
}
