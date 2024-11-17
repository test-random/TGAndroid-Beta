package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationDirectionCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.LocationActivity;

public class LocationActivityAdapter extends BaseLocationAdapter implements LocationController.LocationFetchCallback {
    private String addressName;
    public boolean animated;
    private boolean askingForMyLocation;
    private TLRPC.TL_channelLocation chatLocation;
    public TLRPC.TL_messageMediaVenue city;
    private int currentAccount;
    private ArrayList currentLiveLocations;
    private MessageObject currentMessageObject;
    private Location customLocation;
    private long dialogId;
    private FrameLayout emptyCell;
    private boolean fetchingLocation;
    private boolean fromStories;
    private Location gpsLocation;
    private int locationType;
    private Context mContext;
    private boolean myLocationDenied;
    private boolean needEmptyView;
    private int overScrollHeight;
    private String overrideAddressName;
    private Location previousFetchedLocation;
    private final Theme.ResourcesProvider resourcesProvider;
    private SendLocationCell sendLocationCell;
    private int shareLiveLocationPotistion;
    private SharedMediaLayout sharedMediaLayout;
    private boolean sharedMediaLayoutVisible;
    public TLRPC.TL_messageMediaVenue street;
    private Runnable updateRunnable;

    public LocationActivityAdapter(Context context, int i, long j, boolean z, Theme.ResourcesProvider resourcesProvider, boolean z2, boolean z3, boolean z4) {
        super(z2, z4);
        this.currentAccount = UserConfig.selectedAccount;
        this.shareLiveLocationPotistion = -1;
        this.currentLiveLocations = new ArrayList();
        this.animated = true;
        this.myLocationDenied = false;
        this.askingForMyLocation = false;
        this.fromStories = z3;
        this.mContext = context;
        this.locationType = i;
        this.dialogId = j;
        this.needEmptyView = z;
        this.resourcesProvider = resourcesProvider;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$onCreateViewHolder$0(View view) {
        onDirectionClick();
    }

    private void updateCell() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.LocationActivityAdapter.updateCell():void");
    }

    public void fetchLocationAddress() {
        Location location;
        int i;
        int i2 = this.locationType;
        if (i2 == 8) {
            location = this.customLocation;
            if (location == null && (location = this.gpsLocation) == null) {
                return;
            }
            this.fetchingLocation = true;
            updateCell();
            i = this.biz;
        } else {
            if (i2 == 4) {
                Location location2 = this.customLocation;
                if (location2 == null && (location2 = this.gpsLocation) == null) {
                    return;
                }
                Location location3 = this.previousFetchedLocation;
                if (location3 == null || location3.distanceTo(location2) > 100.0f) {
                    this.addressName = null;
                }
                this.fetchingLocation = true;
                updateCell();
                LocationController.fetchLocationAddress(location2, this);
                return;
            }
            location = this.customLocation;
            if (location == null) {
                return;
            }
            Location location4 = this.previousFetchedLocation;
            if (location4 == null || location4.distanceTo(location) > 20.0f) {
                this.addressName = null;
            }
            this.fetchingLocation = true;
            updateCell();
            i = this.stories ? 2 : 0;
        }
        LocationController.fetchLocationAddress(location, i, this);
    }

    public String getAddressName() {
        return this.addressName;
    }

    public Object getItem(int i) {
        ArrayList arrayList;
        int i2;
        TLRPC.GeoPoint geoPoint;
        Location location;
        int i3 = this.locationType;
        if (i3 == 4) {
            if (this.addressName == null) {
                return null;
            }
            TLRPC.TL_messageMediaVenue tL_messageMediaVenue = new TLRPC.TL_messageMediaVenue();
            tL_messageMediaVenue.address = this.addressName;
            TLRPC.TL_geoPoint tL_geoPoint = new TLRPC.TL_geoPoint();
            tL_messageMediaVenue.geo = tL_geoPoint;
            Location location2 = this.customLocation;
            if (location2 == null) {
                Location location3 = this.gpsLocation;
                if (location3 != null) {
                    tL_geoPoint.lat = location3.getLatitude();
                    geoPoint = tL_messageMediaVenue.geo;
                    location = this.gpsLocation;
                }
                return tL_messageMediaVenue;
            }
            tL_geoPoint.lat = location2.getLatitude();
            geoPoint = tL_messageMediaVenue.geo;
            location = this.customLocation;
            geoPoint._long = location.getLongitude();
            return tL_messageMediaVenue;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            if (i == 1) {
                return messageObject;
            }
            if (i > 4 && i < this.places.size() + 4) {
                arrayList = this.currentLiveLocations;
                i2 = i - 5;
            }
            return null;
        }
        int i4 = 2;
        if (i3 != 2) {
            if (i3 == 1) {
                if (i > 4 && i < this.places.size() + 5) {
                    arrayList = this.places;
                    i2 = i - 5;
                }
            } else if (i3 == 7) {
                int i5 = this.street == null ? 3 : 4;
                if (i > i5) {
                    i4 = i5 + 1;
                    if (i < this.locations.size() + i4) {
                        arrayList = this.locations;
                    }
                }
                int size = i5 + this.locations.size();
                if (i > size) {
                    int i6 = size + 1;
                    if (i < this.places.size() + i6) {
                        arrayList = this.places;
                        i2 = i - i6;
                    }
                }
            } else if (i > 3 && i < this.places.size() + 4) {
                arrayList = this.places;
                i2 = i - 4;
            }
            return null;
        }
        LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
        if (sharingLocationInfo != null && sharingLocationInfo.period != Integer.MAX_VALUE) {
            i4 = 3;
        }
        if (i < i4) {
            return null;
        }
        arrayList = this.currentLiveLocations;
        i2 = i - i4;
        return arrayList.get(i2);
    }

