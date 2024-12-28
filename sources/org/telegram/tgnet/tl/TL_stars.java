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
        public long owner_id;
        public boolean sold_out;
        public long stars;
        public TLRPC.Document sticker;
        public String title;
        public long upgrade_stars;

        public static StarGift TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            StarGift tL_starGiftUnique = i != -1365150482 ? i != 46953416 ? i != 1237678029 ? i != 1779697613 ? null : new TL_starGiftUnique() : new TL_starGift_layer195() : new TL_starGift() : new TL_starGift_layer190();
            if (tL_starGiftUnique == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGift", Integer.valueOf(i)));
            }
            if (tL_starGiftUnique != null) {
                tL_starGiftUnique.readParams(inputSerializedData, z);
            }
            return tL_starGiftUnique;
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
            StarGiftAttribute stargiftattributemodel = i != -1809377438 ? i != -1070837941 ? i != 330104601 ? i != 970559507 ? null : new starGiftAttributeModel() : new starGiftAttributePattern() : new starGiftAttributeOriginalDetails() : new starGiftAttributeBackdrop();
            if (stargiftattributemodel == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGiftAttribute", Integer.valueOf(i)));
            }
            if (stargiftattributemodel != null) {
                stargiftattributemodel.readParams(inputSerializedData, z);
            }
            return stargiftattributemodel;
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
        public static final int constructor = 1779697613;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.id = inputSerializedData.readInt64(z);
            this.title = inputSerializedData.readString(z);
            this.num = inputSerializedData.readInt32(z);
            this.owner_id = inputSerializedData.readInt64(z);
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
            outputSerializedData.writeInt64(this.owner_id);
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
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StarGift TLdeserialize = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.gifts.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1877571094);
            outputSerializedData.writeInt32(this.hash);
            outputSerializedData.writeInt32(481674261);
            int size = this.gifts.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.gifts.get(i).serializeToStream(outputSerializedData);
            }
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
            int readInt322 = inputSerializedData.readInt32(z);
            if (readInt322 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                }
                return;
            }
            int readInt323 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt323; i++) {
                TL_starsGiveawayWinnersOption TLdeserialize = TL_starsGiveawayWinnersOption.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.winners.add(TLdeserialize);
            }
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
            outputSerializedData.writeInt32(481674261);
            int size = this.winners.size();
            outputSerializedData.writeInt32(size);
            for (int i3 = 0; i3 < size; i3++) {
                this.winners.get(i3).serializeToStream(outputSerializedData);
            }
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
                int readInt322 = inputSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = inputSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
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
                outputSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                outputSerializedData.writeInt32(size);
                for (int i9 = 0; i9 < size; i9++) {
                    this.extended_media.get(i9).serializeToStream(outputSerializedData);
                }
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
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction_layer185$$ExternalSyntheticLambda0(), z);
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
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction_layer185$$ExternalSyntheticLambda0(), z);
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
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction_layer185$$ExternalSyntheticLambda0(), z);
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
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction_layer185$$ExternalSyntheticLambda0(), z);
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
                this.extended_media = Vector.deserialize(inputSerializedData, new TL_stars$TL_starsTransaction_layer185$$ExternalSyntheticLambda0(), z);
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

    public static class TL_userStarGift extends UserStarGift {
        public static final int constructor = 844641761;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.name_hidden = (readInt32 & 1) != 0;
            this.unsaved = (readInt32 & 32) != 0;
            this.refunded = (readInt32 & 512) != 0;
            this.can_upgrade = (readInt32 & 1024) != 0;
            if ((readInt32 & 2) != 0) {
                this.from_id = inputSerializedData.readInt64(z);
            }
            this.date = inputSerializedData.readInt32(z);
            this.gift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 4) != 0) {
                this.message = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 8) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
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
            outputSerializedData.writeInt32(844641761);
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
                outputSerializedData.writeInt64(this.from_id);
            }
            outputSerializedData.writeInt32(this.date);
            this.gift.serializeToStream(outputSerializedData);
            if ((this.flags & 4) != 0) {
                this.message.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
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

    public static class TL_userStarGift_layer195 extends TL_userStarGift {
        public static final int constructor = -291202450;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.name_hidden = (readInt32 & 1) != 0;
            this.unsaved = (readInt32 & 32) != 0;
            if ((readInt32 & 2) != 0) {
                this.from_id = inputSerializedData.readInt64(z);
            }
            this.date = inputSerializedData.readInt32(z);
            this.gift = StarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 4) != 0) {
                this.message = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 8) != 0) {
                this.msg_id = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 16) != 0) {
                this.convert_stars = inputSerializedData.readInt64(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-291202450);
            int i = this.name_hidden ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.unsaved ? i | 32 : i & (-33);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt64(this.from_id);
            }
            outputSerializedData.writeInt32(this.date);
            this.gift.serializeToStream(outputSerializedData);
            if ((this.flags & 4) != 0) {
                this.message.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeInt64(this.convert_stars);
            }
        }
    }

    public static class TL_userStarGifts extends TLObject {
        public static final int constructor = 1801827607;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<UserStarGift> gifts = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_userStarGifts TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            TL_userStarGifts tL_userStarGifts = i != 1801827607 ? null : new TL_userStarGifts();
            if (tL_userStarGifts == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_userStarGifts", Integer.valueOf(i)));
            }
            if (tL_userStarGifts != null) {
                tL_userStarGifts.readParams(inputSerializedData, z);
            }
            return tL_userStarGifts;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            this.count = inputSerializedData.readInt32(z);
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                UserStarGift TLdeserialize = UserStarGift.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.gifts.add(TLdeserialize);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = inputSerializedData.readString(z);
            }
            int readInt323 = inputSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = inputSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.User TLdeserialize2 = TLRPC.User.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.users.add(TLdeserialize2);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1801827607);
            outputSerializedData.writeInt32(this.flags);
            outputSerializedData.writeInt32(this.count);
            outputSerializedData.writeInt32(481674261);
            outputSerializedData.writeInt32(this.gifts.size());
            for (int i = 0; i < this.gifts.size(); i++) {
                this.gifts.get(i).serializeToStream(outputSerializedData);
            }
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.next_offset);
            }
            outputSerializedData.writeInt32(481674261);
            outputSerializedData.writeInt32(this.users.size());
            for (int i2 = 0; i2 < this.users.size(); i2++) {
                this.users.get(i2).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class UserStarGift extends TLObject {
        public int can_export_at;
        public boolean can_upgrade;
        public long convert_stars;
        public int date;
        public int flags;
        public long from_id;
        public StarGift gift;
        public TLRPC.TL_textWithEntities message;
        public int msg_id;
        public boolean name_hidden;
        public boolean refunded;
        public long transfer_stars;
        public boolean unsaved;
        public long upgrade_stars;

        public static UserStarGift TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            UserStarGift tL_userStarGift = i != -291202450 ? i != 844641761 ? null : new TL_userStarGift() : new TL_userStarGift_layer195();
            if (tL_userStarGift == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in UserStarGift", Integer.valueOf(i)));
            }
            if (tL_userStarGift != null) {
                tL_userStarGift.readParams(inputSerializedData, z);
            }
            return tL_userStarGift;
        }
    }

    public static class convertStarGift extends TLObject {
        public static final int constructor = 1920404611;
        public int msg_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1920404611);
            outputSerializedData.writeInt32(this.msg_id);
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

    public static class getUserStarGift extends TLObject {
        public static final int constructor = -1258101595;
        public ArrayList<Integer> msg_id = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_userStarGifts.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1258101595);
            Vector.serializeInt(outputSerializedData, this.msg_id);
        }
    }

    public static class getUserStarGifts extends TLObject {
        public static final int constructor = 1584580577;
        public int limit;
        public String offset;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_userStarGifts.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1584580577);
            this.user_id.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.offset);
            outputSerializedData.writeInt32(this.limit);
        }
    }

    public static class saveStarGift extends TLObject {
        public static final int constructor = -1828902226;
        public int flags;
        public int msg_id;
        public boolean unsave;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1828902226);
            int i = this.unsave ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt32(this.msg_id);
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
        public static final int constructor = -1070837941;
        public int date;
        public int flags;
        public TLRPC.TL_textWithEntities message;
        public long recipient_id;
        public long sender_id;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.sender_id = inputSerializedData.readInt64(z);
            }
            this.recipient_id = inputSerializedData.readInt64(z);
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
                outputSerializedData.writeInt64(this.sender_id);
            }
            outputSerializedData.writeInt64(this.recipient_id);
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

    public static class transferStarGift extends TLObject {
        public static final int constructor = 859813158;
        public int msg_id;
        public TLRPC.InputUser to_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(859813158);
            outputSerializedData.writeInt32(this.msg_id);
            this.to_id.serializeToStream(outputSerializedData);
        }
    }

    public static class upgradeStarGift extends TLObject {
        public static final int constructor = -816904319;
        public int flags;
        public boolean keep_original_details;
        public int msg_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-816904319);
            int i = this.keep_original_details ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt32(this.msg_id);
        }
    }
}
