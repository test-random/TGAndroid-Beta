package org.telegram.tgnet.tl;

import java.util.ArrayList;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class TL_stars {

    public static class StarGift extends TLObject {
        public int availability_remains;
        public int availability_total;
        public boolean birthday;
        public long convert_stars;
        public int first_sale_date;
        public int flags;
        public long id;
        public int last_sale_date;
        public boolean limited;
        public boolean sold_out;
        public long stars;
        public TLRPC.Document sticker;

        public static StarGift TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StarGift tL_starGift = i != -1365150482 ? i != 1237678029 ? null : new TL_starGift() : new TL_starGift_layer190();
            if (tL_starGift == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGift", Integer.valueOf(i)));
            }
            if (tL_starGift != null) {
                tL_starGift.readParams(abstractSerializedData, z);
            }
            return tL_starGift;
        }
    }

    public static class StarGifts extends TLObject {
        public static StarGifts TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StarGifts tL_starGiftsNotModified = i != -1877571094 ? i != -1551326360 ? null : new TL_starGiftsNotModified() : new TL_starGifts();
            if (tL_starGiftsNotModified == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarGifts", Integer.valueOf(i)));
            }
            if (tL_starGiftsNotModified != null) {
                tL_starGiftsNotModified.readParams(abstractSerializedData, z);
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

        public static StarsAmount TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1145654109 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in StarsAmount", Integer.valueOf(i)));
                }
                return null;
            }
            StarsAmount starsAmount = new StarsAmount();
            starsAmount.readParams(abstractSerializedData, z);
            return starsAmount;
        }

        public boolean equals(StarsAmount starsAmount) {
            return starsAmount != null && this.amount == starsAmount.amount && this.nanos == starsAmount.nanos;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.amount = abstractSerializedData.readInt64(z);
            this.nanos = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1145654109);
            abstractSerializedData.writeInt64(this.amount);
            abstractSerializedData.writeInt32(this.nanos);
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

        public static StarsStatus TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StarsStatus tL_payments_starsStatus = i != -1141231252 ? i != 1822222573 ? null : new TL_payments_starsStatus() : new TL_payments_starsStatus_layer194();
            if (tL_payments_starsStatus == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarsStatus", Integer.valueOf(i)));
            }
            if (tL_payments_starsStatus != null) {
                tL_payments_starsStatus.readParams(abstractSerializedData, z);
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

        public static StarsSubscription TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StarsSubscription tL_starsSubscription_layer193 = i != -797707802 ? i != 779004698 ? i != 1401868056 ? null : new TL_starsSubscription_layer193() : new TL_starsSubscription() : new TL_starsSubscription_old();
            if (tL_starsSubscription_layer193 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StarsTransaction", Integer.valueOf(i)));
            }
            if (tL_starsSubscription_layer193 != null) {
                tL_starsSubscription_layer193.readParams(abstractSerializedData, z);
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

        public static StarsTransaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
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
                tL_starsTransaction_layer182.readParams(abstractSerializedData, z);
            }
            return tL_starsTransaction_layer182;
        }
    }

    public static class StarsTransactionPeer extends TLObject {
        public TLRPC.Peer peer;

        public static StarsTransactionPeer TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
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
                tL_starsTransactionPeerUnsupported.readParams(abstractSerializedData, z);
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
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-948500360);
            int i = this.canceled != null ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.subscription_id);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeBool(this.canceled.booleanValue());
            }
        }
    }

    public static class TL_fulfillStarsSubscription extends TLObject {
        public static final int constructor = -866391117;
        public TLRPC.InputPeer peer;
        public String subscription_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-866391117);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.subscription_id);
        }
    }

    public static class TL_getStarsSubscriptions extends TLObject {
        public static final int constructor = 52761285;
        public int flags;
        public boolean missing_balance;
        public String offset;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return StarsStatus.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(52761285);
            int i = this.missing_balance ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.offset);
        }
    }

    public static class TL_payments_getStarsGiftOptions extends TLObject {
        public static final int constructor = -741774392;
        public int flags;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                TL_starsGiftOption TLdeserialize = TL_starsGiftOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return vector;
                }
                vector.objects.add(TLdeserialize);
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-741774392);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                this.user_id.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_payments_getStarsGiveawayOptions extends TLObject {
        public static final int constructor = -1122042562;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                TL_starsGiveawayOption TLdeserialize = TL_starsGiveawayOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return vector;
                }
                vector.objects.add(TLdeserialize);
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1122042562);
        }
    }

    public static class TL_payments_getStarsStatus extends TLObject {
        public static final int constructor = 273665959;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return StarsStatus.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(273665959);
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_payments_getStarsTopupOptions extends TLObject {
        public static final int constructor = -1072773165;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                TL_starsTopupOption TLdeserialize = TL_starsTopupOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return vector;
                }
                vector.objects.add(TLdeserialize);
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1072773165);
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
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return StarsStatus.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1731904249);
            int i = this.inbound ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.outbound ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.offset);
        }
    }

    public static class TL_payments_sendStarsForm extends TLObject {
        public static final int constructor = 2040056084;
        public long form_id;
        public TLRPC.InputInvoice invoice;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.payments_PaymentResult.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2040056084);
            abstractSerializedData.writeInt64(this.form_id);
            this.invoice.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_payments_starsStatus extends StarsStatus {
        public static final int constructor = 1822222573;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.balance = StarsAmount.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 2) != 0) {
                int readInt32 = abstractSerializedData.readInt32(z);
                if (readInt32 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                    }
                    return;
                }
                int readInt322 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt322; i++) {
                    StarsSubscription TLdeserialize = StarsSubscription.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.subscriptions.add(TLdeserialize);
                }
            }
            if ((this.flags & 4) != 0) {
                this.subscriptions_next_offset = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.subscriptions_missing_balance = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 8) != 0) {
                int readInt323 = abstractSerializedData.readInt32(z);
                if (readInt323 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                    }
                    return;
                }
                int readInt324 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt324; i2++) {
                    StarsTransaction TLdeserialize2 = StarsTransaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 == null) {
                        return;
                    }
                    this.history.add(TLdeserialize2);
                }
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
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
                TLRPC.Chat TLdeserialize3 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.chats.add(TLdeserialize3);
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
                TLRPC.User TLdeserialize4 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize4 == null) {
                    return;
                }
                this.users.add(TLdeserialize4);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1822222573);
            abstractSerializedData.writeInt32(this.flags);
            this.balance.serializeToStream(abstractSerializedData);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.subscriptions.size();
                abstractSerializedData.writeInt32(size);
                for (int i = 0; i < size; i++) {
                    this.subscriptions.get(i).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeString(this.subscriptions_next_offset);
            }
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeInt64(this.subscriptions_missing_balance);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.history.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.history.get(i2).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
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

    public static class TL_payments_starsStatus_layer194 extends TL_payments_starsStatus {
        public static final int constructor = -1141231252;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.balance = new StarsAmount(abstractSerializedData.readInt64(z));
            if ((this.flags & 2) != 0) {
                int readInt32 = abstractSerializedData.readInt32(z);
                if (readInt32 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                    }
                    return;
                }
                int readInt322 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt322; i++) {
                    StarsSubscription TLdeserialize = StarsSubscription.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.subscriptions.add(TLdeserialize);
                }
            }
            if ((this.flags & 4) != 0) {
                this.subscriptions_next_offset = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.subscriptions_missing_balance = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 8) != 0) {
                int readInt323 = abstractSerializedData.readInt32(z);
                if (readInt323 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                    }
                    return;
                }
                int readInt324 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt324; i2++) {
                    StarsTransaction TLdeserialize2 = StarsTransaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize2 == null) {
                        return;
                    }
                    this.history.add(TLdeserialize2);
                }
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
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
                TLRPC.Chat TLdeserialize3 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.chats.add(TLdeserialize3);
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
                TLRPC.User TLdeserialize4 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize4 == null) {
                    return;
                }
                this.users.add(TLdeserialize4);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1141231252);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt64(this.balance.amount);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.subscriptions.size();
                abstractSerializedData.writeInt32(size);
                for (int i = 0; i < size; i++) {
                    this.subscriptions.get(i).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeString(this.subscriptions_next_offset);
            }
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeInt64(this.subscriptions_missing_balance);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.history.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.history.get(i2).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
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

    public static class TL_starGift extends StarGift {
        public static final int constructor = 1237678029;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.limited = (readInt32 & 1) != 0;
            this.sold_out = (readInt32 & 2) != 0;
            this.birthday = (readInt32 & 4) != 0;
            this.id = abstractSerializedData.readInt64(z);
            this.sticker = TLRPC.Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.stars = abstractSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.availability_remains = abstractSerializedData.readInt32(z);
                this.availability_total = abstractSerializedData.readInt32(z);
            }
            this.convert_stars = abstractSerializedData.readInt64(z);
            if ((this.flags & 2) != 0) {
                this.first_sale_date = abstractSerializedData.readInt32(z);
                this.last_sale_date = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1237678029);
            int i = this.limited ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.sold_out ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.birthday ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            abstractSerializedData.writeInt64(this.id);
            this.sticker.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.availability_remains);
                abstractSerializedData.writeInt32(this.availability_total);
            }
            abstractSerializedData.writeInt64(this.convert_stars);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(this.first_sale_date);
                abstractSerializedData.writeInt32(this.last_sale_date);
            }
        }
    }

    public static class TL_starGift_layer190 extends TL_starGift {
        public static final int constructor = -1365150482;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.limited = (readInt32 & 1) != 0;
            this.id = abstractSerializedData.readInt64(z);
            this.sticker = TLRPC.Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.stars = abstractSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.availability_remains = abstractSerializedData.readInt32(z);
                this.availability_total = abstractSerializedData.readInt32(z);
            }
            this.convert_stars = abstractSerializedData.readInt64(z);
            this.sold_out = this.limited && this.availability_remains <= 0;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1365150482);
            int i = this.limited ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt64(this.id);
            this.sticker.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.availability_remains);
                abstractSerializedData.writeInt32(this.availability_total);
            }
            abstractSerializedData.writeInt64(this.convert_stars);
        }
    }

    public static class TL_starGifts extends StarGifts {
        public static final int constructor = -1877571094;
        public ArrayList<StarGift> gifts = new ArrayList<>();
        public int hash;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.hash = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                StarGift TLdeserialize = StarGift.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.gifts.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1877571094);
            abstractSerializedData.writeInt32(this.hash);
            abstractSerializedData.writeInt32(481674261);
            int size = this.gifts.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.gifts.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_starGiftsNotModified extends StarGifts {
        public static final int constructor = -1551326360;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1551326360);
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

        public static TL_starsGiftOption TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1577421297 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsGiftOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsGiftOption tL_starsGiftOption = new TL_starsGiftOption();
            tL_starsGiftOption.readParams(abstractSerializedData, z);
            return tL_starsGiftOption;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.extended = (readInt32 & 2) != 0;
            this.stars = abstractSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.store_product = abstractSerializedData.readString(z);
            }
            this.currency = abstractSerializedData.readString(z);
            this.amount = abstractSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1577421297);
            int i = this.extended ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.store_product);
            }
            abstractSerializedData.writeString(this.currency);
            abstractSerializedData.writeInt64(this.amount);
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

        public static TL_starsGiveawayOption TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1798404822 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsGiveawayOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsGiveawayOption tL_starsGiveawayOption = new TL_starsGiveawayOption();
            tL_starsGiveawayOption.readParams(abstractSerializedData, z);
            return tL_starsGiveawayOption;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.extended = (readInt32 & 1) != 0;
            this.isDefault = (readInt32 & 2) != 0;
            this.stars = abstractSerializedData.readInt64(z);
            this.yearly_boosts = abstractSerializedData.readInt32(z);
            if ((this.flags & 4) != 0) {
                this.store_product = abstractSerializedData.readString(z);
            }
            this.currency = abstractSerializedData.readString(z);
            this.amount = abstractSerializedData.readInt64(z);
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                }
                return;
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt323; i++) {
                TL_starsGiveawayWinnersOption TLdeserialize = TL_starsGiveawayWinnersOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.winners.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1798404822);
            int i = this.extended ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.isDefault ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            abstractSerializedData.writeInt64(this.stars);
            abstractSerializedData.writeInt32(this.yearly_boosts);
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeString(this.store_product);
            }
            abstractSerializedData.writeString(this.currency);
            abstractSerializedData.writeInt64(this.amount);
            abstractSerializedData.writeInt32(481674261);
            int size = this.winners.size();
            abstractSerializedData.writeInt32(size);
            for (int i3 = 0; i3 < size; i3++) {
                this.winners.get(i3).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_starsGiveawayWinnersOption extends TLObject {
        public static final int constructor = 1411605001;
        public int flags;
        public boolean isDefault;
        public long per_user_stars;
        public int users;

        public static TL_starsGiveawayWinnersOption TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1411605001 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsGiveawayWinnersOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsGiveawayWinnersOption tL_starsGiveawayWinnersOption = new TL_starsGiveawayWinnersOption();
            tL_starsGiveawayWinnersOption.readParams(abstractSerializedData, z);
            return tL_starsGiveawayWinnersOption;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.isDefault = (readInt32 & 1) != 0;
            this.users = abstractSerializedData.readInt32(z);
            this.per_user_stars = abstractSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1411605001);
            int i = this.isDefault ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt32(this.users);
            abstractSerializedData.writeInt64(this.per_user_stars);
        }
    }

    public static class TL_starsSubscription extends StarsSubscription {
        public static final int constructor = 779004698;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.canceled = (readInt32 & 1) != 0;
            this.can_refulfill = (readInt32 & 2) != 0;
            this.missing_balance = (readInt32 & 4) != 0;
            this.bot_canceled = (readInt32 & 128) != 0;
            this.id = abstractSerializedData.readString(z);
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.until_date = abstractSerializedData.readInt32(z);
            this.pricing = TL_starsSubscriptionPricing.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 8) != 0) {
                this.chat_invite_hash = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 32) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 64) != 0) {
                this.invoice_slug = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(779004698);
            int i = this.canceled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.can_refulfill ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.missing_balance ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            int i4 = this.bot_canceled ? i3 | 128 : i3 & (-129);
            this.flags = i4;
            abstractSerializedData.writeInt32(i4);
            abstractSerializedData.writeString(this.id);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.until_date);
            this.pricing.serializeToStream(abstractSerializedData);
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeString(this.chat_invite_hash);
            }
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 32) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 64) != 0) {
                abstractSerializedData.writeString(this.invoice_slug);
            }
        }
    }

    public static class TL_starsSubscriptionPricing extends TLObject {
        public static final int constructor = 88173912;
        public long amount;
        public int period;

        public static TL_starsSubscriptionPricing TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (88173912 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsSubscriptionPricing", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsSubscriptionPricing tL_starsSubscriptionPricing = new TL_starsSubscriptionPricing();
            tL_starsSubscriptionPricing.readParams(abstractSerializedData, z);
            return tL_starsSubscriptionPricing;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.period = abstractSerializedData.readInt32(z);
            this.amount = abstractSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(88173912);
            abstractSerializedData.writeInt32(this.period);
            abstractSerializedData.writeInt64(this.amount);
        }
    }

    public static class TL_starsSubscription_layer193 extends StarsSubscription {
        public static final int constructor = 1401868056;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.canceled = (readInt32 & 1) != 0;
            this.can_refulfill = (readInt32 & 2) != 0;
            this.missing_balance = (readInt32 & 4) != 0;
            this.id = abstractSerializedData.readString(z);
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.until_date = abstractSerializedData.readInt32(z);
            this.pricing = TL_starsSubscriptionPricing.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 8) != 0) {
                this.chat_invite_hash = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1401868056);
            int i = this.canceled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.can_refulfill ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.missing_balance ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            abstractSerializedData.writeString(this.id);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.until_date);
            this.pricing.serializeToStream(abstractSerializedData);
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeString(this.chat_invite_hash);
            }
        }
    }

    public static class TL_starsSubscription_old extends TL_starsSubscription {
        public static final int constructor = -797707802;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.canceled = (readInt32 & 1) != 0;
            this.can_refulfill = (readInt32 & 2) != 0;
            this.missing_balance = (readInt32 & 4) != 0;
            this.id = abstractSerializedData.readString(z);
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.until_date = abstractSerializedData.readInt32(z);
            this.pricing = TL_starsSubscriptionPricing.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-797707802);
            int i = this.canceled ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.can_refulfill ? i | 2 : i & (-3);
            this.flags = i2;
            int i3 = this.missing_balance ? i2 | 4 : i2 & (-5);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            abstractSerializedData.writeString(this.id);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.until_date);
            this.pricing.serializeToStream(abstractSerializedData);
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

        public static TL_starsTopupOption TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (198776256 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_starsTopupOption", Integer.valueOf(i)));
                }
                return null;
            }
            TL_starsTopupOption tL_starsTopupOption = new TL_starsTopupOption();
            tL_starsTopupOption.readParams(abstractSerializedData, z);
            return tL_starsTopupOption;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.extended = (readInt32 & 2) != 0;
            this.stars = abstractSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.store_product = abstractSerializedData.readString(z);
            }
            this.currency = abstractSerializedData.readString(z);
            this.amount = abstractSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(198776256);
            int i = this.extended ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt64(this.stars);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.store_product);
            }
            abstractSerializedData.writeString(this.currency);
            abstractSerializedData.writeInt64(this.amount);
        }
    }

    public static class TL_starsTransaction extends StarsTransaction {
        public static final int constructor = 1692387622;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.floodskip = (readInt32 & 32768) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = StarsAmount.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = abstractSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift = StarGift.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.floodskip_number = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 65536) != 0) {
                this.starref_commission_permille = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 131072) != 0) {
                this.starref_peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                this.starref_amount = StarsAmount.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1692387622);
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
            abstractSerializedData.writeInt32(i7);
            this.stars.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                abstractSerializedData.writeInt32(size);
                for (int i8 = 0; i8 < size; i8++) {
                    this.extended_media.get(i8).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4096) != 0) {
                abstractSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                abstractSerializedData.writeInt32(this.giveaway_post_id);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                abstractSerializedData.writeInt32(this.floodskip_number);
            }
            if ((this.flags & 65536) != 0) {
                abstractSerializedData.writeInt32(this.starref_commission_permille);
            }
            if ((this.flags & 131072) != 0) {
                this.starref_peer.serializeToStream(abstractSerializedData);
                this.starref_amount.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_starsTransactionPeer extends StarsTransactionPeer {
        public static final int constructor = -670195363;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.peer = TLRPC.Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-670195363);
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_starsTransactionPeerAPI extends StarsTransactionPeer {
        public static final int constructor = -110658899;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-110658899);
        }
    }

    public static class TL_starsTransactionPeerAds extends StarsTransactionPeer {
        public static final int constructor = 1617438738;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1617438738);
        }
    }

    public static class TL_starsTransactionPeerAppStore extends StarsTransactionPeer {
        public static final int constructor = -1269320843;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1269320843);
        }
    }

    public static class TL_starsTransactionPeerFragment extends StarsTransactionPeer {
        public static final int constructor = -382740222;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-382740222);
        }
    }

    public static class TL_starsTransactionPeerPlayMarket extends StarsTransactionPeer {
        public static final int constructor = 2069236235;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2069236235);
        }
    }

    public static class TL_starsTransactionPeerPremiumBot extends StarsTransactionPeer {
        public static final int constructor = 621656824;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(621656824);
        }
    }

    public static class TL_starsTransactionPeerUnsupported extends StarsTransactionPeer {
        public static final int constructor = -1779253276;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1779253276);
        }
    }

    public static class TL_starsTransaction_layer181 extends StarsTransaction {
        public static final int constructor = -865044046;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-865044046);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_starsTransaction_layer182 extends TL_starsTransaction {
        public static final int constructor = -1442789224;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1442789224);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            abstractSerializedData.writeInt32(i3);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
        }
    }

    public static class TL_starsTransaction_layer185 extends TL_starsTransaction {
        public static final int constructor = 766853519;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = abstractSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(766853519);
            int i = this.refund ? this.flags | 8 : this.flags & (-9);
            this.flags = i;
            int i2 = this.pending ? i | 16 : i & (-17);
            this.flags = i2;
            int i3 = this.failed ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            int i4 = this.gift ? i3 | 1024 : i3 & (-1025);
            this.flags = i4;
            abstractSerializedData.writeInt32(i4);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                abstractSerializedData.writeInt32(size);
                for (int i5 = 0; i5 < size; i5++) {
                    this.extended_media.get(i5).serializeToStream(abstractSerializedData);
                }
            }
        }
    }

    public static class TL_starsTransaction_layer186 extends TL_starsTransaction {
        public static final int constructor = 1127934763;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = abstractSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1127934763);
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
            abstractSerializedData.writeInt32(i6);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                abstractSerializedData.writeInt32(size);
                for (int i7 = 0; i7 < size; i7++) {
                    this.extended_media.get(i7).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4096) != 0) {
                abstractSerializedData.writeInt32(this.subscription_period);
            }
        }
    }

    public static class TL_starsTransaction_layer188 extends TL_starsTransaction {
        public static final int constructor = -294313259;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = abstractSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-294313259);
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
            abstractSerializedData.writeInt32(i6);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                abstractSerializedData.writeInt32(size);
                for (int i7 = 0; i7 < size; i7++) {
                    this.extended_media.get(i7).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4096) != 0) {
                abstractSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                abstractSerializedData.writeInt32(this.giveaway_post_id);
            }
        }
    }

    public static class TL_starsTransaction_layer191 extends TL_starsTransaction {
        public static final int constructor = 178185410;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = abstractSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift = StarGift.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(178185410);
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
            abstractSerializedData.writeInt32(i6);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                abstractSerializedData.writeInt32(size);
                for (int i7 = 0; i7 < size; i7++) {
                    this.extended_media.get(i7).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4096) != 0) {
                abstractSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                abstractSerializedData.writeInt32(this.giveaway_post_id);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_starsTransaction_layer194 extends TL_starsTransaction {
        public static final int constructor = 903148150;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.refund = (readInt32 & 8) != 0;
            this.pending = (readInt32 & 16) != 0;
            this.failed = (readInt32 & 64) != 0;
            this.gift = (readInt32 & 1024) != 0;
            this.reaction = (readInt32 & 2048) != 0;
            this.subscription = (readInt32 & 4096) != 0;
            this.floodskip = (readInt32 & 32768) != 0;
            this.id = abstractSerializedData.readString(z);
            this.stars = new StarsAmount(abstractSerializedData.readInt64(z));
            this.date = abstractSerializedData.readInt32(z);
            this.peer = StarsTransactionPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.title = abstractSerializedData.readString(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 4) != 0) {
                this.photo = TLRPC.WebDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 128) != 0) {
                this.bot_payload = abstractSerializedData.readByteArray(z);
            }
            if ((this.flags & 256) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 512) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.MessageMedia TLdeserialize = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.extended_media.add(TLdeserialize);
                }
            }
            if ((this.flags & 4096) != 0) {
                this.subscription_period = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 8192) != 0) {
                this.giveaway_post_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift = StarGift.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32768) != 0) {
                this.floodskip_number = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(903148150);
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
            abstractSerializedData.writeInt32(i7);
            abstractSerializedData.writeInt64(this.stars.amount);
            abstractSerializedData.writeInt32(this.date);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 4) != 0) {
                this.photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeByteArray(this.bot_payload);
            }
            if ((this.flags & 256) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 512) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.extended_media.size();
                abstractSerializedData.writeInt32(size);
                for (int i8 = 0; i8 < size; i8++) {
                    this.extended_media.get(i8).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 4096) != 0) {
                abstractSerializedData.writeInt32(this.subscription_period);
            }
            if ((this.flags & 8192) != 0) {
                abstractSerializedData.writeInt32(this.giveaway_post_id);
            }
            if ((this.flags & 16384) != 0) {
                this.stargift.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32768) != 0) {
                abstractSerializedData.writeInt32(this.floodskip_number);
            }
        }
    }

    public static class TL_userStarGift extends UserStarGift {
        public static final int constructor = -291202450;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.name_hidden = (readInt32 & 1) != 0;
            this.unsaved = (readInt32 & 32) != 0;
            if ((readInt32 & 2) != 0) {
                this.from_id = abstractSerializedData.readInt64(z);
            }
            this.date = abstractSerializedData.readInt32(z);
            this.gift = StarGift.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 4) != 0) {
                this.message = TLRPC.TL_textWithEntities.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 8) != 0) {
                this.msg_id = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 16) != 0) {
                this.convert_stars = abstractSerializedData.readInt64(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-291202450);
            int i = this.name_hidden ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.unsaved ? i | 32 : i & (-33);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt64(this.from_id);
            }
            abstractSerializedData.writeInt32(this.date);
            this.gift.serializeToStream(abstractSerializedData);
            if ((this.flags & 4) != 0) {
                this.message.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeInt32(this.msg_id);
            }
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeInt64(this.convert_stars);
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

        public static TL_userStarGifts TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TL_userStarGifts tL_userStarGifts = i != 1801827607 ? null : new TL_userStarGifts();
            if (tL_userStarGifts == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_userStarGifts", Integer.valueOf(i)));
            }
            if (tL_userStarGifts != null) {
                tL_userStarGifts.readParams(abstractSerializedData, z);
            }
            return tL_userStarGifts;
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
                UserStarGift TLdeserialize = UserStarGift.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.gifts.add(TLdeserialize);
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
            abstractSerializedData.writeInt32(1801827607);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            abstractSerializedData.writeInt32(this.gifts.size());
            for (int i = 0; i < this.gifts.size(); i++) {
                this.gifts.get(i).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
            abstractSerializedData.writeInt32(481674261);
            abstractSerializedData.writeInt32(this.users.size());
            for (int i2 = 0; i2 < this.users.size(); i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class UserStarGift extends TLObject {
        public long convert_stars;
        public int date;
        public int flags;
        public long from_id;
        public StarGift gift;
        public TLRPC.TL_textWithEntities message;
        public int msg_id;
        public boolean name_hidden;
        public boolean unsaved;

        public static UserStarGift TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TL_userStarGift tL_userStarGift = i != -291202450 ? null : new TL_userStarGift();
            if (tL_userStarGift == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in UserStarGift", Integer.valueOf(i)));
            }
            if (tL_userStarGift != null) {
                tL_userStarGift.readParams(abstractSerializedData, z);
            }
            return tL_userStarGift;
        }
    }

    public static class convertStarGift extends TLObject {
        public static final int constructor = 69328935;
        public int msg_id;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(69328935);
            this.user_id.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.msg_id);
        }
    }

    public static class getStarGifts extends TLObject {
        public static final int constructor = -1000983152;
        public int hash;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return StarGifts.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1000983152);
            abstractSerializedData.writeInt32(this.hash);
        }
    }

    public static class getUserStarGifts extends TLObject {
        public static final int constructor = 1584580577;
        public int limit;
        public String offset;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_userStarGifts.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1584580577);
            this.user_id.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class saveStarGift extends TLObject {
        public static final int constructor = -2018709362;
        public int flags;
        public int msg_id;
        public boolean unsave;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-2018709362);
            int i = this.unsave ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.user_id.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.msg_id);
        }
    }
}