    @Override
    public int getItemCount() {
        int i = this.locationType;
        if (i != 6 && i != 5 && i != 4 && !this.biz) {
            if (this.currentMessageObject != null) {
                if (!this.currentLiveLocations.isEmpty()) {
                    r5 = this.currentLiveLocations.size() + 3;
                } else if (this.fromStories) {
                    r5 = 0;
                }
                r1 = 2 + r5;
            } else if (i == 2) {
                LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
                r1 = this.currentLiveLocations.size() + 2 + ((sharingLocationInfo == null || sharingLocationInfo.period == Integer.MAX_VALUE) ? 0 : 1);
            } else if (this.searching || !this.searched || this.places.isEmpty()) {
                int i2 = this.locationType;
                if (i2 == 0) {
                    r2 = 5;
                } else if (i2 == 7) {
                    r2 = (this.street == null ? 0 : 1) + 5;
                }
                boolean z = this.myLocationDenied;
                r1 = r2 + ((((z || (!this.searching && this.searched)) ? 0 : 2) + (this.needEmptyView ? 1 : 0)) - (z ? 2 : 0));
            } else {
                r1 = (this.locationType != 1 ? 5 : 6) + this.locations.size() + this.places.size() + (this.needEmptyView ? 1 : 0);
            }
        }
        return (this.sharedMediaLayout == null || !this.sharedMediaLayoutVisible) ? r1 : r1 + 1;
    }

