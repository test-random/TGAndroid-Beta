package org.telegram.tgnet.tl;

import java.util.ArrayList;
import org.telegram.messenger.DialogObject;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;

public class TL_stories {

    public static class Boost extends TLObject {
        public static final long NO_USER_ID = -1;
        public static int constructor = 706514033;
        public int date;
        public int expires;
        public int flags;
        public boolean gift;
        public boolean giveaway;
        public int giveaway_msg_id;
        public String id;
        public int multiplier;
        public long stars;
        public boolean unclaimed;
        public String used_gift_slug;
        public long user_id = -1;

        public static Boost TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            Boost tL_boost = i != 706514033 ? i != 1262359766 ? null : new TL_boost() : new TL_boost_layer186();
            if (tL_boost == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in Boost", Integer.valueOf(i)));
            }
            if (tL_boost != null) {
                tL_boost.readParams(abstractSerializedData, z);
            }
            return tL_boost;
        }
    }

    public static class MediaArea extends TLObject {
        public MediaAreaCoordinates coordinates;
        public boolean dark;
        public int flags;
        public boolean flipped;
        public TLRPC.Reaction reaction;

        public static MediaArea TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            MediaArea tL_mediaAreaWeather2;
            switch (i) {
                case -2057362882:
                    tL_mediaAreaWeather2 = new TL_mediaAreaWeather2();
                    break;
                case -1300094593:
                    tL_mediaAreaWeather2 = new TL_inputMediaAreaVenue();
                    break;
                case -1098720356:
                    tL_mediaAreaWeather2 = new TL_mediaAreaVenue();
                    break;
                case -891992787:
                    tL_mediaAreaWeather2 = new TL_mediaAreaGeoPoint();
                    break;
                case -544523486:
                    tL_mediaAreaWeather2 = new TL_mediaAreaGeoPoint_layer181();
                    break;
                case 340088945:
                    tL_mediaAreaWeather2 = new TL_mediaAreaSuggestedReaction();
                    break;
                case 577893055:
                    tL_mediaAreaWeather2 = new TL_inputMediaAreaChannelPost();
                    break;
                case 926421125:
                    tL_mediaAreaWeather2 = new TL_mediaAreaUrl();
                    break;
                case 1132918857:
                    tL_mediaAreaWeather2 = new TL_mediaAreaWeatherOld();
                    break;
                case 1235637404:
                    tL_mediaAreaWeather2 = new TL_mediaAreaWeather();
                    break;
                case 1996756655:
                    tL_mediaAreaWeather2 = new TL_mediaAreaChannelPost();
                    break;
                default:
                    tL_mediaAreaWeather2 = null;
                    break;
            }
            if (tL_mediaAreaWeather2 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in MediaArea", Integer.valueOf(i)));
            }
            if (tL_mediaAreaWeather2 != null) {
                tL_mediaAreaWeather2.readParams(abstractSerializedData, z);
            }
            return tL_mediaAreaWeather2;
        }
    }

    public static class MediaAreaCoordinates extends TLObject {
        public int flags;
        public double h;
        public double radius;
        public double rotation;
        public double w;
        public double x;
        public double y;

        public static MediaAreaCoordinates TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            MediaAreaCoordinates tL_mediaAreaCoordinates_layer181 = i != -808853502 ? i != 64088654 ? null : new TL_mediaAreaCoordinates_layer181() : new TL_mediaAreaCoordinates();
            if (tL_mediaAreaCoordinates_layer181 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in MediaAreaCoordinates", Integer.valueOf(i)));
            }
            if (tL_mediaAreaCoordinates_layer181 != null) {
                tL_mediaAreaCoordinates_layer181.readParams(abstractSerializedData, z);
            }
            return tL_mediaAreaCoordinates_layer181;
        }
    }

    public static abstract class PeerStories extends TLObject {
        public boolean checkedExpired;
        public int flags;
        public int max_read_id;
        public TLRPC.Peer peer;
        public ArrayList<StoryItem> stories = new ArrayList<>();

        public static PeerStories TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            PeerStories tL_peerStories = i != -2045664768 ? i != -1707742823 ? null : new TL_peerStories() : new TL_peerStories_layer162();
            if (tL_peerStories == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in PeerStories", Integer.valueOf(i)));
            }
            if (tL_peerStories != null) {
                tL_peerStories.readParams(abstractSerializedData, z);
            }
            return tL_peerStories;
        }
    }

    public static class PrepaidGiveaway extends TLObject {
        public int boosts;
        public int date;
        public long id;
        public int quantity;

        public static PrepaidGiveaway TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            PrepaidGiveaway tL_prepaidGiveaway = i != -1700956192 ? i != -1303143084 ? null : new TL_prepaidGiveaway() : new TL_prepaidStarsGiveaway();
            if (tL_prepaidGiveaway == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in PrepaidGiveaway", Integer.valueOf(i)));
            }
            if (tL_prepaidGiveaway != null) {
                tL_prepaidGiveaway.readParams(abstractSerializedData, z);
            }
            return tL_prepaidGiveaway;
        }
    }

    public static class StoryFwdHeader extends TLObject {
        public int flags;
        public TLRPC.Peer from;
        public String from_name;
        public boolean modified;
        public int story_id;

        public static StoryFwdHeader TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TL_storyFwdHeader tL_storyFwdHeader = i != -1205411504 ? null : new TL_storyFwdHeader();
            if (tL_storyFwdHeader == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StoryFwdHeader", Integer.valueOf(i)));
            }
            if (tL_storyFwdHeader != null) {
                tL_storyFwdHeader.readParams(abstractSerializedData, z);
            }
            return tL_storyFwdHeader;
        }
    }

    public static abstract class StoryItem extends TLObject {
        public String attachPath;
        public String caption;
        public boolean close_friends;
        public boolean contacts;
        public int date;
        public String detectedLng;
        public long dialogId;
        public boolean edited;
        public int expire_date;
        public int fileReference;
        public String firstFramePath;
        public int flags;
        public TLRPC.Peer from_id;
        public StoryFwdHeader fwd_from;
        public int id;
        public boolean isPublic;
        public boolean justUploaded;
        public long lastUpdateTime;
        public TLRPC.MessageMedia media;
        public int messageId;
        public int messageType;
        public boolean min;
        public boolean noforwards;
        public boolean out;
        public StoryPrivacyBottomSheet.StoryPrivacy parsedPrivacy;
        public boolean pinned;
        public boolean selected_contacts;
        public TLRPC.Reaction sent_reaction;
        public boolean translated;
        public String translatedLng;
        public TLRPC.TL_textWithEntities translatedText;
        public StoryViews views;
        public ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
        public ArrayList<MediaArea> media_areas = new ArrayList<>();
        public ArrayList<TLRPC.PrivacyRule> privacy = new ArrayList<>();

        public static StoryItem TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StoryItem tL_storyItem_layer174;
            switch (i) {
                case -1352440415:
                    tL_storyItem_layer174 = new TL_storyItem_layer174();
                    break;
                case -5388013:
                    tL_storyItem_layer174 = new TL_storyItemSkipped();
                    break;
                case 1153718222:
                    tL_storyItem_layer174 = new TL_storyItem_layer166();
                    break;
                case 1374088783:
                    tL_storyItem_layer174 = new TL_storyItemDeleted();
                    break;
                case 1445635639:
                    tL_storyItem_layer174 = new TL_storyItem_layer160();
                    break;
                case 2041735716:
                    tL_storyItem_layer174 = new TL_storyItem();
                    break;
                default:
                    tL_storyItem_layer174 = null;
                    break;
            }
            if (tL_storyItem_layer174 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StoryItem", Integer.valueOf(i)));
            }
            if (tL_storyItem_layer174 != null) {
                tL_storyItem_layer174.readParams(abstractSerializedData, z);
            }
            return tL_storyItem_layer174;
        }
    }

    public static class StoryReaction extends TLObject {
        public TLRPC.Message message;
        public TLRPC.Peer peer_id;
        public StoryItem story;

        public static StoryReaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StoryReaction tL_storyReaction = i != -1146411453 ? i != -808644845 ? i != 1620104917 ? null : new TL_storyReaction() : new TL_storyReactionPublicRepost() : new TL_storyReactionPublicForward();
            if (tL_storyReaction == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StoryReaction", Integer.valueOf(i)));
            }
            if (tL_storyReaction != null) {
                tL_storyReaction.readParams(abstractSerializedData, z);
            }
            return tL_storyReaction;
        }
    }

    public static class StoryView extends TLObject {
        public boolean blocked;
        public boolean blocked_my_stories_from;
        public int date;
        public int flags;
        public TLRPC.Message message;
        public TLRPC.Peer peer_id;
        public TLRPC.Reaction reaction;
        public StoryItem story;
        public long user_id;

        public static StoryView TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StoryView tL_storyViewPublicRepost = i != -1870436597 ? i != -1329730875 ? i != -1116418231 ? null : new TL_storyViewPublicRepost() : new TL_storyView() : new TL_storyViewPublicForward();
            if (tL_storyViewPublicRepost == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StoryView", Integer.valueOf(i)));
            }
            if (tL_storyViewPublicRepost != null) {
                tL_storyViewPublicRepost.readParams(abstractSerializedData, z);
            }
            return tL_storyViewPublicRepost;
        }
    }

    public static abstract class StoryViews extends TLObject {
        public int flags;
        public int forwards_count;
        public boolean has_viewers;
        public int reactions_count;
        public int views_count;
        public ArrayList<Long> recent_viewers = new ArrayList<>();
        public ArrayList<TLRPC.ReactionCount> reactions = new ArrayList<>();

        public static StoryViews TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StoryViews tL_storyViews_layer160 = i != -1923523370 ? i != -968094825 ? i != -748199729 ? null : new TL_storyViews_layer160() : new TL_storyViews_layer161() : new TL_storyViews();
            if (tL_storyViews_layer160 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StoryViews", Integer.valueOf(i)));
            }
            if (tL_storyViews_layer160 != null) {
                tL_storyViews_layer160.readParams(abstractSerializedData, z);
            }
            return tL_storyViews_layer160;
        }
    }

    public static class StoryViewsList extends TLObject {
        public int count;
        public int flags;
        public int forwards_count;
        public int reactions_count;
        public int views_count;
        public ArrayList<StoryView> views = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();
        public String next_offset = "";

        public static StoryViewsList TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StoryViewsList tL_storyViewsList = i != 1189722604 ? i != 1507299269 ? null : new TL_storyViewsList() : new TL_storyViewsList_layer167();
            if (tL_storyViewsList == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StoryViewsList", Integer.valueOf(i)));
            }
            if (tL_storyViewsList != null) {
                tL_storyViewsList.readParams(abstractSerializedData, z);
            }
            return tL_storyViewsList;
        }
    }

    public static class TL_boost extends Boost {
        public static final int constructor = 1262359766;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.gift = (readInt32 & 2) != 0;
            this.giveaway = (readInt32 & 4) != 0;
            this.unclaimed = (readInt32 & 8) != 0;
            this.id = abstractSerializedData.readString(z);
            if ((this.flags & 1) != 0) {
                this.user_id = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 4) != 0) {
                this.giveaway_msg_id = abstractSerializedData.readInt32(z);
            }
            this.date = abstractSerializedData.readInt32(z);
            this.expires = abstractSerializedData.readInt32(z);
            if ((this.flags & 16) != 0) {
                this.used_gift_slug = abstractSerializedData.readString(z);
            }
            if ((this.flags & 32) != 0) {
                this.multiplier = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 64) != 0) {
                this.stars = abstractSerializedData.readInt64(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1262359766);
            int i = this.gift ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            int i2 = this.giveaway ? i | 4 : i & (-5);
            this.flags = i2;
            int i3 = this.unclaimed ? i2 | 8 : i2 & (-9);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            abstractSerializedData.writeString(this.id);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(this.giveaway_msg_id);
            }
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt32(this.expires);
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeString(this.used_gift_slug);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.multiplier);
            }
            if ((this.flags & 64) != 0) {
                abstractSerializedData.writeInt64(this.stars);
            }
        }
    }

    public static class TL_boost_layer186 extends TL_boost {
        public static final int constructor = 706514033;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.gift = (readInt32 & 2) != 0;
            this.giveaway = (readInt32 & 4) != 0;
            this.unclaimed = (readInt32 & 8) != 0;
            this.id = abstractSerializedData.readString(z);
            if ((this.flags & 1) != 0) {
                this.user_id = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 4) != 0) {
                this.giveaway_msg_id = abstractSerializedData.readInt32(z);
            }
            this.date = abstractSerializedData.readInt32(z);
            this.expires = abstractSerializedData.readInt32(z);
            if ((this.flags & 16) != 0) {
                this.used_gift_slug = abstractSerializedData.readString(z);
            }
            if ((this.flags & 32) != 0) {
                this.multiplier = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(706514033);
            int i = this.gift ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            int i2 = this.giveaway ? i | 4 : i & (-5);
            this.flags = i2;
            int i3 = this.unclaimed ? i2 | 8 : i2 & (-9);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            abstractSerializedData.writeString(this.id);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(this.giveaway_msg_id);
            }
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt32(this.expires);
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeString(this.used_gift_slug);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.multiplier);
            }
        }
    }

    public static class TL_exportedStoryLink extends TLObject {
        public static final int constructor = 1070138683;
        public String link;

        public static TL_exportedStoryLink TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1070138683 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_exportedStoryLink", Integer.valueOf(i)));
                }
                return null;
            }
            TL_exportedStoryLink tL_exportedStoryLink = new TL_exportedStoryLink();
            tL_exportedStoryLink.readParams(abstractSerializedData, z);
            return tL_exportedStoryLink;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.link = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1070138683);
            abstractSerializedData.writeString(this.link);
        }
    }

    public static class TL_foundStories extends TLObject {
        public static final int constructor = -488736969;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<TL_foundStory> stories = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_foundStories TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-488736969 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_foundStories", Integer.valueOf(i)));
                }
                return null;
            }
            TL_foundStories tL_foundStories = new TL_foundStories();
            tL_foundStories.readParams(abstractSerializedData, z);
            return tL_foundStories;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TL_foundStory TLdeserialize = TL_foundStory.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.stories.add(TLdeserialize);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-488736969);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.stories.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.stories.get(i).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_foundStory extends TLObject {
        public static final int constructor = -394605632;
        public TLRPC.Peer peer;
        public StoryItem storyItem;

        public static TL_foundStory TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-394605632 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_foundStory", Integer.valueOf(i)));
                }
                return null;
            }
            TL_foundStory tL_foundStory = new TL_foundStory();
            tL_foundStory.readParams(abstractSerializedData, z);
            return tL_foundStory;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.storyItem = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-394605632);
            this.peer.serializeToStream(abstractSerializedData);
            this.storyItem.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_geoPointAddress extends TLObject {
        public static final int constructor = -565420653;
        public String city;
        public String country_iso2;
        public int flags;
        public String state;
        public String street;

        public static TL_geoPointAddress TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-565420653 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_geoPointAddress", Integer.valueOf(i)));
                }
                return null;
            }
            TL_geoPointAddress tL_geoPointAddress = new TL_geoPointAddress();
            tL_geoPointAddress.readParams(abstractSerializedData, z);
            return tL_geoPointAddress;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.country_iso2 = abstractSerializedData.readString(z);
            if ((this.flags & 1) != 0) {
                this.state = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.city = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.street = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-565420653);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeString(this.country_iso2);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.state);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.city);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeString(this.street);
            }
        }

        public String toString() {
            String str;
            String str2;
            StringBuilder sb = new StringBuilder();
            sb.append("geo{country=");
            sb.append(this.country_iso2);
            sb.append(", ");
            String str3 = "";
            if (this.state != null) {
                str = "state=" + this.state + ", ";
            } else {
                str = "";
            }
            sb.append(str);
            if (this.city != null) {
                str2 = "city=" + this.city + ", ";
            } else {
                str2 = "";
            }
            sb.append(str2);
            if (this.street != null) {
                str3 = "street=" + this.street;
            }
            sb.append(str3);
            sb.append("}");
            return sb.toString();
        }
    }

    public static class TL_getStoryReactionsList extends TLObject {
        public static final int constructor = -1179482081;
        public int flags;
        public boolean forwards_first;
        public int id;
        public int limit;
        public String offset;
        public TLRPC.InputPeer peer;
        public TLRPC.Reaction reaction;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_storyReactionsList.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1179482081);
            int i = this.forwards_first ? this.flags | 4 : this.flags & (-5);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.id);
            if ((this.flags & 1) != 0) {
                this.reaction.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.offset);
            }
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_inputMediaAreaChannelPost extends MediaArea {
        public static final int constructor = 577893055;
        public TLRPC.InputChannel channel;
        public int msg_id;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.channel = TLRPC.InputChannel.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.msg_id = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(577893055);
            this.coordinates.serializeToStream(abstractSerializedData);
            this.channel.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.msg_id);
        }
    }

    public static class TL_inputMediaAreaVenue extends MediaArea {
        public static final int constructor = -1300094593;
        public long query_id;
        public String result_id;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.query_id = abstractSerializedData.readInt64(z);
            this.result_id = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1300094593);
            this.coordinates.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt64(this.query_id);
            abstractSerializedData.writeString(this.result_id);
        }
    }

    public static class TL_mediaAreaChannelPost extends MediaArea {
        public static final int constructor = 1996756655;
        public long channel_id;
        public int msg_id;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.channel_id = abstractSerializedData.readInt64(z);
            this.msg_id = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1996756655);
            this.coordinates.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt64(this.channel_id);
            abstractSerializedData.writeInt32(this.msg_id);
        }
    }

    public static class TL_mediaAreaCoordinates extends MediaAreaCoordinates {
        public static final int constructor = -808853502;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.x = abstractSerializedData.readDouble(z);
            this.y = abstractSerializedData.readDouble(z);
            this.w = abstractSerializedData.readDouble(z);
            this.h = abstractSerializedData.readDouble(z);
            this.rotation = abstractSerializedData.readDouble(z);
            if ((this.flags & 1) != 0) {
                this.radius = abstractSerializedData.readDouble(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-808853502);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeDouble(this.x);
            abstractSerializedData.writeDouble(this.y);
            abstractSerializedData.writeDouble(this.w);
            abstractSerializedData.writeDouble(this.h);
            abstractSerializedData.writeDouble(this.rotation);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeDouble(this.radius);
            }
        }
    }

    public static class TL_mediaAreaCoordinates_layer181 extends MediaAreaCoordinates {
        public static final int constructor = 64088654;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.x = abstractSerializedData.readDouble(z);
            this.y = abstractSerializedData.readDouble(z);
            this.w = abstractSerializedData.readDouble(z);
            this.h = abstractSerializedData.readDouble(z);
            this.rotation = abstractSerializedData.readDouble(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(64088654);
            abstractSerializedData.writeDouble(this.x);
            abstractSerializedData.writeDouble(this.y);
            abstractSerializedData.writeDouble(this.w);
            abstractSerializedData.writeDouble(this.h);
            abstractSerializedData.writeDouble(this.rotation);
        }
    }

    public static class TL_mediaAreaGeoPoint extends MediaArea {
        public static final int constructor = -891992787;
        public TL_geoPointAddress address;
        public TLRPC.GeoPoint geo;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.geo = TLRPC.GeoPoint.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.address = TL_geoPointAddress.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-891992787);
            abstractSerializedData.writeInt32(this.flags);
            this.coordinates.serializeToStream(abstractSerializedData);
            this.geo.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                this.address.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_mediaAreaGeoPoint_layer181 extends TL_mediaAreaGeoPoint {
        public static final int constructor = -544523486;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.geo = TLRPC.GeoPoint.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-544523486);
            this.coordinates.serializeToStream(abstractSerializedData);
            this.geo.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_mediaAreaSuggestedReaction extends MediaArea {
        public static final int constructor = 340088945;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.dark = (readInt32 & 1) != 0;
            this.flipped = (readInt32 & 2) != 0;
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.reaction = TLRPC.Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(340088945);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.flipped ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            this.coordinates.serializeToStream(abstractSerializedData);
            this.reaction.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_mediaAreaUrl extends MediaArea {
        public static final int constructor = 926421125;
        public String url;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.url = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(926421125);
            this.coordinates.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.url);
        }
    }

    public static class TL_mediaAreaVenue extends MediaArea {
        public static final int constructor = -1098720356;
        public String address;
        public TLRPC.GeoPoint geo;
        public String provider;
        public String title;
        public String venue_id;
        public String venue_type;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.geo = TLRPC.GeoPoint.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.title = abstractSerializedData.readString(z);
            this.address = abstractSerializedData.readString(z);
            this.provider = abstractSerializedData.readString(z);
            this.venue_id = abstractSerializedData.readString(z);
            this.venue_type = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1098720356);
            this.coordinates.serializeToStream(abstractSerializedData);
            this.geo.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.title);
            abstractSerializedData.writeString(this.address);
            abstractSerializedData.writeString(this.provider);
            abstractSerializedData.writeString(this.venue_id);
            abstractSerializedData.writeString(this.venue_type);
        }
    }

    public static class TL_mediaAreaWeather extends MediaArea {
        public static final int constructor = 1235637404;
        public int color;
        public String emoji;
        public double temperature_c;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.emoji = abstractSerializedData.readString(z);
            this.temperature_c = abstractSerializedData.readDouble(z);
            this.color = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1235637404);
            this.coordinates.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.emoji);
            abstractSerializedData.writeDouble(this.temperature_c);
            abstractSerializedData.writeInt32(this.color);
        }
    }

    public static class TL_mediaAreaWeather2 extends MediaArea {
        public static final int constructor = -2057362882;
        public String emoji;
        public int temperature_c;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.dark = (readInt32 & 1) != 0;
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.emoji = abstractSerializedData.readString(z);
            this.temperature_c = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-2057362882);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.coordinates.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.emoji);
            abstractSerializedData.writeInt32(this.temperature_c);
        }
    }

    public static class TL_mediaAreaWeatherOld extends MediaArea {
        public static final int constructor = 1132918857;
        public String emoji;
        public double temperature_c;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.dark = (readInt32 & 1) != 0;
            this.coordinates = MediaAreaCoordinates.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.emoji = abstractSerializedData.readString(z);
            this.temperature_c = abstractSerializedData.readDouble(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1132918857);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.coordinates.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.emoji);
            abstractSerializedData.writeDouble(this.temperature_c);
        }
    }

    public static class TL_myBoost extends TLObject {
        public static int constructor = -1001897636;
        public int cooldown_until_date;
        public int date;
        public int expires;
        public int flags;
        public TLRPC.Peer peer;
        public int slot;

        public static TL_myBoost TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (constructor != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_myBoost", Integer.valueOf(i)));
                }
                return null;
            }
            TL_myBoost tL_myBoost = new TL_myBoost();
            tL_myBoost.readParams(abstractSerializedData, z);
            return tL_myBoost;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.slot = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.date = abstractSerializedData.readInt32(z);
            this.expires = abstractSerializedData.readInt32(z);
            if ((this.flags & 2) != 0) {
                this.cooldown_until_date = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.slot);
            if ((this.flags & 1) != 0) {
                this.peer.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt32(this.expires);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(this.cooldown_until_date);
            }
        }
    }

    public static class TL_peerStories extends PeerStories {
        public static final int constructor = -1707742823;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.max_read_id = abstractSerializedData.readInt32(z);
            }
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryItem TLdeserialize = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.stories.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1707742823);
            abstractSerializedData.writeInt32(this.flags);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.max_read_id);
            }
            abstractSerializedData.writeInt32(481674261);
            int size = this.stories.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.stories.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_peerStories_layer162 extends TL_peerStories {
        public static final int constructor = -2045664768;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            long readInt64 = abstractSerializedData.readInt64(z);
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            this.peer = tL_peerUser;
            tL_peerUser.user_id = readInt64;
            if ((this.flags & 1) != 0) {
                this.max_read_id = abstractSerializedData.readInt32(z);
            }
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryItem TLdeserialize = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.stories.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-2045664768);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt64(this.peer.user_id);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.max_read_id);
            }
            abstractSerializedData.writeInt32(481674261);
            int size = this.stories.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.stories.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_premium_applyBoost extends TLObject {
        public static int constructor = 1803396934;
        public int flags;
        public TLRPC.InputPeer peer;
        public ArrayList<Integer> slots = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_premium_myBoosts.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.slots.size();
                abstractSerializedData.writeInt32(size);
                for (int i = 0; i < size; i++) {
                    abstractSerializedData.writeInt32(this.slots.get(i).intValue());
                }
            }
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_premium_boostsList extends TLObject {
        public static int constructor = -2030542532;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<Boost> boosts = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_premium_boostsList TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (constructor != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_premium_boostsList", Integer.valueOf(i)));
                }
                return null;
            }
            TL_premium_boostsList tL_premium_boostsList = new TL_premium_boostsList();
            tL_premium_boostsList.readParams(abstractSerializedData, z);
            return tL_premium_boostsList;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                Boost TLdeserialize = Boost.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.boosts.add(TLdeserialize);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.User TLdeserialize2 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.users.add(TLdeserialize2);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.boosts.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.boosts.get(i).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.users.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_premium_boostsStatus extends TLObject {
        public static int constructor = 1230586490;
        public String boost_url;
        public int boosts;
        public int current_level_boosts;
        public int flags;
        public int gift_boosts;
        public int level;
        public boolean my_boost;
        public int next_level_boosts;
        public TL_stats.TL_statsPercentValue premium_audience;
        public ArrayList<PrepaidGiveaway> prepaid_giveaways = new ArrayList<>();
        public ArrayList<Integer> my_boost_slots = new ArrayList<>();

        public static TL_premium_boostsStatus TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (constructor != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_premium_boostsStatus", Integer.valueOf(i)));
                }
                return null;
            }
            TL_premium_boostsStatus tL_premium_boostsStatus = new TL_premium_boostsStatus();
            tL_premium_boostsStatus.readParams(abstractSerializedData, z);
            return tL_premium_boostsStatus;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.my_boost = (readInt32 & 4) != 0;
            this.level = abstractSerializedData.readInt32(z);
            this.current_level_boosts = abstractSerializedData.readInt32(z);
            this.boosts = abstractSerializedData.readInt32(z);
            if ((this.flags & 16) != 0) {
                this.gift_boosts = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 1) != 0) {
                this.next_level_boosts = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 2) != 0) {
                this.premium_audience = TL_stats.TL_statsPercentValue.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.boost_url = abstractSerializedData.readString(z);
            if ((this.flags & 8) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    PrepaidGiveaway TLdeserialize = PrepaidGiveaway.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.prepaid_giveaways.add(TLdeserialize);
                }
            }
            if ((this.flags & 4) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                    }
                } else {
                    int readInt325 = abstractSerializedData.readInt32(z);
                    for (int i2 = 0; i2 < readInt325; i2++) {
                        this.my_boost_slots.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
                    }
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            int i = this.my_boost ? this.flags | 4 : this.flags & (-5);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt32(this.level);
            abstractSerializedData.writeInt32(this.current_level_boosts);
            abstractSerializedData.writeInt32(this.boosts);
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeInt32(this.gift_boosts);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.next_level_boosts);
            }
            if ((this.flags & 2) != 0) {
                this.premium_audience.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeString(this.boost_url);
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.prepaid_giveaways.size();
                abstractSerializedData.writeInt32(size);
                for (int i2 = 0; i2 < size; i2++) {
                    this.prepaid_giveaways.get(i2).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.my_boost_slots.size();
                abstractSerializedData.writeInt32(size2);
                for (int i3 = 0; i3 < size2; i3++) {
                    abstractSerializedData.writeInt32(this.my_boost_slots.get(i3).intValue());
                }
            }
        }
    }

    public static class TL_premium_getBoostsList extends TLObject {
        public static int constructor = 1626764896;
        public int flags;
        public boolean gifts;
        public int limit;
        public String offset;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_premium_boostsList.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            int i = this.gifts ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_premium_getBoostsStatus extends TLObject {
        public static int constructor = 70197089;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_premium_boostsStatus.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_premium_getMyBoosts extends TLObject {
        public static int constructor = 199719754;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_premium_myBoosts.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
        }
    }

    public static class TL_premium_myBoosts extends TLObject {
        public static int constructor = -1696454430;
        public ArrayList<TL_myBoost> my_boosts = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_premium_myBoosts TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (constructor != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_premium_myBoosts", Integer.valueOf(i)));
                }
                return null;
            }
            TL_premium_myBoosts tL_premium_myBoosts = new TL_premium_myBoosts();
            tL_premium_myBoosts.readParams(abstractSerializedData, z);
            return tL_premium_myBoosts;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TL_myBoost TLdeserialize = TL_myBoost.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.my_boosts.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            abstractSerializedData.writeInt32(481674261);
            int size = this.my_boosts.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.my_boosts.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_prepaidGiveaway extends PrepaidGiveaway {
        public static final int constructor = -1303143084;
        public int months;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.id = abstractSerializedData.readInt64(z);
            this.months = abstractSerializedData.readInt32(z);
            this.quantity = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1303143084);
            abstractSerializedData.writeInt64(this.id);
            abstractSerializedData.writeInt32(this.months);
            abstractSerializedData.writeInt32(this.quantity);
            abstractSerializedData.writeInt32(this.date);
        }
    }

    public static class TL_prepaidStarsGiveaway extends PrepaidGiveaway {
        public static final int constructor = -1700956192;
        public long stars;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.id = abstractSerializedData.readInt64(z);
            this.stars = abstractSerializedData.readInt64(z);
            this.quantity = abstractSerializedData.readInt32(z);
            this.boosts = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1700956192);
            abstractSerializedData.writeInt64(this.id);
            abstractSerializedData.writeInt64(this.stars);
            abstractSerializedData.writeInt32(this.quantity);
            abstractSerializedData.writeInt32(this.boosts);
            abstractSerializedData.writeInt32(this.date);
        }
    }

    public static class TL_publicForwardStory extends TL_stats.PublicForward {
        public static final int constructor = -302797360;
        public TLRPC.Peer peer;
        public StoryItem story;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.story = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-302797360);
            this.peer.serializeToStream(abstractSerializedData);
            this.story.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_stats_getStoryStats extends TLObject {
        public static final int constructor = 927985472;
        public boolean dark;
        public int flags;
        public int id;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_stats_storyStats.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(927985472);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.id);
        }
    }

    public static class TL_stats_storyStats extends TLObject {
        public static final int constructor = 1355613820;
        public TL_stats.StatsGraph reactions_by_emotion_graph;
        public TL_stats.StatsGraph views_graph;

        public static TL_stats_storyStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1355613820 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_storyStats", Integer.valueOf(i)));
                }
                return null;
            }
            TL_stats_storyStats tL_stats_storyStats = new TL_stats_storyStats();
            tL_stats_storyStats.readParams(abstractSerializedData, z);
            return tL_stats_storyStats;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.views_graph = TL_stats.StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.reactions_by_emotion_graph = TL_stats.StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1355613820);
            this.views_graph.serializeToStream(abstractSerializedData);
            this.reactions_by_emotion_graph.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_storiesStealthMode extends TLObject {
        public static final int constructor = 1898850301;
        public int active_until_date;
        public int cooldown_until_date;
        public int flags;

        public static TL_storiesStealthMode TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1898850301 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_storiesStealthMode", Integer.valueOf(i)));
                }
                return null;
            }
            TL_storiesStealthMode tL_storiesStealthMode = new TL_storiesStealthMode();
            tL_storiesStealthMode.readParams(abstractSerializedData, z);
            return tL_storiesStealthMode;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.active_until_date = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 2) != 0) {
                this.cooldown_until_date = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1898850301);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.active_until_date);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(this.cooldown_until_date);
            }
        }
    }

    public static class TL_stories_activateStealthMode extends TLObject {
        public static final int constructor = 1471926630;
        public int flags;
        public boolean future;
        public boolean past;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1471926630);
            int i = this.past ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.future ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
        }
    }

    public static class TL_stories_allStories extends stories_AllStories {
        public static final int constructor = 1862033025;
        public int count;
        public int flags;
        public boolean has_more;
        public String state;
        public TL_storiesStealthMode stealth_mode;
        public ArrayList<PeerStories> peer_stories = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_more = (readInt32 & 1) != 0;
            this.count = abstractSerializedData.readInt32(z);
            this.state = abstractSerializedData.readString(z);
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                }
                return;
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt323; i++) {
                PeerStories TLdeserialize = PeerStories.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.peer_stories.add(TLdeserialize);
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            if (readInt324 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                }
                return;
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt325; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            if (readInt326 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                }
                return;
            }
            int readInt327 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt327; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
            this.stealth_mode = TL_storiesStealthMode.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1862033025);
            int i = this.has_more ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeString(this.state);
            abstractSerializedData.writeInt32(481674261);
            int size = this.peer_stories.size();
            abstractSerializedData.writeInt32(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.peer_stories.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            abstractSerializedData.writeInt32(size2);
            for (int i3 = 0; i3 < size2; i3++) {
                this.chats.get(i3).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            abstractSerializedData.writeInt32(size3);
            for (int i4 = 0; i4 < size3; i4++) {
                this.users.get(i4).serializeToStream(abstractSerializedData);
            }
            this.stealth_mode.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_stories_allStoriesNotModified extends stories_AllStories {
        public static final int constructor = 291044926;
        public int flags;
        public String state;
        public TL_storiesStealthMode stealth_mode;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.state = abstractSerializedData.readString(z);
            this.stealth_mode = TL_storiesStealthMode.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(291044926);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeString(this.state);
            this.stealth_mode.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_stories_canSendStory extends TLObject {
        public static final int constructor = -941629475;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-941629475);
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_stories_deleteStories extends TLObject {
        public static final int constructor = -1369842849;
        public ArrayList<Integer> id = new ArrayList<>();
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                vector.objects.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1369842849);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
        }
    }

    public static class TL_stories_editStory extends TLObject {
        public static final int constructor = -1249658298;
        public String caption;
        public int flags;
        public int id;
        public TLRPC.InputMedia media;
        public TLRPC.InputPeer peer;
        public ArrayList<MediaArea> media_areas = new ArrayList<>();
        public ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
        public ArrayList<TLRPC.InputPrivacyRule> privacy_rules = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1249658298);
            abstractSerializedData.writeInt32(this.flags);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.id);
            if ((this.flags & 1) != 0) {
                this.media.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.media_areas.size();
                abstractSerializedData.writeInt32(size);
                for (int i = 0; i < size; i++) {
                    this.media_areas.get(i).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.caption);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.entities.size();
                abstractSerializedData.writeInt32(size2);
                for (int i2 = 0; i2 < size2; i2++) {
                    this.entities.get(i2).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size3 = this.privacy_rules.size();
                abstractSerializedData.writeInt32(size3);
                for (int i3 = 0; i3 < size3; i3++) {
                    this.privacy_rules.get(i3).serializeToStream(abstractSerializedData);
                }
            }
        }
    }

    public static class TL_stories_exportStoryLink extends TLObject {
        public static final int constructor = 2072899360;
        public int id;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_exportedStoryLink.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2072899360);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.id);
        }
    }

    public static class TL_stories_getAllReadPeerStories extends TLObject {
        public static final int constructor = -1688541191;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1688541191);
        }
    }

    public static class TL_stories_getAllStories extends TLObject {
        public static final int constructor = -290400731;
        public int flags;
        public boolean include_hidden;
        public boolean next;
        public String state;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return stories_AllStories.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-290400731);
            int i = this.next ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            int i2 = this.include_hidden ? i | 4 : i & (-5);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.state);
            }
        }
    }

    public static class TL_stories_getChatsToSend extends TLObject {
        public static final int constructor = -1519744160;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.messages_Chats.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1519744160);
        }
    }

    public static class TL_stories_getPeerMaxIDs extends TLObject {
        public static final int constructor = 1398375363;
        public ArrayList<TLRPC.InputPeer> id = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                vector.objects.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1398375363);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.id.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_stories_getPeerStories extends TLObject {
        public static final int constructor = 743103056;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_stories_peerStories.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(743103056);
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_stories_getPinnedStories extends TLObject {
        public static final int constructor = 1478600156;
        public int limit;
        public int offset_id;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_stories_stories.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1478600156);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.offset_id);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_stories_getStoriesArchive extends TLObject {
        public static final int constructor = -1271586794;
        public int limit;
        public int offset_id;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_stories_stories.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1271586794);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.offset_id);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_stories_getStoriesByID extends TLObject {
        public static final int constructor = 1467271796;
        public ArrayList<Integer> id = new ArrayList<>();
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_stories_stories.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1467271796);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
        }
    }

    public static class TL_stories_getStoriesViews extends TLObject {
        public static final int constructor = 685862088;
        public ArrayList<Integer> id = new ArrayList<>();
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_stories_storyViews.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(685862088);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
        }
    }

    public static class TL_stories_getStoryViewsList extends TLObject {
        public static final int constructor = 2127707223;
        public int flags;
        public boolean forwards_first;
        public int id;
        public boolean just_contacts;
        public int limit;
        public String offset;
        public TLRPC.InputPeer peer;
        public String q;
        public boolean reactions_first;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return StoryViewsList.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2127707223);
            int i = this.just_contacts ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.reactions_first ? i | 4 : i & (-5);
            this.flags = i2;
            int i3 = this.forwards_first ? i2 | 8 : i2 & (-9);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.q);
            }
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_stories_incrementStoryViews extends TLObject {
        public static final int constructor = -1308456197;
        public ArrayList<Integer> id = new ArrayList<>();
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1308456197);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
        }
    }

    public static class TL_stories_peerStories extends TLObject {
        public static final int constructor = -890861720;
        public PeerStories stories;
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_stories_peerStories TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-890861720 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stories_peerStories", Integer.valueOf(i)));
                }
                return null;
            }
            TL_stories_peerStories tL_stories_peerStories = new TL_stories_peerStories();
            tL_stories_peerStories.readParams(abstractSerializedData, z);
            return tL_stories_peerStories;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.stories = PeerStories.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.Chat TLdeserialize = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.chats.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.User TLdeserialize2 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.users.add(TLdeserialize2);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-890861720);
            this.stories.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.chats.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.chats.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.users.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_stories_readStories extends TLObject {
        public static final int constructor = -1521034552;
        public int max_id;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                vector.objects.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1521034552);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.max_id);
        }
    }

    public static class TL_stories_report extends TLObject {
        public static final int constructor = 433646405;
        public ArrayList<Integer> id = new ArrayList<>();
        public String message;
        public byte[] option;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.ReportResult.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(433646405);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
            abstractSerializedData.writeByteArray(this.option);
            abstractSerializedData.writeString(this.message);
        }
    }

    public static class TL_stories_searchPosts extends TLObject {
        public static final int constructor = -780072697;
        public MediaArea area;
        public int flags;
        public String hashtag;
        public int limit;
        public String offset;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_foundStories.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-780072697);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.hashtag);
            }
            if ((this.flags & 2) != 0) {
                this.area.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 4) != 0) {
                this.peer.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_stories_sendReaction extends TLObject {
        public static final int constructor = 2144810674;
        public boolean add_to_recent;
        public int flags;
        public TLRPC.InputPeer peer;
        public TLRPC.Reaction reaction;
        public int story_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2144810674);
            int i = this.add_to_recent ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.story_id);
            this.reaction.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_stories_sendStory extends TLObject {
        public static final int constructor = -454661813;
        public String caption;
        public int flags;
        public TLRPC.InputPeer fwd_from_id;
        public int fwd_from_story;
        public boolean fwd_modified;
        public TLRPC.InputMedia media;
        public boolean noforwards;
        public TLRPC.InputPeer peer;
        public int period;
        public boolean pinned;
        public long random_id;
        public ArrayList<MediaArea> media_areas = new ArrayList<>();
        public ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
        public ArrayList<TLRPC.InputPrivacyRule> privacy_rules = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-454661813);
            int i = this.pinned ? this.flags | 4 : this.flags & (-5);
            this.flags = i;
            int i2 = this.noforwards ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.fwd_modified ? i2 | 128 : i2 & (-129);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            this.peer.serializeToStream(abstractSerializedData);
            this.media.serializeToStream(abstractSerializedData);
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.media_areas.size();
                abstractSerializedData.writeInt32(size);
                for (int i4 = 0; i4 < size; i4++) {
                    this.media_areas.get(i4).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.caption);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.entities.size();
                abstractSerializedData.writeInt32(size2);
                for (int i5 = 0; i5 < size2; i5++) {
                    this.entities.get(i5).serializeToStream(abstractSerializedData);
                }
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.privacy_rules.size();
            abstractSerializedData.writeInt32(size3);
            for (int i6 = 0; i6 < size3; i6++) {
                this.privacy_rules.get(i6).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt64(this.random_id);
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeInt32(this.period);
            }
            if ((this.flags & 64) != 0) {
                this.fwd_from_id.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 64) != 0) {
                abstractSerializedData.writeInt32(this.fwd_from_story);
            }
        }
    }

    public static class TL_stories_stories extends TLObject {
        public static final int constructor = 1673780490;
        public int count;
        public int flags;
        public ArrayList<StoryItem> stories = new ArrayList<>();
        public ArrayList<Integer> pinned_to_top = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_stories_stories TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1673780490 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stories_stories", Integer.valueOf(i)));
                }
                return null;
            }
            TL_stories_stories tL_stories_stories = new TL_stories_stories();
            tL_stories_stories.readParams(abstractSerializedData, z);
            return tL_stories_stories;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryItem TLdeserialize = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.stories.add(TLdeserialize);
            }
            if ((this.flags & 1) != 0) {
                int readInt323 = abstractSerializedData.readInt32(z);
                if (readInt323 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                    }
                    return;
                } else {
                    int readInt324 = abstractSerializedData.readInt32(z);
                    for (int i2 = 0; i2 < readInt324; i2++) {
                        this.pinned_to_top.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
                    }
                }
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt327 = abstractSerializedData.readInt32(z);
            if (readInt327 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt327)));
                }
                return;
            }
            int readInt328 = abstractSerializedData.readInt32(z);
            for (int i4 = 0; i4 < readInt328; i4++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1673780490);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.stories.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.stories.get(i).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.pinned_to_top.size();
                abstractSerializedData.writeInt32(size2);
                for (int i2 = 0; i2 < size2; i2++) {
                    abstractSerializedData.writeInt32(this.pinned_to_top.get(i2).intValue());
                }
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.chats.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.chats.get(i3).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size4 = this.users.size();
            abstractSerializedData.writeInt32(size4);
            for (int i4 = 0; i4 < size4; i4++) {
                this.users.get(i4).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_stories_storyViews extends TLObject {
        public static final int constructor = -560009955;
        public ArrayList<StoryViews> views = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_stories_storyViews TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-560009955 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stories_storyViews", Integer.valueOf(i)));
                }
                return null;
            }
            TL_stories_storyViews tL_stories_storyViews = new TL_stories_storyViews();
            tL_stories_storyViews.readParams(abstractSerializedData, z);
            return tL_stories_storyViews;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryViews TLdeserialize = StoryViews.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.views.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.User TLdeserialize2 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.users.add(TLdeserialize2);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-560009955);
            abstractSerializedData.writeInt32(481674261);
            int size = this.views.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.views.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.users.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_stories_togglePeerStoriesHidden extends TLObject {
        public static final int constructor = -1123805756;
        public boolean hidden;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1123805756);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeBool(this.hidden);
        }
    }

    public static class TL_stories_togglePinned extends TLObject {
        public static final int constructor = -1703566865;
        public ArrayList<Integer> id = new ArrayList<>();
        public TLRPC.InputPeer peer;
        public boolean pinned;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                vector.objects.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1703566865);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
            abstractSerializedData.writeBool(this.pinned);
        }
    }

    public static class TL_storyFwdHeader extends StoryFwdHeader {
        public static final int constructor = -1205411504;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.modified = (readInt32 & 8) != 0;
            if ((readInt32 & 1) != 0) {
                this.from = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 2) != 0) {
                this.from_name = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.story_id = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1205411504);
            int i = this.modified ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                this.from.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.from_name);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(this.story_id);
            }
        }
    }

    public static class TL_storyItem extends StoryItem {
        public static final int constructor = 2041735716;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.pinned = (readInt32 & 32) != 0;
            this.isPublic = (readInt32 & 128) != 0;
            this.close_friends = (readInt32 & 256) != 0;
            this.min = (readInt32 & 512) != 0;
            this.noforwards = (readInt32 & 1024) != 0;
            this.edited = (readInt32 & 2048) != 0;
            this.contacts = (readInt32 & 4096) != 0;
            this.selected_contacts = (readInt32 & 8192) != 0;
            this.out = (readInt32 & 65536) != 0;
            this.id = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
            if ((this.flags & 262144) != 0) {
                this.from_id = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 131072) != 0) {
                this.fwd_from = StoryFwdHeader.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.expire_date = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.caption = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageEntity TLdeserialize = TLRPC.MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.entities.add(TLdeserialize);
                }
            }
            this.media = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 16384) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                    }
                    return;
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt325; i2++) {
                    MediaArea TLdeserialize2 = MediaArea.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 == null) {
                        return;
                    }
                    this.media_areas.add(TLdeserialize2);
                }
            }
            if ((this.flags & 4) != 0) {
                int readInt326 = abstractSerializedData.readInt32(z);
                if (readInt326 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                    }
                    return;
                }
                int readInt327 = abstractSerializedData.readInt32(z);
                for (int i3 = 0; i3 < readInt327; i3++) {
                    TLRPC.PrivacyRule TLdeserialize3 = TLRPC.PrivacyRule.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize3 == null) {
                        return;
                    }
                    this.privacy.add(TLdeserialize3);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views = StoryViews.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.sent_reaction = TLRPC.Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2041735716);
            int i = this.pinned ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.isPublic ? i | 128 : i & (-129);
            this.flags = i2;
            int i3 = this.close_friends ? i2 | 256 : i2 & (-257);
            this.flags = i3;
            int i4 = this.min ? i3 | 512 : i3 & (-513);
            this.flags = i4;
            int i5 = this.noforwards ? i4 | 1024 : i4 & (-1025);
            this.flags = i5;
            int i6 = this.edited ? i5 | 2048 : i5 & (-2049);
            this.flags = i6;
            int i7 = this.contacts ? i6 | 4096 : i6 & (-4097);
            this.flags = i7;
            int i8 = this.selected_contacts ? i7 | 8192 : i7 & (-8193);
            this.flags = i8;
            int i9 = this.out ? i8 | 65536 : i8 & (-65537);
            this.flags = i9;
            abstractSerializedData.writeInt32(i9);
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeInt32(this.date);
            if ((this.flags & 262144) != 0) {
                this.from_id.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 131072) != 0) {
                this.fwd_from.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(this.expire_date);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.caption);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.entities.size();
                abstractSerializedData.writeInt32(size);
                for (int i10 = 0; i10 < size; i10++) {
                    this.entities.get(i10).serializeToStream(abstractSerializedData);
                }
            }
            this.media.serializeToStream(abstractSerializedData);
            if ((this.flags & 16384) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.media_areas.size();
                abstractSerializedData.writeInt32(size2);
                for (int i11 = 0; i11 < size2; i11++) {
                    this.media_areas.get(i11).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size3 = this.privacy.size();
                abstractSerializedData.writeInt32(size3);
                for (int i12 = 0; i12 < size3; i12++) {
                    this.privacy.get(i12).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                this.sent_reaction.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_storyItemDeleted extends StoryItem {
        public static final int constructor = 1374088783;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.id = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1374088783);
            abstractSerializedData.writeInt32(this.id);
        }
    }

    public static class TL_storyItemSkipped extends StoryItem {
        public static final int constructor = -5388013;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.close_friends = (readInt32 & 256) != 0;
            this.id = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
            this.expire_date = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-5388013);
            int i = this.close_friends ? this.flags | 256 : this.flags & (-257);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt32(this.expire_date);
        }
    }

    public static class TL_storyItem_layer160 extends TL_storyItem {
        public static final int constructor = 1445635639;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.pinned = (readInt32 & 32) != 0;
            this.isPublic = (readInt32 & 128) != 0;
            this.close_friends = (readInt32 & 256) != 0;
            this.min = (readInt32 & 512) != 0;
            this.noforwards = (readInt32 & 1024) != 0;
            this.edited = (readInt32 & 2048) != 0;
            this.contacts = (readInt32 & 4096) != 0;
            this.selected_contacts = (readInt32 & 8192) != 0;
            this.id = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
            this.expire_date = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.caption = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageEntity TLdeserialize = TLRPC.MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.entities.add(TLdeserialize);
                }
            }
            this.media = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 4) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                    }
                    return;
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt325; i2++) {
                    TLRPC.PrivacyRule TLdeserialize2 = TLRPC.PrivacyRule.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 == null) {
                        return;
                    }
                    this.privacy.add(TLdeserialize2);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views = StoryViews.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1445635639);
            int i = this.pinned ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.isPublic ? i | 128 : i & (-129);
            this.flags = i2;
            int i3 = this.close_friends ? i2 | 256 : i2 & (-257);
            this.flags = i3;
            int i4 = this.min ? i3 | 512 : i3 & (-513);
            this.flags = i4;
            int i5 = this.noforwards ? i4 | 1024 : i4 & (-1025);
            this.flags = i5;
            int i6 = this.edited ? i5 | 2048 : i5 & (-2049);
            this.flags = i6;
            int i7 = this.contacts ? i6 | 4096 : i6 & (-4097);
            this.flags = i7;
            int i8 = this.selected_contacts ? i7 | 8192 : i7 & (-8193);
            this.flags = i8;
            abstractSerializedData.writeInt32(i8);
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt32(this.expire_date);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.caption);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.entities.size();
                abstractSerializedData.writeInt32(size);
                for (int i9 = 0; i9 < size; i9++) {
                    this.entities.get(i9).serializeToStream(abstractSerializedData);
                }
            }
            this.media.serializeToStream(abstractSerializedData);
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.privacy.size();
                abstractSerializedData.writeInt32(size2);
                for (int i10 = 0; i10 < size2; i10++) {
                    this.privacy.get(i10).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_storyItem_layer166 extends TL_storyItem {
        public static final int constructor = 1153718222;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.pinned = (readInt32 & 32) != 0;
            this.isPublic = (readInt32 & 128) != 0;
            this.close_friends = (readInt32 & 256) != 0;
            this.min = (readInt32 & 512) != 0;
            this.noforwards = (readInt32 & 1024) != 0;
            this.edited = (readInt32 & 2048) != 0;
            this.contacts = (readInt32 & 4096) != 0;
            this.selected_contacts = (readInt32 & 8192) != 0;
            this.out = (readInt32 & 65536) != 0;
            this.id = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
            this.expire_date = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.caption = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageEntity TLdeserialize = TLRPC.MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.entities.add(TLdeserialize);
                }
            }
            this.media = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 16384) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                    }
                    return;
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt325; i2++) {
                    MediaArea TLdeserialize2 = MediaArea.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 == null) {
                        return;
                    }
                    this.media_areas.add(TLdeserialize2);
                }
            }
            if ((this.flags & 4) != 0) {
                int readInt326 = abstractSerializedData.readInt32(z);
                if (readInt326 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                    }
                    return;
                }
                int readInt327 = abstractSerializedData.readInt32(z);
                for (int i3 = 0; i3 < readInt327; i3++) {
                    TLRPC.PrivacyRule TLdeserialize3 = TLRPC.PrivacyRule.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize3 == null) {
                        return;
                    }
                    this.privacy.add(TLdeserialize3);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views = StoryViews.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.sent_reaction = TLRPC.Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1153718222);
            int i = this.pinned ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.isPublic ? i | 128 : i & (-129);
            this.flags = i2;
            int i3 = this.close_friends ? i2 | 256 : i2 & (-257);
            this.flags = i3;
            int i4 = this.min ? i3 | 512 : i3 & (-513);
            this.flags = i4;
            int i5 = this.noforwards ? i4 | 1024 : i4 & (-1025);
            this.flags = i5;
            int i6 = this.edited ? i5 | 2048 : i5 & (-2049);
            this.flags = i6;
            int i7 = this.contacts ? i6 | 4096 : i6 & (-4097);
            this.flags = i7;
            int i8 = this.selected_contacts ? i7 | 8192 : i7 & (-8193);
            this.flags = i8;
            int i9 = this.out ? i8 | 65536 : i8 & (-65537);
            this.flags = i9;
            abstractSerializedData.writeInt32(i9);
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt32(this.expire_date);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.caption);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.entities.size();
                abstractSerializedData.writeInt32(size);
                for (int i10 = 0; i10 < size; i10++) {
                    this.entities.get(i10).serializeToStream(abstractSerializedData);
                }
            }
            this.media.serializeToStream(abstractSerializedData);
            if ((this.flags & 16384) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.media_areas.size();
                abstractSerializedData.writeInt32(size2);
                for (int i11 = 0; i11 < size2; i11++) {
                    this.media_areas.get(i11).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size3 = this.privacy.size();
                abstractSerializedData.writeInt32(size3);
                for (int i12 = 0; i12 < size3; i12++) {
                    this.privacy.get(i12).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                this.sent_reaction.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_storyItem_layer174 extends TL_storyItem {
        public static final int constructor = -1352440415;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.pinned = (readInt32 & 32) != 0;
            this.isPublic = (readInt32 & 128) != 0;
            this.close_friends = (readInt32 & 256) != 0;
            this.min = (readInt32 & 512) != 0;
            this.noforwards = (readInt32 & 1024) != 0;
            this.edited = (readInt32 & 2048) != 0;
            this.contacts = (readInt32 & 4096) != 0;
            this.selected_contacts = (readInt32 & 8192) != 0;
            this.out = (readInt32 & 65536) != 0;
            this.id = abstractSerializedData.readInt32(z);
            this.date = abstractSerializedData.readInt32(z);
            if ((this.flags & 131072) != 0) {
                this.fwd_from = StoryFwdHeader.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            this.expire_date = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.caption = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageEntity TLdeserialize = TLRPC.MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.entities.add(TLdeserialize);
                }
            }
            this.media = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 16384) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                    }
                    return;
                }
                int readInt325 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt325; i2++) {
                    MediaArea TLdeserialize2 = MediaArea.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 == null) {
                        return;
                    }
                    this.media_areas.add(TLdeserialize2);
                }
            }
            if ((this.flags & 4) != 0) {
                int readInt326 = abstractSerializedData.readInt32(z);
                if (readInt326 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                    }
                    return;
                }
                int readInt327 = abstractSerializedData.readInt32(z);
                for (int i3 = 0; i3 < readInt327; i3++) {
                    TLRPC.PrivacyRule TLdeserialize3 = TLRPC.PrivacyRule.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize3 == null) {
                        return;
                    }
                    this.privacy.add(TLdeserialize3);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views = StoryViews.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.sent_reaction = TLRPC.Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1352440415);
            int i = this.pinned ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.isPublic ? i | 128 : i & (-129);
            this.flags = i2;
            int i3 = this.close_friends ? i2 | 256 : i2 & (-257);
            this.flags = i3;
            int i4 = this.min ? i3 | 512 : i3 & (-513);
            this.flags = i4;
            int i5 = this.noforwards ? i4 | 1024 : i4 & (-1025);
            this.flags = i5;
            int i6 = this.edited ? i5 | 2048 : i5 & (-2049);
            this.flags = i6;
            int i7 = this.contacts ? i6 | 4096 : i6 & (-4097);
            this.flags = i7;
            int i8 = this.selected_contacts ? i7 | 8192 : i7 & (-8193);
            this.flags = i8;
            int i9 = this.out ? i8 | 65536 : i8 & (-65537);
            this.flags = i9;
            abstractSerializedData.writeInt32(i9);
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeInt32(this.date);
            if ((this.flags & 131072) != 0) {
                this.fwd_from.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(this.expire_date);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.caption);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.entities.size();
                abstractSerializedData.writeInt32(size);
                for (int i10 = 0; i10 < size; i10++) {
                    this.entities.get(i10).serializeToStream(abstractSerializedData);
                }
            }
            this.media.serializeToStream(abstractSerializedData);
            if ((this.flags & 16384) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.media_areas.size();
                abstractSerializedData.writeInt32(size2);
                for (int i11 = 0; i11 < size2; i11++) {
                    this.media_areas.get(i11).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size3 = this.privacy.size();
                abstractSerializedData.writeInt32(size3);
                for (int i12 = 0; i12 < size3; i12++) {
                    this.privacy.get(i12).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.views.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                this.sent_reaction.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_storyReaction extends StoryReaction {
        public static final int constructor = 1620104917;
        public int date;
        public TLRPC.Reaction reaction;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer_id = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.date = abstractSerializedData.readInt32(z);
            this.reaction = TLRPC.Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1620104917);
            this.peer_id.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.date);
            this.reaction.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_storyReactionPublicForward extends StoryReaction {
        public static final int constructor = -1146411453;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.message = TLRPC.Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1146411453);
            this.message.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_storyReactionPublicRepost extends StoryReaction {
        public static final int constructor = -808644845;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer_id = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            StoryItem TLdeserialize = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.story = TLdeserialize;
            if (TLdeserialize != null) {
                TLdeserialize.dialogId = DialogObject.getPeerDialogId(this.peer_id);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-808644845);
            this.peer_id.serializeToStream(abstractSerializedData);
            this.story.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_storyReactionsList extends TLObject {
        public static final int constructor = -1436583780;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<StoryReaction> reactions = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_storyReactionsList TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1436583780 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_storyReactionsList", Integer.valueOf(i)));
                }
                return null;
            }
            TL_storyReactionsList tL_storyReactionsList = new TL_storyReactionsList();
            tL_storyReactionsList.readParams(abstractSerializedData, z);
            return tL_storyReactionsList;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryReaction TLdeserialize = StoryReaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.reactions.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1436583780);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.reactions.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.reactions.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
        }
    }

    public static class TL_storyView extends StoryView {
        public static final int constructor = -1329730875;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.blocked = (readInt32 & 1) != 0;
            this.blocked_my_stories_from = (readInt32 & 2) != 0;
            this.user_id = abstractSerializedData.readInt64(z);
            this.date = abstractSerializedData.readInt32(z);
            if ((this.flags & 4) != 0) {
                this.reaction = TLRPC.Reaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1329730875);
            int i = this.blocked ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.blocked_my_stories_from ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            abstractSerializedData.writeInt64(this.user_id);
            abstractSerializedData.writeInt32(this.date);
            if ((this.flags & 4) != 0) {
                this.reaction.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_storyViewPublicForward extends StoryView {
        public static final int constructor = -1870436597;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.blocked = (readInt32 & 1) != 0;
            this.blocked_my_stories_from = (readInt32 & 2) != 0;
            this.message = TLRPC.Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1870436597);
            int i = this.blocked ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.blocked_my_stories_from ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            this.message.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_storyViewPublicRepost extends StoryView {
        public static final int constructor = -1116418231;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.blocked = (readInt32 & 1) != 0;
            this.blocked_my_stories_from = (readInt32 & 2) != 0;
            this.peer_id = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.story = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1116418231);
            int i = this.blocked ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.blocked_my_stories_from ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            this.peer_id.serializeToStream(abstractSerializedData);
            this.story.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_storyViews extends StoryViews {
        public static final int constructor = -1923523370;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_viewers = (readInt32 & 2) != 0;
            this.views_count = abstractSerializedData.readInt32(z);
            if ((this.flags & 4) != 0) {
                this.forwards_count = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 8) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.ReactionCount TLdeserialize = TLRPC.ReactionCount.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.reactions.add(TLdeserialize);
                }
            }
            if ((this.flags & 16) != 0) {
                this.reactions_count = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 1) != 0) {
                int readInt324 = abstractSerializedData.readInt32(z);
                if (readInt324 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                    }
                } else {
                    int readInt325 = abstractSerializedData.readInt32(z);
                    for (int i2 = 0; i2 < readInt325; i2++) {
                        this.recent_viewers.add(Long.valueOf(abstractSerializedData.readInt64(z)));
                    }
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1923523370);
            int i = this.has_viewers ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt32(this.views_count);
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(this.forwards_count);
            }
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.reactions.size();
                abstractSerializedData.writeInt32(size);
                for (int i2 = 0; i2 < size; i2++) {
                    this.reactions.get(i2).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeInt32(this.reactions_count);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size2 = this.recent_viewers.size();
                abstractSerializedData.writeInt32(size2);
                for (int i3 = 0; i3 < size2; i3++) {
                    abstractSerializedData.writeInt64(this.recent_viewers.get(i3).longValue());
                }
            }
        }
    }

    public static class TL_storyViewsList extends StoryViewsList {
        public static final int constructor = 1507299269;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            this.views_count = abstractSerializedData.readInt32(z);
            this.forwards_count = abstractSerializedData.readInt32(z);
            this.reactions_count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryView TLdeserialize = StoryView.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.views.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1507299269);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(this.views_count);
            abstractSerializedData.writeInt32(this.forwards_count);
            abstractSerializedData.writeInt32(this.reactions_count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.views.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.views.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
        }
    }

    public static class TL_storyViewsList_layer167 extends StoryViewsList {
        public static final int constructor = 1189722604;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            this.reactions_count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StoryView TLdeserialize = StoryView.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.views.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.User TLdeserialize2 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.users.add(TLdeserialize2);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1189722604);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(this.reactions_count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.views.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.views.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.users.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
        }
    }

    public static class TL_storyViews_layer160 extends StoryViews {
        public static final int constructor = -748199729;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.views_count = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                int readInt32 = abstractSerializedData.readInt32(z);
                if (readInt32 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                    }
                } else {
                    int readInt322 = abstractSerializedData.readInt32(z);
                    for (int i = 0; i < readInt322; i++) {
                        this.recent_viewers.add(Long.valueOf(abstractSerializedData.readInt64(z)));
                    }
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-748199729);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.views_count);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.recent_viewers.size();
                abstractSerializedData.writeInt32(size);
                for (int i = 0; i < size; i++) {
                    abstractSerializedData.writeInt64(this.recent_viewers.get(i).longValue());
                }
            }
        }
    }

    public static class TL_storyViews_layer161 extends StoryViews {
        public static final int constructor = -968094825;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.views_count = abstractSerializedData.readInt32(z);
            this.reactions_count = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                int readInt32 = abstractSerializedData.readInt32(z);
                if (readInt32 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                    }
                } else {
                    int readInt322 = abstractSerializedData.readInt32(z);
                    for (int i = 0; i < readInt322; i++) {
                        this.recent_viewers.add(Long.valueOf(abstractSerializedData.readInt64(z)));
                    }
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-968094825);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.views_count);
            abstractSerializedData.writeInt32(this.reactions_count);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.recent_viewers.size();
                abstractSerializedData.writeInt32(size);
                for (int i = 0; i < size; i++) {
                    abstractSerializedData.writeInt64(this.recent_viewers.get(i).longValue());
                }
            }
        }
    }

    public static class TL_togglePinnedToTop extends TLObject {
        public static final int constructor = 187268763;
        public ArrayList<Integer> id = new ArrayList<>();
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(187268763);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.id.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeInt32(this.id.get(i).intValue());
            }
        }
    }

    public static class TL_updateReadStories extends TLRPC.Update {
        public static final int constructor = -145845461;
        public int max_id;
        public TLRPC.Peer peer;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.max_id = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-145845461);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.max_id);
        }
    }

    public static class TL_updateStoriesStealthMode extends TLRPC.Update {
        public static final int constructor = 738741697;
        public TL_storiesStealthMode stealth_mode;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.stealth_mode = TL_storiesStealthMode.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(738741697);
            this.stealth_mode.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_updateStory extends TLRPC.Update {
        public static final int constructor = 1974712216;
        public TLRPC.Peer peer;
        public StoryItem story;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.story = StoryItem.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1974712216);
            this.peer.serializeToStream(abstractSerializedData);
            this.story.serializeToStream(abstractSerializedData);
        }
    }

    public static abstract class stories_AllStories extends TLObject {
        public static stories_AllStories TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            stories_AllStories tL_stories_allStories = i != 291044926 ? i != 1862033025 ? null : new TL_stories_allStories() : new TL_stories_allStoriesNotModified();
            if (tL_stories_allStories == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in stories_AllStories", Integer.valueOf(i)));
            }
            if (tL_stories_allStories != null) {
                tL_stories_allStories.readParams(abstractSerializedData, z);
            }
            return tL_stories_allStories;
        }
    }
}
