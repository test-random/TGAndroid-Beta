package org.telegram.tgnet.tl;

import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1;
import org.telegram.tgnet.TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_stars;

public class TL_stars {

    public static class InputSavedStarGift extends TLObject {
        public static InputSavedStarGift TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            InputSavedStarGift tL_inputSavedStarGiftUser = i != -251549057 ? i != 1764202389 ? null : new TL_inputSavedStarGiftUser() : new TL_inputSavedStarGiftChat();
            if (tL_inputSavedStarGiftUser == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in InputSavedStarGift", Integer.valueOf(i)));
            }
            if (tL_inputSavedStarGiftUser != null) {
                tL_inputSavedStarGiftUser.readParams(inputSerializedData, z);
            }
            return tL_inputSavedStarGiftUser;
        }
    }

    public static final class SavedStarGift extends TLObject {
        public static final int constructor = 1616305061;
        public int can_export_at;
        public boolean can_upgrade;
        public long convert_stars;
        public int date;
        public int flags;
        public TLRPC.Peer from_id;
        public StarGift gift;
        public TLRPC.TL_textWithEntities message;
        public int msg_id;
        public boolean name_hidden;
        public boolean refunded;
        public long saved_id;
        public long transfer_stars;
        public boolean unsaved;
        public long upgrade_stars;

        public static SavedStarGift TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (1616305061 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in savedStarGift", Integer.valueOf(i)));
                }
                return null;
            }
            SavedStarGift savedStarGift = new SavedStarGift();
            savedStarGift.readParams(inputSerializedData, z);
            return savedStarGift;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.name_hidden = (readInt32 & 1) != 0;
            this.unsaved = (readInt32 & 32) != 0;
            this.refunded = (readInt32 & 512) != 0;
            this.can_upgrade = (readInt32 & 1024) != 0;
            if ((readInt32 & 2) != 0) {
                this.from_id = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            this.date = inputSerializedData.readInt32(z);
            this.gift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 4) != 0) {
                this.message = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 8) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 2048) != 0) {
                this.saved_id = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 16) != 0) {
                this.convert_stars = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 64) != 0) {
                this.upgrade_stars = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 128) != 0) {
                this.can_export_at = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 256) != 0) {
                this.transfer_stars = inputSerializedData.readInt64(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1616305061);
            int i = this.name_hidden ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.unsaved ? i | 32 : i & (-33);
            this.flags = i2;
            int i3 = this.refunded ? i2 | 512 : i2 & (-513);
            this.flags = i3;
            int i4 = this.can_upgrade ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            outputSerializedData.writeInt32(i4);
            if ((this.flags & 2) != 0) {
                this.from_id.serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(this.date);
            this.gift.serializeToStream(outputSerializedData);
            if ((this.flags & 4) != 0) {
                this.message.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 2048) != 0) {
                outputSerializedData.writeInt64(this.saved_id);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeInt64(this.convert_stars);
            }
            if ((this.flags & 64) != 0) {
                outputSerializedData.writeInt64(this.upgrade_stars);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeInt32(this.can_export_at);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt64(this.transfer_stars);
            }
        }
    }

    public static class StarGift extends TLObject {
        public ArrayList<StarGiftAttribute> attributes = new ArrayList<>();
        public int availability_issued;
        public int availability_remains;
        public int availability_total;
        public boolean birthday;
        public boolean can_upgrade;
        public long convert_stars;
        public int first_sale_date;
        public int flags;
        public long id;
        public int last_sale_date;
        public boolean limited;
        public int num;
        public String owner_address;
        public TLRPC.Peer owner_id;
        public String owner_name;
        public String slug;
        public boolean sold_out;
        public long stars;
        public TLRPC.Document sticker;
        public String title;
        public long upgrade_stars;

        public static StarGift TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarGift tL_starGift_layer190;
            switch (i) {
                case -1365150482:
                    tL_starGift_layer190 = new TL_starGift_layer190();
                    break;
                case -218202550:
                    tL_starGift_layer190 = new TL_starGiftUnique();
                    break;
                case 46953416:
                    tL_starGift_layer190 = new TL_starGift();
                    break;
                case 880997154:
                    tL_starGift_layer190 = new TL_starGiftUnique_layer197();
                    break;
                case 1237678029:
                    tL_starGift_layer190 = new TL_starGift_layer195();
                    break;
                case 1779697613:
                    tL_starGift_layer190 = new TL_starGiftUnique_layer196();
                    break;
                default:
                    tL_starGift_layer190 = null;
                    break;
            }
            if (tL_starGift_layer190 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGift", Integer.valueOf(i)));
            }
            if (tL_starGift_layer190 != null) {
                tL_starGift_layer190.readParams(inputSerializedData, z);
            }
            return tL_starGift_layer190;
        }

        public TLRPC.Document getDocument() {
            TLRPC.Document document = this.sticker;
            if (document != null) {
                return document;
            }
            Iterator<StarGiftAttribute> it = this.attributes.iterator();
            while (it.hasNext()) {
                StarGiftAttribute next = it.next();
                if (next instanceof starGiftAttributeModel) {
                    return ((starGiftAttributeModel) next).document;
                }
            }
            return null;
        }
    }

    public static class StarGiftAttribute extends TLObject {
        public String name;
        public int rarity_permille;

        public static StarGiftAttribute TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarGiftAttribute stargiftattributebackdrop;
            switch (i) {
                case -1809377438:
                    stargiftattributebackdrop = new starGiftAttributeBackdrop();
                    break;
                case -1070837941:
                    stargiftattributebackdrop = new starGiftAttributeOriginalDetails_layer197();
                    break;
                case -524291476:
                    stargiftattributebackdrop = new starGiftAttributeOriginalDetails();
                    break;
                case 330104601:
                    stargiftattributebackdrop = new starGiftAttributePattern();
                    break;
                case 970559507:
                    stargiftattributebackdrop = new starGiftAttributeModel();
                    break;
                default:
                    stargiftattributebackdrop = null;
                    break;
            }
            if (stargiftattributebackdrop == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGiftAttribute", Integer.valueOf(i)));
            }
            if (stargiftattributebackdrop != null) {
                stargiftattributebackdrop.readParams(inputSerializedData, z);
            }
            return stargiftattributebackdrop;
        }
    }

    public static class StarGifts extends TLObject {
        public static StarGifts TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarGifts tL_starGiftsNotModified = i != -1877571094 ? i != -1551326360 ? null : new TL_starGiftsNotModified() : new TL_starGifts();
            if (tL_starGiftsNotModified == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGifts", Integer.valueOf(i)));
            }
            if (tL_starGiftsNotModified != null) {
                tL_starGiftsNotModified.readParams(inputSerializedData, z);
            }
            return tL_starGiftsNotModified;
        }
    }

    public static class StarsAmount extends TLObject {
        public static final int constructor = -1145654109;
        public long amount;
        public int nanos;

        public StarsAmount() {
        }

        public StarsAmount(long j) {
            this.amount = j;
            this.nanos = 0;
        }

        public static StarsAmount TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1145654109 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in StarsAmount", Integer.valueOf(i)));
                }
                return null;
            }
            StarsAmount starsAmount = new StarsAmount();
            starsAmount.readParams(inputSerializedData, z);
            return starsAmount;
        }

        public boolean equals(StarsAmount starsAmount) {
            return starsAmount != null && this.amount == starsAmount.amount && this.nanos == starsAmount.nanos;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.amount = inputSerializedData.readInt64(z);
            this.nanos = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1145654109);
            outputSerializedData.writeInt64(this.amount);
            outputSerializedData.writeInt32(this.nanos);
        }
    }

    public static class StarsStatus extends TLObject {
        public int flags;
        public String next_offset;
        public long subscriptions_missing_balance;
        public String subscriptions_next_offset;
        public StarsAmount balance = new StarsAmount(0);
        public ArrayList<StarsSubscription> subscriptions = new ArrayList<>();
        public ArrayList<StarsTransaction> history = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static StarsStatus TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarsStatus tL_payments_starsStatus = i != -1141231252 ? i != 1822222573 ? null : new TL_payments_starsStatus() : new TL_payments_starsStatus_layer194();
            if (tL_payments_starsStatus == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarsStatus", Integer.valueOf(i)));
            }
            if (tL_payments_starsStatus != null) {
                tL_payments_starsStatus.readParams(inputSerializedData, z);
            }
            return tL_payments_starsStatus;
        }
    }

    public static class StarsSubscription extends TLObject {
        public boolean bot_canceled;
        public boolean can_refulfill;
        public boolean canceled;
        public String chat_invite_hash;
        public int flags;
        public String id;
        public String invoice_slug;
        public boolean missing_balance;
        public TLRPC.Peer peer;
        public TLRPC.WebDocument photo;
        public TL_starsSubscriptionPricing pricing;
        public String title;
        public int until_date;

        public static StarsSubscription TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarsSubscription tL_starsSubscription_layer193 = i != -797707802 ? i != 779004698 ? i != 1401868056 ? null : new TL_starsSubscription_layer193() : new TL_starsSubscription() : new TL_starsSubscription_old();
            if (tL_starsSubscription_layer193 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarsTransaction", Integer.valueOf(i)));
            }
            if (tL_starsSubscription_layer193 != null) {
                tL_starsSubscription_layer193.readParams(inputSerializedData, z);
            }
            return tL_starsSubscription_layer193;
        }
    }

    public static class StarsTransaction extends TLObject {
        public byte[] bot_payload;
        public int date;
        public String description;
        public boolean failed;
        public int flags;
        public boolean floodskip;
        public int floodskip_number;
        public boolean gift;
        public int giveaway_post_id;
        public String id;
        public int msg_id;
        public StarsTransactionPeer peer;
        public boolean pending;
        public TLRPC.WebDocument photo;
        public boolean reaction;
        public TLRPC.Peer received_by;
        public boolean refund;
        public TLRPC.Peer sent_by;
        public StarGift stargift;
        public boolean stargift_upgrade;
        public StarsAmount starref_amount;
        public int starref_commission_permille;
        public TLRPC.Peer starref_peer;
        public boolean subscription;
        public int subscription_period;
        public String title;
        public int transaction_date;
        public String transaction_url;
        public StarsAmount stars = new StarsAmount(0);
        public ArrayList<TLRPC.MessageMedia> extended_media = new ArrayList<>();

        public static StarsTransaction TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarsTransaction tL_starsTransaction_layer182;
            switch (i) {
                case -1442789224:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer182();
                    break;
                case -865044046:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer181();
                    break;
                case -294313259:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer188();
                    break;
                case 178185410:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer191();
                    break;
                case 766853519:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer185();
                    break;
                case 903148150:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer194();
                    break;
                case 1127934763:
                    tL_starsTransaction_layer182 = new TL_starsTransaction_layer186();
                    break;
                case 1692387622:
                    tL_starsTransaction_layer182 = new TL_starsTransaction();
                    break;
                default:
                    tL_starsTransaction_layer182 = null;
                    break;
            }
            if (tL_starsTransaction_layer182 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarsTransaction", Integer.valueOf(i)));
            }
            if (tL_starsTransaction_layer182 != null) {
                tL_starsTransaction_layer182.readParams(inputSerializedData, z);
            }
            return tL_starsTransaction_layer182;
        }
    }

    public static class StarsTransactionPeer extends TLObject {
        public TLRPC.Peer peer;

        public static StarsTransactionPeer TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarsTransactionPeer tL_starsTransactionPeerUnsupported;
            switch (i) {
                case -1779253276:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerUnsupported();
                    break;
                case -1269320843:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerAppStore();
                    break;
                case -670195363:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeer();
                    break;
                case -382740222:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerFragment();
                    break;
                case -110658899:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerAPI();
                    break;
                case 621656824:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerPremiumBot();
                    break;
                case 1617438738:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerAds();
                    break;
                case 2069236235:
                    tL_starsTransactionPeerUnsupported = new TL_starsTransactionPeerPlayMarket();
                    break;
                default:
                    tL_starsTransactionPeerUnsupported = null;
                    break;
            }
            if (tL_starsTransactionPeerUnsupported == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarsTransactionPeer", Integer.valueOf(i)));
            }
            if (tL_starsTransactionPeerUnsupported != null) {
                tL_starsTransactionPeerUnsupported.readParams(inputSerializedData, z);
            }
            return tL_starsTransactionPeerUnsupported;
        }
    }

    public static class TL_changeStarsSubscription extends TLObject {
        public static final int constructor = -948500360;
        public Boolean canceled;
        public int flags;
        public TLRPC.InputPeer peer;
        public String subscription_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-948500360);
            int i = this.canceled != null ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.subscription_id);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeBool(this.canceled.booleanValue());
            }
        }
    }

    public static class TL_fulfillStarsSubscription extends TLObject {
        public static final int constructor = -866391117;
        public TLRPC.InputPeer peer;
        public String subscription_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-866391117);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.subscription_id);
        }
    }

    public static class TL_getStarsSubscriptions extends TLObject {
        public static final int constructor = 52761285;
        public int flags;
        public boolean missing_balance;
        public String offset;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return StarsStatus.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(52761285);
            int i = this.missing_balance ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.offset);
        }
    }

    public static final class TL_inputSavedStarGiftChat extends InputSavedStarGift {
        public static final int constructor = -251549057;
        public TLRPC.InputPeer peer;
        public long saved_id;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.peer = TLRPC.InputPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.saved_id = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-251549057);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt64(this.saved_id);
        }
    }

    public static final class TL_inputSavedStarGiftUser extends InputSavedStarGift {
        public static final int constructor = 1764202389;
        public int msg_id;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.msg_id = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1764202389);
            outputSerializedData.writeInt32(this.msg_id);
        }
    }

    public static class TL_payments_getStarsGiftOptions extends TLObject {
        public static final int constructor = -741774392;
        public int flags;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserialize(inputSerializedData, i, z, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i2, boolean z2) {
                    return TL_stars.TL_starsGiftOption.TLdeserialize(inputSerializedData2, i2, z2);
                }
            });
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-741774392);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                this.user_id.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_payments_getStarsGiveawayOptions extends TLObject {
        public static final int constructor = -1122042562;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserialize(inputSerializedData, i, z, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i2, boolean z2) {
                    return TL_stars.TL_starsGiveawayOption.TLdeserialize(inputSerializedData2, i2, z2);
                }
            });
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1122042562);
        }
    }

    public static class TL_payments_getStarsStatus extends TLObject {
        public static final int constructor = 273665959;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return StarsStatus.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(273665959);
            this.peer.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_payments_getStarsTopupOptions extends TLObject {
        public static final int constructor = -1072773165;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserialize(inputSerializedData, i, z, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i2, boolean z2) {
                    return TL_stars.TL_starsTopupOption.TLdeserialize(inputSerializedData2, i2, z2);
                }
            });
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1072773165);
        }
    }

    public static class TL_payments_getStarsTransactions extends TLObject {
        public static final int constructor = 1731904249;
        public int flags;
        public boolean inbound;
        public String offset;
        public boolean outbound;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return StarsStatus.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1731904249);
            int i = this.inbound ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.outbound ? i | 2 : i & (-3);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.offset);
        }
    }

    public static final class TL_payments_savedStarGifts extends TLObject {
        public static final int constructor = -1779201615;
        public boolean chat_notifications_enabled;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<SavedStarGift> gifts = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_payments_savedStarGifts TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1779201615 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_payments_savedStarGifts", Integer.valueOf(i)));
                }
                return null;
            }
            TL_payments_savedStarGifts tL_payments_savedStarGifts = new TL_payments_savedStarGifts();
            tL_payments_savedStarGifts.readParams(inputSerializedData, z);
            return tL_payments_savedStarGifts;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.count = inputSerializedData.readInt32(z);
            if ((this.flags & 2) != 0) {
                this.chat_notifications_enabled = inputSerializedData.readBool(z);
            }
            this.gifts = Vector.deserialize(inputSerializedData, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i, boolean z2) {
                    return TL_stars.SavedStarGift.TLdeserialize(inputSerializedData2, i, z2);
                }
            }, z);
            if ((this.flags & 1) != 0) {
                this.next_offset = inputSerializedData.readString(z);
            }
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1779201615);
            outputSerializedData.writeInt32(this.flags);
            outputSerializedData.writeInt32(this.count);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeBool(this.chat_notifications_enabled);
            }
            Vector.serialize(outputSerializedData, this.gifts);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.next_offset);
            }
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class TL_payments_sendStarsForm extends TLObject {
        public static final int constructor = 2040056084;
        public long form_id;
        public TLRPC.InputInvoice invoice;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.payments_PaymentResult.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(2040056084);
            outputSerializedData.writeInt64(this.form_id);
            this.invoice.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_payments_starsStatus extends StarsStatus {
        public static final int constructor = 1822222573;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.balance = StarsAmount.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 2) != 0) {
                this.subscriptions = Vector.deserialize(inputSerializedData, new TL_stars$TL_payments_starsStatus$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4) != 0) {
                this.subscriptions_next_offset = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.subscriptions_missing_balance = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 8) != 0) {
                this.history = Vector.deserialize(inputSerializedData, new TL_stars$TL_payments_starsStatus$$ExternalSyntheticLambda1(), z);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = inputSerializedData.readString(z);
            }
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1822222573);
            outputSerializedData.writeInt32(this.flags);
            this.balance.serializeToStream(outputSerializedData);
            if ((this.flags & 2) != 0) {
                Vector.serialize(outputSerializedData, this.subscriptions);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeString(this.subscriptions_next_offset);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeInt64(this.subscriptions_missing_balance);
            }
            if ((this.flags & 8) != 0) {
                Vector.serialize(outputSerializedData, this.history);
            }
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.next_offset);
            }
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class TL_payments_starsStatus_layer194 extends TL_payments_starsStatus {
        public static final int constructor = -1141231252;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.balance = new StarsAmount(inputSerializedData.readInt64(z));
            if ((this.flags & 2) != 0) {
                this.subscriptions = Vector.deserialize(inputSerializedData, new TL_stars$TL_payments_starsStatus$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4) != 0) {
                this.subscriptions_next_offset = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.subscriptions_missing_balance = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 8) != 0) {
                this.history = Vector.deserialize(inputSerializedData, new TL_stars$TL_payments_starsStatus$$ExternalSyntheticLambda1(), z);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = inputSerializedData.readString(z);
            }
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1141231252);
            outputSerializedData.writeInt32(this.flags);
            outputSerializedData.writeInt64(this.balance.amount);
            if ((this.flags & 2) != 0) {
                Vector.serialize(outputSerializedData, this.subscriptions);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeString(this.subscriptions_next_offset);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeInt64(this.subscriptions_missing_balance);
            }
            if ((this.flags & 8) != 0) {
                Vector.serialize(outputSerializedData, this.history);
            }
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.next_offset);
            }
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static final class TL_payments_uniqueStarGift extends TLObject {
        public static final int constructor = -895289845;
        public StarGift gift;
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_payments_uniqueStarGift TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-895289845 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_payments_uniqueStarGift", Integer.valueOf(i)));
                }
                return null;
            }
            TL_payments_uniqueStarGift tL_payments_uniqueStarGift = new TL_payments_uniqueStarGift();
            tL_payments_uniqueStarGift.readParams(inputSerializedData, z);
            return tL_payments_uniqueStarGift;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.gift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-895289845);
            this.gift.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class TL_starGift extends StarGift {
        public static final int constructor = 46953416;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.limited = (readInt32 & 1) != 0;
            this.sold_out = (readInt32 & 2) != 0;
            this.birthday = (readInt32 & 4) != 0;
            this.can_upgrade = (readInt32 & 8) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.sticker = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.stars = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.availability_remains = inputSerializedData.readInt32(z);
                this.availability_total = inputSerializedData.readInt32(z);
            }
            this.convert_stars = inputSerializedData.readInt64(z);
            if ((this.flags & 2) != 0) {
                this.first_sale_date = inputSerializedData.readInt32(z);
                this.last_sale_date = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 8) != 0) {
                this.upgrade_stars = inputSerializedData.readInt64(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(46953416);
            int i = this.limited ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.sold_out ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.birthday ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            int i4 = this.can_upgrade ? i3 | 8 : i3 & (-9);
            this.flags = i4;
            outputSerializedData.writeInt32(i4);
            outputSerializedData.writeInt64(this.id);
            this.sticker.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt32(this.availability_remains);
                outputSerializedData.writeInt32(this.availability_total);
            }
            outputSerializedData.writeInt64(this.convert_stars);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.first_sale_date);
                outputSerializedData.writeInt32(this.last_sale_date);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeInt64(this.upgrade_stars);
            }
        }
    }

    public static class TL_starGiftUnique extends StarGift {
        public static final int constructor = -218202550;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.id = inputSerializedData.readInt64(z);
            this.title = inputSerializedData.readString(z);
            this.slug = inputSerializedData.readString(z);
            this.num = inputSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.owner_id = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 2) != 0) {
                this.owner_name = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.owner_address = inputSerializedData.readString(z);
            }
            this.attributes = Vector.deserialize(inputSerializedData, new TL_stars$TL_starGiftUnique$$ExternalSyntheticLambda0(), z);
            this.availability_issued = inputSerializedData.readInt32(z);
            this.availability_total = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-218202550);
            outputSerializedData.writeInt32(this.flags);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeString(this.title);
            outputSerializedData.writeString(this.slug);
            outputSerializedData.writeInt32(this.num);
            if ((this.flags & 1) != 0) {
                this.owner_id.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.owner_name);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeString(this.owner_address);
            }
            Vector.serialize(outputSerializedData, this.attributes);
            outputSerializedData.writeInt32(this.availability_issued);
            outputSerializedData.writeInt32(this.availability_total);
        }
    }

    public static class TL_starGiftUnique_layer196 extends TL_starGiftUnique {
        public static final int constructor = 1779697613;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.id = inputSerializedData.readInt64(z);
            this.title = inputSerializedData.readString(z);
            this.num = inputSerializedData.readInt32(z);
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            this.owner_id = tL_peerUser;
            tL_peerUser.user_id = inputSerializedData.readInt64(z);
            this.attributes = Vector.deserialize(inputSerializedData, new TL_stars$TL_starGiftUnique$$ExternalSyntheticLambda0(), z);
            this.availability_issued = inputSerializedData.readInt32(z);
            this.availability_total = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1779697613);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeString(this.title);
            outputSerializedData.writeInt32(this.num);
            outputSerializedData.writeInt64(this.owner_id.user_id);
            Vector.serialize(outputSerializedData, this.attributes);
            outputSerializedData.writeInt32(this.availability_issued);
            outputSerializedData.writeInt32(this.availability_total);
        }
    }

    public static class TL_starGiftUnique_layer197 extends TL_starGiftUnique {
        public static final int constructor = 880997154;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.id = inputSerializedData.readInt64(z);
            this.title = inputSerializedData.readString(z);
            this.slug = inputSerializedData.readString(z);
            this.num = inputSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                this.owner_id = tL_peerUser;
                tL_peerUser.user_id = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.owner_name = inputSerializedData.readString(z);
            }
            this.attributes = Vector.deserialize(inputSerializedData, new TL_stars$TL_starGiftUnique$$ExternalSyntheticLambda0(), z);
            this.availability_issued = inputSerializedData.readInt32(z);
            this.availability_total = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(880997154);
            outputSerializedData.writeInt32(this.flags);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeString(this.title);
            outputSerializedData.writeString(this.slug);
            outputSerializedData.writeInt32(this.num);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt64(this.owner_id.user_id);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.owner_name);
            }
            Vector.serialize(outputSerializedData, this.attributes);
            outputSerializedData.writeInt32(this.availability_issued);
            outputSerializedData.writeInt32(this.availability_total);
        }
    }

    public static class TL_starGift_layer190 extends TL_starGift {
        public static final int constructor = -1365150482;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.limited = (readInt32 & 1) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.sticker = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.stars = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.availability_remains = inputSerializedData.readInt32(z);
                this.availability_total = inputSerializedData.readInt32(z);
            }
            this.convert_stars = inputSerializedData.readInt64(z);
            this.sold_out = this.limited && this.availability_remains <= 0;
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1365150482);
            int i = this.limited ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            this.sticker.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt32(this.availability_remains);
                outputSerializedData.writeInt32(this.availability_total);
            }
            outputSerializedData.writeInt64(this.convert_stars);
        }
    }

    public static class TL_starGift_layer195 extends TL_starGift {
        public static final int constructor = 1237678029;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.limited = (readInt32 & 1) != 0;
            this.sold_out = (readInt32 & 2) != 0;
            this.birthday = (readInt32 & 4) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.sticker = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.stars = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.availability_remains = inputSerializedData.readInt32(z);
                this.availability_total = inputSerializedData.readInt32(z);
            }
            this.convert_stars = inputSerializedData.readInt64(z);
            if ((this.flags & 2) != 0) {
                this.first_sale_date = inputSerializedData.readInt32(z);
                this.last_sale_date = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1237678029);
            int i = this.limited ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.sold_out ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.birthday ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeInt64(this.id);
            this.sticker.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt32(this.availability_remains);
                outputSerializedData.writeInt32(this.availability_total);
            }
            outputSerializedData.writeInt64(this.convert_stars);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.first_sale_date);
                outputSerializedData.writeInt32(this.last_sale_date);
            }
        }
    }

    public static class TL_starGifts extends StarGifts {
        public static final int constructor = -1877571094;
        public ArrayList<StarGift> gifts = new ArrayList<>();
        public int hash;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.hash = inputSerializedData.readInt32(z);
            this.gifts = Vector.deserialize(inputSerializedData, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i, boolean z2) {
                    return TL_stars.StarGift.TLdeserialize(inputSerializedData2, i, z2);
                }
            }, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1877571094);
            outputSerializedData.writeInt32(this.hash);
            Vector.serialize(outputSerializedData, this.gifts);
        }
    }

    public static class TL_starGiftsNotModified extends StarGifts {
        public static final int constructor = -1551326360;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1551326360);
        }
    }

    public static class TL_starsGiftOption extends TLObject {
        public static final int constructor = 1577421297;
        public long amount;
        public String currency;
        public boolean extended;
        public int flags;
        public boolean loadingStorePrice;
        public boolean missingStorePrice;
        public long stars;
        public String store_product;

        public static TL_starsGiftOption TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (1577421297 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsGiftOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsGiftOption tL_starsGiftOption = new TL_starsGiftOption();
            tL_starsGiftOption.readParams(inputSerializedData, z);
            return tL_starsGiftOption;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.extended = (readInt32 & 2) != 0;
            this.stars = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.store_product = inputSerializedData.readString(z);
            }
            this.currency = inputSerializedData.readString(z);
            this.amount = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1577421297);
            int i = this.extended ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.store_product);
            }
            outputSerializedData.writeString(this.currency);
            outputSerializedData.writeInt64(this.amount);
        }
    }

    public static class TL_starsGiveawayOption extends TLObject {
        public static final int constructor = -1798404822;
        public long amount;
        public String currency;
        public boolean extended;
        public int flags;
        public boolean isDefault;
        public boolean loadingStorePrice;
        public boolean missingStorePrice;
        public long stars;
        public String store_product;
        public ArrayList<TL_starsGiveawayWinnersOption> winners = new ArrayList<>();
        public int yearly_boosts;

        public static TL_starsGiveawayOption TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1798404822 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsGiveawayOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsGiveawayOption tL_starsGiveawayOption = new TL_starsGiveawayOption();
            tL_starsGiveawayOption.readParams(inputSerializedData, z);
            return tL_starsGiveawayOption;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.extended = (readInt32 & 1) != 0;
            this.isDefault = (readInt32 & 2) != 0;
            this.stars = inputSerializedData.readInt64(z);
            this.yearly_boosts = inputSerializedData.readInt32(z);
            if ((this.flags & 4) != 0) {
                this.store_product = inputSerializedData.readString(z);
            }
            this.currency = inputSerializedData.readString(z);
            this.amount = inputSerializedData.readInt64(z);
            this.winners = Vector.deserialize(inputSerializedData, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i, boolean z2) {
                    return TL_stars.TL_starsGiveawayWinnersOption.TLdeserialize(inputSerializedData2, i, z2);
                }
            }, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1798404822);
            int i = this.extended ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.isDefault ? i | 2 : i & (-3);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            outputSerializedData.writeInt64(this.stars);
            outputSerializedData.writeInt32(this.yearly_boosts);
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeString(this.store_product);
            }
            outputSerializedData.writeString(this.currency);
            outputSerializedData.writeInt64(this.amount);
            Vector.serialize(outputSerializedData, this.winners);
        }
    }

    public static class TL_starsGiveawayWinnersOption extends TLObject {
        public static final int constructor = 1411605001;
        public int flags;
        public boolean isDefault;
        public long per_user_stars;
        public int users;

        public static TL_starsGiveawayWinnersOption TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (1411605001 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsGiveawayWinnersOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsGiveawayWinnersOption tL_starsGiveawayWinnersOption = new TL_starsGiveawayWinnersOption();
            tL_starsGiveawayWinnersOption.readParams(inputSerializedData, z);
            return tL_starsGiveawayWinnersOption;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.isDefault = (readInt32 & 1) != 0;
            this.users = inputSerializedData.readInt32(z);
            this.per_user_stars = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1411605001);
            int i = this.isDefault ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt32(this.users);
            outputSerializedData.writeInt64(this.per_user_stars);
        }
    }

    public static class TL_starsSubscription extends StarsSubscription {
        public static final int constructor = 779004698;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.canceled = (readInt32 & 1) != 0;
            this.can_refulfill = (readInt32 & 2) != 0;
            this.missing_balance = (readInt32 & 4) != 0;
            this.bot_canceled = (readInt32 & 128) != 0;
            this.id = inputSerializedData.readString(z);
            this.peer = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.until_date = inputSerializedData.readInt32(z);
            this.pricing = TL_starsSubscriptionPricing.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 8) != 0) {
                this.chat_invite_hash = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 32) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 64) != 0) {
                this.invoice_slug = inputSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(779004698);
            int i = this.canceled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.can_refulfill ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.missing_balance ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            int i4 = this.bot_canceled ? i3 | 128 : i3 & (-129);
            this.flags = i4;
            outputSerializedData.writeInt32(i4);
            outputSerializedData.writeString(this.id);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.until_date);
            this.pricing.serializeToStream(outputSerializedData);
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeString(this.chat_invite_hash);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 32) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 64) != 0) {
                outputSerializedData.writeString(this.invoice_slug);
            }
        }
    }

    public static class TL_starsSubscriptionPricing extends TLObject {
        public static final int constructor = 88173912;
        public long amount;
        public int period;

        public static TL_starsSubscriptionPricing TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (88173912 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsSubscriptionPricing", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsSubscriptionPricing tL_starsSubscriptionPricing = new TL_starsSubscriptionPricing();
            tL_starsSubscriptionPricing.readParams(inputSerializedData, z);
            return tL_starsSubscriptionPricing;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.period = inputSerializedData.readInt32(z);
            this.amount = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(88173912);
            outputSerializedData.writeInt32(this.period);
            outputSerializedData.writeInt64(this.amount);
        }
    }

    public static class TL_starsSubscription_layer193 extends StarsSubscription {
        public static final int constructor = 1401868056;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.canceled = (readInt32 & 1) != 0;
            this.can_refulfill = (readInt32 & 2) != 0;
            this.missing_balance = (readInt32 & 4) != 0;
            this.id = inputSerializedData.readString(z);
            this.peer = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.until_date = inputSerializedData.readInt32(z);
            this.pricing = TL_starsSubscriptionPricing.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 8) != 0) {
                this.chat_invite_hash = inputSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1401868056);
            int i = this.canceled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.can_refulfill ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.missing_balance ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeString(this.id);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.until_date);
            this.pricing.serializeToStream(outputSerializedData);
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeString(this.chat_invite_hash);
            }
        }
    }

    public static class TL_starsSubscription_old extends TL_starsSubscription {
        public static final int constructor = -797707802;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.canceled = (readInt32 & 1) != 0;
            this.can_refulfill = (readInt32 & 2) != 0;
            this.missing_balance = (readInt32 & 4) != 0;
            this.id = inputSerializedData.readString(z);
            this.peer = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.until_date = inputSerializedData.readInt32(z);
            this.pricing = TL_starsSubscriptionPricing.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-797707802);
            int i = this.canceled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.can_refulfill ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.missing_balance ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeString(this.id);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.until_date);
            this.pricing.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_starsTopupOption extends TLObject {
        public static final int constructor = 198776256;
        public long amount;
        public String currency;
        public boolean extended;
        public int flags;
        public boolean loadingStorePrice;
        public boolean missingStorePrice;
        public long stars;
        public String store_product;

        public static TL_starsTopupOption TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (198776256 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsTopupOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsTopupOption tL_starsTopupOption = new TL_starsTopupOption();
            tL_starsTopupOption.readParams(inputSerializedData, z);
            return tL_starsTopupOption;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.extended = (readInt32 & 2) != 0;
            this.stars = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.store_product = inputSerializedData.readString(z);
            }
            this.currency = inputSerializedData.readString(z);
            this.amount = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(198776256);
            int i = this.extended ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.store_product);
            }
            outputSerializedData.writeString(this.currency);
            outputSerializedData.writeInt64(this.amount);
        }
    }

    public static class TL_starsTransaction extends StarsTransaction {
        public static final int constructor = 1692387622;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.stargift_upgrade = (262144 & readInt32) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.floodskip = (readInt32 & 32768) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = StarsAmount.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = inputSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.floodskip_number = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 65536) != 0) {
                this.starref_commission_permille = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 131072) != 0) {
                this.starref_peer = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                this.starref_amount = StarsAmount.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1692387622);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            int i5 = this.reaction ? i4 | 2048 : i4 & (-2049);
            this.flags = i5;
            int i6 = this.subscription ? i5 | 4096 : i5 & (-4097);
            this.flags = i6;
            int i7 = this.floodskip ? i6 | 32768 : i6 & (-32769);
            this.flags = i7;
            int i8 = this.stargift_upgrade ? i7 | 262144 : i7 & (-262145);
            this.flags = i8;
            outputSerializedData.writeInt32(i8);
            this.stars.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                Vector.serialize(outputSerializedData, this.extended_media);
            }
            if ((this.flags & 4096) != 0) {
                outputSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                outputSerializedData.writeInt32(this.giveaway_post_id);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                outputSerializedData.writeInt32(this.floodskip_number);
            }
            if ((this.flags & 65536) != 0) {
                outputSerializedData.writeInt32(this.starref_commission_permille);
            }
            if ((this.flags & 131072) != 0) {
                this.starref_peer.serializeToStream(outputSerializedData);
                this.starref_amount.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_starsTransactionPeer extends StarsTransactionPeer {
        public static final int constructor = -670195363;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.peer = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-670195363);
            this.peer.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_starsTransactionPeerAPI extends StarsTransactionPeer {
        public static final int constructor = -110658899;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-110658899);
        }
    }

    public static class TL_starsTransactionPeerAds extends StarsTransactionPeer {
        public static final int constructor = 1617438738;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1617438738);
        }
    }

    public static class TL_starsTransactionPeerAppStore extends StarsTransactionPeer {
        public static final int constructor = -1269320843;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1269320843);
        }
    }

    public static class TL_starsTransactionPeerFragment extends StarsTransactionPeer {
        public static final int constructor = -382740222;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-382740222);
        }
    }

    public static class TL_starsTransactionPeerPlayMarket extends StarsTransactionPeer {
        public static final int constructor = 2069236235;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(2069236235);
        }
    }

    public static class TL_starsTransactionPeerPremiumBot extends StarsTransactionPeer {
        public static final int constructor = 621656824;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(621656824);
        }
    }

    public static class TL_starsTransactionPeerUnsupported extends StarsTransactionPeer {
        public static final int constructor = -1779253276;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1779253276);
        }
    }

    public static class TL_starsTransaction_layer181 extends StarsTransaction {
        public static final int constructor = -865044046;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-865044046);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_starsTransaction_layer182 extends TL_starsTransaction {
        public static final int constructor = -1442789224;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1442789224);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
        }
    }

    public static class TL_starsTransaction_layer185 extends TL_starsTransaction {
        public static final int constructor = 766853519;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = inputSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction$$ExternalSyntheticLambda0(), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(766853519);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            outputSerializedData.writeInt32(i4);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                Vector.serialize(outputSerializedData, this.extended_media);
            }
        }
    }

    public static class TL_starsTransaction_layer186 extends TL_starsTransaction {
        public static final int constructor = 1127934763;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = inputSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1127934763);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            int i5 = this.reaction ? i4 | 2048 : i4 & (-2049);
            this.flags = i5;
            int i6 = this.subscription ? i5 | 4096 : i5 & (-4097);
            this.flags = i6;
            outputSerializedData.writeInt32(i6);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                Vector.serialize(outputSerializedData, this.extended_media);
            }
            if ((this.flags & 4096) != 0) {
                outputSerializedData.writeInt32(this.subscription_period);
            }
        }
    }

    public static class TL_starsTransaction_layer188 extends TL_starsTransaction {
        public static final int constructor = -294313259;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = inputSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-294313259);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            int i5 = this.reaction ? i4 | 2048 : i4 & (-2049);
            this.flags = i5;
            int i6 = this.subscription ? i5 | 4096 : i5 & (-4097);
            this.flags = i6;
            outputSerializedData.writeInt32(i6);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                Vector.serialize(outputSerializedData, this.extended_media);
            }
            if ((this.flags & 4096) != 0) {
                outputSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                outputSerializedData.writeInt32(this.giveaway_post_id);
            }
        }
    }

    public static class TL_starsTransaction_layer191 extends TL_starsTransaction {
        public static final int constructor = 178185410;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = inputSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(178185410);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            int i5 = this.reaction ? i4 | 2048 : i4 & (-2049);
            this.flags = i5;
            int i6 = this.subscription ? i5 | 4096 : i5 & (-4097);
            this.flags = i6;
            outputSerializedData.writeInt32(i6);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                Vector.serialize(outputSerializedData, this.extended_media);
            }
            if ((this.flags & 4096) != 0) {
                outputSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                outputSerializedData.writeInt32(this.giveaway_post_id);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_starsTransaction_layer194 extends TL_starsTransaction {
        public static final int constructor = 903148150;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.floodskip = (readInt32 & 32768) != 0;
            this.id = inputSerializedData.readString(z);
            this.stars = new StarsAmount(inputSerializedData.readInt64(z));
            this.date = inputSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = inputSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = inputSerializedData.readInt32(z);
                this.transaction_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = inputSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.floodskip_number = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(903148150);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            int i5 = this.reaction ? i4 | 2048 : i4 & (-2049);
            this.flags = i5;
            int i6 = this.subscription ? i5 | 4096 : i5 & (-4097);
            this.flags = i6;
            int i7 = this.floodskip ? i6 | 32768 : i6 & (-32769);
            this.flags = i7;
            outputSerializedData.writeInt32(i7);
            outputSerializedData.writeInt64(this.stars.amount);
            outputSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeInt32(this.transaction_date);
                outputSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                Vector.serialize(outputSerializedData, this.extended_media);
            }
            if ((this.flags & 4096) != 0) {
                outputSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                outputSerializedData.writeInt32(this.giveaway_post_id);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                outputSerializedData.writeInt32(this.floodskip_number);
            }
        }
    }

    public static class convertStarGift extends TLObject {
        public static final int constructor = 1958676331;
        public InputSavedStarGift stargift;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1958676331);
            this.stargift.serializeToStream(outputSerializedData);
        }
    }

    public static class getSavedStarGift extends TLObject {
        public static final int constructor = -1269456634;
        public ArrayList<InputSavedStarGift> stargift = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_payments_savedStarGifts.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1269456634);
            Vector.serialize(outputSerializedData, this.stargift);
        }
    }

    public static class getSavedStarGifts extends TLObject {
        public static final int constructor = 595791337;
        public boolean exclude_limited;
        public boolean exclude_saved;
        public boolean exclude_unique;
        public boolean exclude_unlimited;
        public boolean exclude_unsaved;
        public int flags;
        public int limit;
        public String offset;
        public TLRPC.InputPeer peer;
        public boolean sort_by_value;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_payments_savedStarGifts.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(595791337);
            int i = this.exclude_unsaved ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.exclude_saved ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.exclude_unlimited ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            int i4 = this.exclude_limited ? i3 | 8 : i3 & (-9);
            this.flags = i4;
            int i5 = this.exclude_unique ? i4 | 16 : i4 & (-17);
            this.flags = i5;
            int i6 = this.sort_by_value ? i5 | 32 : i5 & (-33);
            this.flags = i6;
            outputSerializedData.writeInt32(i6);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.offset);
            outputSerializedData.writeInt32(this.limit);
        }
    }

    public static class getStarGiftUpgradePreview extends TLObject {
        public static final int constructor = -1667580751;
        public long gift_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return starGiftUpgradePreview.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1667580751);
            outputSerializedData.writeInt64(this.gift_id);
        }
    }

    public static final class getStarGiftWithdrawalUrl extends TLObject {
        public static final int constructor = -798059608;
        public TLRPC.InputCheckPasswordSRP password;
        public InputSavedStarGift stargift;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return starGiftWithdrawalUrl.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-798059608);
            this.stargift.serializeToStream(outputSerializedData);
            this.password.serializeToStream(outputSerializedData);
        }
    }

    public static class getStarGifts extends TLObject {
        public static final int constructor = -1000983152;
        public int hash;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return StarGifts.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1000983152);
            outputSerializedData.writeInt32(this.hash);
        }
    }

    public static final class getUniqueStarGift extends TLObject {
        public static final int constructor = -1583919758;
        public String slug;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_payments_uniqueStarGift.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1583919758);
            outputSerializedData.writeString(this.slug);
        }
    }

    public static class saveStarGift extends TLObject {
        public static final int constructor = 707422588;
        public int flags;
        public InputSavedStarGift stargift;
        public boolean unsave;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(707422588);
            int i = this.unsave ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.stargift.serializeToStream(outputSerializedData);
        }
    }

    public static class starGiftAttributeBackdrop extends StarGiftAttribute {
        public static final int constructor = -1809377438;
        public int center_color;
        public int edge_color;
        public int pattern_color;
        public int text_color;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.name = inputSerializedData.readString(z);
            this.center_color = inputSerializedData.readInt32(z);
            this.edge_color = inputSerializedData.readInt32(z);
            this.pattern_color = inputSerializedData.readInt32(z);
            this.text_color = inputSerializedData.readInt32(z);
            this.rarity_permille = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1809377438);
            outputSerializedData.writeString(this.name);
            outputSerializedData.writeInt32(this.center_color);
            outputSerializedData.writeInt32(this.edge_color);
            outputSerializedData.writeInt32(this.pattern_color);
            outputSerializedData.writeInt32(this.text_color);
            outputSerializedData.writeInt32(this.rarity_permille);
        }
    }

    public static class starGiftAttributeModel extends StarGiftAttribute {
        public static final int constructor = 970559507;
        public TLRPC.Document document;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.name = inputSerializedData.readString(z);
            this.document = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.rarity_permille = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(970559507);
            outputSerializedData.writeString(this.name);
            this.document.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.rarity_permille);
        }
    }

    public static class starGiftAttributeOriginalDetails extends StarGiftAttribute {
        public static final int constructor = -524291476;
        public int date;
        public int flags;
        public TLRPC.TL_textWithEntities message;
        public TLRPC.Peer recipient_id;
        public TLRPC.Peer sender_id;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.sender_id = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            this.recipient_id = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.date = inputSerializedData.readInt32(z);
            if ((this.flags & 2) != 0) {
                this.message = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-524291476);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                this.sender_id.serializeToStream(outputSerializedData);
            }
            this.recipient_id.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.date);
            if ((this.flags & 2) != 0) {
                this.message.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class starGiftAttributeOriginalDetails_layer197 extends starGiftAttributeOriginalDetails {
        public static final int constructor = -1070837941;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                this.recipient_id = tL_peerUser;
                tL_peerUser.user_id = inputSerializedData.readInt64(z);
            }
            TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
            this.recipient_id = tL_peerUser2;
            tL_peerUser2.user_id = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            if ((this.flags & 2) != 0) {
                this.message = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1070837941);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt64(this.sender_id.user_id);
            }
            outputSerializedData.writeInt64(this.recipient_id.user_id);
            outputSerializedData.writeInt32(this.date);
            if ((this.flags & 2) != 0) {
                this.message.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class starGiftAttributePattern extends StarGiftAttribute {
        public static final int constructor = 330104601;
        public TLRPC.Document document;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.name = inputSerializedData.readString(z);
            this.document = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.rarity_permille = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(330104601);
            outputSerializedData.writeString(this.name);
            this.document.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.rarity_permille);
        }
    }

    public static class starGiftUpgradePreview extends TLObject {
        public static final int constructor = 377215243;
        public ArrayList<StarGiftAttribute> sample_attributes = new ArrayList<>();

        public static starGiftUpgradePreview TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (377215243 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in starGiftUpgradePreview", Integer.valueOf(i)));
                }
                return null;
            }
            starGiftUpgradePreview stargiftupgradepreview = new starGiftUpgradePreview();
            stargiftupgradepreview.readParams(inputSerializedData, z);
            return stargiftupgradepreview;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.sample_attributes = Vector.deserialize(inputSerializedData, new TL_stars$TL_starGiftUnique$$ExternalSyntheticLambda0(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(377215243);
            Vector.serialize(outputSerializedData, this.sample_attributes);
        }
    }

    public static final class starGiftWithdrawalUrl extends TLObject {
        public static final int constructor = -2069218660;
        public String url;

        public static starGiftWithdrawalUrl TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-2069218660 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in starGiftWithdrawalUrl", Integer.valueOf(i)));
                }
                return null;
            }
            starGiftWithdrawalUrl stargiftwithdrawalurl = new starGiftWithdrawalUrl();
            stargiftwithdrawalurl.readParams(inputSerializedData, z);
            return stargiftwithdrawalurl;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.url = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-2069218660);
            outputSerializedData.writeString(this.url);
        }
    }

    public static final class toggleChatStarGiftNotifications extends TLObject {
        public static final int constructor = 1626009505;
        public boolean enabled;
        public int flags;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1626009505);
            int i = this.enabled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.peer.serializeToStream(outputSerializedData);
        }
    }

    public static class transferStarGift extends TLObject {
        public static final int constructor = 2132285290;
        public InputSavedStarGift stargift;
        public TLRPC.InputPeer to_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(2132285290);
            this.stargift.serializeToStream(outputSerializedData);
            this.to_id.serializeToStream(outputSerializedData);
        }
    }

    public static class upgradeStarGift extends TLObject {
        public static final int constructor = -1361648395;
        public int flags;
        public boolean keep_original_details;
        public InputSavedStarGift stargift;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1361648395);
            int i = this.keep_original_details ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.stargift.serializeToStream(outputSerializedData);
        }
    }
}