    @Override
    public int getItemViewType(int r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.LocationActivityAdapter.getItemViewType(int):int");
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 6 ? (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null && this.gpsLocation == null) ? false : true : itemViewType == 1 || itemViewType == 3 || itemViewType == 8 || itemViewType == 12 || itemViewType == 7;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int i2;
        TLRPC.TL_messageMediaVenue tL_messageMediaVenue;
        boolean z;
        int i3;
        boolean z2;
        String str;
        TLRPC.TL_messageMediaVenue tL_messageMediaVenue2 = null;
        switch (viewHolder.getItemViewType()) {
            case 0:
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new RecyclerView.LayoutParams(-1, this.overScrollHeight);
                } else {
                    ((ViewGroup.MarginLayoutParams) layoutParams).height = this.overScrollHeight;
                }
                viewHolder.itemView.setLayoutParams(layoutParams);
                return;
            case 1:
                this.sendLocationCell = (SendLocationCell) viewHolder.itemView;
                updateCell();
                return;
            case 2:
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString(this.currentMessageObject != null ? R.string.LiveLocations : R.string.NearbyVenue));
                return;
            case 3:
                LocationCell locationCell = (LocationCell) viewHolder.itemView;
                int i4 = this.locationType;
                if (i4 == 0) {
                    i2 = i - 4;
                } else {
                    if (i4 == 7 || i4 == 8) {
                        int i5 = i - 4;
                        if (this.street == null) {
                            i2 = i5;
                        }
                    }
                    i2 = i - 5;
                }
                if (this.searched && (i4 != 7 || !this.searching)) {
                    r4 = true;
                }
                if (r4) {
                    if (i2 >= 0 && i2 < this.locations.size()) {
                        tL_messageMediaVenue2 = (TLRPC.TL_messageMediaVenue) this.locations.get(i2);
                        locationCell.setLocation(tL_messageMediaVenue2, r3, true);
                        return;
                    } else {
                        int size = i2 - this.locations.size();
                        if (size >= 0 && size < this.places.size()) {
                            tL_messageMediaVenue2 = (TLRPC.TL_messageMediaVenue) this.places.get(size);
                        }
                    }
                }
                r3 = i2;
                locationCell.setLocation(tL_messageMediaVenue2, r3, true);
                return;
            case 4:
                ((LocationLoadingCell) viewHolder.itemView).setLoading(this.searching);
                return;
            case 5:
            case 9:
            case 10:
            default:
                return;
            case 6:
                SendLocationCell sendLocationCell = (SendLocationCell) viewHolder.itemView;
                sendLocationCell.setHasLocation(this.gpsLocation != null);
                int i6 = i + 1;
                if (i6 < getItemCount() && getItemViewType(i6) == 7) {
                    r4 = true;
                }
                sendLocationCell.useDivider = r4;
                sendLocationCell.invalidate();
                return;
            case 7:
                ((SendLocationCell) viewHolder.itemView).setHasLocation(this.gpsLocation != null);
                return;
            case 8:
                SharingLiveLocationCell sharingLiveLocationCell = (SharingLiveLocationCell) viewHolder.itemView;
                if (this.locationType == 6) {
                    sharingLiveLocationCell.setDialog(this.currentMessageObject, this.gpsLocation, this.myLocationDenied);
                    return;
                }
                TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    sharingLiveLocationCell.setDialog(this.dialogId, tL_channelLocation);
                    return;
                }
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null && i == 1) {
                    sharingLiveLocationCell.setDialog(messageObject, this.gpsLocation, this.myLocationDenied);
                    return;
                }
                int i7 = i - (messageObject != null ? 5 : 2);
                LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId);
                if (sharingLocationInfo != null && sharingLocationInfo.period != Integer.MAX_VALUE) {
                    i7--;
                }
                if (i7 < 0 || i7 >= this.currentLiveLocations.size()) {
                    return;
                }
                sharingLiveLocationCell.setDialog((LocationActivity.LiveLocation) this.currentLiveLocations.get(i7), this.gpsLocation);
                return;
            case 11:
                viewHolder.itemView.setBackgroundColor(Theme.getColor(this.myLocationDenied ? Theme.key_dialogBackgroundGray : Theme.key_dialogBackground, this.resourcesProvider));
                return;
            case 12:
                LocationCell locationCell2 = (LocationCell) viewHolder.itemView;
                if (this.askingForMyLocation) {
                    if (i == 1 && this.street != null) {
                        r4 = true;
                    }
                    locationCell2.setLocation(null, 2, r4);
                    return;
                }
                if (i == 1) {
                    tL_messageMediaVenue = this.city;
                    z2 = this.street != null;
                    z = this.animated;
                    str = null;
                    i3 = 2;
                } else {
                    tL_messageMediaVenue = this.street;
                    z = this.animated;
                    i3 = 2;
                    z2 = false;
                    str = null;
                }
                locationCell2.setLocation(tL_messageMediaVenue, str, i3, z2, z);
                return;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        View view2;
        switch (i) {
            case 0:
                FrameLayout frameLayout = new FrameLayout(this.mContext);
                this.emptyCell = frameLayout;
                frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, this.overScrollHeight));
                view2 = frameLayout;
                break;
            case 1:
                view = new SendLocationCell(this.mContext, false, false, this.resourcesProvider);
                view2 = view;
                break;
            case 2:
                view2 = new HeaderCell(this.mContext, this.resourcesProvider);
                break;
            case 3:
                view = new LocationCell(this.mContext, false, this.resourcesProvider);
                view2 = view;
                break;
            case 4:
                view2 = new LocationLoadingCell(this.mContext, this.resourcesProvider);
                break;
            case 5:
                view2 = new LocationPoweredCell(this.mContext, this.resourcesProvider);
                break;
            case 6:
                SendLocationCell sendLocationCell = new SendLocationCell(this.mContext, true, false, this.resourcesProvider);
                sendLocationCell.setDialogId(this.dialogId);
                view = sendLocationCell;
                view2 = view;
                break;
            case 7:
                SendLocationCell sendLocationCell2 = new SendLocationCell(this.mContext, true, true, this.resourcesProvider);
                sendLocationCell2.setDialogId(this.dialogId);
                view2 = sendLocationCell2;
                break;
            case 8:
                Context context = this.mContext;
                int i2 = this.locationType;
                view2 = new SharingLiveLocationCell(context, true, (i2 == 4 || i2 == 5 || i2 == 3) ? 16 : 54, this.resourcesProvider);
                break;
            case 9:
                LocationDirectionCell locationDirectionCell = new LocationDirectionCell(this.mContext, this.resourcesProvider);
                locationDirectionCell.setOnButtonClick(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view3) {
                        LocationActivityAdapter.this.lambda$onCreateViewHolder$0(view3);
                    }
                });
                view2 = locationDirectionCell;
                break;
            case 10:
                View shadowSectionCell = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(getThemedColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                combinedDrawable.setFullsize(true);
                shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                view2 = shadowSectionCell;
                break;
            case 11:
            default:
                view2 = new View(this.mContext);
                break;
            case 12:
                LocationCell locationCell = new LocationCell(this.mContext, false, this.resourcesProvider);
                locationCell.setAllowTextAnimation(true);
                view = locationCell;
                view2 = view;
                break;
            case 13:
                view2 = this.sharedMediaLayout;
                break;
        }
        return new RecyclerListView.Holder(view2);
    }

    protected void onDirectionClick() {
    }

    @Override
    public void onLocationAddressAvailable(String str, String str2, TLRPC.TL_messageMediaVenue tL_messageMediaVenue, TLRPC.TL_messageMediaVenue tL_messageMediaVenue2, Location location) {
        this.fetchingLocation = false;
        this.previousFetchedLocation = location;
        int i = this.locationType;
        if (i == 8) {
            this.addressName = str2;
        } else {
            this.addressName = str;
        }
        if (i == 7 && this.askingForMyLocation) {
            this.city = null;
            this.street = null;
        }
        boolean z = this.street != null;
        if (i != 7) {
            updateCell();
            return;
        }
        this.city = tL_messageMediaVenue;
        this.street = tL_messageMediaVenue2;
        if (z != (tL_messageMediaVenue2 == null)) {
            notifyItemRangeChanged(1, 2);
            return;
        }
        notifyItemChanged(1);
        if (this.street == null) {
            notifyItemRemoved(2);
        } else {
            notifyItemInserted(2);
        }
    }

    public void setAddressNameOverride(String str) {
        this.overrideAddressName = str;
        updateCell();
    }

    public void setChatLocation(TLRPC.TL_channelLocation tL_channelLocation) {
        this.chatLocation = tL_channelLocation;
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        fetchLocationAddress();
        updateCell();
    }

    public void setGpsLocation(Location location) {
        int i;
        boolean z = this.gpsLocation == null;
        this.gpsLocation = location;
        if (this.customLocation == null) {
            fetchLocationAddress();
        }
        if (z && (i = this.shareLiveLocationPotistion) > 0) {
            notifyItemChanged(i);
        }
        if (this.currentMessageObject != null) {
            notifyItemChanged(1, new Object());
        } else if (this.locationType != 2) {
            updateCell();
            return;
        }
        updateLiveLocations();
    }

    public void setLiveLocations(ArrayList arrayList) {
        this.currentLiveLocations = new ArrayList(arrayList);
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        for (int i = 0; i < this.currentLiveLocations.size(); i++) {
            if (((LocationActivity.LiveLocation) this.currentLiveLocations.get(i)).id == clientUserId || ((LocationActivity.LiveLocation) this.currentLiveLocations.get(i)).object.out) {
                this.currentLiveLocations.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        notifyDataSetChanged();
    }

    public void setMyLocationDenied(boolean z, boolean z2) {
        if (this.myLocationDenied == z && this.askingForMyLocation == z2) {
            return;
        }
        this.myLocationDenied = z;
        this.askingForMyLocation = z2;
        if (z2) {
            this.city = null;
            this.street = null;
        }
        notifyDataSetChanged();
    }

    public void setOverScrollHeight(int i) {
        this.overScrollHeight = i;
        FrameLayout frameLayout = this.emptyCell;
        if (frameLayout != null) {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) frameLayout.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new RecyclerView.LayoutParams(-1, this.overScrollHeight);
            } else {
                ((ViewGroup.MarginLayoutParams) layoutParams).height = this.overScrollHeight;
            }
            this.emptyCell.setLayoutParams(layoutParams);
            this.emptyCell.forceLayout();
        }
    }

    public void setSharedMediaLayout(SharedMediaLayout sharedMediaLayout) {
        this.sharedMediaLayout = sharedMediaLayout;
    }

    public boolean setSharedMediaLayoutVisible(boolean z) {
        if (this.sharedMediaLayoutVisible == z) {
            return false;
        }
        this.sharedMediaLayoutVisible = z;
        notifyDataSetChanged();
        return true;
    }

    public void setUpdateRunnable(Runnable runnable) {
        this.updateRunnable = runnable;
    }

    public void updateLiveLocations() {
        if (this.currentLiveLocations.isEmpty()) {
            return;
        }
        notifyItemRangeChanged(2, this.currentLiveLocations.size(), new Object());
    }
}
